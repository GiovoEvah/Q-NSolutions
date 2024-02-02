package com.example.qnsolutions.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.example.qnsolutions.models.CittaModel
import com.example.qnsolutions.util.NavigationManager
import com.example.qnsolutions.R
import com.example.qnsolutions.activity.home.HomeActivity
import com.example.qnsolutions.activity.home.fragment.RicercaAttrazioneFragment
import com.example.qnsolutions.databinding.CittaCardBinding

class CittaAdapter(var fullList: MutableList<CittaModel>, private val activity: HomeActivity, val utenteId: String) : RecyclerView.Adapter<CittaAdapter.ViewHolder>()
{
    var filteredList = mutableListOf<CittaModel>()

    init
    {
        filteredList.addAll(fullList)
    }

    class ViewHolder(binding : CittaCardBinding) : RecyclerView.ViewHolder(binding.root)
    {
        val immagine = binding.immagineCitta
        val nome = binding.nomeCitta
        val nazione = binding.nazioneCitta
        val descrizione = binding.descrizioneCitta
        val mostraButton = binding.mostraCitta
        val pane = binding.moreOptionPane
        val visualizza = binding.visualizzaCitta
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = CittaCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val item = filteredList[position]

        holder.immagine.setImageBitmap(item.immagine)
        holder.nome.text = item.nome
        holder.nazione.text = item.nazione
        holder.descrizione.text = item.descrizione

        holder.mostraButton.setOnClickListener()
        {
            if(holder.pane.visibility == View.GONE)
            {
                holder.pane.visibility = View.VISIBLE
                holder.mostraButton.setImageResource(R.drawable.reduce_arrow_icon)
            }
            else
            {
                holder.pane.visibility = View.GONE
                holder.mostraButton.setImageResource(R.drawable.expand_arrow_icon)
            }
        }

        holder.visualizza.setOnClickListener()
        {
            //fragment città
            NavigationManager().scambiaFragment(activity, HomeActivity().frame, RicercaAttrazioneFragment(),"ricerca_attrazioni", HomeActivity().rootTag, false, bundleOf("cittaId" to item.id, "utenteId" to utenteId))
        }
    }

    fun setFilteredList(text: String, list: MutableList<CittaModel>)
    {
        when
        {
            list.isEmpty() && text.isNotEmpty() ->
            {
                filteredList.clear()
                notifyDataSetChanged()
            }

            list.isNotEmpty() ->
            {
                filteredList.clear()
                filteredList.addAll(list)
                notifyDataSetChanged()
            }

            text.isEmpty() ->
            {
                filteredList.clear()
                filteredList.addAll(fullList)
                notifyDataSetChanged()
            }
        }
    }

    fun addCitta(citta: CittaModel)
    {
        fullList.add(citta)
        filteredList.add(citta)
        notifyItemInserted(filteredList.size - 1)
    }
}