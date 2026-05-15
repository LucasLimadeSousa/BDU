import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val nome: String,
    val email: String,
    val senha: String,
    val cpf: String,
    val telefone: String,
    val curso: String,
    val cidade: String,
    val estado: String,
    val data_nascimento: String,
    val adm: Boolean = false
)