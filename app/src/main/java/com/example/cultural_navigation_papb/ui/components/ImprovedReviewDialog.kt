package com.example.cultural_navigation_papb.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Verified
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.clip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.cultural_navigation_papb.data.models.Place
import com.example.cultural_navigation_papb.data.viewmodels.AuthViewModel
import com.example.cultural_navigation_papb.data.viewmodels.ReviewViewModel
import com.example.cultural_navigation_papb.data.models.Review
import com.example.cultural_navigation_papb.data.models.User

// Earth tone colors matching the theme
val EarthBrown = Color(0xFF3E2723)
val LightBrown = Color(0xFF6D4C41)
val OrangeAccent = Color(0xFFFF6F00)
val WarmWhite = Color(0xFFFFFEF7)

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

    // Get current user
    val currentUser by authViewModel.currentUser.collectAsState()
    val isSubmitting by reviewViewModel.isSubmitting.collectAsState()

    // Load reviews untuk place ini
    LaunchedEffect(place.id) {
        reviewViewModel.loadReviewsForPlace(place.id)
    }

    // Get submit error state
    val submitError by reviewViewModel.submitError.collectAsState()

    // Handle form submission
    fun submitReview() {
        if (rating == 0f) {
            return
        }

        if (reviewText.isBlank()) {
            return
        }

        currentUser?.let { user ->
            reviewViewModel.submitReview(
                userId = user.id,
                userName = user.name
            )

            onDismiss()
            // Reset form
            reviewViewModel.updateRating(0f)
            reviewViewModel.updateComment("")
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
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .heightIn(max = 600.dp)
                .verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header with title and close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Buat Review",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EarthBrown,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = place.name,
                            fontSize = 14.sp,
                            color = LightBrown,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // User info section
                if (currentUser != null) {
                    currentUser?.let { user ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Profile image or placeholder
                            if (user.profileImagePath != null) {
                                AsyncImage(
                                    model = "file://${user.profileImagePath}",
                                    contentDescription = "User profile",
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(24.dp)),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(LightBrown, RoundedCornerShape(24.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = "User",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = user.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = EarthBrown,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )

                                // Verified badge
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
                                            text = "Akun Terverifikasi",
                                            fontSize = 12.sp,
                                            color = Color(0xFF4CAF50),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Login prompt
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = OrangeAccent.copy(alpha = 0.1f)
                        ),
                        border = BorderStroke(1.dp, OrangeAccent.copy(alpha = 0.5f))
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
                                text = "Silakan login untuk membuat review",
                                color = OrangeAccent,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Rating input
                Text(
                    text = "Rating Anda",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = EarthBrown
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Star rating with proper sizing
                    (1..5).forEach { star ->
                        IconButton(
                            onClick = { reviewViewModel.updateRating(star.toFloat()) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = if (star <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Rating star $star",
                                tint = if (star <= rating) OrangeAccent else Color(0xFFE0E0E0),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "${rating.toInt()} / 5",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EarthBrown
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Review text area - FIXED: 2-3 lines instead of 1
                Text(
                    text = "Ulasan Anda",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = EarthBrown
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewViewModel.updateComment(it) },
                    placeholder = {
                        Text(
                            "Bagikan pengalaman Anda di ${place.name}...",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp), // Height for 2-3 lines
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (submitError != null) Color(0xFFF44336) else LightBrown,
                        unfocusedBorderColor = Color.Gray,
                        errorBorderColor = Color(0xFFF44336),
                        errorContainerColor = Color(0xFFF44336).copy(alpha = 0.1f),
                        focusedContainerColor = WarmWhite,
                        unfocusedContainerColor = WarmWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    isError = submitError != null && reviewText.isBlank(),
                    maxLines = 3, // Allow up to 3 lines
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Character count
                Text(
                    text = "${reviewText.length} / 500 karakter",
                    fontSize = 12.sp,
                    color = if (reviewText.length > 500) Color(0xFFF44336) else Color.Gray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Success/error messages
                submitError?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF44336).copy(alpha = 0.1f)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFF44336).copy(alpha = 0.5f))
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
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Submit button
                Button(
                    onClick = { submitReview() },
                    enabled = currentUser != null && rating > 0 && reviewText.isNotBlank() && reviewText.length <= 500,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EarthBrown,
                        contentColor = Color.White,
                        disabledContainerColor = LightBrown.copy(alpha = 0.5f),
                        disabledContentColor = Color.White.copy(alpha = 0.7f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Mengirim...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Kirim Review",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Kirim Review",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Terms note
                Text(
                    text = "Dengan mengirim review, Anda setuju dengan panduan review kami. " +
                          "Review dengan konten ofensif akan dihapus.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}