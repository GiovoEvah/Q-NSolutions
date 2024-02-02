package com.example.qnsolutions.activity.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.qnsolutions.databinding.FragmentContattiBinding

class ContattiFragment : Fragment()
{
    lateinit var binding: FragmentContattiBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentContattiBinding.inflate(inflater)

        return binding.root
    }
}