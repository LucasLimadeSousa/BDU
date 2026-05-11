package com.example.bdu.pagamentos

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R
import com.example.bdu.livros.TelahomeActivity

class CartaoCredActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pagamentos_cartao_cred)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.imgBtnVoltarCred).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnConfirmarPagamento).setOnClickListener {
            val valorTotal = findViewById<TextView>(R.id.textViewValorTotal).text.toString().replace("Valor total: ", "")
            
            Toast.makeText(this, "O valor $valorTotal foi quitado com sucesso.", Toast.LENGTH_LONG).show()

            val intent = Intent(this, TelahomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}