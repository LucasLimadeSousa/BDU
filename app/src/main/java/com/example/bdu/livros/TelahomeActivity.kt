package com.example.bdu.livros

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bdu.R
import com.example.bdu.usuario.MeuPerfilActivity

class TelahomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.livros_telahome)

        val btnsearch = findViewById<LinearLayout?>(R.id.search_container)

        val btnvertudo = findViewById<TextView?>(R.id.VerTudo)
        val btnvertudo2 = findViewById<TextView?>(R.id.VerTudo2)
        val btnvertudo3 = findViewById<TextView?>(R.id.VerTudo3)

        val btnFila = findViewById<ImageButton?>(R.id.btn_nav_fila)
        val btnMeusLivros = findViewById<ImageButton?>(R.id.btn_nav_meuslivros)
        val btnHome = findViewById<ImageButton?>(R.id.btn_nav_home)
        val btnDesejos = findViewById<ImageButton?>(R.id.btn_nav_desejos)
        val btnperfil = findViewById<ImageButton?>(R.id.btn_nav_perfil)

        //Scroll 1

        val livroA = findViewById<View>(R.id.item_livro_a)
        val livroB = findViewById<View>(R.id.item_livro_b)
        val livroC = findViewById<View>(R.id.item_livro_c)
        val livroD = findViewById<View>(R.id.item_livro_d)
        val livroE = findViewById<View>(R.id.item_livro_e)

        // Scroll 2

        val livro1 = findViewById<View>(R.id.item_livro_1)
        val livro2 = findViewById<View>(R.id.item_livro_2)
        val livro3 = findViewById<View>(R.id.item_livro_3)
        val livro4 = findViewById<View>(R.id.item_livro_4)
        val livro5 = findViewById<View>(R.id.item_livro_5)

        //Scroll 3

        val livro10 = findViewById<View>(R.id.item_livro_10)
        val livro20 = findViewById<View>(R.id.item_livro_20)
        val livro30 = findViewById<View>(R.id.item_livro_30)
        val livro40 = findViewById<View>(R.id.item_livro_40)
        val livro50 = findViewById<View>(R.id.item_livro_50)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnsearch?.setOnClickListener {
            val intent = Intent(this, BuscaActivity::class.java)
            startActivity(intent)
        }

        //ver tudo

        btnvertudo?.setOnClickListener {
            val intent = Intent(this, VerTudoActivity::class.java)
            startActivity(intent)
        }
        btnvertudo2?.setOnClickListener {
            val intent = Intent(this, VerTudoActivity::class.java)
            startActivity(intent)
        }
        btnvertudo3?.setOnClickListener {
            val intent = Intent(this, VerTudoActivity::class.java)
            startActivity(intent)
        }
        btnFila?.setOnClickListener {
            val intent = Intent(this, ListadeEsperaActivity::class.java)
            startActivity(intent)
        }
        btnMeusLivros?.setOnClickListener {
            val intent = Intent(this, MeusLivrosActivity::class.java)
            startActivity(intent)
        }

        btnHome?.setOnClickListener {
            val intent = Intent(this, TelahomeActivity::class.java)
            startActivity(intent)
        }

        btnDesejos?.setOnClickListener {
            val intent = Intent(this, ListaDesejosActivity::class.java)
            startActivity(intent)
        }

        btnperfil?.setOnClickListener {
            val intent = Intent(this, MeuPerfilActivity::class.java)
            startActivity(intent)
        }
        //Scroll 1

        livroA?.setOnClickListener {
            val intent = Intent(this, PaginaDoLivroActivity::class.java)
            startActivity(intent)
            finish()
        }
        livroB?.setOnClickListener {
            val intent = Intent(this, PaginaDoLivroActivity::class.java)
            startActivity(intent)
            finish()
        }
        livroC?.setOnClickListener {
            val intent = Intent(this, PaginaDoLivroActivity::class.java)
            startActivity(intent)
            finish()
        }
        livroD?.setOnClickListener {
            val intent = Intent(this, PaginaDoLivroActivity::class.java)
            startActivity(intent)
            finish()
        }
        livroE?.setOnClickListener {
            val intent = Intent(this, PaginaDoLivroActivity::class.java)
            startActivity(intent)
            finish()
        }
        //Scroll 2

        livro1?.setOnClickListener {
            val intent = Intent(this, PaginaDoLivroActivity::class.java)
            startActivity(intent)
            finish()
        }
        livro2?.setOnClickListener {
            val intent = Intent(this, PaginaDoLivroActivity::class.java)
            startActivity(intent)
            finish()
        }
        livro3?.setOnClickListener {
            val intent = Intent(this, PaginaDoLivroActivity::class.java)
            startActivity(intent)
            finish()
        }
        livro4?.setOnClickListener {
            val intent = Intent(this, PaginaDoLivroActivity::class.java)
            startActivity(intent)
            finish()
        }
        livro5?.setOnClickListener {
            val intent = Intent(this, PaginaDoLivroActivity::class.java)
            startActivity(intent)
            finish()
        }
        //Scroll 3

        livro10?.setOnClickListener {
            val intent = Intent(this, PaginaDoLivroActivity::class.java)
            startActivity(intent)
            finish()
        }
        livro20?.setOnClickListener {
            val intent = Intent(this, PaginaDoLivroActivity::class.java)
            startActivity(intent)
            finish()
        }
        livro30?.setOnClickListener {
            val intent = Intent(this, PaginaDoLivroActivity::class.java)
            startActivity(intent)
            finish()
        }
        livro40?.setOnClickListener {
            val intent = Intent(this, PaginaDoLivroActivity::class.java)
            startActivity(intent)
            finish()
        }
        livro50?.setOnClickListener {
            val intent = Intent(this, PaginaDoLivroActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}