package com.example.cultural_navigation_papb.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cultural_navigation_papb.data.models.Place
import kotlinx.coroutines.launch

enum class LocationFilter {
    ALL,
    VISITED,
    UNVISITED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DraggableBottomSheetAlt(
    locations: List<Place>,
    selectedIndex: Int,
    visitedPlaceIds: Set<String> = emptySet(),
    onLocationClick: (Int) -> Unit,
    onFilterChanged: (LocationFilter) -> Unit,
    currentFilter: LocationFilter = LocationFilter.ALL
) {
    val scope = rememberCoroutineScope()

    // Standard 3-state bottom sheet: Hidden, PartiallyExpanded, Expanded
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
        confirmValueChange = { true }
    )

    // Show sheet automatically when locations are available
    LaunchedEffect(locations.isNotEmpty()) {
        if (locations.isNotEmpty() && sheetState.isVisible.not()) {
            sheetState.show()
        }
    }

    val isExpanded by remember { derivedStateOf {
        sheetState.currentValue == SheetValue.Expanded
    } }

    val isHalfExpanded by remember { derivedStateOf {
        sheetState.currentValue == SheetValue.PartiallyExpanded
    } }

    // Calculate visible locations based on filter
    val filteredLocations = remember(currentFilter, locations, visitedPlaceIds) {
        when (currentFilter) {
            LocationFilter.ALL -> locations
            LocationFilter.VISITED -> locations.filter { it.id in visitedPlaceIds }
            LocationFilter.UNVISITED -> locations.filter { it.id !in visitedPlaceIds }
        }
    }

    // Show items based on expansion state
    val displayItems = when {
        isExpanded -> filteredLocations
        isHalfExpanded -> filteredLocations.take(3) // Show 3 items when half-expanded
        else -> emptyList()
    }

    // Standard ModalBottomSheet with proper positioning
    if (sheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch { sheetState.hide() }
            },
            sheetState = sheetState,
            dragHandle = {
                // Drag handle indicator
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Drag indicator
                    Surface(
                        modifier = Modifier
                            .width(32.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = Color(0xFF8D6E63)
                    ) {}

                    Spacer(modifier = Modifier.height(12.dp))

                    // Title
                    Text(
                        text = "Pilih Destinasi Prambanan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3E2723)
                    )

                    // Show filter chips only when expanded or half-expanded
                    if (isExpanded || isHalfExpanded) {
                        Spacer(modifier = Modifier.height(12.dp))

                        // Filter chips
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            LocationFilter.values().forEach { filter ->
                                FilterChip(
                                    selected = currentFilter == filter,
                                    onClick = { onFilterChanged(filter) },
                                    label = {
                                        Text(
                                            text = when (filter) {
                                                LocationFilter.ALL -> "Semua"
                                                LocationFilter.VISITED -> "Dikunjungi"
                                                LocationFilter.UNVISITED -> "Belum"
                                            },
                                            color = if (currentFilter == filter) Color.White else Color(0xFF8D6E63)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = when (filter) {
                                            LocationFilter.ALL -> Color(0xFF4CAF50)
                                            LocationFilter.VISITED -> Color(0xFF2196F3)
                                            LocationFilter.UNVISITED -> Color(0xFFFF9800)
                                        },
                                        selectedLabelColor = Color.White,
                                        containerColor = Color.White,
                                        labelColor = Color(0xFF8D6E63)
                                    )
                                )
                            }
                        }
                    }

                    // Filter info
                    if (isExpanded || isHalfExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        val filterText = when (currentFilter) {
                            LocationFilter.ALL -> "Semua Destinasi"
                            LocationFilter.VISITED -> "Destinasi yang Dikunjungi"
                            LocationFilter.UNVISITED -> "Destinasi Belum Dikunjungi"
                        }
                        Text(
                            text = "${filteredLocations.size} $filterText",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF8D6E63),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            tonalElevation = 8.dp,
            scrimColor = Color.Black.copy(alpha = 0.32f)
        ) {
            // Content with white background
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .animateContentSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Location list
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(displayItems) { originalIndex, place ->
                        val actualIndex = locations.indexOfFirst { it.id == place.id }
                        if (actualIndex >= 0) {
                            SimpleLocationListItem(
                                place = place,
                                number = actualIndex + 1,
                                isSelected = selectedIndex == actualIndex,
                                isVisited = place.id in visitedPlaceIds,
                                onClick = { onLocationClick(actualIndex) }
                            )
                        }
                    }

                    // Add "Show more" indicator when half-expanded
                    if (isHalfExpanded && filteredLocations.size > 3) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        scope.launch { sheetState.expand() }
                                    },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFFF3E0)
                                ),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Lihat ${filteredLocations.size - 3} destinasi lagi",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF3E2723),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.ExpandMore,
                                        contentDescription = "Expand",
                                        tint = Color(0xFF3E2723),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Add bottom padding
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    } else {
        // Collapsed state - show floating action button like bar at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            contentAlignment = Alignment.BottomCenter
        ) {
            CollapsedDestinationBar(
                destination = filteredLocations.getOrNull(selectedIndex),
                onClick = {
                    scope.launch { sheetState.show() }
                }
            )
        }
    }
}

@Composable
fun CollapsedDestinationBar(
    destination: Place?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Destination info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = destination?.name ?: "Pilih Destinasi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3E2723),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = destination?.description ?: "Ketuk untuk melihat destinasi",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF8D6E63),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Expand button
            IconButton(
                onClick = onClick
            ) {
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "Lihat Semua",
                    tint = Color(0xFF6D4C41)
                )
            }
        }
    }
}

@Composable
fun SimpleLocationListItem(
    place: Place,
    number: Int,
    isSelected: Boolean,
    isVisited: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> Color(0xFFFF6F00).copy(alpha = 0.1f)
                isVisited -> Color(0xFF4CAF50).copy(alpha = 0.05f)
                else -> Color.White
            }
        ),
        border = when {
            isSelected -> androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFF6F00))
            isVisited -> androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4CAF50))
            else -> null
        },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Number or visited indicator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        when {
                            isVisited -> Color(0xFF4CAF50)
                            isSelected -> Color(0xFFFF6F00)
                            else -> Color(0xFF6D4C41)
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isVisited) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Visited",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = number.toString(),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Place Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = place.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A3428),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (isVisited) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp)),
                            color = Color(0xFF4CAF50).copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "Dikunjungi",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF4CAF50),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = place.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐ ${String.format("%.1f", place.rating)}",
                        fontSize = 12.sp,
                        color = Color(0xFFFF6F00),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${place.reviewCount} ulasan)",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // Navigation Icon
            Icon(
                Icons.Default.Directions,
                contentDescription = "Navigate",
                tint = if (isSelected) Color(0xFFFF6F00) else Color(0xFF6D4C41),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}