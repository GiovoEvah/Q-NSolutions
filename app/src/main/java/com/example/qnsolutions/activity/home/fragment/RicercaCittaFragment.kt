package com.example.qnsolutions.activity.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qnsolutions.activity.home.HomeActivity
import com.example.qnsolutions.adapters.CittaAdapter
import com.example.qnsolutions.databinding.FragmentRicercaCittaBinding
import com.example.qnsolutions.models.CittaModel
import com.example.qnsolutions.util.DBMSQuery
import com.example.qnsolutions.util.QueryReturnCallback

class RicercaCittaFragment : Fragment()
{
    lateinit var binding: FragmentRicercaCittaBinding
    lateinit var cittaAdapter: CittaAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentRicercaCittaBinding.inflate(inflater)
        val utenteId = requireArguments().getString("utenteId")!!
        //Lista vuota in caso di errori
        binding.ricercaCittaRecycle.layoutManager = LinearLayoutManager(requireContext())
        cittaAdapter = CittaAdapter(mutableListOf(), requireActivity() as HomeActivity, utenteId)
        binding.ricercaCittaRecycle.adapter = cittaAdapter
        //città da prendere dal db
        DBMSQuery().getListaCitta(requireContext(), object : QueryReturnCallback
        {
            override fun onReturnValue(response: Any, message: String)
            {
                cittaAdapter.addCitta(response as CittaModel)

            }

            override fun onQueryFailed(fail: String)//città non trovate, non dovrebbe succedere
            {
                Toast.makeText(context, fail, Toast.LENGTH_LONG).show()

            }

            override fun onQueryError(error: String)//errore di rete
            {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        })

        binding.ricercaCittaView.setOnQueryTextListener(object : SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(newText: String?): Boolean
            {
                return false
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
        var filter = mutableListOf<CittaModel>()

        for (item in cittaAdapter.fullList)
        {
            if(item.nome.lowercase().contains(text.lowercase()) || item.nazione.lowercase().contains(text.lowercase()))
            {
                filter.add(item)
            }
        }

        cittaAdapter.setFilteredList(text, filter)
    }
}

