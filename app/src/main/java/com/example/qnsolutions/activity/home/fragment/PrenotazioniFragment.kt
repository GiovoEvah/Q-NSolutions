package com.example.qnsolutions.activity.home.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.qnsolutions.adapters.FragmentPageAdapter
import com.example.qnsolutions.databinding.FragmentPrenotazioniBinding
import com.example.qnsolutions.models.PrenotazioneModel
import com.example.qnsolutions.models.UtenteModel
import com.example.qnsolutions.util.DBMSQuery
import com.example.qnsolutions.util.QueryReturnCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import java.time.LocalDateTime
import java.time.ZoneId

class PrenotazioniFragment() : Fragment()
{
    lateinit var binding: FragmentPrenotazioniBinding
    lateinit var listaInCorso: MutableList<PrenotazioneModel>
    lateinit var listaScaduti: MutableList<PrenotazioneModel>
    lateinit var utente: UtenteModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentPrenotazioniBinding.inflate(inflater)

        utente = requireArguments().getParcelable("utente")!!

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Prenotazioni in corso"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Prenotazioni scadute"))

        listaInCorso = mutableListOf()
        listaScaduti = mutableListOf()

        DBMSQuery().getPrenotazioni(requireContext(), utente.email, object : QueryReturnCallback
        {
            override fun onReturnValue(response: Any, message: String)
            {

                val list = response as MutableList<PrenotazioneModel>

                for (prenotazione in list)
                {
                    Log.d("DataOra", ZoneId.getAvailableZoneIds().toString())
                    Log.d("DataOra", "Prenotazione: ${prenotazione.data_ora_prenotazione}\nOra: ${LocalDateTime.now()}\nControllo:${LocalDateTime.parse(prenotazione.data_ora_prenotazione).isBefore(LocalDateTime.now())}")
                    if(LocalDateTime.parse(prenotazione.data_ora_prenotazione).isBefore(LocalDateTime.now(ZoneId.of("Europe/Rome"))))
                    {
                        listaScaduti.add(prenotazione)
                    }
                    else
                    {
                        listaInCorso.add(prenotazione)
                    }
                }

                binding.viewPager.adapter = FragmentPageAdapter(requireActivity().supportFragmentManager, lifecycle, listaInCorso, listaScaduti)

                binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener
                {
                    override fun onTabSelected(tab: TabLayout.Tab?)
                    {
                        if (tab != null)
                        {
                            binding.viewPager.currentItem = tab.position
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {}
                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                })

                binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback()
                {
                    override fun onPageSelected(position: Int)
                    {
                        super.onPageSelected(position)
                        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
                    }
                })
            }

            override fun onQueryFailed(fail: String)
            {
                Toast.makeText(requireContext(), fail, Toast.LENGTH_SHORT).show()
            }

            override fun onQueryError(error: String)
            {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        })

        return binding.root
    }
}