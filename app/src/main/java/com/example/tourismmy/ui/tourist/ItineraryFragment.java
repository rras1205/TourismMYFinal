package com.example.tourismmy.ui.tourist;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tourismmy.R;
import com.example.tourismmy.model.ItineraryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItineraryFragment extends Fragment {

    private Spinner spinnerInterest;
    private Button btnGenerate, btnSavePlan, btnViewSaved;
    private RecyclerView recyclerView;
    private FloatingActionButton fabAdd;
    private ItineraryAdapter adapter;
    private List<ItineraryItem> currentPlans = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_itinerary, container, false);

        spinnerInterest = view.findViewById(R.id.spinnerInterest);
        btnGenerate = view.findViewById(R.id.btnGenerate);
        btnSavePlan = view.findViewById(R.id.btnSavePlan);
        btnViewSaved = view.findViewById(R.id.btnViewSaved);
        recyclerView = view.findViewById(R.id.recyclerItinerary);
        fabAdd = view.findViewById(R.id.fabAdd);

        // Setup Spinner
        String[] interests = {"Nature", "Food", "Culture", "Shopping"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, interests);
        spinnerInterest.setAdapter(spinnerAdapter);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ItineraryAdapter(currentPlans);
        recyclerView.setAdapter(adapter);

        btnGenerate.setOnClickListener(v -> generateItinerary());
        btnSavePlan.setOnClickListener(v -> savePlan());

        // Manual Add
        fabAdd.setOnClickListener(v -> showAddDialog());

        // Go to View Saved Plans
        btnViewSaved.setOnClickListener(v -> startActivity(new Intent(getContext(), MyPlansActivity.class)));

        return view;
    }

    private void generateItinerary() {
        String selectedInterest = spinnerInterest.getSelectedItem().toString();
        currentPlans.clear();

        if (selectedInterest.equals("Nature")) {
            currentPlans.add(new ItineraryItem("08:00 AM", "Hiking at Broga Hill", "Semenyih"));
            currentPlans.add(new ItineraryItem("02:00 PM", "Batu Caves Visit", "Gombak"));
        } else if (selectedInterest.equals("Food")) {
            currentPlans.add(new ItineraryItem("09:00 AM", "Nasi Lemak", "Village Park"));
            currentPlans.add(new ItineraryItem("08:00 PM", "Street Food", "Jalan Alor"));
        } else {
            currentPlans.add(new ItineraryItem("10:00 AM", "Pavilion Mall", "Bukit Bintang"));
        }

        adapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "Plan Generated! Click 'Save Plan' to keep it.", Toast.LENGTH_SHORT).show();
    }

    private void savePlan() {
        if (currentPlans.isEmpty()) {
            Toast.makeText(getContext(), "Generate or add items first!", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getUid();
        String interest = spinnerInterest.getSelectedItem().toString();

        // Check Duplicates
        db.collection("itineraries")
                .whereEqualTo("userId", uid)
                .whereEqualTo("interest", interest)
                .get()
                .addOnSuccessListener(snap -> {
                    if (!snap.isEmpty()) {
                        Toast.makeText(getContext(), "You already have a saved plan for " + interest, Toast.LENGTH_SHORT).show();
                    } else {
                        Map<String, Object> map = new HashMap<>();
                        map.put("userId", uid);
                        map.put("interest", interest);
                        map.put("items", currentPlans);

                        db.collection("itineraries").add(map)
                                .addOnSuccessListener(doc -> Toast.makeText(getContext(), "Plan Saved to Cloud!", Toast.LENGTH_SHORT).show());
                    }
                });
    }

    private void showAddDialog() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText etTime = new EditText(getContext()); etTime.setHint("Time (e.g. 10:00 AM)");
        final EditText etActivity = new EditText(getContext()); etActivity.setHint("Activity Name");
        final EditText etLoc = new EditText(getContext()); etLoc.setHint("Location");

        layout.addView(etTime); layout.addView(etActivity); layout.addView(etLoc);

        new AlertDialog.Builder(getContext())
                .setTitle("Add Custom Item")
                .setView(layout)
                .setPositiveButton("Add", (d, w) -> {
                    currentPlans.add(new ItineraryItem(etTime.getText().toString(), etActivity.getText().toString(), etLoc.getText().toString()));
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}