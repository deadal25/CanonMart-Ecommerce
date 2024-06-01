package com.example.canonmart.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.canonmart.db.UserDBHelper;
import com.example.canonmart.R;


public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                // Periksa validitas username dan password
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Silahkan mengisi Username dan Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (username.length() < 3) {
                    Toast.makeText(RegisterActivity.this, "Username harus lebih dari 3 karakter", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 3) {
                    Toast.makeText(RegisterActivity.this, "Password harus melebihi 3 karakter", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserDBHelper userDBHelper = new UserDBHelper(RegisterActivity.this);
                userDBHelper.addUser(username, password);

                Toast.makeText(RegisterActivity.this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show();

                // Pindah ke LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
