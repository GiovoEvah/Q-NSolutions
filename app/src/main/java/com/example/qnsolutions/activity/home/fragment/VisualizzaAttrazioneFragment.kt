package com.example.qnsolutions.activity.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.scale
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.qnsolutions.R
import com.example.qnsolutions.databinding.FragmentVisualizzaAttrazioneBinding
import com.example.qnsolutions.models.AttrazioneTuristicaModel
import com.example.qnsolutions.util.DBMSQuery
import com.example.qnsolutions.util.QueryReturnCallback


class VisualizzaAttrazioneFragment : Fragment()
{
    lateinit var binding: FragmentVisualizzaAttrazioneBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentVisualizzaAttrazioneBinding.inflate(inflater)

        val attrazioneId = requireArguments().getInt("attrazioneId")!!
        val tipo = requireArguments().getString("attrazioneTipo")!!
        val utenteId = requireArguments().getString("utenteId")!!

        DBMSQuery().getAttrazione(requireContext(), attrazioneId, object : QueryReturnCallback
        {
            override fun onReturnValue(response: Any, message: String)
            {
                val attrazione = response as AttrazioneTuristicaModel

                binding.visualizzaAttrazioneImmagine.setImageBitmap(attrazione.immagine?.scale(500, 300))
                binding.visualizzaAttrazioneNome.text = "${requireContext().resources.getText(R.string.visualizza_attazione_nome_text)} ${attrazione.nome}"
                binding.visualizzaAttrazioneTipo.text = "${requireContext().resources.getText(R.string.visualizza_attazione_categoria_text)} ${attrazione.tipo}"
                binding.visualizzaAttrazioneDescrizione.text = attrazione.descrizione
                binding.visualizzaAttrazioneIndirizzo.text = "${requireContext().resources.getText(R.string.visualizza_attazione_indirizzo_text)} ${attrazione.indirizzo}"
                if(attrazione.prezzo_medio == "0.00".toBigDecimal())
                {
                    binding.visualizzaAttrazionePrezzo.text = "${resources.getText(R.string.costo_del_biglietto_text)} Gratis €"
                }
                else
                {
                    binding.visualizzaAttrazionePrezzo.text = "${requireContext().resources.getText(R.string.visualizza_attazione_prezzo_medio_text)} ${attrazione.prezzo_medio} €"
                }

                binding.visualizzaAttrazioneTelefono.text = "${requireContext().resources.getText(R.string.visualizza_attazione_numero_di_telefono_text)} ${attrazione.num_telefono}"
                binding.attrazioneRatingBar.rating = attrazione.rating.toFloat()

                binding.recensioniButton.setOnClickListener()
                {
                    RecensioniFragment(attrazioneId, utenteId).show(childFragmentManager, "recensioni")
                }

                when (tipo)
                {
                    "Hotel" ->
                    {
                        val fragment = HotelPanelFragment()
                        fragment.arguments = bundleOf("hotelId" to attrazione.id, "utenteId" to utenteId, "costoCamera" to attrazione.prezzo_medio.toString())
                        requireActivity().supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, fragment, "hotel_panel").commit()
                    }

                    "Ristorante" ->
                    {
                        val fragment = RistorantePanelFragment()
                        fragment.arguments = bundleOf("ristoranteId" to attrazione.id, "utenteId" to utenteId)
                        requireActivity().supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, fragment, "ristorante_panel").commit()
                    }

                    else ->
                    {
                        val fragment = AttrazionePanelFragment()
                        fragment.arguments = bundleOf("attrazioneId" to attrazione.id, "utenteId" to utenteId, "prezzoBiglietto" to attrazione.prezzo_medio.toString())
                        requireActivity().supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, fragment, "attrazione_panel").commit()
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

        return binding.root
    }
}












