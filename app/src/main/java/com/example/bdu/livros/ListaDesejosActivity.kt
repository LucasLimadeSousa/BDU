package com.example.bdu.livros

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.bdu.R
import com.example.bdu.usuario.MeuPerfilActivity

class ListaDesejosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.livros_lista_desejos)

        // Navegação
        configurarNavegacao()
    }

    override fun onResume() {
        super.onResume()
        carregarFavoritos()
    }

    private fun carregarFavoritos() {
        val gridFavoritos = findViewById<GridLayout>(R.id.grid_favoritos)
        gridFavoritos.removeAllViews()

        val prefs = getSharedPreferences("favoritos_prefs", MODE_PRIVATE)
        val favoritesSet = prefs.getStringSet("favorites_list", null)?.toMutableSet() ?: mutableSetOf()

        if (favoritesSet.isEmpty()) {
            val emptyMessage = TextView(this)
            emptyMessage.text = "Sua lista de desejos está vazia.\nFavorite livros para vê-los aqui!"
            emptyMessage.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            emptyMessage.setPadding(0, 100, 0, 0)
            gridFavoritos.addView(emptyMessage)
            return
        }

        val inflater = LayoutInflater.from(this)

        for (title in favoritesSet) {
            val author = prefs.getString("author_$title", "Autor desconhecido")
            val image = prefs.getString("image_$title", null)

            val itemView = inflater.inflate(R.layout.item_book_wishlist, gridFavoritos, false)

            // Configurar layout para 3 colunas (Igual ao VerTudoActivity)
            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = GridLayout.LayoutParams.WRAP_CONTENT
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f)
            params.setMargins(8, 8, 8, 8)
            itemView.layoutParams = params

            val imgLivro = itemView.findViewById<ImageView>(R.id.imgLivro)
            val txtTitulo = itemView.findViewById<TextView>(R.id.tituloLivro)
            val txtAutor = itemView.findViewById<TextView>(R.id.autorLivro)
            val btnRemover = itemView.findViewById<ImageButton>(R.id.btn_remover_desejo)

            txtTitulo.text = title
            txtAutor.text = author
            imgLivro.load(image) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_background)
            }

            btnRemover.imageTintList = ColorStateList.valueOf(Color.RED)
            btnRemover.setOnClickListener {
                // Lógica para remover dos favoritos
                val editor = prefs.edit()
                editor.remove("fav_$title")
                editor.remove("author_$title")
                editor.remove("image_$title")
                editor.remove("genre_$title")
                editor.remove("publication_$title")
                editor.remove("isbn_$title")
                editor.remove("publisher_$title")
                editor.remove("pages_$title")
                editor.remove("synopsis_$title")

                val newSet = prefs.getStringSet("favorites_list", null)?.toMutableSet() ?: mutableSetOf()
                newSet.remove(title)
                editor.putStringSet("favorites_list", newSet)
                editor.apply()

                // Recarregar a lista
                carregarFavoritos()
            }

            itemView.setOnClickListener {
                // Abrir a página do livro ao clicar no item
                val intent = Intent(this, PaginaDoLivroActivity::class.java)
                intent.putExtra("BOOK_TITLE", title)
                intent.putExtra("BOOK_AUTHOR", author)
                intent.putExtra("BOOK_IMAGE", image)
                intent.putExtra("BOOK_GENRE", prefs.getString("genre_$title", null))
                intent.putExtra("BOOK_PUBLICATION", prefs.getString("publication_$title", null))
                intent.putExtra("BOOK_ISBN", prefs.getString("isbn_$title", null))
                intent.putExtra("BOOK_PUBLISHER", prefs.getString("publisher_$title", null))
                intent.putExtra("BOOK_PAGES", prefs.getString("pages_$title", null))
                intent.putExtra("BOOK_SYNOPSIS", prefs.getString("synopsis_$title", null))
                startActivity(intent)
            }

            gridFavoritos.addView(itemView)
        }

        // Adiciona "espaçadores" invisíveis para manter a grade de 3 colunas se houver menos de 3 itens
        val numItens = favoritesSet.size
        val faltamParaCompletarLinha = (3 - (numItens % 3)) % 3
        
        for (i in 0 until faltamParaCompletarLinha) {
            val spacer = android.view.View(this)
            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = 0
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f)
            spacer.layoutParams = params
            gridFavoritos.addView(spacer)
        }
    }

    private fun configurarNavegacao() {
        findViewById<ImageButton>(R.id.btn_nav_fila).setOnClickListener {
            startActivity(Intent(this, ListadeEsperaActivity::class.java))
        }
        findViewById<ImageButton>(R.id.btn_nav_meuslivros).setOnClickListener {
            startActivity(Intent(this, MeusLivrosActivity::class.java))
        }
        findViewById<ImageButton>(R.id.btn_nav_home).setOnClickListener {
            startActivity(Intent(this, TelahomeActivity::class.java))
        }
        findViewById<ImageButton>(R.id.btn_nav_perfil).setOnClickListener {
            startActivity(Intent(this, MeuPerfilActivity::class.java))
        }
    }
}
