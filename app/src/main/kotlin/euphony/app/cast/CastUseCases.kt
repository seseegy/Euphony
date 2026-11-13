package euphony.app.cast

class ObserveCastStateUseCase(
    private val repository: CastPlaybackRepository,
) {
    operator fun invoke() = repository.screenState
}

class SelectCastRouteUseCase

class DisconnectCastSessionUseCase(
    private val repository: CastPlaybackRepository,
) {
    operator fun invoke() = repository.disconnect()
}

class SetCastVolumeUseCase(
    private val repository: CastPlaybackRepository,
) {
    operator fun invoke(volume: Float) = repository.setVolume(volume)
}
