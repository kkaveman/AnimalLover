
# Aplikasi Komunitas Pecinta Kucing



---

## Anggota Kelompok
| No | Nama Anggota | NIM |
|----|---------------|-----|
| 1. | Amran Thaqif Rajendra | 00000101383 |
| 2. | El Jalaluddin Raaqi | 00000099877 |
| 3. | Gabriel Imanullah P | 00000100492 |
| 4. | Kevin Anatta Foong | 00000098811 |

---

## Cara Penggunaan Aplikasi

### 1. Login dan Register
- Saat pertama kali membuka aplikasi, pengguna akan diarahkan ke **Fragment Login**.  
- Jika belum memiliki akun, tekan tombol **Daftar** untuk menuju ke **Fragment Register**.  
- Isi data yang diperlukan kemudian tekan **Daftar / Register**.  
- Setelah berhasil login, pengguna akan diarahkan ke **halaman utama (Fragment Home)**.

Jika sudah pernah login sebelumnya, aplikasi akan langsung menampilkan **Fragment Home** tanpa perlu login ulang.

---

### 2. Fragment Home (Halaman Utama)
Menampilkan berbagai posting dan event komunitas.

Fitur yang tersedia:
- **Event Carousel** untuk menampilkan kegiatan terbaru komunitas.  
- **Search Bar** untuk mencari posting, pengguna, atau event tertentu.  
- **Ikon hati** untuk menyukai posting.  
- **Ikon chat** untuk menuju ke **Fragment Chat**.  
- **Profil kucing** untuk menuju ke **Fragment Profile**.

---

### 3. Fragment Chat
- Menampilkan daftar percakapan antar pengguna.  
- Tekan salah satu chat untuk membuka **Fragment Selected Chat** dan melanjutkan obrolan.

---

### 4. Fragment Profile
- Menampilkan informasi pengguna dan profil kucing.  
- Dapat diakses melalui:
  - Tombol **Profile Kucing** di halaman utama, atau  
  - **App Drawer (ikon hamburger)** di kiri atas layar.

---

### 5. Fragment Events
Menampilkan daftar kegiatan komunitas seperti vaksinasi, adopsi, dan gathering.  
Pengguna dapat melihat detail acara dan mendaftar untuk berpartisipasi.

---

### 6. Fragment Community
Berisi ruang interaksi antar anggota komunitas.

Terdiri dari beberapa bagian:
- **Fragment Community Post** untuk melihat detail posting komunitas.  
- **Fragment Add Post** untuk membuat posting baru.  
- **Fragment Adoption Center** untuk melihat atau mengajukan adopsi kucing.  
- **Fragment Information Center** untuk membaca artikel dan informasi seputar kucing.

---

### 7. Fragment Screening
Fitur skrining untuk menilai kesiapan pengguna sebelum melakukan adopsi kucing.

---

### 8. App Drawer (Hamburger Menu)
Dapat diakses dari ikon garis tiga di kiri atas layar.

Isi menu:
- **Header** untuk menampilkan profil pengguna (klik untuk ke Fragment Profile).  
- **Settings** untuk membuka **Fragment Settings**.  
- **Help Center** untuk membuka **Fragment Help Center**.  
- **Admin (Hidden)** hanya terlihat dan dapat diakses oleh pengguna dengan hak akses admin.

---

### 9. Fragment Admin (Khusus Admin)
Menu ini hanya dapat diakses oleh akun admin melalui App Drawer.  
Setelah login sebagai admin, menu “Admin” akan muncul secara otomatis.

Di dalamnya terdapat:
- **Fragment Admin ViewPager** sebagai tab navigasi internal untuk manajemen data.  
- **Fragment User CRUD** di mana admin dapat:
  - Melihat daftar pengguna aplikasi.  
  - Menambah, mengedit, atau menghapus data pengguna (CRUD).  
  - Mengatur status akun pengguna (aktif/nonaktif).  
- **Menu Logout / Login** untuk keluar atau masuk akun admin.

---

### 10. Logout
- Tekan menu **Logout** di App Drawer untuk keluar dari akun.  
- Setelah logout, aplikasi akan kembali ke **Fragment Login**.  
- Pengguna dapat login kembali atau membuat akun baru bila diperlukan.

---

### 11. Alur Umum Penggunaan
1. Buka aplikasi dan tampil **Fragment Login**.  
2. Register atau login akun pengguna / admin.  
3. Masuk ke **Fragment Home**.  
4. Gunakan **bottom navbar** untuk navigasi antar halaman.  
5. Gunakan **App Drawer** untuk akses pengaturan, bantuan, atau menu admin.  
6. Lakukan interaksi (chat, posting, event, adopsi, dan sebagainya).  
7. Logout bila ingin keluar dari akun.
