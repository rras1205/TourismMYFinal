package com.example.tourismmy;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tourismmy.auth.LoginActivity;
import com.example.tourismmy.ui.business.BusinessActivity;
import com.example.tourismmy.ui.admin.AdminActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // No layout needed, just logic
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            // Check Role
            FirebaseFirestore.getInstance().collection("users")
                    .document(FirebaseAuth.getInstance().getUid())
                    .get()
                    .addOnSuccessListener(doc -> {
                        String role = doc.getString("role");
                        if ("business".equals(role)) {
                            startActivity(new Intent(this, BusinessActivity.class));
                        } else if ("admin".equals(role)) {
                            startActivity(new Intent(this, AdminActivity.class));
                        } else {
                            startActivity(new Intent(this, MainActivity.class));
                        }
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        // Fallback to login if error
                        startActivity(new Intent(this, LoginActivity.class));
                        finish();
                    });
        }
    }
}