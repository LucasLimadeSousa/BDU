package com.example.bdu.livros

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R
import com.example.bdu.suporte.ReportarProblemaActivity

class MeusLivrosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.livros_meuslivros)

        val btnVoltarHome = findViewById<ImageButton?>(R.id.btn_backReturn)
        val btnReport = findViewById<TextView?>(R.id.btn_report)
        val btnVerTudo = findViewById<TextView?>(R.id.VerTudo)


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
        btnVoltarHome?.setOnClickListener {
            val intent = Intent(this, TelahomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnReport?.setOnClickListener {
            val intent = Intent(this, ReportarProblemaActivity::class.java)
            startActivity(intent)
        }
        btnVerTudo?.setOnClickListener {
            val intent = Intent(this, VerTudoActivity::class.java)
            startActivity(intent)
        }
    }

}