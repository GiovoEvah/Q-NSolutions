package com.example.qnsolutions.activity.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qnsolutions.activity.home.HomeActivity
import com.example.qnsolutions.adapters.AttrazioneAdapter
import com.example.qnsolutions.databinding.FragmentPreferitiBinding
import com.example.qnsolutions.models.AttrazioneTuristicaModel
import com.example.qnsolutions.models.UtenteModel
import com.example.qnsolutions.util.DBMSQuery
import com.example.qnsolutions.util.QueryReturnCallback

class PreferitiFragment : Fragment()
{
    private lateinit var binding: FragmentPreferitiBinding
    private lateinit var preferiti: MutableList<Int>
    private lateinit var attrazioneAdapter: AttrazioneAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentPreferitiBinding.inflate(inflater)

        val utente: UtenteModel = requireArguments().getParcelable("utente")!!
        preferiti = mutableListOf()
        attrazioneAdapter = AttrazioneAdapter(mutableListOf(), requireActivity() as HomeActivity, utente.email, preferiti)

        binding.preferitiRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.preferitiRecycler.adapter = attrazioneAdapter

        DBMSQuery().prendiPreferiti(requireContext(), utente.email, object : QueryReturnCallback
        {
            override fun onReturnValue(response: Any, message: String)
            {
                attrazioneAdapter.preferiti = response as MutableList<Int>
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

        DBMSQuery().getAttrazioniPreferite(requireContext(), utente.email, object : QueryReturnCallback
        {
            override fun onReturnValue(response: Any, message: String)
            {
                attrazioneAdapter.addAttrazione(response as AttrazioneTuristicaModel)
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