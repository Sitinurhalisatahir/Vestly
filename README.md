# Vestly - Fashion Inspiration App

## Deskripsi
Vestly adalah aplikasi inspirasi fashion yang membantu pengguna menemukan ide gaya berpakaian sehari-hari. Aplikasi ini menampilkan foto-foto outfit dari berbagai kategori seperti Casual, Formal, Streetwear, Minimalist, dan Hijab.

**Tema Aplikasi:** Gaya Hidup (Lifestyle)

## Fitur Utama

| No | Fitur | Deskripsi |
|----|-------|-----------|
| 1 | 🏠 **Home** | Menampilkan trending fashion outfit dari Pexels API dengan **filter kategori** (All, Casual, Formal, Streetwear, Minimalist, Hijab) |
| 2 | 🔍 **Search** | Mencari inspirasi fashion berdasarkan **kata kunci** yang diinginkan pengguna |
| 3 | ❤️ **Favorite** |  Menyimpan outfit favorit **secara lokal** menggunakan **SQLite Database**  |
| 4 | ⚙️ **Settings** | Mengatur **Dark/Light Mode** dan menampilkan **informasi aplikasi** |
| 5 | 📴 **Offline Cache** | Data Home dan hasil pencarian **tetap tampil** meskipun tanpa koneksi internet |
| 6 | 🔄 **Pull to Refresh** | **Tarik layar ke bawah** untuk me-refresh data terbaru dari API |
| 7 | 🗑️ **Clear All Favorites** | Menghapus **semua koleksi favorit** sekaligus dengan konfirmasi dialog |
| 8 | 📱 **Splash Overlay** | Tampilan **selamat datang** dengan logo dan slogan "Your Daily Fashion Inspiration" |
| 9 | ⚠️ **Error Handling** | **Tombol retry** muncul saat gagal memuat data atau tidak ada koneksi internet |

## API yang Digunakan

**Pexels API** — https://www.pexels.com/api/

API ini menyediakan foto-foto berkualitas tinggi gratis untuk konten fashion dan gaya hidup. Memerlukan API key untuk mengakses endpoint.

| Endpoint | Method | Fungsi |
|----------|--------|--------|
| `/v1/search?query={query}&page={page}` | GET | Mencari foto berdasarkan kata kunci (fashion, casual, formal, dll) |
| `/v1/curated?page={page}` | GET | Mengambil foto-foto trending/curated untuk halaman Home |

## Arsitektur & Struktur Teknis

Aplikasi Vestly mengusung desain UI dengan **tema warna Primary Purple (#6200EE)**. Aplikasi mendukung **Dark Mode** dan **Light Mode** yang dapat diatur oleh pengguna.

### Activity

| Activity | Launcher | Fungsi |
|----------|----------|--------|
| MainActivity | ✅ Ya | Halaman utama dengan BottomNavigationView dan NavHostFragment |
| DetailActivity | ❌ Tidak | Menampilkan detail foto (photographer, deskripsi, tombol favorite & share) |

### Fragment

| Fragment | Fungsi |
|----------|--------|
| HomeFragment | Menampilkan feed foto fashion dengan filter kategori (Chips) dan RecyclerView (GridLayout) |
| SearchFragment | Mencari foto berdasarkan kata kunci dengan debounce 3 karakter |
| FavoriteFragment | Menampilkan daftar foto favorit yang disimpan secara lokal (GridLayout) |
| SettingsFragment | Pengaturan Dark/Light Mode dan informasi aplikasi |

### Database (SQLite)

| File | Fungsi |
|------|--------|
| DatabaseContract.java | Mendefinisikan nama tabel dan kolom |
| DatabaseHelper.java | Membuat dan upgrade database SQLite |
| FavoriteDao.java | CRUD operasi (insert, delete, query, isFavorite) |
| FavoriteRepository.java | Jembatan antara fragment dengan database |

### Navigasi

**Intent:** MainActivity → DetailActivity (membawa photoId, photoUrl, photographer, category)

### Penyimpanan Data

| Jenis Data | Metode Penyimpanan | Keterangan |
|------------|-------------------|-------------|
| Favorite | **SQLite** | Data favorit tersimpan permanen di database lokal |
| Tema | SharedPreferences | Dark/Light mode |
| Cache Home | SharedPreferences | Data offline untuk halaman Home |
| Cache Search | SharedPreferences | Data offline untuk hasil pencarian |

### Offline Cache

| Fragment | Metode Penyimpanan | Perilaku Saat Offline |
|----------|-------------------|----------------------|
| HomeFragment | SharedPreferences | Menampilkan data terakhir yang pernah di-load |
| SearchFragment | SharedPreferences | Menampilkan hasil pencarian terakhir (per kata kunci) |
| FavoriteFragment | SQLite | Menampilkan semua favorit dari database |

### RecyclerView

| Lokasi | Layout Manager | Item View |
|--------|---------------|-----------|
| HomeFragment | GridLayoutManager (2 kolom) | MaterialCardView dengan foto + photographer |
| SearchFragment | GridLayoutManager (2 kolom) | MaterialCardView dengan foto + photographer |
| FavoriteFragment | GridLayoutManager (2 kolom) | MaterialCardView dengan foto + photographer + tombol hapus |

## Networking & Logika (Retrofit)

Sesuai materi Bab 6, **Retrofit** digunakan untuk mengambil data foto dari **Pexels API**. Aplikasi Vestly memiliki dua skenario pengambilan data:

### 1. Pencarian Default (HomeFragment)

Saat pengguna membuka halaman **Home**, aplikasi akan melakukan fetch foto trending/curated dari endpoint `/v1/curated` dengan halaman acak (random page 1-10) untuk memberikan variasi konten.

### 2. Filter Kategori (Chip)

Ketika pengguna mengklik salah satu **chip filter** (Casual, Formal, Streetwear, Minimalist, Hijab), aplikasi akan melakukan fetch API Pexels dengan keyword spesifik:

| Chip | Query |
|------|-------|
| All | "fashion outfit style" |
| Casual | "casual outfit" |
| Formal | "formal outfit" |
| Streetwear | "streetwear outfit" |
| Minimalist | "minimalist outfit" |
| Hijab | "hijab outfit" |

### 3. Pencarian (SearchFragment)

Pengguna dapat mencari foto berdasarkan **kata kunci** bebas. Pencarian akan otomatis dimulai setelah pengguna mengetik minimal **3 karakter** (debounce). API dipanggil menggunakan endpoint `/v1/search?query={query}&page={randomPage}`.

### Penerapan Offline Cache

```java
// Menyimpan hasil API ke SharedPreferences
private void savePhotosToCache(List<Photo> photos) {
    Gson gson = new Gson();
    String json = gson.toJson(photos);
    SharedPrefManager.getInstance(context).saveHomeCache(json);
}

// Mengambil data dari cache saat offline
private void showCachedPhotos() {
    List<Photo> cachedPhotos = loadPhotosFromCache();
    if (!cachedPhotos.isEmpty()) {
        photoList.clear();
        photoList.addAll(cachedPhotos);
        photoAdapter.setPhotos(photoList);
        showContent();
    }
}
```

## Networking & Logika (Retrofit)

**Retrofit** digunakan untuk mengambil data foto dari **Pexels API**. Aplikasi Vestly memiliki tiga skenario pengambilan data:

### 1. Halaman Home (Trending)

Saat pengguna membuka halaman **Home**, aplikasi melakukan fetch foto trending dari endpoint `/v1/curated` dengan halaman acak (random page 1-10) untuk memberikan variasi konten.

### 2. Filter Kategori (Chip)

Ketika pengguna mengklik **chip filter**, aplikasi melakukan fetch API dengan keyword spesifik:

| Chip | Query API |
|------|-----------|
| All | "fashion outfit style" |
| Casual | "casual outfit" |
| Formal | "formal outfit" |
| Streetwear | "streetwear outfit" |
| Minimalist | "minimalist outfit" |
| Hijab | "hijab outfit" |

### 3. Pencarian (Search)

Pengguna mencari foto berdasarkan **kata kunci** bebas. Pencarian dimulai setelah mengetik minimal **3 karakter** (debounce) menggunakan endpoint `/v1/search?query={query}&page={randomPage}`.

### Offline Cache

Hasil API disimpan ke **SharedPreferences** dalam bentuk JSON, sehingga data tetap tampil saat offline.

```java
private void savePhotosToCache(List<Photo> photos) {
    Gson gson = new Gson();
    String json = gson.toJson(photos);
    SharedPrefManager.getInstance(context).saveHomeCache(json);
}

private void showCachedPhotos() {
    List<Photo> cachedPhotos = loadPhotosFromCache();
    if (!cachedPhotos.isEmpty()) {
        photoList.clear();
        photoList.addAll(cachedPhotos);
        photoAdapter.setPhotos(photoList);
        showContent();
    }
}
```
## Background Thread

Operasi database SQLite dijalankan di **background thread** menggunakan `ExecutorService` agar tidak memblokir UI. Hasilnya dikembalikan ke Main Thread melalui `Handler`.

```java
ExecutorService executor = Executors.newSingleThreadExecutor();
Handler handler = new Handler(Looper.getMainLooper());

executor.execute(() -> {
    List<FavoritePhoto> favorites = favoriteDao.getAllFavorites();
    handler.post(() -> {
        favoriteAdapter.setFavorites(favorites);
    });
});
```

---

### 5. **Update Tech Stack**

```markdown
| SQLite | Penyimpanan favorit secara lokal |
| ExecutorService | Background thread untuk operasi database |
```

## Struktur Folder Project
```
Vestly/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/vestly/
│   │   │   │   ├── activity/
│   │   │   │   │   ├── MainActivity.java
│   │   │   │   │   └── DetailActivity.java
│   │   │   │   │
│   │   │   │   ├── fragment/
│   │   │   │   │   ├── HomeFragment.java
│   │   │   │   │   ├── SearchFragment.java
│   │   │   │   │   ├── FavoriteFragment.java
│   │   │   │   │   └── SettingsFragment.java
│   │   │   │   │
│   │   │   │   ├── adapter/
│   │   │   │   │   ├── PhotoAdapter.java
│   │   │   │   │   └── FavoriteAdapter.java
│   │   │   │   │
│   │   │   │   ├── database/
│   │   │   │   │   ├── DatabaseContract.java
│   │   │   │   │   ├── DatabaseHelper.java
│   │   │   │   │   └── FavoriteDao.java
│   │   │   │   │
│   │   │   │   ├── helper/
│   │   │   │   │   ├── SharedPrefManager.java
│   │   │   │   │   └── ThemeManager.java
│   │   │   │   │
│   │   │   │   ├── model/
│   │   │   │   │   ├── Photo.java
│   │   │   │   │   ├── PhotoResponse.java
│   │   │   │   │   └── FavoritePhoto.java
│   │   │   │   │
│   │   │   │   ├── network/
│   │   │   │   │   ├── ApiClient.java
│   │   │   │   │   ├── ApiService.java
│   │   │   │   │   └── NetworkUtils.java
│   │   │   │   │
│   │   │   │   └── repository/
│   │   │   │       ├── PhotoRepository.java
│   │   │   │       └── FavoriteRepository.java
│   │   │   │
│   │   │   ├── res/
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── ic_home.xml
│   │   │   │   │   ├── ic_search.xml
│   │   │   │   │   ├── ic_heart_outline.xml
│   │   │   │   │   ├── ic_heart_filled.xml
│   │   │   │   │   ├── ic_heart_selector.xml
│   │   │   │   │   ├── ic_settings.xml
│   │   │   │   │   ├── ic_back.xml
|   |   |   |   |   ├── ic_launcher_background.xml
│   │   │   │   │   └── bg_btn_back.xml
│   │   │   │   │
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_detail.xml
│   │   │   │   │   ├── fragment_home.xml
│   │   │   │   │   ├── fragment_search.xml
│   │   │   │   │   ├── fragment_favorite.xml
│   │   │   │   │   ├── fragment_settings.xml
│   │   │   │   │   ├── item_photo.xml
│   │   │   │   │   └── item_favorite.xml
│   │   │   │   │
│   │   │   │   ├── menu/
│   │   │   │   │   └── bottom_nav_menu.xml
│   │   │   │   │
│   │   │   │   ├── navigation/
│   │   │   │   │   └── nav_graph.xml
│   │   │   │   │
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   ├── themes.xml
│   │   │   │   │   └── themes.xml (night)
│   │   │   │   │
│   │   │   │   └── mipmap/
│   │   │   │
│   │   │   └── AndroidManifest.xml
│   │   │
│   │   └── test/
│   │
│   └── build.gradle.kts
│
├── build.gradle.kts
├── gradle.properties
├── settings.gradle.kts
└── README.md
```

---

## Statistik File

| Kategori | Jumlah File |
|----------|-------------|
| Activity | 2 |
| Fragment | 4 |
| Adapter | 2 |
| Database | 3 |
| Helper | 2 |
| Model | 3 |
| Network | 3 |
| Repository | 2 |
| Layout XML | 8 |
| Drawable | 9 |
| Menu | 1 |
| Navigation | 1 |
| Values | 4 |
| **Total** | **~43 file** |


---

### Alur Pengguna

#### 1. Membuka Aplikasi
- Pengguna membuka aplikasi → Muncul **Splash Overlay** (2 detik)
- Menampilkan logo "VESTLY" dan slogan "Your Daily Fashion Inspiration"
- Otomatis masuk ke halaman **Home**

#### 2. Halaman Home
- Menampilkan **feed foto fashion** dari Pexels API
- Pengguna dapat **scroll** untuk melihat lebih banyak foto
- **Pull to refresh** untuk memuat ulang data
- **Filter Chip** (All, Casual, Formal, Streetwear, Minimalist, Hijab) untuk mengubah kategori
- Klik foto → pindah ke **DetailActivity**

#### 3. Halaman Search
- Pengguna mengetik **kata kunci** minimal 3 karakter
- Otomatis mencari foto yang sesuai
- Hasil pencarian ditampilkan dalam bentuk **grid**
- **Pull to refresh** untuk mengulang pencarian
- Klik foto → pindah ke **DetailActivity**

#### 4. Halaman Favorite
- Menampilkan semua foto yang pernah **disukai** pengguna
- Klik ❤️ pada item → menghapus dari favorit
- Klik tombol **"Clear All Favorites"** → menghapus semua favorit sekaligus (dengan konfirmasi)
- Klik foto → pindah ke **DetailActivity**

#### 5. Halaman Settings
- **Dark Mode toggle** → mengubah tema aplikasi (Light/Dark)
- Menampilkan **informasi aplikasi** (versi, developer, sumber API)

#### 6. Detail Activity
- Menampilkan foto **ukuran besar**
- Informasi **photographer**
- Tombol **Favorite** (❤️) → menambah/menghapus dari favorit
- Tombol **Share** → berbagi foto ke aplikasi lain
- **Similar photos** (horizontal scroll) → rekomendasi foto serupa
- Tombol **back** → kembali ke halaman sebelumnya

---

### Status Koneksi

| **Ada Internet** | Fetch dari API | Fetch dari API | Ambil dari **SQLite** |
| **Tidak Ada Internet (pernah buka)** | Tampil dari cache | Tampil dari cache (keyword yang sama) | Ambil dari **SQLite** |
| **Tidak Ada Internet (belum pernah buka)** | Error + Tombol Retry | Error + Tombol Retry | Tetap tampil dari **SQLite** (jika ada favorit) |

## Cara Install

### Cara 1 — Via APK (Mudah)

1. Buka halaman **Releases** di GitHub:  
   [https://github.com/Sitinurhalisatahir/Vestly/releases](https://github.com/Sitinurhalisatahir/Vestly/releases)

2. Klik file **Vestly.apk** (atau `app-debug.apk`) → otomatis download

3. Pindahkan file APK ke HP Android

4. **Aktifkan** "Install from unknown sources" di HP:  
   *Pengaturan → Keamanan → Install from unknown sources → ON*

5. Buka file APK di HP → klik **Install**

6. Buka aplikasi **Vestly**

---

### Cara 2 — Via Source Code (Build Sendiri)

#### Persyaratan
- Android Studio (versi terbaru)
- Java JDK 11 atau lebih tinggi
- Koneksi internet

#### Langkah-langkah

**1. Download source code dari GitHub:**
   - Klik tombol **Code** → **Download ZIP**
   - Extract file ZIP ke folder komputer kamu

   **Atau via terminal:**
   ```bash
   git clone https://github.com/Sitinurhalisatahir/Vestly.git
   ```

## Tech Stack

| Teknologi | Fungsi |
|-----------|--------|
| Java | Bahasa pemrograman utama |
| Retrofit | Networking & API call ke Pexels |
| SQLite | Penyimpanan favorit secara lokal |
| SharedPreferences | Penyimpanan tema dan cache offline |
| Navigation Component | Navigasi antar fragment |
| Glide | Memuat dan menampilkan gambar |
| Material Design | UI Components (CardView, Chip, BottomNav) |
| SwipeRefreshLayout | Pull to refresh |
| ExecutorService | Background thread untuk operasi database |


## Developer

| | |
|---|---|
| **Nama** | Siti Nur Halisa Tahir |
| **NIM** | H071241086 |
| **Tema Aplikasi** | Gaya Hidup (Lifestyle) |
| **Mata Kuliah** | Pemrograman Mobile |
| **Tahun** | 2026 |

---

## Referensi

- [Pexels API Documentation](https://www.pexels.com/api/documentation/)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Material Design Guidelines](https://material.io/design)
