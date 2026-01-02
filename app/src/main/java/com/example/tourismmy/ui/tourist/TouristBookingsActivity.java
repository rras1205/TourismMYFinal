package com.example.tourismmy.ui.tourist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tourismmy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class TouristBookingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_bookings);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Bookings");
        }

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerBookings);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadBookings();
    }

    private void loadBookings() {
        String uid = FirebaseAuth.getInstance().getUid();
        db.collection("bookings").whereEqualTo("userId", uid).get()
                .addOnSuccessListener(snapshots -> {
                    List<DocumentSnapshot> list = new ArrayList<>(snapshots.getDocuments());
                    recyclerView.setAdapter(new Adapter(list));
                });
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }

    class Adapter extends RecyclerView.Adapter<Adapter.VH> {
        List<DocumentSnapshot> list;
        Adapter(List<DocumentSnapshot> l) { list = l; }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_tourist_booking, p, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(@NonNull VH h, int i) {
            DocumentSnapshot doc = list.get(i);
            h.name.setText(doc.getString("serviceName"));
            h.status.setText("Status: " + doc.getString("status"));

            h.btnCancel.setOnClickListener(v -> {
                db.collection("bookings").document(doc.getId()).delete()
                        .addOnSuccessListener(voidV -> {
                            Toast.makeText(TouristBookingsActivity.this, "Booking Cancelled & Refunded", Toast.LENGTH_SHORT).show();
                            list.remove(i);
                            notifyItemRemoved(i);
                            notifyItemRangeChanged(i, list.size());
                        });
            });
        }

        @Override public int getItemCount() { return list.size(); }
        class VH extends RecyclerView.ViewHolder {
            TextView name, status; Button btnCancel;
            VH(View v) { super(v); name=v.findViewById(R.id.tvServiceName); status=v.findViewById(R.id.tvStatus); btnCancel=v.findViewById(R.id.btnCancel); }
        }
    }
}