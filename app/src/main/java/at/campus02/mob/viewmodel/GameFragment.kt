package at.campus02.mob.viewmodel

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import at.campus02.mob.viewmodel.databinding.FragmentGameBinding

class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding
    private val gameViewModel: GameViewModel by activityViewModels()

    private val progressIndicators: List<View> get() = listOf(
        binding.p1,
        binding.p2,
        binding.p3,
        binding.p4,
        binding.p5,
        binding.p6,
        binding.p7,
        binding.p8,
        binding.p9,
        binding.p10
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(inflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        // Observers
        gameViewModel.question.observe(this) { question ->
            binding.questionText.text = Html.fromHtml(question.question, Html.FROM_HTML_MODE_LEGACY)
            binding.button1Label.text = Html.fromHtml(question.answerA, Html.FROM_HTML_MODE_LEGACY)
            binding.button2Label.text = Html.fromHtml(question.answerB, Html.FROM_HTML_MODE_LEGACY)
            binding.button3Label.text = Html.fromHtml(question.answerC, Html.FROM_HTML_MODE_LEGACY)
            binding.button4Label.text = Html.fromHtml(question.answerD, Html.FROM_HTML_MODE_LEGACY)
        }
        gameViewModel.buttonMarkers.observe(this) { buttonMarkerMap ->
            binding.button1Layout.setBackgroundResource(buttonMarkerMap[Choice.A] ?: R.drawable.button_background)
            binding.button2Layout.setBackgroundResource(buttonMarkerMap[Choice.B] ?: R.drawable.button_background)
            binding.button3Layout.setBackgroundResource(buttonMarkerMap[Choice.C] ?: R.drawable.button_background)
            binding.button4Layout.setBackgroundResource(buttonMarkerMap[Choice.D] ?: R.drawable.button_background)
        }
        gameViewModel.guessingProgress.observe(this) { progressValue ->
            binding.progressBar.progress = progressValue
            binding.progressBar.visibility = if (progressValue > 0) View.VISIBLE else View.INVISIBLE
        }
        gameViewModel.score.observe(this) { score ->
            binding.continueButtonLabel.text = score ?: getString(R.string.continue_button_label)
        }
        gameViewModel.progressMarkers.observe(this) { markers ->
            progressIndicators.forEachIndexed { index, view ->
                view.setBackgroundResource(markers[index])
            }
        }
        gameViewModel.error.observe(this) { errorMessage ->
            if(errorMessage == null) {
                binding.errorMessage.text = null
                binding.errorMessage.visibility = View.GONE
            } else {
                binding.errorMessage.text = errorMessage
                binding.errorMessage.visibility = View.VISIBLE
            }

        }

        // User Aktionen
        binding.button1Layout.setOnClickListener {
            gameViewModel.chooseAnswer(Choice.A)
        }
        binding.button2Layout.setOnClickListener {
            gameViewModel.chooseAnswer(Choice.B)
        }
        binding.button3Layout.setOnClickListener {
            gameViewModel.chooseAnswer(Choice.C)
        }
        binding.button4Layout.setOnClickListener {
            gameViewModel.chooseAnswer(Choice.D)
        }

        binding.continueButtonLayout.setOnClickListener {
            gameViewModel.next()
        }
    }
}