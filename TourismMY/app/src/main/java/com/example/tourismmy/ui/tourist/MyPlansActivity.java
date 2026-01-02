package com.example.tourismmy.ui.tourist;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Map;

public class MyPlansActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plans);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Saved Plans");
        }

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerPlans);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadPlans();
    }

    private void loadPlans() {
        String uid = FirebaseAuth.getInstance().getUid();
        db.collection("itineraries").whereEqualTo("userId", uid).get()
                .addOnSuccessListener(snapshots -> {
                    List<DocumentSnapshot> list = new ArrayList<>(snapshots.getDocuments());
                    recyclerView.setAdapter(new PlanAdapter(list));
                });
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }

    class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.VH> {
        List<DocumentSnapshot> list;
        PlanAdapter(List<DocumentSnapshot> l) { list = l; }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            View v = LayoutInflater.from(p.getContext()).inflate(android.R.layout.simple_list_item_2, p, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(@NonNull VH h, int i) {
            DocumentSnapshot doc = list.get(i);
            h.t1.setText("Plan: " + doc.getString("interest"));
            h.t2.setText("Tap to View Details | Long Press to Delete");

            // Click -> View Details
            h.itemView.setOnClickListener(v -> {
                List<Map<String, String>> items = (List<Map<String, String>>) doc.get("items");
                StringBuilder sb = new StringBuilder();
                if (items != null) {
                    for (Map<String, String> item : items) {
                        sb.append("• ").append(item.get("time")).append(": ")
                                .append(item.get("activity")).append("\n");
                    }
                }
                new AlertDialog.Builder(MyPlansActivity.this)
                        .setTitle(doc.getString("interest") + " Itinerary")
                        .setMessage(sb.toString())
                        .setPositiveButton("Close", null)
                        .show();
            });

            // Long Click -> Delete
            h.itemView.setOnLongClickListener(v -> {
                new AlertDialog.Builder(MyPlansActivity.this)
                        .setTitle("Delete Plan?")
                        .setPositiveButton("Delete", (d, w) -> {
                            db.collection("itineraries").document(doc.getId()).delete();
                            list.remove(i);
                            notifyItemRemoved(i);
                            Toast.makeText(MyPlansActivity.this, "Plan Deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                return true;
            });
        }

        @Override public int getItemCount() { return list.size(); }
        class VH extends RecyclerView.ViewHolder {
            TextView t1, t2;
            VH(View v) { super(v); t1=v.findViewById(android.R.id.text1); t2=v.findViewById(android.R.id.text2); }
        }
    }
}