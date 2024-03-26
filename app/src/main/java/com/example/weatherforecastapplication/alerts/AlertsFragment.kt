package com.example.weatherforecastapplication.alerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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
            val navController = findNavController()
            val action =AlertsFragmentDirections.actionNavigationAlertsToNavigationMap()
            action.id = 3
            navController.navigate(action)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}