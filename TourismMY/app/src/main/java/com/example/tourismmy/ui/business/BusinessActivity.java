package com.example.tourismmy.ui.business;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tourismmy.R;
import com.example.tourismmy.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import com.example.tourismmy.ui.business.BusinessManageActivity;

public class BusinessActivity extends AppCompatActivity {

    private EditText etName, etDesc, etPrice, etTiming; // Added etTiming
    private Button btnUpload, btnLogout, btnViewBookings, btnManageListings;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);

        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.etServiceName);
        etDesc = findViewById(R.id.etServiceDesc);
        etPrice = findViewById(R.id.etServicePrice);
        etTiming = findViewById(R.id.etServiceTiming); // Bind new view

        btnUpload = findViewById(R.id.btnUpload);
        btnLogout = findViewById(R.id.btnLogoutBusiness);
        btnViewBookings = findViewById(R.id.btnViewBookings);
        btnManageListings = findViewById(R.id.btnManageListings);

        btnUpload.setOnClickListener(v -> uploadListing());

        btnViewBookings.setOnClickListener(v ->
                startActivity(new Intent(this, BusinessBookingsActivity.class)));

        btnManageListings.setOnClickListener(v ->
                startActivity(new Intent(this, BusinessManageActivity.class)));

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void uploadListing() {
        String name = etName.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        String timing = etTiming.getText().toString().trim(); // Get timing input

        if (name.isEmpty() || price.isEmpty() || timing.isEmpty()) {
            Toast.makeText(this, "Please fill required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getUid();

        // 1. CHECK FOR DUPLICATES FIRST
        db.collection("listings")
                .whereEqualTo("name", name)
                .whereEqualTo("ownerId", userId)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (!snapshots.isEmpty()) {
                        // Duplicate found!
                        Toast.makeText(this, "You already have a listing with this name!", Toast.LENGTH_SHORT).show();
                    } else {
                        // 2. NO DUPLICATE FOUND -> PROCEED TO SAVE
                        Map<String, Object> listing = new HashMap<>();
                        listing.put("name", name);
                        listing.put("description", desc);
                        listing.put("price", price);
                        listing.put("timing", timing); // Save timing
                        listing.put("ownerId", userId);

                        db.collection("listings").add(listing)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(this, "Service Published!", Toast.LENGTH_SHORT).show();
                                    // Clear fields
                                    etName.setText("");
                                    etDesc.setText("");
                                    etPrice.setText("");
                                    etTiming.setText("");
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Network Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}