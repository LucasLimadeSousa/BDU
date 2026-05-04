package com.example.bdu.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R
import com.example.bdu.suporte.TermosCondicoesActivity

class CadastroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_cadastro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<TextView>(R.id.btnFinalizar).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        val checkboxTermos = findViewById<TextView>(R.id.termosLink)
        val textoCompleto = "Concordo com os termos e condições"
        val textoLink = "termos e condições"
        val inicio = textoCompleto.indexOf(textoLink)
        val fim = inicio + textoLink.length

        val spannable = SpannableString(textoCompleto)

        spannable.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this, R.color.unifor_blue)),
            inicio,fim, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            StyleSpan(android.graphics.Typeface.BOLD),
            inicio,fim, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            object : ClickableSpan(){
                override fun onClick(widget: View) {
                    startActivity(Intent(this@CadastroActivity, TermosCondicoesActivity::class.java))
                }
            },
            inicio, fim, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        checkboxTermos.text = spannable
        checkboxTermos.movementMethod = LinkMovementMethod.getInstance()
        checkboxTermos.highlightColor = Color.TRANSPARENT

        findViewById<ImageButton>(R.id.btn_backReturn).setOnClickListener {
            finish()
        }
    }
}