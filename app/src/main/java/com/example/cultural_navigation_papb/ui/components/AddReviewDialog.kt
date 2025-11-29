package com.example.cultural_navigation_papb.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.cultural_navigation_papb.data.viewmodels.ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewDialog(
    viewModel: ReviewViewModel,
    userId: String,
    userName: String,
    onDismiss: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    val context = LocalContext.current
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    // Collect StateFlow as State for Compose
    val rating by viewModel.rating.collectAsState()
    val comment by viewModel.comment.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val submitError by viewModel.submitError.collectAsState()

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.size <= 5) { // Max 5 photos
            selectedImages = uris
        } else {
            Toast.makeText(context, "Maksimal 5 foto", Toast.LENGTH_SHORT).show()
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            photoPickerLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Izin akses galeri diperlukan", Toast.LENGTH_SHORT).show()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        title = {
            Text(
                text = "Tulis Review",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Rating
                Column {
                    Text(
                        text = "Rating",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    RatingStars(
                        rating = rating,
                        onRatingChanged = viewModel::updateRating
                    )
                    if (rating == 0f) {
                        Text(
                            text = "Silakan berikan rating",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // Comment
                Column {
                    Text(
                        text = "Komentar",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = comment,
                        onValueChange = viewModel::updateComment,
                        placeholder = { Text("Bagikan pengalaman Anda...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 4,
                        isError = comment.isBlank(),
                        supportingText = if (comment.isBlank()) {
                            { Text("Komentar tidak boleh kosong") }
                        } else null
                    )
                }

                // Photo Upload
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Foto (${selectedImages.size}/5)",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium
                        )
                        TextButton(
                            onClick = {
                                when (PackageManager.PERMISSION_GRANTED) {
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                    ) -> {
                                        photoPickerLauncher.launch("image/*")
                                    }
                                    else -> {
                                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                    }
                                }
                            },
                            enabled = selectedImages.size < 5
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PhotoLibrary,
                                contentDescription = "Add Photo",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tambah Foto")
                        }
                    }

                    AnimatedVisibility(visible = selectedImages.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(selectedImages) { uri ->
                                Box(
                                    modifier = Modifier.size(80.dp)
                                ) {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = "Selected Photo",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(8.dp))
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.outline,
                                                RoundedCornerShape(8.dp)
                                            ),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { selectedImages = selectedImages - uri },
                                        modifier = Modifier
                                            .size(24.dp)
                                            .align(Alignment.TopEnd)
                                            .background(
                                                Color.Black.copy(alpha = 0.6f),
                                                CircleShape
                                            )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = "Remove Photo",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Error Message
                submitError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.submitReview(userId, userName)
                    onSubmitSuccess()
                },
                enabled = rating > 0f &&
                         comment.isNotBlank() &&
                         !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Menyimpan...")
                } else {
                    Text("Kirim Review")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

// Extract RatingStars as a separate Composable function
@Composable
private fun RatingStars(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = "Star $i",
                tint = if (i <= rating) Color(0xFFFFD700) else Color.Gray,
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onRatingChanged(i.toFloat()) }
            )
        }
    }
}
