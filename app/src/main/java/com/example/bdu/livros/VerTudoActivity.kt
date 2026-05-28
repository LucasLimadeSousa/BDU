package com.example.bdu.livros

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.bdu.R
import com.example.bdu.api.RetrofitInstance
import com.example.bdu.model.BookItem
import com.example.bdu.usuario.MeuPerfilActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val apiKey = "AIzaSyBiIxsUcqL9mzrxTAnSFmowMpL7e-e9ow4"

class VerTudoActivity : AppCompatActivity() {

    private var tipoLista: String = "GERAL"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.livros_ver_tudo)

        tipoLista = intent.getStringExtra("TIPO_LISTA") ?: "GERAL"
        
        val headerTitle = findViewById<TextView>(R.id.header_title)
        headerTitle.text = when(tipoLista) {
            "CURSO" -> "Livros do seu Curso"
            "ALGORITMO" -> "Recomendados para Você"
            else -> "Catálogo de Livros"
        }

        configurarInsets()
        configurarBotoes()
        carregarLivros()
    }

    private fun configurarBotoes() {
        findViewById<ImageButton>(R.id.btn_backReturn)?.setOnClickListener {
            finish()
        }

        findViewById<ImageButton>(R.id.btn_nav_fila)?.setOnClickListener {
            startActivity(Intent(this, ListadeEsperaActivity::class.java))
        }
        findViewById<ImageButton>(R.id.btn_nav_meuslivros)?.setOnClickListener {
            startActivity(Intent(this, MeusLivrosActivity::class.java))
        }
        findViewById<ImageButton>(R.id.btn_nav_home)?.setOnClickListener {
            startActivity(Intent(this, TelahomeActivity::class.java))
            finish()
        }
        findViewById<ImageButton>(R.id.btn_nav_desejos)?.setOnClickListener {
            startActivity(Intent(this, ListaDesejosActivity::class.java))
        }
        findViewById<ImageButton>(R.id.btn_nav_perfil)?.setOnClickListener {
            startActivity(Intent(this, MeuPerfilActivity::class.java))
        }
    }

    private fun carregarLivros() {
        val grid = findViewById<GridLayout>(R.id.grid_todos_livros)
        
        lifecycleScope.launch {
            try {
                val searchTerms = when(tipoLista) {
                    "CURSO" -> RecommendationManager.getCourseSearchTerms(this@VerTudoActivity)
                    "ALGORITMO" -> RecommendationManager.getGeneralSearchTerms(this@VerTudoActivity)
                    else -> listOf("programming", "fiction", "history", "science")
                }

                val allBooks = mutableListOf<BookItem>()
                val prefs = getSharedPreferences("favoritos_prefs", MODE_PRIVATE)
                val favoritesSet = prefs.getStringSet("favorites_list", emptySet()) ?: emptySet()

                // Buscamos livros para os primeiros termos da lista
                for (term in searchTerms.take(5)) {
                    val response = withContext(Dispatchers.IO) {
                        RetrofitInstance.api.searchBooks(term, apiKey)
                    }
                    response.items?.let { items ->
                        // Filtrar livros sem imagem, sem autor, repetidos ou já favoritados
                        val filtered = items.filter { item ->
                            val title = item.volumeInfo.title ?: ""
                            item.volumeInfo.imageLinks?.thumbnail != null &&
                            !item.volumeInfo.authors.isNullOrEmpty() &&
                            !favoritesSet.contains(title)
                        }
                        allBooks.addAll(filtered)
                    }
                    if (allBooks.distinctBy { it.volumeInfo.title }.size >= 30) break
                }

                val inflater = LayoutInflater.from(this@VerTudoActivity)
                val finalBooks = allBooks.distinctBy { it.volumeInfo.title }

                if (finalBooks.isEmpty()) {
                    // Feedback se não encontrar nada
                    val emptyTxt = TextView(this@VerTudoActivity)
                    emptyTxt.text = "Nenhum livro novo encontrado no momento."
                    emptyTxt.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    grid.addView(emptyTxt)
                    return@launch
                }

                finalBooks.forEach { book ->
                    val info = book.volumeInfo
                    val itemView = inflater.inflate(R.layout.item_book_home, grid, false)
                    
                    val params = GridLayout.LayoutParams()
                    params.width = 0
                    params.height = GridLayout.LayoutParams.WRAP_CONTENT
                    params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f)
                    params.setMargins(8, 8, 8, 8)
                    itemView.layoutParams = params

                    val img = itemView.findViewById<ImageView>(R.id.imgLivro)
                    val txtTitulo = itemView.findViewById<TextView>(R.id.tituloLivro)
                    val txtAutor = itemView.findViewById<TextView>(R.id.autorLivro)

                    txtTitulo.text = info.title
                    txtAutor.text = info.authors?.get(0)
                    
                    val imageUri = info.imageLinks?.thumbnail?.replace("http://", "https://")
                    img.load(imageUri) {
                        crossfade(true)
                        placeholder(R.drawable.ic_launcher_background)
                        error(R.drawable.ic_launcher_background)
                    }

                    itemView.setOnClickListener {
                        val intent = Intent(this@VerTudoActivity, PaginaDoLivroActivity::class.java)
                        intent.putExtra("BOOK_TITLE", info.title)
                        intent.putExtra("BOOK_AUTHOR", info.authors?.joinToString(", "))
                        intent.putExtra("BOOK_GENRE", info.categories?.joinToString(", "))
                        intent.putExtra("BOOK_PUBLICATION", info.publishedDate)
                        intent.putExtra("BOOK_ISBN", info.industryIdentifiers?.firstOrNull { it.type == "ISBN_13" }?.identifier
                            ?: info.industryIdentifiers?.firstOrNull { it.type == "ISBN_10" }?.identifier)
                        intent.putExtra("BOOK_PUBLISHER", info.publisher)
                        intent.putExtra("BOOK_PAGES", info.pageCount?.toString())
                        intent.putExtra("BOOK_SYNOPSIS", info.description)
                        intent.putExtra("BOOK_IMAGE", imageUri)
                        
                        // Algoritmo: Aprende com o clique
                        info.title?.let { RecommendationManager.addInterest(this@VerTudoActivity, it) }
                        
                        startActivity(intent)
                    }

                    grid.addView(itemView)
                }

                // Ajuste de colunas
                val faltam = (3 - (finalBooks.size % 3)) % 3
                for (i in 0 until faltam) {
                    val spacer = View(this@VerTudoActivity)
                    val params = GridLayout.LayoutParams()
                    params.width = 0
                    params.height = 0
                    params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f)
                    spacer.layoutParams = params
                    grid.addView(spacer)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
