package com.example.studymate.ui.screens.main

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.IOException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.studymate.ui.components.StudyMateBackHeader
import androidx.navigation.NavController
import com.example.studymate.navigation.Screen
import com.example.studymate.viewmodel.AuthState
import com.example.studymate.viewmodel.AuthViewModel
import com.example.studymate.viewmodel.AppPreferencesViewModel
import com.example.studymate.ui.localization.appString
import com.example.studymate.ui.localization.localizedFormat
import com.example.studymate.ui.localization.localizedUiLabel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private data class UserInfoDialog(val title: String, val rows: List<Pair<String, String>>)

@Composable
fun UserScreen(
    navController: NavController,
    rootNavController: NavController,
    authViewModel: AuthViewModel,
    preferencesViewModel: AppPreferencesViewModel
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    var confirmLogout by remember { mutableStateOf(false) }
    var infoDialog by remember { mutableStateOf<UserInfoDialog?>(null) }

    val displayName = currentUser?.name?.trim()?.takeIf { it.isNotEmpty() } ?: localizedUiLabel("Pelajar")
    val status = localizedUiLabel(if (authState is AuthState.Authenticated) "Aktif" else "Tidak aktif")
    val language = preferencesViewModel.preferencesState.collectAsState().value.languageTag

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(start = 20.dp, top = 4.dp, end = 20.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProfileHeader(
            name = displayName,
            imageUri = currentUser?.profileImageUri
        )
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = localizedUiLabel("PENGATURAN AKUN"),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            item {
                UserMenuRow(Icons.Default.Edit, "Edit Profil", "Ubah nama dan foto profil") {
                    navController.navigate("user_edit_profile")
                }
            }
            item {
                UserMenuRow(Icons.Default.Lock, "Akun & Keamanan", "Informasi akun dan sesi") {
                    navController.navigate("user_account_security")
                }
            }
            item {
                UserMenuRow(
                    Icons.Default.Settings,
                    appString(language, "app_settings"),
                    appString(language, "language_description")
                ) {
                    navController.navigate("user_app_settings")
                }
            }
            item {
                UserMenuRow(Icons.Default.HelpOutline, "Pusat Bantuan", "Pertanyaan umum StudyMate") {
                    navController.navigate("user_help") {
                        launchSingleTop = true
                    }
                }
            }
            item {
                UserMenuRow(Icons.Default.Info, "Tentang StudyMate", "Versi dan informasi aplikasi") {
                    navController.navigate("user_about") {
                        launchSingleTop = true
                    }
                }
            }
            item {
                OutlinedButton(
                    onClick = { confirmLogout = true },
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = localizedUiLabel("Keluar dari akun"), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(localizedUiLabel("Keluar dari akun"), color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    infoDialog?.let { dialog ->
        AlertDialog(
            onDismissRequest = { infoDialog = null },
            title = { Text(localizedUiLabel(dialog.title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    dialog.rows.forEach { (label, value) ->
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text(localizedUiLabel(label), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { infoDialog = null }) { Text(localizedUiLabel("Tutup")) } }
        )
    }

    if (confirmLogout) {
        AlertDialog(
            onDismissRequest = { confirmLogout = false },
            title = { Text(localizedUiLabel("Keluar dari akun?")) },
            text = { Text(localizedUiLabel("Apakah kamu yakin ingin keluar dari akun StudyMate?")) },
            confirmButton = {
                TextButton(onClick = {
                    confirmLogout = false
                    authViewModel.logout()
                    rootNavController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }) { Text(localizedUiLabel("Keluar"), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { confirmLogout = false }) { Text(localizedUiLabel("Batal")) } }
        )
    }
}

@Composable
private fun ProfileHeader(name: String, imageUri: String?) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProfileAvatar(name = name, imageUri = imageUri, size = 76.dp)
        Text(name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text(localizedUiLabel("Pelajar"), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun EditProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val user by authViewModel.currentUser.collectAsState()
    val currentUser = user ?: return
    var name by remember(currentUser.id, currentUser.name) { mutableStateOf(currentUser.name) }
    var fullName by remember(currentUser.id, currentUser.fullName) { mutableStateOf(currentUser.fullName) }
    var bio by remember(currentUser.id, currentUser.bio) { mutableStateOf(currentUser.bio) }
    var educationLevel by remember(currentUser.id, currentUser.educationLevel) { mutableStateOf(currentUser.educationLevel) }
    var school by remember(currentUser.id, currentUser.school) { mutableStateOf(currentUser.school) }
    var gradeOrSemester by remember(currentUser.id, currentUser.gradeOrSemester) { mutableStateOf(currentUser.gradeOrSemester) }
    var selectedImageUri by remember(currentUser.id, currentUser.profileImageUri) {
        mutableStateOf(currentUser.profileImageUri)
    }
    var nameError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // Photo Picker providers may grant temporary access only.
            }
            selectedImageUri = uri.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        StudyMateBackHeader(
            title = "Edit Profil",
            onBack = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProfileAvatar(
                        name = name.trim().ifBlank { "Pelajar" },
                        imageUri = selectedImageUri,
                        size = 112.dp
                    )
                    Text(
                        localizedUiLabel("Gunakan foto yang mudah dikenali"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(
                        onClick = {
                            picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(localizedUiLabel("Ganti Foto Profil"))
                    }
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(localizedUiLabel("Identitas Profil"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            if (it.length <= 80) {
                                name = it
                                nameError = false
                            }
                        },
                        label = { Text(localizedUiLabel("Nama tampilan")) },
                        placeholder = { Text(localizedUiLabel("Masukkan nama yang ingin ditampilkan")) },
                        singleLine = true,
                        isError = nameError,
                        supportingText = if (nameError) {
                            { Text(localizedUiLabel("Nama tampilan wajib diisi, maksimal 80 karakter.")) }
                        } else null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { if (it.length <= 120) fullName = it },
                        label = { Text(localizedUiLabel("Nama lengkap (opsional)")) },
                        placeholder = { Text(localizedUiLabel("Nama lengkap kamu")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(localizedUiLabel("Tentang Saya"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { if (it.length <= 240) bio = it },
                        label = { Text(localizedUiLabel("Bio (opsional)")) },
                        placeholder = { Text(localizedUiLabel("Ceritakan sedikit tentang dirimu")) },
                        minLines = 3,
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = educationLevel,
                        onValueChange = { if (it.length <= 60) educationLevel = it },
                        label = { Text(localizedUiLabel("Jenjang pendidikan (opsional)")) },
                        placeholder = { Text(localizedUiLabel("Contoh: SMA atau Perguruan Tinggi")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = school,
                        onValueChange = { if (it.length <= 120) school = it },
                        label = { Text(localizedUiLabel("Sekolah atau universitas (opsional)")) },
                        placeholder = { Text(localizedUiLabel("Nama institusi pendidikan")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = gradeOrSemester,
                        onValueChange = { if (it.length <= 60) gradeOrSemester = it },
                        label = { Text(localizedUiLabel("Kelas atau semester (opsional)")) },
                        placeholder = { Text(localizedUiLabel("Contoh: Kelas 12 atau Semester 2")) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Button(
                onClick = {
                    nameError = name.trim().isBlank() || name.trim().length > 80
                    if (!nameError && authViewModel.updateProfile(
                            name,
                            fullName,
                            bio,
                            educationLevel,
                            school,
                            gradeOrSemester,
                            selectedImageUri
                        )
                    ) {
                        navController.popBackStack()
                    } else if (!nameError) {
                        nameError = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(localizedUiLabel("Simpan Perubahan"))
            }
        }
    }
}

@Composable
private fun ProfileAvatar(name: String, imageUri: String?, size: androidx.compose.ui.unit.Dp) {
    val bitmap by rememberProfileBitmap(imageUri)
    Box(
        modifier = Modifier.size(size).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!,
                contentDescription = localizedFormat(
                    com.example.studymate.ui.localization.LocalAppLanguage.current,
                    "profile_photo_named",
                    name
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize())
        } else {
            Text(name.firstOrNull()?.uppercaseChar()?.toString() ?: "S",
                style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
private fun rememberProfileBitmap(imageUri: String?): androidx.compose.runtime.State<androidx.compose.ui.graphics.ImageBitmap?> {
    val context = LocalContext.current
    return produceState(
        initialValue = null,
        key1 = imageUri
    ) {
        value = if (imageUri.isNullOrBlank()) null else withContext(Dispatchers.IO) {
            decodeProfileBitmap(context, imageUri)
        }
    }
}

private fun decodeProfileBitmap(context: Context, imageUri: String): androidx.compose.ui.graphics.ImageBitmap? {
    val bitmap = try {
        context.contentResolver.openInputStream(Uri.parse(imageUri))?.use(BitmapFactory::decodeStream)
    } catch (_: IOException) {
        null
    } catch (_: SecurityException) {
        null
    } catch (_: IllegalArgumentException) {
        null
    }
    return bitmap?.asImageBitmap()
}

@Composable
private fun UserMenuRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = localizedUiLabel(title), tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(localizedUiLabel(title), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                Text(localizedUiLabel(subtitle), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = localizedFormat(
                    com.example.studymate.ui.localization.LocalAppLanguage.current,
                    "open_item",
                    localizedUiLabel(title)
                ),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
internal fun AccountSecurityScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    learningGoalsViewModel: LearningGoalsViewModel
) {
    val user by authViewModel.currentUser.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    val email = user?.email?.takeIf { it.isNotBlank() } ?: "Belum ditambahkan"
    val joinDate = user?.joinDate?.takeIf { it.isNotBlank() } ?: "Belum tersedia"
    val status = if (authState is AuthState.Authenticated) localizedUiLabel("Aktif") else localizedUiLabel("Tidak aktif")
    val awards by learningGoalsViewModel.awards.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        StudyMateBackHeader(
            title = "Akun & Keamanan",
            onBack = { navController.popBackStack() }
        )
        LazyColumn(
            modifier = Modifier.padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        AccountInfoRow(Icons.Default.MailOutline, localizedUiLabel("Email"), email)
                        AccountInfoRow(Icons.Default.VerifiedUser, localizedUiLabel("Status akun"), status)
                        AccountInfoRow(Icons.Default.CalendarToday, localizedUiLabel("Tanggal bergabung"), joinDate)
                    }
                }
            }
            item {
                Text(
                    localizedUiLabel("PENGHARGAAN BELAJAR"),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            if (awards.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            localizedUiLabel("Lencana akan muncul di sini setelah kamu menyelesaikan target belajar."),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(awards.sortedByDescending { it.earnedAtMillis }, key = { it.id }) { award ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.EmojiEvents,
                                contentDescription = localizedUiLabel("Penghargaan diperoleh"),
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                                Text(award.goalName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text(award.goalType.label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    "${localizedUiLabel("Penghargaan diperoleh")} ${java.text.SimpleDateFormat("d MMM yyyy", java.util.Locale.forLanguageTag(com.example.studymate.ui.localization.LocalAppLanguage.current)).format(java.util.Date(award.earnedAtMillis))}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
            item {
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(localizedUiLabel("Keamanan akun"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(localizedUiLabel("Metode login: Email dan kata sandi (mode demo lokal)."),
                            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(localizedUiLabel("Perubahan keamanan server belum tersedia sebelum integrasi backend."),
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(localizedUiLabel(label), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, maxLines = 2)
    }
}
