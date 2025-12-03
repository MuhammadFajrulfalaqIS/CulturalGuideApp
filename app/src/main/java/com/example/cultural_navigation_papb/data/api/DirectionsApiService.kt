package com.example.cultural_navigation_papb.data.api

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.net.URLEncoder

/**
 * Service untuk memanggil Google Directions API
 * Dokumentasi: https://developers.google.com/maps/documentation/directions
 */
class DirectionsApiService {

    companion object {
        private const val TAG = "DirectionsAPI"
        private const val BASE_URL = "https://maps.googleapis.com/maps/api/directions/json"

        // ✅ SECURE: Get API Key from BuildConfig (loaded from local.properties)
        private val API_KEY = com.example.cultural_navigation_papb.BuildConfig.GOOGLE_DIRECTIONS_API_KEY
    }

    /**
     * Get walking directions dari origin ke destination
     * ✅ NEW: Support alternatives, waypoints, dan custom routing untuk kompleks Prambanan
     * @return List<LatLng> untuk polyline route
     */
    suspend fun getWalkingDirections(
        origin: LatLng,
        destination: LatLng,
        useAlternatives: Boolean = true // Request alternative routes
    ): DirectionResult = withContext(Dispatchers.IO) {
        try {
            // Check if both points are within Prambanan complex area
            val isPrambananRoute = isWithinPrambananComplex(origin) && isWithinPrambananComplex(destination)

            if (isPrambananRoute) {
                // ✅ Use custom routing for Prambanan complex
                Log.d(TAG, "🏛️ Both points in Prambanan complex, using custom routing with waypoints")
                return@withContext getCustomPrambananRoute(origin, destination)
            }

            // Build URL dengan parameters
            val originStr = "${origin.latitude},${origin.longitude}"
            val destStr = "${destination.latitude},${destination.longitude}"

            val urlString = buildString {
                append(BASE_URL)
                append("?origin=").append(URLEncoder.encode(originStr, "UTF-8"))
                append("&destination=").append(URLEncoder.encode(destStr, "UTF-8"))
                append("&mode=walking") // Mode jalan kaki

                // ✅ Request alternative routes
                if (useAlternatives) {
                    append("&alternatives=true")
                }

                // ✅ Avoid highways to prioritize pedestrian paths
                append("&avoid=highways")

                append("&key=").append(API_KEY)
            }

            Log.d(TAG, "🌐 Requesting directions: $urlString")

            // Make HTTP request
            val response = URL(urlString).readText()

            // Parse JSON response
            parseDirectionsResponse(response)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error getting directions", e)
            DirectionResult.Error("Failed to get directions: ${e.message}")
        }
    }

    /**
     * ✅ NEW: Check if coordinate is within Prambanan complex
     * Prambanan complex bounds: lat -7.746 to -7.754, lng 110.489 to 110.495
     */
    private fun isWithinPrambananComplex(location: LatLng): Boolean {
        return location.latitude in -7.754..-7.746 &&
               location.longitude in 110.489..110.495
    }

    /**
     * ✅ NEW: Custom routing for Prambanan complex with waypoints
     * Adds intermediate waypoints to force route through pedestrian paths
     */
    private suspend fun getCustomPrambananRoute(
        origin: LatLng,
        destination: LatLng
    ): DirectionResult = try {
        // Define key waypoints within Prambanan complex
        // These are pedestrian paths that connect different temples
        val centerPoint = LatLng(-7.752008, 110.491825) // Center of main temple
        val northPath = LatLng(-7.748, 110.492) // Northern pedestrian path
        val southPath = LatLng(-7.753, 110.491) // Southern pedestrian path

        // Calculate which waypoint is best based on origin and destination
        val waypoint = findBestWaypoint(origin, destination, listOf(centerPoint, northPath, southPath))

        // Build URL with waypoint
        val originStr = "${origin.latitude},${origin.longitude}"
        val destStr = "${destination.latitude},${destination.longitude}"
        val waypointStr = "${waypoint.latitude},${waypoint.longitude}"

        val urlString = buildString {
            append(BASE_URL)
            append("?origin=").append(URLEncoder.encode(originStr, "UTF-8"))
            append("&destination=").append(URLEncoder.encode(destStr, "UTF-8"))
            append("&waypoints=").append(URLEncoder.encode(waypointStr, "UTF-8"))
            append("&mode=walking")
            append("&avoid=highways")
            append("&key=").append(API_KEY)
        }

        Log.d(TAG, "🏛️ Custom Prambanan route with waypoint: $waypointStr")

        val response = URL(urlString).readText()
        parseDirectionsResponse(response)

    } catch (e: Exception) {
        Log.e(TAG, "❌ Error getting custom Prambanan route", e)
        DirectionResult.Error("Failed to get custom route: ${e.message}")
    }

    /**
     * Find best waypoint based on origin and destination
     */
    private fun findBestWaypoint(
        origin: LatLng,
        destination: LatLng,
        waypoints: List<LatLng>
    ): LatLng {
        // Find waypoint that minimizes total distance
        return waypoints.minByOrNull { waypoint ->
            distanceBetween(origin, waypoint) + distanceBetween(waypoint, destination)
        } ?: waypoints.first()
    }

    /**
     * Calculate distance between two coordinates (in meters)
     */
    private fun distanceBetween(from: LatLng, to: LatLng): Float {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(
            from.latitude, from.longitude,
            to.latitude, to.longitude,
            results
        )
        return results[0]
    }

    /**
     * Parse JSON response dari Directions API
     */
    private fun parseDirectionsResponse(jsonResponse: String): DirectionResult {
        try {
            val json = JSONObject(jsonResponse)
            val status = json.getString("status")

            if (status != "OK") {
                Log.e(TAG, "❌ Directions API error: $status")
                return DirectionResult.Error("API returned status: $status")
            }

            val routes = json.getJSONArray("routes")
            if (routes.length() == 0) {
                return DirectionResult.Error("No routes found")
            }

            // Get first route
            val route = routes.getJSONObject(0)

            // Get overview polyline (simplified path)
            val overviewPolyline = route.getJSONObject("overview_polyline")
            val encodedPolyline = overviewPolyline.getString("points")

            // Decode polyline
            val pathPoints = decodePolyline(encodedPolyline)

            // Get distance and duration
            val legs = route.getJSONArray("legs")
            val firstLeg = legs.getJSONObject(0)

            val distance = firstLeg.getJSONObject("distance").getString("text")
            val duration = firstLeg.getJSONObject("duration").getString("text")

            // Get steps for detailed instructions
            val steps = mutableListOf<DirectionStep>()
            val stepsArray = firstLeg.getJSONArray("steps")

            for (i in 0 until stepsArray.length()) {
                val step = stepsArray.getJSONObject(i)
                val instruction = step.getString("html_instructions")
                    .replace("<[^>]*>".toRegex(), "") // Remove HTML tags
                val stepDistance = step.getJSONObject("distance").getString("text")
                val stepDuration = step.getJSONObject("duration").getString("text")

                steps.add(DirectionStep(
                    instruction = instruction,
                    distance = stepDistance,
                    duration = stepDuration
                ))
            }

            Log.d(TAG, "✅ Parsed route: ${pathPoints.size} points, $distance, $duration")

            return DirectionResult.Success(
                path = pathPoints,
                distance = distance,
                duration = duration,
                steps = steps
            )

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error parsing directions response", e)
            return DirectionResult.Error("Failed to parse response: ${e.message}")
        }
    }

    /**
     * Decode Google's encoded polyline format
     * Algorithm: https://developers.google.com/maps/documentation/utilities/polylinealgorithm
     */
    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = mutableListOf<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0

            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0

            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(latLng)
        }

        return poly
    }
}

/**
 * Sealed class untuk hasil dari Directions API
 */
sealed class DirectionResult {
    data class Success(
        val path: List<LatLng>,
        val distance: String,
        val duration: String,
        val steps: List<DirectionStep>
    ) : DirectionResult()

    data class Error(val message: String) : DirectionResult()
}

/**
 * Data class untuk step-by-step instructions
 */
data class DirectionStep(
    val instruction: String,
    val distance: String,
    val duration: String
)
