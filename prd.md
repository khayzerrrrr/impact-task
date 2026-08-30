Konsep Produk
Android · Kotlin
Draft v0.2
Impact Task
Task manager di mana setiap pekerjaan terhubung ke enam aspek hidupmu, punya bobot dampak yang kamu tentukan sendiri, dan menumbuhkan tanaman yang layu kalau ditunda.

01 — Premis
Kenapa ini bukan to-do list biasa
Task punya konsekuensi
To-do list biasa memperlakukan “beli kopi” dan “kirim proposal klien” sebagai baris yang setara. Di sini setiap task dinilai di dua sumbu, dan hasilnya kelihatan.

Progres yang terakumulasi
Task selesai tidak hilang — dia jadi EXP di salah satu dari enam Gains. Setelah sebulan kamu bisa lihat aspek hidup mana yang benar-benar kamu urus.

Penundaan yang terasa
Task yang dibiarkan tidak cuma jadi badge merah. Dia jadi tanaman yang lupa disiram — layunya kelihatan, keluar dari kartunya, dan menunggu di kebun utama layar utamamu.

02 — Koreksi desain
Dua hal di ide awal yang perlu dibetulkan dulu
Ini bagian paling penting. Kalau dua ini tidak dibereskan sekarang, seluruh sistem skoring akan terasa aneh dipakai sehari-hari.

Masalah 1 — Urgensi tidak bisa datang dari 200 poin
Kamu bilang 10 level urgensi diambil dari total Kesulitan + Dampak. Tapi coba dua contoh ini:

Bayar tagihan listrik — kesulitan 5, dampak 30. Total 35, tier terendah. Padahal jatuh tempo besok dan kalau telat listrik mati.
Belajar bahasa Jepang — kesulitan 85, dampak 90. Total 175, tier tertinggi. Padahal tidak ada deadline sama sekali.
Skor 200 mengukur seberapa berat sebuah task, bukan seberapa mendesak. Keduanya beda sumbu.

Solusi — pisahkan jadi dua sumbu, tanamannya tetap satu
Mekaniknya tidak berubah sama sekali dari draf sebelumnya — cuma wujudnya yang diganti, dari monster yang mengejar jadi tanaman yang kamu rawat sendiri, supaya penundaan terasa seperti rasa sayang dan tanggung jawab, bukan rasa takut dikejar. Yang menentukan sistemnya tetap: 200 poin menentukan ukuran & jenis tanaman, deadline menentukan seberapa segar dia.

Sumbu A — Skala Tumbuh 1–10
Dari Kesulitan + Dampak (0–200). Menentukan tanaman apa yang tumbuh dari task ini, berapa EXP yang dia bawa, dan seberapa besar dia digambar.

Sumbu B — Kondisi Kesegaran
Dari sisa waktu ke deadline. Menentukan tanamannya sedang apa — berakar tenang, mulai haus, perlu disiram, atau layu. Tier tinggi mulai haus lebih awal dan minta perhatian lebih sering.

Efeknya: tagihan listrik jadi Perdu yang layu (kecil tapi minta tolong sekarang), belajar Jepang jadi Pohon Purba yang masih berakar tenang (besar tapi belum mendesak). Persis seperti hidup nyata.

Masalah 2 — Kesulitan yang menambah EXP mengundang kecurangan
Kalau EXP naik seiring skor kesulitan, dan skornya diisi sendiri oleh user, otak akan cari jalan pintas: “cuci piring itu kesulitan 90, kan capek.” Sistemnya rusak dalam dua minggu.

Solusi — tiga rem kecil
Kunci skor setelah task jalan. Kesulitan & dampak hanya bisa diedit sebelum sesi kerja pertama dimulai. Sesudah itu terkunci.
Dampak berbobot lebih besar dari kesulitan. Dampak ×1.0, kesulitan ×0.7 di rumus EXP. Yang dihargai hasilnya, bukan penderitaannya.
Konfrontasi lembut, bukan hukuman. Kalau estimasi 3 jam tapi selesai 10 menit, app tanya sekali: “Rating kesulitannya masih pas?” Tidak ada penalti, cuma cermin.
03 — Fondasi
Enam Gains
Kamu belum menyebut keenamnya, jadi ini usulanku: satu kata bahasa Indonesia, tidak tumpang tindih, dan cukup luas untuk menampung task apa pun tanpa bikin user bingung memilih.

Raga
Fisik & kesehatan
Olahraga, tidur, makan, check-up, berhenti dari kebiasaan buruk.

Nalar
Ilmu & mental
Belajar, membaca, kursus, riset, terapi, journaling.

Karya
Karier & keahlian
Pekerjaan, proyek, portofolio, deliverable klien, latihan skill.

Harta
Finansial
Tagihan, tabungan, investasi, budgeting, pemasukan tambahan.

Ikatan
Relasi & keluarga
Waktu bersama keluarga, kabar ke teman, networking, komunitas.

Jiwa
Spiritual & makna
Ibadah, refleksi, berbagi, hobi yang mengisi, istirahat yang disengaja.

Gains tambahan — opsional, manual
Keenam Gains di atas tetap fondasi yang tidak berubah — radar chart di beranda selalu menampilkan keenamnya duluan, dan tidak ada rencana menggantinya jadi metafora lain (mis. “6 petak kebun”). Tapi kalau hidupmu punya satu aspek spesifik yang tidak pas dipetakan ke salah satu dari enam itu — “Bisnis Sampingan” yang terasa beda dari Karya, atau “Parenting” yang terasa lebih spesifik dari Ikatan — kamu bisa menambah Gain sendiri lewat tombol “Tambah Gain” di layar Detail Gains: nama bebas, pilih warna & ikon seperti Gain bawaan. Gain buatan sendiri ini ikut rumus EXP dan pengali keseimbangan yang sama persis di bagian 07 — bukan mekanik baru, cuma entri baru di tabel. Dibatasi maksimal 4 Gain tambahan supaya radar chart tidak jadi terlalu ramai untuk dibaca sekali pandang.

Satu task boleh mengisi beberapa Gains
“Lari pagi bareng adik” itu Raga dan Ikatan. User mengalokasikan persentase ke maksimal 3 Gains (bawaan maupun buatan sendiri) lewat slider — default 100% ke satu Gain supaya input tetap cepat, alokasi ke banyak Gains disembunyikan di balik tombol “Bagi ke Gains lain”.

04 — Input
Dua slider, satu skor
Kesulitan · 0–100
Seberapa berat buat kamu
Bukan seberapa lama, tapi seberapa besar hambatan mental & fisiknya. Beri anchor di UI: 20 otomatis · 50 perlu fokus · 80 bikin ditunda-tunda · 100 menakutkan.

Dampak · 0–100
Seberapa besar pengaruhnya ke hidup
Anchor: 20 hilang tak terasa · 50 terasa minggu ini · 80 terasa tahun ini · 100 mengubah arah hidup.

Preview langsung saat menggeser
Ini momen paling menyenangkan di seluruh app dan pantas dikerjakan paling serius: saat kedua slider digeser, tanamannya tumbuh secara real-time di atas slider. Geser dampak dari 40 ke 70, tunas kecil membesar jadi Semak berdaun rimbun di depan mata. Orang akan main-mainkan slider ini cuma buat lihat animasinya — dan itu bagus, karena artinya mereka benar-benar memikirkan bobot task-nya.

// Skala Tumbuh dihitung real-time, tanpa pembulatan yang bikin bingung
skor  = kesulitan + dampak          // 0 .. 200
tier  = clamp(1, 10, floor(skor / 20) + 1)
05 — Jawaban untuk pertanyaan namamu
Sepuluh Skala Tumbuh
Ini rekomendasi utamaku, sudah divalidasi lewat prototipe interaktif dan kamu setujui: nama-nama pendek yang selaras dengan gagasan kebun, mudah dibayangkan wujudnya, dan jelas berjenjang tanpa perlu dijelaskan — daun kecil jelas lebih muda dari kanopi rimbun, tidak butuh keterangan tambahan.

Tier	Poin	Nama	Wujud tanaman	Perilaku saat menunggak
1	0–19	
Tunas
Sebutir biji yang baru pecah, dua helai daun mungil menyembul dari tanah	Diam saja. Tidak pernah kirim notifikasi.
2	20–39	
Kecambah
Batang tipis setinggi jari, tiga-empat daun kecil	Satu notifikasi lembut di hari-H, itu pun bisa dimatikan.
3	40–59	
Perdu
Semak kecil rimbun setinggi lutut	Mulai tampak layu di sudut kartu saat lewat deadline.
4	60–79	
Semak
Rimbun setinggi pinggang, mulai berbunga	Notifikasi H-1 dan hari-H. Daunnya kelihatan menunduk di daftar task.
5	80–99	
Pohon Muda
Batang mulai mengeras, kanopi kecil terbentuk	Tetes air mengetuk “kaca” layar sekali saat terlewat.
6	100–119	
Pohon Rimbun
Kanopi penuh dan teduh, akar mulai kokoh	Muncul di kebun utama beranda tiap kali app dibuka. Tidak pergi sampai disiram.
7	120–139	
Pohon Tua
Batang tebal berlumut, akar mulai terlihat di permukaan	Kalau Terjadwal: alarm layar penuh (vignette gelap tegas) tepat di jam mulai. Kalau bukan: heads-up prioritas tinggi. Mengunci widget sampai ditanggapi.
8	140–159	
Beringin
Terlalu besar untuk muat di layar — hanya akar gantung & dedaunan bawah yang kelihatan	Layar utama bergetar halus, seperti tertiup angin kencang. Mulai mengurangi EXP Gains terkait.
9	160–179	
Hutan Kecil
Kanopi rimbun jadi latar, satu pohon satelit mulai tumbuh di sampingnya	Menurunkan saturasi seluruh tema app selama masih menunggak — kekeringan yang menjalar.
10	180–200	
Pohon Purba
Siluet raksasa jadi latar belakang layar utama, bukan lagi ikon, dikelilingi pohon-pohon satelit	Mengambil alih layar utama. Task lain diredupkan sampai dia dihadapi (disiram).
Alternatif A — Nusantara
Kalau mau identitas lokal lebih kental dari nama pohon umum
Semai → Pandan → Bambu Muda → Puspa → Meranti Muda → Meranti Rimbun → Jati Tua → Beringin Kampung → Rimba Kecil → Rimba Purba

Hangat dan langsung akrab buat pengguna Indonesia yang besar mengenal nama-nama ini. Risikonya: sebagian nama spesifik daerah (meranti, puspa) kurang dikenal pengguna kota besar, dan lebih sulit diterjemahkan ke Inggris tanpa kehilangan rasa lokalnya.

Alternatif B — Musim
Kalau mau fokus ke tahap pertumbuhan, bukan spesies tertentu
Semai → Tunas → Berkuncup → Berbunga → Berbuah Muda → Berbuah Lebat → Rimbun → Menua → Melegenda → Abadi

Tidak terikat identitas flora tertentu, jadi lebih netral secara budaya. Tapi jenjangnya terasa lebih abstrak dan sedikit lebih sulit dibayangkan wujud visualnya dibanding nama pohon yang konkret.

Rekomendasi
Pakai set utama (Tunas → Pohon Purba) — ini yang sudah kamu setujui lewat prototipe. Simpan nama Nusantara dan set Musim sebagai skin pack di kemudian hari: ganti nama & warna, tier dan angkanya sama persis.

06 — Mekanik inti
Siklus hidup tanaman
Setiap task menanam tepat satu tanaman saat dibuat. Tier-nya menetap; kondisinya berubah mengikuti waktu.

STATE 1
Berakar — Tenang
Lebih dari 3 hari sebelum jatuh tempo, atau task tanpa jadwal. Tertanam diam di kartu task, akarnya kokoh. Nol notifikasi.

STATE 2
Mulai Haus — Menunduk pelan
H-3 sampai H-1. Daun mulai menunduk pelan. Tier 7+ dapat satu notifikasi tenang.

STATE 3
Perlu Disiram — Berdiri tegak
Hari-H. Animasi idle penuh. Kartu task naik ke atas daftar dengan sendirinya.

STATE 4
Layu — Keluar dari kartu
Lewat deadline. Keluar dari kartu, pindah ke kebun utama beranda. Perilakunya sesuai tabel tier di atas.

STATE 5
Mengering — Kekeringan menjalar
Telat lebih dari 3 hari. Mulai menggerogoti EXP Gains terkait, −1%/hari, maksimal −15% dari task itu. Bisa dimatikan lewat setelan.

Jatah gangguan harian
Ini yang akan menentukan app-mu dipakai atau di-uninstall di minggu kedua. Aturannya:

Maksimal 6 notifikasi kebun per hari, apa pun jumlah task menunggak.
Kalau jatahnya penuh, yang lolos adalah tier tertinggi; sisanya digabung jadi satu ringkasan: “4 tanaman lain sedang menunggu disiram.”
Jam tenang yang bisa diatur. Hanya tier 9–10 boleh menembusnya, dan hanya kalau user mengizinkan secara eksplisit.
Alarm layar penuh (USE_FULL_SCREEN_INTENT) cuma untuk task Terjadwal pada jam mulainya persis — ini pemakaian yang sah di mata Play Store karena setara alarm/pengingat waktu-tepat. Keputusan final, bukan lagi trade-off terbuka: supaya tetap terasa darurat meski wujudnya sekarang tanaman (bukan monster yang mengaung), layar alarm ini selalu memakai vignette gelap yang tegas di atas ilustrasi tanaman, tidak cuma mengandalkan animasi layu yang lembut — estetika kebun tidak boleh melunakkan alarm task yang sudah genting. Task tier 7+ yang cuma terlewat (state Layu/Mengering tanpa jam pasti) tetap pakai notifikasi heads-up prioritas tinggi, bukan layar penuh. Google Play sejak Android 14 membatasi ketat siapa yang boleh pakai layar penuh dan menanyakan alasannya lewat Formulir Deklarasi Izin di Play Console — kalau dipakai sembarangan untuk “task telat” biasa, risikonya app ditolak saat review.
Memanen tanaman
Centang task → animasi panen pendek (600–900 ms, bisa dilewati — kuncup mekar sekejap lalu dipetik) → EXP terbang ke ikon Gains yang bersangkutan → bar Gains terisi.

Tanaman yang dipanen masuk Almanak Kebun. Tiap spesies punya penghitung: “Pohon Purba — dipanen 3×”. Ini yang mengubah riwayat task dari daftar membosankan jadi sesuatu yang ingin dilihat lagi.

Membatalkan task juga sah — tanamannya “dilepas ke alam”, bukan dipanen. Tidak ada EXP, tidak ada penalti. Menghukum orang karena membatalkan hal yang memang sudah tidak relevan itu keliru.

07 — Ekonomi
Rumus EXP dan level Gains
Semua angka di bawah ini sengaja dibuat mudah diubah, dan tidak berubah sama sekali dari draf monster — pergantian ke tanaman murni soal wujud, bukan angka. Taruh di satu file konstanta, jangan disebar ke mana-mana — kamu akan menyetelnya berkali-kali setelah dipakai sendiri seminggu.

// ---- 1. EXP dasar (dampak lebih berharga dari penderitaan)
base = (dampak * 1.0) + (kesulitan * 0.7)        // 0 .. 170

// ---- 2. Pengali ketepatan waktu
selesai lebih awal  -> 1.15
tepat waktu         -> 1.00
telat               -> max(0.40, 1 - 0.05 * hariTelat)
tanpa deadline      -> 1.00

// ---- 3. Pengali keseimbangan   <- pembeda utama app ini
// Gains dengan level terendah memberi bonus, supaya user
// terdorong ke aspek hidup yang sedang dia telantarkan
gainTerendah -> 1.15      gainLainnya -> 1.00

// ---- 4. Pengali runtutan (streak)
streak = 1 + min(0.20, 0.02 * hariBeruntun)

// ---- 5. Hasil akhir, dibagi ke Gains sesuai alokasi
total      = round(base * waktu * seimbang * streak)
expPerGain = round(total * alokasiPersen[gain])
Kurva level tiap Gains
// EXP kumulatif untuk mencapai level n
expKumulatif(n) = round(100 * n^1.6)

// lv 2  ->    303       lv 10 ->  3.981
// lv 5  ->  1.379       lv 20 -> 12.126
Eksponen 1.6 memberi level-level awal yang cepat memuaskan tanpa membuat level 20 terasa mustahil. Naikkan kalau kamu ingin perjalanan yang lebih panjang.

Kenapa pengali keseimbangan itu inti produknya
Tanpa itu, orang akan menumpuk EXP di Karya sampai level 30 sementara Raga tertinggal di level 2 — persis kebiasaan buruk yang seharusnya app ini bantu perbaiki.

Dengan itu, layar utama bisa berkata: “Ikatan tertinggal 4 level. Task apa pun di sana dapat +15% minggu ini.” Ini satu-satunya fitur yang tidak dipunyai task manager lain, dan pantas jadi bahan promosi utama.

Tampilkan Gains sebagai radar chart di layar utama — enam bawaan dulu, lalu Gain tambahan buatan sendiri (bagian 03) menyusul di sisi yang sama. Bentuk yang penyok langsung memberi tahu segalanya tanpa satu kata pun.

08 — Waktu
Tiga jenis task, satu form
Jenis	Input waktu	Pengingat	Kondisi tanaman
Lentur	Hanya estimasi durasi (menit). Tanpa tanggal.	Tidak ada, kecuali dijadwalkan belakangan.	Berakar terus sampai user memberinya tanggal.
Bertenggat	Estimasi durasi + tanggal jatuh tempo.	Notifikasi biasa, mengikuti jatah harian.	Siklus penuh: Berakar → Mulai Haus → Perlu Disiram → Layu.
Terjadwal	Tanggal + jam mulai, durasi, opsi pengulangan.	Alarm tepat waktu. Layar penuh untuk tier 7+.	Perlu disiram tepat pada jam mulai, bukan tengah malam.
Sesi fokus
Tombol Mulai di detail task menjalankan timer di notifikasi permanen. Selama timer jalan, tanamannya masuk “rumah kaca” — animasinya berubah jadi terlindung di bawah kubah kaca kecil, tenang tumbuh tanpa gangguan. Umpan balik visual yang gratis tapi terasa mahal.

Simpan estimasi dan waktu aktual. Setelah 20–30 task, app bisa berkata: “Kamu rata-rata butuh 1,8× lebih lama dari perkiraanmu.” Itu insight yang benar-benar berguna, dan datanya sudah ada tanpa usaha tambahan.

Yang harus diurus di sisi Android
SCHEDULE_EXACT_ALARM — sejak Android 12 harus diminta lewat layar setelan, bukan dialog izin biasa. Siapkan onboarding khusus.
POST_NOTIFICATIONS — wajib diminta di Android 13+.
USE_FULL_SCREEN_INTENT — dibatasi ketat di Android 14+, secara resmi ditujukan untuk app alarm & panggilan. Deklarasikan di Play Console dengan alasan “pengingat jadwal bertenggat waktu (alarm)” dan batasi pemakaiannya hanya untuk task Terjadwal — jangan untuk task yang sekadar telat, lihat bagian 06.
RECEIVE_BOOT_COMPLETED — alarm hilang setelah HP restart kalau ini tidak dipasang.
Pembatasan baterai OEM — Xiaomi, Oppo, dan Vivo membunuh alarm secara agresif. Sediakan tombol “Alarm saya tidak bunyi” yang mengarahkan ke setelan autostart. Ini sumber ulasan bintang satu nomor satu untuk app pengingat.
09 — Antarmuka
Sembilan layar
00
Masuk / Daftar
Tombol Google besar di atas, opsi email di bawahnya. Tidak memaksa login di percobaan pertama — lihat bagian 13.

01
Beranda
Radar chart Gains (enam bawaan, plus tambahan manual kalau ada), level & EXP, task hari ini, dan tanaman yang layu menunggu disiram di kebun kosong.

02
Daftar task
Saring per Gains, tier, atau tanggal. Diurutkan otomatis oleh Prioritas Otomatis (bagian 12): yang paling layu di atas, yang masih berakar tenang di bawah.

03
Buat task
Dua slider dengan preview tanaman tumbuh hidup, pemilih Gains, jenis waktu. Layar terpenting di app.

04
Detail task
Tanaman berukuran besar dengan bar Pertumbuhan dari checklist subtask, timer fokus, catatan, riwayat penjadwalan ulang.

05
Detail Gains
Kurva level, EXP masuk 30 hari terakhir, task yang paling banyak menyumbang. Termasuk Gain tambahan buatan sendiri.

06
Almanak Kebun
Sepuluh spesies tanaman, berapa kali dipanen, rekor terbaik. Alasan untuk kembali membuka app.

07
Statistik
Akurasi estimasi, tingkat penyelesaian per tier, kalender streak, keseimbangan Gains dari waktu ke waktu.

08
Setelan
Jam tenang, jatah notifikasi, saklar EXP menyusut, tema, bahasa, kelola akun & hapus akun, ekspor data.

10 — Struktur task
Task besar, langkah kecil
Task boleh dipecah jadi checklist di dalam dirinya sendiri — bukan jadi task baru yang terpisah.

Bagaimana subtask bekerja
Task manapun boleh punya daftar subtask sederhana: judul + centang, tersusun dan bisa diurut ulang dengan seret.
Tanaman di layar detail task tampil dengan bar Pertumbuhan di bawahnya. Tiap subtask dicentang, bar-nya bertambah — centang subtask terakhir, tanamannya siap dipanen.
EXP tetap dibayar satu kali, saat task induk ditandai selesai — bukan per subtask. Kalau semua subtask sudah tercentang, app menyodorkan satu tombol: “Semua langkah selesai — tandai task ini selesai?”
Yang sengaja tidak dibuat
Subtask tidak punya kesulitan, dampak, deadline, atau Gains sendiri. Begitu subtask boleh dijadwalkan dan diberi skor sendiri, dia jadi task penuh dengan tanaman sendiri — dan satu “task besar” akan meledak jadi rombongan tanaman kecil yang membingungkan, bukan satu tanaman yang perlahan tumbuh besar. Satu task, satu tanaman, tetap berlaku.

11 — Lokalisasi
Dua bahasa, satu pengalaman
Bahasa Indonesia dan English. Default-nya ikut setelan bahasa HP — tapi pemilihnya tidak dikubur di Setelan. Begitu user pertama kali masuk, pemilih bahasa nongol di layar utama supaya orang yang HP-nya kebetulan English tapi mikirnya Indonesia (atau sebaliknya) bisa langsung membetulkannya tanpa mencari-cari.

Elemen	Indonesia	English	Diterjemahkan?
UI, label, notifikasi	“Tandai selesai”	“Mark as done”	Ya — via strings.xml / values-en
Nama Gains	Raga, Nalar, Karya…	Body, Mind, Craft…	Ya — makna harus tersampaikan
Nama tier tanaman	Tunas, Semak, Pohon Purba	Sprout, Bush, Ancient Tree	Ya — beda dari nama monster lama (Speck, Goblin, Titan sengaja tidak diterjemahkan karena nama diri fiksi, seperti nama Pokémon). Tunas dan Pohon Purba adalah kata benda umum yang bermakna — dibiarkan tidak diterjemahkan di UI Inggris, orangnya cuma melihat kata asing tanpa arti, dan itu merusak inti nilai jual metafora ini: jenjang yang terbaca sendiri tanpa penjelasan.
Tanggal & angka	29 Agu 2026	Aug 29, 2026	Ya — via Locale, bukan concat string manual
Implementasi
Pakai AppCompatDelegate.setApplicationLocales() (per-app language bawaan Android 13+, di-backport AndroidX Core ke versi lebih lama). Nilai awalnya dibaca dari Locale sistem saat pertama buka — lalu sebuah chip kecil “ID / EN” muncul di pojok Beranda tepat setelah login pertama, sekali ganti langsung tersimpan ke DataStore dan tidak ditanyakan lagi. Jangan lupa: listing Play Store-nya sendiri juga perlu ditulis dua kali di Play Console — judul, deskripsi, dan screenshot untuk lokal id dan en-US terpisah.

12 — Prioritas otomatis
Yang paling mendesak, paling atas
Daftar task tidak diurutkan manual oleh user. Urutannya konsekuensi langsung dari sistem dua-sumbu di bagian 02: kondisi kesegaran menang atas tier, karena task yang sedang layu lebih genting daripada task berat yang masih berakar tenang.

// Setiap task diberi satu angka, daftar diurutkan menurun
bobotState = { mengering: 5, layu: 4, perluDisiram: 3, mulaiHaus: 2, berakar: 1 }

prioritas = bobotState[state] * 1000 + tier * 10
// seri — task dengan prioritas sama diurutkan oleh:
//   tenggat lebih dekat dulu, tanpa tenggat taruh paling akhir
Task	Tier	Kondisi	Prioritas	Posisi
Bayar tagihan listrik	2	Layu	4020	1
Presentasi klien besok	7	Perlu Disiram	3070	2
Servis motor	4	Mulai Haus	2040	3
Belajar bahasa Jepang	10	Berakar	1100	4
Pohon Purba-nya belajar Jepang tetap di posisi buncit selama dia masih berakar tenang — persis alasan kenapa bagian 02 memisahkan dua sumbu ini sejak awal. Tidak ada opsi “pin manual” di v1 secara sengaja: begitu user bisa menimpa urutan ini dengan tangan, dia akan menimpanya dengan bias yang sama seperti to-do list biasa, dan seluruh nilai dari fitur ini hilang.

13 — Akun & masuk
Masuk dengan Google, atau email + OTP
Ini perubahan arsitektur paling besar dari draf pertama: v1 tadinya murni offline (lihat bagian 10), sekarang jadi offline-first dengan sinkron akun — karena tujuan login biasanya memang supaya progres Gains ikut pindah kalau ganti HP.

Jalur 1
Google Sign-In
Satu tombol lewat Credential Manager API (API resmi terbaru Google, bukan GoogleSignInClient yang sudah deprecated), diteruskan ke Firebase Auth. Tidak perlu password, tidak perlu OTP — kepercayaannya sudah diwariskan dari akun Google.

Jalur 2
Email + kata sandi + OTP
Firebase Auth bawaan memverifikasi email lewat link, bukan kode — supaya benar-benar OTP 6 digit seperti yang kamu mau, perlu Cloud Function sendiri.

Alur OTP email, langkah demi langkah
1. User isi email + kata sandi → akun Firebase dibuat dengan status belum terverifikasi.
2. Cloud Function membuat kode 6 digit, menyimpan versi ter-hash-nya di Firestore dengan masa berlaku 5 menit, lalu mengirim kodenya lewat penyedia email transaksional (mis. Resend atau SendGrid — Firebase tidak mengirim OTP angka secara bawaan).
3. User memasukkan kode di app. Cocok → akun ditandai terverifikasi dan langsung masuk. Maksimal 5 percobaan salah, kirim ulang dibatasi jeda 60 detik.
4. Alur lupa kata sandi memakai mekanisme OTP yang sama, bukan email link Firebase bawaan — supaya pengalamannya konsisten di seluruh app.
Konsekuensi yang perlu kamu sadari
Karena Play Store mensyaratkan app dengan pembuatan akun untuk menyediakan jalur hapus akun di dalam app (bukan cuma lewat tiket support), sertakan tombol “Hapus Akun” di Setelan yang menghapus data Firestore sekaligus akun Firebase Auth-nya. Kamu juga wajib mengisi formulir Data Safety dan mencantumkan URL Kebijakan Privasi di Play Console sebelum submit — keduanya baru relevan begitu ada login, jadi belum perlu dipikirkan kalau v1 tetap offline murni.

Terkonfirmasi — login wajib
Layar 00 (Masuk/Daftar) jadi gerbang pertama, bukan opsional. Konsekuensinya dua: pertama, EXP dan progres Gains langsung tersimpan sejak task pertama — tidak ada data “tamu” yang perlu dipindah belakangan, model data jadi lebih sederhana. Kedua, momen pertama-buka-app jadi lebih berat (isi email, tunggu OTP, baru sampai ke slider pertama) — jadi layar 00 harus dirancang secepat mungkin: Google Sign-In satu ketuk sebagai opsi paling atas, dan form email diringkas ke dua langkah saja (isi → kode OTP) tanpa langkah tambahan di antaranya.

14 — Teknis
Tumpukan teknologi & model data
Tumpukan
Kotlin + Jetpack Compose — Material 3, satu modul dulu.
Room + Flow — cache lokal, offline-first. Setelah login, Firestore jadi sumber kebenaran; Room tetap dibaca UI supaya app tetap kilat tanpa internet.
Firebase Auth + Credential Manager API — Google Sign-In lewat Credential Manager (bukan GoogleSignInClient lama yang sudah deprecated), plus email/password.
Cloud Functions + Firestore — generate & verifikasi kode OTP email, dan jadi tempat sinkron Task/Gain/ExpLedger lintas perangkat.
Hilt untuk dependency injection.
AlarmManager untuk alarm tepat waktu, WorkManager untuk pemeriksaan kondisi tanaman harian dan sinkron latar belakang ke Firestore.
DataStore untuk preferensi, termasuk pilihan bahasa.
Tanaman digambar penuh lewat kode, bukan lewat AI. Ini penyederhanaan nyata dibanding rencana monster: prototipe konsep sudah membuktikan teknik cluster-kanopi (blob bayangan di belakang, blob isi warna utama, blob highlight kecil di atas, semuanya lingkaran/elips sederhana dengan jitter berbibit-acak, plus batang meruncing dua-nada untuk efek silinder) menghasilkan 10 tier + 5 kondisi kesegaran yang konsisten satu sama lain, tanpa satu pun aset gambar dari luar. Lottie tetap dipakai, tapi hanya untuk efek kecil yang murni gerak abstrak (partikel, kilau, asap) yang tidak butuh gambar sumber.
MVVM dengan pemisahan data / domain / ui. Rumus EXP dan tier hidup di domain — murni, tanpa dependensi Android, dan mudah dites.
Entitas Room
Gain(id, ownerId, nama, warna, ikon,
     totalExp, level, bawaan)

Task(id, ownerId, judul, catatan,
     kesulitan, dampak, tier,
     jenisWaktu, jatuhTempo, jamMulai,
     estimasiMenit, aktualMenit,
     aturanUlang, status,
     dibuatPada, selesaiPada,
     skorTerkunci, diperbaruiPada)

Subtask(id, taskId, judul,
        selesai, urutan)

TaskGain(taskId, gainId, persen)

Tanaman(taskId, spesies, kondisi,
        terakhirDiingatkan,
        jumlahPengingat)

ExpLedger(id, ownerId, gainId, taskId,
          jumlah, alasan, waktu)
ExpLedger itu wajib, bukan opsional. Jangan hanya menyimpan total EXP di tabel Gain — simpan setiap transaksi. Tanpa itu kamu tidak bisa membatalkan penyelesaian task, membangun grafik, atau memperbaiki bug perhitungan tanpa merusak data orang.

ownerId baru muncul karena ada login (lihat bagian 13). diperbaruiPada dipakai untuk sinkronisasi last-write-wins ke Firestore — cukup untuk kasus satu akun dipakai di satu HP pada satu waktu, tidak perlu resolusi konflik yang rumit. Kolom bawaan di Gain membedakan enam Gain fondasi dari Gain tambahan buatan user (bagian 03) — dipakai UI untuk selalu menaruh yang bawaan lebih dulu di radar chart.

Render prosedural, langkah demi langkah
Teknik yang sudah divalidasi di prototipe konsep, tinggal dipindah ke Compose Canvas (drawCircle/drawPath, bukan lagi SVG):

Kanopi = cluster blob. Satu elips bayangan gelap di belakang untuk kedalaman, beberapa lingkaran isi warna utama saling tumpang-tindih membentuk siluet bulat, dua lingkaran kecil lebih terang di atas untuk kesan cahaya. Jumlah blob & radius kanopi naik mengikuti tier (dari 3 blob kecil di Kecambah sampai 12 blob di Pohon Purba).
Batang = trapesium dua-nada. Separuh kiri lebih gelap, separuh kanan lebih terang, meruncing dari lebar di pangkal ke sempit di kanopi — kesan silinder tanpa perlu gradient.
Kesegaran = interpolasi warna + posisi. Warna kanopi bergeser dari warna Gain menuju cokelat kering sesuai kondisi; blob yang lebih jauh dari tengah kanopi “menunduk” lebih jauh saat kondisinya Layu/Mengering.
Tier 8+ dapat pohon satelit kecil di sampingnya, dirender lewat fungsi kanopi yang sama dengan skala lebih kecil — bukan aset terpisah.
1 ilustrasi statis per tier (opsional, untuk listing Play Store & marketing) tetap bisa digenerate AI belakangan sebagai referensi, tapi app runtime tidak bergantung padanya sama sekali.
15 — Urutan pengerjaan
Roadmap
Urutannya penting: setiap fase harus menghasilkan sesuatu yang benar-benar bisa kamu pakai sendiri sebelum lanjut ke fase berikutnya.

Fase 1
Rangka
CRUD task + subtask, dua slider, perhitungan tier & prioritas otomatis, enam Gains (plus dukungan skema untuk Gain tambahan), EXP dan level, database Room dengan ownerId anonim dari Firebase Auth sejak awal — supaya tidak perlu migrasi skema besar-besaran nanti di Fase 2. Tanamannya masih placeholder statis — emoji pun cukup. Tujuannya membuktikan angka-angkanya terasa benar sebelum sepeser pun dihabiskan untuk render.

Fase 2
Waktu & Akun
Tenggat, task terjadwal, AlarmManager, notifikasi, jatah gangguan harian, jam tenang, timer fokus — digabung dengan Google Sign-In, email + OTP, dan sinkron Firestore, karena keduanya sama-sama menyentuh siklus hidup task. Uji notifikasi & alarm di HP Xiaomi atau Oppo sungguhan sedini mungkin — emulator berbohong soal ini.

Fase 3
Nyawa
Render prosedural cluster-kanopi di Compose Canvas untuk 10 tier × 5 kondisi kesegaran (teknik sudah divalidasi lewat prototipe konsep — bukan lagi 10 ilustrasi AI yang perlu dijaga konsistensi gayanya), animasi goyang tertiup angin, layu bertahap, daun berguguran saat Layu/Mengering, bar Pertumbuhan subtask, preview real-time di slider, animasi panen, kebun tanaman menunggu di beranda. Fase ini jadi jauh lebih murah dan lebih cepat dibanding rencana monster berbasis AI — tidak ada lagi risiko sepuluh generasi AI terasa beda gaya satu sama lain.

Fase 4
Kedalaman
Almanak Kebun, statistik, akurasi estimasi, streak, pengali keseimbangan, radar chart (termasuk Gain tambahan), task berulang, terjemahan English lengkap (termasuk nama tier tanaman, lihat bagian 11), hapus akun, ekspor data.

Fase 5
Rilis
Kebijakan Privasi, formulir Data Safety, verifikasi layar consent OAuth Google, screenshot & listing dwibahasa (id / en-US) di Play Console, uji tertutup (closed testing) sebelum rilis produksi.

Nanti
Setelah dipakai sebulan
Widget layar utama, integrasi kalender, skin pack Nusantara atau Musim, kolaborasi. Jangan sentuh apa pun di sini sampai kamu benar-benar memakai app-nya sendiri selama empat minggu penuh.

16 — Spesifikasi terkunci
Sebelas keputusan, sudah dikonfirmasi
Semua pertanyaan di draf sebelumnya sudah kamu jawab. Ini rekapnya, supaya jadi satu referensi tunggal saat mulai membangun — tidak perlu bolak-balik ke pesan-pesan sebelumnya.

01
Enam Gains — final. Raga, Nalar, Karya, Harta, Ikatan, Jiwa. Lihat bagian 03.

02
Nama tier — final. Tunas → Pohon Purba dipakai sebagai set utama untuk tanaman, menggantikan set monster (Speck → Titan) dari draf sebelumnya. Set Nusantara (Semai→Rimba Purba) dan set Musim disimpan sebagai skin pack untuk dirilis belakangan, bukan dihapus — struktur datanya (tier 1–10) tetap sama, cuma nama & warnanya yang diganti. Lihat bagian 05.

03
EXP menyusut — dipakai sesuai default. −1% EXP/hari saat task masuk kondisi Mengering, dibatasi maksimal −15% dari task itu, dan bisa dimatikan lewat Setelan. Lihat bagian 06.

04
Agresivitas notifikasi — ditentukan mengikuti kebijakan Play Store. Maksimal 6 notifikasi/hari, jam tenang bisa diatur, dan alarm layar penuh dibatasi hanya untuk task Terjadwal pada jam mulainya — bukan untuk task yang sekadar terlambat — karena Android 14+ mensyaratkan alasan alarm/waktu-tepat untuk izin ini. Lihat bagian 06 & 08.

05
Seni tanaman — digambar penuh lewat kode. Bukan lagi ilustrasi AI statis seperti rencana monster di draf sebelumnya: render prosedural cluster-kanopi di Compose Canvas, sudah divalidasi lewat prototipe konsep, mencakup 10 tier × 5 kondisi kesegaran tanpa aset gambar dari luar. Detail teknik ada di bagian 14.

06
Login — wajib. Tidak ada mode tamu; layar Masuk/Daftar jadi gerbang pertama sejak buka app pertama kali. Lihat bagian 13.

07
OTP — lewat email saja, bukan SMS. Dikirim via Cloud Function + penyedia email transaksional, bukan fitur SMS Firebase Phone Auth yang berbayar per pesan. Lihat bagian 13.

08
Bahasa — ikut setelan HP, dengan pemilih di layar utama. Default dibaca dari Locale sistem saat instal pertama; chip “ID / EN” muncul di Beranda tepat setelah login pertama supaya gampang dikoreksi tanpa masuk Setelan. Beda dari nama tier monster lama, nama tier tanaman sekarang ikut diterjemahkan karena maknanya kata benda umum, bukan nama diri fiksi. Lihat bagian 11.

09
Metafora inti — final. Monster diganti tanaman/kebun supaya penundaan terasa seperti rasa sayang dan tanggung jawab, bukan dikejar rasa takut. Mekanik dua sumbu dan seluruh rumus di bagian 07 & 12 tidak berubah sama sekali dari draf sebelumnya — yang direka ulang cuma istilah di bagian 05 (nama tier), 06 (siklus hidup & notifikasi), dan 14 (cara render visual).

10
Gains tambahan manual — final. Enam Gains dasar tidak berubah dan selalu tampil lebih dulu. Di atasnya, user bisa menambah Gain sendiri (maksimal 4), memakai rumus EXP yang sama. Lihat bagian 03.

11
Alarm mendesak tetap tegas — final. Alarm layar penuh untuk task Terjadwal tier 7+ tetap memakai vignette gelap yang tegas di atas ilustrasi tanaman, tidak dilunakkan oleh estetika kebun yang lembut. Lihat bagian 06.
