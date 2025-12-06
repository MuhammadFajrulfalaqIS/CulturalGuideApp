package com.example.cultural_navigation_papb.data

import com.example.cultural_navigation_papb.data.models.Place

/**
 * Data candi-candi di kompleks Prambanan dengan koordinat akurat dari Google Maps
 * Siap di-insert ke database saat aplikasi pertama kali dijalankan
 */
object PrambananData {

    val allTemples = listOf(
        // CANDI UTAMA (KOMPLEK SIWA) - Data lengkap dengan koordinat akurat
        Place(
            id = "candi_siwa",
            name = "Candi Siwa",
            description = "Candi utama dan tertinggi yang didedikasikan untuk Dewa Siwa.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.arcasiwa,
            detailedDescription = "Candi Siwa merupakan candi utama di kompleks Candi Prambanan. Dengan ketinggian mencapai 47 meter, candi ini menjadi bangunan tertinggi dan paling megah di kompleks ini. Candi Siwa didedikasikan untuk Dewa Siwa, sang penghancur dalam Trimurti Hindu.",
            historicalInfo = "Dibangun pada abad ke-9 Masehi oleh Raja Rakai Pikatan dari Wangsa Sanjaya. Candi ini merupakan representasi dari kebesaran Kerajaan Mataram Kuno dan menunjukkan tingginya peradaban Hindu di Jawa pada masa itu. Kompleks ini sempat ditinggalkan dan mengalami kerusakan akibat gempa bumi pada abad ke-16.",
            architectureInfo = "Arsitektur candi menampilkan relief-relief indah yang menceritakan kisah Ramayana. Bangunan berbentuk vertikal melambangkan gunung Mahameru, tempat tinggal para dewa. Di dalam candi terdapat empat ruangan yang berisi arca Siwa, Durga (istri Siwa), Agastya (guru), dan Ganesha (putra Siwa).",
            visitingInfo = "Buka setiap hari pukul 06.00-17.00 WIB. Disarankan berkunjung pada pagi hari atau sore hari untuk menghindari terik matahari. Gunakan alas kaki yang nyaman karena akan banyak berjalan di area kompleks candi.",
            latitude = -7.751921183882181,
            longitude = 110.49122544029663,
            category = "candi_utama",
            isAvailable = true,
            rating = 4.9f,
            reviewCount = 3250
        ),

        Place(
            id = "candi_wisnu",
            name = "Candi Wisnu",
            description = "Berada di utara, didedikasikan untuk Dewa Wisnu, sang pemelihara.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.arcawisnu,
            detailedDescription = "Candi Wisnu terletak di sebelah utara Candi Siwa dan merupakan salah satu dari tiga candi utama di kompleks Prambanan. Candi ini didedikasikan kepada Dewa Wisnu, sang pemelihara alam semesta dalam kepercayaan Hindu.",
            historicalInfo = "Sebagai bagian dari kompleks Prambanan yang dibangun pada abad ke-9, Candi Wisnu mencerminkan kepercayaan Hindu Trimurti yang menghormati tiga dewa utama: Brahma (pencipta), Wisnu (pemelihara), dan Siwa (penghancur). Candi ini menjadi bukti toleransi beragama di masa Kerajaan Mataram Kuno.",
            architectureInfo = "Tinggi candi mencapai 33 meter dengan arsitektur yang serupa dengan Candi Siwa namun lebih ramping. Ruang utama candi berisi arca Dewa Wisnu. Relief di dinding candi menggambarkan kisah Kresnayana yang menceritakan petualangan Kresna (avatara Wisnu) sejak kecil.",
            visitingInfo = "Dapat dikunjungi bersamaan dengan Candi Siwa dalam satu tiket terusan. Lokasi candi yang bersebelahan memudahkan wisatawan untuk menjelajahi semua candi utama dalam satu kunjungan.",
            latitude = -7.751661833630753,
            longitude = 110.49119936564723,
            category = "candi_utama",
            isAvailable = true,
            rating = 4.8f,
            reviewCount = 2980
        ),

        Place(
            id = "candi_brahma",
            name = "Candi Brahma",
            description = "Berada di selatan, didedikasikan untuk Dewa Brahma, sang pencipta.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.arcabrahma,
            detailedDescription = "Candi Brahma terletak di sisi selatan Candi Siwa dan merupakan candi ketiga dalam trimurti candi utama Prambanan. Candi ini dipersembahkan untuk Dewa Brahma, sang pencipta alam semesta dalam mitologi Hindu.",
            historicalInfo = "Dibangun sebagai bagian integral dari kompleks Prambanan, Candi Brahma melengkapi konsep Trimurti dalam arsitektur candi. Pembangunannya menunjukkan pemahaman mendalam tentang filosofi Hindu dan kemampuan teknik konstruksi yang tinggi pada masa itu.",
            architectureInfo = "Dengan ketinggian yang sama dengan Candi Wisnu (33 meter), Candi Brahma memiliki arsitektur yang simetris terhadap Candi Wisnu. Di dalam ruang utama terdapat arca Dewa Brahma dengan empat wajah yang menghadap ke empat penjuru mata angin. Relief di dinding candi melanjutkan kisah Ramayana yang dimulai dari Candi Siwa.",
            visitingInfo = "Letaknya yang strategis membuat candi ini mudah diakses dari pintu masuk utama. Pengunjung dapat menikmati relief Ramayana yang berkelanjutan di ketiga candi utama dengan urutan: Brahma → Siwa → Wisnu.",
            latitude = -7.7523278113708,
            longitude = 110.49119861819185,
            category = "candi_utama",
            isAvailable = true,
            rating = 4.7f,
            reviewCount = 2760
        ),

        // CANDI WAHANA (PERWARA)
        Place(
            id = "candi_nandi",
            name = "Candi Nandi",
            description = "Candi wahana berisi arca Nandi, lembu tunggangan Dewa Siwa.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.arcalembunandi,
            detailedDescription = "Candi Nandi terletak tepat di depan Candi Siwa dan merupakan candi wahana yang didedikasikan untuk Nandi, lembu putih suci yang menjadi kendaraan Dewa Siwa.",
            historicalInfo = "Konsep candi wahana merupakan tradisi arsitektur Hindu di Jawa yang menggambarkan wahana atau kendaraan dari dewa yang dipuja di candi utama. Candi Nandi menjadi bukti kelengkapan tata letak kompleks Prambanan sesuai dengan prinsip arsitektur candi Hindu.",
            architectureInfo = "Ukuran candi lebih kecil dari candi utama. Di dalamnya terdapat arca Nandi dalam posisi berbaring menghadap ke arah Candi Siwa. Arca ini dipahat dengan detail yang mengesankan, menunjukkan keterampilan seni pahat tinggi pada masa itu.",
            visitingInfo = "Lokasi candi yang berhadapan langsung dengan Candi Siwa membuatnya mudah ditemukan. Pengunjung dapat melihat arca Nandi dari luar atau masuk ke dalam ruangan candi.",
            latitude = -7.751992941701674,
            longitude = 110.49162777162582,
            category = "candi_perwara",
            isAvailable = true,
            rating = 4.6f,
            reviewCount = 1840
        ),

        Place(
            id = "candi_angsa",
            name = "Candi Angsa",
            description = "Candi wahana di depan Candi Brahma untuk Angsa, tunggangan Dewa Brahma.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.candiangsa,
            detailedDescription = "Candi Angsa terletak berhadapan dengan Candi Brahma sebagai candi wahana. Angsa atau Hamsa dalam mitologi Hindu adalah burung angsa suci yang menjadi kendaraan Dewa Brahma.",
            historicalInfo = "Dalam filosofi Hindu, angsa melambangkan kebijaksanaan dan kemampuan membedakan yang benar dari yang salah. Keberadaan candi ini menunjukkan pemahaman mendalam pembuat candi terhadap simbolisme Hindu.",
            architectureInfo = "Arsitekturnya mengikuti pola candi wahana lainnya di kompleks Prambanan dengan ukuran yang proporsional terhadap candi utamanya. Relief dan ornamen pada candi menggambarkan keindahan dan keanggunan angsa suci.",
            visitingInfo = "Terletak di barisan depan kompleks candi bersama dengan Candi Nandi dan Garuda. Ketiga candi wahana ini dapat dikunjungi secara berurutan untuk memahami konsep lengkap arsitektur kompleks Prambanan.",
            latitude = -7.752316513698344,
            longitude = 110.49163352578545,
            category = "candi_perwara",
            isAvailable = true,
            rating = 4.4f,
            reviewCount = 1290
        ),

        Place(
            id = "candi_garuda",
            name = "Candi Garuda",
            description = "Candi wahana di depan Candi Wisnu untuk Garuda, burung tunggangan Wisnu.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.arcagaruda,
            detailedDescription = "Candi Garuda berada tepat di depan Candi Wisnu. Candi ini didedikasikan untuk Garuda, burung raksasa mitologi yang menjadi kendaraan Dewa Wisnu dalam perjalanannya melintasi alam semesta.",
            historicalInfo = "Garuda memiliki makna penting dalam mitologi Hindu sebagai simbol kebebasan dan kekuatan. Keberadaan candi ini melengkapi konsep arsitektur candi dengan sistem candi utama dan candi wahana yang berpasangan.",
            architectureInfo = "Struktur candi serupa dengan Candi Nandi namun dengan beberapa perbedaan ornamen. Sayangnya, arca Garuda yang asli tidak ditemukan dalam kondisi utuh sehingga yang ada saat ini merupakan rekonstruksi berdasarkan penelitian arkeologis.",
            visitingInfo = "Candi ini dapat dikunjungi dalam satu rute dengan Candi Wisnu. Meskipun ukurannya lebih kecil, candi ini tetap memiliki nilai sejarah dan arsitektur yang penting. Buka setiap hari pukul 06.00-17.00 WIB.",
            latitude = -7.751624850524988,
            longitude = 110.49164788820084,
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
            imageUrl = com.example.cultural_navigation_papb.R.drawable.candi_apit,
            detailedDescription = "Candi Apit adalah sepasang candi kecil yang terletak di sisi utara dan selatan kompleks utama Prambanan. Candi-candi ini berfungsi sebagai penjaga gerbang yang menjaga kesucian kompleks candi.",
            historicalInfo = "Candi Apit dibangun sebagai bagian dari sistem pertahanan spiritual kompleks Prambanan. Posisinya yang strategis di pinggir menunjukkan fungsinya sebagai guardian temples yang melindungi candi-candi utama.",
            architectureInfo = "Meskipun berukuran lebih kecil, Candi Apit memiliki ornamen dan relief yang indah. Arsitekturnya mencerminkan gaya khas Jawa Tengah dengan perpaduan unsur Hindu dan lokal.",
            visitingInfo = "Candi Apit dapat dilihat saat berjalan mengelilingi kompleks utama. Lokasi yang lebih tenang membuatnya cocok untuk pengunjung yang ingin menikmati ketenangan dan berfoto tanpa keramaian.",
            latitude = -7.751580334062159,
            longitude = 110.49141855935162,
            category = "candi_perwara",
            isAvailable = true,
            rating = 4.3f,
            reviewCount = 980
        ),

        Place(
            id = "candi_kelir",
            name = "Candi Kelir",
            description = "Candi pagar yang berfungsi sebagai gerbang spiritual di kompleks Prambanan.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.arcabrahma,
            detailedDescription = "Candi Kelir adalah struktur candi tipis seperti layar yang berfungsi sebagai gerbang simbolis di kompleks Prambanan. Candi ini menandai batas antara dunia profan dan sakral dalam kompleks Prambanan.",
            historicalInfo = "Nama 'Kelir' berasal dari kata Jawa yang berarti layar atau tirai. Candi ini menjadi pembatas spiritual yang harus dilalui pengunjung sebelum memasuki area suci.",
            architectureInfo = "Berbentuk tipis dan tinggi seperti dinding berdiri, Candi Kelir memiliki arsitektur yang unik. Ornamen pada candi menggambarkan motif-motif Hindu yang berfungsi sebagai perlindungan spiritual.",
            visitingInfo = "Terletak di kompleks Prambanan sebagai penanda masuk area suci. Pengunjung dapat melihat struktur unik candi ini yang berbeda dari candi-candi lainnya. Perhatikan detail ornamen yang masih terlihat jelas.",
            latitude = -7.752051404782827,
            longitude = 110.49184032612249,
            category = "candi_perwara",
            isAvailable = true,
            rating = 4.2f,
            reviewCount = 650
        ),

        Place(
            id = "candi_patok",
            name = "Candi Patok",
            description = "Candi kecil yang berfungsi sebagai penanda atau patok batas area sakral kompleks Prambanan.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.arcawisnu,
            detailedDescription = "Candi Patok adalah candi kecil yang berfungsi sebagai penanda batas area sakral di kompleks Prambanan. Nama 'Patok' berarti 'penanda' atau 'patokan' yang menunjukkan fungsinya sebagai pembatas wilayah suci.",
            historicalInfo = "Candi Patok dibangun sebagai bagian dari sistem tata ruang kompleks Prambanan yang terstruktur. Keberadaannya menunjukkan perencanaan arsitektur yang matang dalam pembangunan kompleks candi.",
            architectureInfo = "Berukuran kecil namun memiliki detail arsitektur yang menarik. Struktur candi menunjukkan gaya arsitektur Hindu-Jawa dengan ornamen sederhana namun bermakna.",
            visitingInfo = "Terletak di area kompleks Prambanan. Meskipun kecil, candi ini memiliki nilai arkeologis penting sebagai bagian dari sistem tata ruang keseluruhan kompleks.",
            latitude = -7.752018854242006,
            longitude = 110.4918108323854,
            category = "candi_perwara",
            isAvailable = true,
            rating = 4.1f,
            reviewCount = 420
        ),

        // KOMPLEK SEWU (DI SELATAN KOMPLEK UTAMA)
        Place(
            id = "candi_sewu",
            name = "Candi Sewu",
            description = "Kompleks candi Buddha terbesar kedua setelah Borobudur, terletak sekitar 800 meter utara Candi Prambanan.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.arcasiwa,
            detailedDescription = "Candi Sewu adalah kompleks candi Buddha yang megah dengan nama asli Manjusrigrha. Dengan 249 candi, kompleks ini merupakan bukti toleransi beragama dan kejayaan arsitektur Buddha di Jawa.",
            historicalInfo = "Dibangun pada abad ke-8 Masehi, lebih tua dari Prambanan. Nama Sewu berarti 'seribu' dalam bahasa Jawa, merujuk pada jumlah candi yang sangat banyak meski sebenarnya berjumlah 249.",
            architectureInfo = "Candi utama berbentuk segi delapan dengan empat ruangan berisi arca Buddha. Dikelilingi ratusan candi perwara yang tersusun dalam pola konsentris, menciptakan tata ruang yang harmonis dan simetris.",
            visitingInfo = "Terletak sekitar 800 meter dari Prambanan, dapat dicapai dengan berjalan kaki atau sepeda. Satu tiket dapat digunakan untuk Prambanan dan Sewu. Suasana lebih tenang dan cocok untuk meditasi.",
            latitude = -7.743915650200958,
            longitude = 110.49289041393445,
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
            detailedDescription = "Candi Lumbung adalah candi Buddha yang terletak di kawasan antara Prambanan dan Sewu. Nama 'Lumbung' menunjukkan fungsinya yang diduga sebagai tempat penyimpanan atau gudang benda-benda sakral.",
            historicalInfo = "Dibangun pada periode yang sama dengan kompleks Sewu, candi ini menjadi bukti kehidupan religius yang aktif di kawasan Prambanan pada masa lalu. Keberadaannya menunjukkan toleransi antara Hindu dan Buddha.",
            architectureInfo = "Arsitektur candi menunjukkan gaya Buddha khas Jawa Tengah dengan stupa dan relief Buddha. Meskipun berukuran lebih kecil dari Sewu, candi ini memiliki detail arsitektur yang menarik.",
            visitingInfo = "Terletak di jalur antara Prambanan dan Sewu. Pengunjung dapat berhenti sejenak untuk mengamati candi ini saat menuju Sewu. Suasana tenang dan cocok untuk refleksi.",
            latitude = -7.748115761620499,
            longitude = 110.49295107641981,
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
            detailedDescription = "Candi Bubrah adalah reruntuhan candi Buddha yang sedang dalam proses pemugaran. Nama Bubrah berarti 'rusak' atau 'runtuh', menggambarkan kondisi candi yang ditemukan.",
            historicalInfo = "Diperkirakan dibangun pada abad ke-9, namun mengalami kerusakan berat akibat gempa bumi dan erupsi Gunung Merapi. Proses pemugaran dimulai pada tahun 1990-an dan masih berlanjut hingga kini.",
            architectureInfo = "Dari reruntuhan yang ada, terlihat bahwa candi ini memiliki arsitektur yang indah dengan relief-relief Buddha. Proses pemugaran menggunakan metode anastylosis, yaitu merekonstruksi dengan menggunakan batu-batu asli.",
            visitingInfo = "Lokasi candi berada di jalur antara Prambanan dan Sewu. Pengunjung dapat melihat langsung proses pemugaran dan restorasi cagar budaya, memberikan pengalaman edukatif tentang pelestarian warisan.",
            latitude = -7.746534150349081,
            longitude = 110.49290557956988,
            category = "candi_buddha",
            isAvailable = true,
            rating = 4.3f,
            reviewCount = 560
        ),

        // MUSEUM DAN FASILITAS
        Place(
            id = "museum_candi_prambanan",
            name = "Museum Candi Prambanan",
            description = "Museum yang menyimpan koleksi artefak, relief, dan sejarah kompleks Prambanan. Sempurna untuk memahami konteks historis.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.photos_pic,
            detailedDescription = "Museum Candi Prambanan adalah pusat informasi dan edukasi yang menampilkan koleksi lengkap artefak dari kompleks Prambanan. Museum ini juga menyelenggarakan pameran tetap tentang sejarah Hindu di Jawa dan pengaruhnya terhadap budaya lokal.",
            historicalInfo = "Museum ini didirikan sebagai upaya pelestarian warisan budaya dan pendidikan publik tentang kejayaan Hindu-Jawa. Koleksinya mencakup arca, relief, prasasti, dan benda-benda peninggalan dari abad ke-8 hingga ke-10 Masehi.",
            architectureInfo = "Bangunan museum didesain dengan konsep terbuka yang memadukan elemen tradisional dan modern. Pencahayaan alami dimanfaatkan untuk menerangi artefak tanpa merusaknya, sementara pendingin udara menjaga kelembaban ideal.",
            visitingInfo = "Berlokasi di area kompleks Prambanan. Buka setiap hari 08.00-17.00 WIB. Tiket terintegrasi dengan Prambanan. Tersedia pemandu wisata berbahasa Indonesia dan Inggris. Cocok untuk pelajar dan peneliti.",
            latitude = -7.749467684419051,
            longitude = 110.49362560713017,
            category = "museum",
            isAvailable = true,
            rating = 4.6f,
            reviewCount = 1320
        ),

        Place(
            id = "loket_candi",
            name = "Loket Tiket Candi Prambanan",
            description = "Pusat pembelian tiket untuk masuk ke kompleks Candi Prambanan dan pertunjukan Ramayana Ballet.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.photos_pic,
            detailedDescription = "Loket Tiket Candi Prambanan adalah pusat layanan penjualan tiket untuk berbagai layanan di kompleks Prambanan, mulai dari tiket masuk candi hingga pertunjukan seni dan tur berpemandu.",
            historicalInfo = "Layanan tiket terus dikembangkan untuk memudahkan wisatawan, dari sistem manual hingga online booking dan layanan pelanggan yang ramah.",
            architectureInfo = "Bangunan loket modern dengan beberapa counter layanan, area tunggu ber-AC, dan papan informasi digital. Terdapat jalur khusus untuk wisatawan mancanegara.",
            visitingInfo = "Buka 06.00-18.00 WIB. Harga tiket masuk: Rp50.000 (domestik), Rp75.000 (mancanegara). Menerima kartu kredit dan QRIS. Ada diskon untuk pelajar dan grup.",
            latitude = -7.753367720356585,
            longitude = 110.49423257073313,
            category = "fasilitas",
            isAvailable = true,
            rating = 4.2f,
            reviewCount = 650
        ),

        Place(
            id = "pusat_souvenir_prambanan",
            name = "Pusat Souvenir Prambanan",
            description = "Pusat perbelanjaan suvenir dan oleh-oleh khas Prambanan dengan berbagai pilihan produk lokal.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.explore_pic,
            detailedDescription = "Pusat Souvenir Prambanan adalah pusat perbelanjaan modern yang menjual berbagai produk suvenir, kerajinan tangan, dan oleh-oleh khas Prambanan dan Yogyakarta.",
            historicalInfo = "Dibangun untuk mengakomodasi kebutuhan wisatawan akan oleh-oleh berkualitas dan mendukung UMKM lokal. Menjadi destinasi belanja favorit setelah mengunjungi candi.",
            architectureInfo = "Bangunan bergaya modern dengan sentuhan arsitektur Jawa. Terdiri dari beberapa lantai dengan kategori produk yang berbeda-beda, mulai dari suvenir murah hingga produk premium.",
            visitingInfo = "Buka 08.00-20.00 WIB. Lokasi strategis dekat pintu keluar. Menerima kartu kredit dan QRIS. Ada diskon untuk pembelian grosir dan pengunjung grup.",
            latitude = -7.753713340515007,
            longitude = 110.494642042283,
            category = "perbelanjaan",
            isAvailable = true,
            rating = 4.3f,
            reviewCount = 1240
        ),

        // PERTUNJUKAN DAN AKOMODASI
        Place(
            id = "sendratari_ramayana_ballet",
            name = "Sendratari Ramayana Ballet",
            description = "Pertunjukan tari Ramayana yang spektakuler di panggung terbuka dengan latar belakang Candi Prambanan.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.sendratari,
            detailedDescription = "Sendratari Ramayana Ballet adalah pertunjukan seni dan budaya yang menggambarkan kisah epik Ramayana dalam bentuk tari dan drama. Dipertunjukkan di panggung terbuka dengan Candi Prambanan sebagai latar yang megah.",
            historicalInfo = "Pertunjukan ini telah diselenggarakan sejak tahun 1960-an dan menjadi salah satu atraksi wisata budaya terpopuler di Yogyakarta. Menggabungkan seni tradisional Jawa dengan teknologi modern untuk menciptakan pengalaman yang tak terlupakan.",
            architectureInfo = "Panggung terbuka dirancang khusus dengan akustik yang baik dan pencahayaan dramatis. Penonton dapat menikmati pertunjukan sambil melihat siluet Candi Prambanan yang indah di malam hari.",
            visitingInfo = "Dipertunjukkan setiap malam (kecuali Selasa) pukul 19.30-21.00 WIB. Tiket: Rp150.000-750.000. Bawa jaket karena bisa dingin. Reservasi disarankan untuk mendapatkan tempat duduk terbaik.",
            latitude = -7.753039387233279,
            longitude = 110.48850499339729,
            category = "pertunjukan",
            isAvailable = true,
            rating = 4.8f,
            reviewCount = 3250
        ),

        Place(
            id = "bumi_perkemahan_prambanan",
            name = "Bumi Perkemahan Prambanan",
            description = "Area perkemahan dengan pemandangan Candi Prambanan, cocok untuk kegiatan outdoor dan family gathering.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.bumi_kemah,
            detailedDescription = "Bumi Perkemahan Prambanan adalah area camping ground yang terletak strategis dengan pemandangan langsung ke Candi Prambanan. Menyediakan fasilitas perkemahan modern dengan nuansa alam.",
            historicalInfo = "Dibangun sebagai bagian dari pengembangan pariwisata Prambanan, area ini menjadi alternatif akomodasi wisatawan yang ingin mengalami penginapan yang berbeda dekat dengan situs bersejarah.",
            architectureInfo = "Area perkemahan dirancang dengan sistem zona yang jelas: area tenda, fasilitas umum, dan area rekreasi. Terdapat jalur pejalan kaki yang tertata dengan baik dan pencahayaan malam.",
            visitingInfo = "Buka 24 jam. Tarif tenda: Rp150.000-300.000/malam. Fasilitas: toilet umum, shower, mushola, area BBQ, dan security 24 jam. Reservasi diperlukan untuk peak season.",
            latitude = -7.750346446068719,
            longitude = 110.49105592332168,
            category = "akomodasi",
            isAvailable = true,
            rating = 4.4f,
            reviewCount = 780
        ),

        // KULINER
        Place(
            id = "rama_shinta_garden_resto",
            name = "Rama Shinta Garden Resto",
            description = "Restoran dengan konsep taman yang menyajikan masakan tradisional dan modern dengan pemandangan Candi Prambanan.",
            imageUrl = com.example.cultural_navigation_papb.R.drawable.rama_resto,
            detailedDescription = "Rama Shinta Garden Resto adalah restoran berkonsep garden dining yang menawarkan pengalaman kuliner unik dengan pemandangan langsung ke Candi Prambanan. Menu yang ditawarkan memadukan masakan tradisional Jawa dan fusion modern.",
            historicalInfo = "Nama Rama Shinta diambil dari tokoh utama dalam epik Ramayana yang reliefnya terukir indah di Candi Prambanan. Restoran ini didirikan untuk memberikan pengalaman gastronomi yang memadukan kuliner dan budaya.",
            architectureInfo = "Desain restoran menggabungkan konsep taman tropis dengan bangunan joglo modern. Terdapat area indoor ber-AC dan outdoor garden dengan gazebo-gazebo untuk makan santai. Dekorasi terinspirasi dari seni dan budaya Jawa klasik.",
            visitingInfo = "Buka setiap hari pukul 10.00-22.00 WIB. Menu mulai dari Rp35.000-150.000. Spesialisasi: Nasi Liwet Prambanan, Ayam Bakar Ramayana, Iga Bakar Siwa. Reservasi direkomendasikan untuk weekend. Menerima pembayaran cashless.",
            latitude = -7.752578620343696,
            longitude = 110.48865054055716,
            category = "kuliner",
            isAvailable = true,
            rating = 4.5f,
            reviewCount = 1650
        )
    )

    /**
     * Path yang direkomendasikan untuk tour candi Prambanan
     */
    val recommendedPaths = listOf(
        // Path 1: Komplek Utama (1.5-2 jam)
        listOf("candi_nandi", "candi_siwa", "candi_garuda", "candi_wisnu", "candi_angsa", "candi_brahma"),

        // Path 2: Komplek Lengkap (3-4 jam)
        listOf("museum_candi_prambanan", "candi_siwa", "candi_wisnu", "candi_brahma", "candi_sewu", "candi_lumbung", "candi_bubrah"),

        // Path 3: Foto Tour (2 jam)
        listOf("candi_siwa", "candi_nandi", "candi_apit", "candi_kelir", "sendratari_ramayana_ballet")
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

/**
 * Data class untuk informasi ringkasan Prambanan
 */
data class PrambananSummary(
    val title: String,
    val highlightInfo: String,
    val summary: String
)

/**
 * Daftar ringkasan informasi tentang Prambanan
 */
val prambananSummaries = listOf(
    PrambananSummary(
        title = "Candi Prambanan",
        highlightInfo = "Situs Warisan Dunia UNESCO",
        summary = "Kompleks candi Hindu terbesar di Indonesia yang dibangun pada abad ke-9 Masehi. Merupakan mahakarya arsitektur Hindu dengan 240 candi yang tersebar di area kompleks."
    ),
    PrambananSummary(
        title = "Candi Trimurti",
        highlightInfo = "3 Candi Utama",
        summary = "Candi Siwa (47m), Candi Wisnu, dan Candi Brahma merupakan tiga candi utama yang mewakili Trimurti Hindu: penghancur, pemelihara, dan pencipta alam semesta."
    ),
    PrambananSummary(
        title = "Relief Ramayana",
        highlightInfo = "Kisah Epik",
        summary = "Dinding candi dihiasi dengan relief indah yang menceritakan kisah Ramayana, epik klasik India yang menggambarkan petualangan Rama dalam menyelamatkan Sinta."
    ),
    PrambananSummary(
        title = "Lokasi & Akses",
        highlightInfo = "17 km dari Yogyakarta",
        summary = "Terletak di perbatasan Yogyakarta dan Jawa Tengah, mudah diakses dengan berbagai transportasi. Buka setiap hari pukul 06.00-17.00 WIB."
    )
)
