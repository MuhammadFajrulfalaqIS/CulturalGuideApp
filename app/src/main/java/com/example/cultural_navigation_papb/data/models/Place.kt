package com.example.cultural_navigation_papb.data.models

// File: data/models/Place.kt


data class Place(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String
)

// Data Placeholder untuk Carousel
val prambananHighlights = listOf(
    Place(
        id = "1",
        name = "Candi Siwa",
        description = "Candi utama dan tertinggi yang didedikasikan untuk Dewa Siwa.",
        imageUrl = "https://example.com/siwa.jpg" // Ganti dengan URL riil nanti
    ),
    Place(
        id = "2",
        name = "Candi Wisnu",
        description = "Berada di utara, didedikasikan untuk Dewa Wisnu, sang pemelihara.",
        imageUrl = "https://example.com/wisnu.jpg"
    ),
    Place(
        id = "3",
        name = "Candi Brahma",
        description = "Berada di selatan, didedikasikan untuk Dewa Brahma, sang pencipta.",
        imageUrl = "https://example.com/brahma.jpg"
    )
)