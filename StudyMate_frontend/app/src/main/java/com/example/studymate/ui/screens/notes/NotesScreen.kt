package com.example.studymate.ui.screens.notes

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import com.example.studymate.ui.components.SearchBar
import com.example.studymate.ui.components.buildHighlightedText
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotesScreen(navController: NavController, viewModel: NotesViewModel = viewModel()) {
    val notes by viewModel.filteredNotes.collectAsState()
    val query by viewModel.query.collectAsState()

    Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchBar(query = query, onQueryChange = { viewModel.setQuery(it) }, placeholder = "Cari catatan...")

            val pinned = notes.filter { it.isPinned }
            val others = notes.filter { !it.isPinned }

            if (pinned.isNotEmpty()) {
                Text("PINNED", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(vertical = 8.dp))
                NotesGrid(items = pinned, onClick = { note -> navController.navigate("edit_note/${note.id}") }, onLongAction = { note, onAction ->
                    onAction()
                }, onPinToggle = { id -> viewModel.togglePin(id) }, onDelete = { id -> viewModel.deleteNote(id) }, query = query)
            }

            Text("OTHERS", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(vertical = 8.dp))
            NotesGrid(items = others, onClick = { note -> navController.navigate("edit_note/${note.id}") }, onLongAction = { note, onAction -> onAction() }, onPinToggle = { id -> viewModel.togglePin(id) }, onDelete = { id -> viewModel.deleteNote(id) }, query = query)
        }

        FloatingActionButton(
            onClick = { navController.navigate("add_note") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 24.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NotesGrid(
    items: List<Note>,
    onClick: (Note) -> Unit,
    onLongAction: (Note, () -> Unit) -> Unit,
    onPinToggle: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    query: String = ""
) {
    LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        items(items) { note ->
            var expanded by remember { mutableStateOf(false) }
            Box(modifier = Modifier.padding(4.dp)) {
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(onClick = { onClick(note) }, onLongClick = { expanded = true })) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(text = buildHighlightedText(note.title, query), style = MaterialTheme.typography.titleMedium)
                            if (note.isPinned) {
                                Icon(imageVector = Icons.Default.Star, contentDescription = "Pinned", modifier = Modifier.size(18.dp))
                            }
                        }
                        Text(text = buildHighlightedText(note.content, query), maxLines = 3, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
                    }
                }

                androidx.compose.material3.DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    androidx.compose.material3.DropdownMenuItem(text = { Text("Edit") }, onClick = {
                        expanded = false
                        onClick(note)
                    }, leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) })
                    androidx.compose.material3.DropdownMenuItem(text = { Text(if (note.isPinned) "Unpin" else "Pin") }, onClick = {
                        expanded = false
                        onPinToggle(note.id)
                    }, leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) })
                    androidx.compose.material3.DropdownMenuItem(text = { Text("Delete") }, onClick = {
                        expanded = false
                        onDelete(note.id)
                    }, leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(navController: NavController, noteId: Int? = null, viewModel: NotesViewModel = viewModel()) {
    val existing = noteId?.let { viewModel.getNoteById(it) }
    var title by remember { mutableStateOf(existing?.title ?: "") }
    var content by remember { mutableStateOf(existing?.content ?: "") }
    var pinned by remember { mutableStateOf(existing?.isPinned ?: false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = if (existing == null) "Add Note" else "Edit Note", style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { pinned = !pinned }) {
                Icon(imageVector = Icons.Default.Star, contentDescription = "Pin")
            }
        }

        TextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, modifier = Modifier.fillMaxWidth())
        TextField(value = content, onValueChange = { content = it }, label = { Text("Content") }, modifier = Modifier.fillMaxWidth(), maxLines = 8)

        Button(onClick = {
            if (existing == null) {
                viewModel.addNote(title.trim(), content.trim(), pinned)
            } else {
                viewModel.updateNote(existing.id, title.trim(), content.trim(), pinned)
            }
            navController.popBackStack()
        }, modifier = Modifier.padding(top = 12.dp)) {
            Text("Save")
        }
    }
}
