package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.data.DuniaDatabase
import com.example.data.DuniaRepository
import com.example.ui.DuniaApp
import com.example.ui.DuniaViewModel
import com.example.ui.DuniaViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize SQLite Local Room Database
        val database = Room.databaseBuilder(
            applicationContext,
            DuniaDatabase::class.java, "dunia-v2-database"
        )
        .fallbackToDestructiveMigration()
        .build()

        val dao = database.duniaDao()
        val repository = DuniaRepository(dao)

        // Instantiate state engine ViewModel
        val viewModel = ViewModelProvider(
            this,
            DuniaViewModelFactory(application, repository)
        )[DuniaViewModel::class.java]

        setContent {
            MyApplicationTheme {
                DuniaApp(viewModel)
            }
        }
    }
}
