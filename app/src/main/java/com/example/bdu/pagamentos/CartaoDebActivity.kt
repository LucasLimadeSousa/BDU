package com.example.bdu.pagamentos

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R
import com.example.bdu.livros.TelahomeActivity
import java.util.Locale

class CartaoDebActivity : AppCompatActivity() {

    private lateinit var etNumeroCartao: EditText
    private lateinit var tvBandeira: TextView
    private lateinit var tvValorTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pagamentos_cartao_deb)

        etNumeroCartao = findViewById(R.id.et_numero_cartao)
        tvBandeira = findViewById(R.id.tv_bandeira_cartao)
        tvValorTotal = findViewById(R.id.textViewValorTotal)

        setupWindowInsets()
        setupValorTotal()
        setupCardNumberWatcher()

        findViewById<ImageButton>(R.id.imgBtnVoltarCred).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnConfirmarPagamento).setOnClickListener {
            confirmarPagamento()
        }
    }

    private fun setupWindowInsets() {
        val mainView = findViewById<View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }

    private fun setupValorTotal() {
        tvValorTotal.text = String.format(Locale.getDefault(), "Valor total: R$%.2f", PaymentManager.divida)
    }

    private fun setupCardNumberWatcher() {
        etNumeroCartao.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                identificarBandeira(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun identificarBandeira(numero: String) {
        val n = numero.replace(" ", "")
        if (n.isEmpty()) {
            tvBandeira.visibility = View.GONE
            return
        }

        tvBandeira.visibility = View.VISIBLE

        when {
            n.startsWith("4") -> {
                tvBandeira.text = "VISA"
            }
            n.matches(Regex("^(5[1-5]|222[1-9]|22[3-9]|2[3-6]|27[0-1]|2720).*")) -> {
                tvBandeira.text = "MASTER"
            }
            n.matches(Regex("^3[47].*")) -> {
                tvBandeira.text = "AMEX"
            }
            n.matches(Regex("^(606282|5067|4576|4011).*")) -> {
                tvBandeira.text = "ELO"
            }
            n.startsWith("38") || n.startsWith("60") -> {
                tvBandeira.text = "HIPER"
            }
            else -> {
                tvBandeira.visibility = View.GONE
            }
        }
    }

    private fun confirmarPagamento() {
        val valorTotalStr = String.format(Locale.getDefault(), "R$%.2f", PaymentManager.divida)

        // Zera a dÃ­vida
        PaymentManager.divida = 0.0

        Toast.makeText(this, "O valor $valorTotalStr foi quitado com sucesso.", Toast.LENGTH_LONG).show()

        val intent = Intent(this, TelahomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}