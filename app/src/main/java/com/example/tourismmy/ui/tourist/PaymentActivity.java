package com.example.tourismmy.ui.tourist;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tourismmy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {

    // 1. Declare variables here so they can be seen by all methods
    private String serviceName;
    private String price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // 2. Initialize the variables
        price = getIntent().getStringExtra("SERVICE_PRICE");
        if (price == null) price = "0.00";

        serviceName = getIntent().getStringExtra("SERVICE_NAME");
        if (serviceName == null) serviceName = "Unknown Service";

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        EditText etCard = findViewById(R.id.etCardNum);
        Button btnPay = findViewById(R.id.btnPay);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        btnPay.setOnClickListener(v -> {
            if (etCard.getText().toString().length() < 12) {
                Toast.makeText(this, "Invalid Card", Toast.LENGTH_SHORT).show();
                return;
            }

            btnPay.setEnabled(false);
            btnPay.setText("Processing...");
            progressBar.setVisibility(View.VISIBLE);

            // SIMULATE PAYMENT DELAY, THEN SAVE TO DB
            new Handler().postDelayed(() -> saveBookingToFirestore(), 2000);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void saveBookingToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getUid();

        Map<String, Object> booking = new HashMap<>();
        booking.put("userId", userId);
        booking.put("serviceName", serviceName);

        // 3. Now this method can see 'price'
        booking.put("price", price);

        booking.put("status", "Paid");
        booking.put("timestamp", System.currentTimeMillis());

        db.collection("bookings").add(booking)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Payment Success! Booking Saved.", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving booking", Toast.LENGTH_SHORT).show());
    }
}