package com.example.tourismmy.ui.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourismmy.R;
import com.example.tourismmy.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminAdapter adapter;
    private FirebaseFirestore db;
    private TextView tvUserCount, tvBookingCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        db = FirebaseFirestore.getInstance();
        tvUserCount = findViewById(R.id.tvUserCount);
        tvBookingCount = findViewById(R.id.tvBookingCount);
        recyclerView = findViewById(R.id.recyclerAdmin);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Logout Logic
        findViewById(R.id.btnAdminLogout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        loadStats();
        loadListings();
    }

    private void loadStats() {
        // Count Users
        db.collection("users").get()
                .addOnSuccessListener(snapshots ->
                        tvUserCount.setText(String.valueOf(snapshots.size())));

        // Count Bookings
        db.collection("bookings").get()
                .addOnSuccessListener(snapshots ->
                        tvBookingCount.setText(String.valueOf(snapshots.size())));
    }

    private void loadListings() {
        db.collection("listings").get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<Listing> list = new ArrayList<>();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                list.add(new Listing(doc.getId(), doc.getString("name"), doc.getString("price")));
            }
            adapter = new AdminAdapter(list);
            recyclerView.setAdapter(adapter);
        });
    }

    // --- Internal Adapter Class ---
    private class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.ViewHolder> {
        List<Listing> data;
        AdminAdapter(List<Listing> data) { this.data = data; }

        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Listing item = data.get(position);
            holder.text1.setText(item.name);
            holder.text2.setText("RM " + item.price);

            // DELETE CONFIRMATION DIALOG
            holder.itemView.setOnClickListener(v -> {
                new AlertDialog.Builder(AdminActivity.this)
                        .setTitle("Delete Listing?")
                        .setMessage("Are you sure you want to remove '" + item.name + "' permanently?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            // Perform Delete
                            db.collection("listings").document(item.id).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(AdminActivity.this, "Listing Deleted", Toast.LENGTH_SHORT).show();
                                        data.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, data.size());
                                    });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        @Override
        public int getItemCount() { return data.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView text1, text2;
            ViewHolder(View v) {
                super(v);
                text1 = v.findViewById(android.R.id.text1);
                text2 = v.findViewById(android.R.id.text2);
            }
        }
    }

    private static class Listing {
        String id, name, price;
        Listing(String id, String name, String price) { this.id = id; this.name = name; this.price = price; }
    }
}