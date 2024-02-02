package com.example.qnsolutions.activity.home.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qnsolutions.R
import com.example.qnsolutions.activity.home.HomeActivity
import com.example.qnsolutions.adapters.AttrazioneAdapter
import com.example.qnsolutions.common.FilterWrapper
import com.example.qnsolutions.databinding.FragmentRicercaAttrazioneBinding
import com.example.qnsolutions.models.AttrazioneTuristicaModel
import com.example.qnsolutions.util.DBMSQuery
import com.example.qnsolutions.util.QueryReturnCallback

class RicercaAttrazioneFragment : Fragment()
{
    lateinit var binding: FragmentRicercaAttrazioneBinding
    private lateinit var filtri: FilterWrapper
    private lateinit var attrazioneAdapter: AttrazioneAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentRicercaAttrazioneBinding.inflate(inflater)
        val cittaId = requireArguments().getInt("cittaId")!!
        val utenteId = requireArguments().getString("utenteId")!!
        binding.ricercaAttrazioneRecycle.layoutManager = LinearLayoutManager(requireContext())
        attrazioneAdapter = AttrazioneAdapter(mutableListOf(), requireActivity() as HomeActivity, utenteId, mutableListOf())
        binding.ricercaAttrazioneRecycle.adapter = attrazioneAdapter

        DBMSQuery().prendiPreferiti(requireContext(), utenteId, object : QueryReturnCallback
        {
            override fun onReturnValue(response: Any, message: String)
            {
                attrazioneAdapter.preferiti.addAll(response as MutableList<Int>)
            }

            override fun onQueryFailed(fail: String) {}

            override fun onQueryError(error: String) {}

        })

        val spinnerCucinaAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(), R.array.cucinaFilter, android.R.layout.simple_spinner_item)
        val spinnerTipoAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(), R.array.tipoAttrazioneFilter, android.R.layout.simple_spinner_item)

        val spinnerPrezzoAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(), R.array.prezzoFilter, android.R.layout.simple_spinner_item)

        val spinnerRatingAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(), R.array.ratingFilter, android.R.layout.simple_spinner_item)

        spinnerCucinaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPrezzoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRatingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.tipoAttrazioneFilter.adapter = spinnerTipoAdapter
        binding.ratingFilter.adapter = spinnerRatingAdapter
        binding.prezzoFilter.adapter = spinnerPrezzoAdapter
        binding.tipoCucinaFilter.adapter = spinnerCucinaAdapter

        filtri = FilterWrapper(cittaId, binding.tipoAttrazioneFilter.selectedItem.toString(),
            binding.tipoCucinaFilter.selectedItem.toString(),
            binding.ratingFilter.selectedItem.toString(),
            binding.prezzoFilter.selectedItem.toString(),
        )

        binding.ricercaAttrazioneSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(newText: String?): Boolean
            {
                //cerca attrazioni
                //prende i valori dagli spinner e li inserisce nella classe filtri
                filtri.tipoAttrazione = binding.tipoAttrazioneFilter.selectedItem.toString()
                filtri.tipoCucina = binding.tipoCucinaFilter.selectedItem.toString()
                filtri.rating = binding.ratingFilter.selectedItem.toString()
                filtri.prezzo_medio = binding.prezzoFilter.selectedItem.toString()

                Log.d("Filter", "Cucina: ${filtri.tipoCucina}")
                Log.d("Filter", "Rating: ${filtri.rating}")
                Log.d("Filter", "Prezzo: ${filtri.prezzo_medio}")
                Log.d("Filter", "Tipo: ${filtri.tipoAttrazione}")
                Log.d("Filter", "Città: ${filtri.cittaId}")

                if(filtri.tipoAttrazione != "Ristorante")//evito di mettere il filtro di cucita per i non ristoranti
                {
                    filtri.tipoCucina = "Nessun filtro"
                    binding.tipoCucinaFilter.setSelection(0)
                }

                attrazioneAdapter.svuota()

                DBMSQuery().getAttrazioniCitta(requireContext(), filtri, object : QueryReturnCallback
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
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean
            {
                if (newText != null)
                {
                    setFilter(newText)
                }
                return true
            }
        })

        return binding.root
    }

    private fun setFilter(text: String)
    {
        var filter = mutableListOf<AttrazioneTuristicaModel>()

        for (item in attrazioneAdapter.fullList)
        {
            if(item.nome.lowercase().contains(text.lowercase()) || item.indirizzo.lowercase().contains(text.lowercase()))
            {
                filter.add(item)
            }
        }

        attrazioneAdapter.setFilteredList(text, filter)
    }
}