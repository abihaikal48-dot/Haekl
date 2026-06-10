package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateAdvice(prompt: String, systemInstruction: String): Pair<String, Long> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API key is not configured, using simulated default fallback")
            return@withContext Pair(getSimulationFallbackAdvice(prompt), 150L)
        }

        val startTime = System.currentTimeMillis()
        try {
            // Build the standard JSON request
            val requestJson = JSONObject().apply {
                // Contents
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        })
                    })
                })
                
                // System Instruction
                if (systemInstruction.isNotEmpty()) {
                    put("systemInstruction", JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", systemInstruction)
                            })
                        })
                    })
                }
                
                // Generation Config
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                })
            }

            val requestBody = requestJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            val url = "$BASE_URL?key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Content-Type", "application/json")
                .build()

            client.newCall(request).execute().use { response ->
                val responseTime = System.currentTimeMillis() - startTime
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e(TAG, "Gemini API failed with code ${response.code}: $errBody")
                    return@withContext Pair("Maaf, API Gemini memberikan error (Code: ${response.code}).\n$errBody", responseTime)
                }

                val resString = response.body?.string() ?: ""
                val resJson = JSONObject(resString)
                val candidates = resJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.optJSONObject("content")
                    if (content != null) {
                        val parts = content.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val textValue = parts.getJSONObject(0).optString("text", "Tidak ada respon.")
                            return@withContext Pair(textValue, responseTime)
                        }
                    }
                }
                Pair("Maaf, format respon dari server Gemini tidak sesuai.", responseTime)
            }
        } catch (e: Exception) {
            val responseTime = System.currentTimeMillis() - startTime
            Log.e(TAG, "Gemini API request error", e)
            Pair("Koneksi gagal: Selidiki koneksi internet Anda atau periksa Kunci API Anda.\nDetail: ${e.localizedMessage}", responseTime)
        }
    }

    private fun getSimulationFallbackAdvice(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("evaluasi") || lower.contains("analisis") -> {
                "**[SIMULASI AI DUNIA v2] — Analisis Keuangan Haikal & Ummu**\n\n" +
                        "1. **Bocor Halus**: Pengeluaran hiburan dan impulsif harus dijaga di akhir bulan.\n" +
                        "2. **Dana Darurat**: Dana darurat Haikal baru 35% terisi. Target ideal Rp 4.500.000, terus tambah porsi bulanan.\n" +
                        "3. **Saran Posisi**: Lanjutkan porsi investasi Rp 100.000 ke reksadana pasar uang untuk menjamin likuiditas pernikahan di 2029.\n" +
                        "**Rekomendasi**: Pertahankan streak mencatat di atas 7 hari berturut-turut untuk meningkatkan Skor Kesehatan Finansial."
            }
            lower.contains("nikah") || lower.contains("rumah") || lower.contains("simulasi") -> {
                "**[SIMULASI AI DUNIA v2] — Analisis Target Pernikahan & Rumah**\n\n" +
                        "• **Target Nikah (2029)**: Sisa waktu sekitar 36 bulan. Menabung Rp 500.000/bulan secara patungan akan membuat dana Rp 20 Juta tercapai di pertengahan 2029!\n" +
                        "• **Target DP Rumah (2030)**: Target Rp 80 Juta (20% KPR). Nabung Rp 1.500.000/bulan berdua sangat diperlukan setelah menikah untuk melunasi DP di tahun 2030.\n" +
                        "• **Peluang Sukses**: **Sangat Realistis** jika cicilan motor Haikal lunas dalam 30 bulan ke depan, membebaskan cash flow sebesar Rp 550.000/bulan."
            }
            lower.contains("tips") || lower.contains("hemat") -> {
                "**[SIMULASI AI DUNIA v2] — 3 Tips Hemat Spesifik Hari Ini**\n\n" +
                        "1. **Hara Chicken Employee Benefit**: Manfaatkan makan gratis/diskon karyawan Hara Chicken tempat Haikal bekerja untuk menekan budget makan menjadi Rp 10.000/hari.\n" +
                        "2. **Kencan Kreatif**: Alihkan kencan ke taman kota atau masak bersama, menghemat Rp 75.000 per sesi kencan dibanding kafe.\n" +
                        "3. **Automasi Dana Darurat**: Set up auto-debet Rp 50.000 saat hari gajian untuk pos tabungan wajib."
            }
            else -> {
                "Halo Haikal dan Ummu! Saya asisten AI DUNIA v2.0.\n\n" +
                        "Gaji kalian saat ini masing-masing Rp 2.300.000. Untuk mencapai target menikah 2029 dan DP Rumah 2030, kuncinya adalah konsistensi mencatat transaksi dan menjaga rasio cicilan di bawah 30%.\n\n" +
                        "Ada yang bisa saya bantu analisa hari ini? Klik preset di bawah untuk simulasi instan!"
            }
        }
    }
}
