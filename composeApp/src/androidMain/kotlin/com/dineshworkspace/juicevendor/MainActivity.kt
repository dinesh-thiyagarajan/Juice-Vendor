package com.dineshworkspace.juicevendor

import JuiceVendorApp
import viewModels.JuiceVendorViewModel
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import repositories.JuiceVendorRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val juiceKadaiViewModel: JuiceVendorViewModel = viewModel {
                JuiceVendorViewModel(
                    JuiceVendorRepository()
                )
            }
            JuiceVendorApp(juiceKadaiViewModel)
        }
    }
}