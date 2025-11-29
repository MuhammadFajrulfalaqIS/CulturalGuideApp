package com.example.cultural_navigation_papb.data.repository

import android.content.Context
import com.example.cultural_navigation_papb.BuildConfig
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * Repository for Google Directions API
 * Handles fetching walking routes between two points
 */
class DirectionsRepository(context: Context) {

    private val gson = Gson()
    private val apiKey = BuildConfig.GOOGLE_DIRECTIONS_API_KEY

    private val retrofit by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private val directionsApi by lazy {
        retrofit.create(DirectionsApi::class.java)
    }

    /**
     * Get directions from origin to destination for walking
     * @param origin Starting point
     * @param destination Ending point
     * @return List of LatLng points representing the route
     */
    suspend fun getDirections(origin: LatLng, destination: LatLng): Result<List<LatLng>> {
        return try {
            val response = directionsApi.getDirections(
                origin = "${origin.latitude},${origin.longitude}",
                destination = "${destination.latitude},${destination.longitude}",
                mode = "walking",
                apiKey = apiKey
            )

            if (response.status == "OK" && response.routes.isNotEmpty()) {
                val route = response.routes[0]
                if (route.legs.isNotEmpty()) {
                    val points = mutableListOf<LatLng>()
                    route.legs.forEach { leg ->
                        leg.steps.forEach { step ->
                            // Decode polyline for each step
                            val decodedPoints = decodePolyline(step.polyline.points)
                            points.addAll(decodedPoints)
                        }
                    }
                    Result.success(points)
                } else {
                    Result.failure(Exception("No legs found in route"))
                }
            } else {
                Result.failure(Exception("Directions API failed: ${response.status} - ${response.errorMessage}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Failed to get directions: ${e.message}", e))
        }
    }

    /**
     * Decode polyline string into list of LatLng points
     * Algorithm from Google's polyline encoding documentation
     */
    private fun decodePolyline(polyline: String): List<LatLng> {
        val points = mutableListOf<LatLng>()
        var index = 0
        val len = polyline.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var shift = 0
            var result = 0
            do {
                val b = polyline[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlat = if ((result and 1) != 0) {
                (result shr 1).inv()
            } else {
                result shr 1
            }
            lat += dlat

            shift = 0
            result = 0
            do {
                val b = polyline[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)

            val dlng = if ((result and 1) != 0) {
                (result shr 1).inv()
            } else {
                result shr 1
            }
            lng += dlng

            val latLng = LatLng(
                lat / 1E5,
                lng / 1E5
            )
            points.add(latLng)
        }

        return points
    }

    /**
     * Retrofit API interface for Google Directions API
     */
    interface DirectionsApi {
        @GET("directions/json")
        suspend fun getDirections(
            @Query("origin") origin: String,
            @Query("destination") destination: String,
            @Query("mode") mode: String = "walking",
            @Query("key") apiKey: String
        ): DirectionsResponse
    }

    /**
     * Data classes for parsing Google Directions API response
     */
    data class DirectionsResponse(
        @SerializedName("routes")
        val routes: List<Route>,
        @SerializedName("status")
        val status: String,
        @SerializedName("error_message")
        val errorMessage: String?
    )

    data class Route(
        @SerializedName("legs")
        val legs: List<Leg>,
        @SerializedName("overview_polyline")
        val overviewPolyline: OverviewPolyline
    )

    data class Leg(
        @SerializedName("steps")
        val steps: List<Step>,
        @SerializedName("distance")
        val distance: Distance,
        @SerializedName("duration")
        val duration: Duration
    )

    data class Step(
        @SerializedName("polyline")
        val polyline: OverviewPolyline,
        @SerializedName("distance")
        val distance: Distance,
        @SerializedName("duration")
        val duration: Duration,
        @SerializedName("start_location")
        val startLocation: LocationPoint,
        @SerializedName("end_location")
        val endLocation: LocationPoint
    )

    data class OverviewPolyline(
        @SerializedName("points")
        val points: String
    )

    data class Distance(
        @SerializedName("text")
        val text: String,
        @SerializedName("value")
        val value: Int
    )

    data class Duration(
        @SerializedName("text")
        val text: String,
        @SerializedName("value")
        val value: Int
    )

    data class LocationPoint(
        @SerializedName("lat")
        val lat: Double,
        @SerializedName("lng")
        val lng: Double
    ) {
        fun toLatLng(): LatLng = LatLng(lat, lng)
    }
}