package com.example.qnsolutions.activity.main.fragment

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.example.qnsolutions.R
import com.example.qnsolutions.activity.main.MainActivity
import com.example.qnsolutions.common.DatePickerFragment
import com.example.qnsolutions.databinding.FragmentRegisterBinding
import com.example.qnsolutions.models.UtenteModel
import com.example.qnsolutions.util.DBMSQuery
import com.example.qnsolutions.util.NavigationManager
import com.example.qnsolutions.util.QueryReturnCallback
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar
import java.util.regex.Pattern

class RegisterFragment : Fragment()
{
    private lateinit var binding : FragmentRegisterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentRegisterBinding.inflate(inflater)

        setFragmentResultListener("requestKey")
        { _ , bundle ->
            val result = bundle.getString("bundleKey")
            binding.DataNascitaRegister.text = result
            binding.DataNascitaRegister.setTextColor(resources.getColor(R.color.black, null))
        }

        binding.SelezionaData.setOnClickListener()
        {
            DatePickerFragment("bundleKey", "requestKey", null, Calendar.getInstance().time.time).show(requireActivity().supportFragmentManager, "datePicker")
        }

        var sesso = binding.SessoErrorText.text.toString()

        binding.SessoRegister.setOnCheckedChangeListener()
        {   radioGroup, _ ->
            when (radioGroup.checkedRadioButtonId)
            {
                binding.MaschioRegister.id ->
                {
                    sesso = binding.MaschioRegister.text.toString()
                    binding.SessoErrorText.visibility = View.INVISIBLE
                }

                binding.FemminaRegister.id ->
                {
                    sesso = binding.FemminaRegister.text.toString()
                    binding.SessoErrorText.visibility = View.INVISIBLE
                }

                binding.AltroRegister.id ->
                {
                    sesso = binding.AltroRegister.text.toString()
                    binding.SessoErrorText.visibility = View.INVISIBLE
                }
            }
        }

        verificaCampoVisivo(binding.EmailRegister, 0)
        verificaCampoVisivo(binding.NomeRegister, 0)
        verificaCampoVisivo(binding.CognomeRegister, 0)
        verificaCampoVisivo(binding.PasswordRegister, 7)
        verificaCampoVisivo(binding.ConfirmPasswordRegister, 0)
        verificaCampoVisivo(binding.CellulareRegister, 9)
        verificaCampoVisivo(binding.ResidenzaRegister, 0)

        binding.ButtonRegister.setOnClickListener()
        {
            //Controllo sui campi

            val email = binding.EmailRegister.editText!!.text.toString()
            val emailOk = Patterns.EMAIL_ADDRESS.matcher(email).matches()
            Log.d("emailValidation", "${email} ${emailOk}")

            val password = binding.PasswordRegister.editText!!.text.toString()
            val passwordOk = passwordValidation(password)
            Log.d("password", "${password} ${passwordOk}")

            val confermaPassword = binding.ConfirmPasswordRegister.editText!!.text.toString()
            val confermaPasswordOk = passwordOk && confermaPassword == password
            Log.d("confermaPassword", "${confermaPassword} ${confermaPasswordOk}")

            val nome = binding.NomeRegister.editText!!.text.toString()
            val nomeOk = verificaLunghezza(nome, 1, 30)
            Log.d("nome", "${nome} ${nomeOk}")

            val cognome = binding.CognomeRegister.editText!!.text.toString()
            val cognomeOk = verificaLunghezza(cognome, 1, 30)
            Log.d("cognome", "${cognome} ${cognomeOk}")

            val cellulare = binding.CellulareRegister.editText!!.text.toString()
            val cellulareOk = Patterns.PHONE.matcher(cellulare).matches()
            Log.d("cellulareValidation", "${cellulare} ${cellulareOk}")

            val residenza = binding.ResidenzaRegister.editText!!.text.toString()
            val residenzaOk = verificaLunghezza(residenza, 1, 60)
            Log.d("residenza", "${residenza} ${residenzaOk}")

            val dataNascita = binding.DataNascitaRegister.text.toString()
            val dataNascitaOk = if (dataNascita == getString(R.string.data_error))
            {
                binding.DataNascitaRegister.visibility = View.VISIBLE
                false
            }
            else
            {
                true
            }
            Log.d("dataNascita", "${dataNascita} ${dataNascitaOk}")

            val sessoOk = if (sesso == resources.getText(R.string.sesso_error_text))
            {
                binding.SessoErrorText.visibility = View.VISIBLE
                false
            }
            else
            {
                true
            }

            val terminiOk = binding.CheckBoxCondizioni.isChecked

            if(!terminiOk)
            {
                binding.CheckBoxCondizioni.setTextColor(resources.getColor(R.color.red, null))
            }
            else
            {
                binding.CheckBoxCondizioni.setTextColor(resources.getColor(R.color.black, null))
            }

            if(emailOk && nomeOk && cognomeOk && cellulareOk && sessoOk && dataNascitaOk && residenzaOk && terminiOk)
            {
                registrazineUtente(UtenteModel(nome, cognome, dataNascita, sesso, email, residenza, password, cellulare, null))
            }
        }

        binding.buttonBack.setOnClickListener()
        {
            NavigationManager().rimuoviFragment(requireActivity() as AppCompatActivity, this.tag.toString())
        }

        return binding.root
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

    private fun registrazineUtente(utente: UtenteModel)
    {
        DBMSQuery().registrazioneUtente(requireContext(), utente, object : QueryReturnCallback
        {
            override fun onReturnValue(response: Any, message: String)
            {
                NavigationManager().rimuoviFragment(requireActivity() as MainActivity, "registrati")
                Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
            }

            override fun onQueryFailed(fail: String)
            {
                Toast.makeText(requireActivity(), fail, Toast.LENGTH_SHORT).show()
            }

            override fun onQueryError(error: String)
            {
                Toast.makeText(requireActivity(), error, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun verificaLunghezza(text: String, minIndex: Int, maxIndex: Int) : Boolean
    {
        return text.length in minIndex .. maxIndex
    }
}
