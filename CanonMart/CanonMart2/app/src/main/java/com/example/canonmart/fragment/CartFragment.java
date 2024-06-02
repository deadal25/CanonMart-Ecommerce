package com.example.canonmart.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canonmart.adapter.CartAdapter;
import com.example.canonmart.db.CartDBHelper;
import com.example.canonmart.model.CartItem;
import com.example.canonmart.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment implements CartAdapter.OnQuantityChangeListener {

    public static final int MAX_QUANTITY =  100 ;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private TextView totalPriceTextView;
    private TextView emptyCartTextView;
    private CartDBHelper dbHelper;

    private TextView saldoSayaTextView;

    private SharedPreferences sharedPreferences;


//    static final int MAX_QUANTITY = 100;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_cart_fragment, container, false);

        dbHelper = new CartDBHelper(getContext());

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        totalPriceTextView = view.findViewById(R.id.totalPriceTextView);
        emptyCartTextView = view.findViewById(R.id.emptyCartTextView);

        // Inisialisasi sharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("checkout_prefs", MODE_PRIVATE);
        saldoSayaTextView = view.findViewById(R.id.saldosaya);

        double saldo = getCurrentSaldo();
        String formattedPrice = String.format("Total Uang: $%.2f", saldo);

        saldoSayaTextView.setText(formattedPrice);
        // Load data saat pertama kali fragment dibuat
        loadData();

        Button checkoutButton = view.findViewById(R.id.checkoutButton);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartItems.isEmpty()) {
                    Toast.makeText(getContext(), "Keranjang Tidak Memiliki Produk, Silahkan tambah produk ke Keranjang", Toast.LENGTH_SHORT).show();
                } else {
                    showCheckoutConfirmationDialog();
                }
            }
        });

        return view;
    }

    private double getCurrentSaldo() {
        // Mendapatkan saldo dari SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE);
        return sharedPreferences.getInt("saldo", 0);
    }


    private void saveCheckoutItems() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        // Ambil item checkout yang ada
        String existingJson = sharedPreferences.getString("checkout_items", null);
        Type type = new TypeToken<List<CartItem>>() {
        }.getType();
        List<CartItem> existingItems = gson.fromJson(existingJson, type);

        // Jika tidak ada item checkout yang ada, buat list baru
        if (existingItems == null) {
            existingItems = new ArrayList<>();
        }

        // Tambahkan item baru ke list yang ada
        existingItems.addAll(cartItems);

        // Simpan kembali ke SharedPreferences
        String json = gson.toJson(existingItems);
        editor.putString("checkout_items", json);
        editor.apply();
    }

    private void loadData() {
        cartItems = getAllCartItems();
        cartAdapter = new CartAdapter(cartItems, CartFragment.this);
        recyclerView.setAdapter(cartAdapter);
        displayTotalPrice();

        if (cartItems.isEmpty()) {
            emptyCartTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyCartTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private List<CartItem> getAllCartItems() {
        return dbHelper.getAllCartItems();
    }

    private void displayTotalPrice() {
        double totalPrice = calculateTotalPrice();
        String formattedPrice = String.format("Total Price: $%.2f", totalPrice);
        totalPriceTextView.setText(formattedPrice);
    }

    private double calculateTotalPrice() {
        double totalPrice = 0;
        for (CartItem item : cartItems) {
            totalPrice += item.getTotalPrice();
        }
        return totalPrice;
    }


    @Override
    public void onQuantityChanged() {
        displayTotalPrice();
        checkCartEmpty();
    }

    private void checkCartEmpty() {
        if (cartItems.isEmpty()) {
            emptyCartTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyCartTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void clearCart() {
        dbHelper.deleteAllItems();
        cartItems.clear();
        cartAdapter.notifyDataSetChanged();
        totalPriceTextView.setText("Total Price: $0.00");
        checkCartEmpty();
    }

    private void showCheckoutConfirmationDialog() {
        double totalPrice = calculateTotalPrice();
        double saldo = getCurrentSaldo();


        // Jika saldo tidak mencukupi
        if (saldo < totalPrice) {
            Toast.makeText(getContext(), "Uang Anda tidak cukup, silahkan lakukan Top Up Di Menu Settings   ", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Konfirmasi Pembelian")
                .setMessage("Apakah Anda benar-benar ingin membeli produk ini?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        saveCheckoutItems();
                        // Kurangi saldo setelah checkout
                        double remainingSaldo = saldo - totalPrice;
                        // Simpan saldo terbaru ke SharedPreferences
                        saveCurrentSaldo(remainingSaldo);
                        // Update tampilan saldo secara langsung
                        saldoSayaTextView.setText(String.format("Total Uang: $%.2f", remainingSaldo));
                        clearCart();
                        Toast.makeText(getContext(), "Pembelian Berhasil, Silahkan melihat Histori Pesanan Di Menu Settings.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.baseline_shopping_cart_checkout_24)
                .show();
    }


    private void saveCurrentSaldo(double saldo) {
        // Menyimpan saldo terbaru ke SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("saldo", (int) saldo);
        editor.apply();
    }


//    Button checkoutButton = findViewById(R.id.checkoutButton);
//                checkoutButton.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            // Membuat AlertDialog Builder
//            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ProductDetailsActivity.this);
//            builder.setTitle("Konfirmasi Pembelian");
//            builder.setMessage("Apakah Anda benar-benar ingin membeli produk ini?");
//
//            // Menambahkan tombol positif (Ya)
//            builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    List<CartItem> checkoutItems = getCheckoutItems();
//                    CartItem cartItem = new CartItem(product.getName(), product.getSalePrice(), 1, product.getImage());
//                    // Include image URL
//                    checkoutItems.add(cartItem);
//                    saveCheckoutItems(checkoutItems);
//                    Toast.makeText(ProductDetailsActivity.this, "Added to checkout", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            // Menambahkan tombol negatif (Tidak)
//            builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    // Jika pengguna menekan Tidak, tutup dialog dan tidak lakukan apa-apa
//                    dialog.dismiss();
//                }
//            });
//
//            // Menampilkan AlertDialog
//            android.app.AlertDialog alertDialog = builder.create();
//            alertDialog.show();
//        }
//    });
//public interface OnCheckoutListener {
//    void onCheckoutSuccess(double totalPrice);
//}
//
//    private OnCheckoutListener checkoutListener;
//
//    // Metode setter untuk checkoutListener
//    public void setCheckoutListener(OnCheckoutListener listener) {
//        this.checkoutListener = listener;
//    }
//
//    // Metode untuk memberitahu SettingsFragment bahwa checkout telah berhasil
//    private void notifyCheckoutSuccess(double totalPrice) {
//        if (checkoutListener != null) {
//            checkoutListener.onCheckoutSuccess(totalPrice);
//        }
//    }


}
