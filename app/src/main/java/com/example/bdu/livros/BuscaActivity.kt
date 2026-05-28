package com.example.bdu.livros

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bdu.R
import com.example.bdu.api.RetrofitInstance
import com.example.bdu.model.BookItem
import com.example.bdu.usuario.MeuPerfilActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val apiKey = "AIzaSyB0MZ-X3tR51eJW1GOTcN6v57tzojhIvw8"

class BuscaActivity : AppCompatActivity() {

    private var searchJob: Job? = null
    private lateinit var adapter: SugestoesAdapter
    private lateinit var recyclerViewSugestoes: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.livros_busca)

        val btnreturn = findViewById<ImageButton?>(R.id.btn_backReturn)
        val searchViewBusca = findViewById<SearchView>(R.id.searchViewBusca)
        recyclerViewSugestoes = findViewById(R.id.recyclerViewSugestoes)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = SugestoesAdapter { livro ->
            val intent = Intent(this, PaginaDoLivroActivity::class.java)
            val info = livro.volumeInfo
            intent.putExtra("BOOK_TITLE", info.title)
            intent.putExtra("BOOK_AUTHOR", info.authors?.joinToString(", "))
            intent.putExtra("BOOK_GENRE", info.categories?.joinToString(", "))
            intent.putExtra("BOOK_PUBLICATION", info.publishedDate)
            intent.putExtra("BOOK_ISBN", info.industryIdentifiers?.firstOrNull { it.type == "ISBN_13" }?.identifier
                ?: info.industryIdentifiers?.firstOrNull { it.type == "ISBN_10" }?.identifier)
            intent.putExtra("BOOK_PUBLISHER", info.publisher)
            intent.putExtra("BOOK_PAGES", info.pageCount?.toString())
            intent.putExtra("BOOK_SYNOPSIS", info.description)
            intent.putExtra("BOOK_IMAGE", info.imageLinks?.thumbnail?.replace("http://", "https://"))
            intent.putExtra("IS_ESGOTADO", false)
            
            // Algoritmo: Aprende com o livro que o usuário escolheu ver
            info.title?.let { RecommendationManager.addInterest(this, it) }
            info.categories?.firstOrNull()?.let { RecommendationManager.addInterest(this, it) }
            
            startActivity(intent)
        }
        recyclerViewSugestoes.layoutManager = LinearLayoutManager(this)
        recyclerViewSugestoes.adapter = adapter

        val btnAplicar = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnAplicarFiltros)

        searchViewBusca.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    RecommendationManager.addInterest(this@BuscaActivity, query)
                }
                buscarComFiltros()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(600)
                    buscarComFiltros()
                }
                return true
            }
        })

        btnAplicar?.setOnClickListener {
            val query = searchViewBusca.query.toString()
            if (query.isNotBlank()) {
                RecommendationManager.addInterest(this, query)
            }
            buscarComFiltros()
        }

        btnreturn?.setOnClickListener {
            val intent = Intent(this, TelahomeActivity::class.java)
            startActivity(intent)
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
        }

        findViewById<ImageButton>(R.id.btn_nav_desejos)?.setOnClickListener {
            startActivity(Intent(this, ListaDesejosActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btn_nav_perfil)?.setOnClickListener {
            startActivity(Intent(this, MeuPerfilActivity::class.java))
        }
    }

    private fun buscarComFiltros() {
        val searchViewBusca = findViewById<SearchView>(R.id.searchViewBusca)
        val queryText = searchViewBusca.query.toString()
        
        val spinnerGenero = findViewById<android.widget.Spinner>(R.id.spinnerGenero)
        val editTextData = findViewById<android.widget.EditText>(R.id.editTextData)
        val spinnerCurso = findViewById<android.widget.Spinner>(R.id.spinnerCursoBusca)
        
        val genero = spinnerGenero?.selectedItem?.toString() ?: ""
        val ano = editTextData?.text?.toString() ?: ""
        val curso = spinnerCurso?.selectedItem?.toString() ?: ""

        if (queryText.isBlank() && (genero == "Todos os Gêneros" || genero == "") && ano.isBlank() && (curso == "Todos os Cursos" || curso == "")) {
            recyclerViewSugestoes.visibility = View.GONE
            return
        }

        lifecycleScope.launch {
            try {
                var apiQuery = ""
                
                if (queryText.isNotBlank()) {
                    apiQuery += "intitle:\"$queryText\""
                }
                
                if (genero != "Todos os Gêneros" && genero.isNotBlank()) {
                    if (apiQuery.isNotBlank()) apiQuery += "+"
                    apiQuery += "subject:\"$genero\""
                    RecommendationManager.addInterest(this@BuscaActivity, genero)
                }

                if (curso != "Todos os Cursos" && curso.isNotBlank()) {
                    if (apiQuery.isNotBlank()) apiQuery += "+"
                    apiQuery += "\"$curso\""
                    RecommendationManager.addInterest(this@BuscaActivity, curso)
                }

                if (ano.isNotBlank()) {
                    if (apiQuery.isBlank()) apiQuery = "publishedDate:$ano"
                    else apiQuery += "+inpublisher:$ano"
                }
                
                if (apiQuery.isBlank()) apiQuery = "livros"

                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.api.searchBooks(apiQuery, apiKey)
                }

                val originalResults = response.items ?: emptyList()
                
                val filteredResults = originalResults.filter { book ->
                    val title = book.volumeInfo.title ?: ""
                    val matchesText = queryText.isBlank() || title.contains(queryText, ignoreCase = true)
                    val matchesYear = ano.isBlank() || (book.volumeInfo.publishedDate?.contains(ano) == true)
                    matchesText && matchesYear
                }

                withContext(Dispatchers.Main) {
                    if (filteredResults.isNotEmpty()) {
                        adapter.setLivros(filteredResults)
                        recyclerViewSugestoes.visibility = View.VISIBLE
                    } else {
                        if (originalResults.isNotEmpty() && queryText.length <= 2) {
                            adapter.setLivros(originalResults.take(5))
                            recyclerViewSugestoes.visibility = View.VISIBLE
                        } else {
                            recyclerViewSugestoes.visibility = View.GONE
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    class SugestoesAdapter(private val onClick: (BookItem) -> Unit) :
        RecyclerView.Adapter<SugestoesAdapter.ViewHolder>() {

        private var livros: List<BookItem> = emptyList()

        fun setLivros(novosLivros: List<BookItem>) {
            this.livros = novosLivros
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sugestao, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val livro = livros[position]
            holder.textView.text = livro.volumeInfo.title
            holder.itemView.setOnClickListener { onClick(livro) }
        }

        override fun getItemCount() = livros.size

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView = view.findViewById(R.id.textSugestao)
        }
    }
}