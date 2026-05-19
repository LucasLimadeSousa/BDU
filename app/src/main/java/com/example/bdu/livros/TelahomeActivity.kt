package com.example.bdu.livros

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.bdu.R
import com.example.bdu.api.RetrofitInstance
import com.example.bdu.usuario.MeuPerfilActivity
import com.example.bdu.adm.TelaPaginaDoLivroAdmActivity
import com.example.bdu.adm.AdicionarLivroAdmActivity
import com.example.bdu.network.SupabaseConfig
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import Usuario

private const val apiKey = "AIzaSyCEr7bp6m5SGwLrOGSihqN5tmwkAbLogxU"

class TelahomeActivity : AppCompatActivity() {

    private var isAdm: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.livros_telahome)

        isAdm = intent.getBooleanExtra("USER_IS_ADM", false)
        verificarStatusAdm()

        configurarInsets()
        configurarBotoes()
        configurarCliquesLivros()
        carregarLivrosHome()
    }

    private fun verificarStatusAdm() {
        val userEmail = SupabaseConfig.client.auth.currentSessionOrNull()?.user?.email
        if (userEmail != null) {
            lifecycleScope.launch {
                try {
                    val usuario = SupabaseConfig.client.from("Dados_Usuario")
                        .select { filter { eq("email", userEmail) } }.decodeSingle<Usuario>()
                    isAdm = usuario.adm
                    atualizarVisibilidadeAdm()
                    configurarCliquesLivros()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun atualizarVisibilidadeAdm() {
        findViewById<View?>(R.id.btnAdicionarLivro)?.visibility = if (isAdm) View.VISIBLE else View.GONE
    }

    private fun carregarLivroSeguro(
        idInclude: Int,
        query: String,
        delayMillis: Long = 0
    ) {
        val container = findViewById<LinearLayout>(idInclude) ?: return
        val img = container.findViewById<ImageView>(R.id.imgLivro)
        val txtTitulo = container.findViewById<TextView>(R.id.tituloLivro)
        val txtAutor = container.findViewById<TextView>(R.id.autorLivro)

        txtTitulo.text = "Carregando..."
        txtAutor.text = ""

        lifecycleScope.launch {
            try {
                if (delayMillis > 0) delay(delayMillis)

                var tentativa = 0
                var sucesso = false

                while (tentativa < 3 && !sucesso) {
                    try {
                        val resposta = withContext(Dispatchers.IO) {
                            RetrofitInstance.api.searchBooks(query, apiKey)
                        }

                        val livro = resposta.items?.firstOrNull {
                            it.volumeInfo.imageLinks?.thumbnail != null
                        }

                        if (livro != null) {
                            txtTitulo.text = livro.volumeInfo.title ?: "Sem título"
                            txtAutor.text =
                                livro.volumeInfo.authors?.getOrNull(0) ?: "Autor desconhecido"

                            val imagem = livro.volumeInfo.imageLinks
                                ?.thumbnail
                                ?.replace("http://", "https://")

                            img.load(imagem) {
                                crossfade(true)
                                placeholder(R.drawable.ic_launcher_background)
                                error(R.drawable.ic_launcher_background)
                            }
                            sucesso = true
                        } else {
                            tentativa++
                            if (tentativa == 3) {
                                txtTitulo.text = "Não encontrado"
                            } else {
                                delay(1000)
                            }
                        }
                    } catch (e: Exception) {
                        tentativa++
                        if (tentativa >= 3) {
                            txtTitulo.text = "Erro"
                            e.printStackTrace()
                        } else {
                            delay(1500)
                        }
                    }
                }
            } catch (e: Exception) {
                txtTitulo.text = "Erro"
                e.printStackTrace()
            }
        }
    }

    private fun carregarLivrosHome() {
        carregarLivroSeguro(R.id.item_livro_a, "intitle:Harry Potter Rowling", 0)
        carregarLivroSeguro(R.id.item_livro_b, "intitle:Dom Casmurro Machado", 500)
        carregarLivroSeguro(R.id.item_livro_c, "intitle:Invincible Robert Kirkman", 1000)
        carregarLivroSeguro(R.id.item_livro_d, "intitle:Java Como Programar Deitel", 1500)
        carregarLivroSeguro(R.id.item_livro_e, "intitle:Java Efetivo Joshua Bloch", 2000)

        carregarLivroSeguro(R.id.item_livro_1, "intitle:Estrutura de Dados", 2500)
        carregarLivroSeguro(R.id.item_livro_2, "intitle:Arquitetura de Software", 3000)
        carregarLivroSeguro(R.id.item_livro_3, "intitle:Engenharia de Dados", 3500)
        carregarLivroSeguro(R.id.item_livro_4, "intitle:JavaScript Guia Definitivo", 4000)
        carregarLivroSeguro(R.id.item_livro_5, "intitle:Linguagem SQL", 4500)

        carregarLivroSeguro(R.id.item_livro_10, "intitle:Pense em Python", 5000)
        carregarLivroSeguro(R.id.item_livro_20, "intitle:Netter Atlas Anatomia", 5500)
        carregarLivroSeguro(R.id.item_livro_30, "intitle:Clean Code Martin", 6000)
        carregarLivroSeguro(R.id.item_livro_40, "intitle:Design Patterns", 6500)
        carregarLivroSeguro(R.id.item_livro_50, "intitle:Refactoring Fowler", 7000)
    }

    private fun configurarCliquesLivros() {
        val idsLivros = listOf(
            R.id.item_livro_a, R.id.item_livro_b, R.id.item_livro_c, R.id.item_livro_d, R.id.item_livro_e,
            R.id.item_livro_1, R.id.item_livro_2, R.id.item_livro_3, R.id.item_livro_4, R.id.item_livro_5,
            R.id.item_livro_10, R.id.item_livro_20, R.id.item_livro_30, R.id.item_livro_40, R.id.item_livro_50
        )

        val clickLivro = View.OnClickListener { v ->
            val destino = if (isAdm) TelaPaginaDoLivroAdmActivity::class.java else PaginaDoLivroActivity::class.java
            val intent = Intent(this, destino)
            if (v.id == R.id.item_livro_b) {
                intent.putExtra("IS_ESGOTADO", true)
                intent.putExtra("BOOK_TITLE", "Dom Casmurro")
            }
            startActivity(intent)
        }

        idsLivros.forEach { id ->
            findViewById<View?>(id)?.setOnClickListener(clickLivro)
        }
    }

    private fun configurarBotoes() {
        findViewById<LinearLayout?>(R.id.search_container)?.setOnClickListener {
            startActivity(Intent(this, BuscaActivity::class.java))
        }

        findViewById<TextView?>(R.id.VerTudo)?.setOnClickListener {
            startActivity(Intent(this, VerTudoActivity::class.java))
        }
        findViewById<TextView?>(R.id.VerTudo2)?.setOnClickListener {
            startActivity(Intent(this, VerTudoActivity::class.java))
        }
        findViewById<TextView?>(R.id.VerTudo3)?.setOnClickListener {
            startActivity(Intent(this, VerTudoActivity::class.java))
        }

        findViewById<ImageButton?>(R.id.btn_nav_fila)?.setOnClickListener {
            startActivity(Intent(this, ListadeEsperaActivity::class.java))
        }
        findViewById<ImageButton?>(R.id.btn_nav_meuslivros)?.setOnClickListener {
            startActivity(Intent(this, MeusLivrosActivity::class.java))
        }
        findViewById<ImageButton?>(R.id.btn_nav_home)?.setOnClickListener {
            val intent = Intent(this, TelahomeActivity::class.java)
            intent.putExtra("USER_IS_ADM", isAdm)
            startActivity(intent)
            finish()
        }
        findViewById<ImageButton?>(R.id.btn_nav_desejos)?.setOnClickListener {
            startActivity(Intent(this, ListaDesejosActivity::class.java))
        }
        findViewById<ImageButton?>(R.id.btn_nav_perfil)?.setOnClickListener {
            startActivity(Intent(this, MeuPerfilActivity::class.java))
        }

        findViewById<View?>(R.id.btnAdicionarLivro)?.setOnClickListener {
            startActivity(Intent(this, AdicionarLivroAdmActivity::class.java))
        }

        atualizarVisibilidadeAdm()
    }

    private fun configurarInsets() {
        findViewById<View?>(R.id.main)?.let { mainView ->
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }
}
