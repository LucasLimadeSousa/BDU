package com.example.bdu.livros

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
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bdu.R
import com.example.bdu.adm.EditarLivroAdmActivity
import com.example.bdu.adm.TelaPaginaDoLivroAdmActivity
import com.example.bdu.usuario.MeuPerfilActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.core.graphics.toColorInt
import android.widget.ImageView
import coil.load

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

class PaginaDoLivroActivity : AppCompatActivity() {

    private fun showRentalNotification(bookTitle: String) {
        val channelId = "rental_channel"
        val notificationId = 101

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Aluguéis", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_nav_book_user) // Usando um ícone existente
            .setContentTitle("Livro Alugado!")
            .setContentText("Você alugou: $bookTitle")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.livros_pagina_do_livro)

        val btnBotaoRetornar = findViewById<ImageButton>(R.id.btnBackLivro)


        btnBotaoRetornar?.setOnClickListener {
            onBackPressed()
        }


        val btnAlugar = findViewById<MaterialButton>(R.id.btnAlugar)
        val tvBookTitle = findViewById<TextView>(R.id.tvBookTitleMain)
        val ivBookCover = findViewById<ImageView>(R.id.ivBookCover)
        val tvBookAuthor = findViewById<TextView>(R.id.tvBookAuthor)
        val tvBookGenre = findViewById<TextView>(R.id.tvBookGenre)
        val tvBookPublication = findViewById<TextView>(R.id.tvBookPublication)
        val tvBookIsbn = findViewById<TextView>(R.id.tvBookIsbn)
        val tvBookPublisher = findViewById<TextView>(R.id.tvBookPublisher)
        val tvBookPages = findViewById<TextView>(R.id.tvBookPages)
        val tvBookSynopsis = findViewById<TextView>(R.id.tvBookSynopsis)

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

        // --- LÓGICA DE PERSISTÊNCIA DO CORAÇÃO ---
        val btnWishlist = findViewById<MaterialButton>(R.id.btnWishlist)
        val prefs = getSharedPreferences("favoritos_prefs", MODE_PRIVATE)
        
        // Usamos o título do livro como chave para saber se ele está favoritado
        val titleForPrefs = bookTitle ?: ""
        val bookKey = "fav_$titleForPrefs" 
        
        var isFavorito = if (titleForPrefs.isNotEmpty()) prefs.getBoolean(bookKey, false) else false

        // Define a cor inicial baseada no que foi salvo
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
                
                // Salva os dados do livro para mostrar na lista de desejos
                favoritesSet.add(titleForPrefs)
                editor.putString("author_$titleForPrefs", bookAuthor)
                editor.putString("image_$titleForPrefs", bookImage)
                editor.putString("genre_$titleForPrefs", bookGenre)
                editor.putString("publication_$titleForPrefs", bookPublication)
                editor.putString("isbn_$titleForPrefs", bookIsbn)
                editor.putString("publisher_$titleForPrefs", bookPublisher)
                editor.putString("pages_$titleForPrefs", bookPages)
                editor.putString("synopsis_$titleForPrefs", bookSynopsis)

                // Algoritmo: Adiciona interesse (se a privacidade de favoritos permitir)
                if (!RecommendationManager.isFavoritesPrivate(this)) {
                    RecommendationManager.addInterest(this, titleForPrefs)
                    bookGenre?.split(",")?.firstOrNull()?.let { RecommendationManager.addInterest(this, it.trim()) }
                    bookAuthor?.let { RecommendationManager.addInterest(this, it) }
                }
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
        // ----------------------------------------

        btnAlugar?.setOnClickListener {
            val bookTitle = tvBookTitle?.text?.toString() ?: "O Livro"

            if (isEsgotado) {
                // LÓGICA PARA LIVRO ESGOTADO (FOTO 3)
                val builder = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
                builder.setMessage("Este livro está esgotado. Gostaria de colocá-lo em sua lista de espera?")

                builder.setPositiveButton("Sim") { _, _ ->
                    // Adiciona à lista de espera real
                    val waitlistItem = WaitlistItem(
                        title = bookTitle ?: "Livro",
                        author = bookAuthor,
                        image = bookImage,
                        position = "${(2..10).random()} de ${(11..20).random()}",
                        date = "1${(0..9).random()}/05"
                    )
                    WaitlistManager.addToWaitlist(this@PaginaDoLivroActivity, waitlistItem)
                    
                    Toast.makeText(this@PaginaDoLivroActivity, "livro na lista de espera", Toast.LENGTH_LONG).show()

                    // Segundo Pop Up com link clicável
                    val infoBuilder = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
                    
                    val message = "Livro na lista de espera.\nVisualize sua lista de reservas."
                    val spannableString = SpannableString(message)
                    
                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            val intentList = Intent(this@PaginaDoLivroActivity, ListadeEsperaActivity::class.java)
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
                    
                    // IMPORTANTE: Necessário para o link funcionar
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
                    val tvBookTitle = findViewById<TextView>(R.id.tvBookTitleMain)
                    val ivBookCover = findViewById<ImageView>(R.id.ivBookCover)
                    val tvBookSynopsis = findViewById<TextView>(R.id.tvBookSynopsis)
                    it.setBackgroundColor("#D32F2F".toColorInt())
                    it.setTextColor(Color.WHITE)
                }

            } else {
                // LÓGICA NORMAL PARA LIVRO DISPONÍVEL
                if (RentalManager.hasRentedInSession()) {
                    Toast.makeText(this, "Você já possui um livro alugado nesta sessão. Devolva-o para alugar outro.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val builder = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
                builder.setMessage("Você tem certeza que quer reservar o livro: $bookTitle ?")

                builder.setPositiveButton("Sim") { _, _ ->
                    // Salva o livro no gerenciador de sessão e no histórico permanente
                    RentalManager.rentBook(this, bookTitle ?: "Livro sem título", bookImage, bookAuthor)
                    
                    // Algoritmo: Adiciona interesse ao alugar
                    RecommendationManager.addInterest(this, bookTitle ?: "")
                    bookAuthor?.let { RecommendationManager.addInterest(this, it) }
                    bookGenre?.split(",")?.firstOrNull()?.let { RecommendationManager.addInterest(this, it.trim()) }

                    // Mostra notificação
                    showRentalNotification(bookTitle ?: "Livro")

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
    }
}