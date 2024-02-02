package com.example.qnsolutions.adapters;

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.qnsolutions.activity.home.fragment.NotificaFragment
import com.example.qnsolutions.databinding.NotificaCardBinding
import com.example.qnsolutions.models.NotificaModel;
import com.example.qnsolutions.util.DBMSQuery
import com.example.qnsolutions.util.QueryReturnCallback


class NotificaAdapter(val list: MutableList<NotificaModel>, val context: Context,val fragment: NotificaFragment):RecyclerView.Adapter<NotificaAdapter.ViewHolder>()
{
    class ViewHolder(binding: NotificaCardBinding) : RecyclerView.ViewHolder(binding.root)
    {
        val titolo = binding.titoloNotifica
        val descrizione = binding.descrizioneNotifica
        val vista = binding.vistaNotifiche
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificaAdapter.ViewHolder
    {
        val view = NotificaCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificaAdapter.ViewHolder, position: Int)
    {
        val item = list[position]
        holder.titolo.text = item.titolo
        holder.descrizione.text = item.descrizione
        holder.vista.setOnClickListener(){
            val builder = AlertDialog.Builder(context)
            //Setto il titolo del dialog con i bottoni si e no
            builder.setTitle("Eliminare la notifica?")

            builder.setPositiveButton("Si")
            { dialog, _ ->
                dialog.dismiss()

                DBMSQuery().cancellaNotifica(context, item, object : QueryReturnCallback
                {
                    override fun onReturnValue(response: Any, message: String)
                    {
                        list.removeAt(holder.adapterPosition)
                        notifyItemRemoved(holder.adapterPosition)
                        notifyItemRangeChanged(holder.adapterPosition, list.size)
                        if(list.isEmpty())
                        {
                            fragment.dismiss()
                        }
                    }

                    override fun onQueryFailed(fail: String)
                    {
                        Toast.makeText(context, fail, Toast.LENGTH_SHORT).show()
                    }

                    override fun onQueryError(error: String)
                    {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }

                })
            }
            builder.setNegativeButton("No")
            { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()

        }
    }

    override fun getItemCount(): Int
    {
        return list.size
    }
}
