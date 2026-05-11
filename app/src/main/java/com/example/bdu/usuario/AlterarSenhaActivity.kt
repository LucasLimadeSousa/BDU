package com.example.bdu.usuario

import android.graphics.Color
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R
import com.example.bdu.login.EsqueciSenhaActivity
import com.example.bdu.login.LoginActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AlterarSenhaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.usuario_alterar_senha)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageButton>(R.id.imageButton2).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.textView24).setOnClickListener {
            val intent = Intent(this, EsqueciSenhaActivity::class.java)
            startActivity(intent)
        }

        findViewById<MaterialButton>(R.id.button4).setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this, R.style.CustomAlertDialog)
            builder.setMessage("Tem certeza que deseja alterar a senha?")
            
            builder.setPositiveButton("Sim") { _, _ ->
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            
            builder.setNegativeButton("Não") { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()

            // Personalizando as cores dos botões para corresponder à imagem
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.let {
                it.setBackgroundColor("#2E7D32".toColorInt()) // Verde
                it.setTextColor(Color.WHITE)
                
                val params = it.layoutParams as android.widget.LinearLayout.LayoutParams
                params.setMargins(10, 0, 10, 0)
                it.layoutParams = params
            }

            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.let {
                it.setBackgroundColor("#D32F2F".toColorInt()) // Vermelho
                it.setTextColor(Color.WHITE)

                val params = it.layoutParams as android.widget.LinearLayout.LayoutParams
                params.setMargins(10, 0, 10, 0)
                it.layoutParams = params
            }
        }
    }
}