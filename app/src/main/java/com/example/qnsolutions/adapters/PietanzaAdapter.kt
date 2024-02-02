package com.example.qnsolutions.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.scale
import androidx.recyclerview.widget.RecyclerView
import com.example.qnsolutions.databinding.PietanzaCardBinding
import com.example.qnsolutions.models.PietanzaModel

class PietanzaAdapter(var list: MutableList<PietanzaModel>) : RecyclerView.Adapter<PietanzaAdapter.ViewHolder>()
{
    class ViewHolder(binding: PietanzaCardBinding)  : RecyclerView.ViewHolder(binding.root)
    {
        val nome = binding.nomePetanza
        val ingredienti = binding.listaIngredienti
        val prezzo = binding.prezzoPietanza
        var immagine = binding.immaginePietanza
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = PietanzaCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val item = list[position]

        holder.nome.text = item.nome
        holder.ingredienti.text = item.ingredienti
        holder.prezzo.text = "${item.prezzo} €"
        holder.immagine.setImageBitmap(item.immagine?.scale(200, 200))
    }
}