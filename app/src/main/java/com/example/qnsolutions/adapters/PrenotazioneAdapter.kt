package com.example.qnsolutions.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.qnsolutions.R
import com.example.qnsolutions.databinding.PrenotazioniCardBinding
import com.example.qnsolutions.models.PrenotazioneModel
import com.example.qnsolutions.util.DBMSQuery
import com.example.qnsolutions.util.QueryReturnCallback
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId

class PrenotazioneAdapter(var list: MutableList<PrenotazioneModel>, val context: Context, val inCorso: Boolean) : RecyclerView.Adapter<PrenotazioneAdapter.ViewHolder>()
{
    class ViewHolder(binding: PrenotazioniCardBinding) : RecyclerView.ViewHolder(binding.root)
    {
        val luogo = binding.luogoPrenotazioneCard
        val transazione = binding.idTransazionePrenotazioneCard
        val dataOra = binding.dataEOraPrenotazioneCard
        val prenotati = binding.numeroPrenotatiPrenotazioneCard
        val card = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = PrenotazioniCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int
    {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val item = list[position]

        holder.luogo.text = "${context.resources.getText(R.string.luogo_prenotazione_card)} ${item.nome}"
        holder.transazione.text = "${context.resources.getText(R.string.id_transazione_prenotazione_card)} ${item.ref_transazione}"
        holder.dataOra.text = "${context.resources.getText(R.string.data_e_ora_transazione)} ${item.data_ora_prenotazione.replace('T', ' ')}"
        holder.prenotati.text = "${context.resources.getText(R.string.numero_prenotati_prenotazione_card)} ${item.numero_prenotati}"

        if(inCorso)
        {
            holder.card.background = context.resources.getDrawable(R.drawable.prenotazione_in_corso, null)

            holder.card.setOnClickListener()
            {
                val builder = AlertDialog.Builder(context)
                //Setto il titolo del dialog con i bottoni si e no
                builder.setTitle("Annullare la prenotazione?")
                var rimborso = item.importo
                if(item.importo != "0.00".toBigDecimal())
                {
                    val formatter = DecimalFormat("0.00")
                    val dataPrenotazione = LocalDateTime.parse(item.data_ora_prenotazione)
                    val giorni = Period.between(LocalDate.now(ZoneId.of("Europe/Rome")), dataPrenotazione.toLocalDate()).days
                    when
                    {
                        giorni < 5 -> rimborso *= "0.1".toBigDecimal()

                        giorni in 5 until 10 -> rimborso *= "0.3".toBigDecimal()

                        giorni in 10 until 15 -> rimborso *= "0.5".toBigDecimal()

                        giorni in 15 until 20 -> rimborso *= "0.7".toBigDecimal()

                        giorni in 20 until 30 -> rimborso *= "0.9".toBigDecimal()
                    }

                    builder.setMessage("Verranno rimborsati ${formatter.format(rimborso)} € su ${formatter.format(item.importo)} €")
                }

                builder.setPositiveButton("Si")
                { dialog, _ ->
                    // Azioni da eseguire se l'utente sceglie "Si"
                    dialog.dismiss()


                    DBMSQuery().annullaPrenotazione(context, item, rimborso, object : QueryReturnCallback
                    {
                        override fun onReturnValue(response: Any, message: String)
                        {

                            list.removeAt(holder.adapterPosition)
                            notifyItemRemoved(holder.adapterPosition)
                            notifyItemRangeChanged(holder.adapterPosition, list.size)
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
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
                    // Azioni da eseguire se l'utente sceglie "No"
                    dialog.dismiss()
                }
                val dialog = builder.create()
                dialog.show()
            }
        }
        else
        {
            holder.card.background = context.resources.getDrawable(R.drawable.prenotazione_scaduta, null)
        }
    }
}