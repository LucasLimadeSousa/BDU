package com.example.tigrao

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    lateinit var etLogin: EditText
    lateinit var etSenha: EditText
    lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etLogin = findViewById(R.id.editTextEmail)
        etSenha = findViewById(R.id.editTextSenha)
        btnLogin = findViewById(R.id.buttonLogar)

        }

    override fun onStart() {
        super.onStart()

        btnLogin.setOnClickListener {
            validarCampos()
        }
    }

    fun validarCampos() {

        if(etLogin.text.toString() == "123" && etSenha.text.toString() == "123"){
            startActivity(Intent(this, MainActivity3::class.java))
        }

        if(etLogin.text.toString() == "thiago@unifor.com"){
            if(etSenha.text.toString() == "123456"){
                Toast.makeText(this,
                    "Login efetuado com sucesso",
                Toast.LENGTH_SHORT)
                    .show()
                var intent = Intent(this, MainActivity2::class.java)
                startActivity(intent)
            } else{
                Toast.makeText(this,
                    "Senha incorreta",
                    Toast.LENGTH_SHORT)
                    .show()

            }
        } else {
            Toast.makeText(this,
                "Email incorreto",
                Toast.LENGTH_SHORT)
                .show()
        }
    }
}
