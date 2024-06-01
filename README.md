# CanonMart-Ecommerce

CanonMart adalah aplikasi yang khusus menyediakan produk-produk merek Canon, mulai dari tinta printer, printer, kamera, dan barang-barang lainnya yang diproduksi oleh Canon. Ini bisa menjadi tempat yang sangat nyaman bagi para penggemar produk Canon atau orang-orang yang mencari produk-produk berkualitas dari merek tersebut. Dengan CanonMart, pengguna dapat dengan mudah menemukan dan membeli produk-produk Canon tanpa harus mencari-cari di tempat lain.

## Fitur
- Login Dan Registrasi : Pengguna dapat membuat akun baru atau masuk menggunakan akun yang sudah ada. Data pengguna disimpan dengan aman menggunakan database SQLite.
- Detail Product : Informasi tentang produk canon dari nama produk,harga,deskripsi,dan nomor SKU.
- Profil Pengguna: Pengguna dapat mengakses dan mengupdate informasi profil mereka. Fitur ini juga memungkinkan pengguna untuk logout dan melihat histori pesanan produk yang sudah dibelanja serta bisa mengganti tema.
- Menyimpan Produk ke Daftar Favorit: Tandai dan simpan Produk Ke favorit untuk diakses kembali dengan mudah.
- Cart : Pengguna dapat melihat informasi di keranjang belanja dan bisa mengupdate jumlah produk yang di inginkan dan juga menghapus produk tersebut.

## Penggunaan Aplikasi
- Register : Buat akun baru dengan mengisi formulir registrasi, diAplikasi ini saya hanya memasukkan username dan password.
- Login : Masuk ke aplikasi menggunakan akun yang sudah terdaftar.
- Home: Halaman pertama yang diakses oleh user yang telah login ialah halaman beranda CanonMart.Dimana menampilkan berbagai jenis produk dari Canon disertai nama produk dan harganya.
- Product Detail : Ketika klik produk yang ada dihome, maka halaman product detail akan tampil disertai dengan nama product,harga,sku, desckripsi produk, dan juga disertai tombol favorite untuk menyimpan produk yang disukai, ada juga tombol cart dimana untuk memasukkan produk ke keranjag, dan ada juga tombol beli sekarang jika ingin langsung membeli produk tersebut.
- Search : untuk search ada dibagian home dan expensive dimana perbedaan pencariannya itu untuk home itu bisa mencari semua nama produk dan sku, tapi tidak bisa mencari menggunakan harga (sebenarnya bisa saya tambahkan tapi saya kasi spesial expensive biar berbeda), sedangkan expensive bisa semuanya mencari baik itu nama produk,sku, dan harga.
- Favorite : setelah menyimpan produk di favorite maka dibagian kanan atas ada tombol favorite untuk melihat produk yang disukai dan juga bisa menghapus produk tersebut.
- Cart : setelah menambahkan ke keranjang maka bisa mengedit jumlah produk yang diinginkan dan juga dihapus
- Profile : setelah membeli barang saya menyiapkan saldo agar lebih real untuk aplikasi ecommerce, dimana jika sudah membeli maka saldonya berkurang, dan bisa melihat histori pesanan yang sudah dibeli serta mengatur tema yang diinginkan dan juga jika ingin mengupdate informasi maka ada setingan akun jika ingin mengupdate informasi.

## Teknologi
SQLite : digunakan untuk menyimpan data pengguna dan informasi terkait aplikasi lainnya.
Retrofit : untuk pengambilan API
Androdi Studio : untuk pengembangan Aplikasi
Lottie : Untuk progressbar

## Authors
[@dead.al](https://www.instagram.com/@dead.al/)

## Api

[Api CanonMart] (https://api.bestbuy.com/v1/products(manufacturer=canon&salePrice%3C1000)?format=json&show=sku,name,salePrice,image,longDescription&apiKey=GnOHjdEh0feKXImgF1k9sev3)


