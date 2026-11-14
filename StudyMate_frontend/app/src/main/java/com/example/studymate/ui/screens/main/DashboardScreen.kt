package com.example.studymate.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.studymate.ui.components.SearchBar
import com.example.studymate.viewmodel.BannerArtwork
import com.example.studymate.viewmodel.DashboardResultType
import com.example.studymate.viewmodel.DashboardSearchResult
import com.example.studymate.viewmodel.DashboardViewModel
import com.example.studymate.ui.screens.notes.NotesViewModel
import com.example.studymate.ui.screens.schedule.ScheduleViewModel
import com.example.studymate.ui.localization.LocalAppLanguage
import com.example.studymate.ui.localization.localizedFormat
import com.example.studymate.ui.localization.localizedUiLabel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class HomeFeature(
    val title: String,
    val icon: ImageVector,
    val destination: String,
    val showProgress: Boolean = false
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun DashboardScreen(
    navController: NavHostController,
    dashboardViewModel: DashboardViewModel = viewModel(),
    scheduleViewModel: ScheduleViewModel = viewModel(),
    notesViewModel: NotesViewModel = viewModel(),
    focusTimerViewModel: FocusTimerViewModel = viewModel()
) {
    val language = LocalAppLanguage.current
    val schedules by scheduleViewModel.fullSchedules.collectAsState()
    val notes by notesViewModel.fullNotes.collectAsState()
    val focusState by focusTimerViewModel.state.collectAsState()
    val focusSessions = focusState.completedFocusSessions
    val progressMetrics = remember(schedules, notes, focusSessions) {
        LearningProgressMetrics.from(schedules, notes, focusSessions)
    }
    val scheduleProgress = if (progressMetrics.totalActivities == 0) 0f else {
        progressMetrics.completedActivities.toFloat() / progressMetrics.totalActivities
    }.coerceIn(0f, 1f)
    val notifications by dashboardViewModel.notifications.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val banners = dashboardViewModel.banners
    val news = dashboardViewModel.news
    var refreshKey by remember { mutableIntStateOf(0) }
    val searchResults = remember(searchQuery, refreshKey, dashboardViewModel) {
        dashboardViewModel.searchContent(searchQuery)
    }
    val features = remember {
        listOf(
            HomeFeature("Progress\nBelajar", Icons.AutoMirrored.Filled.TrendingUp, "progress", showProgress = true),
            HomeFeature("Info Bimbel", Icons.Filled.School, "tutoring_list"),
            HomeFeature("Materi Video", Icons.Filled.PlayCircle, "learning_videos")
        )
    }
    val initialBannerPage = if (banners.isEmpty()) 0 else (Int.MAX_VALUE / 2 / banners.size) * banners.size
    val pagerState = rememberPagerState(
        initialPage = initialBannerPage,
        pageCount = { if (banners.isEmpty()) 0 else Int.MAX_VALUE }
    )
    val colorScheme = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current
    val refreshScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val pullToRefreshState = rememberPullToRefreshState()
    var isRefreshing by remember { mutableStateOf(false) }
    val onRefresh: () -> Unit = {
        if (!isRefreshing) {
            isRefreshing = true
            refreshScope.launch {
                try {
                    refreshKey++
                    delay(800)
                } finally {
                    isRefreshing = false
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.School,
                contentDescription = "StudyMate",
                tint = colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "StudyMate",
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 23.sp, lineHeight = 27.sp),
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground,
                    maxLines = 1
                )
                Text(
                    text = localizedUiLabel(language, "Belajar Lebih Mudah, Raih Masa Depan"),
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp, lineHeight = 15.sp),
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
            BadgedBox(
                badge = {
                    val unread = notifications.count { !it.isRead }
                    if (unread > 0) {
                        Badge { Text(if (unread > 9) "9+" else unread.toString()) }
                    }
                }
            ) {
                IconButton(onClick = { navController.navigate("notifications") }, modifier = Modifier.size(48.dp)) {
                    Icon(
                        Icons.Filled.NotificationsNone,
                        contentDescription = localizedUiLabel(language, "Notifikasi"),
                        tint = colorScheme.onBackground,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        SearchBar(
            query = searchQuery,
            onQueryChange = { searchQuery = it },
            placeholder = "Cari materi, bimbel, berita, dll...",
            modifier = Modifier.padding(bottom = 14.dp),
            compact = true
        )

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            state = pullToRefreshState,
            indicator = {
                if (isRefreshing || pullToRefreshState.distanceFraction >= 1f) {
                    PullToRefreshDefaults.Indicator(
                        state = pullToRefreshState,
                        isRefreshing = isRefreshing,
                        modifier = Modifier.align(Alignment.TopCenter),
                        containerColor = colorScheme.surface,
                        color = colorScheme.primary
                    )
                }
            }
        ) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { focusManager.clearFocus() })
                    },
                contentPadding = PaddingValues(bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
        if (searchQuery.isNotBlank()) {
            item {
                HomeSearchResults(
                    results = searchResults,
                    onResultClick = { result ->
                        val route = when (result.type) {
                            DashboardResultType.News -> "news_detail/${result.id}"
                            DashboardResultType.Tutoring -> "tutoring_detail/${result.id}"
                            DashboardResultType.Video -> "video_detail/${result.id}"
                            DashboardResultType.Banner, DashboardResultType.Feature -> result.destination.orEmpty()
                        }
                        if (route.isNotBlank()) navController.navigate(route)
                    }
                )
            }
        } else {
        item {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(188.dp)
                    .padding(horizontal = 16.dp),
                pageSpacing = 12.dp
            ) { page ->
                val banner = banners[page % banners.size]
                val bannerColor = when (banner.artwork) {
                    BannerArtwork.Exam -> colorScheme.primary
                    BannerArtwork.Tips -> colorScheme.tertiary
                    BannerArtwork.Tutoring -> colorScheme.surfaceVariant
                }
                val contentColor = if (banner.artwork == BannerArtwork.Exam) colorScheme.onPrimary else colorScheme.onSurface
                val bannerIcon = when (banner.artwork) {
                    BannerArtwork.Exam -> Icons.Filled.School
                    BannerArtwork.Tips -> Icons.Filled.Lightbulb
                    BannerArtwork.Tutoring -> Icons.Filled.LocationCity
                }

                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = bannerColor),
                    onClick = { navController.navigate(banner.destination) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 18.dp, top = 16.dp, end = 14.dp, bottom = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = banner.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = contentColor,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = banner.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = contentColor,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(9.dp))
                            TextButton(
                                onClick = { navController.navigate(banner.destination) },
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                colors = ButtonDefaults.textButtonColors(contentColor = contentColor)
                            ) {
                                Text(banner.actionText, style = MaterialTheme.typography.labelMedium)
                                Icon(Icons.Filled.ChevronRight, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(contentColor.copy(alpha = 0.13f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = bannerIcon,
                                contentDescription = null,
                                tint = contentColor,
                                modifier = Modifier.size(54.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 11.dp, bottom = 22.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(banners.size) { page ->
                    val selected = pagerState.currentPage % banners.size == page
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .size(width = if (selected) 16.dp else 6.dp, height = 6.dp)
                            .clip(CircleShape)
                            .background(if (selected) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.2f))
                    )
                }
            }
        }

        item {
            Text(
                text = localizedUiLabel(language, "Fitur Utama StudyMate"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.onBackground,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                features.forEach { feature ->
                    Card(
                        onClick = { navController.navigate(feature.destination) },
                        modifier = Modifier
                            .weight(1f)
                            .height(112.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 6.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = feature.icon,
                                contentDescription = localizedUiLabel(language, feature.title.replace("\n", " ")),
                                tint = colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.height(7.dp))
                            Text(
                                text = localizedUiLabel(language, feature.title),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.onSurface,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (feature.showProgress) {
                                Text(
                                    "${(scheduleProgress * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                LinearProgressIndicator(
                                    progress = { scheduleProgress },
                                    modifier = Modifier.width(56.dp).height(3.dp),
                                    color = colorScheme.primary,
                                    trackColor = colorScheme.surface
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = localizedUiLabel(language, "Berita Pendidikan"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = { navController.navigate("news_list") },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(localizedUiLabel(language, "Lihat Semua"), style = MaterialTheme.typography.labelMedium)
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }
        }

        itemsIndexed(news, key = { _, item -> item.id }, contentType = { _, _ -> "home_news" }) { index, newsItem ->
            Column {
                if (index > 0) Spacer(Modifier.height(10.dp))
                NewsItemCard(
                    news = newsItem,
                    onClick = { navController.navigate("news_detail/${newsItem.id}") }
                )
            }
        }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
        }
    }
}

@Composable
private fun HomeSearchResults(results: List<DashboardSearchResult>, onResultClick: (DashboardSearchResult) -> Unit) {
    val language = LocalAppLanguage.current
    val resultGroups = listOf(
        DashboardResultType.Video to "Materi Video",
        DashboardResultType.Tutoring to "Info Bimbel",
        DashboardResultType.News to "Berita Pendidikan",
        DashboardResultType.Banner to "Informasi",
        DashboardResultType.Feature to "Fitur StudyMate"
    ).mapNotNull { (type, title) ->
        results.filter { it.type == type }.takeIf { it.isNotEmpty() }?.let { title to it }
    }
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
        Text(localizedUiLabel(language, "Hasil pencarian"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        if (results.isEmpty()) {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 34.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(localizedUiLabel(language, "Tidak ada hasil yang cocok"), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(localizedUiLabel(language, "Hapus atau ubah kata kunci untuk mencoba lagi."), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            }
        } else {
            Column(modifier = Modifier.padding(top = 10.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                resultGroups.forEach { (groupTitle, groupResults) ->
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            localizedUiLabel(language, groupTitle),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        groupResults.forEach { result ->
                            val (icon, routeType) = when (result.type) {
                                DashboardResultType.News -> Icons.AutoMirrored.Filled.MenuBook to "Berita"
                                DashboardResultType.Tutoring -> Icons.Filled.School to "Bimbel"
                                DashboardResultType.Video -> Icons.Filled.PlayCircle to "Video"
                                DashboardResultType.Banner -> Icons.Filled.Lightbulb to "Informasi"
                                DashboardResultType.Feature -> Icons.AutoMirrored.Filled.TrendingUp to "Fitur"
                            }
                            Card(
                                onClick = { onResultClick(result) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(result.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                        Text("$routeType · ${result.supportingText}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    }
                                    Icon(
                                        Icons.Filled.ChevronRight,
                                        contentDescription = localizedFormat(language, "open_item", result.title),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = value,
                    style = MaterialTheme.typography.displayMedium,
                    color = Color(0xFF3F51B5)
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}