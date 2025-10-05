package com.group8.mobilesecurity.network

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // OPTIONAL: Certificate pinning example.
    // Replace "jsonplaceholder.typicode.com" and fingerprint with your target host and the correct
    // certificate SHA-256 pin. This prevents MITM even if system trusts CA.
    // Use `openssl s_client -connect host:443 -showcerts` and compute SHA256, or fetch from browser devtools.
    private val certificatePinner = CertificatePinner.Builder()
        // Example placeholder pin — DO NOT use in production as-is. Replace with real hash:
        .add("jsonplaceholder.typicode.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
        .build()

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(logging)
        .apply {
            // Uncomment the next line to enable pinning. Keep commented during development if you don't have the exact fingerprint.
            // certificatePinner(certificatePinner)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://jsonplaceholder.typicode.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttp)
        .build()

    val api: ApiService = retrofit.create(ApiService::class.java)
}
