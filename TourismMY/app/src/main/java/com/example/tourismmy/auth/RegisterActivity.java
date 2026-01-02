package com.example.tourismmy.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tourismmy.MainActivity;
import com.example.tourismmy.R;
import com.example.tourismmy.model.User;
import com.example.tourismmy.ui.business.BusinessActivity; // Import Business Activity
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword;
    private RadioGroup radioGroupRole;
    private Button btnRegister;
    private TextView tvLoginLink;
    private CheckBox cbPrivacy;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind Views
        cbPrivacy = findViewById(R.id.cbPrivacy);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        radioGroupRole = findViewById(R.id.radioGroupRole);
        btnRegister = findViewById(R.id.btnRegister);
        tvLoginLink = findViewById(R.id.tvLoginLink);

        // Handle Register Click
        btnRegister.setOnClickListener(v -> registerUser());

        // Go to Login Page
        tvLoginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // This closes the current screen and goes back
        return true;
    }


    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 1. Check Privacy Checkbox
        if (!cbPrivacy.isChecked()) {
            Toast.makeText(this, "Please agree to the Privacy Policy", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Validate Inputs
        if (name.isEmpty() || email.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Please fill all fields (Pass min 6 chars)", Toast.LENGTH_SHORT).show();
            return;
        }

        // 3. Determine Role
        String role = "tourist"; // default
        if (radioGroupRole.getCheckedRadioButtonId() == R.id.rbBusiness) {
            role = "business";
        }

        // 4. Create User in Firebase Auth
        String finalRole = role;
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();

                    // 5. Save User Details to Firestore
                    User newUser = new User(uid, name, email, finalRole);

                    db.collection("users").document(uid).set(newUser)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Account Created!", Toast.LENGTH_SHORT).show();

                                // --- FIXED REDIRECTION LOGIC ---
                                if (finalRole.equals("business")) {
                                    // Go to Business Dashboard
                                    startActivity(new Intent(this, BusinessActivity.class));
                                } else {
                                    // Go to Tourist Dashboard
                                    startActivity(new Intent(this, MainActivity.class));
                                }
                                finish();
                                // -------------------------------
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Error saving data: " + e.getMessage(), Toast.LENGTH_SHORT).show());

                })
                .addOnFailureListener(e -> Toast.makeText(this, "Registration Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}