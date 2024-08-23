package com.vscodeners.ui_study_android.feature

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.vscodeners.ui_study_android.feature.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var navController: NavController? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = view.findNavController()

        binding.btnLogin.setOnClickListener {
            navController?.navigate(R.id.action_navigate_fragment_to_login)
        }

        binding.btnScreenShot.setOnClickListener {
            navController?.navigate(R.id.action_navigate_fragment_to_screen_shot)
        }

        binding.btnProgressBar.setOnClickListener {
            navController?.navigate(R.id.action_navigate_fragment_to_progress_bar)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}