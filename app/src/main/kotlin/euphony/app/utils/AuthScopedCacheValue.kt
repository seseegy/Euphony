package euphony.app.utils

data class AuthScopedCacheValue(
    val url: String,
    val expiresAtMs: Long,
    val authFingerprint: String,
) {
    fun isValidFor(
        authFingerprint: String,
        nowMs: Long = System.currentTimeMillis(),
        minimumRemainingMs: Long = 0L,
    ): Boolean = this.authFingerprint == authFingerprint && expiresAtMs > nowMs + minimumRemainingMs
}
