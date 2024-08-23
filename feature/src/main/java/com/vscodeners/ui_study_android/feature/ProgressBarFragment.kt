package com.vscodeners.ui_study_android.feature

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.vscodeners.ui_study_android.feature.databinding.FragmentProgressBarBinding
import java.util.Timer
import kotlin.concurrent.timer

class ProgressBarFragment : Fragment() {
    private var _binding: FragmentProgressBarBinding? = null
    private val binding get() = _binding!!

    private var timer: Timer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_progress_bar, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStartProgress.setOnClickListener {
            startProgress()
        }
    }

    private fun startProgress() {
        if (timer != null) {
            timer!!.cancel()
        }
        var progress = 0
        timer = timer(period = 10) {
            progress++
            binding.progressBar.progress = progress
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (timer != null) {
            timer!!.cancel()
        }
        _binding = null
    }
}