package com.example.qnsolutions.activity.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qnsolutions.R
import com.example.qnsolutions.adapters.RecensioneAdapter
import com.example.qnsolutions.databinding.FragmentRecenisioneBinding
import com.example.qnsolutions.models.RecensioneModel
import com.example.qnsolutions.util.DBMSQuery
import com.example.qnsolutions.util.QueryReturnCallback


class RecensioniFragment(private val attrazioneId: Int, private val utenteId: String) : DialogFragment()
{
    lateinit var binding: FragmentRecenisioneBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentRecenisioneBinding.inflate(inflater)

        DBMSQuery().getRecensioni(requireContext(), attrazioneId, object : QueryReturnCallback
        {
            override fun onReturnValue(response: Any, message: String)
            {
                binding.recensioniRecycler.layoutManager = LinearLayoutManager(requireContext())
                binding.recensioniRecycler.adapter = RecensioneAdapter(response as MutableList<RecensioneModel>)
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

        binding.aggiungiRecensioneButton.setOnClickListener()
        {
            if (binding.aggiungiRecensione.visibility == View.GONE)
            {
                binding.aggiungiRecensione.visibility = View.VISIBLE
                binding.aggiungiRecensioneButton.setImageResource(R.drawable.close_icon)
            }
            else
            {
                azzeraCampi()
            }
        }

        binding.salvaRecensioneButton.setOnClickListener()
        {
            val titolo = binding.titoloNuovaRecensione.editText!!.text.toString()
            val descrizione = binding.descrizioneNuovaRecensione.editText!!.text.toString()
            val rating = binding.ratingNuovaRecensione.rating.toBigDecimal()

            val titoloOk = !(titolo.length > 20 || titolo.isBlank())
            val descrizioneOk = !(descrizione.length > 250 || descrizione.isBlank())

            if(titoloOk && descrizioneOk)
            {
                DBMSQuery().aggiungiRecensione(requireContext(), attrazioneId, utenteId, titolo, descrizione, rating, object : QueryReturnCallback
                {
                    override fun onReturnValue(response: Any, message: String)
                    {
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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
            }

            azzeraCampi()
        }

        dialog?.setCanceledOnTouchOutside(true)

        return binding.root
    }

    fun azzeraCampi()
    {
        binding.aggiungiRecensione.visibility = View.GONE
        binding.aggiungiRecensioneButton.setImageResource(R.drawable.add_icon)

        //azzera campi
        binding.ratingNuovaRecensione.rating = 0.0F
        binding.titoloNuovaRecensione.editText!!.setText("")
        binding.descrizioneNuovaRecensione.editText!!.setText("")
    }
}