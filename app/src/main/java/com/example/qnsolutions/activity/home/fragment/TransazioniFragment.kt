package com.example.qnsolutions.activity.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qnsolutions.adapters.TransazioniAdapter
import com.example.qnsolutions.databinding.FragmentTransazioniBinding
import com.example.qnsolutions.models.TransazioniModel
import com.example.qnsolutions.models.UtenteModel
import com.example.qnsolutions.util.DBMSQuery
import com.example.qnsolutions.util.QueryReturnCallback

class TransazioniFragment : Fragment()
{
    lateinit var binding: FragmentTransazioniBinding
    lateinit var list: MutableList<TransazioniModel>
    lateinit var utente: UtenteModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentTransazioniBinding.inflate(inflater)

        list = mutableListOf()
        utente = requireArguments().getParcelable("utente")!!

        DBMSQuery().getTransazioni(requireContext(), utente.email, object : QueryReturnCallback
        {
            override fun onReturnValue(response: Any, message: String)
            {
                list = response as MutableList<TransazioniModel>
                binding.TransazioniRecycle.layoutManager = LinearLayoutManager(requireActivity())
                binding.TransazioniRecycle.adapter = TransazioniAdapter(list, requireContext())
            }

            override fun onQueryFailed(fail: String)
            {
                //nessuna transazione
            }

            override fun onQueryError(error: String)
            {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }

        })
        //layout e passaggio di dati all'adapter
        return binding.root
    }
}
