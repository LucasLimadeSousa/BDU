package com.example.bdu.pagamentos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R
import com.example.bdu.livros.TelahomeActivity

class CartaoDebActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. PRIMEIRO: Define o layout
        setContentView(R.layout.pagamentos_cartao_deb)

        // 2. DEPOIS: Configura os ajustes de tela (WindowInsets)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 3. AGORA SIM: Encontra os botões
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmarPagamento)
        val btnVoltar = findViewById<ImageButton>(R.id.imgBtnVoltarCred)

        // Configura o clique de voltar
        btnVoltar?.setOnClickListener {
            finish()
        }

        // Configura o clique de confirmar (Volta para a Home)
        btnConfirmar?.setOnClickListener {
            val intent = Intent(this, TelahomeActivity::class.java)
            // Limpa a pilha de telas para que o usuário não volte para a tela de pagamento ao apertar "voltar" no celular
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}