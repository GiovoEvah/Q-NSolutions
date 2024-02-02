package com.example.qnsolutions.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.scale
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import com.example.qnsolutions.R
import com.example.qnsolutions.activity.home.HomeActivity
import com.example.qnsolutions.activity.home.fragment.VisualizzaAttrazioneFragment
import com.example.qnsolutions.databinding.AttrazioneCardBinding
import com.example.qnsolutions.models.AttrazioneTuristicaModel
import com.example.qnsolutions.util.DBMSQuery
import com.example.qnsolutions.util.NavigationManager
import com.example.qnsolutions.util.QueryReturnCallback

class AttrazioneAdapter(var fullList : MutableList<AttrazioneTuristicaModel>, val activity: HomeActivity, val utenteId: String, var preferiti : MutableList<Int>) : RecyclerView.Adapter<AttrazioneAdapter.ViewHolder>()
{
    private var filteredList = mutableListOf<AttrazioneTuristicaModel>()

    init
    {
        filteredList.addAll(fullList)
    }

    class ViewHolder(binding : AttrazioneCardBinding) : RecyclerView.ViewHolder(binding.root)
    {
        val immagine = binding.attrazioneCardImage
        val nome = binding.attrazioneCardName
        val visualizza = binding.attrazioneVisualizza
        val espandi = binding.attrazioneCardExpand
        val preferito = binding.attrazioneCardPreferiti
        val tipo = binding.attrazioneCardTipo
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val item = filteredList[position]
        item.preferito = preferiti.contains(item.id)
        holder.immagine.setImageBitmap(item.immagine?.scale(400, 200))
        holder.nome.text = item.nome
        holder.tipo.text = item.tipo
        Log.d("Fatto", "${item.preferito}")
        if(item.preferito)
        {
            //setta la stellina colorata all'apertura
            holder.preferito.setImageDrawable(activity.resources.getDrawable(R.drawable.filled_star, null))
        }
        else
        {
            //idem qui
            holder.preferito.setImageDrawable(activity.resources.getDrawable(R.drawable.empty_star, null))
        }
        holder.visualizza.setOnClickListener()
        {
            //fragmentDialog con informazioni del'attrazione
            NavigationManager().scambiaFragment(activity, activity.frame, VisualizzaAttrazioneFragment(), "attrazione", activity.rootTag, false, bundleOf("attrazioneId" to item.id, "attrazioneTipo" to item.tipo, "utenteId" to utenteId))
        }
        holder.preferito.setOnClickListener()
        {
            //inserisce nei preferiti in locale
            //setta stellina colorata
            if(item.preferito)
            {
                //setta stellina vuota

                //rimuovi dai preferiti
                preferiti.remove(item.id)
                //query per togliere dai preferiti
                DBMSQuery().rimuoviPreferiti(activity, utenteId, item.id, object : QueryReturnCallback
                {
                    override fun onReturnValue(response: Any, message: String)
                    {
                        item.preferito = false
                        holder.preferito.setImageDrawable(activity.resources.getDrawable(R.drawable.empty_star))
                    }

                    override fun onQueryFailed(fail: String) {}
                    override fun onQueryError(error: String) {}
                })
            }
            else
            {
                //setta stellina colorata

                //aggiungi ai preferiti
                preferiti.add(item.id)
                //query per aggiungere ai preferiti
                DBMSQuery().aggiungiPreferiti(activity, utenteId, item.id, object : QueryReturnCallback{
                    override fun onReturnValue(response: Any, message: String)
                    {
                        if(response as Boolean)
                        {
                            item.preferito = true
                            holder.preferito.setImageDrawable(activity.resources.getDrawable(R.drawable.filled_star))
                        }
                        else{
                            //buonasera prof
                        }
                    }

                    override fun onQueryFailed(fail: String) {}
                    override fun onQueryError(error: String) {}

                })
            }
        }

        holder.espandi.setOnClickListener()
        {
            //mostra visualizza
            if(holder.visualizza.visibility == View.GONE)
            {
                holder.visualizza.visibility = View.VISIBLE
                holder.espandi.setImageResource(R.drawable.reduce_arrow_icon)
            }
            else
            {
                holder.visualizza.visibility = View.GONE
                holder.espandi.setImageResource(R.drawable.expand_arrow_icon)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = AttrazioneCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return filteredList.size
    }

    fun addAttrazione(attrazione: AttrazioneTuristicaModel)
    {
        fullList.add(attrazione)
        filteredList.add(attrazione)
        notifyItemInserted(filteredList.size - 1)
    }

    fun setFilteredList(text: String, list: MutableList<AttrazioneTuristicaModel>)
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

    fun svuota()
    {
        fullList.clear()
        filteredList.clear()
        notifyDataSetChanged()
    }
}