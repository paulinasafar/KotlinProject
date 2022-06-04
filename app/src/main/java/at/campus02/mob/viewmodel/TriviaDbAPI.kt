package at.campus02.mob.viewmodel

import com.squareup.moshi.Moshi
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

data class QuestionsResponse (
    val response_code: Int,
    val results: List<Question>
)

// https://opentdb.com/api.php?amount=10&type=multiple&difficulty=easy
interface TriviaDbAPI {

    @GET("api.php?amount=10&type=multiple&difficulty=easy")
    fun getQuestions(): Call<QuestionsResponse>
}

val triviaDbApi = Retrofit.Builder()
        //JSON converter: Moshi, keine zusätzlichen eigenen Converter
    .addConverterFactory(
        MoshiConverterFactory.create(
            Moshi.Builder().build()
        )
    )

//BASE URL die API
    .baseUrl("https://opentdb.com/")
//Konfiguration bauen
    .build()
//API Interface implementieren lassen
    .create(TriviaDbAPI::class.java)