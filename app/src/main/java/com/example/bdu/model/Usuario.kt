package com.example.bdu.model

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val id: String? = null,
    val nome: String,
    val email: String,
    val senha: String,
    val cpf: String,
    val telefone: String,
    val curso: String,
    val cidade: String,
    val estado: String,
    val data_nascimento: String,
    val adm: Boolean = false,
    val foto: String? = null
)
