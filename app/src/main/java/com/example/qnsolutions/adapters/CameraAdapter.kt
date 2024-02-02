package com.example.qnsolutions.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.scale
import androidx.recyclerview.widget.RecyclerView
import com.example.qnsolutions.databinding.CameraCardBinding
import com.example.qnsolutions.models.CameraModel

class CameraAdapter(var list: MutableList<CameraModel>, val context: Context, val contatore: IntArray) : RecyclerView.Adapter<CameraAdapter.ViewHolder>()
{
    class ViewHolder(binding: CameraCardBinding) : RecyclerView.ViewHolder(binding.root)
    {
        val capienza = binding.capienza
        var immagine = binding.immagineCamera
        val aggiungi = binding.aggiungiButton
        val rimuovi = binding.rimuoviButton
        val counter = binding.counterText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = CameraCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val item = list[position]

        when(item.capienza)
        {
            1 -> holder.capienza.text = "Singola"
            2 -> holder.capienza.text = "Doppia"
            3 -> holder.capienza.text = "Tripla"
            4 -> holder.capienza.text = "Quadrupla"
            else -> holder.capienza.text = "Errore"
        }
        holder.immagine.setImageBitmap(item.immagine?.scale(400, 400))

        holder.counter.text = "0 su ${item.numero_camere}"

        holder.aggiungi.setOnClickListener()
        {
            var numero = holder.counter.text[0].digitToInt()

            if (item.numero_camere - numero > 0)
            {
                numero++
                holder.counter.text = "${numero} su ${item.numero_camere}"
                contatore[item.capienza - 1]++
            }
        }

        holder.rimuovi.setOnClickListener()
        {
            var numero = holder.counter.text[0].digitToInt()
            if (numero > 0)
            {
                numero--
                holder.counter.text = "${numero} su ${item.numero_camere}"
                contatore[item.capienza - 1]--
            }
        }
    }

    override fun getItemCount(): Int
    {
        return list.size
    }
}