package com.example.cultural_navigation_papb.data

import com.example.cultural_navigation_papb.data.models.Place

/**
 * Data candi-candi di kompleks Prambanan dengan koordinat real
 * Siap di-insert ke database saat aplikasi pertama kali dijalankan
 */
object PrambananData {

    val allTemples = listOf(
        // CANDI UTAMA (KOMPLEK SIWA)
        Place(
            id = "candi_siwa",
            name = "Candi Siwa",
            description = "Candi utama dan tertinggi di kompleks Prambanan, didedikasikan untuk Dewa Siwa. Candi ini memiliki tinggi 47 meter dan merupakan mahakarya arsitektur Hindu.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.arcasiwa,
            latitude = -7.7520,
            longitude = 110.4891,
            category = "candi_utama",
            isAvailable = true,
            rating = 4.9f,
            reviewCount = 3250
        ),

        Place(
            id = "candi_wisnu",
            name = "Candi Wisnu",
            description = "Candi di sebelah utara Candi Siwa, didedikasikan untuk Dewa Wisnu sebagai pemelihara alam semesta dalam trinitas Hindu.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.arcawisnu,
            latitude = -7.7515,
            longitude = 110.4895,
            category = "candi_utama",
            isAvailable = true,
            rating = 4.8f,
            reviewCount = 2980
        ),

        Place(
            id = "candi_brahma",
            name = "Candi Brahma",
            description = "Candi di sebelah selatan Candi Siwa, didedikasikan untuk Dewa Brahma sebagai pencipta alam semesta.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.arcabrahma,
            latitude = -7.7525,
            longitude = 110.4895,
            category = "candi_utama",
            isAvailable = true,
            rating = 4.7f,
            reviewCount = 2760
        ),

        Place(
            id = "candi_nandi",
            name = "Candi Nandi",
            description = "Candi pendem di depan Candi Siwa yang dihiasi relief arca sapi Nandi, wahana Dewa Siwa.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.arcalembunandi,
            latitude = -7.7520,
            longitude = 110.4885,
            category = "candi_perwara",
            isAvailable = true,
            rating = 4.6f,
            reviewCount = 1840
        ),

        Place(
            id = "candi_angsa",
            name = "Candi Angsa",
            description = "Candi pendem di depan Candi Brahma dengan relief arca angsa, wahana Dewa Brahma.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.candiangsa,
            latitude = -7.7528,
            longitude = 110.4890,
            category = "candi_perwara",
            isAvailable = true,
            rating = 4.4f,
            reviewCount = 1290
        ),

        Place(
            id = "candi_garuda",
            name = "Candi Garuda",
            description = "Candi pendem di depan Candi Wisnu dengan relief arca burung Garuda, wahana Dewa Wisnu.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.arcagaruda,
            latitude = -7.7512,
            longitude = 110.4890,
            category = "candi_perwara",
            isAvailable = true,
            rating = 4.5f,
            reviewCount = 1520
        ),

        // CANDI PERWARA KECIL
        Place(
            id = "candi_apit",
            name = "Candi Apit",
            description = "Dua candi kecil yang terletak di antara candi-candi utama, berfungsi sebagai candi penjaga.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.arcasiwa,
            latitude = -7.7518,
            longitude = 110.4892,
            category = "candi_perwara",
            isAvailable = true,
            rating = 4.3f,
            reviewCount = 980
        ),

        Place(
            id = "candi_kelir_utara",
            name = "Candi Kelir Utara",
            description = "Candi pagar di sisi utara kompleks, berfungsi sebagai gerbang spiritual.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.arcabrahma,
            latitude = -7.750987,
            longitude = 110.493012,
            category = "candi_perwara",
            isAvailable = true,
            rating = 4.2f,
            reviewCount = 650
        ),

        Place(
            id = "candi_kelir_selatan",
            name = "Candi Kelir Selatan",
            description = "Candi pagar di sisi selatan kompleks, menandai batas area sakral.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.arcawisnu,
            latitude = -7.753269,
            longitude = 110.490548,
            category = "candi_perwara",
            isAvailable = true,
            rating = 4.2f,
            reviewCount = 590
        ),

        // KOMPLEK SEWU (DI SELATAN KOMPLEK UTAMA)
        Place(
            id = "candi_sewu",
            name = "Candi Sewu",
            description = "Kompleks candi Buddha terbesar kedua setelah Borobudur, terletak sekitar 800 meter selatan Candi Prambanan. Nama aslinya adalah Manjusrigrha.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.arcasiwa,
            latitude = -7.746890,
            longitude = 110.492123,
            category = "candi_buddha",
            isAvailable = true,
            rating = 4.7f,
            reviewCount = 2130
        ),

        Place(
            id = "candi_lumbung",
            name = "Candi Lumbung",
            description = "Candi Buddha yang terletak dekat Candi Sewu, berfungsi sebagai tempat penyimpanan barang berharga.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.arcawisnu,
            latitude = -7.7610,
            longitude = 110.4930,
            category = "candi_buddha",
            isAvailable = true,
            rating = 4.3f,
            reviewCount = 890
        ),

        Place(
            id = "candi_bubrah",
            name = "Candi Bubrah",
            description = "Sisa-sisa reruntuhan candi Buddha di dekat Candi Sewu yang telah dipugar sebagian.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.arcabrahma,
            latitude = -7.756234,
            longitude = 110.490876,
            category = "candi_buddha",
            isAvailable = true,
            rating = 4.3f,
            reviewCount = 560
        ),

        // KOMPLEK PLAOSAN (DI TIMUR)
        Place(
            id = "candi_plaosan_lor",
            name = "Candi Plaosan Lor",
            description = "Kompleks candi Buddha yang unik dengan perpaduan arsitektur Hindu dan Buddha, terdiri dari candi utama dan perwara.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.arcasiwa,
            latitude = -7.7480,
            longitude = 110.5000,
            category = "candi_buddha",
            isAvailable = true,
            rating = 4.6f,
            reviewCount = 1420
        ),

        Place(
            id = "candi_plaosan_kidul",
            name = "Candi Plaosan Kidul",
            description = "Bagian selatan dari kompleks Plaosan dengan arsitektur yang serupa namun lebih kecil.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.arcawisnu,
            latitude = -7.7490,
            longitude = 110.5010,
            category = "candi_buddha",
            isAvailable = true,
            rating = 4.4f,
            reviewCount = 1120
        ),

        // MUSEUM DAN FASILITAS REKREASI
        Place(
            id = "museum_candi_prambanan",
            name = "Museum Candi Prambanan",
            description = "Museum yang menyimpan koleksi artefak, relief, dan sejarah kompleks Prambanan. Sempurna untuk memahami konteks historis.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.photos_pic,
            latitude = -7.7540,
            longitude = 110.4880,
            category = "museum",
            isAvailable = true,
            rating = 4.6f,
            reviewCount = 1320
        ),

        Place(
            id = "museum_taman_wisnu",
            name = "Museum Taman Wisnu",
            description = "Museum arkeologi dengan koleksi artefak Prambanan dan pameran sejarah Hindu di Jawa.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.photos_pic,
            latitude = -7.750587,
            longitude = 110.493589,
            category = "museum",
            isAvailable = true,
            rating = 4.6f,
            reviewCount = 1420
        ),

        Place(
            id = "taman_wisnu",
            name = "Taman Wisnu",
            description = "Taman hijau dengan pemandangan istimewa ke arah kompleks Prambanan, cocok untuk bersantai.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.explore_pic,
            latitude = -7.750723,
            longitude = 110.493421,
            category = "taman",
            isAvailable = true,
            rating = 4.5f,
            reviewCount = 890
        ),

        Place(
            id = "taman_safari_prambanan",
            name = "Taman Safari Prambanan",
            description = "Area rekreasi dengan satwa liar yang terletak di dalam kompleks Prambanan, cocok untuk keluarga.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.explore_pic,
            latitude = -7.7560,
            longitude = 110.4940,
            category = "rekreasi",
            isAvailable = true,
            rating = 4.5f,
            reviewCount = 1870
        ),

        // FASILITAS PENGUNJUNG
        Place(
            id = "area_parkir_utama",
            name = "Area Parkir Utama",
            description = "Area parkir luas untuk kendaraan pengunjung dengan akses mudah ke pintu masuk utama.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.prambanan_shadow,
            latitude = -7.753789,
            longitude = 110.492145,
            category = "fasilitas",
            isAvailable = true,
            rating = 4.1f,
            reviewCount = 430
        ),

        Place(
            id = "pusat_informasi_pengunjung",
            name = "Pusat Informasi Pengunjung",
            description = "Pusat informasi dan pembelian tiket dengan staff yang siap membantu pengunjung.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.photos_pic,
            latitude = -7.753456,
            longitude = 110.491987,
            category = "fasilitas",
            isAvailable = true,
            rating = 4.3f,
            reviewCount = 670
        ),

        Place(
            id = "sendang_putri",
            name = "Sendang Putri",
            description = "Mata air suci yang terletak di dekat kompleks candi, dipercaya memiliki nilai spiritual bagi umat Hindu.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.prambanan_shadow,
            latitude = -7.7470,
            longitude = 110.4870,
            category = "situs_suci",
            isAvailable = true,
            rating = 4.3f,
            reviewCount = 740
        ),

        // KULINER
        Place(
            id = "warung_budaya",
            name = "Warung Budaya",
            description = "Area kuliner tradisional dengan berbagai makanan khas Yogyakarta dan toko suvenir.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.explore_pic,
            latitude = -7.752345,
            longitude = 110.492876,
            category = "kuliner",
            isAvailable = true,
            rating = 4.4f,
            reviewCount = 1120
        ),

        Place(
            id = "warung_prambanan",
            name = "Pusat Kuliner Prambanan",
            description = "Area food court dengan berbagai makanan tradisional Yogyakarta dan suvenir khas Prambanan.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.explore_pic,
            latitude = -7.7500,
            longitude = 110.4950,
            category = "kuliner",
            isAvailable = true,
            rating = 4.4f,
            reviewCount = 1580
        )
    )

    /**
     * Path yang direkomendasikan untuk tour candi Prambanan
     */
    val recommendedPaths = listOf(
        // Path 1: Komplek Utama (1.5-2 jam)
        listOf("candi_nandi", "candi_siwa", "candi_garuda", "candi_wisnu", "candi_angsa", "candi_brahma"),

        // Path 2: Komplek Lengkap (3-4 jam)
        listOf("museum_candi_prambanan", "candi_siwa", "candi_wisnu", "candi_brahma", "candi_sewu", "candi_lumbung", "candi_plaosan_lor"),

        // Path 3: Foto Tour (2 jam)
        listOf("candi_siwa", "candi_nandi", "candi_apit", "candi_bubrah", "sendang_putri", "warung_prambanan")
    )

    /**
     * Mendapatkan path berdasarkan durasi yang diinginkan
     */
    fun getPathByDuration(duration: String): List<String> {
        return when (duration.lowercase()) {
            "pendek", "short" -> recommendedPaths[0]
            "panjang", "long" -> recommendedPaths[1]
            "foto", "photography" -> recommendedPaths[2]
            else -> recommendedPaths[0]
        }
    }
}