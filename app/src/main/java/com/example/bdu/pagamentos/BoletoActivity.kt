package com.example.bdu.pagamentos

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R

class BoletoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pagamentos_boleto)

        findViewById<ImageButton>(R.id.imgBtnVoltarBoleto).setOnClickListener {
            val intent = Intent(this, R.layout.pagamentos_selecionar_metodo::class.java)
            startActivity(intent)
            finish()
        }

        }
    }
