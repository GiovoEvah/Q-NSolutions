package com.example.qnsolutions.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qnsolutions.R
import com.example.qnsolutions.databinding.TransazioniCardBinding
import com.example.qnsolutions.models.TransazioniModel

class TransazioniAdapter(private val list: List<TransazioniModel>, val context : Context) : RecyclerView.Adapter<TransazioniAdapter.ViewHolder>()
{
    class ViewHolder(binding: TransazioniCardBinding) : RecyclerView.ViewHolder(binding.root)
    {
        val identificativo = binding.identificativo
        val carta = binding.carta
        val data_ora = binding.data
        val importo = binding.importo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = TransazioniCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val item = list[position]
        holder.carta.text = holder.carta.context.getString(R.string.numero_carta) + "   " + item.carta
        holder.data_ora.text = context.getString(R.string.data_e_ora_transazione) + "   " + item.data_ora_pagamento.replace('T', ' ')
        holder.importo.text = holder.importo.context.getString(R.string.importo_transazione) + "   " + item.importo.toString()
        holder.identificativo.text = holder.identificativo.context.getString(R.string.id_transazione) + "   " + item.id//mettere id da tabella
    }

    override fun getItemCount(): Int
    {
        return list.size
    }

}