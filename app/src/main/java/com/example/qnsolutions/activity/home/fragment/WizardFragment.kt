package com.example.qnsolutions.activity.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.qnsolutions.R
import com.example.qnsolutions.activity.home.HomeActivity
import com.example.qnsolutions.databinding.FragmentWizardBinding
import com.example.qnsolutions.util.NavigationManager

class WizardFragment : Fragment()
{
    private lateinit var binding: FragmentWizardBinding
    private var progress = 0
    private lateinit var risposte: MutableList<Char>
    private var rispostaCorrente = 'Z'
    private lateinit var lista : MutableList<String>
    private lateinit var listaRisposte : MutableList<MutableList<String>>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentWizardBinding.inflate(inflater)
        val utenteId = requireArguments().getString("utenteId")!!
        risposte = mutableListOf()
        binding.forwardArrow.isEnabled = false
        binding.backArrow.isEnabled = false
        lista = mutableListOf()
        listaRisposte = mutableListOf()
        lista.add("Ti piace il caldo?")
        lista.add("Che tipo di cucina ti piace?")
        lista.add("Cosa non può mancare nella tua vacanza ideale?")
        lista.add("Che souvenir vorresti comprare?")
        lista.add("Cosa fai quando piove?")

        listaRisposte.add(mutableListOf())
        listaRisposte.get(0).add("Lo adoro!!!")
        listaRisposte.get(0).add("Ci convivo")
        listaRisposte.get(0).add("No")
        listaRisposte.get(0).add("Lo odio!!!")

        listaRisposte.add(mutableListOf())
        listaRisposte.get(1).add("Mediterranea")
        listaRisposte.get(1).add("Multietnica")
        listaRisposte.get(1).add("Nord-europea")
        listaRisposte.get(1).add("Non è ciò che cerco principalmente")

        listaRisposte.add(mutableListOf())
        listaRisposte.get(2).add("Del buon cibo!!")
        listaRisposte.get(2).add("Escursioni nella natura!!")
        listaRisposte.get(2).add("Lunghe passeggiate")
        listaRisposte.get(2).add("Tantissimi musei!!")

        listaRisposte.add(mutableListOf())
        listaRisposte.get(3).add("Qualche cibo tipico")
        listaRisposte.get(3).add("Un pezzo di storia")
        listaRisposte.get(3).add("Vestiti tipici")
        listaRisposte.get(3).add("Tantissime calamite")

        listaRisposte.add(mutableListOf())
        listaRisposte.get(4).add("Mi riparo!")
        listaRisposte.get(4).add("Corro!")
        listaRisposte.get(4).add("Guardo la pioggia da un riparo")
        listaRisposte.get(4).add("Mi godo l'acqua!!!")

        inserisciDomande(risposte.size)
        binding.backArrow.setOnClickListener()
        {
            if (risposte.size > 0)
            {
                rispostaCorrente = risposte.removeLast()
                progress -= 20
                binding.wizardProgressBar.progress = progress
                Toast.makeText(requireContext(), risposte.toString(), Toast.LENGTH_SHORT).show()

                when(rispostaCorrente)
                {
                    'A' -> binding.risposteRadio.check(binding.rispostaA.id)

                    'B' -> binding.risposteRadio.check(binding.rispostaB.id)

                    'C' -> binding.risposteRadio.check(binding.rispostaC.id)

                    'D' -> binding.risposteRadio.check(binding.rispostaD.id)
                }

                if (risposte.isEmpty())
                {
                    binding.backArrow.isEnabled = false
                }

                else{
                    binding.forwardArrow.isEnabled = true
                    inserisciDomande(risposte.size)
                }
            }
        }

        binding.forwardArrow.setOnClickListener()
        {
            if(risposte.size < 4){
                risposte.add(rispostaCorrente)
                progress += 20
                binding.wizardProgressBar.progress = progress
                binding.backArrow.isEnabled = risposte.isNotEmpty()
                binding.risposteRadio.clearCheck()
                rispostaCorrente = 'Z'
                inserisciDomande(risposte.size)
            }
            else{
                Toast.makeText(requireContext(), "Finito", Toast.LENGTH_SHORT).show()
                //cambiafragment
                var contatori = intArrayOf(0, 0, 0, 0)
                for(i in risposte){
                    when (i){
                        'A' -> contatori[0]++
                        'B' -> contatori[1]++
                        'C' -> contatori[2]++
                        'D' -> contatori[3]++
                    }
                }
                var d : Int

                when(max_contatori(contatori)){
                    0 -> {
                        d = 1
                        Toast.makeText(context, "Palermo", Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        d = 9
                        Toast.makeText(context, "Atene", Toast.LENGTH_SHORT).show()
                    }
                    2 -> {
                        d = 10
                        Toast.makeText(context, "Bruxelles", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        d = 5
                        Toast.makeText(context, "Augusta", Toast.LENGTH_SHORT).show()
                    }
                }
                NavigationManager().scambiaFragment(requireActivity() as HomeActivity, (requireActivity() as HomeActivity).frame, RicercaAttrazioneFragment(), "citta", (requireActivity() as HomeActivity).rootTag, true, bundleOf("cittaId" to d, "utenteId" to utenteId))
            }

            binding.forwardArrow.isEnabled = false
        }

        binding.risposteRadio.setOnCheckedChangeListener()
        { group, _ ->

            when(group.checkedRadioButtonId)
            {
                binding.rispostaA.id ->
                {
                    binding.forwardArrow.isEnabled = true
                    rispostaCorrente = 'A'
                }

                binding.rispostaB.id ->
                {
                    binding.forwardArrow.isEnabled = true
                    rispostaCorrente = 'B'
                }

                binding.rispostaC.id ->
                {
                    binding.forwardArrow.isEnabled = true
                    rispostaCorrente = 'C'
                }

                binding.rispostaD.id ->
                {
                    binding.forwardArrow.isEnabled = true
                    rispostaCorrente = 'D'
                }
            }
        }

        return binding.root
    }

    fun inserisciDomande(i : Int) {
        if(i < 5){
            binding.domandaText.text = lista.get(i)
            binding.rispostaA.text = "${resources.getText(R.string.risposta_a)}  ${listaRisposte.get(i).get(0)}"
            binding.rispostaB.text = "${resources.getText(R.string.risposta_b)}  ${listaRisposte.get(i).get(1)}"
            binding.rispostaC.text = "${resources.getText(R.string.risposta_c)}  ${listaRisposte.get(i).get(2)}"
            binding.rispostaD.text = "${resources.getText(R.string.risposta_d)}  ${listaRisposte.get(i).get(3)}"
        }

    }
    fun max_contatori(a : IntArray) : Int{
        var max = 0
        var indice_max = 0
        for(i in a.indices){
            if(a[i] > max){
                max = a[i]
                indice_max = i
            }
        }
        return indice_max
    }
}