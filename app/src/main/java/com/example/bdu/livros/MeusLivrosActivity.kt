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
import com.example.bdu.usuario.MeuPerfilActivity

class MeusLivrosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.livros_meuslivros)

        val btnVoltarHome = findViewById<ImageButton?>(R.id.btn_backReturn)
        val btnReport = findViewById<TextView?>(R.id.btn_report)
        val btnVerTudo = findViewById<TextView?>(R.id.VerTudo)

        val btnFila = findViewById<ImageButton?>(R.id.btn_navFila)
        val btnMeusLivros = findViewById<ImageButton?>(R.id.btn_navMeusLivros)
        val btnHome = findViewById<ImageButton?>(R.id.btn_navHome)
        val btnDesejos = findViewById<ImageButton?>(R.id.btn_navDesejos)
        val btnPerfil = findViewById<ImageButton?>(R.id.btn_navPerfil)

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
            Handler(Looper.getMainLooper()).postDelayed({
                popup.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    popup.visibility = View.GONE
                }, 0)
            }, 1000)
        }

        btnVoltarHome?.setOnClickListener {
            val intent = Intent(this, TelahomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnReport?.setOnClickListener {
            val intent = Intent(this, com.example.bdu.suporte.ReportarProblema2Activity::class.java)
            startActivity(intent)
        }

        btnVerTudo?.setOnClickListener {
            val intent = Intent(this, VerTudoActivity::class.java)
            startActivity(intent)
        }
        //barra de tarefas

        btnFila?.setOnClickListener {
            val intent = Intent(this, ListadeEsperaActivity::class.java)
            startActivity(intent)
        }
        btnMeusLivros?.setOnClickListener {
            // Já está na atividade Meus Livros
        }
        btnHome?.setOnClickListener {
            val intent = Intent(this, TelahomeActivity::class.java)
            startActivity(intent)
        }
        btnDesejos?.setOnClickListener {
            val intent = Intent(this, ListaDesejosActivity::class.java)
            startActivity(intent)
        }
        btnPerfil?.setOnClickListener {
            val intent = Intent(this, MeuPerfilActivity::class.java)
            startActivity(intent)
        }
    }
}