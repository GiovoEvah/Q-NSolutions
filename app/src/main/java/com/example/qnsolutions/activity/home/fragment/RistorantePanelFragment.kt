package com.example.qnsolutions.activity.home.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.qnsolutions.R
import com.example.qnsolutions.databinding.FragmentRistorantePanelBinding
import com.example.qnsolutions.models.RistoranteModel
import com.example.qnsolutions.util.DBMSQuery
import com.example.qnsolutions.util.QueryReturnCallback
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Calendar

class RistorantePanelFragment : Fragment()
{
    lateinit var binding: FragmentRistorantePanelBinding
    lateinit var calendar: Calendar
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentRistorantePanelBinding.inflate(inflater)
        calendar = Calendar.getInstance()
        val ristoranteId = requireArguments().getInt("ristoranteId")
        val utenteId = requireArguments().getString("utenteId")!!
        binding.dataPrenotazioneCalendar.minDate = calendar.time.time
        DBMSQuery().getRistorante(requireContext(), ristoranteId, object : QueryReturnCallback
        {
            override fun onReturnValue(response: Any, message: String)
            {
                val ristorante = response as RistoranteModel
                binding.orarioApertura.text = "${resources.getText(R.string.orario_apertura)} ${ristorante.orario_apertura}"
                binding.orarioChiusura.text = "${resources.getText(R.string.orario_chiusira)} ${ristorante.orario_chiusura}"
                binding.numeroCoperti.text = "${resources.getText(R.string.numero_coperti_disponibili)} ${ristorante.numero_coperti}"
                binding.tipoCucina.text = "${resources.getText(R.string.cucina)} ${ristorante.tipo_cucina}"


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

                binding.confermaPrenotazioneRistorante.setOnClickListener()
                {
                    //prendo i dati dai campi
                    var errorMessage = "Errore:\n"
                    val oraPrenotazione = "${binding.oraSpinner.selectedItem}:${binding.minutoSpinner.selectedItem}:00"

                    val dataOra = "${dataPrenotazione}T${oraPrenotazione}"
                    val dataOraApertura = "${LocalDate.now(ZoneId.of("Europe/Rome"))}T${ristorante.orario_apertura}"
                    val dataOraChiusura = "${LocalDate.now(ZoneId.of("Europe/Rome"))}T${ristorante.orario_chiusura}"

                    val oraOk = if (LocalDate.parse(dataPrenotazione).isEqual(LocalDate.now(ZoneId.of("Europe/Rome"))))
                    {
                        if (LocalTime.parse(oraPrenotazione).isAfter(LocalTime.now(ZoneId.of("Europe/Rome"))))
                        {
                            if (LocalTime.parse(oraPrenotazione).isAfter(LocalTime.parse(ristorante.orario_apertura)) && LocalTime.parse(oraPrenotazione).isBefore(LocalTime.parse(ristorante.orario_chiusura)))
                            {
                                true
                            }
                            else
                            {
                                errorMessage += "L'orario selezionato è oltre i limiti di apertura o chiusura\n"
                                false
                            }
                        }
                        else
                        {
                            errorMessage += "L'orario selezionato è già passato\n"
                            false
                        }
                    }
                    else
                    {
                        if (LocalTime.parse(oraPrenotazione).isAfter(LocalTime.parse(ristorante.orario_apertura)) && LocalTime.parse(oraPrenotazione).isBefore(LocalTime.parse(ristorante.orario_chiusura)))
                        {
                            true
                        }
                        else
                        {
                            errorMessage += "L'orario è oltre i limiti di apertura o chiusura\n"
                            false
                        }
                    }

                    val numero_prenotati_string = binding.numeroPrenotatiAttrazione.text.toString()
                    var numero_prenotati = 0

                    if (numero_prenotati_string.isNotEmpty())
                    {
                        if (numero_prenotati_string.toInt() != 0)
                        {
                            numero_prenotati = numero_prenotati_string.toInt()
                        }
                    }

                    if(numero_prenotati != 0 && numero_prenotati <= ristorante.numero_coperti && oraOk)
                    {
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("Confermare la prenotazione?")
                        //ref_attrazione, ref_utente, ref_transazione, numero prenotati
                        builder.setPositiveButton("Conferma")
                        { dialog, _ ->

                            dialog.dismiss()
                            DBMSQuery().eseguiTransazione(requireContext(), utenteId, "****************", "0.00".toBigDecimal(), object : QueryReturnCallback
                            {
                                override fun onReturnValue(response: Any, message: String)
                                {
                                    val transazioneId = response as Int

                                    DBMSQuery().inserisciPrenotazione(requireContext(), utenteId, ristoranteId, transazioneId, dataOra, numero_prenotati, object : QueryReturnCallback
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

                            binding.prenotaRistorante.visibility = View.GONE
                            binding.prenotaRistoranteButton.text = resources.getText(R.string.prenota_button)
                        }

                        builder.setNegativeButton("Annulla")
                        { dialog, _ ->
                            dialog.dismiss()
                        }

                        val dialog = builder.create()
                        dialog.show()
                    }
                    else
                    {
                        Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
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

        binding.mostraMenuButton.setOnClickListener()
        {
            //mostra menu
            RistoranteMenuFragment(ristoranteId).show(requireActivity().supportFragmentManager, "menu")
        }

        binding.prenotaRistoranteButton.setOnClickListener()
        {
            if(binding.prenotaRistorante.visibility == View.GONE)
            {
                binding.prenotaRistorante.visibility = View.VISIBLE
                binding.prenotaRistoranteButton.text = resources.getText(R.string.annulla_button)
            }
            else
            {
                //annullo ogni campo di prenotazione
                binding.oraSpinner.setSelection(0)
                binding.minutoSpinner.setSelection(0)
                binding.prenotaRistorante.visibility = View.GONE
                binding.prenotaRistoranteButton.text = resources.getText(R.string.prenota_button)
            }
        }

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

        sessaggesimiSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        oreSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.minutoSpinner.adapter = sessaggesimiSpinnerAdapter
        binding.oraSpinner.adapter = oreSpinnerAdapter

        return binding.root
    }
}