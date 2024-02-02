package com.example.qnsolutions.activity.home

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import com.example.qnsolutions.util.NavigationManager
import com.example.qnsolutions.R
import com.example.qnsolutions.activity.home.fragment.ContattiFragment
import com.example.qnsolutions.activity.home.fragment.HomeFragment
import com.example.qnsolutions.activity.home.fragment.NotificaFragment
import com.example.qnsolutions.activity.home.fragment.PagamentoFragment
import com.example.qnsolutions.activity.home.fragment.PreferitiFragment
import com.example.qnsolutions.activity.home.fragment.PrenotazioniFragment
import com.example.qnsolutions.activity.home.fragment.ProfiloFragment
import com.example.qnsolutions.activity.home.fragment.RicercaCittaFragment
import com.example.qnsolutions.activity.home.fragment.TransazioniFragment
import com.example.qnsolutions.activity.main.MainActivity
import com.example.qnsolutions.models.UtenteModel
import com.example.qnsolutions.databinding.ActivityHomeBinding
import com.example.qnsolutions.models.NotificaModel
import com.example.qnsolutions.util.DBMSQuery
import com.example.qnsolutions.util.QueryReturnCallback


class HomeActivity : AppCompatActivity()
{
    private lateinit var binding: ActivityHomeBinding
    lateinit var toggle: ActionBarDrawerToggle
    val frame = R.id.HomeFrameLayout
    val rootTag = "home"
    private lateinit var utente: UtenteModel

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        utente = intent.extras!!.getParcelable("utente")!!

        binding.HomeMenuButton.setOnClickListener()
        {
            binding.DrowerLayout.openDrawer(GravityCompat.START)
        }

        binding.HomeNotificationButton.setOnClickListener()
        {
            //click sulla campanellina

            DBMSQuery().prendiNotifiche(this, utente.email, object : QueryReturnCallback
            {
                override fun onReturnValue(response: Any, message: String)
                {
                    NotificaFragment(response as MutableList<NotificaModel>).show(supportFragmentManager, "Notifiche")
                }

                override fun onQueryFailed(fail: String)
                {
                    Toast.makeText(this@HomeActivity, fail, Toast.LENGTH_SHORT).show()
                }

                override fun onQueryError(error: String)
                {
                    Toast.makeText(this@HomeActivity, error, Toast.LENGTH_SHORT).show()
                }
            })
        }

        toggle = ActionBarDrawerToggle(this, binding.DrowerLayout, R.string.open_drower, R.string.close_drower)
        binding.DrowerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.searchButton.setOnClickListener()
        {
                NavigationManager().scambiaFragment(this@HomeActivity, frame, RicercaCittaFragment(), "ricerca_citta", rootTag, true, bundleOf("utenteId" to utente.email))
        }

        var bundle = Bundle()
        bundle.putParcelable("utente", utente)

        binding.navigationMenu.setNavigationItemSelectedListener()
        {
             when(it.itemId)
             {
                 R.id.profile_home_menu ->
                 {
                     svuotaMenu()
                     NavigationManager().scambiaFragment(this, frame, ProfiloFragment(), "profilo", rootTag, true, bundle)
                 }

                 R.id.dati_pagamento_home_menu ->
                 {
                     svuotaMenu()
                     NavigationManager().scambiaFragment(this, frame, PagamentoFragment(), "pagamenti", rootTag, true, bundle)
                 }

                 R.id.prenotazioni_home_menu ->
                 {
                     svuotaMenu()
                     NavigationManager().scambiaFragment(this, frame, PrenotazioniFragment(), "prenotazioni", rootTag, true, bundle)
                 }

                 R.id.transazioni_home_menu ->
                 {
                     svuotaMenu()
                     NavigationManager().scambiaFragment(this, frame, TransazioniFragment(), "transazioni", rootTag, true, bundle)
                 }

                 R.id.contatti_home_menu ->
                 {
                     svuotaMenu()
                     NavigationManager().scambiaFragment(this, frame, ContattiFragment(), "contatti", rootTag,true, null)
                 }

                 R.id.preferiti_home_menu ->
                 {
                    //apre le mete preferite
                     svuotaMenu()
                     NavigationManager().scambiaFragment(this, frame, PreferitiFragment(), "preferiti", rootTag, true, bundle)
                 }

                 R.id.logout_home_menu ->
                 {
                    //torna alla schermata di benvenuto
                     logout()
                 }
             }
            binding.DrowerLayout.closeDrawer(binding.navigationMenu)
            true
        }

        binding.navigationMenu.getHeaderView(0).setOnClickListener()
        {
            val manager = supportFragmentManager
            if (manager.backStackEntryCount > 0 && manager.getBackStackEntryAt(manager.backStackEntryCount-1).name != "home")
            {
                for (entry in 0 until manager.backStackEntryCount)
                {
                    manager.popBackStack()
                }
                val home = HomeFragment()
                home.arguments = bundleOf("utenteId" to utente.email)
                NavigationManager().apriFragment(this, frame, home, "home")
                binding.DrowerLayout.closeDrawer(binding.navigationMenu)
            }

        }

        binding.navigationMenu.bringToFront()

        if(savedInstanceState == null)
        {
            val fragment = HomeFragment()
            fragment.arguments = bundleOf("utenteId" to utente.email)
            NavigationManager().apriFragment(this, frame, fragment, "home")
        }
    }

    override fun onBackPressed()
    {
        if(supportFragmentManager.backStackEntryCount != 1)
        {
            super.onBackPressed()
        }
        else
        {
            logout()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        if(toggle.onOptionsItemSelected(item))
        {
            true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun logout()
    {
        //Apro un dialog per assicurarmi che l'utente sia sicuro di volersi disconnettere
        val builder = AlertDialog.Builder(this)
        //Setto il titolo del dialog con i bottoni si e no
        builder.setTitle("Sicuro di voler uscire?")
        builder.setPositiveButton("Si")
        { dialog, _ ->
            // Azioni da eseguire se l'utente sceglie "Si"
            Toast.makeText(applicationContext, "Logout effettuato", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            for (entry in 0 until supportFragmentManager.backStackEntryCount)
            {
                supportFragmentManager.popBackStack()
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        builder.setNegativeButton("No")
        { dialog, _ ->
            // Azioni da eseguire se l'utente sceglie "No"
            Toast.makeText(applicationContext, "Grazie per essere rimasto con noi :-)", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun svuotaMenu()
    {
        for (entry in 2 until supportFragmentManager.backStackEntryCount)
        {
            supportFragmentManager.popBackStack()
        }
    }
}