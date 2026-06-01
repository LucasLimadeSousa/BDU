package com.example.bdu.adm

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.example.bdu.R
import com.example.bdu.livros.TelahomeActivity
import com.google.android.material.button.MaterialButton

class EditarLivroAdmActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.adm_editar_livro_adm)

        val mainView = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // Recuperar dados do Intent
        val bookTitle = intent.getStringExtra("BOOK_TITLE")
        val originalBookTitle = intent.getStringExtra("ORIGINAL_BOOK_TITLE") ?: bookTitle
        val bookAuthor = intent.getStringExtra("BOOK_AUTHOR")
        val bookGenre = intent.getStringExtra("BOOK_GENRE")
        val bookPublication = intent.getStringExtra("BOOK_PUBLICATION")
        val bookIsbn = intent.getStringExtra("BOOK_ISBN")
        val bookPublisher = intent.getStringExtra("BOOK_PUBLISHER")
        val bookPages = intent.getStringExtra("BOOK_PAGES")
        val bookSynopsis = intent.getStringExtra("BOOK_SYNOPSIS")
        val bookImage = intent.getStringExtra("BOOK_IMAGE")

        // Referenciar componentes da UI
        val ivBookCover = findViewById<ImageView>(R.id.ivBookCover)
        val etTitle = findViewById<EditText>(R.id.etTitle)
        val etAuthor = findViewById<EditText>(R.id.etAuthor)
        val etGenre = findViewById<EditText>(R.id.etGenre)
        val etSynopsis = findViewById<EditText>(R.id.etSynopsis)
        val editTextDate = findViewById<EditText>(R.id.editTextDate)
        val etPublisher = findViewById<EditText>(R.id.etPublisher)
        val etPages = findViewById<EditText>(R.id.etPages)
        val etIsbn = findViewById<EditText>(R.id.etIsbn)

        // Preencher os campos com os dados atuais
        etTitle.setText(bookTitle)
        etAuthor.setText(bookAuthor)
        etGenre.setText(bookGenre)
        etSynopsis.setText(bookSynopsis)
        editTextDate.setText(bookPublication)
        etPublisher.setText(bookPublisher)
        etPages.setText(bookPages)
        etIsbn.setText(bookIsbn)

        ivBookCover.load(bookImage) {
            placeholder(R.drawable.ic_launcher_background)
            error(R.drawable.ic_launcher_background)
        }

        findViewById<ImageButton>(R.id.btnBackLivro)?.setOnClickListener {
            finish()
        }

        val btnUpdateBook = findViewById<MaterialButton>(R.id.btnUpdateBook)
        btnUpdateBook?.setOnClickListener {
            val updatedTitle = etTitle.text.toString().trim()
            val updatedAuthor = etAuthor.text.toString().trim()
            val updatedGenre = etGenre.text.toString().trim()
            val updatedSynopsis = etSynopsis.text.toString().trim()
            val updatedDate = editTextDate.text.toString().trim()
            val updatedPublisher = etPublisher.text.toString().trim()
            val updatedPages = etPages.text.toString().trim()
            val updatedIsbn = etIsbn.text.toString().trim()

            if (updatedTitle.isEmpty()) {
                Toast.makeText(this, "O título é obrigatório.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updatedBook = CustomBook(
                title = updatedTitle,
                originalTitle = originalBookTitle,
                author = updatedAuthor,
                genre = updatedGenre,
                synopsis = updatedSynopsis,
                date = updatedDate,
                publisher = updatedPublisher,
                pages = updatedPages,
                isbn = updatedIsbn,
                image = bookImage,
                language = "pt"
            )

            // Salva as alterações no BookCatalogManager
            BookCatalogManager.updateCustomBook(this, bookTitle ?: "", originalBookTitle, updatedBook)

            Toast.makeText(this, "Livro editado com sucesso.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, TelahomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("USER_IS_ADM", true)
            startActivity(intent)
            finish()
        }
    }
}