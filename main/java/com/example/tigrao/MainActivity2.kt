package com.example.tigrao

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        Log.d("ronaldo", "Entrou no onCreate")
    }

    override fun onStart() {
        super.onStart()
        Log.d("ronaldo", "Entrou no onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("ronaldo", "Entrou no onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("ronaldo", "Entrou no onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("ronaldo", "Entrou no onStop")
    }

    override fun onDestroy() {
        Log.d("ronaldo", "Entrou no onDestroy")
        super.onDestroy()
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("ronaldo", "Entrou no onRestart")
    }
}