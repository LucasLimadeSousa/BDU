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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val apiKey = "AIzaSyCEr7bp6m5SGwLrOGSihqN5tmwkAbLogxU"

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
            intent.putExtra("BOOK_TITLE", livro.volumeInfo.title)
            intent.putExtra("IS_ESGOTADO", false) 
            startActivity(intent)
        }
        recyclerViewSugestoes.layoutManager = LinearLayoutManager(this)
        recyclerViewSugestoes.adapter = adapter

        searchViewBusca.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchJob?.cancel()
                if (!newText.isNullOrBlank()) {
                    searchJob = lifecycleScope.launch {
                        delay(500) // Debounce
                        buscarLivros(newText)
                    }
                } else {
                    recyclerViewSugestoes.visibility = View.GONE
                }
                return true
            }
        })

        findViewById<ImageButton>(R.id.btn_backReturn)?.setOnClickListener {
            finish()
        }
        btnreturn?.setOnClickListener {
            val intent = Intent(this, TelahomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private suspend fun buscarLivros(query: String) {
        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitInstance.api.searchBooks(query, apiKey)
            }
            val livros = response.items ?: emptyList()
            withContext(Dispatchers.Main) {
                if (livros.isNotEmpty()) {
                    adapter.setLivros(livros)
                    recyclerViewSugestoes.visibility = View.VISIBLE
                } else {
                    recyclerViewSugestoes.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                recyclerViewSugestoes.visibility = View.GONE
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
