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
        imageUrl = R.drawable.arcalembunandi,
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
        imageUrl = R.drawable.arcagaruda,
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
        imageUrl = R.drawable.candiangsa,
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
    ),
    Place(
        id = "7",
        name = "Candi Apit",
        description = "Dua candi kecil yang terletak di antara candi-candi utama, berfungsi sebagai candi penjaga.",
        imageUrl = R.drawable.candi_apit,
        detailedDescription = "Candi Apit adalah sepasang candi kecil yang terletak di sisi utara dan selatan kompleks utama Prambanan. Candi-candi ini berfungsi sebagai penjaga gerbang yang menjaga kesucian kompleks candi.",
        historicalInfo = "Candi Apit dibangun sebagai bagian dari sistem pertahanan spiritual kompleks Prambanan. Posisinya yang strategis di pinggir menunjukkan fungsinya sebagai guardian temples yang melindungi candi-candi utama.",
        architectureInfo = "Meskipun berukuran lebih kecil, Candi Apit memiliki ornamen dan relief yang indah. Arsitekturnya mencerminkan gaya khas Jawa Tengah dengan perpaduan unsur Hindu dan lokal.",
        visitingInfo = "Candi Apit dapat dilihat saat berjalan mengelilingi kompleks utama. Lokasi yang lebih tenang membuatnya cocok untuk pengunjung yang ingin menikmati ketenangan dan berfoto tanpa keramaian.",
        latitude = -7.7518,
        longitude = 110.4892,
        category = "candi_perwara",
        isAvailable = true,
        rating = 4.3f,
        reviewCount = 980
    ),
    Place(
        id = "8",
        name = "Candi Kelir Utara",
        description = "Candi pagar di sisi utara kompleks, berfungsi sebagai gerbang spiritual.",
        imageUrl = R.drawable.candi_kelir,
        detailedDescription = "Candi Kelir Utara adalah struktur candi tipis seperti layar yang berfungsi sebagai gerbang simbolis di sisi utara kompleks. Candi ini menandai batas antara dunia profan dan sakral dalam kompleks Prambanan.",
        historicalInfo = "Nama 'Kelir' berasal dari kata Jawa yang berarti layar atau tirai. Candi ini menjadi pembatas spiritual yang harus dilalui pengunjung sebelum memasuki area suci dari arah utara.",
        architectureInfo = "Berbentuk tipis dan tinggi seperti dinding berdiri, Candi Kelir memiliki arsitektur yang unik. Ornamen pada candi menggambarkan motif-motif Hindu yang berfungsi sebagai perlindungan spiritual dari arah utara.",
        visitingInfo = "Terletak di sisi utara kompleks Prambanan. Pengunjung dapat melihat struktur unik candi ini yang berbeda dari candi-candi lainnya. Perhatikan detail ornamen yang masih terlihat jelas.",
        latitude = -7.750987,
        longitude = 110.493012,
        category = "candi_perwara",
        isAvailable = true,
        rating = 4.2f,
        reviewCount = 650
    ),
    Place(
        id = "9",
        name = "Candi Kelir Selatan",
        description = "Candi pagar di sisi selatan kompleks, menandai batas area sakral.",
        imageUrl = R.drawable.candi_kelir,
        detailedDescription = "Candi Kelir Selatan adalah pasangan dari Candi Kelir Utara, terletak di sisi selatan kompleks. Candi ini berfungsi sebagai gerbang spiritual dan pembatas area sakral dari arah selatan.",
        historicalInfo = "Keberadaan Candi Kelir di sisi utara dan selatan menunjukkan konsep simetris dan keseimbangan dalam arsitektur Hindu-Jawa. Kedua candi ini menjaga kesucian kompleks dari empat penjuru mata angin.",
        architectureInfo = "Memiliki bentuk dan fungsi yang sama dengan Candi Kelir Utara namun dengan orientasi ke arah selatan. Struktur tipis dan tinggi menjadi ciri khas candi kelir yang membedakannya dari jenis candi lain.",
        visitingInfo = "Terletak di sisi selatan kompleks. Pengunjung dapat membandingkan arsitektur dan ornamen kedua Candi Kelir untuk memahami konsep simetri dan keseimbangan dalam tata letak kompleks Prambanan.",
        latitude = -7.753269,
        longitude = 110.490548,
        category = "candi_perwara",
        isAvailable = true,
        rating = 4.2f,
        reviewCount = 590
    ),
    Place(
        id = "10",
        name = "Museum Taman Wisnu",
        description = "Museum arkeologi dengan koleksi artefak Prambanan dan pameran sejarah Hindu di Jawa.",
        imageUrl = R.drawable.photos_pic,
        detailedDescription = "Museum Taman Wisnu adalah museum arkeologi yang menampilkan koleksi lengkap artefak dari kompleks Prambanan. Museum ini juga menyelenggarakan pameran tetap tentang sejarah Hindu di Jawa dan pengaruhnya terhadap budaya lokal.",
        historicalInfo = "Museum ini didirikan sebagai upaya pelestarian warisan budaya dan pendidikan publik tentang kejayaan Hindu-Jawa. Koleksinya mencakup arca, relief, prasasti, dan benda-benda peninggalan dari abad ke-8 hingga ke-10 Masehi.",
        architectureInfo = "Bangunan museum didesain dengan konsep terbuka yang memadukan elemen tradisional dan modern. Pencahayaan alami dimanfaatkan untuk menerangi artefak tanpa merusaknya, sementara pendingin udara menjaga kelembaban ideal.",
        visitingInfo = "Berlokasi di area utara kompleks Prambanan dekat Taman Wisnu. Buka setiap hari 08.00-17.00 WIB. Tiket terintegrasi dengan Prambanan. Tersedia pemandu wisata berbahasa Indonesia dan Inggris. Cocok untuk pelajar dan peneliti.",
        latitude = -7.750587,
        longitude = 110.493589,
        category = "museum",
        isAvailable = true,
        rating = 4.6f,
        reviewCount = 1420
    ),
    Place(
        id = "11",
        name = "Taman Wisnu",
        description = "Taman hijau dengan pemandangan istimewa ke arah kompleks Prambanan, cocok untuk bersantai.",
        imageUrl = R.drawable.explore_pic,
        detailedDescription = "Taman Wisnu adalah area hijau yang luas dengan pemandangan menakjubkan ke kompleks Candi Prambanan. Taman ini dirancang sebagai ruang publik untuk bersantai, berfoto, dan menikmati keindahan alam sekaligus warisan budaya.",
        historicalInfo = "Taman ini dinamai Taman Wisnu untuk menghormati salah satu dewa Trimurti Hindu yang dipuja di Prambanan. Pembangunan taman ini merupakan bagian dari proyek revitalisasi kawasan Prambanan sebagai destinasi wisata budaya kelas dunia.",
        architectureInfo = "Taman didesain dengan konsep lanskap tropis yang memadukan elemen alam dan budaya. Terdapat gazebo-gazebo dengan arsitektur Jawa, jalur pejalan kaki beraspal, dan spot foto Instagramable dengan latar belakang candi.",
        visitingInfo = "Lokasi strategis dekat Museum Taman Wisnu. Gratis untuk pengunjung yang sudah membeli tiket Prambanan. Waktu terbaik berkunjung adalah pagi hari (06.00-09.00) atau sore hari (15.00-17.00) untuk menghindari panas. Bawa tikar untuk piknik.",
        latitude = -7.750723,
        longitude = 110.493421,
        category = "taman",
        isAvailable = true,
        rating = 4.5f,
        reviewCount = 890
    ),
    Place(
        id = "12",
        name = "Area Parkir Utama",
        description = "Area parkir luas untuk kendaraan pengunjung dengan akses mudah ke pintu masuk utama.",
        imageUrl = R.drawable.area_parkir_utama,
        detailedDescription = "Area Parkir Utama Prambanan adalah fasilitas parkir modern yang dapat menampung ratusan kendaraan roda dua dan roda empat. Dilengkapi dengan sistem keamanan 24 jam dan akses langsung ke area wisata.",
        historicalInfo = "Pembangunan area parkir ini merupakan bagian dari modernisasi infrastruktur Prambanan untuk meningkatkan kenyamanan wisatawan. Sebelumnya, parkir tersebar di beberapa titik yang kurang terorganisir.",
        architectureInfo = "Area parkir dirancang dengan sistem zonasi yang jelas: zona motor, mobil, dan bus wisata. Terdapat kanopi di beberapa area untuk melindungi kendaraan dari panas dan hujan. Lantai beraspal dan memiliki marka yang jelas.",
        visitingInfo = "Terletak di pintu masuk utama kompleks Prambanan. Tarif parkir: Motor Rp3.000, Mobil Rp10.000, Bus Rp20.000. Buka 24 jam. Tersedia toilet umum dan mushola di dekat area parkir. Jaga barang berharga Anda.",
        latitude = -7.753789,
        longitude = 110.492145,
        category = "fasilitas",
        isAvailable = true,
        rating = 4.1f,
        reviewCount = 430
    ),
    Place(
        id = "13",
        name = "Pusat Informasi Pengunjung",
        description = "Pusat informasi dan pembelian tiket dengan staff yang siap membantu pengunjung.",
        imageUrl = R.drawable.photos_pic,
        detailedDescription = "Pusat Informasi Pengunjung adalah fasilitas one-stop service untuk segala kebutuhan informasi wisata Prambanan. Di sini tersedia loket tiket, brosur wisata, peta kompleks, dan petugas yang siap membantu dalam bahasa Indonesia, Inggris, dan Jepang.",
        historicalInfo = "Pusat informasi ini didirikan sebagai respons terhadap meningkatnya jumlah wisatawan mancanegara yang membutuhkan informasi detail tentang Prambanan. Sejak dibuka, kepuasan wisatawan meningkat signifikan.",
        architectureInfo = "Bangunan berarsitektur modern minimalis dengan atap limasan Jawa. Terdapat ruang tunggu ber-AC, rak brosur, layar informasi digital, dan meja konsultasi. Area ini juga dilengkapi free WiFi untuk kenyamanan pengunjung.",
        visitingInfo = "Terletak tepat setelah Area Parkir Utama, sebelum pintu masuk candi. Buka 06.00-18.00 WIB. Layanan: penjualan tiket, info tour guide, penyewaan audio guide (Rp50.000), booking Ramayana Ballet. Kartu kredit diterima.",
        latitude = -7.753456,
        longitude = 110.491987,
        category = "fasilitas",
        isAvailable = true,
        rating = 4.3f,
        reviewCount = 670
    ),
    Place(
        id = "14",
        name = "Warung Budaya",
        description = "Area kuliner tradisional dengan berbagai makanan khas Yogyakarta dan toko suvenir.",
        imageUrl = R.drawable.explore_pic,
        detailedDescription = "Warung Budaya adalah food court dan pusat suvenir yang menyajikan kekayaan kuliner Yogyakarta dalam satu tempat. Dari gudeg, sate klathak, hingga bakpia, semua tersedia dengan harga terjangkau dan rasa autentik.",
        historicalInfo = "Konsep Warung Budaya diciptakan untuk memperkenalkan kuliner lokal kepada wisatawan sembari mendukung UMKM sekitar Prambanan. Sejak dibuka tahun 2015, tempat ini menjadi favorit wisatawan untuk bersantap setelah tur candi.",
        architectureInfo = "Bangunan bergaya joglo dengan area semi-outdoor yang sejuk. Terdapat 15 stan kuliner dan 10 toko suvenir yang menjual batik, miniatur candi, kaos, gantungan kunci, dan kerajinan lokal. Kapasitas duduk 200 orang.",
        visitingInfo = "Terletak di sebelah timur kompleks utama, mudah dijangkau. Buka 07.00-19.00 WIB. Harga makanan Rp15.000-50.000. Rekomendasi: Gudeg Mbah Lindu, Sate Klathak Pak Pong, Es Dawet Mbah Karto. Toilet bersih tersedia. Menerima QRIS.",
        latitude = -7.752345,
        longitude = 110.492876,
        category = "kuliner",
        isAvailable = true,
        rating = 4.4f,
        reviewCount = 1120
    ),
    Place(
        id = "15",
        name = "Candi Sewu",
        description = "Kompleks candi Buddha terbesar kedua setelah Borobudur, terletak sekitar 800 meter selatan Candi Prambanan.",
        imageUrl = R.drawable.candi_sewu,
        detailedDescription = "Candi Sewu adalah kompleks candi Buddha yang megah dengan nama asli Manjusrigrha. Dengan 249 candi, kompleks ini merupakan bukti toleransi beragama dan kejayaan arsitektur Buddha di Jawa.",
        historicalInfo = "Dibangun pada abad ke-8 Masehi, lebih tua dari Prambanan. Nama Sewu berarti 'seribu' dalam bahasa Jawa, merujuk pada jumlah candi yang sangat banyak meski sebenarnya berjumlah 249.",
        architectureInfo = "Candi utama berbentuk segi delapan dengan empat ruangan berisi arca Buddha. Dikelilingi ratusan candi perwara yang tersusun dalam pola konsentris, menciptakan tata ruang yang harmonis dan simetris.",
        visitingInfo = "Terletak 800 meter selatan Prambanan, dapat dicapai dengan berjalan kaki atau sepeda. Satu tiket dapat digunakan untuk Prambanan dan Sewu. Suasana lebih tenang dan cocok untuk meditasi.",
        latitude = -7.746890,
        longitude = 110.492123,
        category = "candi_buddha",
        isAvailable = true,
        rating = 4.7f,
        reviewCount = 2130
    ),
    Place(
        id = "16",
        name = "Candi Bubrah",
        description = "Sisa-sisa reruntuhan candi Buddha di dekat Candi Sewu yang telah dipugar sebagian.",
        imageUrl = R.drawable.candi_bubrah,
        detailedDescription = "Candi Bubrah adalah reruntuhan candi Buddha yang sedang dalam proses pemugaran. Nama Bubrah berarti 'rusak' atau 'runtuh', menggambarkan kondisi candi yang ditemukan.",
        historicalInfo = "Diperkirakan dibangun pada abad ke-9, namun mengalami kerusakan berat akibat gempa bumi dan erupsi Gunung Merapi. Proses pemugaran dimulai pada tahun 1990-an dan masih berlanjut hingga kini.",
        architectureInfo = "Dari reruntuhan yang ada, terlihat bahwa candi ini memiliki arsitektur yang indah dengan relief-relief Buddha. Proses pemugaran menggunakan metode anastylosis, yaitu merekonstruksi dengan menggunakan batu-batu asli.",
        visitingInfo = "Lokasi candi berada di jalur antara Prambanan dan Sewu. Pengunjung dapat melihat langsung proses pemugaran dan restorasi cagar budaya, memberikan pengalaman edukatif tentang pelestarian warisan.",
        latitude = -7.756234,
        longitude = 110.490876,
        category = "candi_buddha",
        isAvailable = true,
        rating = 4.3f,
        reviewCount = 560
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
