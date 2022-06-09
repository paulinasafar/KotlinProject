package at.campus02.mob.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class Choice {
    A, B, C, D, NONE
}

data class Question(
    val question: String,
    private val correct_answer: String,
    private val incorrect_answers: List<String>
) {
    private var _randomizedAnswers: List<String>? = null
    private val randomizedAnswers: List<String>
        get() {
            if (_randomizedAnswers == null) {
                _randomizedAnswers = (incorrect_answers + correct_answer).shuffled()
            }
            return _randomizedAnswers!!
        }
    val answerA get() = randomizedAnswers[0] // when it's a method, we can execute in the moment we need it
    val answerB get() = randomizedAnswers[1] // without a get(), it's only a pointer to the value in memory
    val answerC get() = randomizedAnswers[2] // at the beginning the value in memory is always null
    val answerD get() = randomizedAnswers[3] // because that is how Moshi converter works
    val correctChoice
        get() = when (correct_answer) {
            answerA -> Choice.A
            answerB -> Choice.B
            answerC -> Choice.C
            answerD -> Choice.D
            else -> throw IllegalStateException("No correct answer found!")
        }
    var choice: Choice? = null

    fun choose(userChoice: Choice) {
        choice = userChoice
    }

    val isAnswered get() = choice != null
    val isCorrect get() = isAnswered && choice == correctChoice
}

class GameViewModel : ViewModel() {

    // intern, veränderbar
    private var questionsMutable: MutableLiveData<List<Question>> = MutableLiveData()
    private var questionMutable: MutableLiveData<Question> = MutableLiveData()
    private var buttonMarkersMutable: MutableLiveData<Map<Choice, Int>> = MutableLiveData(
        mapOf(
            Choice.A to R.drawable.button_background,
            Choice.B to R.drawable.button_background,
            Choice.C to R.drawable.button_background,
            Choice.D to R.drawable.button_background,
        )
    )
    private var guessingProgressMutable: MutableLiveData<Int> = MutableLiveData(0)
    private var scoreMutable: MutableLiveData<String> = MutableLiveData()
    private var progressMarkersMutable: MutableLiveData<List<Int>> = MutableLiveData()
    private var errorMutable: MutableLiveData<String> = MutableLiveData()

    // von außen sichtbar, aber nicht veränderbar
    val questions: LiveData<List<Question>> get() = questionsMutable
    val question: LiveData<Question> get() = questionMutable
    val buttonMarkers: LiveData<Map<Choice, Int>> get() = buttonMarkersMutable
    val guessingProgress: LiveData<Int> get() = guessingProgressMutable
    val score: LiveData<String> get() = scoreMutable
    val progressMarkers: LiveData<List<Int>> get() = progressMarkersMutable
    val error: LiveData<String> get() = errorMutable

    // index auf den Fragen
    private var index = 0

    // User Aktionen - start das Spiel
    fun start() {

        errorMutable.value = null

/*        // REST access mit "Call" interface
        triviaDbApi.getQuestions().enqueue(object: Callback<QuestionsResponse> {
            override fun onResponse(
                call: Call<QuestionsResponse>,
                response: Response<QuestionsResponse>
            ) {
                if(response.isSuccessful) {
                    val questionsFromServer = response.body()?.results
                    index = 0
                    questionsMutable.value = questionsFromServer
                    questionMutable.value = questionsMutable.value?.get(index)
                    updateButtonMarkers()
                    updateProgressMarkers()
                    updateScore()
                    guessingCountDownTimer.start()
                } else {
                    errorMutable.value = "Error when fetching questions: ${response.message()}"
                }}
            override fun onFailure(call: Call<QuestionsResponse>, t: Throwable) {
                errorMutable.value = "Communication Failure: ${t.message}"
            }} )
 */
        // REST access mit Kotlin couroutines
        // ViewModelScope.launch -> kümmert sich um anderen Thread
        viewModelScope.launch {
            try {
                val response = triviaDbApi.getQuestionsWithCoroutines(10)
                if (response.isSuccessful) {
                    //MainScope.lauch -> wieder in den UI Thread
                    MainScope().launch {
                        val questionsFromServer = response.body()?.results
                        index = 0
                        questionsMutable.value = questionsFromServer
                        questionMutable.value = questionsMutable.value?.get(index)
                        updateButtonMarkers()
                        updateProgressMarkers()
                        updateScore()
                        guessingCountDownTimer.start()
                    }
                } else {
                    // Achtung : hier sind wir noch im ViewModelScope
                    // -> also entweder wieder MainSchope verwenden oder
                    // postValue (LiveData) -> kümmert sich dann um das Thread-Handling
                    errorMutable.postValue("Error when fetching questions: ${response.message()}")
                }
            } catch (exc: Exception) {
                // Communication failure
                errorMutable.postValue("Communication Failure: ${exc.message}")

            }
        }
    }

    fun chooseAnswer(choice: Choice) {
        if (question.value?.isAnswered != true) {
            question.value?.choose(choice)
            updateButtonMarkers()
            updateProgressMarkers()
            guessingCountDownTimer.cancel()
            updateScore()
        }
    }

    // continue button is clicked. We vcan use it only when the question is answered.
    // we get the number of the question with the increasing index
    fun next() {
        if (question.value?.isAnswered == true) {
            index++
            if (index < (questions.value?.size ?: 0)) {
                questionMutable.value = questionsMutable.value?.get(index)
                updateButtonMarkers()
                updateProgressMarkers()
                guessingCountDownTimer.start()
            }
        }
    }

    //Timer - countdown
    private val guessingCountDownTimer = object {
        private lateinit var countDownTimer: CountDownTimer

        //parametri su granice unutar kojeg vremena se countdown mora odvijati
        fun start() {
            guessingProgressMutable.value = 100
            countDownTimer = object : CountDownTimer(10_000, 500) {
                override fun onTick(remainingMillis: Long) {
                    guessingProgressMutable.value = ((remainingMillis / 10_000.0) * 100).toInt()
                }

                //Answer is not given by the user, but by system (NONE)
                override fun onFinish() {
                    guessingProgressMutable.value = 0
                    if (question.value?.isAnswered == false)
                        chooseAnswer(Choice.NONE)
                }
            }
            countDownTimer.start()
        }

        // to stop counting down when the answer is given
        fun cancel() {
            guessingProgressMutable.value = 0
            countDownTimer.cancel()
        }
    }

    // Hilfsmethoden
    private fun updateScore() {
        val allQuestions = questionsMutable.value ?: return
        if (allQuestions.all { it.isAnswered }) {
            scoreMutable.value =
                "Score: ${allQuestions.count { it.isCorrect }} / ${allQuestions.size} correct"
        } else {
            scoreMutable.value = null
        }
    }

    private fun updateButtonMarkers() {
        buttonMarkersMutable.value = mapOf(
            Choice.A to buttonResourceFor(question.value, Choice.A),
            Choice.B to buttonResourceFor(question.value, Choice.B),
            Choice.C to buttonResourceFor(question.value, Choice.C),
            Choice.D to buttonResourceFor(question.value, Choice.D),
        )
    }

    private fun buttonResourceFor(question: Question?, choice: Choice): Int {
        return when {
            // keine Frage vorhanden: alle Buttons neutral
            question == null -> R.drawable.button_background
            // Frage noch nicht beantwortet: alle Buttons neutral
            !question.isAnswered -> R.drawable.button_background
            // Button entspricht der richtigen Antwort und die Frage wurde korrekt beantwortet: grün
            question.isCorrect && choice == question.correctChoice -> R.drawable.button_background_correct
            // Hint
            !question.isCorrect && choice == question.correctChoice -> R.drawable.button_background_hint
            // Button entspricht falsch gegebener Antwort
            !question.isCorrect && choice == question.choice -> R.drawable.button_background_incorrect
            // falls kein anderer Fall zuständig ist: neutral
            else -> R.drawable.button_background
        }
    }

    private fun updateProgressMarkers() {
        progressMarkersMutable.value = listOf(
            progressResourceFor(0),
            progressResourceFor(1),
            progressResourceFor(2),
            progressResourceFor(3),
            progressResourceFor(4),
            progressResourceFor(5),
            progressResourceFor(6),
            progressResourceFor(7),
            progressResourceFor(8),
            progressResourceFor(9),
        )
    }

    private fun progressResourceFor(index: Int): Int {
        // Die Frage, die dem Progress Indikator mit diesem index entrspricht (currently being shown)
        val progressQuestion = questions.value?.get(index)
        return when {
            // keine passende Frage -> neutraler Mittlergrund
            progressQuestion == null -> R.drawable.progress_unanswered
            // header für die aktuelle Frage, noch unbeantwortet
            progressQuestion == question.value && !progressQuestion.isAnswered -> R.drawable.progress_current
            // header für die aktuelle Frage, falsch beantwortet
            progressQuestion == question.value && !progressQuestion.isCorrect -> R.drawable.progress_current_incorrect
            // header für die aktuelle Frage, richtig beantwortet
            progressQuestion == question.value && progressQuestion.isCorrect -> R.drawable.progress_current_correct
            // header für bereits beantwortete Frage, falsch beantwortet/
            progressQuestion.isAnswered && progressQuestion.isCorrect -> R.drawable.progress_correct
            // header für bereits beantwortete Frage, richtig beantwortet/
            progressQuestion.isAnswered && !progressQuestion.isCorrect -> R.drawable.progress_incorrect

            else -> R.drawable.progress_unanswered
        }
    }

}