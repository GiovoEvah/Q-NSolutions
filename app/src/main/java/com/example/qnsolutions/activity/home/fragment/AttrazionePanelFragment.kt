package com.example.qnsolutions.activity.home.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.qnsolutions.R
import com.example.qnsolutions.databinding.FragmentAttrazionePanelBinding
import com.example.qnsolutions.models.CreditCardModel
import com.example.qnsolutions.util.DBMSQuery
import com.example.qnsolutions.util.QueryReturnCallback
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

class AttrazionePanelFragment : Fragment()
{
    lateinit var binding: FragmentAttrazionePanelBinding
    lateinit var calendar: Calendar
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentAttrazionePanelBinding.inflate(inflater)
        calendar = Calendar.getInstance()
        binding.dataPrenotazioneCalendar.minDate = calendar.time.time

        val utenteId = requireArguments().getString("utenteId")!!
        val prezzoBiglietto = requireArguments().getString("prezzoBiglietto")!!
        val attrazioneId = requireArguments().getInt("attrazioneId")!!

        val spinnerMesiAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(), R.array.mesiSpiner, android.R.layout.simple_spinner_item)

        var anniArray = mutableListOf<CharSequence>()

        for(i in 0 until 5)
        {
            anniArray.add(Calendar.getInstance().get(Calendar.YEAR).plus(i).toString())
        }

        val spinnerAnniAdapter: ArrayAdapter<CharSequence> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, anniArray)

        spinnerAnniAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMesiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.mesiSpinner.adapter = spinnerMesiAdapter
        binding.anniSpinner.adapter = spinnerAnniAdapter


        DBMSQuery().getCarte(requireContext(), utenteId, object : QueryReturnCallback
        {
            override fun onReturnValue(response: Any, message: String)
            {
                var carteArray = mutableListOf<String>()
                val carte = response as MutableList<CreditCardModel>
                carteArray.add("Selezionare un metodo di pagamento...")

                for (carta in carte)
                {
                    carteArray.add("**** **** **** ${carta.numero_carta.subSequence(12, 16)} ${carta.data_scadenza}")
                }

                carteArray.add("Utilizza un altro metodo...")

                val pagamentoSpinnerAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, carteArray)
                pagamentoSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.metodoDiPagamentoPrenotazione.adapter = pagamentoSpinnerAdapter

                binding.metodoDiPagamentoPrenotazione.onItemSelectedListener = object : OnItemSelectedListener
                {
                    override fun onItemSelected(adapter: AdapterView<*>?, view: View?, position: Int, id: Long)
                    {
                        if(position == (carteArray.size - 1))
                        {
                            binding.layoutCarta.visibility = View.VISIBLE
                            binding.nuovaCarta.editText!!.setText("")
                            binding.nuovoCvv.editText!!.setText("")
                            binding.mesiSpinner.setSelection(0)
                            binding.anniSpinner.setSelection(0)
                        }
                        else if (position == 0)
                        {
                            binding.layoutCarta.visibility = View.GONE
                            binding.nuovaCarta.editText!!.setText("")
                            binding.nuovoCvv.editText!!.setText("")
                            binding.mesiSpinner.setSelection(0)
                            binding.anniSpinner.setSelection(0)
                        }
                        else
                        {
                            binding.layoutCarta.visibility = View.GONE
                            binding.nuovaCarta.editText!!.setText(carte[position - 1].numero_carta)
                            binding.nuovoCvv.editText!!.setText(carte[position - 1].cvv)
                            val meseScadenza = carte[position - 1].data_scadenza.subSequence(0, 1)
                            val annoScadenza = carte[position - 1].data_scadenza.subSequence(3, 6)

                            when(meseScadenza)
                            {
                                "01" -> binding.mesiSpinner.setSelection(0)

                                "02" -> binding.mesiSpinner.setSelection(1)

                                "03" -> binding.mesiSpinner.setSelection(2)

                                "04" -> binding.mesiSpinner.setSelection(3)

                                "05" -> binding.mesiSpinner.setSelection(4)

                                "06" -> binding.mesiSpinner.setSelection(5)

                                "07" -> binding.mesiSpinner.setSelection(6)

                                "08" -> binding.mesiSpinner.setSelection(7)

                                "09" -> binding.mesiSpinner.setSelection(8)

                                "10" -> binding.mesiSpinner.setSelection(9)

                                "11" -> binding.mesiSpinner.setSelection(10)

                                "12" -> binding.mesiSpinner.setSelection(11)
                            }

                            binding.anniSpinner.setSelection(anniArray.indexOf(annoScadenza))
                        }
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }
            }

            override fun onQueryFailed(fail: String)
            {
                Toast.makeText(requireContext(), fail, Toast.LENGTH_SHORT).show()
            }

            override fun onQueryError(error: String)
            {

            }

        })

        var sessaggesimiArray = mutableListOf<String>()
        var oreArray = mutableListOf<String>()

        for (i in 0 until 60)
        {
            if (i.toString().length < 2)
            {
                sessaggesimiArray.add("0${i}")
            }
            else
            {
                sessaggesimiArray.add(i.toString())
            }
        }

        for(i in 0 until 24)
        {
            if (i.toString().length < 2)
            {
                oreArray.add("0${i}")
            }
            else
            {
                oreArray.add(i.toString())
            }
        }

        val sessaggesimiSpinnerAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sessaggesimiArray)
        val oreSpinnerAdapter: ArrayAdapter<String> = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, oreArray)
        //val

        sessaggesimiSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        oreSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.minutoSpinner.adapter = sessaggesimiSpinnerAdapter
        binding.oraSpinner.adapter = oreSpinnerAdapter


        if(prezzoBiglietto == "0.00")
        {
            binding.costoBigliettoAttrazione.text = "${resources.getText(R.string.costo_del_biglietto_text)} Gratis €"
            binding.metodoDiPagamentoPrenotazione.visibility = View.GONE
        }
        else
        {
            binding.costoBigliettoAttrazione.text = "${resources.getText(R.string.costo_del_biglietto_text)} ${prezzoBiglietto} €"
        }

        binding.prenotaButton.setOnClickListener()
        {
            if(binding.prenotaAttrazione.visibility == View.GONE)
            {
                binding.prenotaAttrazione.visibility = View.VISIBLE
                binding.prenotaButton.text = resources.getText(R.string.annulla_button)
            }
            else
            {
                //annullo ogni campo di prenotazione
                binding.oraSpinner.setSelection(0)
                binding.minutoSpinner.setSelection(0)
                binding.metodoDiPagamentoPrenotazione.setSelection(0)
                binding.prenotaAttrazione.visibility = View.GONE
                binding.prenotaButton.text = resources.getText(R.string.prenota_button)
            }
        }



        var dataPrenotazione = LocalDate.now(ZoneId.of("Europe/Rome")).toString()
        Log.d("data", dataPrenotazione)
        binding.dataPrenotazioneCalendar.minDate = calendar.time.time
        binding.dataPrenotazioneCalendar.setOnDateChangeListener()
        { _, year, month, day ->
            dataPrenotazione = if (month + 1 < 10)
            {
                "${year}-0${month + 1}-${day}"
            }
            else
            {
                "${year}-${month + 1}-${day}"
            }
            Log.d("data", dataPrenotazione)
        }

        binding.confermaPrenotazioneAttrazione.setOnClickListener()
        {
            //prendo i dati dai campi
            var cvvOk = true
            var cartaOk = true
            var oraOk = true

            val dataOra = "${dataPrenotazione}T${binding.oraSpinner.selectedItem}:${binding.minutoSpinner.selectedItem}:00"
            if (LocalDateTime.parse(dataOra).isBefore(LocalDateTime.now(ZoneId.of("Europe/Rome"))))
            {
                oraOk = false
            }

            val numero_prenotati_string = binding.numeroPrenotatiAttrazione.text.toString()
            var numero_prenotati = 0
            var numero_carta = "****************"

            if (prezzoBiglietto != "0.00")
            {
                if (binding.nuovaCarta.editText!!.text.toString().length == 16 && (binding.nuovaCarta.editText!!.text.toString().toCharArray()[0] == '4' || binding.nuovaCarta.editText!!.text.toString().toCharArray()[0] == '5'))
                {
                    numero_carta = binding.nuovaCarta.editText!!.text.toString()
                }
                else
                {
                    cartaOk = false
                }

                if (binding.nuovoCvv.editText!!.text.length != 3)
                {
                    cvvOk = false
                }
            }

            if (numero_prenotati_string.isNotEmpty())
            {

                if (numero_prenotati_string.toInt() != 0)
                {
                    numero_prenotati = numero_prenotati_string.toInt()
                }
            }

            if(numero_prenotati != 0 && cartaOk && cvvOk && oraOk)
            {
                val builder = AlertDialog.Builder(context)
                //Setto il titolo del dialog con i bottoni si e no
                builder.setTitle("Confermare il pagamento di ${prezzoBiglietto.toBigDecimal() * numero_prenotati.toBigDecimal()} €")
                //ref_attrazione, ref_utente, ref_transazione, numero prenotati
                builder.setPositiveButton("Conferma")
                { dialog, _ ->

                    dialog.dismiss()
                    DBMSQuery().eseguiTransazione(requireContext(), utenteId, numero_carta, prezzoBiglietto.toBigDecimal() * numero_prenotati.toBigDecimal(), object : QueryReturnCallback {
                            override fun onReturnValue(response: Any, message: String) {
                                val transazioneId = response as Int

                                DBMSQuery().inserisciPrenotazione(requireContext(), utenteId, attrazioneId, transazioneId, dataOra, numero_prenotati, object : QueryReturnCallback
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

                            override fun onQueryFailed(fail: String)
                            {
                                Toast.makeText(requireContext(), fail, Toast.LENGTH_SHORT).show()
                            }

                            override fun onQueryError(error: String)
                            {
                                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                            }
                        })
                    binding.prenotaAttrazione.visibility = View.GONE
                    binding.prenotaButton.text = resources.getText(R.string.prenota_button)
                }

                builder.setNegativeButton("Annulla")
                { dialog, _ ->
                    // Annulla
                    dialog.dismiss()
                }

                val dialog = builder.create()
                dialog.show()
            }
            else
            {
                Toast.makeText(requireContext(), "Campi non validi", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}