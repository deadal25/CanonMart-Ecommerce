package com.example.canonmart.fragment;

import static android.accounts.AccountManager.KEY_PASSWORD;
import static android.content.Context.MODE_PRIVATE;
import static com.example.canonmart.db.UserDBHelper.KEY_USERNAME;
import static com.example.canonmart.db.UserDBHelper.TABLE_USERS;
import static com.example.canonmart.db.UserDBHelper.KEY_USERNAME;
import static com.example.canonmart.db.UserDBHelper.TABLE_USERS;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.canonmart.activity.LoginActivity;
import com.example.canonmart.db.UserDBHelper;
import com.example.canonmart.R;

public class SettingsFragment extends Fragment {

    private TextView tvUsername;
    private SwitchCompat switchTheme;
    private SharedPreferences sharedPreferences;
    private TextView saldoTextView;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_settings_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvUsername = view.findViewById(R.id.tvUsername);
        switchTheme = view.findViewById(R.id.switchTheme);
        saldoTextView = view.findViewById(R.id.saldo);

        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE);

        updateSaldoTextView();

        ImageButton topupButton = view.findViewById(R.id.topup);
        topupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Membuat AlertDialog Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Konfirmasi Top Up");

                // Inflate custom view with an EditText
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_topup, null);
                builder.setView(dialogView);

                final EditText inputAmount = dialogView.findViewById(R.id.inputAmount);

                // Menambahkan tombol positif (Ya)
                builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Ambil nilai input dari EditText
                        String input = inputAmount.getText().toString().trim();
                        if (!input.isEmpty()) {
                            try {
                                int topupAmount = Integer.parseInt(input);
                                if (topupAmount > 0) {
                                    // Update saldo
                                    int currentSaldo = getCurrentSaldo();
                                    currentSaldo += topupAmount;
                                    updateSaldo(currentSaldo);
                                    Toast.makeText(getContext(), "Top up berhasil, saldo Anda sekarang: $" + currentSaldo, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Jumlah top up harus lebih dari 0", Toast.LENGTH_SHORT).show();
                                }
                            } catch (NumberFormatException e) {
                                Toast.makeText(getContext(), "Masukkan jumlah yang valid", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Masukkan jumlah top up", Toast.LENGTH_SHORT).show();
                        }
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
        });

        // Handle top-up button click
//        ImageButton topupButton = view.findViewById(R.id.topup);
//        topupButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Update saldo
//                int currentSaldo = getCurrentSaldo();
//                currentSaldo += 100;
//                updateSaldo(currentSaldo);
//            }
//        });

        // Set the current username
        String username = getLoggedInUsername();
        tvUsername.setText(username);

        // Set the current theme state
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        switchTheme.setChecked(isDarkMode);

        // Handle theme switch change listener
        switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Enable Dark Mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    sharedPreferences.edit().putBoolean("dark_mode", true).apply();
                } else {
                    // Disable Dark Mode
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    sharedPreferences.edit().putBoolean("dark_mode", false).apply();
                }
            }
        });

        // Handle logout button click
        Button btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        // Handle change credentials button click
        Button btnChangeCredentials = view.findViewById(R.id.button2);
        btnChangeCredentials.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeCredentialsDialog();
            }
        });

        // Handle order button click
        Button button4 = view.findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to OrderFragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, new OrderFragment());
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    private void showChangeCredentialsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Ubah Username dan Password");

        // Inflate layout for dialog
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_credentials, (ViewGroup) getView(), false);

        // Get references to EditText fields in the dialog layout
        EditText editTextUsername = viewInflated.findViewById(R.id.editTextUsername);
        EditText editTextPassword = viewInflated.findViewById(R.id.editTextPassword);

        // Set current username in the EditText
        editTextUsername.setText(getLoggedInUsername());

        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Show confirmation dialog before saving
                AlertDialog.Builder saveConfirmationBuilder = new AlertDialog.Builder(requireContext());
                saveConfirmationBuilder.setTitle("Konfirmasi Simpan");
                saveConfirmationBuilder.setMessage("Apakah Anda yakin ingin menyimpan perubahan?");
                saveConfirmationBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newUsername = editTextUsername.getText().toString();
                        String newPassword = editTextPassword.getText().toString();
                        updateCredentials(newUsername, newPassword);
                        Toast.makeText(getContext(), "Perubahan berhasil.", Toast.LENGTH_SHORT).show();
                    }
                });
                saveConfirmationBuilder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Toast.makeText(getContext(), "Perubahan dibatalkan.", Toast.LENGTH_SHORT).show();
                    }
                });
                saveConfirmationBuilder.show();
            }
        });

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Toast.makeText(getContext(), "Perubahan dibatalkan.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    private void updateCredentials(String newUsername, String newPassword) {
        UserDBHelper dbHelper = new UserDBHelper(requireContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, newUsername);
        values.put(KEY_PASSWORD, newPassword);

        String selection = KEY_USERNAME + " = ?";
        String[] selectionArgs = {getLoggedInUsername()};

        db.update(TABLE_USERS, values, selection, selectionArgs);

        // Update the username in SharedPreferences
        sharedPreferences.edit().putString(KEY_USERNAME, newUsername).apply();

        // Update the displayed username
        tvUsername.setText(newUsername);
    }

    private String getLoggedInUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    private void logout() {
        // Display confirmation dialog before logout
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Konfirmasi Keluar");
        builder.setMessage("Apakah Anda yakin ingin keluar?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Clear certain SharedPreferences entries
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("username");
                editor.remove("last_opened_fragment");
                editor.apply();

                // Navigate back to the login screen
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancel logout and dismiss dialog
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private int getCurrentSaldo() {
        // Retrieve current saldo from SharedPreferences
        return sharedPreferences.getInt("saldo", 0);
    }

    private void updateSaldo(int newSaldo) {
        // Save updated saldo to SharedPreferences
        sharedPreferences.edit().putInt("saldo", newSaldo).apply();
        // Update saldo TextView
        updateSaldoTextView();
    }

    private void updateSaldoTextView() {
        // Retrieve current saldo from SharedPreferences and display it in TextView
        double saldo = getCurrentSaldo();
        String formattedPrice = String.format("Saldo: $%.2f", saldo);
        saldoTextView.setText(formattedPrice);
    }


//    @Override
//    public void onCheckoutSuccess(double totalPrice) {
//        int currentSaldo = getCurrentSaldo();
//        int newSaldo = currentSaldo - (int) totalPrice; // Kurangi saldo dengan total harga belanja
//        updateSaldo(newSaldo); // Update saldo di SharedPreferences
//
//        // Tampilkan dialog konfirmasi pembelian
//        showCheckoutConfirmationDialog(totalPrice);
//    }


//    private void showCheckoutConfirmationDialog(double totalPrice) {
//        new AlertDialog.Builder(getContext())
//                .setTitle("Konfirmasi Pembelian")
//                .setMessage("Apakah Anda ingin melanjutkan pembelian dengan total $" + totalPrice + "?")
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Lanjutkan dengan proses checkout di sini
//                        Toast.makeText(getContext(), "Checkout berhasil.", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Batalkan checkout
//                        Toast.makeText(getContext(), "Checkout dibatalkan.", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show();
//    }

//    private void updateSaldoAfterCheckout(double totalPrice) {
//        // Mendapatkan saldo dari SharedPreferences
//        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", MODE_PRIVATE);
//        int currentSaldo = sharedPreferences.getInt("saldo", 0);
//
//        // Mengurangi saldo dengan total harga pembelian
//        int newSaldo = currentSaldo - (int) totalPrice;
//
//        // Menyimpan saldo yang baru ke dalam SharedPreferences
//        sharedPreferences.edit().putInt("saldo", newSaldo).apply();
//
//        // Memperbarui tampilan saldo di fragment
//        updateSaldoTextView();
//    }


}

