package com.sofiyashahi.aitranslator

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class TranslationService {

    private val TAG = "TranslationService"
    private val url = "https://api.openai.com/v1/completions"
    private val apiKey = "YOUR_API_KEY"
    private val client = OkHttpClient()

    fun translateText(sourceText: String, sourceLanguage: String, targetLanguage: String, callback: (String) -> Unit){
        //use this model when it's released = gpt-3.5-turbo-instruct
        Log.d(TAG, "translateText: $sourceLanguage to $targetLanguage, $sourceText")
        val prompt = "Translate the following $sourceLanguage text to $targetLanguage: '$sourceText'"
        val requestBody = """
            {
            "model": "text-davinci-003",
            "prompt": "Translate the following $sourceLanguage text to $targetLanguage: '$sourceText'",
            "max_tokens": 500,
            "temperature": 0
            }
            """.trimIndent()
        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "onFailure: Api connection fail")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d(TAG, "onResponse: $responseBody")

                val jsonObject = JSONObject(responseBody)
                val choiceArray:JSONArray = jsonObject.getJSONArray("choices")

                if(choiceArray.length() > 0){
                    val translation = choiceArray.getJSONObject(0).getString("text")
                    Log.d(TAG, "translateText: $translation")
                    callback(translation)
                } else {
                    Log.d(TAG, "onResponse: No choice Array found")
                }
            }
        })
    }
}
