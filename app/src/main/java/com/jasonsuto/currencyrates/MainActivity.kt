package com.jasonsuto.currencyrates

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.jasonsuto.currencyrates.data.models.Rates
import com.jasonsuto.currencyrates.databinding.ActivityMainBinding
import com.jasonsuto.currencyrates.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.reflect.full.memberProperties

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //generate dropdown menu string array from Rates class
        val spinnerArray = Rates::class.memberProperties.map { it.name }.toTypedArray()

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerArray)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.fromSpinner.adapter = adapter
        binding.toSpinner.adapter = adapter

        binding.convertButton.setOnClickListener {
            viewModel.convert(
                binding.fromEditText.text.toString(),
                binding.fromSpinner.selectedItem.toString(),
                binding.toSpinner.selectedItem.toString(),
                getString(R.string.api_key)

            )
        }
        lifecycleScope.launchWhenStarted {
            viewModel.conversion.collect { event ->
                when (event) {
                    is MainViewModel.CurrencyEvent.Success -> {
                        binding.progressBar.isVisible = false
                        binding.resultTV.setTextColor(Color.BLACK)
                        binding.resultTV.text = event.resultText
                        println(event.resultText)

                    }
                    is MainViewModel.CurrencyEvent.Failure -> {
                        binding.progressBar.isVisible = false
                        binding.resultTV.setTextColor(Color.RED)
                        println(event.errorText)
                        binding.resultTV.text = event.errorText

                    }
                    is MainViewModel.CurrencyEvent.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                    else -> Unit
                }
            }
        }
    }
}
