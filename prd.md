Konsep Produk
Android · Kotlin
Draft v0.1
Impact Task
Task manager di mana setiap pekerjaan terhubung ke enam aspek hidupmu, punya bobot dampak yang kamu tentukan sendiri, dan melahirkan monster yang makin ganas kalau ditunda.

01 — Premis
Kenapa ini bukan to-do list biasa
Task punya konsekuensi
To-do list biasa memperlakukan “beli kopi” dan “kirim proposal klien” sebagai baris yang setara. Di sini setiap task dinilai di dua sumbu, dan hasilnya kelihatan.

Progres yang terakumulasi
Task selesai tidak hilang — dia jadi EXP di salah satu dari enam Gains. Setelah sebulan kamu bisa lihat aspek hidup mana yang benar-benar kamu urus.

Penundaan yang terasa
Task yang dibiarkan tidak cuma jadi badge merah. Dia jadi makhluk yang bangun, keluar dari daftar, dan nongkrong di layar utamamu.

02 — Koreksi desain
Dua hal di ide awal yang perlu dibetulkan dulu
Ini bagian paling penting. Kalau dua ini tidak dibereskan sekarang, seluruh sistem skoring akan terasa aneh dipakai sehari-hari.

Masalah 1 — Urgensi tidak bisa datang dari 200 poin
Kamu bilang 10 level urgensi diambil dari total Kesulitan + Dampak. Tapi coba dua contoh ini:

Bayar tagihan listrik — kesulitan 5, dampak 30. Total 35, tier terendah. Padahal jatuh tempo besok dan kalau telat listrik mati.
Belajar bahasa Jepang — kesulitan 85, dampak 90. Total 175, tier tertinggi. Padahal tidak ada deadline sama sekali.
Skor 200 mengukur seberapa berat sebuah task, bukan seberapa mendesak. Keduanya beda sumbu.

Solusi — pisahkan jadi dua sumbu, monsternya tetap satu
Ide monsternya tidak berubah sama sekali. Yang berubah cuma ini: 200 poin menentukan ukuran & jenis monster, deadline menentukan tingkah lakunya.

Sumbu A — Threat Tier 1–10
Dari Kesulitan + Dampak (0–200). Menentukan monster apa yang lahir dari task ini, berapa EXP yang dia bawa, dan seberapa besar dia digambar.

Sumbu B — Urgency State
Dari sisa waktu ke deadline. Menentukan monsternya sedang apa — tidur, menggeliat, bangun, atau mengamuk. Tier tinggi bangun lebih awal dan berisik lebih sering.

Efeknya: tagihan listrik jadi Imp yang mengamuk (kecil tapi teriak-teriak), belajar Jepang jadi Titan yang tidur (raksasa tapi belum ganggu). Persis seperti hidup nyata.

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

Satu task boleh mengisi beberapa Gains
“Lari pagi bareng adik” itu Raga dan Ikatan. User mengalokasikan persentase ke maksimal 3 Gains lewat slider — default 100% ke satu Gain supaya input tetap cepat, alokasi ke banyak Gains disembunyikan di balik tombol “Bagi ke Gains lain”.

04 — Input
Dua slider, satu skor
Kesulitan · 0–100
Seberapa berat buat kamu
Bukan seberapa lama, tapi seberapa besar hambatan mental & fisiknya. Beri anchor di UI: 20 otomatis · 50 perlu fokus · 80 bikin ditunda-tunda · 100 menakutkan.

Dampak · 0–100
Seberapa besar pengaruhnya ke hidup
Anchor: 20 hilang tak terasa · 50 terasa minggu ini · 80 terasa tahun ini · 100 mengubah arah hidup.

Preview langsung saat menggeser
Ini momen paling menyenangkan di seluruh app dan pantas dikerjakan paling serius: saat kedua slider digeser, monsternya berubah wujud secara real-time di atas slider. Geser dampak dari 40 ke 70, si Goblin tumbuh jadi Brute di depan mata. Orang akan main-mainkan slider ini cuma buat lihat animasinya — dan itu bagus, karena artinya mereka benar-benar memikirkan bobot task-nya.

// Threat Tier dihitung real-time, tanpa pembulatan yang bikin bingung
skor  = kesulitan + dampak          // 0 .. 200
tier  = clamp(1, 10, floor(skor / 20) + 1)
05 — Jawaban untuk pertanyaan namamu
Sepuluh Threat Tier
Ini rekomendasi utamaku: nama-nama pendek yang selaras dengan nama app, mudah dijadikan karakter, dan jelas berjenjang tanpa perlu dijelaskan.

Tier	Poin	Nama	Wujud karakter	Perilaku saat menunggak
1	0–19	
Speck
Sebutir debu bermata satu, mengambang malas	Diam saja. Tidak pernah kirim notifikasi.
2	20–39	
Blob
Gumpalan lendir bulat yang memantul pelan	Satu notifikasi di hari-H, itu pun bisa dimatikan.
3	40–59	
Imp
Setan cebol bertanduk kecil, cengengesan	Mulai muncul di sudut layar utama saat lewat deadline.
4	60–79	
Goblin
Bertaring, bawa tongkat, jalan mondar-mandir	Notifikasi H-1 dan hari-H. Berjalan menyeberangi layar.
5	80–99	
Brute
Bertubuh besar, tangan berat, napas terlihat	Mengetuk “kaca” layar sekali saat terlewat.
6	100–119	
Stalker
Kurus, tinggi, wajah tersembunyi, gerak patah-patah	Muncul di ujung layar tiap kali app dibuka. Tidak pergi.
7	120–139	
Warden
Berzirah, memanggul rantai, berdiri diam mengawasi	Kalau Terjadwal: alarm layar penuh tepat di jam mulai. Kalau bukan: heads-up prioritas tinggi. Mengunci widget sampai ditanggapi.
8	140–159	
Behemoth
Terlalu besar untuk muat di layar — hanya kaki & mata yang kelihatan	Layar utama bergetar halus. Mulai mengurangi EXP Gains terkait.
9	160–179	
Wraith
Bayangan tembus pandang yang meredupkan warna di sekitarnya	Menurunkan saturasi seluruh tema app selama masih menunggak.
10	180–200	
Titan
Siluet raksasa jadi latar belakang layar utama, bukan lagi ikon	Mengambil alih layar utama. Task lain diredupkan sampai dia dihadapi.
Alternatif A — Nusantara
Kalau mau identitas lokal
Debu → Kunang → Cicak → Tikus → Kelelawar → Serigala → Buaya → Harimau → Naga → Kala

Hangat dan langsung akrab buat pengguna Indonesia. Risikonya: fauna nyata lebih sulit dibikin lucu-menakutkan dibanding monster fiksi, dan jenjangnya kurang terbaca di pasar global.

Alternatif B — Kosmik
Kalau mau nyambung ke nama app
Dust → Pebble → Shard → Stone → Boulder → Bolide → Meteor → Comet → Asteroid → Cataclysm

Permainan kata yang rapi — impact juga berarti tumbukan meteor. Tapi batu tidak punya mata, dan seluruh pilar emosional app ini bergantung pada karakter yang bisa menatapmu.

Rekomendasi
Pakai set utama (Speck → Titan). Alasannya satu dan menentukan: kesepuluhnya bisa digambar sebagai makhluk hidup dengan mata dan ekspresi, dan itulah satu-satunya alasan mekanik monster ini bekerja. Simpan nama Nusantara sebagai skin pack di kemudian hari — ganti nama & sprite, tier dan angkanya sama persis.

06 — Mekanik inti
Siklus hidup monster
Setiap task melahirkan tepat satu monster saat dibuat. Tier-nya menetap; state-nya berubah mengikuti waktu.

STATE 1
Dormant — Tidur
Lebih dari 3 hari sebelum jatuh tempo, atau task tanpa jadwal. Meringkuk tidur di kartu task. Nol notifikasi.

STATE 2
Stirring — Menggeliat
H-3 sampai H-1. Membuka satu mata, sesekali menguap. Tier 7+ dapat satu notifikasi tenang.

STATE 3
Awake — Bangun
Hari-H. Berdiri, animasi idle penuh. Kartu task naik ke atas daftar dengan sendirinya.

STATE 4
Rampage — Mengamuk
Lewat deadline. Keluar dari kartu, pindah ke layar utama. Perilakunya sesuai tabel tier di atas.

STATE 5
Feral — Liar
Telat lebih dari 3 hari. Mulai menggerogoti EXP Gains terkait, −1%/hari, maksimal −15% dari task itu. Bisa dimatikan lewat setelan.

Jatah gangguan harian
Ini yang akan menentukan app-mu dipakai atau di-uninstall di minggu kedua. Aturannya:

Maksimal 6 notifikasi monster per hari, apa pun jumlah task menunggak.
Kalau jatahnya penuh, yang lolos adalah tier tertinggi; sisanya digabung jadi satu ringkasan: “4 monster lain sedang menunggu.”
Jam tenang yang bisa diatur. Hanya tier 9–10 boleh menembusnya, dan hanya kalau user mengizinkan secara eksplisit.
Alarm layar penuh (USE_FULL_SCREEN_INTENT) cuma untuk task Terjadwal pada jam mulainya persis — ini pemakaian yang sah di mata Play Store karena setara alarm/pengingat waktu-tepat. Task tier 7+ yang cuma terlewat (state Mengamuk/Liar tanpa jam pasti) pakai notifikasi heads-up prioritas tinggi, bukan layar penuh. Google Play sejak Android 14 membatasi ketat siapa yang boleh pakai layar penuh dan menanyakan alasannya lewat Formulir Deklarasi Izin di Play Console — kalau dipakai sembarangan untuk “task telat” biasa, risikonya app ditolak saat review.
Mengalahkan monster
Centang task → animasi kekalahan pendek (600–900 ms, bisa dilewati) → EXP terbang ke ikon Gains yang bersangkutan → bar Gains terisi.

Monster yang kalah masuk Bestiary. Tiap spesies punya penghitung: “Titan — dikalahkan 3×”. Ini yang mengubah riwayat task dari daftar membosankan jadi sesuatu yang ingin dilihat lagi.

Membatalkan task juga sah — monsternya “dilepaskan”, bukan dikalahkan. Tidak ada EXP, tidak ada penalti. Menghukum orang karena membatalkan hal yang memang sudah tidak relevan itu keliru.

07 — Ekonomi
Rumus EXP dan level Gains
Semua angka di bawah ini sengaja dibuat mudah diubah. Taruh di satu file konstanta, jangan disebar ke mana-mana — kamu akan menyetelnya berkali-kali setelah dipakai sendiri seminggu.

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

Tampilkan keenam Gains sebagai radar chart di layar utama. Bentuk yang penyok langsung memberi tahu segalanya tanpa satu kata pun.

08 — Waktu
Tiga jenis task, satu form
Jenis	Input waktu	Pengingat	State monster
Lentur	Hanya estimasi durasi (menit). Tanpa tanggal.	Tidak ada, kecuali dijadwalkan belakangan.	Tidur terus sampai user memberinya tanggal.
Bertenggat	Estimasi durasi + tanggal jatuh tempo.	Notifikasi biasa, mengikuti jatah harian.	Siklus penuh: Tidur → Menggeliat → Bangun → Mengamuk.
Terjadwal	Tanggal + jam mulai, durasi, opsi pengulangan.	Alarm tepat waktu. Layar penuh untuk tier 7+.	Bangun tepat pada jam mulai, bukan tengah malam.
Sesi fokus
Tombol Mulai di detail task menjalankan timer di notifikasi permanen. Selama timer jalan, monsternya “terkurung” — animasinya berubah jadi terikat. Umpan balik visual yang gratis tapi terasa mahal.

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
Radar chart 6 Gains, level & EXP, task hari ini, dan monster yang sedang mengamuk berkeliaran di area kosong.

02
Daftar task
Saring per Gains, tier, atau tanggal. Diurutkan otomatis oleh Prioritas Otomatis (bagian 12): yang mengamuk di atas, yang tidur di bawah.

03
Buat task
Dua slider dengan preview monster hidup, pemilih Gains, jenis waktu. Layar terpenting di app.

04
Detail task
Monster berukuran besar dengan bar HP dari checklist subtask, timer fokus, catatan, riwayat penjadwalan ulang.

05
Detail Gains
Kurva level, EXP masuk 30 hari terakhir, task yang paling banyak menyumbang.

06
Bestiary
Sepuluh spesies, berapa kali dikalahkan, rekor terbaik. Alasan untuk kembali membuka app.

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
Monster di layar detail task tampil dengan bar HP di bawahnya. Tiap subtask dicentang, HP-nya berkurang — centang subtask terakhir, monsternya tinggal senggol.
EXP tetap dibayar satu kali, saat task induk ditandai selesai — bukan per subtask. Kalau semua subtask sudah tercentang, app menyodorkan satu tombol: “Semua langkah selesai — tandai task ini selesai?”
Yang sengaja tidak dibuat
Subtask tidak punya kesulitan, dampak, deadline, atau Gains sendiri. Begitu subtask boleh dijadwalkan dan diberi skor sendiri, dia jadi task penuh dengan monster sendiri — dan satu “task besar” akan meledak jadi rombongan monster kecil yang membingungkan, bukan satu monster yang perlahan melemah. Satu task, satu monster, tetap berlaku.

11 — Lokalisasi
Dua bahasa, satu pengalaman
Bahasa Indonesia dan English. Default-nya ikut setelan bahasa HP — tapi pemilihnya tidak dikubur di Setelan. Begitu user pertama kali masuk, pemilih bahasa nongol di layar utama supaya orang yang HP-nya kebetulan English tapi mikirnya Indonesia (atau sebaliknya) bisa langsung membetulkannya tanpa mencari-cari.

Elemen	Indonesia	English	Diterjemahkan?
UI, label, notifikasi	“Tandai selesai”	“Mark as done”	Ya — via strings.xml / values-en
Nama Gains	Raga, Nalar, Karya…	Body, Mind, Craft…	Ya — makna harus tersampaikan
Nama tier monster	Speck, Goblin, Titan	Speck, Goblin, Titan	Tidak — nama diri, seperti nama Pokémon
Tanggal & angka	29 Agu 2026	Aug 29, 2026	Ya — via Locale, bukan concat string manual
Implementasi
Pakai AppCompatDelegate.setApplicationLocales() (per-app language bawaan Android 13+, di-backport AndroidX Core ke versi lebih lama). Nilai awalnya dibaca dari Locale sistem saat pertama buka — lalu sebuah chip kecil “ID / EN” muncul di pojok Beranda tepat setelah login pertama, sekali ganti langsung tersimpan ke DataStore dan tidak ditanyakan lagi. Jangan lupa: listing Play Store-nya sendiri juga perlu ditulis dua kali di Play Console — judul, deskripsi, dan screenshot untuk lokal id dan en-US terpisah.

12 — Prioritas otomatis
Yang paling mendesak, paling atas
Daftar task tidak diurutkan manual oleh user. Urutannya konsekuensi langsung dari sistem dua-sumbu di bagian 02: state monster menang atas tier, karena task yang sedang mengamuk lebih genting daripada task berat yang masih tidur.

// Setiap task diberi satu angka, daftar diurutkan menurun
bobotState = { liar: 5, mengamuk: 4, bangun: 3, menggeliat: 2, tidur: 1 }

prioritas = bobotState[state] * 1000 + tier * 10
// seri — task dengan prioritas sama diurutkan oleh:
//   tenggat lebih dekat dulu, tanpa tenggat taruh paling akhir
Task	Tier	State	Prioritas	Posisi
Bayar tagihan listrik	2	Mengamuk	4020	1
Presentasi klien besok	7	Bangun	3070	2
Servis motor	4	Menggeliat	2040	3
Belajar bahasa Jepang	10	Tidur	1100	4
Titan-nya belajar Jepang tetap di posisi buncit selama dia masih tidur — persis alasan kenapa bagian 02 memisahkan dua sumbu ini sejak awal. Tidak ada opsi “pin manual” di v1 secara sengaja: begitu user bisa menimpa urutan ini dengan tangan, dia akan menimpanya dengan bias yang sama seperti to-do list biasa, dan seluruh nilai dari fitur ini hilang.

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
AlarmManager untuk alarm tepat waktu, WorkManager untuk pemeriksaan state monster harian dan sinkron latar belakang ke Firestore.
DataStore untuk preferensi, termasuk pilihan bahasa.
Ilustrasi statis (AI) + animasi prosedural Compose menggantikan rencana 50 Lottie penuh — lihat kartu di sebelah untuk alasannya. Lottie tetap dipakai, tapi hanya untuk efek kecil yang murni gerak abstrak (partikel, kilau, asap) yang tidak butuh gambar sumber.
MVVM dengan pemisahan data / domain / ui. Rumus EXP dan tier hidup di domain — murni, tanpa dependensi Android, dan mudah dites.
Entitas Room
Gain(id, ownerId, nama, warna, ikon,
     totalExp, level)

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

Monster(taskId, spesies, state,
        terakhirMengganggu,
        jumlahGangguan)

ExpLedger(id, ownerId, gainId, taskId,
          jumlah, alasan, waktu)
ExpLedger itu wajib, bukan opsional. Jangan hanya menyimpan total EXP di tabel Gain — simpan setiap transaksi. Tanpa itu kamu tidak bisa membatalkan penyelesaian task, membangun grafik, atau memperbaiki bug perhitungan tanpa merusak data orang.

ownerId baru muncul karena ada login (lihat bagian 13). diperbaruiPada dipakai untuk sinkronisasi last-write-wins ke Firestore — cukup untuk kasus satu akun dipakai di satu HP pada satu waktu, tidak perlu resolusi konflik yang rumit.

Soal art monster pakai AI
Bisa — tapi aku sendiri tidak bisa menggambarnya untukmu, alat yang kupunya di sini tidak termasuk penghasil gambar. Yang bisa kubantu: menulis prompt yang konsisten untuk 10 monster, dan menata ulang cara animasinya dibuat supaya cocok dengan apa yang sebenarnya dihasilkan alat AI gambar.

Generator gambar (Midjourney, SDXL, DALL·E, Ideogram) menghasilkan ilustrasi diam, bukan file Lottie berpenggerak. Jadi rencana “50 animasi Lottie” di draf pertama diganti jalur yang lebih realistis dikerjakan sendiri:

1 ilustrasi statis per tier (10 total, bukan 50) dengan latar transparan.
Kunci gaya di semua 10 generasi — pakai fitur style-reference (mis. --sref Midjourney) atau img2img dari satu sketsa dasar, supaya Speck dan Titan terasa satu keluarga, bukan sepuluh gaya yang berbeda-beda.
Hidupkan lewat animasi prosedural di Compose, bukan rig tangan: napas (scale naik-turun pelan), kedip (ganti ke 1 varian mata-tertutup sesekali), oleng/getar saat Mengamuk, desaturasi warna saat Wraith — semua ini transform & color-filter murni kode di atas satu gambar diam, tidak perlu file animasi tambahan sama sekali.
Contoh prompt yang bisa dipakai ulang untuk tier lain, tinggal ganti nama & warnanya:
“Goblin, mobile game mascot character, flat vector illustration, thick clean outline, chibi proportions, big expressive eyes, three-quarter view, single #94B348 accent color, transparent background, character-sheet style, consistent line weight”

15 — Urutan pengerjaan
Roadmap
Urutannya penting: setiap fase harus menghasilkan sesuatu yang benar-benar bisa kamu pakai sendiri sebelum lanjut ke fase berikutnya.

Fase 1
Rangka
CRUD task + subtask, dua slider, perhitungan tier & prioritas otomatis, enam Gains, EXP dan level, database Room dengan ownerId anonim dari Firebase Auth sejak awal — supaya tidak perlu migrasi skema besar-besaran nanti di Fase 2. Monsternya masih placeholder statis — emoji pun cukup. Tujuannya membuktikan angka-angkanya terasa benar sebelum sepeser pun dihabiskan untuk animasi.

Fase 2
Waktu & Akun
Tenggat, task terjadwal, AlarmManager, notifikasi, jatah gangguan harian, jam tenang, timer fokus — digabung dengan Google Sign-In, email + OTP, dan sinkron Firestore, karena keduanya sama-sama menyentuh siklus hidup task. Uji notifikasi & alarm di HP Xiaomi atau Oppo sungguhan sedini mungkin — emulator berbohong soal ini.

Fase 3
Nyawa
Sepuluh ilustrasi monster (AI, gaya terkunci konsisten), animasi prosedural napas/kedip/oleng di Compose, bar HP subtask, preview real-time di slider, animasi kekalahan, monster berkeliaran di beranda. Ini fase termahal sekaligus paling menyenangkan. Kerjakan tier 1, 5, dan 10 lebih dulu untuk memastikan gaya visualnya konsisten dari ujung ke ujung sebelum generate tujuh sisanya.

Fase 4
Kedalaman
Bestiary, statistik, akurasi estimasi, streak, pengali keseimbangan, radar chart, task berulang, terjemahan English lengkap, hapus akun, ekspor data.

Fase 5
Rilis
Kebijakan Privasi, formulir Data Safety, verifikasi layar consent OAuth Google, screenshot & listing dwibahasa (id / en-US) di Play Console, uji tertutup (closed testing) sebelum rilis produksi.

Nanti
Setelah dipakai sebulan
Widget layar utama, integrasi kalender, skin pack Nusantara, kolaborasi. Jangan sentuh apa pun di sini sampai kamu benar-benar memakai app-nya sendiri selama empat minggu penuh.

16 — Spesifikasi terkunci
Delapan keputusan, sudah dikonfirmasi
Semua pertanyaan di draf sebelumnya sudah kamu jawab. Ini rekapnya, supaya jadi satu referensi tunggal saat mulai membangun — tidak perlu bolak-balik ke pesan-pesan sebelumnya.

01
Enam Gains — final. Raga, Nalar, Karya, Harta, Ikatan, Jiwa. Lihat bagian 03.

02
Nama tier — final. Speck → Titan dipakai sebagai set utama. Set Nusantara (Debu→Kala) dan set kosmik disimpan sebagai skin pack untuk dirilis belakangan, bukan dihapus — struktur datanya (tier 1–10) tetap sama, cuma nama & ilustrasinya yang diganti. Lihat bagian 05.

03
EXP menyusut — dipakai sesuai default. −1% EXP/hari saat task masuk state Liar, dibatasi maksimal −15% dari task itu, dan bisa dimatikan lewat Setelan. Lihat bagian 06.

04
Agresivitas notifikasi — ditentukan mengikuti kebijakan Play Store. Maksimal 6 notifikasi/hari, jam tenang bisa diatur, dan alarm layar penuh dibatasi hanya untuk task Terjadwal pada jam mulainya — bukan untuk task yang sekadar terlambat — karena Android 14+ mensyaratkan alasan alarm/waktu-tepat untuk izin ini. Lihat bagian 06 & 08.

05
Seni monster — digarap dengan AI, animasinya prosedural. 10 ilustrasi statis (bukan 50 Lottie) dengan gaya visual dikunci lewat style-reference, dihidupkan lewat animasi napas/kedip/oleng di Compose. Panduan prompt & alasannya ada di bagian 14.

06
Login — wajib. Tidak ada mode tamu; layar Masuk/Daftar jadi gerbang pertama sejak buka app pertama kali. Lihat bagian 13.

07
OTP — lewat email saja, bukan SMS. Dikirim via Cloud Function + penyedia email transaksional, bukan fitur SMS Firebase Phone Auth yang berbayar per pesan. Lihat bagian 13.

08
Bahasa — ikut setelan HP, dengan pemilih di layar utama. Default dibaca dari Locale sistem saat instal pertama; chip “ID / EN” muncul di Beranda tepat setelah login pertama supaya gampang dikoreksi tanpa masuk Setelan. Lihat bagian 11.