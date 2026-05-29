package com.example.bdu.livros

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.example.bdu.R
import com.example.bdu.usuario.MeuPerfilActivity
import com.google.android.material.button.MaterialButton

class ListadeEsperaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.livros_lista_de_espera)

        configurarNavegacao()
        carregarListaEspera()
    }

    private fun carregarListaEspera() {
        val container = findViewById<LinearLayout>(R.id.container_lista_espera)
        container.removeAllViews()

        val waitlist = WaitlistManager.getWaitlist(this)
        val inflater = LayoutInflater.from(this)

        if (waitlist.isEmpty()) {
            val emptyTxt = TextView(this)
            emptyTxt.text = "Sua lista de espera está vazia."
            emptyTxt.textAlignment = View.TEXT_ALIGNMENT_CENTER
            emptyTxt.setPadding(0, 100, 0, 0)
            container.addView(emptyTxt)
            return
        }

        waitlist.forEach { item ->
            val layoutId = if (item.isAvailable) R.layout.item_waitlist_available else R.layout.item_waitlist_waiting
            val itemView = inflater.inflate(layoutId, container, false)

            itemView.findViewById<TextView>(R.id.tv_waitlist_title).text = item.title
            itemView.findViewById<ImageView>(R.id.iv_waitlist_cover).load(item.image) {
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_background)
            }
            itemView.findViewById<TextView>(R.id.tv_waitlist_position).text = "Posição na lista de espera: ${item.position}"

            if (!item.isAvailable) {
                itemView.findViewById<TextView>(R.id.tv_waitlist_date).text = "Previsão de disponibilidade: ${item.date}"
                
                val btnMoverDesejos = itemView.findViewById<MaterialButton>(R.id.btn_mover_desejos)
                btnMoverDesejos.setOnClickListener {
                    WishlistManager.addToWishlist(this, item)
                    Toast.makeText(this, "Adicionado à Lista de Desejos", Toast.LENGTH_SHORT).show()
                }

                itemView.findViewById<MaterialButton>(R.id.btn_remover_espera).setOnClickListener {
                    WaitlistManager.removeFromWaitlist(this, item.title)
                    carregarListaEspera()
                    Toast.makeText(this, "Removido da Lista de Espera", Toast.LENGTH_SHORT).show()
                }
            } else {
                itemView.findViewById<MaterialButton>(R.id.btn_confirmar_reserva).setOnClickListener {
                    WaitlistManager.removeFromWaitlist(this, item.title)
                    // Aqui poderia alugar de fato
                    Toast.makeText(this, "Reserva confirmada!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MeusLivrosActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            container.addView(itemView)
        }
    }

    private fun configurarNavegacao() {
        findViewById<ImageButton?>(R.id.btn_nav_fila)?.setOnClickListener { }
        findViewById<ImageButton?>(R.id.btn_nav_meuslivros)?.setOnClickListener {
            startActivity(Intent(this, MeusLivrosActivity::class.java))
        }
        findViewById<ImageButton?>(R.id.btn_nav_home)?.setOnClickListener {
            startActivity(Intent(this, TelahomeActivity::class.java))
        }
        findViewById<ImageButton?>(R.id.btn_nav_desejos)?.setOnClickListener {
            startActivity(Intent(this, ListaDesejosActivity::class.java))
        }
        findViewById<ImageButton?>(R.id.btn_nav_perfil)?.setOnClickListener {
            startActivity(Intent(this, MeuPerfilActivity::class.java))
        }

        val mainView = findViewById<View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }
}
