package com.matuamod.converter

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.matuamod.converter.databinding.FragmentKeyboardBinding

class KeyboardFragment : Fragment() {
    private val dataModel: DataModel by activityViewModels()
    lateinit var binding: FragmentKeyboardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentKeyboardBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindingClickListener()
    }


    private fun bindingClickListener() {
        binding.button0.setOnClickListener {
            dataModel.digit.value = "0"
        }
        binding.button1.setOnClickListener {
            dataModel.digit.value = "1"
        }
        binding.button2.setOnClickListener {
            dataModel.digit.value = "2"
        }
        binding.button3.setOnClickListener {
            dataModel.digit.value = "3"
        }
        binding.button4.setOnClickListener {
            dataModel.digit.value = "4"
        }
        binding.button5.setOnClickListener {
            dataModel.digit.value = "5"
        }
        binding.button6.setOnClickListener {
            dataModel.digit.value = "6"
        }
        binding.button7.setOnClickListener {
            dataModel.digit.value = "7"
        }
        binding.button8.setOnClickListener {
            dataModel.digit.value = "8"
        }
        binding.button9.setOnClickListener {
            dataModel.digit.value = "9"
        }
        binding.buttonCrop.setOnClickListener {
            dataModel.digit.value = "."
        }
        binding.button0.setOnClickListener {
            dataModel.digit.value = "0"
        }
        binding.buttonDelete.setOnClickListener {
            dataModel.digit.value = "delete"
        }
        binding.buttonEnter.setOnClickListener {
            dataModel.digit.value = "enter"
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = KeyboardFragment()
    }
}