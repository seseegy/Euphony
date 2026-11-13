package euphony.app.musicrecognition

internal enum class BackgroundRecognitionState {
    Idle,
    Listening,
    Processing,
}

internal object MusicRecognitionRuntimeState {
    @Volatile
    var state: BackgroundRecognitionState = BackgroundRecognitionState.Idle
        private set

    fun update(state: BackgroundRecognitionState) {
        this.state = state
    }
}
