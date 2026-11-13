package euphony.app.viewmodels

internal fun shouldSynchronizeAuthenticatedSession(
    previousLoginState: Boolean?,
    isLoggedIn: Boolean,
): Boolean = isLoggedIn && previousLoginState != true
