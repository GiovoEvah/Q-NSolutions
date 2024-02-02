package com.example.qnsolutions.activity.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qnsolutions.adapters.NotificaAdapter
import com.example.qnsolutions.databinding.FragmentNotificheBinding
import com.example.qnsolutions.models.NotificaModel

class NotificaFragment(private val notifiche : MutableList<NotificaModel>) : DialogFragment()
{
    lateinit var binding : FragmentNotificheBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentNotificheBinding.inflate(inflater)

        binding.listaNotifiche.layoutManager = LinearLayoutManager(requireContext())
        binding.listaNotifiche.adapter = NotificaAdapter(notifiche, requireContext(), this)

        dialog?.setCanceledOnTouchOutside(true)

        return binding.root
    }
}