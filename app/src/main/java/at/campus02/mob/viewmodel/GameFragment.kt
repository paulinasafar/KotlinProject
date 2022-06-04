package at.campus02.mob.viewmodel

import android.os.Bundle
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
            binding.questionText.text = question.question
            binding.button1Label.text = question.answerA
            binding.button2Label.text = question.answerB
            binding.button3Label.text = question.answerC
            binding.button4Label.text = question.answerD
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