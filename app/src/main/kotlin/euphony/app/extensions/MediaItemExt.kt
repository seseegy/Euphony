package euphony.app.extensions

import android.os.Bundle
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata.MEDIA_TYPE_MUSIC
import androidx.media3.common.MediaMetadata.MEDIA_TYPE_PODCAST_EPISODE
import euphony.app.db.entities.Song
import euphony.app.innertube.models.EpisodeItem
import euphony.app.innertube.models.SongItem
import euphony.app.innertube.models.WatchEndpoint.WatchEndpointMusicSupportedConfigs.WatchEndpointMusicConfig.Companion.MUSIC_VIDEO_TYPE_OMV
import euphony.app.innertube.models.WatchEndpoint.WatchEndpointMusicSupportedConfigs.WatchEndpointMusicConfig.Companion.MUSIC_VIDEO_TYPE_UGC
import euphony.app.models.MediaMetadata
import euphony.app.models.toMediaMetadata
import euphony.app.ui.utils.YTThumbQuality
import euphony.app.ui.utils.YtimgResizePolicy
import euphony.app.ui.utils.buildYTThumbnailUrl
import euphony.app.ui.utils.resize
import euphony.app.utils.NotificationArtworkSizePx
import euphony.app.utils.isLocalMediaId

const val ExtraIsMusicVideo = "euphony.app.extra.IS_MUSIC_VIDEO"
const val ExtraIsPodcast = "euphony.app.extra.IS_PODCAST"

val MediaItem.metadata: MediaMetadata?
    get() = localConfiguration?.tag as? MediaMetadata

private fun String?.toNotificationArtworkUri() =
    this
        ?.resize(
            width = NotificationArtworkSizePx,
            height = NotificationArtworkSizePx,
            ytimgResizePolicy = YtimgResizePolicy.PreserveOriginal,
        )?.toUri()

private fun MediaItem.Builder.setCacheKeyIfRemote(mediaId: String): MediaItem.Builder {
    if (!mediaId.isLocalMediaId()) {
        setCustomCacheKey(mediaId)
    }
    return this
}

fun Song.toMediaItem() =
    MediaItem
        .Builder()
        .setMediaId(song.id)
        .setUri(song.id)
        .setCacheKeyIfRemote(song.id)
        .setTag(toMediaMetadata())
        .setMediaMetadata(
            androidx.media3.common.MediaMetadata
                .Builder()
                .setTitle(song.title)
                .setSubtitle(artists.joinToString { it.name })
                .setArtist(artists.joinToString { it.name })
                .setArtworkUri(
                    if (song.isMusicVideo) {
                        buildYTThumbnailUrl(song.id, YTThumbQuality.HQ).toUri()
                    } else {
                        song.thumbnailUrl.toNotificationArtworkUri()
                    },
                )
                .setAlbumTitle(song.albumName)
                .setAlbumArtist(song.albumArtist)
                .setTrackNumber(song.trackNumber)
                .setDiscNumber(song.discNumber)
                .setReleaseYear(song.year)
                .setIsPlayable(true)
                .setMediaType(if (song.isPodcast) MEDIA_TYPE_PODCAST_EPISODE else MEDIA_TYPE_MUSIC)
                .setExtras(
                    Bundle().apply {
                        putBoolean(ExtraIsMusicVideo, song.isMusicVideo)
                        putBoolean(ExtraIsPodcast, song.isPodcast)
                    },
                )
                .build(),
        ).build()

fun SongItem.toMediaItem() =
    MediaItem
        .Builder()
        .setMediaId(id)
        .setUri(id)
        .setCacheKeyIfRemote(id)
        .setTag(toMediaMetadata())
        .setMediaMetadata(
            androidx.media3.common.MediaMetadata
                .Builder()
                .setTitle(title)
                .setSubtitle(artists.joinToString { it.name })
                .setArtist(artists.joinToString { it.name })
                .setArtworkUri(
                    if (isMusicVideo()) {
                        buildYTThumbnailUrl(id, YTThumbQuality.HQ).toUri()
                    } else {
                        thumbnail.toNotificationArtworkUri()
                    },
                ).setAlbumTitle(album?.name)
                .setIsPlayable(true)
                .setMediaType(MEDIA_TYPE_MUSIC)
                .setExtras(
                    Bundle().apply {
                        putBoolean(ExtraIsMusicVideo, isMusicVideo())
                        putBoolean(ExtraIsPodcast, false)
                    },
                )
                .build(),
        ).build()

fun EpisodeItem.toMediaItem() = toMediaMetadata().toMediaItem()

fun MediaMetadata.toMediaItem() =
    MediaItem
        .Builder()
        .setMediaId(id)
        .setUri(id)
        .setCacheKeyIfRemote(id)
        .setTag(this)
        .setMediaMetadata(
            androidx.media3.common.MediaMetadata
                .Builder()
                .setTitle(title)
                .setSubtitle(artists.joinToString { it.name })
                .setArtist(artists.joinToString { it.name })
                .setArtworkUri(
                    if (isMusicVideo) {
                        buildYTThumbnailUrl(id, YTThumbQuality.HQ).toUri()
                    } else {
                        thumbnailUrl.toNotificationArtworkUri()
                    },
                ).setAlbumTitle(album?.title)
                .setAlbumArtist(albumArtist)
                .setTrackNumber(trackNumber)
                .setDiscNumber(discNumber)
                .setReleaseYear(year)
                .setIsPlayable(true)
                .setMediaType(if (isPodcast) MEDIA_TYPE_PODCAST_EPISODE else MEDIA_TYPE_MUSIC)
                .setExtras(
                    Bundle().apply {
                        putBoolean(ExtraIsMusicVideo, isMusicVideo)
                        putBoolean(ExtraIsPodcast, isPodcast)
                    },
                )
                .build(),
        ).build()

private fun SongItem.isMusicVideo(): Boolean {
    val musicVideoType = endpoint?.watchEndpointMusicSupportedConfigs?.watchEndpointMusicConfig?.musicVideoType
    return musicVideoType == MUSIC_VIDEO_TYPE_OMV || musicVideoType == MUSIC_VIDEO_TYPE_UGC
}
