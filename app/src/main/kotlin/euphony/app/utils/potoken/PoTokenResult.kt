package euphony.app.utils.potoken

/**
 * Holds the two PoToken variants produced by a single BotGuard minting cycle.
 *
 * - [playerToken]: bound to a specific video — sent in the player request's
 *   `serviceIntegrityDimensions.poToken`.
 * - [sessionToken]: bound to the visitor/dataSync session — retained as a
 *   fallback for clients that accept session-bound GVS tokens.
 *
 * The video-bound player token is also used for Web GVS and subtitle requests
 * because the Web BotGuard provider exposes one content-bound token for the
 * requested video. It is never sent to Android, iOS, or Android VR clients.
 */
data class PoTokenResult(
    val playerToken: String,
    val sessionToken: String,
)
