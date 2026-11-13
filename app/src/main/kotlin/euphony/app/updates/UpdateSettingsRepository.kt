package euphony.app.updates

import android.content.Context
import androidx.datastore.preferences.core.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import euphony.app.constants.AutomaticUpdateCheckKey
import euphony.app.constants.EnableUpdateNotificationKey
import euphony.app.utils.dataStore
import javax.inject.Inject
import javax.inject.Singleton

data class UpdateSettings(
    val automaticChecksEnabled: Boolean,
    val notificationsEnabled: Boolean,
)

@Singleton
class UpdateSettingsRepository
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) {
        fun observeSettings(): Flow<UpdateSettings> =
            context.dataStore.data
                .map { preferences ->
                    UpdateSettings(
                        automaticChecksEnabled = preferences[AutomaticUpdateCheckKey] ?: true,
                        notificationsEnabled = preferences[EnableUpdateNotificationKey] ?: false,
                    )
                }.distinctUntilChanged()

        suspend fun setAutomaticChecksEnabled(enabled: Boolean) {
            withContext(Dispatchers.IO) {
                context.dataStore.edit { preferences ->
                    preferences[AutomaticUpdateCheckKey] = enabled
                }
            }
        }

        suspend fun setNotificationsEnabled(enabled: Boolean) {
            withContext(Dispatchers.IO) {
                context.dataStore.edit { preferences ->
                    preferences[EnableUpdateNotificationKey] = enabled
                }
            }
        }
    }
