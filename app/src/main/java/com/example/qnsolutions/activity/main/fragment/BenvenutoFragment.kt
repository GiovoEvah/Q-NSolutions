package com.example.qnsolutions.activity.main.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.qnsolutions.activity.main.MainActivity
import com.example.qnsolutions.databinding.FragmentBenvenutoBinding
import com.example.qnsolutions.util.NavigationManager


class BenvenutoFragment : Fragment()
{
    private lateinit var binding: FragmentBenvenutoBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentBenvenutoBinding.inflate(inflater)

        val mainActivity = requireActivity() as MainActivity

        binding.accediButton.setOnClickListener()
        {
            //apri il fragment dell'accesso
            NavigationManager().scambiaFragment(mainActivity, mainActivity.frame, LoginFragment(), "login", mainActivity.root_tag, false, null)
        }

        binding.registratiButton.setOnClickListener()
        {
            //apri il fragment della registrazione
            NavigationManager().scambiaFragment(mainActivity, mainActivity.frame, RegisterFragment(), "registrati", mainActivity.root_tag, false, null)
        }

        return binding.root
    }
}