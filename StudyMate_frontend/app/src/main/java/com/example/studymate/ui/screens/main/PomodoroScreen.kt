package com.example.studymate.ui.screens.main

import android.media.AudioManager
import android.media.ToneGenerator
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studymate.ui.components.StudyMateButton
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlin.math.min
import com.example.studymate.ui.localization.LocalAppLanguage
import com.example.studymate.ui.localization.appString
import com.example.studymate.ui.localization.localizedUiLabel

@Composable
internal fun PomodoroScreen(timerViewModel: FocusTimerViewModel = viewModel()) {
    val language = LocalAppLanguage.current
    val context = LocalContext.current
    val timer by timerViewModel.state.collectAsState()
    var showSettings by remember { mutableStateOf(false) }
    var showRunningModeWarning by remember { mutableStateOf(false) }
    var showZeroDurationError by remember { mutableStateOf(false) }

    LaunchedEffect(timer.completionCount) {
        if (timer.completionCount > 0) {
            Toast.makeText(context, appString(language, "time_finished"), Toast.LENGTH_SHORT).show()
            runCatching {
                val tone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 85)
                tone.startTone(ToneGenerator.TONE_PROP_ACK, 250)
                delay(300L)
                tone.release()
            }
        }
    }

    val rawProgress = if (timer.totalDurationMillis <= 0L) 0f else {
        (timer.remainingMillis.toFloat() / timer.totalDurationMillis.toFloat()).coerceIn(0f, 1f)
    }
    val ringProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = tween(durationMillis = 100, easing = LinearEasing),
        label = "focus_timer_progress"
    )
    val modeTitle = when (timer.mode) {
        FocusTimerMode.Focus -> appString(language, "study_focus")
        FocusTimerMode.ShortBreak -> appString(language, "short_break")
        FocusTimerMode.LongBreak -> appString(language, "long_break")
        FocusTimerMode.Custom -> appString(language, "timer")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(start = 16.dp, top = 4.dp, end = 16.dp, bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = modeTitle,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = "${appString(language, "mode")}: ${when (timer.mode) {
                FocusTimerMode.Focus -> localizedUiLabel("Fokus")
                FocusTimerMode.ShortBreak -> appString(language, "short_break")
                FocusTimerMode.LongBreak -> appString(language, "long_break")
                FocusTimerMode.Custom -> appString(language, "timer")
            }}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        BoxWithConstraints(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            val ringSize = (maxWidth - 16.dp).coerceAtMost(300.dp)
            Box(modifier = Modifier.size(ringSize), contentAlignment = Alignment.Center) {
                val trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                val progressColor = MaterialTheme.colorScheme.primary
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 10.dp.toPx()
                    val inset = strokeWidth / 2f + 2.dp.toPx()
                    val diameter = min(size.width, size.height) - 2f * inset
                    val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
                    drawArc(
                        color = trackColor,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(diameter, diameter),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                    if (ringProgress > 0f) {
                        drawArc(
                            color = progressColor,
                            startAngle = -90f,
                            sweepAngle = 360f * ringProgress,
                            useCenter = false,
                            topLeft = topLeft,
                            size = Size(diameter, diameter),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatTimer(timer.remainingMillis, timer.mode == FocusTimerMode.Custom),
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = if (timer.mode == FocusTimerMode.Custom && timer.remainingMillis >= 3_600_000L) 38.sp else 48.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 54.sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                    )
                    Text(
                        text = modeTitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                IconButton(
                    onClick = { showSettings = true },
                    modifier = Modifier.align(Alignment.TopEnd).offset(x = 8.dp, y = (-8).dp).size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = appString(language, "timer_settings"),
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(21.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StudyMateButton(
                text = if (timer.isRunning) appString(language, "pause") else if (timer.remainingMillis < timer.totalDurationMillis) appString(language, "resume") else appString(language, "start"),
                onClick = {
                    if (timer.isRunning) timerViewModel.pause()
                    else if (timer.mode == FocusTimerMode.Custom) timerViewModel.startCustomTimer()
                    else timerViewModel.startPomodoro()
                },
                modifier = Modifier.weight(1f)
            )
            OutlinedButton(
                onClick = { timerViewModel.stop() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(localizedUiLabel("Stop"))
            }
        }

        if (timer.mode != FocusTimerMode.Custom) {
            Text(
                text = "${appString(language, "session")} ${if (timer.mode == FocusTimerMode.Focus) localizedUiLabel("Belajar") else appString(language, "break")}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }

    if (showSettings) {
        TimerSettingsDialog(
            timer = timer,
            onDismiss = { showSettings = false },
            onSelectFocus = timerViewModel::setFocusDuration,
            onSelectShortBreak = timerViewModel::setShortBreakDuration,
            onSelectLongBreak = timerViewModel::setLongBreakDuration,
            onSetHours = timerViewModel::setCustomHours,
            onSetMinutes = timerViewModel::setCustomMinutes,
            onSetSeconds = timerViewModel::setCustomSeconds,
            onStartCustom = {
                when {
                    timer.isRunning -> showRunningModeWarning = true
                    timerViewModel.startCustomTimer() -> showSettings = false
                    else -> showZeroDurationError = true
                }
            },
            onUsePomodoro = {
                if (timerViewModel.usePomodoroMode()) showSettings = false
                else showRunningModeWarning = true
            },
            showZeroDurationError = showZeroDurationError,
            clearZeroDurationError = { showZeroDurationError = false }
        )
    }

    if (showRunningModeWarning) {
        AlertDialog(
            onDismissRequest = { showRunningModeWarning = false },
            title = { Text(localizedUiLabel("Timer sedang berjalan")) },
            text = { Text(localizedUiLabel("Pause atau stop timer sebelum mengganti mode.")) },
            confirmButton = {
                TextButton(onClick = { showRunningModeWarning = false }) { Text(localizedUiLabel("Mengerti")) }
            }
        )
    }
}

@Composable
private fun TimerSettingsDialog(
    timer: FocusTimerState,
    onDismiss: () -> Unit,
    onSelectFocus: (Int) -> Unit,
    onSelectShortBreak: (Int) -> Unit,
    onSelectLongBreak: (Int) -> Unit,
    onSetHours: (Int) -> Unit,
    onSetMinutes: (Int) -> Unit,
    onSetSeconds: (Int) -> Unit,
    onStartCustom: () -> Unit,
    onUsePomodoro: () -> Unit,
    showZeroDurationError: Boolean,
    clearZeroDurationError: () -> Unit
) {
    val language = LocalAppLanguage.current
    var focusExpanded by remember { mutableStateOf(true) }
    var showCustomPicker by remember { mutableStateOf(false) }
    val customSeconds = timerTotalSeconds(timer.customHours, timer.customMinutes, timer.customSeconds)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(appString(language, "timer_settings"), style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 480.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AccordionHeader("Fokus Belajar", focusExpanded) { focusExpanded = !focusExpanded }
                AnimatedVisibility(
                    visible = focusExpanded,
                    enter = expandVertically(animationSpec = tween(220)),
                    exit = shrinkVertically(animationSpec = tween(180))
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp, bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(appString(language, "focus_duration"), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        ChoiceRow(listOf(25, 30, 45), timer.focusDurationMinutes, "m", onSelectFocus)
                        Text(appString(language, "short_break"), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        ChoiceRow(listOf(5, 10), timer.shortBreakMinutes, "m", onSelectShortBreak)
                        Text(appString(language, "long_break"), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        ChoiceRow(listOf(15, 20), timer.longBreakMinutes, "m", onSelectLongBreak)
                        if (timer.mode == FocusTimerMode.Custom) {
                            TextButton(onClick = onUsePomodoro, enabled = !timer.isRunning) { Text(appString(language, "use_pomodoro")) }
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                AccordionHeader("Pengatur Waktu", expanded = false) { showCustomPicker = true }
                Column(
                    modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp, bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = localizedUiLabel("Durasi: ${formatTimer(customSeconds * 1000L, true)}"),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Button(
                        onClick = onStartCustom,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !timer.isRunning && customSeconds > 0,
                        shape = RoundedCornerShape(12.dp)
                    ) { Text(appString(language, "start_timer")) }
                    if (showZeroDurationError) {
                        Text(
                            localizedUiLabel("Pilih durasi lebih dari 0 detik."),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    if (timer.isRunning) {
                        Text(
                            localizedUiLabel("Pause atau stop timer sebelum mengganti mode."),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(localizedUiLabel("Tutup")) } }
    )

    if (showCustomPicker) {
        CustomDurationPickerDialog(
            hours = timer.customHours,
            minutes = timer.customMinutes,
            seconds = timer.customSeconds,
            onDismiss = { showCustomPicker = false },
            onApply = { hours, minutes, seconds ->
                onSetHours(hours)
                onSetMinutes(minutes)
                onSetSeconds(seconds)
                clearZeroDurationError()
                showCustomPicker = false
            }
        )
    }
}

@Composable
private fun AccordionHeader(title: String, expanded: Boolean, onClick: () -> Unit) {
    TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)) {
        Text(
            text = localizedUiLabel(title),
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        Icon(
            imageVector = if (expanded) Icons.Default.ExpandMore else Icons.Default.KeyboardArrowRight,
            contentDescription = "${if (expanded) appString(LocalAppLanguage.current, "close") else appString(LocalAppLanguage.current, "open")} ${localizedUiLabel(title)}"
        )
    }
}

@Composable
private fun ChoiceRow(options: List<Int>, selected: Int, suffix: String, onSelect: (Int) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { value ->
            FilterChip(selected = value == selected, onClick = { onSelect(value) }, label = { Text("$value$suffix") })
        }
    }
}

@Composable
private fun NumberWheelPicker(
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val itemHeight = 40.dp
    val valueCount = range.last - range.first + 1
    val virtualItemCount = 100_000
    val middleIndex = virtualItemCount / 2
    val initialIndex = middleIndex - floorMod(middleIndex, valueCount) +
        (value.coerceIn(range) - range.first)
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialIndex
    )
    val flingBehavior = rememberSnapFlingBehavior(
        lazyListState = listState,
        snapPosition = SnapPosition.Center
    )
    val currentValue by rememberUpdatedState(value)
    val currentOnValueChange by rememberUpdatedState(onValueChange)

    LaunchedEffect(listState, range) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .filter { isScrolling -> !isScrolling }
            .collect {
                val viewport = listState.layoutInfo
                val center = (viewport.viewportStartOffset + viewport.viewportEndOffset) / 2
                val closest = viewport.visibleItemsInfo.minByOrNull { item ->
                    kotlin.math.abs((item.offset + item.size / 2) - center)
                } ?: return@collect
                val selected = range.first + floorMod(closest.index, valueCount)
                if (selected != currentValue) currentOnValueChange(selected)
            }
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = localizedUiLabel(label),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight * 3)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = itemHeight),
                flingBehavior = flingBehavior,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val viewportCenter = (listState.layoutInfo.viewportStartOffset +
                    listState.layoutInfo.viewportEndOffset) / 2
                val centeredIndex = listState.layoutInfo.visibleItemsInfo
                    .minByOrNull { item ->
                        kotlin.math.abs((item.offset + item.size / 2) - viewportCenter)
                    }
                    ?.index
                items(
                    count = virtualItemCount,
                    key = { it }
                ) { index ->
                    val number = range.first + floorMod(index, valueCount)
                    val isCentered = index == centeredIndex
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = number.toString().padStart(2, '0'),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = if (isCentered) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isCentered) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
                            },
                            textAlign = TextAlign.Center
                        )
                    }
                }

            }
            HorizontalDivider(
                modifier = Modifier.align(Alignment.CenterStart),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
            )
            HorizontalDivider(
                modifier = Modifier.align(Alignment.CenterEnd),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
            )
        }
    }
}

private fun floorMod(value: Int, modulus: Int): Int =
    ((value % modulus) + modulus) % modulus

@Composable
private fun CustomDurationPickerDialog(
    hours: Int,
    minutes: Int,
    seconds: Int,
    onDismiss: () -> Unit,
    onApply: (Int, Int, Int) -> Unit
) {
    var draftHours by remember(hours) { mutableStateOf(hours) }
    var draftMinutes by remember(minutes) { mutableStateOf(minutes) }
    var draftSeconds by remember(seconds) { mutableStateOf(seconds) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(appString(LocalAppLanguage.current, "timer")) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NumberWheelPicker(
                        label = "Jam",
                        value = draftHours,
                        range = 0..99,
                        onValueChange = { draftHours = it },
                        modifier = Modifier.weight(1f)
                    )
                    NumberWheelPicker(
                        label = "Menit",
                        value = draftMinutes,
                        range = 0..59,
                        onValueChange = { draftMinutes = it },
                        modifier = Modifier.weight(1f)
                    )
                    NumberWheelPicker(
                        label = "Detik",
                        value = draftSeconds,
                        range = 0..59,
                        onValueChange = { draftSeconds = it },
                        modifier = Modifier.weight(1f)
                    )
                }
                Text(
                    text = localizedUiLabel("Durasi: ${formatTimer(timerTotalSeconds(draftHours, draftMinutes, draftSeconds) * 1000L, true)}"),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onApply(draftHours, draftMinutes, draftSeconds) }) {
                Text(localizedUiLabel("Terapkan"))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(localizedUiLabel("Batal")) }
        }
    )
}

private fun formatTimer(remainingMillis: Long, showHours: Boolean): String {
    val totalSeconds = ((remainingMillis.coerceAtLeast(0L) + 999L) / 1000L).toInt()
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (showHours || hours > 0) "%02d:%02d:%02d".format(hours, minutes, seconds)
    else "%02d:%02d".format(minutes, seconds)
}