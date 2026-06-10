# Vestly - Fashion Inspiration App

## Deskripsi
Vestly adalah aplikasi inspirasi fashion yang membantu pengguna menemukan ide gaya berpakaian sehari-hari. Aplikasi ini menampilkan foto-foto outfit dari berbagai kategori seperti Casual, Formal, Streetwear, Minimalist, dan Hijab.

**Tema Aplikasi:** Gaya Hidup (Lifestyle)

## Fitur Utama

| No | Fitur | Deskripsi |
|----|-------|-----------|
| 1 | 🏠 **Home** | Menampilkan trending fashion outfit dari Pexels API dengan **filter kategori** (All, Casual, Formal, Streetwear, Minimalist, Hijab) |
| 2 | 🔍 **Search** | Mencari inspirasi fashion berdasarkan **kata kunci** yang diinginkan pengguna |
| 3 | ❤️ **Favorite** | Menyimpan outfit favorit **secara lokal** menggunakan SharedPreferences |
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

### Navigasi

**Intent:** MainActivity → DetailActivity (membawa photoId, photoUrl, photographer, category)

### Offline Cache

| Fragment | Metode Penyimpanan | Perilaku Saat Offline |
|----------|-------------------|----------------------|
| HomeFragment | SharedPreferences | Menampilkan data terakhir yang pernah di-load |
| SearchFragment | SharedPreferences | Menampilkan hasil pencarian terakhir (per kata kunci) |
| FavoriteFragment | SharedPreferences | Menampilkan semua favorit (tersimpan permanen) |

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

