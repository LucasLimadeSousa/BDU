package com.example.bdu

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity

class PixActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.pagamentos_pix)

        findViewById<ImageButton>(R.id.btnVoltarPix).setOnClickListener{
            startActivity(Intent(this, R.layout.pagamentos_selecionar_metodo::class.java))
        }


        }
    }
