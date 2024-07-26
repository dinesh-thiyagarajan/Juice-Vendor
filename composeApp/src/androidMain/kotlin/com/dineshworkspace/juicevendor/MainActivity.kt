package com.dineshworkspace.juicevendor

import JuiceVendorApp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import auth.repositories.AuthRepository
import auth.viewModels.AuthViewModel
import file.FilesRepository
import juices.repositories.JuiceVendorRepository
import juices.viewModels.JuiceVendorViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val filesDirectory = this.applicationContext.filesDir
            val juiceKadaiViewModel: JuiceVendorViewModel = viewModel {
                JuiceVendorViewModel(
                    filesRepository = FilesRepository(filesDirectory),
                    juiceVendorRepository = JuiceVendorRepository()
                )
            }

            val authViewModel: AuthViewModel = viewModel {
                AuthViewModel(
                    AuthRepository()
                )
            }
            JuiceVendorApp(juiceKadaiViewModel, authViewModel)
        }
    }
}