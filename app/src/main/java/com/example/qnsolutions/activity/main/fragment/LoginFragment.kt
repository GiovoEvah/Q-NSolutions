package com.example.qnsolutions.activity.main.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.qnsolutions.R
import com.example.qnsolutions.activity.home.HomeActivity
import com.example.qnsolutions.activity.main.MainActivity
import com.example.qnsolutions.databinding.FragmentLoginBinding
import com.example.qnsolutions.models.UtenteModel
import com.example.qnsolutions.util.DBMSQuery
import com.example.qnsolutions.util.NavigationManager
import com.example.qnsolutions.util.QueryReturnCallback

class LoginFragment : Fragment()
{
    private lateinit var binding : FragmentLoginBinding
    private lateinit var sharedPref : SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentLoginBinding.inflate(inflater)
        //Setto nella onCreateView, all'inizio, tutti i valori di email
        // e password in modo da trovare gia memorizzati i campi

        sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        binding.EmailLogin.editText!!.setText(sharedPref.getString("id", ""))
        binding.PasswordLogin.editText!!.setText(sharedPref.getString("password", ""))
        binding.ricordamiSwitch.isChecked = sharedPref.getBoolean("check", false)

        binding.forgottenLogin.setOnClickListener()
        {
            recuperaPassword(binding.EmailLogin.editText!!.text.toString())
        }

        binding.buttonLogin.setOnClickListener()
        {
            //Controllo delle credenziali e se corrette Intent alla Home Activity
            val email = binding.EmailLogin.editText!!.text.toString()
            val password = binding.PasswordLogin.editText!!.text.toString()

            var emailOk = if(email.isEmpty() || email.isBlank())
            {
                binding.EmailLogin.error = "Email non valida"
                false
            }
            else
            {
                binding.EmailLogin.error = null
                true
            }

            var passwordOk = if(password.isBlank() || password.isEmpty())
            {
                binding.PasswordLogin.error = "Password non valida"
                false
            }
            else
            {
                binding.PasswordLogin.error = null
                true
            }

            if(emailOk && passwordOk)
            {
                Log.e("login", "funge")
                verificaCredenziali(email, password)
            }
        }

        binding.ricordamiSwitch.setOnCheckedChangeListener()
        { _, isChecked ->
            val editor = sharedPref.edit()
            editor.putBoolean("ricordamiSwitch", isChecked)
            editor.apply()
        }

        binding.indietroLogin.setOnClickListener()
        {
            //ritorna alla schermata di benvenuto
            NavigationManager().rimuoviFragment(requireActivity() as MainActivity, this.tag.toString())
        }

        return binding.root
    }

    private fun recuperaPassword(email: String)
    {
        val builder = AlertDialog.Builder(requireActivity())

        if(email.isNotEmpty() && email.isNotBlank())
        {
            DBMSQuery().recuperaPassword(requireContext(), email, object : QueryReturnCallback
            {
                override fun onReturnValue(response: Any, message: String)//password aggiornata
                {
                    builder.setTitle("Nuova password: \"${resources.getText(R.string.default_password)}\"\nConsigliata modifica")
                    builder.setPositiveButton("OK") { _ , _ -> }
                    val dialog = builder.create()
                    binding.PasswordLogin.editText!!.setText(resources.getText(R.string.default_password))
                    dialog.show()
                }

                override fun onQueryFailed(fail: String)//email non trovata
                {
                    Toast.makeText( requireContext(), fail, Toast.LENGTH_SHORT).show()
                }

                override fun onQueryError(error: String)//errore di rete
                {
                    Toast.makeText( requireContext(), error, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun verificaCredenziali(email: String, password: String)
    {

        DBMSQuery().verificaCredenziali(requireContext(), email, password, object :
            QueryReturnCallback
        {
            override fun onReturnValue(response: Any, message: String)
            {
                //recupero i dati dell'utente
                DBMSQuery().getUtente(requireContext(), email, object : QueryReturnCallback
                {
                    override fun onReturnValue(response: Any, message: String)//se la query va a buon fine
                    {
                        Log.e("login", "lesgo")
                        val editor = sharedPref.edit()
                        if(binding.ricordamiSwitch.isChecked)//ricordami
                        {
                            editor.putString("id", email)
                            editor.putString("password", password)
                            editor.putBoolean("check", binding.ricordamiSwitch.isChecked)
                            editor.apply()
                        }
                        else
                        {
                            editor.remove("id")
                            editor.remove("password")
                            editor.remove("check")
                            editor.apply()
                        }

                        for (entry in 0 until requireActivity().supportFragmentManager.backStackEntryCount)
                        {
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                        //mi sposto sulla nuova activity
                        val intent = Intent(requireActivity(), HomeActivity::class.java)
                        intent.putExtra("utente", response as UtenteModel)
                        startActivity(intent)
                        requireActivity().finish()
                    }

                    override fun onQueryFailed(fail: String)//non trovo nessuno ma non dovrebbe succedere poiché le credenziali sono verificate
                    {
                        Toast.makeText(requireContext(), fail, Toast.LENGTH_SHORT).show()
                        Log.e("login", fail)
                    }

                    override fun onQueryError(error: String)//errore di rete
                    {
                        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                        Log.e("login", error)
                    }
                })
            }

            override fun onQueryFailed(fail: String)//Email o password errata
            {
                Toast.makeText(requireContext(), fail, Toast.LENGTH_SHORT).show()
            }

            override fun onQueryError(error: String)//Errore di rete
            {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                Log.e("login", error)
            }
        })
    }
}