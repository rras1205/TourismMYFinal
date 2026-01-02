package com.example.tourismmy.ui.tourist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.tourismmy.R;

public class AssistanceFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_assistance, container, false);

        TextView tvWeather = view.findViewById(R.id.tvWeather);
        Button btnPolice = view.findViewById(R.id.btnPolice);
        Button btnAmbulance = view.findViewById(R.id.btnAmbulance);



        // Simulate Weather API Call
        tvWeather.setText("32°C \u2600 Sunny");

        // Call Feature
        btnPolice.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:999"));
            startActivity(intent);
        });

        btnAmbulance.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:999")); // Or 991 depending on region
            startActivity(intent);
        });

        return view;
    }
}