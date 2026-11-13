package euphony.app.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import euphony.app.R
import euphony.app.ads.presentation.SupportEuphonyScreenState
import euphony.app.ads.presentation.SupportEuphonyUiEvent
import euphony.app.ads.presentation.SupportEuphonyViewModel

internal const val supportEuphonyAvailable = true

@Composable
internal fun SupportEuphonySection(
    modifier: Modifier = Modifier,
    onMessage: (String) -> Unit,
    viewModel: SupportEuphonyViewModel = hiltViewModel(),
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val failureMessage = stringResource(R.string.support_euphony_failed)
    val openSupportPage = remember(viewModel) { viewModel::onSupportEuphonyClick }

    LaunchedEffect(viewModel, onMessage, failureMessage) {
        viewModel.events.collect { event ->
            when (event) {
                SupportEuphonyUiEvent.OpenFailed -> onMessage(failureMessage)
            }
        }
    }

    SupportEuphonyCard(
        state = state,
        onClick = openSupportPage,
        modifier = modifier,
    )
}

@Composable
private fun SupportEuphonyCard(
    state: SupportEuphonyScreenState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerColor = MaterialTheme.colorScheme.primaryContainer
    val contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    val iconContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
    val iconContainerModifier =
        remember(iconContainerColor) {
            Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(iconContainerColor)
        }
    val description =
        when (state) {
            is SupportEuphonyScreenState.Loading -> {
                stringResource(R.string.support_euphony_preparing)
            }

            is SupportEuphonyScreenState.Success -> {
                stringResource(R.string.support_euphony_description)
            }

            is SupportEuphonyScreenState.Empty -> {
                stringResource(R.string.support_euphony_unavailable)
            }

            is SupportEuphonyScreenState.Error -> {
                stringResource(R.string.support_euphony_retry)
            }
        }

    Card(
        onClick = onClick,
        enabled =
            state !is SupportEuphonyScreenState.Loading &&
                state !is SupportEuphonyScreenState.Empty,
        shape = MaterialTheme.shapes.extraLarge,
        colors =
            CardDefaults.cardColors(
                containerColor = containerColor,
                contentColor = contentColor,
                disabledContainerColor = containerColor,
                disabledContentColor = contentColor.copy(alpha = 0.7f),
            ),
        modifier =
            modifier
                .fillMaxWidth()
                .heightIn(min = 128.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 22.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = iconContainerModifier,
            ) {
                Icon(
                    painter = painterResource(R.drawable.star),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(34.dp),
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = stringResource(R.string.support_euphony_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(64.dp),
            ) {
                if (state is SupportEuphonyScreenState.Loading) {
                    CircularProgressIndicator(
                        color = contentColor,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(28.dp),
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.play),
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
        }
    }
}
