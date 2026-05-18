package com.example.bdu.livros

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.example.bdu.R
import com.example.bdu.adm.AdicionarLivroAdmActivity
import com.example.bdu.api.RetrofitInstance
import com.example.bdu.usuario.MeuPerfilActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.delay

private val apiKey = "AIzaSyCEr7bp6m5SGwLrOGSihqN5tmwkAbLogxU"

class TelahomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.livros_telahome)

        // --- Função de Carregamento Seguro ---
        fun carregarLivroSeguro(idInclude: Int, query: String, delayMillis: Long = 0) {
            val container = findViewById<LinearLayout>(idInclude)
            val img = container.findViewById<ImageView>(R.id.imgLivro)
            val txtTitulo = container.findViewById<TextView>(R.id.tituloLivro)
            val txtAutor = container.findViewById<TextView>(R.id.autorLivro)

            txtTitulo.text = "Carregando..."

            lifecycleScope.launch {
                try {
                    if (delayMillis > 0) delay(delayMillis)

                    val resposta = withContext(Dispatchers.IO) {
                        RetrofitInstance.api.searchBooks(query, apiKey)
                    }

                    val livro = resposta.items?.firstOrNull { it.volumeInfo.imageLinks?.thumbnail != null }

                    if (livro != null) {
                        txtTitulo.text = livro.volumeInfo.title ?: "Sem título"
                        txtAutor.text = livro.volumeInfo.authors?.getOrNull(0) ?: "Autor desconhecido"
                        
                        val imagem = livro.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")
                        img.load(imagem) {
                            crossfade(true)
                            placeholder(R.drawable.ic_launcher_background)
                            error(R.drawable.ic_launcher_background)
                        }
                    } else {
                        txtTitulo.text = "Não encontrado"
                    }

                } catch (e: Exception) {
                    txtTitulo.text = "Erro"
                    e.printStackTrace()
                }
            }
        }

        // --- Chamadas dos Livros ---

        // Lista de Desejos
        carregarLivroSeguro(R.id.item_livro_a, "intitle:Harry Potter Rowling", 0)
        carregarLivroSeguro(R.id.item_livro_b, "intitle:Dom Casmurro", 200)
        carregarLivroSeguro(R.id.item_livro_c, "intitle:Invincible Kirkman", 400)
        carregarLivroSeguro(R.id.item_livro_d, "intitle:Java Como Programar Deitel", 600)
        carregarLivroSeguro(R.id.item_livro_e, "intitle:Java Efetivo Joshua Bloch", 800)

        // Livros Populares do Curso
        carregarLivroSeguro(R.id.item_livro_1, "intitle:Estrutura de Dados", 1000)
        carregarLivroSeguro(R.id.item_livro_2, "intitle:Arquitetura de Software", 1200)
        carregarLivroSeguro(R.id.item_livro_3, "intitle:Engenharia de Dados", 1400)
        carregarLivroSeguro(R.id.item_livro_4, "intitle:JavaScript Guia Definitivo", 1600)
        carregarLivroSeguro(R.id.item_livro_5, "intitle:Linguagem SQL", 1800)

        // Livros Populares
        carregarLivroSeguro(R.id.item_livro_10, "intitle:Pense em Python", 2000)
        carregarLivroSeguro(R.id.item_livro_20, "intitle:Netter Atlas Anatomia", 2200)
        carregarLivroSeguro(R.id.item_livro_30, "intitle:Clean Code", 2400)
        carregarLivroSeguro(R.id.item_livro_40, "intitle:Design Patterns", 2600)
        carregarLivroSeguro(R.id.item_livro_50, "intitle:Refactoring", 2800)

        // Transições

        val btnsearch =
            findViewById<LinearLayout>(R.id.search_container)

        val btnADM =
            findViewById<Button>(R.id.btnADM)

        val btnvertudo =
            findViewById<TextView>(R.id.VerTudo)

        val btnvertudo2 =
            findViewById<TextView>(R.id.VerTudo2)

        val btnvertudo3 =
            findViewById<TextView>(R.id.VerTudo3)

        val btnFila =
            findViewById<ImageButton>(R.id.btn_nav_fila)

        val btnMeusLivros =
            findViewById<ImageButton>(R.id.btn_nav_meuslivros)

        val btnHome =
            findViewById<ImageButton>(R.id.btn_nav_home)

        val btnDesejos =
            findViewById<ImageButton>(R.id.btn_nav_desejos)

        val btnperfil =
            findViewById<ImageButton>(R.id.btn_nav_perfil)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->

            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        // =========================
        // PESQUISA
        // =========================

        btnsearch.setOnClickListener {

            val intent =
                Intent(this, BuscaActivity::class.java)

            startActivity(intent)
        }

        // =========================
        // VER TUDO
        // =========================

        btnvertudo.setOnClickListener {

            val intent =
                Intent(this, VerTudoActivity::class.java)

            startActivity(intent)
        }

        btnvertudo2.setOnClickListener {

            val intent =
                Intent(this, VerTudoActivity::class.java)

            startActivity(intent)
        }

        btnvertudo3.setOnClickListener {

            val intent =
                Intent(this, VerTudoActivity::class.java)

            startActivity(intent)
        }

        // =========================
        // NAVEGAÇÃO
        // =========================

        btnFila.setOnClickListener {

            val intent =
                Intent(this, ListadeEsperaActivity::class.java)

            startActivity(intent)
        }

        btnMeusLivros.setOnClickListener {

            val intent =
                Intent(this, MeusLivrosActivity::class.java)

            startActivity(intent)
        }

        btnHome.setOnClickListener {

            val intent =
                Intent(this, TelahomeActivity::class.java)

            startActivity(intent)
        }

        btnDesejos.setOnClickListener {

            val intent =
                Intent(this, ListaDesejosActivity::class.java)

            startActivity(intent)
        }

        btnperfil.setOnClickListener {

            val intent =
                Intent(this, MeuPerfilActivity::class.java)

            startActivity(intent)
        }

        // =========================
        // ADM
        // =========================

        btnADM.setOnClickListener {

            val intent =
                Intent(this, AdicionarLivroAdmActivity::class.java)

            startActivity(intent)
        }
    }
}