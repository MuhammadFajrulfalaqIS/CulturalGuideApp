package com.example.cultural_navigation_papb.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.cultural_navigation_papb.R
import com.example.cultural_navigation_papb.data.converters.Converters
import com.google.android.gms.maps.model.LatLng

// File: data/models/Place.kt

@Entity(tableName = "places")
@TypeConverters(Converters::class)
data class Place(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: Int,
    val detailedDescription: String = description, // Full detailed description for detail screen
    val historicalInfo: String = "", // Historical background
    val architectureInfo: String = "", // Architectural details
    val visitingInfo: String = "", // Visiting hours, tips, etc.
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val category: String = "",
    val isAvailable: Boolean = true,
    val rating: Float = 0.0f,
    val reviewCount: Int = 0
) {
    // Helper function to get LatLng for maps
    fun getLatLng(): LatLng = LatLng(latitude, longitude)

    // Helper function to check if place is open
    fun isOpen(): Boolean = isAvailable
}

// Data Placeholder untuk Carousel
val prambananHighlights = listOf(
    Place(
        id = "1",
        name = "Candi Siwa",
        description = "Candi utama dan tertinggi yang didedikasikan untuk Dewa Siwa.",
        imageUrl = R.drawable.arcasiwa, // CHANGE THIS to your actual image
        detailedDescription = "Candi Siwa merupakan candi utama di kompleks Candi Prambanan. Dengan ketinggian mencapai 47 meter, candi ini menjadi bangunan tertinggi dan paling megah di kompleks ini. Candi Siwa didedikasikan untuk Dewa Siwa, sang penghancur dalam Trimurti Hindu.",
        historicalInfo = "Dibangun pada abad ke-9 Masehi oleh Raja Rakai Pikatan dari Wangsa Sanjaya. Candi ini merupakan representasi dari kebesaran Kerajaan Mataram Kuno dan menunjukkan tingginya peradaban Hindu di Jawa pada masa itu. Kompleks ini sempat ditinggalkan dan mengalami kerusakan akibat gempa bumi pada abad ke-16.",
        architectureInfo = "Arsitektur candi menampilkan relief-relief indah yang menceritakan kisah Ramayana. Bangunan berbentuk vertikal melambangkan gunung Mahameru, tempat tinggal para dewa. Di dalam candi terdapat empat ruangan yang berisi arca Siwa, Durga (istri Siwa), Agastya (guru), dan Ganesha (putra Siwa).",
        visitingInfo = "Buka setiap hari pukul 06.00-17.00 WIB. Disarankan berkunjung pada pagi hari atau sore hari untuk menghindari terik matahari. Gunakan alas kaki yang nyaman karena akan banyak berjalan di area kompleks candi.",
        latitude = -7.752128,
        longitude = 110.491732,
        category = "candi_utama",
        isAvailable = true,
        rating = 4.9f,
        reviewCount = 3250
    ),
    Place(
        id = "2",
        name = "Candi Wisnu",
        description = "Berada di utara, didedikasikan untuk Dewa Wisnu, sang pemelihara.",
        imageUrl = R.drawable.arcawisnu, // CHANGE THIS to your actual image
        detailedDescription = "Candi Wisnu terletak di sebelah utara Candi Siwa dan merupakan salah satu dari tiga candi utama di kompleks Prambanan. Candi ini didedikasikan kepada Dewa Wisnu, sang pemelihara alam semesta dalam kepercayaan Hindu.",
        historicalInfo = "Sebagai bagian dari kompleks Prambanan yang dibangun pada abad ke-9, Candi Wisnu mencerminkan kepercayaan Hindu Trimurti yang menghormati tiga dewa utama: Brahma (pencipta), Wisnu (pemelihara), dan Siwa (penghancur). Candi ini menjadi bukti toleransi beragama di masa Kerajaan Mataram Kuno.",
        architectureInfo = "Tinggi candi mencapai 33 meter dengan arsitektur yang serupa dengan Candi Siwa namun lebih ramping. Ruang utama candi berisi arca Dewa Wisnu. Relief di dinding candi menggambarkan kisah Kresnayana yang menceritakan petualangan Kresna (avatara Wisnu) sejak kecil.",
        visitingInfo = "Dapat dikunjungi bersamaan dengan Candi Siwa dalam satu tiket terusan. Lokasi candi yang bersebelahan memudahkan wisatawan untuk menjelajahi semua candi utama dalam satu kunjungan.",
        latitude = -7.751489,
        longitude = 110.492237,
        category = "candi_utama",
        isAvailable = true,
        rating = 4.8f,
        reviewCount = 2980
    ),
    Place(
        id = "3",
        name = "Candi Brahma",
        description = "Berada di selatan, didedikasikan untuk Dewa Brahma, sang pencipta.",
        imageUrl = R.drawable.arcabrahma, // CHANGE THIS to your actual image
        detailedDescription = "Candi Brahma terletak di sisi selatan Candi Siwa dan merupakan candi ketiga dalam trimurti candi utama Prambanan. Candi ini dipersembahkan untuk Dewa Brahma, sang pencipta alam semesta dalam mitologi Hindu.",
        historicalInfo = "Dibangun sebagai bagian integral dari kompleks Prambanan, Candi Brahma melengkapi konsep Trimurti dalam arsitektur candi. Pembangunannya menunjukkan pemahaman mendalam tentang filosofi Hindu dan kemampuan teknik konstruksi yang tinggi pada masa itu.",
        architectureInfo = "Dengan ketinggian yang sama dengan Candi Wisnu (33 meter), Candi Brahma memiliki arsitektur yang simetris terhadap Candi Wisnu. Di dalam ruang utama terdapat arca Dewa Brahma dengan empat wajah yang menghadap ke empat penjuru mata angin. Relief di dinding candi melanjutkan kisah Ramayana yang dimulai dari Candi Siwa.",
        visitingInfo = "Letaknya yang strategis membuat candi ini mudah diakses dari pintu masuk utama. Pengunjung dapat menikmati relief Ramayana yang berkelanjutan di ketiga candi utama dengan urutan: Brahma → Siwa → Wisnu.",
        latitude = -7.752767,
        longitude = 110.491278,
        category = "candi_utama",
        isAvailable = true,
        rating = 4.7f,
        reviewCount = 2760
    )
)

// Additional sample data for list screen
val allPrambananPlaces = prambananHighlights + listOf(
    Place(
        id = "4",
        name = "Candi Nandi",
        description = "Candi wahana berisi arca Nandi, lembu tunggangan Dewa Siwa.",
        imageUrl = R.drawable.arcalembunandi, // CHANGE THIS to your actual image
        detailedDescription = "Candi Nandi terletak tepat di depan Candi Siwa dan merupakan candi wahana yang didedikasikan untuk Nandi, lembu putih suci yang menjadi kendaraan Dewa Siwa.",
        historicalInfo = "Konsep candi wahana merupakan tradisi arsitektur Hindu di Jawa yang menggambarkan wahana atau kendaraan dari dewa yang dipuja di candi utama. Candi Nandi menjadi bukti kelengkapan tata letak kompleks Prambanan sesuai dengan prinsip arsitektur candi Hindu.",
        architectureInfo = "Ukuran candi lebih kecil dari candi utama. Di dalamnya terdapat arca Nandi dalam posisi berbaring menghadap ke arah Candi Siwa. Arca ini dipahat dengan detail yang mengesankan, menunjukkan keterampilan seni pahat tinggi pada masa itu.",
        visitingInfo = "Lokasi candi yang berhadapan langsung dengan Candi Siwa membuatnya mudah ditemukan. Pengunjung dapat melihat arca Nandi dari luar atau masuk ke dalam ruangan candi.",
        latitude = -7.752988,
        longitude = 110.491602,
        category = "candi_perwara",
        isAvailable = true,
        rating = 4.6f,
        reviewCount = 1840
    ),
    Place(
        id = "5",
        name = "Candi Garuda",
        description = "Candi wahana di depan Candi Wisnu untuk Garuda, burung tunggangan Wisnu.",
        imageUrl = R.drawable.arcagaruda, // CHANGE THIS to your actual image
        detailedDescription = "Candi Garuda berada tepat di depan Candi Wisnu. Candi ini didedikasikan untuk Garuda, burung raksasa mitologi yang menjadi kendaraan Dewa Wisnu dalam perjalanannya melintasi alam semesta.",
        historicalInfo = "Garuda memiliki makna penting dalam mitologi Hindu sebagai simbol kebebasan dan kekuatan. Keberadaan candi ini melengkapi konsep arsitektur candi dengan sistem candi utama dan candi wahana yang berpasangan.",
        architectureInfo = "Struktur candi serupa dengan Candi Nandi namun dengan beberapa perbedaan ornamen. Sayangnya, arca Garuda yang asli tidak ditemukan dalam kondisi utuh sehingga yang ada saat ini merupakan rekonstruksi berdasarkan penelitian arkeologis.",
        visitingInfo = "Candi ini dapat dikunjungi dalam satu rute dengan Candi Wisnu. Meskipun ukurannya lebih kecil, candi ini tetap memiliki nilai sejarah dan arsitektur yang penting.",
        latitude = -7.751278,
        longitude = 110.492581,
        category = "candi_perwara",
        isAvailable = true,
        rating = 4.5f,
        reviewCount = 1520
    ),
    Place(
        id = "6",
        name = "Candi Angsa",
        description = "Candi wahana di depan Candi Brahma untuk Angsa, tunggangan Dewa Brahma.",
        imageUrl = R.drawable.candiangsa, // CHANGE THIS to your actual image
        detailedDescription = "Candi Angsa terletak berhadapan dengan Candi Brahma sebagai candi wahana. Angsa atau Hamsa dalam mitologi Hindu adalah burung angsa suci yang menjadi kendaraan Dewa Brahma.",
        historicalInfo = "Dalam filosofi Hindu, angsa melambangkan kebijaksanaan dan kemampuan membedakan yang benar dari yang salah. Keberadaan candi ini menunjukkan pemahaman mendalam pembuat candi terhadap simbolisme Hindu.",
        architectureInfo = "Arsitekturnya mengikuti pola candi wahana lainnya di kompleks Prambanan dengan ukuran yang proporsional terhadap candi utamanya. Relief dan ornamen pada candi menggambarkan keindahan dan keanggunan angsa suci.",
        visitingInfo = "Terletak di barisan depan kompleks candi bersama dengan Candi Nandi dan Garuda. Ketiga candi wahana ini dapat dikunjungi secara berurutan untuk memahami konsep lengkap arsitektur kompleks Prambanan.",
        latitude = -7.753307,
        longitude = 110.490658,
        category = "candi_perwara",
        isAvailable = true,
        rating = 4.4f,
        reviewCount = 1290
    )
)

// Temple Summary data for info card carousel
data class TempleSummary(
    val id: String,
    val title: String,
    val summary: String,
    val highlightInfo: String = ""
)

val prambananSummaries = listOf(
    TempleSummary(
        id = "summary_1",
        title = "Prambanan",
        summary = "Candi Prambanan adalah kompleks candi Hindu terbesar di Indonesia yang dibangun pada abad ke-9 Masehi. Kompleks ini terdiri dari 240 candi, termasuk 3 candi utama Trimurti yang didedikasikan untuk Brahma, Wisnu, dan Siwa.",
        highlightInfo = "Situs Warisan Dunia UNESCO sejak 1991"
    ),
    TempleSummary(
        id = "summary_2",
        title = "Trimurti Candi",
        summary = "Tiga candi utama Prambanan merepresentasikan Trimurti Hindu. Candi Siwa (47m) di tengah, Candi Wisnu (33m) di utara, dan Candi Brahma (33m) di selatan membentuk kesatuan arsitektur yang megah dan harmonis.",
        highlightInfo = "Ketinggian total: 47 meter"
    ),
    TempleSummary(
        id = "summary_3",
        title = "Relief Ramayana",
        summary = "Dinding candi dihiasi relief indah yang menceritakan kisah epik Ramayana. Relief ini menggambarkan petualangan Rama menyelamatkan Sinta dari Rahwana, dipahat dengan detail luar biasa oleh seniman Jawa kuno.",
        highlightInfo = "180+ panel relief"
    ),
    TempleSummary(
        id = "summary_4",
        title = "Candi Wahana",
        summary = "Tiga candi wahana (Nandi, Garuda, dan Angsa) terletak berhadapan dengan candi utama. Candi-candi ini didedikasikan untuk kendaraan para dewa: lembu Nandi untuk Siwa, burung Garuda untuk Wisnu, dan angsa untuk Brahma.",
        highlightInfo = "3 candi pendamping"
    )
)
