package com.example.qnsolutions.activity.home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qnsolutions.adapters.CameraAdapter
import com.example.qnsolutions.databinding.FragmentCamereHotelBinding
import com.example.qnsolutions.models.CameraModel
import com.example.qnsolutions.util.DBMSQuery
import com.example.qnsolutions.util.QueryReturnCallback

class CamereHotelFragment(private val hotelId: Int) : DialogFragment()
{
    lateinit var binding: FragmentCamereHotelBinding
    lateinit var list: MutableList<CameraModel>
    lateinit var contaCamere: IntArray
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = FragmentCamereHotelBinding.inflate(inflater)
        contaCamere = intArrayOf(0, 0, 0, 0)
        list = mutableListOf()
        binding.camereRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.camereRecycler.adapter = CameraAdapter(list, requireContext(), contaCamere)

        DBMSQuery().getCamere(requireContext(), hotelId, object : QueryReturnCallback
        {
            override fun onReturnValue(response: Any, message: String)
            {
                list = response as MutableList<CameraModel>
                (binding.camereRecycler.adapter as CameraAdapter).list = list
                (binding.camereRecycler.adapter as CameraAdapter).notifyDataSetChanged()
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

        binding.annullaCamere.setOnClickListener()
        {
            dismiss()
        }

        binding.confermaCamere.setOnClickListener()
        {
            var sum = 0
            for (num in contaCamere)
            {
                sum += num
            }
            if (sum > 0)
            {
                setFragmentResult("bundleCamere", bundleOf("numeroCamere" to contaCamere))
                dismiss()
            }
            else
            {
                Toast.makeText(requireContext(), "Nessuna camera selezionata", Toast.LENGTH_SHORT).show()
            }
        }

       return binding.root
    }
}