package com.example.bdu.pagamentos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R
import com.google.android.material.button.MaterialButton
import java.util.Locale

class JurosMultaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pagamentos_juros_multa)

        val tvValorTotal = findViewById<TextView>(R.id.tvValorTotal)
        val buttonSomar = findViewById<Button>(R.id.buttonSomar)

        // Função para atualizar o texto do valor total
        fun atualizarValor() {
            tvValorTotal.text = String.format(Locale.getDefault(), "R$%.2f", PaymentManager.divida)
        }

        // Inicializa o valor na tela
        atualizarValor()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        findViewById<ImageButton>(R.id.btnBackLivro).setOnClickListener {
            finish()
        }


        buttonSomar.setOnClickListener {
            PaymentManager.divida += 10.50
            atualizarValor()
        }

        findViewById<MaterialButton>(R.id.btnPagar).setOnClickListener {
            val intent = Intent(this, SelecionarMetodoActivity::class.java)
            startActivity(intent)
        }
    }
}
