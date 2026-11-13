package euphony.app.playback.stream

interface AudioStreamRepository {
    suspend fun resolve(request: AudioStreamRequest): ResolvedAudioStream
}
