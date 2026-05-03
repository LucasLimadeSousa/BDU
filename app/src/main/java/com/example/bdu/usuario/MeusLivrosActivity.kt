package com.example.bdu

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.cardview.widget.CardView

class MeusLivrosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.usuario_meus_livros)
        
        val mainView = findViewById<View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        val popup = findViewById<CardView>(R.id.popup_notification)
        if (popup != null) {
            // Mostrar o popup após 1 segundo
            Handler(Looper.getMainLooper()).postDelayed({
                popup.visibility = View.VISIBLE
                
                // Esconder o popup após 5 segundos
                Handler(Looper.getMainLooper()).postDelayed({
                    popup.visibility = View.GONE
                }, 5000)
            }, 1000)
        }
    }
}