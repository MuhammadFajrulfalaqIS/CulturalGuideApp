package com.example.cultural_navigation_papb.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.cultural_navigation_papb.data.models.Place
import com.example.cultural_navigation_papb.data.viewmodels.AuthViewModel
import com.example.cultural_navigation_papb.data.viewmodels.ReviewViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import kotlin.math.round

// Earth tone colors matching the theme
val EarthBrown = Color(0xFF3E2723)
val LightBrown = Color(0xFF6D4C41)
val OrangeAccent = Color(0xFFFF6F00)
val WarmWhite = Color(0xFFFFFEF7)
val DarkGray = Color(0xFF424242)
val LightGray = Color(0xFFE0E0E0)

@Composable
fun ImprovedReviewDialog(
    place: Place,
    onDismiss: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    reviewViewModel: ReviewViewModel = hiltViewModel()
) {
    // State untuk form
    val rating by reviewViewModel.rating.collectAsState()
    val reviewText by reviewViewModel.comment.collectAsState()
    val selectedPhotos by reviewViewModel.photos.collectAsState()

    // Get current user
    val currentUser by authViewModel.currentUser.collectAsState()
    val isSubmitting by reviewViewModel.isSubmitting.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { uri ->
            val fileName = "review_${System.currentTimeMillis()}.jpg"
            val file = File(context.cacheDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            reviewViewModel.addPhoto(file.absolutePath)
        }
    }

    // Load reviews dan place untuk dialog ini
    LaunchedEffect(place.id) {
        reviewViewModel.loadReviewsForPlace(place.id)
        reviewViewModel.loadPlaceForReview(place.id)
    }

    // Get submit error state
    val submitError by reviewViewModel.submitError.collectAsState()

    // Handle form submission
    fun submitReview() {
        if (rating == 0f) {
            android.util.Log.e("ImprovedReviewDialog", "❌ Rating is 0")
            return
        }

        if (reviewText.isBlank()) {
            android.util.Log.e("ImprovedReviewDialog", "❌ Review text is blank")
            return
        }

        currentUser?.let { user ->
            android.util.Log.d("ImprovedReviewDialog", "✅ Submitting review for place: ${place.id}, user: ${user.name}")

            reviewViewModel.submitReview(
                userId = user.id,
                userName = user.name
            )

            // Don't dismiss immediately - wait for submission to complete
            coroutineScope.launch {
                delay(500) // Wait for submission
                if (submitError == null) {
                    onDismiss()
                }
            }
        } ?: run {
            android.util.Log.e("ImprovedReviewDialog", "❌ Current user is null")
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(horizontal = 8.dp)
                .heightIn(max = 700.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                // ===== HEADER =====
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "✍️ Buat Review",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = EarthBrown
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = place.name,
                            fontSize = 14.sp,
                            color = LightBrown,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(40.dp)) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = DarkGray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ===== USER INFO SECTION =====
                if (currentUser != null) {
                    currentUser?.let { user ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFFF5F5F5),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                        ) {
                            if (user.profileImagePath != null) {
                                AsyncImage(
                                    model = "file://${user.profileImagePath}",
                                    contentDescription = "User profile",
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(22.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(LightBrown, RoundedCornerShape(22.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = "User",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = user.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EarthBrown
                                )

                                if (user.email?.endsWith(".ac.id") == true) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Verified,
                                            contentDescription = "Verified",
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Terverifikasi",
                                            fontSize = 11.sp,
                                            color = Color(0xFF4CAF50),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = OrangeAccent.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Login,
                                contentDescription = "Login required",
                                tint = OrangeAccent,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Login dulu untuk membuat review",
                                color = OrangeAccent,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ===== IMPROVED RATING SECTION =====
                Text(
                    text = "⭐ Berikan Rating",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EarthBrown
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Rating Display Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF8E1)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Star Display - Improved partial fill
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            (1..5).forEach { star ->
                                val starFillPercentage = when {
                                    rating >= star -> 1f
                                    rating > star - 1 -> rating - (star - 1)
                                    else -> 0f
                                }

                                Box(
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                ) {
                                    // Background outline star
                                    Icon(
                                        imageVector = Icons.Outlined.StarBorder,
                                        contentDescription = "Star outline $star",
                                        tint = LightGray,
                                        modifier = Modifier.size(36.dp)
                                    )

                                    // Filled star with partial clip
                                    if (starFillPercentage > 0f) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = "Star filled $star",
                                            tint = OrangeAccent,
                                            modifier = Modifier
                                                .size(36.dp)
                                                .drawWithContent {
                                                    clipPath(
                                                        Path().apply {
                                                            addRect(
                                                                Rect(
                                                                    left = 0f,
                                                                    top = 0f,
                                                                    right = size.width * starFillPercentage,
                                                                    bottom = size.height
                                                                )
                                                            )
                                                        }
                                                    ) {
                                                        this@drawWithContent.drawContent()
                                                    }
                                                }
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Rating Number Display
                        Text(
                            text = if (rating > 0) String.format(Locale.US, "%.1f", rating) else "0.0",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangeAccent
                        )

                        Text(
                            text = getRatingText(rating),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = LightBrown
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Slider
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Slider(
                                value = rating,
                                onValueChange = { newValue ->
                                    // Round to nearest 0.1
                                    val rounded = (round(newValue * 10) / 10f).coerceIn(0f, 5f)
                                    reviewViewModel.updateRating(rounded)
                                },
                                valueRange = 0f..5f,
                                steps = 49, // 5.0 / 0.1 - 1 = 49 steps
                                colors = SliderDefaults.colors(
                                    thumbColor = OrangeAccent,
                                    activeTrackColor = OrangeAccent,
                                    inactiveTrackColor = LightGray.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Quick rating buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                listOf(1f, 2f, 3f, 4f, 5f).forEach { quickRating ->
                                    OutlinedButton(
                                        onClick = { reviewViewModel.updateRating(quickRating) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 2.dp)
                                            .height(36.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            containerColor = if (rating == quickRating)
                                                OrangeAccent.copy(alpha = 0.1f)
                                            else Color.Transparent,
                                            contentColor = if (rating == quickRating)
                                                OrangeAccent
                                            else LightBrown
                                        ),
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = if (rating == quickRating) 2.dp else 1.dp,
                                            color = if (rating == quickRating)
                                                OrangeAccent
                                            else LightGray
                                        ),
                                        contentPadding = PaddingValues(4.dp)
                                    ) {
                                        Text(
                                            text = quickRating.toInt().toString(),
                                            fontSize = 13.sp,
                                            fontWeight = if (rating == quickRating)
                                                FontWeight.Bold
                                            else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ===== REVIEW TEXT SECTION =====
                Text(
                    text = "💬 Tulis Review Anda",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EarthBrown
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { if (it.length <= 500) reviewViewModel.updateComment(it) },
                    placeholder = {
                        Text(
                            "Bagikan pengalaman Anda di ${place.name}...",
                            color = LightGray,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LightBrown,
                        unfocusedBorderColor = LightGray,
                        errorBorderColor = Color(0xFFF44336),
                        focusedContainerColor = WarmWhite,
                        unfocusedContainerColor = Color(0xFFFAFAFA)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 4,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${reviewText.length}/500 karakter",
                    fontSize = 12.sp,
                    color = if (reviewText.length > 500) Color(0xFFF44336) else DarkGray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.End
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ===== PHOTO SECTION =====
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📸 Tambah Foto (Opsional)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EarthBrown,
                        modifier = Modifier.weight(1f)
                    )

                    if (selectedPhotos.size < 5) {
                        OutlinedButton(
                            onClick = { photoPickerLauncher.launch("image/*") },
                            modifier = Modifier.height(32.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, LightBrown),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = LightBrown
                            )
                        ) {
                            Icon(
                                Icons.Default.AddAPhoto,
                                contentDescription = "Add photo",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Tambah",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedPhotos.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(selectedPhotos) { photoPath ->
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(LightGray)
                            ) {
                                AsyncImage(
                                    model = "file://$photoPath",
                                    contentDescription = "Review photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Delete button
                                IconButton(
                                    onClick = { reviewViewModel.removePhoto(photoPath) },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .background(
                                            color = Color.Black.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove photo",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${selectedPhotos.size}/5 foto",
                        fontSize = 12.sp,
                        color = DarkGray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                } else {
                    Text(
                        text = "Tambahkan hingga 5 foto untuk review yang lebih lengkap",
                        fontSize = 12.sp,
                        color = LightGray,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // ===== ERROR MESSAGE =====
                submitError?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFCE4EC)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = "Error",
                                tint = Color(0xFFF44336),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = error,
                                color = Color(0xFFF44336),
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // ===== ACTION BUTTONS =====
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, LightBrown),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = LightBrown
                        )
                    ) {
                        Text(
                            "Batal",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = { submitReview() },
                        enabled = currentUser != null && rating > 0 && reviewText.isNotBlank() && !isSubmitting,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EarthBrown,
                            contentColor = Color.White,
                            disabledContainerColor = LightBrown.copy(alpha = 0.5f),
                            disabledContentColor = Color.White.copy(alpha = 0.7f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Mengirim...",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Submit",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Kirim Review",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Dengan mengirim, Anda setuju dengan panduan review kami. Konten ofensif akan dihapus.",
                    fontSize = 11.sp,
                    color = LightGray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}

/**
 * Helper function to get rating description text
 */
private fun getRatingText(rating: Float): String {
    return when {
        rating == 0f -> "Pilih rating Anda"
        rating < 1.5f -> "Sangat Buruk"
        rating < 2.5f -> "Buruk"
        rating < 3.5f -> "Cukup"
        rating < 4.5f -> "Baik"
        else -> "Sangat Baik"
    }
}
