package pl.mareklangiewicz.koder

actual object KoderFactory {
    actual fun provideKoder(): Koder = NativeKoder
}

object NativeKoder : Koder {
    // TODO: use password hashCode (or some cross-platform hash?)
    override fun encode(password: String, src: ByteArray) = encode64(src)

    // TODO: use password hashCode
    override fun decode(password: String, src: ByteArray): ByteArray {
        TODO()
    }
}

private val BASE64_ALPHABET: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
private val BASE64_MASK: Byte = 0x3f
private val BASE64_PAD: Char = '='
private val BASE64_INVERSE_ALPHABET = IntArray(256) {
    BASE64_ALPHABET.indexOf(it.toChar())
}

private fun Int.toBase64(): Char = BASE64_ALPHABET[this]

private fun encode64(src: ByteArray): ByteArray {
    fun ByteArray.getOrZero(index: Int): Int = if (index >= size) 0 else get(index).toInt()
    // 4n / 3 is expected Base64 payload
    val result = ArrayList<Byte>(4 * src.size / 3)
    var index = 0
    while (index < src.size) {
        val symbolsLeft = src.size - index
        val padSize = if (symbolsLeft >= 3) 0 else (3 - symbolsLeft) * 8 / 6
        val chunk = (src.getOrZero(index) shl 16) or (src.getOrZero(index + 1) shl 8) or src.getOrZero(index + 2)
        index += 3

        for (i in 3 downTo padSize) {
            val char = (chunk shr (6 * i)) and BASE64_MASK.toInt()
            result.add(char.toBase64().toByte())
        }
        // Fill the pad with '='
        repeat(padSize) { result.add(BASE64_PAD.toByte()) }
    }

    return result.toByteArray()
}
