package euphony.app.updates

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import euphony.app.utils.Updater
import java.time.LocalDate
import java.time.format.DateTimeParseException
import javax.inject.Inject

data class ChangelogRelease(
    val tag: String,
    val version: String,
    val title: String,
    val publishedDate: LocalDate?,
    val notes: String?,
)

class ObserveChangelogUseCase @Inject constructor(
    private val repository: ChangelogRepository,
) {
    operator fun invoke(): Flow<List<ChangelogRelease>> =
        repository.observeReleases().map { releases ->
            releases.map { release ->
                ChangelogRelease(
                    tag = release.tagName,
                    version = Updater.getReleaseVersionName(release),
                    title = release.name.ifBlank { release.tagName },
                    publishedDate =
                        try {
                            LocalDate.parse(release.publishedAt.take(10))
                        } catch (_: DateTimeParseException) {
                            null
                        },
                    notes = release.body?.takeIf { it.isNotBlank() },
                )
            }
        }
}
