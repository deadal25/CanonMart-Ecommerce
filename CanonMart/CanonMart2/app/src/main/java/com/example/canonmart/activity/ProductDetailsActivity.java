package com.example.canonmart.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.canonmart.db.CartDBHelper;
import com.example.canonmart.db.ProductDBHelper;
import com.example.canonmart.model.CartItem;
import com.example.canonmart.model.Product;
import com.example.canonmart.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailsActivity extends AppCompatActivity {

    private ProductDBHelper dbHelper;
    private CartDBHelper cartDbHelper;
    private ImageButton favoriteButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        dbHelper = new ProductDBHelper(this);
        cartDbHelper = new CartDBHelper(this);
        sharedPreferences = getSharedPreferences("checkout_prefs", Context.MODE_PRIVATE);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("product")) {
            final Product product = intent.getParcelableExtra("product");
            if (product != null) {
                ImageView imageView = findViewById(R.id.productImageView);
                TextView nameTextView = findViewById(R.id.productNameTextView);
                TextView skuTextView = findViewById(R.id.productSkuTextView);
                TextView priceTextView = findViewById(R.id.productPriceTextView);
                TextView descriptionTextView = findViewById(R.id.productDescriptionTextView);
                favoriteButton = findViewById(R.id.favoriteButton);

                // Load image using Picasso
                String imageUrl = product.getImage();
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Picasso.get().load(imageUrl).into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.error_image); // Set default image if URL is empty
                }

                nameTextView.setText(product.getName());
                skuTextView.setText("SKU: " + product.getSku());
                priceTextView.setText("Price: $" + product.getSalePrice());
                descriptionTextView.setText(product.getLongDescription());

                if (dbHelper.isFavorite(product.getSku())) {
                    favoriteButton.setImageResource(R.drawable.baseline_favorite_24);
                } else {
                    favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24);
                }

                favoriteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dbHelper.isFavorite(product.getSku())) {
                            dbHelper.removeFavorite(product.getSku());
                            Toast.makeText(ProductDetailsActivity.this, "Mengapus Produk Dari Favorite", Toast.LENGTH_SHORT).show();
                            favoriteButton.setImageResource(R.drawable.baseline_favorite_border_24);

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("removed_product_sku", product.getSku());
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        } else {
                            dbHelper.addFavorite(product);
                            Toast.makeText(ProductDetailsActivity.this, "Menambahkan Produk Ke Favorite", Toast.LENGTH_SHORT).show();
                            favoriteButton.setImageResource(R.drawable.baseline_favorite_24);
                        }
                    }
                });

                ImageButton addToCartButton = findViewById(R.id.addToCartButton);
                addToCartButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!cartDbHelper.isCartItem(product.getName())) {
                            cartDbHelper.addCartItem(product);
                            Toast.makeText(ProductDetailsActivity.this, "Menambahkan Ke Keranjang", Toast.LENGTH_SHORT).show();
                        } else {
                            int currentQuantity = cartDbHelper.getCartItemQuantity(product.getName());
                            cartDbHelper.updateCartItemQuantity(product.getName(), currentQuantity + 1);
                            Toast.makeText(ProductDetailsActivity.this, "Menambahkan Ke Keranjang", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                Button checkoutButton = findViewById(R.id.checkoutButton);
                checkoutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Membuat AlertDialog Builder
                        double totalPrice = product.getSalePrice();
                        double saldo = getCurrentSaldo();
                        if (saldo < totalPrice) {
                            Toast.makeText(ProductDetailsActivity.this, "Uang Anda tidak cukup, silahkan lakukan Top Up di Menu Settings", Toast.LENGTH_SHORT).show();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailsActivity.this);
                            builder.setTitle("Konfirmasi Pembelian");
                            builder.setMessage("Apakah Anda benar-benar ingin membeli produk ini?");

                            // Menambahkan tombol positif (Ya)
                            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    double totalPrice = product.getSalePrice();
                                    double saldo = getCurrentSaldo();
                                    List<CartItem> checkoutItems = getCheckoutItems();
                                    CartItem cartItem = new CartItem(product.getName(), product.getSalePrice(), 1, product.getImage());
                                    double remainingSaldo = saldo - totalPrice;
                                    // Simpan saldo terbaru ke SharedPreferences
                                    saveCurrentSaldo(remainingSaldo);
                                    // Include image URL
                                    checkoutItems.add(cartItem);
                                    saveCheckoutItems(checkoutItems);
                                    Toast.makeText(ProductDetailsActivity.this, "Anda Telah Melakukan Pembelian Produk, Silahkan Cek Halaman Settings", Toast.LENGTH_SHORT).show();

                                }
                            });


                            // Menambahkan tombol negatif (Tidak)
                            builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Jika pengguna menekan Tidak, tutup dialog dan tidak lakukan apa-apa
                                    dialog.dismiss();
                                }
                            });

                            // Menampilkan AlertDialog
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }
                });
            }
        }
    }

    private List<CartItem> getCheckoutItems() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("checkout_items", null);
        Type type = new TypeToken<List<CartItem>>() {
        }.getType();
        List<CartItem> checkoutItems = gson.fromJson(json, type);
        return checkoutItems != null ? checkoutItems : new ArrayList<>();
    }

    private void saveCheckoutItems(List<CartItem> checkoutItems) {
        Gson gson = new Gson();
        String json = gson.toJson(checkoutItems);
        sharedPreferences.edit().putString("checkout_items", json).apply();
    }

//    private void addToCart(Product product) {
//        if (!cartDbHelper.isCartItem(product.getName())) {
//            cartDbHelper.addCartItem(product);
//            Toast.makeText(ProductDetailsActivity.this, "Menambahkan Ke Keranjang", Toast.LENGTH_SHORT).show();
//            // Update saldo saat item ditambahkan ke keranjang belanja
//            updateSaldo(-product.getSalePrice());
//        } else {
//            int currentQuantity = cartDbHelper.getCartItemQuantity(product.getName());
//            cartDbHelper.updateCartItemQuantity(product.getName(), currentQuantity + 1);
//            Toast.makeText(ProductDetailsActivity.this, "Menambahkan Ke Keranjang", Toast.LENGTH_SHORT).show();
//            // Update saldo saat item ditambahkan ke keranjang belanja
//            updateSaldo(-product.getSalePrice());
//        }
//    }

//    private void showCheckoutConfirmationDialog(final Product product) {
//        // Membuat AlertDialog Builder
//        AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailsActivity.this);
//        builder.setTitle("Konfirmasi Pembelian");
//        builder.setMessage("Apakah Anda benar-benar ingin membeli produk ini?");
//
//        // Menambahkan tombol positif (Ya)
//        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                List<CartItem> checkoutItems = getCheckoutItems();
//                CartItem cartItem = new CartItem(product.getName(), product.getSalePrice(), 1, product.getImage());
//                // Include image URL
//                checkoutItems.add(cartItem);
//                saveCheckoutItems(checkoutItems);
//                Toast.makeText(ProductDetailsActivity.this, "Added to checkout", Toast.LENGTH_SHORT).show();
//                // Update saldo saat checkout
//                updateSaldo(-product.getSalePrice());
//            }
//        });
//
//        // Menambahkan tombol negatif (Tidak)
//        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                // Jika pengguna menekan Tidak, tutup dialog dan tidak lakukan apa-apa
//                dialog.dismiss();
//            }
//        });
//
//        // Menampilkan AlertDialog
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }

    private void updateSaldo(double amount) {
        // Retrieve current saldo from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        float currentSaldo = sharedPreferences.getFloat("saldo", 0.0f);
        // Update saldo by adding/subtracting the amount
        float newSaldo = currentSaldo + (float) amount;
        // Save updated saldo to SharedPreferences
        sharedPreferences.edit().putFloat("saldo", newSaldo).apply();
    }

    private double getCurrentSaldo() {
        // Mendapatkan saldo dari SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return sharedPreferences.getInt("saldo", 0);
    }

    private void saveCurrentSaldo(double saldo) {
        // Menyimpan saldo terbaru ke SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("saldo", (int) saldo);
        editor.apply();
    }


}
