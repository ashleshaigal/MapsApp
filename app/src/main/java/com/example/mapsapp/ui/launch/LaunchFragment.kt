package com.example.mapsapp.ui.launch

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.mapsapp.R
import com.example.mapsapp.databinding.FragmentLaunchBinding

class LaunchFragment : Fragment() {


    private lateinit var binding: FragmentLaunchBinding
    private lateinit var viewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLaunchBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]
        binding.viewModel = viewModel

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val email = sharedPref.getString(getString(R.string.preference_email_key),"")
        if (email != null ) {
            viewModel.setUser(email)
        }


        val navController = findNavController()
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null && user.email != "") {
                navController.navigate(LaunchFragmentDirections.actionLaunchFragment2ToMapsScreen( user.email))
            } else {
                navController.navigate(LaunchFragmentDirections.actionLaunchFragment2ToLoginScreen())
            }
        }
    }


}