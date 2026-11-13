package euphony.app.models

import euphony.app.db.entities.LocalItem
import euphony.app.innertube.models.YTItem

data class SimilarRecommendation(
    val title: LocalItem,
    val items: List<YTItem>,
)
