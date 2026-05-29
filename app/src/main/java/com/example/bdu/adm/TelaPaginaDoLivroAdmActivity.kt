package com.example.bdu.adm

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bdu.R
import com.example.bdu.livros.TelahomeActivity
import com.example.bdu.livros.ListadeEsperaActivity
import com.example.bdu.livros.MeusLivrosActivity
import com.example.bdu.livros.ListaDesejosActivity
import com.example.bdu.livros.RecommendationManager
import com.example.bdu.livros.RentalManager
import com.example.bdu.livros.WaitlistManager
import com.example.bdu.livros.WaitlistItem
import com.example.bdu.usuario.MeuPerfilActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.core.graphics.toColorInt
import coil.load

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class TelaPaginaDoLivroAdmActivity : AppCompatActivity() {

    private fun showRentalNotification(bookTitle: String) {
        val channelId = "rental_channel"
        val notificationId = 101

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Aluguéis", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_nav_book_user)
            .setContentTitle("Livro Alugado!")
            .setContentText("Você alugou: $bookTitle")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.adm_tela_pagina_do_livro_adm)

        // Componentes da UI
        val btnBack = findViewById<ImageButton>(R.id.btnBackLivro)
        val btnEditar = findViewById<MaterialButton>(R.id.btnEditarLivro)
        val btnExcluir = findViewById<MaterialButton>(R.id.btnExcluir)
        val btnAlugar = findViewById<MaterialButton>(R.id.btnAlugarLivro)
        val btnWishlist = findViewById<MaterialButton>(R.id.btnWishlist)

        val tvBookTitle = findViewById<TextView>(R.id.tvBookTitleMain)
        val ivBookCover = findViewById<ImageView>(R.id.ivBookCover)
        val tvBookAuthor = findViewById<TextView>(R.id.tvBookAuthor)
        val tvBookGenre = findViewById<TextView>(R.id.tvBookGenre)
        val tvBookPublication = findViewById<TextView>(R.id.tvBookPublication)
        val tvBookIsbn = findViewById<TextView>(R.id.tvBookIsbn)
        val tvBookPublisher = findViewById<TextView>(R.id.tvBookPublisher)
        val tvBookPages = findViewById<TextView>(R.id.tvBookPages)
        val tvBookSynopsis = findViewById<TextView>(R.id.tvBookSynopsis)

        // Dados do Intent
        val isEsgotado = intent.getBooleanExtra("IS_ESGOTADO", false)
        val bookTitle = intent.getStringExtra("BOOK_TITLE")
        val bookAuthor = intent.getStringExtra("BOOK_AUTHOR")
        val bookGenre = intent.getStringExtra("BOOK_GENRE")
        val bookPublication = intent.getStringExtra("BOOK_PUBLICATION")
        val bookIsbn = intent.getStringExtra("BOOK_ISBN")
        val bookPublisher = intent.getStringExtra("BOOK_PUBLISHER")
        val bookPages = intent.getStringExtra("BOOK_PAGES")
        val bookSynopsis = intent.getStringExtra("BOOK_SYNOPSIS")
        val bookImage = intent.getStringExtra("BOOK_IMAGE")

        // Preenchimento da Tela
        tvBookTitle.text = bookTitle ?: "Título Indisponível"
        tvBookAuthor.text = "Autor: ${bookAuthor ?: "Desconhecido"}"
        tvBookGenre.text = "Gênero: ${bookGenre ?: "Não informado"}"
        tvBookPublication.text = "Publicação: ${bookPublication ?: "N/A"}"
        tvBookIsbn.text = "ISBN: ${bookIsbn ?: "N/A"}"
        tvBookPublisher.text = "Editora: ${bookPublisher ?: "N/A"}"
        tvBookPages.text = "Páginas: ${bookPages ?: "N/A"}"
        tvBookSynopsis.text = bookSynopsis ?: "Sinopse não disponível."

        ivBookCover.load(bookImage) {
            placeholder(R.drawable.ic_launcher_background)
            error(R.drawable.ic_launcher_background)
        }

        // Lógica dos Botões
        btnBack?.setOnClickListener {
            val intent = Intent(this, TelahomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnEditar?.setOnClickListener {
            val intent = Intent(this, EditarLivroAdmActivity::class.java)
            startActivity(intent)
        }

        btnAlugar?.setOnClickListener {
            val title = bookTitle ?: "O Livro"

            if (isEsgotado) {
                val builder = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
                builder.setMessage("Este livro está esgotado. Gostaria de colocá-lo em sua lista de espera?")

                builder.setPositiveButton("Sim") { _, _ ->
                    val waitlistItem = WaitlistItem(
                        title = title,
                        author = bookAuthor,
                        image = bookImage,
                        position = "${(2..10).random()} de ${(11..20).random()}",
                        date = "1${(0..9).random()}/05"
                    )
                    WaitlistManager.addToWaitlist(this@TelaPaginaDoLivroAdmActivity, waitlistItem)
                    
                    Toast.makeText(this@TelaPaginaDoLivroAdmActivity, "livro na lista de espera", Toast.LENGTH_LONG).show()

                    val infoBuilder = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
                    val message = "Livro na lista de espera.\nVisualize sua lista de reservas."
                    val spannableString = SpannableString(message)
                    
                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            val intentList = Intent(this@TelaPaginaDoLivroAdmActivity, ListadeEsperaActivity::class.java)
                            startActivity(intentList)
                        }
                    }

                    val linkStart = message.indexOf("Visualize sua lista de reservas.")
                    val linkEnd = linkStart + "Visualize sua lista de reservas.".length
                    
                    spannableString.setSpan(clickableSpan, linkStart, linkEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(ForegroundColorSpan(Color.BLUE), linkStart, linkEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    spannableString.setSpan(UnderlineSpan(), linkStart, linkEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                    infoBuilder.setMessage(spannableString)
                    infoBuilder.setPositiveButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }
                    
                    val infoDialog = infoBuilder.create()
                    infoDialog.show()
                    
                    infoDialog.findViewById<TextView>(android.R.id.message)?.movementMethod = LinkMovementMethod.getInstance()
                    infoDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.BLACK)
                }

                builder.setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }

                val dialog = builder.create()
                dialog.show()

                dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.let {
                    it.setBackgroundColor("#2E7D32".toColorInt())
                    it.setTextColor(Color.WHITE)
                }
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.let {
                    it.setBackgroundColor("#D32F2F".toColorInt())
                    it.setTextColor(Color.WHITE)
                }

            } else {
                if (RentalManager.hasRentedInSession()) {
                    Toast.makeText(this, "Você já possui um livro alugado nesta sessão. Devolva-o para alugar outro.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val builder = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
                builder.setMessage("Você tem certeza que quer reservar o livro: $title ?")

                builder.setPositiveButton("Sim") { _, _ ->
                    RentalManager.rentBook(this, title, bookImage, bookAuthor)
                    RecommendationManager.addInterest(this, title)

                    showRentalNotification(title)

                    val infoBuilder = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
                    infoBuilder.setMessage("O livro está reservado para você.\nBusque na Biblioteca Unifor em até 3 dias apresentando documento com foto.")
                    infoBuilder.setPositiveButton("OK") { _, _ ->
                        val intentMeusLivros = Intent(this, MeusLivrosActivity::class.java)
                        startActivity(intentMeusLivros)
                        finish()
                    }
                    val infoDialog = infoBuilder.create()
                    infoDialog.show()
                    infoDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(Color.BLACK)
                }

                builder.setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }

                val dialog = builder.create()
                dialog.show()

                dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.let {
                    it.setBackgroundColor("#2E7D32".toColorInt())
                    it.setTextColor(Color.WHITE)
                }
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.let {
                    it.setBackgroundColor("#D32F2F".toColorInt())
                    it.setTextColor(Color.WHITE)
                }
            }
        }

        btnExcluir?.setOnClickListener {
            val titleForExcluir = bookTitle ?: "LIVRO"

            val builder = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
            builder.setMessage("Tem certeza que deseja excluir o livro ($titleForExcluir) do catálogo?")

            builder.setPositiveButton("Sim") { _, _ ->
                Toast.makeText(this, "Livro removido com sucesso.", Toast.LENGTH_SHORT).show()
                val intentHome = Intent(this, TelahomeActivity::class.java)
                intentHome.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intentHome)
                finish()
            }

            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.let {
                it.setBackgroundColor("#2E7D32".toColorInt())
                it.setTextColor(Color.WHITE)
            }

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.let {
                it.setBackgroundColor("#D32F2F".toColorInt())
                it.setTextColor(Color.WHITE)
            }
        }

        // Favoritos
        val prefs = getSharedPreferences("favoritos_prefs", MODE_PRIVATE)
        val titleForPrefs = bookTitle ?: ""
        val bookKey = "fav_$titleForPrefs"
        var isFavorito = if (titleForPrefs.isNotEmpty()) prefs.getBoolean(bookKey, false) else false

        if (isFavorito) {
            btnWishlist.iconTint = ColorStateList.valueOf(Color.RED)
        } else {
            btnWishlist.iconTint = ColorStateList.valueOf(Color.WHITE)
        }

        btnWishlist?.setOnClickListener {
            if (titleForPrefs.isEmpty()) {
                Toast.makeText(this, "Título do livro indisponível", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            isFavorito = !isFavorito
            val editor = prefs.edit()
            editor.putBoolean(bookKey, isFavorito)

            val favoritesSet = prefs.getStringSet("favorites_list", null)?.toMutableSet() ?: mutableSetOf()

            if (isFavorito) {
                btnWishlist.iconTint = ColorStateList.valueOf(Color.RED)
                Toast.makeText(this, "Adicionado aos favoritos", Toast.LENGTH_SHORT).show()

                favoritesSet.add(titleForPrefs)
                editor.putString("author_$titleForPrefs", bookAuthor)
                editor.putString("image_$titleForPrefs", bookImage)
                editor.putString("genre_$titleForPrefs", bookGenre)
                editor.putString("publication_$titleForPrefs", bookPublication)
                editor.putString("isbn_$titleForPrefs", bookIsbn)
                editor.putString("publisher_$titleForPrefs", bookPublisher)
                editor.putString("pages_$titleForPrefs", bookPages)
                editor.putString("synopsis_$titleForPrefs", bookSynopsis)
            } else {
                btnWishlist.iconTint = ColorStateList.valueOf(Color.WHITE)
                Toast.makeText(this, "Removido dos favoritos", Toast.LENGTH_SHORT).show()

                favoritesSet.remove(titleForPrefs)
                editor.remove("author_$titleForPrefs")
                editor.remove("image_$titleForPrefs")
                editor.remove("genre_$titleForPrefs")
                editor.remove("publication_$titleForPrefs")
                editor.remove("isbn_$titleForPrefs")
                editor.remove("publisher_$titleForPrefs")
                editor.remove("pages_$titleForPrefs")
                editor.remove("synopsis_$titleForPrefs")
            }

            editor.putStringSet("favorites_list", favoritesSet)
            editor.apply()
        }

        // Navegação Inferior
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
}
