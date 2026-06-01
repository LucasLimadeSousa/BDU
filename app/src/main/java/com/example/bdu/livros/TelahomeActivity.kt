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
import com.example.bdu.model.Usuario
import com.example.bdu.model.VolumeInfo
import com.example.bdu.model.ImageLinks
import com.example.bdu.model.IndustryIdentifier
import retrofit2.HttpException

class TelahomeActivity : AppCompatActivity() {

    private var isAdm: Boolean = false
    private val livrosCarregados = mutableMapOf<Int, VolumeInfo>()
    private val displayedTitles = mutableSetOf<String>()
    private var favoritesSet = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.livros_telahome)

        isAdm = intent.getBooleanExtra("USER_IS_ADM", false)
        verificarStatusAdm()

        configurarInsets()
        configurarBotoes()
        configurarCliquesLivros()
    }

    override fun onResume() {
        super.onResume()
        val prefs = getSharedPreferences("favoritos_prefs", MODE_PRIVATE)
        favoritesSet = prefs.getStringSet("favorites_list", null)?.toMutableSet() ?: mutableSetOf()
        
        // Limpeza total antes de recarregar para evitar "livros fantasma"
        displayedTitles.clear()
        livrosCarregados.clear()
        
        // Reset visual imediato de containers que podem esconder livros excluídos
        val allContainers = listOf(
            R.id.item_livro_a, R.id.item_livro_b, R.id.item_livro_c, R.id.item_livro_d, R.id.item_livro_e,
            R.id.item_livro_1, R.id.item_livro_2, R.id.item_livro_3, R.id.item_livro_4, R.id.item_livro_5,
            R.id.item_livro_10, R.id.item_livro_20, R.id.item_livro_30, R.id.item_livro_40, R.id.item_livro_50
        )
        allContainers.forEach { id ->
            findViewById<LinearLayout>(id)?.let { container ->
                container.findViewById<TextView>(R.id.tituloLivro)?.text = "Carregando..."
                container.findViewById<ImageView>(R.id.imgLivro)?.setImageDrawable(null)
            }
        }

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

                    // Algoritmo: Identifica e salva o curso do usuário para recomendações específicas
                    RecommendationManager.setUserCourse(this@TelahomeActivity, usuario.curso)

                    atualizarVisibilidadeAdm()
                    configurarCliquesLivros()

                    // Recarrega os livros agora que o curso foi identificado
                    carregarLivrosHome()
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
                            RetrofitInstance.api.searchBooks(query, RetrofitInstance.API_KEY)
                        }

                        val livro = resposta.items?.firstOrNull {
                            val title = it.volumeInfo.title ?: ""
                            val normalizedTitle = title.lowercase().trim()
                            
                            val isExcluded = com.example.bdu.adm.BookCatalogManager.isBookExcluded(this@TelahomeActivity, title)

                            it.volumeInfo.imageLinks?.thumbnail != null &&
                                    title.isNotBlank() &&
                                    !it.volumeInfo.authors.isNullOrEmpty() &&
                                    !displayedTitles.contains(normalizedTitle) &&
                                    !isExcluded &&
                                    !favoritesSet.any { fav -> fav.lowercase().trim() == normalizedTitle }
                        }

                        if (livro != null) {
                            val finalTitle = livro.volumeInfo.title ?: ""
                            displayedTitles.add(finalTitle.lowercase().trim())

                            // Tenta carregar uma versão editada do livro se existir
                            val override = com.example.bdu.adm.BookCatalogManager.getBookOverride(this@TelahomeActivity, finalTitle)
                            
                            val infoToShow = if (override != null) {
                                VolumeInfo(
                                    title = override.title,
                                    authors = listOf(override.author ?: ""),
                                    description = override.synopsis,
                                    categories = listOf(override.genre ?: ""),
                                    publishedDate = override.date,
                                    pageCount = override.pages?.toIntOrNull(),
                                    publisher = override.publisher,
                                    language = override.language,
                                    industryIdentifiers = listOf(IndustryIdentifier("ISBN", override.isbn)),
                                    imageLinks = ImageLinks(override.image)
                                )
                            } else {
                                livro.volumeInfo
                            }

                            livrosCarregados[idInclude] = infoToShow
                            txtTitulo.text = infoToShow.title
                            txtAutor.text = infoToShow.authors?.getOrNull(0) ?: "Autor desconhecido"

                            val imagem = infoToShow.imageLinks
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
                                delay(2000)
                            }
                        }
                    } catch (e: HttpException) {
                        if (e.code() == 429) {
                            txtTitulo.text = "Cota Excedida"
                            sucesso = true
                        } else {
                            tentativa++
                            if (tentativa >= 3) txtTitulo.text = "Erro API"
                            delay(2000)
                        }
                    } catch (e: Exception) {
                        tentativa++
                        if (tentativa >= 3) {
                            txtTitulo.text = "Erro"
                            e.printStackTrace()
                        } else {
                            delay(2000)
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
        val allAddedBooks = com.example.bdu.adm.BookCatalogManager.getAddedBooks(this)
        
        // Filtra livros adicionados que não foram excluídos pelo ADM
        val validAddedBooks = allAddedBooks.filter { 
            !com.example.bdu.adm.BookCatalogManager.isBookExcluded(this, it.title)
        }.toMutableList()

        // --- Seção: Lista de Desejos (Favoritos Reais) ---
        val prefs = getSharedPreferences("favoritos_prefs", MODE_PRIVATE)
        val listFavoritos = favoritesSet.toList()
        val idsFavoritos = listOf(R.id.item_livro_a, R.id.item_livro_b, R.id.item_livro_c, R.id.item_livro_d, R.id.item_livro_e)

        var addedIndex = 0

        idsFavoritos.forEachIndexed { index, id ->
            val container = findViewById<LinearLayout>(id)
            if (index < listFavoritos.size) {
                val title = listFavoritos[index]
                val author = prefs.getString("author_$title", "Autor desconhecido")
                val image = prefs.getString("image_$title", null)

                // Verifica se existe uma versão editada pelo ADM para este favorito
                val override = com.example.bdu.adm.BookCatalogManager.getBookOverride(this@TelahomeActivity, title)
                
                val finalTitle = override?.title ?: title
                val finalAuthor = override?.author ?: author
                val finalImage = override?.image ?: image

                displayedTitles.add(finalTitle.lowercase().trim())
                container?.visibility = View.VISIBLE
                val img = container?.findViewById<ImageView>(R.id.imgLivro)
                val txtTitulo = container?.findViewById<TextView>(R.id.tituloLivro)
                val txtAutor = container?.findViewById<TextView>(R.id.autorLivro)

                txtTitulo?.text = finalTitle
                txtAutor?.text = finalAuthor
                img?.load(finalImage) {
                    crossfade(true)
                    placeholder(R.drawable.ic_launcher_background)
                    error(R.drawable.ic_launcher_background)
                }

                livrosCarregados[id] = if (override != null) {
                    VolumeInfo(
                        title = override.title,
                        authors = listOf(override.author ?: ""),
                        description = override.synopsis,
                        categories = listOf(override.genre ?: ""),
                        publishedDate = override.date,
                        pageCount = override.pages?.toIntOrNull(),
                        publisher = override.publisher,
                        language = override.language,
                        industryIdentifiers = listOf(IndustryIdentifier("ISBN", override.isbn)),
                        imageLinks = ImageLinks(override.image)
                    )
                } else {
                    VolumeInfo(
                        title = title,
                        authors = listOf(author ?: "Desconhecido"),
                        description = prefs.getString("synopsis_$title", null),
                        categories = listOf(prefs.getString("genre_$title", "") ?: ""),
                        publishedDate = prefs.getString("publication_$title", null),
                        pageCount = prefs.getString("pages_$title", null)?.toIntOrNull(),
                        publisher = prefs.getString("publisher_$title", null),
                        language = "pt",
                        industryIdentifiers = listOf(IndustryIdentifier("ISBN", prefs.getString("isbn_$title", null))),
                        imageLinks = ImageLinks(image)
                    )
                }
            } else {
                // Se sobrarem espaços nos slots da primeira fileira, coloca os livros adicionados pelo ADM
                var foundBook = false
                while (addedIndex < validAddedBooks.size && !foundBook) {
                    val book = validAddedBooks[addedIndex]
                    val normalizedTitle = book.title.lowercase().trim()
                    
                    if (!displayedTitles.contains(normalizedTitle)) {
                        displayedTitles.add(normalizedTitle)
                        container?.visibility = View.VISIBLE
                        val img = container?.findViewById<ImageView>(R.id.imgLivro)
                        val txtTitulo = container?.findViewById<TextView>(R.id.tituloLivro)
                        val txtAutor = container?.findViewById<TextView>(R.id.autorLivro)

                        txtTitulo?.text = book.title
                        txtAutor?.text = book.author
                        img?.load(book.image) {
                            crossfade(true)
                            placeholder(R.drawable.ic_launcher_background)
                            error(R.drawable.ic_launcher_background)
                        }

                        livrosCarregados[id] = VolumeInfo(
                            title = book.title,
                            authors = listOf(book.author ?: ""),
                            description = book.synopsis,
                            categories = listOf(book.genre ?: ""),
                            publishedDate = book.date,
                            pageCount = book.pages?.toIntOrNull(),
                            publisher = book.publisher,
                            language = book.language,
                            industryIdentifiers = listOf(IndustryIdentifier("ISBN", book.isbn)),
                            imageLinks = ImageLinks(book.image)
                        )
                        foundBook = true
                    }
                    addedIndex++
                }
                
                if (!foundBook) {
                    container?.visibility = View.INVISIBLE
                }
            }
        }

        // --- Seção: Recomendações por Curso ---
        val courseTerms = RecommendationManager.getCourseSearchTerms(this)
        val genericFallback = listOf("livro", "estudo", "academia", "ciência", "educação").shuffled()

        carregarLivroSeguro(R.id.item_livro_1, courseTerms.getOrElse(0) { genericFallback[0] }, 2000)
        carregarLivroSeguro(R.id.item_livro_2, courseTerms.getOrElse(1) { genericFallback[1] }, 2800)
        carregarLivroSeguro(R.id.item_livro_3, courseTerms.getOrElse(2) { genericFallback[2] }, 3600)
        carregarLivroSeguro(R.id.item_livro_4, courseTerms.getOrElse(3) { genericFallback[3] }, 4400)
        carregarLivroSeguro(R.id.item_livro_5, courseTerms.getOrElse(4) { genericFallback[4] }, 5200)

        // --- Seção: Livros Populares ---
        val generalTerms = RecommendationManager.getGeneralSearchTerms(this)
        carregarLivroSeguro(R.id.item_livro_10, generalTerms.getOrElse(0) { "ficção" }, 6000)
        carregarLivroSeguro(R.id.item_livro_20, generalTerms.getOrElse(1) { "história" }, 6800)
        carregarLivroSeguro(R.id.item_livro_30, generalTerms.getOrElse(2) { "romance" }, 7600)
        carregarLivroSeguro(R.id.item_livro_40, generalTerms.getOrElse(3) { "biografia" }, 8400)
        carregarLivroSeguro(R.id.item_livro_50, generalTerms.getOrElse(4) { "ciência" }, 9200)
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

            val info = livrosCarregados[v.id]
            if (info != null) {
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

                // Algoritmo: Aprende quando o usuário clica num livro
                info.title?.let { RecommendationManager.addInterest(this, it) }
                info.categories?.firstOrNull()?.let { RecommendationManager.addInterest(this, it) }
                info.authors?.firstOrNull()?.let { RecommendationManager.addInterest(this, it) }
            }

            if (v.id == R.id.item_livro_b) {
                intent.putExtra("IS_ESGOTADO", true)
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
            startActivity(Intent(this, ListaDesejosActivity::class.java))
        }
        findViewById<TextView?>(R.id.VerTudo2)?.setOnClickListener {
            val intent = Intent(this, VerTudoActivity::class.java)
            intent.putExtra("TIPO_LISTA", "CURSO")
            startActivity(intent)
        }
        findViewById<TextView?>(R.id.VerTudo3)?.setOnClickListener {
            val intent = Intent(this, VerTudoActivity::class.java)
            intent.putExtra("TIPO_LISTA", "ALGORITMO")
            startActivity(intent)
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

        findViewById<View?>(R.id.fab_chatbot)?.setOnClickListener {
            startActivity(Intent(this, LivrosChatbotActivity::class.java))
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