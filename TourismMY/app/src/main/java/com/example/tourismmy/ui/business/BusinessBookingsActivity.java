package com.example.tourismmy.ui.business;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tourismmy.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class BusinessBookingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_bookings);

        // Setup Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Incoming Bookings");
        }

        RecyclerView rv = findViewById(R.id.recyclerBookings);
        rv.setLayoutManager(new LinearLayoutManager(this));

        FirebaseFirestore.getInstance().collection("bookings").get()
                .addOnSuccessListener(snapshots -> {
                    List<DocumentSnapshot> bookings = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        bookings.add(doc);
                    }
                    rv.setAdapter(new BookingAdapter(bookings));
                });
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }

    class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.VH> {
        List<DocumentSnapshot> list;
        BookingAdapter(List<DocumentSnapshot> l) { list = l; }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_business_booking, p, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(@NonNull VH holder, int i) {
            DocumentSnapshot doc = list.get(i);

            // 1. Set Booking Info
            holder.tvService.setText(doc.getString("serviceName"));
            String price = doc.getString("price");
            holder.tvPrice.setText(price != null ? "Paid: RM " + price : "Paid: RM 0.00");

            // 2. Fetch User Details (Nested Query)
            String userId = doc.getString("userId");
            if (userId != null) {
                FirebaseFirestore.getInstance().collection("users").document(userId).get()
                        .addOnSuccessListener(userDoc -> {
                            if (userDoc.exists()) {
                                String name = userDoc.getString("name");
                                String email = userDoc.getString("email");
                                holder.tvUser.setText("Booked by: " + name + "\n(" + email + ")");
                            }
                        });
            }
        }

        @Override public int getItemCount() { return list.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView tvService, tvPrice, tvUser;
            VH(View v) {
                super(v);
                tvService = v.findViewById(R.id.tvServiceName);
                tvPrice = v.findViewById(R.id.tvPrice);
                tvUser = v.findViewById(R.id.tvUserDetails);
            }
        }
    }
}