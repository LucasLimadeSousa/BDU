package com.example.bdu.livros

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.bdu.R
import com.example.bdu.usuario.MeuPerfilActivity
import com.example.bdu.adm.TelaPaginaDoLivroAdmActivity
import com.example.bdu.adm.AdicionarLivroAdmActivity
import com.example.bdu.network.SupabaseConfig
import com.google.android.material.button.MaterialButton
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import Usuario

class TelahomeActivity : AppCompatActivity() {
    private var isAdm: Boolean = false
    private var btnAdicionarLivro: MaterialButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.livros_telahome)

        btnAdicionarLivro = findViewById(R.id.btnAdicionarLivro)
        
        // Tenta pegar o valor inicial que veio do login (pra ser rápido)
        isAdm = intent.getBooleanExtra("USER_IS_ADM", false)
        atualizarInterfaceAdm()

        // Mas também confirma com o banco de dados para não "esquecer" ao voltar
        verificarStatusAdm()

        val btnsearch = findViewById<LinearLayout?>(R.id.search_container)
        val btnvertudo = findViewById<TextView?>(R.id.VerTudo)
        val btnvertudo2 = findViewById<TextView?>(R.id.VerTudo2)
        val btnvertudo3 = findViewById<TextView?>(R.id.VerTudo3)

        val btnFila = findViewById<ImageButton?>(R.id.btn_nav_fila)
        val btnMeusLivros = findViewById<ImageButton?>(R.id.btn_nav_meuslivros)
        val btnHome = findViewById<ImageButton?>(R.id.btn_nav_home)
        val btnDesejos = findViewById<ImageButton?>(R.id.btn_nav_desejos)
        val btnperfil = findViewById<ImageButton?>(R.id.btn_nav_perfil)

        // Botões de livros
        val livrosIds = listOf(
            R.id.item_livro_a, R.id.item_livro_b, R.id.item_livro_c, R.id.item_livro_d, R.id.item_livro_e,
            R.id.item_livro_1, R.id.item_livro_2, R.id.item_livro_3, R.id.item_livro_4, R.id.item_livro_5,
            R.id.item_livro_10, R.id.item_livro_20, R.id.item_livro_30, R.id.item_livro_40, R.id.item_livro_50
        )

        livrosIds.forEach { id ->
            findViewById<View>(id)?.setOnClickListener {
                val destino = if (isAdm) TelaPaginaDoLivroAdmActivity::class.java else PaginaDoLivroActivity::class.java
                val intent = Intent(this, destino)
                
                if (id == R.id.item_livro_b) {
                    intent.putExtra("IS_ESGOTADO", true)
                    intent.putExtra("BOOK_TITLE", "Orgulho e Preconceito")
                }
                
                startActivity(intent)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnsearch?.setOnClickListener {
            startActivity(Intent(this, BuscaActivity::class.java))
        }

        btnvertudo?.setOnClickListener { startActivity(Intent(this, VerTudoActivity::class.java)) }
        btnvertudo2?.setOnClickListener { startActivity(Intent(this, VerTudoActivity::class.java)) }
        btnvertudo3?.setOnClickListener { startActivity(Intent(this, VerTudoActivity::class.java)) }

        btnFila?.setOnClickListener { startActivity(Intent(this, ListadeEsperaActivity::class.java)) }
        btnMeusLivros?.setOnClickListener { startActivity(Intent(this, MeusLivrosActivity::class.java)) }
        
        btnHome?.setOnClickListener {
            val intent = Intent(this, TelahomeActivity::class.java)
            intent.putExtra("USER_IS_ADM", isAdm)
            startActivity(intent)
        }

        btnDesejos?.setOnClickListener { startActivity(Intent(this, ListaDesejosActivity::class.java)) }
        btnperfil?.setOnClickListener { startActivity(Intent(this, MeuPerfilActivity::class.java)) }

        btnAdicionarLivro?.setOnClickListener {
            startActivity(Intent(this, AdicionarLivroAdmActivity::class.java))
        }
    }

    private fun atualizarInterfaceAdm() {
        if (isAdm) {
            btnAdicionarLivro?.visibility = View.VISIBLE
        } else {
            btnAdicionarLivro?.visibility = View.GONE
        }
    }

    private fun verificarStatusAdm() {
        val userEmail = SupabaseConfig.client.auth.currentSessionOrNull()?.user?.email
        if (userEmail != null) {
            lifecycleScope.launch {
                try {
                    val usuario = SupabaseConfig.client.from("Dados_Usuario")
                        .select {
                            filter {
                                eq("email", userEmail)
                            }
                        }.decodeSingle<Usuario>()
                    
                    isAdm = usuario.adm
                    atualizarInterfaceAdm()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}