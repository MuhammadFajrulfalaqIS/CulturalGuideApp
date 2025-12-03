package com.example.cultural_navigation_papb.data.api

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

/**
 * Service untuk upload gambar ke Cloudinary
 * Cloudinary adalah cloud storage gratis untuk gambar
 */
class CloudinaryService(private val context: Context) {

    companion object {
        private const val TAG = "CloudinaryService"

        // ✅ SECURE: Get credentials from BuildConfig (loaded from local.properties)
        private val CLOUD_NAME = com.example.cultural_navigation_papb.BuildConfig.CLOUDINARY_CLOUD_NAME
        private val UPLOAD_PRESET = com.example.cultural_navigation_papb.BuildConfig.CLOUDINARY_UPLOAD_PRESET

        private val UPLOAD_URL = "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"
    }

    private val client = OkHttpClient()

    /**
     * Upload gambar dari Uri ke Cloudinary
     * @param imageUri Uri dari gambar (dari gallery/camera)
     * @return URL gambar yang sudah diupload, atau null jika gagal
     */
    suspend fun uploadImage(imageUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📤 Uploading image to Cloudinary: $imageUri")

            // Convert Uri to File
            val file = uriToFile(imageUri)
            if (file == null || !file.exists()) {
                Log.e(TAG, "❌ File not found or invalid")
                return@withContext null
            }

            // Build multipart request
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.name, file.asRequestBody("image/*".toMediaTypeOrNull()))
                .addFormDataPart("upload_preset", UPLOAD_PRESET)
                .build()

            val request = Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build()

            // Execute request
            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val json = JSONObject(responseBody ?: "")
                val imageUrl = json.getString("secure_url")

                Log.d(TAG, "✅ Upload successful: $imageUrl")

                // Delete temporary file
                file.delete()

                return@withContext imageUrl
            } else {
                Log.e(TAG, "❌ Upload failed: ${response.code} - ${response.message}")
                file.delete()
                return@withContext null
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error uploading image", e)
            return@withContext null
        }
    }

    /**
     * Convert Uri to File untuk upload
     */
    private fun uriToFile(uri: Uri): File? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                Log.e(TAG, "❌ Cannot open input stream for uri: $uri")
                return null
            }

            // Create temp file
            val tempFile = File(context.cacheDir, "temp_upload_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(tempFile)

            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()

            return tempFile
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error converting uri to file", e)
            return null
        }
    }

    /**
     * Upload multiple images
     * @return List of image URLs
     */
    suspend fun uploadImages(imageUris: List<Uri>): List<String> {
        val uploadedUrls = mutableListOf<String>()

        for (uri in imageUris) {
            val url = uploadImage(uri)
            if (url != null) {
                uploadedUrls.add(url)
            }
        }

        return uploadedUrls
    }
}
