package at.campus02.mob.viewmodel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import at.campus02.mob.viewmodel.databinding.FragmentStartBinding

class StartFragment : Fragment() {

    private lateinit var binding: FragmentStartBinding
    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartBinding.inflate(inflater)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.startButton.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_gameFragment)
            gameViewModel.start()
        }
    }
}