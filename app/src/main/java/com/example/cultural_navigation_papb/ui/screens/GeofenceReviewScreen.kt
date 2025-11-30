package com.example.cultural_navigation_papb.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.cultural_navigation_papb.data.models.Place
import com.example.cultural_navigation_papb.data.models.Review
import com.example.cultural_navigation_papb.data.viewmodels.ReviewViewModel
import com.example.cultural_navigation_papb.ui.theme.CulturalnavigationpapbTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeofenceReviewScreen(
    placeId: String,
    onNavigateUp: () -> Unit,
    viewModel: ReviewViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var rating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }

    // Load place details
    LaunchedEffect(placeId) {
        viewModel.loadPlaceForReview(placeId)
    }

    // Show success/error messages
    LaunchedEffect(uiState.reviewSubmitted) {
        if (uiState.reviewSubmitted) {
            Toast.makeText(context, "Review submitted successfully!", Toast.LENGTH_SHORT).show()
            onNavigateUp()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    CulturalnavigationpapbTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (uiState.place?.isVisited == true) "Review Your Visit" else "Review Destination",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateUp) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { paddingValues ->
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                uiState.place?.let { place ->
                    ReviewScreenContent(
                        place = place,
                        rating = rating,
                        onRatingChange = { rating = it },
                        comment = comment,
                        onCommentChange = { comment = it },
                        isSubmitting = uiState.isSubmitting,
                        onSubmitReview = {
                            if (rating > 0 && comment.isNotBlank()) {
                                viewModel.submitReview(
                                    Review(
                                        id = "", // Will be generated
                                        placeId = placeId,
                                        userId = viewModel.getCurrentUserId(),
                                        userName = "Anonymous User", // You might want to get this from user profile
                                        rating = rating.toFloat(),
                                        comment = comment,
                                        timestamp = System.currentTimeMillis()
                                    )
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                } ?: run {
                    // Place not found
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Place not found",
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = onNavigateUp) {
                                Text("Go Back")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewScreenContent(
    place: Place,
    rating: Int,
    onRatingChange: (Int) -> Unit,
    comment: String,
    onCommentChange: (String) -> Unit,
    isSubmitting: Boolean,
    onSubmitReview: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Place Image
        AsyncImage(
            model = place.imageUrl,
            contentDescription = place.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        // Visit Status Badge
        if (place.isVisited) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "You visited this place on ${
                            place.lastVisitedAt?.let {
                                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                    .format(Date(it))
                            } ?: "a recent date"
                        }",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Place Info
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = place.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = place.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Rating Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "How would you rate your visit?",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        IconButton(
                            onClick = { onRatingChange(index + 1) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = if (index < rating) {
                                    Icons.Filled.Star
                                } else {
                                    Icons.Outlined.StarBorder
                                },
                                contentDescription = "Star ${index + 1}",
                                tint = if (index < rating) {
                                    Color(0xFFFFC107) // Yellow
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    if (rating > 0) {
                        Text(
                            text = "$rating/5",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Comment Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Share your experience",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                OutlinedTextField(
                    value = comment,
                    onValueChange = onCommentChange,
                    label = { Text("Tell us about your visit...") },
                    placeholder = { Text("What did you like? What could be improved?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5,
                    enabled = !isSubmitting
                )
            }
        }

        // Visit History (if available)
        if (place.isVisited && place.visitCount > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Visit History",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "This is your ${getVisitCountText(place.visitCount)} visit to this place",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Submit Button
        Button(
            onClick = onSubmitReview,
            modifier = Modifier.fillMaxWidth(),
            enabled = rating > 0 && comment.isNotBlank() && !isSubmitting
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submitting...")
            } else {
                Text("Submit Review")
            }
        }

        // Helper text
        Text(
            text = "Your review helps other visitors plan their trip to cultural destinations.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
private fun getVisitCountText(count: Int): String {
    return when (count) {
        1 -> "first"
        2 -> "second"
        3 -> "third"
        else -> "${count}th"
    }
}