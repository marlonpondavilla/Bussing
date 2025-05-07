package marlon.dev.bussing.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import marlon.dev.bussing.R;
import marlon.dev.bussing.ui.account.AccountFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<CardLists> cardLists = new ArrayList<>();
    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;

    private ShapeableImageView appBarProfile;
    private TextView appBarUsername;
    private TextView appBarEmail;
    private TextView userNameTextView;
    private Button book;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        cardAdapter = new CardAdapter(getContext(), cardLists);
        recyclerView.setAdapter(cardAdapter);

        TextView dateTextView = view.findViewById(R.id.dateTextView);
        String currentDate = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(new Date());
        dateTextView.setText(currentDate);

        appBarProfile = view.findViewById(R.id.appBarProfile);
        appBarUsername = view.findViewById(R.id.appbarUsername);
        appBarEmail = view.findViewById(R.id.appbarEmail);
        userNameTextView = view.findViewById(R.id.userName);
        book = view.findViewById(R.id.bookButton);
        swipeRefreshLayout = view.findViewById(R.id.swipe);

        swipeRefreshLayout.setOnRefreshListener(this);

        updateUserInfo();
        appBarProfile.setOnClickListener(this::showPopupMenu);

        fetchDataFromFireStore();

        if (book != null) {
            book.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_blue, null)));
        } else {
            Log.e("HomeFragment", "bookButton is null!");
        }

        return view;
    }

    @Override
    public void onRefresh() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                cardLists.clear();
                fetchDataFromFireStore();
                cardAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    private void fetchDataFromFireStore() {
        listenerRegistration = db.collection("HomeDocumentsCollection")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Firestore Error", error.getMessage());
                            return;
                        }

                        cardLists.clear();
                        for (DocumentChange doc : value.getDocumentChanges()) {
                            if (doc.getType() == DocumentChange.Type.ADDED ||
                                    doc.getType() == DocumentChange.Type.MODIFIED) {

                                String id = doc.getDocument().getId();
                                String arrivalPoint = doc.getDocument().getString("arrivalPoint");
                                String busNumber = "BUS" + doc.getDocument().getString("busNo");
                                String fromLocation = doc.getDocument().getString("startingPoint");
                                String currentLocation = doc.getDocument().getString("startingPoint");
                                String departureTime = doc.getDocument().getString("departureTime");
                                String driver = doc.getDocument().getString("busDriver");
                                String conductor = doc.getDocument().getString("busConductor");
                                String status = doc.getDocument().getString("status");

                                // Safe parsing of busCapacity
                                Object capacityObj = doc.getDocument().get("busCapacity");
                                int capacity = 0; // Default value in case of errors

                                if (capacityObj instanceof Number) {
                                    capacity = ((Number) capacityObj).intValue(); // Convert safely
                                } else if (capacityObj instanceof String) {
                                    try {
                                        capacity = Integer.parseInt((String) capacityObj);
                                    } catch (NumberFormatException e) {
                                        Log.e("Firestore Error", "Invalid busCapacity value: " + capacityObj);
                                    }
                                } else {
                                    Log.e("Firestore Error", "Unexpected busCapacity type: " + capacityObj);
                                }

                                cardLists.add(new CardLists(busNumber, fromLocation, arrivalPoint, R.drawable.bus_front,
                                        currentLocation, departureTime, driver, conductor, capacity, 0, status));
                            }
                        }
                        cardAdapter.notifyDataSetChanged();
                    }
                });
    }


    public void updateUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String username = user.getDisplayName();
            String email = user.getEmail();

            // Reference to the Realtime Database to get base64 image
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
            String userID = user.getUid();

            databaseReference.child(userID).child("profileImageBase64").get()
                    .addOnSuccessListener(snapshot -> {
                        String base64 = snapshot.getValue(String.class);
                        if (base64 != null && !base64.isEmpty()) {
                            loadBase64Image(base64, appBarProfile);
                        } else {
                            // Fallback if base64 image is not available
                            Glide.with(getContext())
                                    .load(R.drawable.default_user1)
                                    .circleCrop()
                                    .into(appBarProfile);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Glide.with(getContext())
                                .load(R.drawable.default_user1)
                                .circleCrop()
                                .into(appBarProfile);
                    });

            appBarUsername.setText(username != null ? username : "Username");
            appBarEmail.setText(email != null ? email : "username@gmail.com");

            if (username != null) {
                String firstName = username.split(" ")[0];
                userNameTextView.setText(firstName);
            } else {
                userNameTextView.setText("User");
            }
        }
    }

    private void loadBase64Image(String base64, ShapeableImageView imageView) {
        try {
            byte[] decodedBytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
            Bitmap decodedBitmap = android.graphics.BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            imageView.setImageBitmap(decodedBitmap);
        } catch (Exception e) {
            e.printStackTrace();
            imageView.setImageResource(R.drawable.default_user1); // fallback
        }
    }



    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.profile_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.check_account) {
                navigateToAccountFragment();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void navigateToAccountFragment() {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.homeFragment, new AccountFragment());
        transaction.addToBackStack(null);
        transaction.commit();

        if (getActivity() != null) {
            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav_view);
            bottomNavigationView.setSelectedItemId(R.id.navigation_account);
        }
    }
}