package com.example.studymate.ui.screens.notes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.studymate.ui.components.StudyMateBackHeader
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.studymate.ui.components.SearchBar
import com.example.studymate.ui.components.buildHighlightedText
import com.example.studymate.ui.components.clearFocusWhenKeyboardHidden
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.studymate.ui.localization.localizedUiLabel
import com.example.studymate.ui.localization.localizedCount

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesScreen(navController: NavController, viewModel: NotesViewModel = viewModel()) {
    val notes by viewModel.filteredNotes.collectAsState()
    val query by viewModel.query.collectAsState()
    var selectedNoteIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var deleteTargetIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    val focusManager = LocalFocusManager.current
    val selectedNotes = viewModel.fullNotes.collectAsState().value.filter { it.id in selectedNoteIds }
    val isSelectionMode = selectedNoteIds.isNotEmpty()
    val pinned = notes.filter { it.isPinned }
    val others = notes.filterNot { it.isPinned }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(top = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (!isSelectionMode) {
                SearchBar(
                    query = query,
                    onQueryChange = viewModel::setQuery,
                    placeholder = "Telusuri catatan...",
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                    compact = true
                )
            } else {
                NoteActionToolbar(
                    selectedNotes = selectedNotes,
                    onPin = {
                        val shouldPin = selectedNotes.any { !it.isPinned }
                        viewModel.setPinned(selectedNoteIds, shouldPin)
                        selectedNoteIds = emptySet()
                    },
                    onDelete = { deleteTargetIds = selectedNoteIds },
                    onClose = { selectedNoteIds = emptySet() }
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 148.dp),
                state = rememberLazyGridState(),
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(isSelectionMode) {
                        detectTapGestures(onTap = {
                            focusManager.clearFocus()
                            if (isSelectionMode) selectedNoteIds = emptySet()
                        })
                    },
                contentPadding = PaddingValues(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 104.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (notes.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        NotesEmptyState(isSearching = query.isNotBlank())
                    }
                } else {
                    if (pinned.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            NotesSectionHeading("Disematkan")
                        }
                        items(pinned, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                query = query,
                                onOpen = {
                                    if (isSelectionMode) {
                                        selectedNoteIds = selectedNoteIds.toggle(note.id)
                                    } else {
                                        navController.navigate("edit_note/${note.id}")
                                    }
                                },
                                onLongPress = { selectedNoteIds = selectedNoteIds + note.id },
                                selected = note.id in selectedNoteIds
                            )
                        }
                    }

                    if (others.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            NotesSectionHeading(if (pinned.isEmpty()) "Catatan" else "Catatan lainnya")
                        }
                        items(others, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                query = query,
                                onOpen = {
                                    if (isSelectionMode) {
                                        selectedNoteIds = selectedNoteIds.toggle(note.id)
                                    } else {
                                        navController.navigate("edit_note/${note.id}")
                                    }
                                },
                                onLongPress = { selectedNoteIds = selectedNoteIds + note.id },
                                selected = note.id in selectedNoteIds
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("add_note") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Add, contentDescription = localizedUiLabel("Tambah catatan"))
        }
    }

    if (deleteTargetIds.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { deleteTargetIds = emptySet() },
            title = { Text(localizedUiLabel("Hapus catatan?")) },
            text = { Text(localizedCount("delete_notes_count", deleteTargetIds.size)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteNotes(deleteTargetIds)
                    deleteTargetIds = emptySet()
                    selectedNoteIds = emptySet()
                }) { Text(localizedUiLabel("Hapus")) }
            },
            dismissButton = { TextButton(onClick = { deleteTargetIds = emptySet() }) { Text(localizedUiLabel("Batal")) } }
        )
    }
}

private fun Set<Int>.toggle(id: Int): Set<Int> =
    if (id in this) this - id else this + id

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NoteCard(
    note: Note,
    query: String,
    onOpen: () -> Unit,
    onLongPress: () -> Unit,
    selected: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onOpen, onLongClick = onLongPress),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (selected) androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary
        ) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(start = 14.dp, top = 12.dp, end = 8.dp, bottom = 14.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                val displayTitle = note.title.ifBlank {
                    note.content.ifBlank { localizedUiLabel("Catatan tanpa judul") }
                }
                Text(
                    text = buildHighlightedText(displayTitle, query),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (note.title.isNotBlank() && note.content.isNotBlank()) {
                Text(
                    text = buildHighlightedText(note.content, query),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            if (note.isPinned) {
                PinIndicator(
                    crossed = false,
                    contentDescription = "Disematkan",
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp).size(24.dp)
                )
            }
        }

    }
}

@Composable
private fun NoteActionToolbar(
    selectedNotes: List<Note>,
    onPin: () -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = localizedCount("selected_notes", selectedNotes.size),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onPin) {
            PinIndicator(
                crossed = selectedNotes.isNotEmpty() && selectedNotes.all { it.isPinned },
                contentDescription = if (selectedNotes.all { it.isPinned }) {
                    "Lepas sematan"
                } else {
                    "Sematkan"
                },
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = localizedUiLabel("Hapus catatan"))
        }
        IconButton(onClick = onClose) {
            Text("X", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
    }

}

@Composable
private fun PinIndicator(
    crossed: Boolean,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val pinColor = MaterialTheme.colorScheme.onSurface
        Icon(
            imageVector = Icons.Default.PushPin,
            contentDescription = localizedUiLabel(contentDescription),
            tint = pinColor,
            modifier = Modifier.size(18.dp)
        )
        if (crossed) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawLine(
                    color = pinColor,
                    start = androidx.compose.ui.geometry.Offset(4f, size.height - 4f),
                    end = androidx.compose.ui.geometry.Offset(size.width - 4f, 4f),
                    strokeWidth = 2.5f
                )
            }
        }
    }
}

@Composable
private fun NotesSectionHeading(title: String) {
    Text(
        text = localizedUiLabel(title),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
    )
}

@Composable
private fun NotesEmptyState(isSearching: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 56.dp, start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Notes,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = localizedUiLabel(if (isSearching) "Tidak ada catatan yang cocok" else "Belum ada catatan"),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Text(
            text = localizedUiLabel(if (isSearching) "Coba gunakan kata kunci lain." else "Simpan ide, materi, tugas, atau catatan belajar kamu di sini."),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
fun AddEditNoteScreen(navController: NavController, noteId: Int? = null, viewModel: NotesViewModel = viewModel()) {
    val allNotes by viewModel.fullNotes.collectAsState()
    val existing = allNotes.find { it.id == noteId }
    val scope = rememberCoroutineScope()
    var draftId by remember(noteId) { mutableStateOf(noteId) }
    var title by remember(noteId) { mutableStateOf(existing?.title.orEmpty()) }
    var contentState by remember(noteId) {
        mutableStateOf(TextFieldValue(existing?.content.orEmpty()))
    }
    var pinned by remember(noteId) { mutableStateOf(existing?.isPinned ?: false) }
    val content = contentState.text
    val canSave = title.isNotBlank() || content.isNotBlank()

    LaunchedEffect(title, content, pinned) {
        if (canSave) {
            delay(500)
            draftId = viewModel.saveDraft(draftId, title, content, pinned)
        }
    }

    fun goBack() {
        scope.launch {
            if (canSave) {
                draftId = viewModel.saveDraft(draftId, title, content, pinned)
            }
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        StudyMateBackHeader(
            onBack = ::goBack,
            actions = {
                IconButton(onClick = { pinned = !pinned }) {
                    Icon(
                        Icons.Default.PushPin,
                        contentDescription = if (pinned) "Batalkan sematan" else "Sematkan",
                        tint = if (pinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(18.dp))
        BasicTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().clearFocusWhenKeyboardHidden(),
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Next
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (title.isEmpty()) {
                        Text(
                            localizedUiLabel("Judul catatan"),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                        )
                    }
                    innerTextField()
                }
            }
        )
        Spacer(modifier = Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, top = 14.dp, end = 20.dp)
        ) {
            BasicTextField(
                value = contentState,
                onValueChange = { contentState = processNoteContentChange(contentState, it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 240.dp)
                    .clearFocusWhenKeyboardHidden(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 24.sp
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Default
                ),
                decorationBox = { innerTextField ->
                    Box {
                        if (content.isEmpty()) {
                            Text(
                                localizedUiLabel("Tulis catatan..."),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }

                        innerTextField()
                    }
                }
            )
        }

    }
}

private fun processNoteContentChange(
    previous: TextFieldValue,
    next: TextFieldValue
): TextFieldValue {
    val change = textChange(previous.text, next.text)
    if (change == null || !next.selection.collapsed) return next

    removeEmptyNumberedItemAfterBackspace(previous, next, change)?.let { return it }

    if (change.inserted == "\n") {
        return continueListOnNewLine(next, change.start)
    }

    if (change.inserted.length == 1 && change.inserted[0].isLowerCase()) {
        val beforeCursor = next.text.substring(0, change.start)
        if (beforeCursor.endsWith(". ") || beforeCursor.endsWith(".\n")) {
            val capitalized = change.inserted[0].uppercaseChar().toString()
            val updatedText = next.text.replaceRange(change.start, change.end, capitalized)
            return next.copy(
                text = updatedText,
                selection = TextRange(change.start + capitalized.length)
            )
        }
    }

    return next
}

private fun removeEmptyNumberedItemAfterBackspace(
    previous: TextFieldValue,
    next: TextFieldValue,
    change: TextChange
): TextFieldValue? {
    if (change.inserted.isNotEmpty() || change.end != change.start) return null

    val cursor = next.selection.start
    val lineStart = next.text.lastIndexOf('\n', (cursor - 1).coerceAtLeast(0)) + 1
    val lineEnd = next.text.indexOf('\n', cursor).let { if (it == -1) next.text.length else it }
    val currentLine = next.text.substring(lineStart, lineEnd)
    val previousLineStart = previous.text.lastIndexOf('\n', (change.start - 1).coerceAtLeast(0)) + 1
    val previousLineEnd = previous.text.indexOf('\n', change.start).let {
        if (it == -1) previous.text.length else it
    }
    val previousLine = previous.text.substring(previousLineStart, previousLineEnd)

    if (change.removed != " " ||
        cursor != lineEnd ||
        !Regex("""^\d+\.$""").matches(currentLine) ||
        previousLineStart != lineStart ||
        !Regex("""^\d+\. $""").matches(previousLine)
    ) {
        return null
    }

    // Keep the newline so the user remains on this line as plain text.
    val removeStart = lineStart
    val removeEnd = lineEnd
    return next.copy(
        text = next.text.removeRange(removeStart, removeEnd),
        selection = TextRange(removeStart)
    )
}

private fun continueListOnNewLine(value: TextFieldValue, insertionStart: Int): TextFieldValue {
    if (insertionStart + 1 < value.text.length && value.text[insertionStart + 1] != '\n') {
        return value
    }

    val lineStart = value.text.lastIndexOf('\n', insertionStart - 1) + 1
    val lineBeforeBreak = value.text.substring(lineStart, insertionStart)
    val numbered = Regex("""^(\d+)\. (.*)$""").matchEntire(lineBeforeBreak)
    val bullet = lineBeforeBreak.startsWith("- ")

    if (numbered != null) {
        val itemText = numbered.groupValues[2]
        if (itemText.isBlank()) {
            val withoutEmptyPrefix = value.text.removeRange(lineStart, insertionStart + 1)
            return value.copy(
                text = withoutEmptyPrefix,
                selection = TextRange(lineStart)
            )
        }

        val nextNumber = numbered.groupValues[1].toIntOrNull()?.plus(1)
        if (nextNumber != null) {
            val continuation = "$nextNumber. "
            val updatedText = value.text.substring(0, insertionStart + 1) +
                continuation +
                value.text.substring(insertionStart + 1)
            val cursor = insertionStart + 1 + continuation.length
            return value.copy(text = updatedText, selection = TextRange(cursor))
        }
    }

    if (bullet) {
        if (lineBeforeBreak.removePrefix("- ").isBlank()) {
            val withoutEmptyPrefix = value.text.removeRange(lineStart, insertionStart + 1)
            return value.copy(
                text = withoutEmptyPrefix,
                selection = TextRange(lineStart)
            )
        }

        val continuation = "- "
        val updatedText = value.text.substring(0, insertionStart + 1) +
            continuation +
            value.text.substring(insertionStart + 1)
        val cursor = insertionStart + 1 + continuation.length
        return value.copy(text = updatedText, selection = TextRange(cursor))
    }

    return value
}

private data class TextChange(
    val start: Int,
    val end: Int,
    val inserted: String,
    val removed: String
)

private fun textChange(previous: String, next: String): TextChange? {
    var prefix = 0
    while (prefix < previous.length && prefix < next.length && previous[prefix] == next[prefix]) {
        prefix++
    }

    var previousEnd = previous.length
    var nextEnd = next.length
    while (previousEnd > prefix && nextEnd > prefix &&
        previous[previousEnd - 1] == next[nextEnd - 1]
    ) {
        previousEnd--
        nextEnd--
    }

    if (prefix == previous.length && prefix == next.length) return null
    return TextChange(
        start = prefix,
        end = nextEnd,
        inserted = next.substring(prefix, nextEnd),
        removed = previous.substring(prefix, previousEnd)
    )
}
