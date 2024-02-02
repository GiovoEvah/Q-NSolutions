package com.example.qnsolutions.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.qnsolutions.models.CreditCardModel
import com.example.qnsolutions.R
import com.example.qnsolutions.databinding.CartaLayoutBinding
import com.example.qnsolutions.util.DBMSQuery
import com.example.qnsolutions.util.QueryReturnCallback

//i parametri in input saranno una lista presa dal db
class CreditCardAdapter(val list : MutableList<CreditCardModel>, val context: Context, val email: String) : RecyclerView.Adapter<CreditCardAdapter.ViewHolder>() {
    class ViewHolder(binding : CartaLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        val numero_carta = binding.numeroCarta
        val cvv = binding.cvv
        val data_scadenza = binding.dataScadenza
        val immagine = binding.circuitoImage
        val elimina = binding.eliminaCarta
        val saldo = binding.saldoButton
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pippo = list[position]
        holder.numero_carta.text = context.resources.getString(R.string.numero_carta) + pippo.numero_carta
        holder.cvv.text = context.resources.getString(R.string.cvv) + pippo.cvv
        holder.data_scadenza.text = context.resources.getString(R.string.data_di_scadenza) + pippo.data_scadenza
        if(pippo.numero_carta.toCharArray()[0] == '4'){
            //visa
            holder.immagine.setImageDrawable(context.resources.getDrawable(R.drawable.visa_icon))
        }
        else{
            //mastercard
            holder.immagine.setImageDrawable(context.resources.getDrawable(R.drawable.mastercard_icon))
        }

        holder.saldo.setOnClickListener()
        {
            val builder = AlertDialog.Builder(context)
            //Setto il titolo del dialog con i bottoni si e no
            builder.setTitle("Saldo: ${pippo.saldo}")
            builder.setPositiveButton("Ok")
            { dialog, _ ->
                // Azioni da eseguire se l'utente sceglie "Si"
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }

        holder.elimina.setOnClickListener(){
            //Apro un dialog per assicurarmi che l'utente sia sicuro di voler eliminare la carta
            val builder = AlertDialog.Builder(context)
            //Setto il titolo del dialog con i bottoni si e no
            builder.setTitle("Sicuro di voler eliminare la carta??")
            builder.setPositiveButton("Si")
            { dialog, _ ->
                // Azioni da eseguire se l'utente sceglie "Si"
                Toast.makeText(context, "Carta eliminata", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                list.removeAt(holder.adapterPosition)
                notifyItemRemoved(holder.adapterPosition)
                notifyItemRangeChanged(holder.adapterPosition, list.size)

                DBMSQuery().eliminaCarta(context, email, pippo.numero_carta, object : QueryReturnCallback
                {
                    override fun onReturnValue(response: Any, message: String)
                    {
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
                Toast.makeText(context, "Carta non eliminata", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = CartaLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun aggiungiCarta(carta: CreditCardModel)
    {
        list.add(carta)
        notifyItemInserted(list.size - 1)
    }
}