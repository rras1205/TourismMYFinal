package com.example.tourismmy.ui.business;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tourismmy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessManageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private ManageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_manage);

        // Setup Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Manage Listings");
        }

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerManage);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadListings();
    }

    private void loadListings() {
        String myId = FirebaseAuth.getInstance().getUid();
        db.collection("listings").whereEqualTo("ownerId", myId).get()
                .addOnSuccessListener(snapshots -> {
                    List<Listing> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        list.add(new Listing(doc.getId(), doc.getString("name"), doc.getString("price"), doc.getString("timing")));
                    }
                    adapter = new ManageAdapter(list);
                    recyclerView.setAdapter(adapter);
                });
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }

    // --- ADAPTER ---
    class ManageAdapter extends RecyclerView.Adapter<ManageAdapter.VH> {
        List<Listing> list;
        ManageAdapter(List<Listing> l) { list = l; }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            View v = LayoutInflater.from(p.getContext()).inflate(android.R.layout.simple_list_item_2, p, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(@NonNull VH h, int i) {
            Listing item = list.get(i);
            h.t1.setText(item.name);
            h.t2.setText("RM " + item.price + " | " + item.timing + " (Tap to Edit)");

            h.itemView.setOnClickListener(v -> showOptionsDialog(item));
        }

        @Override public int getItemCount() { return list.size(); }
        class VH extends RecyclerView.ViewHolder {
            TextView t1, t2;
            VH(View v) { super(v); t1=v.findViewById(android.R.id.text1); t2=v.findViewById(android.R.id.text2); }
        }
    }

    private void showOptionsDialog(Listing item) {
        CharSequence[] options = {"Edit Listing", "Delete Listing"};
        new AlertDialog.Builder(this).setItems(options, (dialog, which) -> {
            if (which == 0) showEditDialog(item);
            else deleteListing(item);
        }).show();
    }

    private void deleteListing(Listing item) {
        db.collection("listings").document(item.id).delete()
                .addOnSuccessListener(v -> {
                    Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                    loadListings(); // Refresh
                });
    }

    private void showEditDialog(Listing item) {
        // Create Inputs Programmatically to save creating XML
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        final EditText etPrice = new EditText(this);
        etPrice.setHint("New Price");
        etPrice.setText(item.price);
        layout.addView(etPrice);

        final EditText etTiming = new EditText(this);
        etTiming.setHint("New Timing");
        etTiming.setText(item.timing);
        layout.addView(etTiming);

        new AlertDialog.Builder(this)
                .setTitle("Edit " + item.name)
                .setView(layout)
                .setPositiveButton("Update", (d, w) -> {
                    db.collection("listings").document(item.id)
                            .update("price", etPrice.getText().toString(), "timing", etTiming.getText().toString())
                            .addOnSuccessListener(v -> {
                                Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
                                loadListings();
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    static class Listing {
        String id, name, price, timing;
        Listing(String id, String name, String price, String timing) {
            this.id=id; this.name=name; this.price=price; this.timing=timing;
        }
    }
}