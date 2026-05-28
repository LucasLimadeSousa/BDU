package com.example.bdu.usuario

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.example.bdu.R
import com.example.bdu.livros.RentalManager

class HistoricoDeLivrosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.usuario_historico_de_livros)
        
        val container = findViewById<LinearLayout>(R.id.container_historico_completo)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.btnBackLivro).setOnClickListener {
            finish()
        }

        carregarHistoricoReal(container)
    }

    private fun carregarHistoricoReal(container: LinearLayout?) {
        if (container == null) return
        
        // Remove itens antigos, exceto o botão voltar
        val startFrom = 1 
        if (container.childCount > startFrom) {
            container.removeViews(startFrom, container.childCount - startFrom)
        }

        val history = RentalManager.getPersistentHistory(this)
        val inflater = LayoutInflater.from(this)

        history.forEachIndexed { index, item ->
            val itemView = inflater.inflate(R.layout.item_historico_detalhado, container, false)
            
            val img = itemView.findViewById<ImageView>(R.id.ivBookHistory)
            val title = itemView.findViewById<TextView>(R.id.tvTitleHistory)
            val author = itemView.findViewById<TextView>(R.id.tvAuthorHistory)
            val date = itemView.findViewById<TextView>(R.id.tvDateHistory)
            val status = itemView.findViewById<TextView>(R.id.tvStatusHistory)

            title.text = item.title
            author.text = item.author ?: "Autor desconhecido"
            date.text = "Alugado: ${item.date}"

            img.load(item.imageUrl) {
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_background)
            }

            // Lógica de status: se for o livro alugado nesta sessão
            if (index == 0 && RentalManager.sessionRentedTitle == item.title) {
                status.text = "Status: Em posse"
                status.setTextColor(Color.parseColor("#FFA500")) // Laranja
            } else {
                status.text = "Status: Devolvido há ${item.daysAgo} dias"
                status.setTextColor(Color.parseColor("#008000")) // Verde
            }

            container.addView(itemView)
        }
    }
}