package marlon.dev.bussing.ui.status;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import marlon.dev.bussing.R;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.CardViewHolder> {

    private Context context;
    private ArrayList<StatusLists> statusListsArrayList;

    public StatusAdapter(Context context) {
        this.context = context;
        this.statusListsArrayList = new ArrayList<>();
        fetchBusSchedules();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public TextView busNum, dpTime, busFrom, busTo, price, busStatus;

        public CardViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.cardBusStatus);
            busNum = view.findViewById(R.id.busNo);
            dpTime = view.findViewById(R.id.dpTime);
            busFrom = view.findViewById(R.id.from);
            busTo = view.findViewById(R.id.toWhere);
            price = view.findViewById(R.id.price);
            busStatus = view.findViewById(R.id.busStatus);
        }
    }

    @NonNull
    @Override
    public StatusAdapter.CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_bus_status, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        StatusLists status = statusListsArrayList.get(position);

        // Add "BUS" before the bus number
        String formattedBusNo = status.getBusNo() != null ? "BUS" + status.getBusNo() : "N/A";

        // Add "DP:" before the departure time
        String formattedDpTime = status.getBusDP() != null ? "DP: " + status.getBusDP() : "DP: N/A";

        holder.busNum.setText(formattedBusNo);
        holder.dpTime.setText(formattedDpTime);
        holder.busFrom.setText(status.getBusFrom() != null ? status.getBusFrom() : "N/A");
        holder.busTo.setText(status.getBusTo() != null ? status.getBusTo() : "N/A");
        holder.price.setText(status.getBusPrice() != null ? "₱" + status.getBusPrice() : "₱0.00");
        holder.busStatus.setText(status.getStatus() != null ? status.getStatus() : "Unknown");

        // Change status color dynamically
        if ("Active".equalsIgnoreCase(status.getStatus())) {
            holder.busStatus.setTextColor(Color.parseColor("#03c03c"));
        } else if ("Inactive".equalsIgnoreCase(status.getStatus())) {
            holder.busStatus.setTextColor(Color.parseColor("#FF0000"));
        } else {
            holder.busStatus.setTextColor(Color.GRAY); // Default color
        }
    }


    @Override
    public int getItemCount() {
        return statusListsArrayList.size();
    }

    public void fetchBusSchedules() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("ScheduleDocumentsCollection")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("FirestoreError", "Error fetching data", error);
                        return;
                    }

                    if (value != null) {
                        statusListsArrayList.clear();
                        for (QueryDocumentSnapshot document : value) {
                            String busNo = document.getString("busNo");
                            String dpTime = document.getString("departureTime");
                            String busFrom = document.getString("from");
                            String busTo = document.getString("to");
                            String price = document.getString("price");
                            String status = document.getString("status");

                            // Debug log to check retrieved values
                            Log.d("FirestoreData", "Bus: " + busNo + ", " + busFrom + " -> " + busTo + ", Price: " + price + ", Status: " + status);

                            statusListsArrayList.add(new StatusLists(busNo, busFrom, busTo, dpTime, price, status));
                        }
                        notifyDataSetChanged();
                    }
                });
    }
}
