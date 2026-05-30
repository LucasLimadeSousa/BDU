package com.example.bdu.livros

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.example.bdu.R
import com.example.bdu.usuario.HistoricoDeLivrosActivity
import com.example.bdu.usuario.MeuPerfilActivity
import com.google.android.material.button.MaterialButton

class MeusLivrosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.livros_meuslivros)

        val cardRented = findViewById<CardView>(R.id.cardRentedBook)
        val tvTitle = findViewById<TextView>(R.id.tvBookTitle)
        val ivCover = findViewById<ImageView>(R.id.ivBookCoverRented)
        val tvNoBook = findViewById<TextView>(R.id.tvNoBookMessage)

        // Atualiza o cartão do topo APENAS com o aluguel desta sessão ativa
        if (RentalManager.hasRentedInSession()) {
            cardRented?.visibility = View.VISIBLE
            tvNoBook?.visibility = View.GONE
            tvTitle?.text = RentalManager.sessionRentedTitle
            ivCover?.load(RentalManager.sessionRentedImage) {
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_background)
            }
        } else {
            cardRented?.visibility = View.GONE
            tvNoBook?.visibility = View.VISIBLE
        }

        // Carrega o histórico completo (permanente)
        carregarHistoricoCompleto()

        val btnReport = findViewById<LinearLayout?>(R.id.btn_report)
        val btnVerTudo = findViewById<TextView?>(R.id.VerTudo)
        val btnAddDesejos = findViewById<MaterialButton?>(R.id.btn_desejos)

        val btnFila = findViewById<ImageButton?>(R.id.btn_nav_fila)
        val btnHome = findViewById<ImageButton?>(R.id.btn_nav_home)
        val btnDesejosNav = findViewById<ImageButton?>(R.id.btn_nav_desejos)
        val btnPerfil = findViewById<ImageButton?>(R.id.btn_nav_perfil)

        val mainView = findViewById<View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        // Lógica do botão "Lista de Desejos"
        val prefs = getSharedPreferences("favoritos_prefs", MODE_PRIVATE)
        val currentBookTitle = RentalManager.sessionRentedTitle
        val currentBookImage = RentalManager.sessionRentedImage
        val history = RentalManager.getPersistentHistory(this)
        val currentBookAuthor = history.find { it.title == currentBookTitle }?.author

        val bookKey = "fav_${currentBookTitle ?: "LivroPosse"}"

        var isFavorito = prefs.getBoolean(bookKey, false)
        if (isFavorito) {
            btnAddDesejos?.iconTint = ColorStateList.valueOf(Color.RED)
        } else {
            btnAddDesejos?.iconTint = ColorStateList.valueOf(Color.WHITE)
        }

        btnAddDesejos?.setOnClickListener {
            if (currentBookTitle == null) {
                Toast.makeText(this, "Nenhum livro alugado para favoritar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            isFavorito = !isFavorito
            val editor = prefs.edit()
            editor.putBoolean(bookKey, isFavorito)

            val favoritesSet = prefs.getStringSet("favorites_list", null)?.toMutableSet() ?: mutableSetOf()

            if (isFavorito) {
                btnAddDesejos.iconTint = ColorStateList.valueOf(Color.RED)
                favoritesSet.add(currentBookTitle)
                editor.putString("author_$currentBookTitle", currentBookAuthor)
                editor.putString("image_$currentBookTitle", currentBookImage)
                Toast.makeText(this, "Adicionado aos favoritos", Toast.LENGTH_SHORT).show()
            } else {
                btnAddDesejos.iconTint = ColorStateList.valueOf(Color.WHITE)
                favoritesSet.remove(currentBookTitle)
                editor.remove("author_$currentBookTitle")
                editor.remove("image_$currentBookTitle")
                Toast.makeText(this, "Removido", Toast.LENGTH_SHORT).show()
            }
            editor.putStringSet("favorites_list", favoritesSet)
            editor.apply()
        }

        btnReport?.setOnClickListener {
            val intent = Intent(this, com.example.bdu.suporte.ReportarProblema2Activity::class.java)
            startActivity(intent)
        }

        btnVerTudo?.setOnClickListener {
            val intent = Intent(this, HistoricoDeLivrosActivity::class.java)
            startActivity(intent)
        }

        // Barra de tarefas
        btnFila?.setOnClickListener { startActivity(Intent(this, ListadeEsperaActivity::class.java)) }
        btnHome?.setOnClickListener { startActivity(Intent(this, TelahomeActivity::class.java)) }
        btnDesejosNav?.setOnClickListener { startActivity(Intent(this, ListaDesejosActivity::class.java)) }
        btnPerfil?.setOnClickListener { startActivity(Intent(this, MeuPerfilActivity::class.java)) }
    }

    private fun carregarHistoricoCompleto() {
        val grid = findViewById<GridLayout>(R.id.grid_historico)
        if (grid == null) return
        grid.removeAllViews()

        val inflater = LayoutInflater.from(this)
        val history = RentalManager.getPersistentHistory(this)
        
        history.forEachIndexed { index, item ->
            val view = inflater.inflate(R.layout.item_historico_aluguel, grid, false)
            
            val params = GridLayout.LayoutParams()
            params.width = 0
            params.height = GridLayout.LayoutParams.WRAP_CONTENT
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f)
            params.setMargins(12, 12, 12, 12)
            view.layoutParams = params

            val tvHistoryTitle = view.findViewById<TextView>(R.id.tvHistoryTitle)
            val tvHistoryStatus = view.findViewById<TextView>(R.id.tvHistoryStatus)
            val tvHistoryDate = view.findViewById<TextView>(R.id.tvHistoryDate)
            val tvHistoryReturn = view.findViewById<TextView>(R.id.tvHistoryReturn)

            tvHistoryTitle.text = item.title
            tvHistoryDate.text = "Alugado em: ${item.date}"

            // Lógica: Se for o primeiro da lista E foi alugado NESTA sessão, fica "Em posse"
            if (index == 0 && RentalManager.sessionRentedTitle == item.title) {
                tvHistoryStatus.text = "Status: Em posse"
                tvHistoryStatus.setTextColor(Color.parseColor("#FFA500")) // Laranja
                tvHistoryReturn.visibility = View.VISIBLE
                tvHistoryReturn.text = "Devolver em até 30 dias"
            } else {
                tvHistoryStatus.text = "Status: Devolvido há ${item.daysAgo} dias"
                tvHistoryStatus.setTextColor(Color.parseColor("#008000")) // Verde
                tvHistoryReturn.visibility = View.GONE
            }

            grid.addView(view)
        }
    }
}