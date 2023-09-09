package com.sofiyashahi.aitranslator

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.sofiyashahi.aitranslator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var translationService: TranslationService
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        translationService = TranslationService()

        if(isInternetAvailable()){
            Log.d(TAG, "onCreate: internet is on")
        } else {
            Toast.makeText(this, "No internet connection. Please turn on your internet.", Toast.LENGTH_LONG).show()
        }

        binding.btnTranslate.setOnClickListener {
            if(isInternetAvailable()) {
                binding.progressBar.visibility = View.VISIBLE
                val sourceText = binding.srcText.text.toString()
                val sourceLanguage = binding.srcLang.text.toString()
                val targetLanguage = binding.trgtLang.text.toString()
                translationService.translateText(
                    sourceText,
                    sourceLanguage,
                    targetLanguage
                ) { translatedText ->
                    runOnUiThread {
                        binding.translatedText.text = translatedText
                        binding.progressBar.visibility = View.INVISIBLE
                    }
                }
            } else {
                Toast.makeText(this, "No internet connection. Please turn on your internet.", Toast.LENGTH_LONG).show()
            }
        }

        binding.clear.setOnClickListener {
            binding.translatedText.text = ""
        }

    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}