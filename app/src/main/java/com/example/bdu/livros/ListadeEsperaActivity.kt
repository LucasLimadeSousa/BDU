package com.example.bdu.livros

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R
import com.example.bdu.usuario.MeuPerfilActivity

class ListadeEsperaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.livros_lista_de_espera)

        val btnFila = findViewById<ImageButton?>(R.id.btn_nav_fila)
        val btnMeusLivros = findViewById<ImageButton?>(R.id.btn_nav_meuslivros)
        val btnHome = findViewById<ImageButton?>(R.id.btn_nav_home)
        val btnDesejos = findViewById<ImageButton?>(R.id.btn_nav_desejos)
        val btnperfil = findViewById<ImageButton?>(R.id.btn_nav_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btnFila?.setOnClickListener {
            val intent = Intent(this, ListadeEsperaActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnMeusLivros?.setOnClickListener {
            val intent = Intent(this, MeusLivrosActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnHome?.setOnClickListener {
            val intent = Intent(this, TelahomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnDesejos?.setOnClickListener {
            val intent = Intent(this, ListaDesejosActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnperfil?.setOnClickListener {
            val intent = Intent(this, MeuPerfilActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<TextView>(R.id.btn_confirmar_reserva).setOnClickListener {
            Toast.makeText(this, "Reserva confirmada!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, TelahomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}