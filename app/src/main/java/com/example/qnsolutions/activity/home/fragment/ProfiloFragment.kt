package com.example.qnsolutions.activity.home.fragment
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.graphics.scale
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.example.qnsolutions.R
import com.example.qnsolutions.common.DatePickerFragment
import com.example.qnsolutions.databinding.FragmentProfiloBinding
import com.example.qnsolutions.models.UtenteModel
import com.example.qnsolutions.util.DBMSQuery
import com.example.qnsolutions.util.QueryReturnCallback
import com.google.android.material.textfield.TextInputLayout
import java.time.LocalDate
import java.util.Calendar
import java.util.regex.Pattern

class ProfiloFragment: Fragment(), AdapterView.OnItemSelectedListener
{
    private lateinit var binding : FragmentProfiloBinding
    private lateinit var utente: UtenteModel
    private var modificabile = false
    var i : Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfiloBinding.inflate(inflater)
        utente = requireArguments().getParcelable("utente")!!

        memorizzaCredenziali()
        binding.passwordProfiloText.transformationMethod = PasswordTransformationMethod()
        binding.passwordTextProfiloEdit.editText!!.transformationMethod = PasswordTransformationMethod()

        verificaCampoVisivo(binding.nomeTextProfiloEdit, 0)
        verificaCampoVisivo(binding.cognomeTextProfiloEdit, 0)
        verificaCampoVisivo(binding.passwordTextProfiloEdit, 7)
        verificaCampoVisivo(binding.cognomeTextProfiloEdit, 9)
        verificaCampoVisivo(binding.residenzaProfiloEdit, 0)

        binding.ModificaDataProfilo.setOnClickListener()
        {
            setFragmentResultListener("requestKey")
            { _ , bundle ->
                val result = bundle.getString("bundleKey")
                binding.dataNascitaTextProfilo.text = result
            }

            DatePickerFragment("bundleKey", "requestKey", null, Calendar.getInstance().time.time).show(requireActivity().supportFragmentManager, "datePicker")
        }

        binding.mostraPassword.setOnCheckedChangeListener()
        { _, isChecked ->
            if(!isChecked)//nascondi
            {
                binding.passwordProfiloText.transformationMethod = PasswordTransformationMethod()
                binding.passwordTextProfiloEdit.editText!!.transformationMethod = PasswordTransformationMethod()
            }
            else//mostra
            {
                binding.passwordProfiloText.transformationMethod = null
                binding.passwordTextProfiloEdit.editText!!.transformationMethod = null
            }
        }
        binding.buttonModificaProfilo.setOnClickListener()
        {
            modificaCredenziali()
        }

        return binding.root
    }
    private fun memorizzaCredenziali()
    {
        //memorizzo nell'interfaccia i dati dell'utente
        binding.immagineProfilo.setImageBitmap(utente.immagine!!.scale(500, 400))
        binding.nomeProfiloText.text = utente.nome
        binding.cognomeProfiloText.text = utente.cognome
        binding.emailProfiloText.text = utente.email
        binding.telefonoText.text = utente.cellulare
        binding.passwordProfiloText.text = utente.passw
        binding.residenzaProfiloText.text = utente.residenza

        binding.nomeTextProfiloEdit.editText!!.setText(utente.nome)
        binding.cognomeTextProfiloEdit.editText!!.setText(utente.cognome)
        binding.telefonoEdit.editText!!.setText(utente.cellulare)
        binding.passwordTextProfiloEdit.editText!!.setText(utente.passw)
        binding.residenzaProfiloEdit.editText!!.setText(utente.residenza)
        binding.SessoTextProfilo.text = utente.sesso
        binding.dataNascitaTextProfilo.text = utente.data_nascita

        val spinnerAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sessi_profilo,
            android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.sessoSpinner.adapter = spinnerAdapter
        binding.sessoSpinner.onItemSelectedListener = this

        when(utente.sesso)
        {
            "Maschio" -> binding.sessoSpinner.setSelection(0)
            "Femmina" -> binding.sessoSpinner.setSelection(1)
            "Altro" -> binding.sessoSpinner.setSelection(2)
        }
    }



    private fun modificaCredenziali()
    {
        if(!modificabile)
        {
            //rendo modificabile il campo
            binding.nomeProfiloText.visibility = View.GONE
            binding.cognomeProfiloText.visibility = View.GONE
            binding.passwordProfiloText.visibility = View.GONE
            binding.residenzaProfiloText.visibility = View.GONE
            binding.telefonoText.visibility = View.GONE

            binding.nomeTextProfiloEdit.visibility = View.VISIBLE
            binding.cognomeTextProfiloEdit.visibility = View.VISIBLE
            binding.passwordTextProfiloEdit.visibility = View.VISIBLE
            binding.residenzaProfiloEdit.visibility = View.VISIBLE
            binding.telefonoEdit.visibility = View.VISIBLE
            binding.ModificaDataProfilo.visibility = View.VISIBLE
            binding.sessoSpinner.visibility = View.VISIBLE

            modificabile = true
            binding.buttonModificaProfilo.text = "Salva"
        }
        else
        {
            //rendo non modificabili i campi
            aggiornaVisibilita(binding.nomeProfiloText)
            aggiornaVisibilita(binding.cognomeProfiloText)
            aggiornaVisibilita(binding.passwordProfiloText)
            aggiornaVisibilita(binding.residenzaProfiloText)
            aggiornaVisibilita(binding.telefonoText)

            aggiornaVisibilita(binding.nomeTextProfiloEdit)
            aggiornaVisibilita(binding.cognomeTextProfiloEdit)
            aggiornaVisibilita(binding.passwordTextProfiloEdit)
            aggiornaVisibilita(binding.residenzaProfiloEdit)
            aggiornaVisibilita(binding.telefonoEdit)
            aggiornaVisibilita(binding.ModificaDataProfilo)
            aggiornaVisibilita(binding.sessoSpinner)

            val oldUtente = utente.copy()
            //salvo i dati

            val password = binding.passwordTextProfiloEdit.editText!!.text.toString()
            val passwordOk = passwordValidation(password)

            val nome = binding.nomeTextProfiloEdit.editText!!.text.toString()
            val nomeOk = verificaLunghezza(nome, 1, 30)

            val cognome = binding.cognomeTextProfiloEdit.editText!!.text.toString()
            val cognomeOk = verificaLunghezza(cognome, 1, 30)

            val cellulare = binding.telefonoEdit.editText!!.text.toString()
            val cellulareOk = Patterns.PHONE.matcher(cellulare).matches()

            val residenza = binding.residenzaProfiloEdit.editText!!.text.toString()
            val residenzaOk = verificaLunghezza(residenza, 1, 60)

            val dataNascita = binding.dataNascitaTextProfilo.text.toString()
            val dataNascitaOk = LocalDate.parse(dataNascita).isBefore(LocalDate.now())

            if (passwordOk && nomeOk && cognomeOk && cellulareOk && residenzaOk && dataNascitaOk)
            {
                utente.nome = nome
                utente.cognome = cognome
                utente.passw = password
                utente.cellulare = cellulare
                utente.residenza = residenza
                utente.data_nascita = dataNascita
                utente.sesso = binding.SessoTextProfilo.text.toString()
            }


            //aggiorno il database solo se l'utente ha modificato almeno un campo
            if(oldUtente.toString() != utente.toString())
            {
                aggiornaDBMS(utente)
            }

            //aggiorno l'interfaccia
            memorizzaCredenziali()
            modificabile = false
            binding.buttonModificaProfilo.text = resources.getString(R.string.modifica_profilo)
        }
    }

    override fun onItemSelected(adapter: AdapterView<*>?, spinner: View?, p2: Int, p3: Long)
    {
        binding.SessoTextProfilo.text = binding.sessoSpinner.selectedItem.toString()
    }

    override fun onNothingSelected(adapter: AdapterView<*>?) {}

    private fun aggiornaDBMS(utente: UtenteModel)
    {
        DBMSQuery().salvaModifiche(requireContext(), utente, object : QueryReturnCallback
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

    fun aggiornaVisibilita(view: View)
    {
        if (view.visibility == View.VISIBLE)
        {
            view.visibility = View.GONE
        }
        else
        {
            view.visibility = View.VISIBLE
        }
    }

    private fun verificaCampoVisivo(view: TextInputLayout, minLength: Int)
    {
        view.editText!!.doOnTextChanged()
        { text, _, _, _ ->
            if(text!!.length > view.counterMaxLength)
            {
                view.error = "Elemento troppo lungo"
            }
            else if(text.length <= minLength)
            {
                view.error = "Elemento troppo corto"
            }
            else
            {
                view.error = null
            }
        }
    }

    private fun passwordValidation(password: String): Boolean
    {
        if(password.length !in 8..20)
        {
            return false
        }

        if(!Pattern.matches(".*[0-9].*", password))
        {
            return false
        }

        if (!Pattern.matches(".*[A-Z].*" , password))
        {
            return false
        }

        if (!Pattern.matches(".*[a-z].*", password))
        {
            return false
        }

        if(!Pattern.matches(".*[!?_£$%&#@].*", password))
        {
            return false
        }

        return true
    }

    fun verificaLunghezza(text: String, minIndex: Int, maxIndex: Int) : Boolean
    {
        return text.length in minIndex .. maxIndex
    }
}