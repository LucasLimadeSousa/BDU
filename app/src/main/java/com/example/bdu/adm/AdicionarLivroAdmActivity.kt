package com.example.bdu.adm

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.bdu.R
import com.example.bdu.api.RetrofitInstance
import com.example.bdu.livros.TelahomeActivity
import com.example.bdu.model.BookItem
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdicionarLivroAdmActivity : AppCompatActivity() {

    private var searchJob: Job? = null
    private var selectedBookImage: String? = null
    private lateinit var rvSuggestions: RecyclerView
    private lateinit var adapter: SuggestionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.adm_adicionar_livro_adm)

        configurarLayout()
        configurarBuscaEPreenchimento()
        configurarBotaoAdicionar()
    }

    private fun configurarLayout() {
        val mainLayout = findViewById<View>(R.id.main)
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<View>(R.id.btnBackLivro).setOnClickListener {
            finish()
        }
    }

    private fun configurarBuscaEPreenchimento() {
        val etTitle = findViewById<EditText>(R.id.etTitle)
        rvSuggestions = findViewById(R.id.rvTitleSuggestions)

        rvSuggestions.layoutManager = LinearLayoutManager(this)
        adapter = SuggestionsAdapter { book ->
            preencherCamposComLivro(book)
            rvSuggestions.visibility = View.GONE
        }
        rvSuggestions.adapter = adapter

        etTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchJob?.cancel()
                if (s.isNullOrBlank() || s.length < 3) {
                    rvSuggestions.visibility = View.GONE
                    return
                }

                searchJob = lifecycleScope.launch {
                    delay(500)
                    buscarSugestoes(s.toString())
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private suspend fun buscarSugestoes(query: String) {
        try {
            val response = withContext(Dispatchers.IO) {
                RetrofitInstance.api.searchBooks(query, RetrofitInstance.API_KEY, 5)
            }
            val items = response.items ?: emptyList()
            withContext(Dispatchers.Main) {
                if (items.isNotEmpty()) {
                    adapter.setItems(items)
                    rvSuggestions.visibility = View.VISIBLE
                } else {
                    rvSuggestions.visibility = View.GONE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun preencherCamposComLivro(book: BookItem) {
        val info = book.volumeInfo
        findViewById<EditText>(R.id.etTitle).setText(info.title)
        findViewById<EditText>(R.id.etAuthor).setText(info.authors?.joinToString(", "))
        findViewById<EditText>(R.id.etGenre).setText(info.categories?.joinToString(", "))
        findViewById<EditText>(R.id.etSynopsis).setText(info.description)
        findViewById<EditText>(R.id.editTextDate).setText(info.publishedDate)
        findViewById<EditText>(R.id.etPublisher).setText(info.publisher)
        findViewById<EditText>(R.id.etPages).setText(info.pageCount?.toString())
        findViewById<EditText>(R.id.etLanguage).setText(info.language)
        findViewById<EditText>(R.id.etIsbn).setText(info.industryIdentifiers?.firstOrNull()?.identifier)

        selectedBookImage = info.imageLinks?.thumbnail?.replace("http://", "https://")
        findViewById<ImageView>(R.id.ivBookCover).load(selectedBookImage) {
            placeholder(R.drawable.ic_launcher_background)
            error(R.drawable.ic_launcher_background)
        }
    }

    private fun configurarBotaoAdicionar() {
        val btnAddBook = findViewById<MaterialButton>(R.id.btnAddBook)
        btnAddBook.setOnClickListener {
            val title = findViewById<EditText>(R.id.etTitle).text.toString()

            if (title.isBlank()) {
                Toast.makeText(this, "Por favor, insira o título do livro", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val builder = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
            builder.setMessage("Deseja adicionar o livro '$title' ao catálogo oficial?")

            builder.setPositiveButton("Adicionar") { _, _ ->
                val newBook = CustomBook(
                    title = title,
                    author = findViewById<EditText>(R.id.etAuthor).text.toString(),
                    genre = findViewById<EditText>(R.id.etGenre).text.toString(),
                    synopsis = findViewById<EditText>(R.id.etSynopsis).text.toString(),
                    date = findViewById<EditText>(R.id.editTextDate).text.toString(),
                    publisher = findViewById<EditText>(R.id.etPublisher).text.toString(),
                    pages = findViewById<EditText>(R.id.etPages).text.toString(),
                    language = findViewById<EditText>(R.id.etLanguage).text.toString(),
                    isbn = findViewById<EditText>(R.id.etIsbn).text.toString(),
                    image = selectedBookImage
                )

                BookCatalogManager.addCustomBook(this, newBook)

                Toast.makeText(this, "Livro adicionado com sucesso!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, TelahomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }

            builder.setNegativeButton("Cancelar", null)
            val dialog = builder.create()
            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.let {
                it.setBackgroundColor("#2E7D32".toColorInt())
                it.setTextColor(Color.WHITE)
            }
        }
    }

    class SuggestionsAdapter(private val onClick: (BookItem) -> Unit) :
        RecyclerView.Adapter<SuggestionsAdapter.ViewHolder>() {

        private var items: List<BookItem> = emptyList()

        fun setItems(newItems: List<BookItem>) {
            this.items = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.text.text = item.volumeInfo.title
            holder.itemView.setOnClickListener { onClick(item) }
        }

        override fun getItemCount() = items.size

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val text: TextView = view.findViewById(android.R.id.text1)
        }
    }
}