package at.campus02.mob.viewmodel

import com.squareup.moshi.Moshi
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class QuestionsResponse (
    val response_code: Int,
    val results: List<Question>
)

// https://opentdb.com/api.php?amount=10&type=m ultiple&difficulty=easy

interface TriviaDbAPI {

    //calling with Call function
    @GET("api.php123?amount=10&type=multiple&difficulty=easy")
    fun getQuestions(): Call<QuestionsResponse>

    //calling with "await" asynch function (suspend)
    @GET("api.php?type=multiple&difficulty=easy")
    suspend fun getQuestionsWithCoroutines(@Query("amount") amount: Int): Response<QuestionsResponse>
}

val triviaDbApi = Retrofit.Builder()
        //JSON converter: Moshi, keinen zusätzlichen eigenen Converter
    .addConverterFactory(
        MoshiConverterFactory.create(
            Moshi.Builder().build()
        )
    )

//BASE URL die API - if path doesn't work, we built in a "communication failure)
    .baseUrl("https://opentdb.com/")
//Konfiguration bauen
    .build()
//API Interface implementieren lassen
    .create(TriviaDbAPI::class.java)