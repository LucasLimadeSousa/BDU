package com.example.bdu.livros

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bdu.R
import com.example.bdu.model.ChatMessage
import com.example.bdu.model.Sender
import dev.shreyaspatil.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class LivrosChatbotActivity : AppCompatActivity() {

    private lateinit var rvMessages: RecyclerView
    private lateinit var etMessage: EditText
    private lateinit var btnSend: ImageButton
    private val messagesList = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    // Configuração do Gemini
    private val geminiApiKey = "AQ.Ab8RN6K28Fb4ISOhzF7Z7trchSxH42Qrx61beQDb_fSzPzGwHw"
    
    // Instruções de sistema para definir o comportamento e personalidade
    private val systemInstructions = """
        Você é o assistente virtual da BDU. 
        - Responda estritamente em formato de texto simples.
        - NUNCA use markdown (como asteriscos *, negrito **, ou listas com *).
        - Use apenas texto limpo, parágrafos bem definidos e pontuação comum.
        - Se precisar listar livros, apenas pule uma linha e use números simples (1., 2.).
        - SEJA DIRETO: Vá direto ao ponto. Não se apresente, não diga "Olá", não peça desculpas e não repita saudações em nenhuma resposta.
    """.trimIndent()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-3.1-flash-lite",
        apiKey = geminiApiKey
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.livros_chatbot)
        
        configurarInsets()
        initViews()
        setupRecyclerView()

        findViewById<ImageButton>(R.id.btn_voltar).setOnClickListener {
            finish()
        }

        btnSend.setOnClickListener {
            sendMessage()
        }

        // Mensagem inicial amigável
        if (messagesList.isEmpty()) {
            addBotMessage("Olá! Sou o assistente da BDU (v2). Posso te ajudar a encontrar livros, tirar dúvidas sobre o acervo ou dar recomendações. Como posso ajudar?")
        }
    }

    private fun initViews() {
        rvMessages = findViewById(R.id.rv_chat_messages)
        etMessage = findViewById(R.id.et_message)
        btnSend = findViewById(R.id.btn_send)
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messagesList)
        rvMessages.adapter = chatAdapter
        rvMessages.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
    }

    private fun sendMessage() {
        val text = etMessage.text.toString().trim()
        if (text.isNotEmpty()) {
            val userMessage = ChatMessage(text, Sender.USER)
            messagesList.add(userMessage)
            chatAdapter.notifyItemInserted(messagesList.size - 1)
            rvMessages.scrollToPosition(messagesList.size - 1)
            etMessage.text.clear()

            getAIResponse(text)
        }
    }

    private fun addBotMessage(text: String) {
        val botMessage = ChatMessage(text, Sender.BOT)
        messagesList.add(botMessage)
        chatAdapter.notifyItemInserted(messagesList.size - 1)
        rvMessages.scrollToPosition(messagesList.size - 1)
    }

    private fun getAIResponse(userInput: String) {
        lifecycleScope.launch {
            try {
                // Combina as instruções de sistema com a entrada do usuário
                val fullPrompt = "$systemInstructions\n\nUsuário: $userInput"
                val response = generativeModel.generateContent(fullPrompt)
                val responseText = response.text ?: "Desculpe, não consegui processar sua mensagem."
                addBotMessage(responseText)

            } catch (e: Exception) {
                val errorMessage = e.localizedMessage ?: ""
                if (errorMessage.contains("quota", ignoreCase = true) || errorMessage.contains("limit", ignoreCase = true)) {
                    addBotMessage("Ops! O limite de mensagens gratuitas da IA foi atingido ou a chave atual está sem cota ativa.\n\nTente usar uma nova API Key gratuita do Google AI Studio (https://aistudio.google.com/), que comece com 'AIzaSy'.")
                } else {
                    addBotMessage("Ops! Tive um problema ao me conectar com a IA: $errorMessage")
                }
            }
        }
    }

    private fun configurarInsets() {
        findViewById<android.view.View?>(R.id.main)?.let { mainView ->
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }
}
