package com.example.weatherforecastapplication.alerts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weatherforecastapplication.MapActivity
import com.example.weatherforecastapplication.R
import com.example.weatherforecastapplication.databinding.FragmentAlertsBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AlertsFragment : Fragment() {

    lateinit var btnAdd: FloatingActionButton

    private var _binding: FragmentAlertsBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(AlertsViewModel::class.java)

        _binding = FragmentAlertsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        notificationsViewModel.text.observe(viewLifecycleOwner) {

        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAdd=view.findViewById(R.id.btnAddLoction)
        btnAdd.setOnClickListener{
            val intent= Intent(context, MapActivity::class.java)
                .putExtra("id","alert")
            startActivity(intent)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}