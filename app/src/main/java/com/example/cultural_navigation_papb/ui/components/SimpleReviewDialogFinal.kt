package com.example.cultural_navigation_papb.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Verified
import androidx.compose.foundation.clickable
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.cultural_navigation_papb.data.models.Place
import com.example.cultural_navigation_papb.data.viewmodels.AuthViewModel
import com.example.cultural_navigation_papb.data.viewmodels.ReviewViewModel
import com.example.cultural_navigation_papb.data.models.Review
import com.example.cultural_navigation_papb.data.models.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleReviewDialogFinal(
    place: Place,
    onDismiss: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    reviewViewModel: ReviewViewModel = hiltViewModel()
) {
    // State untuk dialog
    var showReviewDialog by remember { mutableStateOf(false) }
    val rating by reviewViewModel.rating.collectAsState()
    val reviewText by reviewViewModel.comment.collectAsState()

    // Get current user
    val currentUser by authViewModel.currentUser.collectAsState()

    // Load reviews untuk place ini
    LaunchedEffect(place.id) {
        reviewViewModel.loadReviewsForPlace(place.id)
    }

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

            showReviewDialog = false
            // Reset form
            reviewViewModel.updateRating(0f)
            reviewViewModel.updateComment("")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable(
                indication = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Review for ${place.name}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A3428)
                    )

                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User info
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile image or placeholder
                    if (currentUser?.profileImagePath != null) {
                        AsyncImage(
                            model = "file://${currentUser!!.profileImagePath}",
                            contentDescription = "User profile",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF6D4C41), RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "User",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = currentUser?.name ?: "Anonymous",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4A3428)
                        )

                        // Verified badge
                        if (currentUser?.email?.endsWith(".ac.id") == true) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Verified Reviewer",
                                    fontSize = 12.sp,
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Rating input
                Text(
                    text = "Your Rating",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4A3428)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Star rating
                    (1..5).forEach { star ->
                        IconButton(
                            onClick = { reviewViewModel.updateRating(star.toFloat()) },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (star <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Rating star",
                                tint = if (star <= rating) Color(0xFFFF6F00) else Color(0xFF9E9E9E),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "${rating.toInt()} / 5",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A3428)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Review text
                Text(
                    text = "Your Review",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF4A3428)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewViewModel.updateComment(it) },
                    placeholder = {
                        Text("Share your experience at ${place.name}...")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (reviewViewModel.submitError.value != null) Color(0xFFF44336) else Color(0xFF6D4C41),
                        unfocusedBorderColor = Color.Gray,
                        errorBorderColor = Color(0xFFF44336),
                        errorContainerColor = Color(0xFFF44336).copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    isError = reviewViewModel.submitError.value != null && reviewText.isBlank()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Success/error messages
                reviewViewModel.submitError.value?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF44336).copy(alpha = 0.1f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF44336).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
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
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Submit button
                Button(
                    onClick = { submitReview() },
                    enabled = currentUser != null && rating > 0 && reviewText.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A3428),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    if (reviewViewModel.isSubmitting.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Submitting...",
                            fontSize = 16.sp,
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
                            text = "Submit Review",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Login prompt if not logged in
                if (currentUser == null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFF6F00).copy(alpha = 0.1f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF6F00).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Login,
                                contentDescription = "Login required",
                                tint = Color(0xFFFF6F00),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Please log in to write a review",
                                color = Color(0xFFFF6F00),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Terms note
                Text(
                    text = "By submitting, you agree to our review guidelines. " +
                          "Reviews with offensive content will be removed.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}