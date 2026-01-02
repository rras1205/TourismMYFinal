package com.example.tourismmy.ui.tourist;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tourismmy.SplashActivity;
import com.example.tourismmy.ui.tourist.TouristBookingsActivity;

import androidx.fragment.app.Fragment;

import com.example.tourismmy.MainActivity;
import com.example.tourismmy.R;
import com.example.tourismmy.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail;
    private Button btnLogout, btnLang, btnMyBookings;
    private FirebaseFirestore db;
    private FirebaseAuth auth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tvName = view.findViewById(R.id.tvProfileName);
        tvEmail = view.findViewById(R.id.tvProfileEmail);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnLang = view.findViewById(R.id.btnChangeLang);
        btnMyBookings = view.findViewById(R.id.btnMyBookings);

        // 1. Load User Data
        loadUserData();

        btnMyBookings.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), TouristBookingsActivity.class));
        });

        // 2. Handle Logout
        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            // Clear back stack so user can't press "back" to return
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // 3. Handle Language Switch
        btnLang.setOnClickListener(v -> {
            // Toggle between English and Malay
            String currentLang = Locale.getDefault().getLanguage();
            if (currentLang.equals("ms")) {
                setLocale("en");
            } else {
                setLocale("ms");
            }
        });

        return view;
    }

    private void loadUserData() {
        String uid = auth.getUid();
        if (uid != null) {
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            tvName.setText(documentSnapshot.getString("name"));
                            tvEmail.setText(documentSnapshot.getString("email"));
                        }
                    });
        }
    }

    private void setLocale(String langCode) {
        Locale myLocale = new Locale(langCode);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();

        conf.setLocale(myLocale);
        res.updateConfiguration(conf, dm);

        // --- RESTART THE WHOLE APP FLOW ---
        // This ensures language applies to all Activities, not just the current one
        Intent refresh = new Intent(getActivity(), SplashActivity.class);

        // Clear the back stack so the user can't press "Back" to see the old language
        refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(refresh);
        getActivity().finish();
    }
}