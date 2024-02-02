package com.example.qnsolutions.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.qnsolutions.activity.home.fragment.PrenotazioniInCorsoFragment
import com.example.qnsolutions.activity.home.fragment.PrenotazioniScaduteFragment
import com.example.qnsolutions.models.PrenotazioneModel

class FragmentPageAdapter(manager: FragmentManager, lifecycle: Lifecycle, val listaInCorso: MutableList<PrenotazioneModel>, val listaScaduti: MutableList<PrenotazioneModel>) : FragmentStateAdapter(manager, lifecycle)
{
    override fun getItemCount(): Int
    {
        return 2
    }

    override fun createFragment(position: Int): Fragment
    {
        return if (position == 0)
        {
            PrenotazioniInCorsoFragment(listaInCorso)
        }
        else
        {
            PrenotazioniScaduteFragment(listaScaduti)
        }
    }
}