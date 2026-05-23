package com.example.bdu.livros

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.bdu.R
import com.example.bdu.usuario.MeuPerfilActivity

class ListaDesejosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.livros_lista_desejos)

        // Encontrar o GridLayout e configurar todos os botões de coração
        val gridFavoritos = findViewById<GridLayout>(R.id.grid_favoritos)
        
        for (i in 0 until gridFavoritos.childCount) {
            val item = gridFavoritos.getChildAt(i)
            val btnCoracao = item.findViewById<ImageButton>(R.id.btn_remover_desejo)

            btnCoracao?.let { btn ->
                // Como o FUNDO do círculo agora é AZUL, o coração começa BRANCO para aparecer
                btn.imageTintList = ColorStateList.valueOf(Color.RED)
                var isCoracaoPreto = false

                // Lógica de clique (Toggle)
                btn.setOnClickListener {
                    if (isCoracaoPreto) {
                        btn.imageTintList = ColorStateList.valueOf(Color.RED)
                        isCoracaoPreto = false
                    } else {
                        btn.imageTintList = ColorStateList.valueOf(Color.WHITE)
                        isCoracaoPreto = true
                    }
                }
            }
        }

        // Navegação
        findViewById<ImageButton>(R.id.btn_nav_fila).setOnClickListener {
            startActivity(Intent(this, ListadeEsperaActivity::class.java))
        }
        findViewById<ImageButton>(R.id.btn_nav_meuslivros).setOnClickListener {
            startActivity(Intent(this, MeusLivrosActivity::class.java))
        }
        findViewById<ImageButton>(R.id.btn_nav_home).setOnClickListener {
            startActivity(Intent(this, TelahomeActivity::class.java))
        }
        findViewById<ImageButton>(R.id.btn_nav_desejos).setOnClickListener {
            // Já estamos na Lista de Desejos
        }
        findViewById<ImageButton>(R.id.btn_nav_perfil).setOnClickListener {
            startActivity(Intent(this, MeuPerfilActivity::class.java))
        }
    }
}
