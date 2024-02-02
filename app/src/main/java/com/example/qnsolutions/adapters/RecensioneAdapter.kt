package com.example.qnsolutions.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qnsolutions.databinding.RecensioneCardBinding
import com.example.qnsolutions.models.RecensioneModel

class RecensioneAdapter(val list: MutableList<RecensioneModel>) : RecyclerView.Adapter<RecensioneAdapter.ViewHolder>()
{
    class ViewHolder(binding: RecensioneCardBinding) : RecyclerView.ViewHolder(binding.root)
    {
        val autore = binding.autoreRecensione
        val rating = binding.ratingRecensione
        val titolo = binding.titoloNuovaRecensione
        val descrizione = binding.descrizioneRecensione
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = RecensioneCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val item = list[position]

        holder.autore.text = item.nome
        holder.descrizione.text = item.descrizione
        holder.titolo.text = item.titolo
        holder.rating.rating = item.rating.toFloat()
    }

    override fun getItemCount(): Int
    {
        return list.size
    }
}