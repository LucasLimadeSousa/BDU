package com.example.bdu.livros

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R
import com.example.bdu.usuario.MeuPerfilActivity
import com.google.android.material.button.MaterialButton

class ListadeEsperaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.livros_lista_de_espera)

        val btnFila = findViewById<ImageButton?>(R.id.btn_nav_fila)
        val btnMeusLivros = findViewById<ImageButton?>(R.id.btn_nav_meuslivros)
        val btnHome = findViewById<ImageButton?>(R.id.btn_nav_home)
        val btnDesejosNav = findViewById<ImageButton?>(R.id.btn_nav_desejos)
        val btnperfil = findViewById<ImageButton?>(R.id.btn_nav_perfil)
        
        val mainView = findViewById<View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // Lógica do botão "Lista de Desejos" (Coração: Branco <-> Vermelho)
        val btnMoverDesejos = findViewById<MaterialButton?>(R.id.btn_mover_desejos)
        var isFavorito = false

        btnMoverDesejos?.setOnClickListener {
            isFavorito = !isFavorito
            if (isFavorito) {
                btnMoverDesejos.iconTint = ColorStateList.valueOf(Color.RED)
                Toast.makeText(this, "Adicionado aos favoritos", Toast.LENGTH_SHORT).show()
            } else {
                btnMoverDesejos.iconTint = ColorStateList.valueOf(Color.WHITE)
                Toast.makeText(this, "Removido dos favoritos", Toast.LENGTH_SHORT).show()
            }
        }

        // Barra de navegação
        btnFila?.setOnClickListener {
            // Já está na fila
        }
        btnMeusLivros?.setOnClickListener {
            startActivity(Intent(this, MeusLivrosActivity::class.java))
        }

        btnHome?.setOnClickListener {
            startActivity(Intent(this, TelahomeActivity::class.java))
        }

        btnDesejosNav?.setOnClickListener {
            startActivity(Intent(this, ListaDesejosActivity::class.java))
        }

        btnperfil?.setOnClickListener {
            startActivity(Intent(this, MeuPerfilActivity::class.java))
        }

        findViewById<MaterialButton?>(R.id.btn_confirmar_reserva)?.setOnClickListener {
            Toast.makeText(this, "Reserva confirmada!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, TelahomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        findViewById<View?>(R.id.btn_remover_espera)?.setOnClickListener {
            Toast.makeText(this, "Removido da Lista de Espera", Toast.LENGTH_SHORT).show()
        }
    }
}
