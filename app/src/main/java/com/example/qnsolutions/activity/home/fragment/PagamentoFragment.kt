package com.example.qnsolutions.activity.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qnsolutions.R
import com.example.qnsolutions.adapters.CreditCardAdapter
import com.example.qnsolutions.databinding.FragmentDatiDiPagamentoBinding
import com.example.qnsolutions.models.CreditCardModel
import com.example.qnsolutions.models.UtenteModel
import com.example.qnsolutions.util.DBMSQuery
import com.example.qnsolutions.util.QueryReturnCallback
import java.util.Calendar

class PagamentoFragment : Fragment(){
    lateinit var binding: FragmentDatiDiPagamentoBinding
    lateinit var list: MutableList<CreditCardModel>
    private lateinit var utente: UtenteModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentDatiDiPagamentoBinding.inflate(inflater)
        list = mutableListOf()

        utente = requireArguments().getParcelable("utente")!!


        DBMSQuery().getCarte(requireContext(), utente.email, object : QueryReturnCallback{
            override fun onReturnValue(response: Any, message: String)
            {
                list = response as MutableList<CreditCardModel>
                binding.listaCarte.layoutManager = LinearLayoutManager(requireContext())
                binding.listaCarte.adapter = CreditCardAdapter(list, requireContext(), utente.email)

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



                binding.aggiungiCarta.setOnClickListener()
                {
                    if(binding.layoutCarta.visibility == View.GONE)
                    {
                        binding.aggiungiCarta.text = "Salva"
                        binding.layoutCarta.visibility = View.VISIBLE
                    }
                    else
                    {
                        //verfificare se i campi sono corretti
                        //inserire la carta nel dbms
                        val numero_carta = binding.nuovaCarta.editText!!.text.toString()
                        val cvv = binding.nuovoCvv.editText!!.text.toString()
                        if(numero_carta.length == 16 && cvv.length == 3 && (numero_carta.toCharArray()[0] == '4' || numero_carta.toCharArray()[0] == '5'))
                        {
                            val mese = binding.mesiSpinner.selectedItem.toString()
                            val anno = binding.anniSpinner.selectedItem.toString()
                            val carta = CreditCardModel(numero_carta, "${mese}/${anno}", cvv, 15000.00)
                            (binding.listaCarte.adapter as CreditCardAdapter).aggiungiCarta(carta)

                            DBMSQuery().salvaCarta(requireContext(), utente.email, carta, object : QueryReturnCallback
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
                        else
                        {
                            Toast.makeText(requireContext(), "Carta non valida", Toast.LENGTH_SHORT).show()
                        }

                        binding.mesiSpinner.setSelection(0)
                        binding.anniSpinner.setSelection(0)
                        binding.nuovaCarta.editText!!.setText("")
                        binding.nuovoCvv.editText!!.setText("")
                        binding.layoutCarta.visibility = View.GONE
                        binding.aggiungiCarta.text = "Aggiungi"
                    }
                }

            }

            override fun onQueryFailed(fail: String)
            {
                //ciao
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
            }

            override fun onQueryError(error: String)
            {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        })

        return binding.root
    }
}