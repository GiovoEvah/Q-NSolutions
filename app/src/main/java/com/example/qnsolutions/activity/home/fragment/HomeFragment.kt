package com.example.qnsolutions.activity.home.fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.scale
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.qnsolutions.R
import com.example.qnsolutions.activity.home.HomeActivity
import com.example.qnsolutions.databinding.FragmentHomeBinding
import com.example.qnsolutions.util.NavigationManager

class HomeFragment : Fragment()
{
    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentHomeBinding.inflate(inflater)
        val utenteId = requireArguments().getString("utenteId")!!
        val homeActivity = requireActivity() as HomeActivity
        val nonSaiScegliere = BitmapFactory.decodeResource(resources, R.drawable.non_sai_scegliere).scale(900, 200)
        val unaMeta = BitmapFactory.decodeResource(resources, R.drawable.una_meta).scale(900, 200)
        val provaConUnaMagia = BitmapFactory.decodeResource(resources, R.drawable.prova_con_una_magia).scale(900, 200)
        val wizardImage = BitmapFactory.decodeResource(resources, R.drawable.wizard).scale(500, 500)

        binding.nonSaiScegliere.setImageBitmap(nonSaiScegliere)
        binding.unaMeta.setImageBitmap(unaMeta)
        binding.provaConUnaMagia.setImageBitmap(provaConUnaMagia)
        binding.wizardButton.setImageBitmap(wizardImage)

        binding.wizardButton.setOnClickListener()
        {
            NavigationManager().scambiaFragment(homeActivity, homeActivity.frame, WizardFragment(), "wizard", homeActivity.rootTag, true, bundleOf("utenteId" to utenteId))
        }

        return binding.root
    }
}