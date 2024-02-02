package com.example.qnsolutions.activity.home.fragment

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.example.qnsolutions.R
import com.example.qnsolutions.common.DatePickerFragment
import com.example.qnsolutions.databinding.FragmentHotelPanelBinding
import com.example.qnsolutions.models.CreditCardModel
import com.example.qnsolutions.util.DBMSQuery
import com.example.qnsolutions.util.QueryReturnCallback
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.Period
import java.time.ZoneId
import java.util.Calendar

class HotelPanelFragment : Fragment()
{
    lateinit var binding: FragmentHotelPanelBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = FragmentHotelPanelBinding.inflate(inflater)

        val utenteId = requireArguments().getString("utenteId")!!
        val costoCamera = requireArguments().getString("costoCamera")!!
        val hotelId = requireArguments().getInt("hotelId")

        binding.numeroSingole.text = "${resources.getString(R.string.numero_singole)} 0"
        binding.numeroDoppie.text = "${resources.getString(R.string.numero_doppie)} 0"
        binding.numeroTriple.text = "${resources.getString(R.string.numero_triple)} 0"
        binding.numeroQuadruple.text = "${resources.getString(R.string.numero_quadruple)} 0"

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

        val mezzoSpinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.mezziSpinner,
            android.R.layout.simple_spinner_item)
        mezzoSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.mezzoSpinner.adapter = mezzoSpinnerAdapter

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

                binding.metodoDiPagamentoPrenotazione.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener
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

        binding.prenotaHotelButton.setOnClickListener()
        {
            if(binding.prenotaCamere.visibility == View.GONE)
            {
                CamereHotelFragment(hotelId).show(requireActivity().supportFragmentManager, "camere")
            }
            else
            {
                binding.prenotaCamere.visibility = View.GONE
                binding.prenotaHotelButton.text = resources.getText(R.string.prenota_button)
                binding.metodoDiPagamentoPrenotazione.setSelection(0)
            }
        }

        setFragmentResultListener("bundleCamere")
        { _ , bundle ->
            val result = bundle.getIntArray("numeroCamere")
            if (result != null)
            {
                binding.numeroSingole.text = "${resources.getString(R.string.numero_singole)} ${result[0]}"
                binding.numeroDoppie.text = "${resources.getString(R.string.numero_doppie)} ${result[1]}"
                binding.numeroTriple.text = "${resources.getString(R.string.numero_triple)} ${result[2]}"
                binding.numeroQuadruple.text = "${resources.getString(R.string.numero_quadruple)} ${result[3]}"
                binding.prenotaCamere.visibility = View.VISIBLE
                binding.prenotaHotelButton.text = resources.getText(R.string.annulla_button)
            }
        }

        binding.dataInizioButton.setOnClickListener()
        {
            setFragmentResultListener("bundleDataInizio")
            { _ , bundle ->
                val result = bundle.getString("dataInizio")
                if (result != null)
                {
                    Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show()
                    binding.dataInizio.text = "${resources.getText(R.string.data_inizio_text)} ${result}"
                }
            }

            DatePickerFragment("dataInizio", "bundleDataInizio", Calendar.getInstance().time.time, null).show(requireActivity().supportFragmentManager, "dataInizio")
        }

        binding.dataFineButton.setOnClickListener()
        {
            setFragmentResultListener("bundleDataFine")
            { _ , bundle ->
                val result = bundle.getString("dataFine")
                if (result != null)
                {
                    binding.dataFine.text = "${resources.getText(R.string.data_fine_text)} ${result}"
                }
            }

            DatePickerFragment("dataFine", "bundleDataFine", Calendar.getInstance().time.time, null).show(requireActivity().supportFragmentManager, "dataInizio")
        }

        binding.oraPartenzaButton.setOnClickListener()
        {
            val ora = LocalTime.now(ZoneId.of("Europe/Rome"))
            TimePickerDialog(requireContext(), R.style.Theme_QNSolutions,
            { _, hours, minutes ->
                var stringTime = ""
                stringTime += if (hours < 10)
                {
                    "0${hours}"
                } else
                {
                    "${hours}"
                }

                stringTime += if (minutes < 10)
                {
                    ":0${minutes}:00"
                } else
                {
                    ":${minutes}:00"
                }
                binding.oraPartenza.text = "${resources.getText(R.string.ora_partenza)} ${stringTime}"

            }, ora.hour, ora.minute, true).show()
        }

        binding.oraRientroButton.setOnClickListener()
        {
            val ora = LocalTime.now(ZoneId.of("Europe/Rome"))
            TimePickerDialog(requireContext(), R.style.Theme_QNSolutions,
            { _, hours, minutes ->
                var stringTime = ""
                stringTime += if (hours < 10)
                {
                    "0${hours}"
                } else
                {
                    "${hours}"
                }

                stringTime += if (minutes < 10)
                {
                    ":0${minutes}:00"
                } else
                {
                    ":${minutes}:00"
                }

                binding.oraRientro.text = "${resources.getText(R.string.ora_rientro)} ${stringTime}"

            }, ora.hour, ora.minute, true).show()
        }

        binding.confermaPrenotazioneHotel.setOnClickListener()
        {

            var dataInizio = ""
            var dataFine = ""
            val dataOk = if (binding.dataInizio.text.length == 12 || binding.dataFine.text.length == 10)
            {
                false
            }
            else
            {
                dataInizio = binding.dataInizio.text.subSequence(13, 23).toString()
                dataFine = binding.dataFine.text.subSequence(11, 21).toString()
                //controlla le date
                LocalDate.parse(dataInizio).isBefore(LocalDate.parse(dataFine))
            }

            var oraPartenza = ""
            var oraRientro = ""

            val oraOk = if (binding.oraPartenza.text.length == 13 || binding.oraRientro.text.length == 12)
            {
                false
            }
            else
            {
                oraPartenza = binding.oraPartenza.text.subSequence(14, 22).toString()
                oraRientro = binding.oraRientro.text.subSequence(13, 20).toString()

                if (dataOk)
                {
                    if (LocalDate.parse(dataInizio).isEqual(LocalDate.now(ZoneId.of("Europe/Rome"))))//controllo se la partenza è oggi
                    {
                        !LocalTime.parse(oraPartenza).isBefore(LocalTime.now(ZoneId.of("Europe/Rome")))//controllo che l'orario di partenza non sia prima di ora
                    }
                    else
                    {
                        true
                    }
                }
                else
                {
                    false
                }
            }

            val numeroCarta = if (binding.nuovaCarta.editText!!.text.toString().length == 16 && (binding.nuovaCarta.editText!!.text.toString().toCharArray()[0] == '4' || binding.nuovaCarta.editText!!.text.toString().toCharArray()[0] == '5'))
            {
                binding.nuovaCarta.editText!!.text.toString()
            }
            else
            {
                "****************"
            }

            val cvv = if(binding.nuovoCvv.editText!!.text.length == 3)
            {
                binding.nuovoCvv.editText!!.text.toString()
            }
            else
            {
                "***"
            }

            val numeroPrenotati: Int = if(binding.numeroPrenotati.text.isNotEmpty() || binding.numeroPrenotati.text.isNotBlank())
            {
                binding.numeroPrenotati.text.toString().toInt()
            }
            else
            {
                0
            }


            if (dataOk && oraOk && numeroCarta != "****************" && cvv != "***" && numeroPrenotati != 0)
            {
                //prenota
                val mezzo = binding.mezzoSpinner.selectedItem.toString()
                val durata = Period.between(LocalDate.parse(dataInizio), LocalDate.parse(dataFine)).days + 1
                val dataOra = "${dataInizio}T${oraPartenza}"

                val numeroCamere = intArrayOf(
                    binding.numeroSingole.text[binding.numeroSingole.text.length - 1].digitToInt(),
                    binding.numeroDoppie.text[binding.numeroDoppie.text.length - 1].digitToInt(),
                    binding.numeroTriple.text[binding.numeroTriple.text.length - 1].digitToInt(),
                    binding.numeroQuadruple.text[binding.numeroQuadruple.text.length - 1].digitToInt()
                )

                val costo = calcolaPrezzo(costoCamera.toBigDecimal(), numeroCamere, numeroPrenotati, mezzo, durata)

                val builder = AlertDialog.Builder(context)
                //Setto il titolo del dialog con i bottoni si e no
                builder.setTitle("Confermare il pagamento di ${costo} €?")
                //ref_attrazione, ref_utente, ref_transazione, numero prenotati
                builder.setPositiveButton("Conferma")
                { dialog, _ ->

                    dialog.dismiss()
                    DBMSQuery().eseguiTransazione(requireContext(), utenteId, numeroCarta, costo, object : QueryReturnCallback {
                        override fun onReturnValue(response: Any, message: String) {
                            val transazioneId = response as Int

                            DBMSQuery().inserisciPrenotazione(requireContext(), utenteId, hotelId, transazioneId, dataOra, numeroPrenotati, object : QueryReturnCallback
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
                    binding.prenotaCamere.visibility = View.GONE
                    binding.prenotaHotelButton.text = resources.getText(R.string.prenota_button)
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
                Toast.makeText(requireContext(), "dataok: ${dataOk}\n ${numeroCarta} ${cvv}", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    fun calcolaPrezzo(costoCamera: BigDecimal, numeroCamere: IntArray, numeroPrenotati: Int, mezzo: String, durata: Int) : BigDecimal
    {
        var costo = "0.00".toBigDecimal()// inizializzo il costo

        for (i in numeroCamere.indices)//costo delle camere
        {
            when(i)
            {
                0 -> costo += costoCamera * numeroCamere[i].toBigDecimal()

                1 -> costo += costoCamera * numeroCamere[i].toBigDecimal() * "1.50".toBigDecimal()

                2 -> costo += costoCamera * numeroCamere[i].toBigDecimal() * "2.00".toBigDecimal()

                3 -> costo += costoCamera * numeroCamere[i].toBigDecimal() * "2.50".toBigDecimal()
            }
        }

        when(mezzo)//supplemento dei biglietti
        {
            "Treno" -> costo += "20.00".toBigDecimal() * numeroPrenotati.toBigDecimal()
            "Nave" -> costo += "50.00".toBigDecimal() * numeroPrenotati.toBigDecimal()
            "Aereo" -> costo += "100.00".toBigDecimal() * numeroPrenotati.toBigDecimal()
        }

        costo *= durata.toBigDecimal()//moltiplico per la durata

        return costo
    }
}