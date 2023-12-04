package com.example.vinonovi2

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class ApiManager {
    suspend fun uploadImage(context: Context, question: String): List<DataItem> =
        withContext(Dispatchers.IO) {
            // navDeepLink
            val url = "http://192.168.1.44:5000/predict"
            val client = OkHttpClient()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "question",
                    question
                )
                .build()

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val dataItemList: MutableList<DataItem> = emptyList<DataItem>().toMutableList()

            try {
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    // Image uploaded successfully
                    val responseBody = response.body?.string()

                    // Extract values from JSON
                    val jsonArray = JSONArray(responseBody)

                    for(i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)

                        val image: String = jsonObject.getString("image")
                        val answer: String = jsonObject.getString("answer")
                        Log.d("image", image)
                        Log.d("answer", answer)
                        val dataItem = DataItem(image, answer)

                        dataItemList += dataItem
                    }
                } else {
                    Log.e("망함", "망함")
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return@withContext dataItemList
        }
}

data class DataItem(val image: String, val answer: String)
