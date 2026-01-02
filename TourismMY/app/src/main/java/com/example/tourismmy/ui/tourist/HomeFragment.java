package com.example.tourismmy.ui.tourist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tourismmy.R;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView rv = view.findViewById(R.id.recyclerAttractions);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        List<AttractionAdapter.Attraction> places = new ArrayList<>();

        // 1. Add some default famous places (Hardcoded)
        // Added "80.00" and "0.00" as the price argument
        places.add(new AttractionAdapter.Attraction("Petronas Twin Towers", "Iconic skyscrapers in KL", "80.00", "geo:3.1579,101.7116?q=Petronas+Twin+Towers"));
        places.add(new AttractionAdapter.Attraction("Batu Caves", "Limestone hill with temples", "0.00", "geo:3.2379,101.6840?q=Batu+Caves"));

        // 2. Fetch REAL services from Firestore (Uploaded by Business Owners)
        FirebaseFirestore.getInstance().collection("listings").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        String description = doc.getString("description"); // Clean description
                        String price = doc.getString("price"); // Get price separately

                        // We use a generic search query for the map since businesses didn't upload coords
                        String geo = "geo:0,0?q=" + name + " Malaysia";

                        // Pass all 4 arguments: Name, Desc, Price, Geo
                        places.add(new AttractionAdapter.Attraction(name, description, price, geo));
                    }
                    // Update Adapter
                    rv.setAdapter(new AttractionAdapter(places));
                });

        return view;
    }
}