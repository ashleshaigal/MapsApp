package com.example.mapsapp.ui.login

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.mapsapp.R
import com.example.mapsapp.databinding.FragmentLoginScreenBinding
import com.example.mapsapp.util.States
import kotlinx.coroutines.launch

class LoginScreen : Fragment() {

    private lateinit var viewModel: LoginScreenViewModel
    private lateinit var binding: FragmentLoginScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginScreenBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this)[LoginScreenViewModel::class.java]
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isLoginButtonEnabled.observe(viewLifecycleOwner) {
            binding.buttonLogin.isEnabled = it
        }

        viewModel.states.observe(viewLifecycleOwner) {
            when (it) {
                is States.Loading -> showLoader()
                else -> hideLoader()
            }
        }

        binding.buttonLogin.setOnClickListener {
            viewModel.viewModelScope.launch {

                viewModel.login().apply {

                    val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return@apply
                    with (sharedPref.edit()) {
                        putString(getString(R.string.preference_email_key), viewModel.email.value)
                        apply()
                    }
                   findNavController()
                        .navigate(LoginScreenDirections.actionLoginScreenToMapsScreen(viewModel.email.value.toString()))
                }
            }


        }

    }

    private fun showLoader() {
        binding.loader.visibility = View.VISIBLE
        binding.buttonLogin.visibility = View.GONE
    }

    private fun hideLoader() {
        binding.loader.visibility = View.GONE
        binding.buttonLogin.visibility = View.VISIBLE
    }

}