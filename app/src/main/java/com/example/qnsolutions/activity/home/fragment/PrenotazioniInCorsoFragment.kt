package com.example.qnsolutions.activity.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qnsolutions.adapters.PrenotazioneAdapter
import com.example.qnsolutions.databinding.FragmentPrenotazioniRecyclerBinding
import com.example.qnsolutions.models.PrenotazioneModel

class PrenotazioniInCorsoFragment(val list: MutableList<PrenotazioneModel>) : Fragment()
{
    lateinit var binding: FragmentPrenotazioniRecyclerBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentPrenotazioniRecyclerBinding.inflate(inflater)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = PrenotazioneAdapter(list, requireContext(), true)

        return binding.root
    }
}