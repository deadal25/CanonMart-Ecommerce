package com.example.canonmart.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.canonmart.adapter.OrderAdapter;
import com.example.canonmart.model.CartItem;
import com.example.canonmart.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private List<CartItem> orderItems;

    private TextView totalPriceTextView;
    private TextView pesananTextView; // Tambahkan variabel untuk menampilkan teks "Histori Pesanan Kosong"
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        recyclerView = view.findViewById(R.id.orderRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        totalPriceTextView = view.findViewById(R.id.totalPriceTextView);
        pesananTextView = view.findViewById(R.id.PesananTextView); // Inisialisasi pesananTextView

        sharedPreferences = requireActivity().getSharedPreferences("checkout_prefs", Context.MODE_PRIVATE);

        ImageButton deleteOrderButton = view.findViewById(R.id.deleteOrderButton);
        deleteOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteOrder(v);
            }
        });
        loadData();

        return view;
    }

    private void loadData() {
        orderItems = getCheckoutItems();
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
        orderAdapter = new OrderAdapter(orderItems);
        recyclerView.setAdapter(orderAdapter);
        displayTotalPrice();

        // Tampilkan teks "Histori Pesanan Kosong" jika list pesanan kosong saat dimuat
        showEmptyOrderText();
    }

    private List<CartItem> getCheckoutItems() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("checkout_items", null);
        Type type = new TypeToken<List<CartItem>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    private void displayTotalPrice() {
        double totalPrice = calculateTotalPrice();
        String formattedPrice = String.format("Total Price: $%.2f", totalPrice);
        totalPriceTextView.setText(formattedPrice);
    }

    private double calculateTotalPrice() {
        double totalPrice = 0;
        for (CartItem item : orderItems) {
            totalPrice += item.getTotalPrice();
        }
        return totalPrice;
    }

    public void deleteOrder(View view) {
        if (orderItems.isEmpty()) {
            Toast.makeText(requireContext(), "Silahkan Membeli Produk Terlebih Dahulu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Membuat AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Konfirmasi");
        builder.setMessage("Apakah Anda benar-benar yakin untuk menghapus semua histori pesanan Anda?");

        // Menambahkan tombol positif (Ya)
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Jika pengguna menekan Ya, hapus semua histori pesanan
                clearOrder();
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

    // Metode untuk menghapus semua histori pesanan
    private void clearOrder() {
        // Clear the list of order items
        orderItems.clear();

        // Notify the adapter that the dataset has changed
        orderAdapter.notifyDataSetChanged();

        // Update the total price display
        displayTotalPrice();

        // Clear the shared preferences to remove all saved order items
        sharedPreferences.edit().remove("checkout_items").apply();

        // Tampilkan teks "Histori Pesanan Kosong" setelah semua pesanan dihapus
        showEmptyOrderText();
    }

    // Metode untuk menampilkan teks "Histori Pesanan Kosong" jika list pesanan kosong
    private void showEmptyOrderText() {
        if (orderItems.isEmpty()) {
            pesananTextView.setVisibility(View.VISIBLE);
        } else {
            pesananTextView.setVisibility(View.GONE);
        }
    }
}
