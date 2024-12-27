package marlon.dev.bussing.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;

import marlon.dev.bussing.ui.account.AccountFragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import marlon.dev.bussing.R;

public class HomeFragment extends Fragment {

    private ArrayList<CardLists> cardLists = new ArrayList<>();
    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;

    private ShapeableImageView appBarProfile;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Inflate the FrameLayout containing homeBackground
        View frameLayoutView = inflater.inflate(R.layout.home_background, container, false);

        // Initialize RecyclerView and set up Adapter
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Add data to cardLists if it's not already added
        if (cardLists.isEmpty()) {
            cardData();
        }

        // Set the adapter to the RecyclerView
        cardAdapter = new CardAdapter(getContext(), cardLists);
        recyclerView.setAdapter(cardAdapter);

        // Find the TextView for displaying the current date
        TextView dateTextView = view.findViewById(R.id.dateTextView);

        //get the current date in desired format
        String currentDate = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(new Date());

        // Set the current date in the TextView
        dateTextView.setText(currentDate);

        // Initialize ImageView for the app bar profile image
        appBarProfile = view.findViewById(R.id.appBarProfile);

        // Set up a click listener for the profile image to show the dropdown menu
        appBarProfile.setOnClickListener(v -> showPopupMenu(v));

        return view;
    }


    // Method to add multiple card items to cardLists
    private void cardData() {
        cardLists.add(new CardLists("BUS01", "Bulacan", "Valenzuela", R.drawable.bus_front, "Obando", "09:30"));
        cardLists.add(new CardLists("BUS02", "Bocaue", "Trinoma", R.drawable.bus_front, "Meycauyan", "13:45"));
        cardLists.add(new CardLists("BUS03", "Quezon City", "Cubao", R.drawable.bus_front, "Caloocan", "18:00"));
        cardLists.add(new CardLists("BUS04", "Taguig", "BGC", R.drawable.bus_front, "Pasig", "12:25"));
        cardLists.add(new CardLists("BUS05", "Manila", "Intramuros", R.drawable.bus_front, "Makati", "10:30"));
    }

    // Method to show the PopupMenu
    private void showPopupMenu(View view) {
        // Create a PopupMenu and link it to the profile
        PopupMenu popupMenu = new PopupMenu(getContext(), view);

        // Inflate the menu from a resource file
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.profile_menu, popupMenu.getMenu());

        // Set a listener to handle item clicks
        popupMenu.setOnMenuItemClickListener(item -> {
            // Handle the "Check Account" menu item click
            if (item.getItemId() == R.id.check_account) {
                navigateToAccountFragment();
                return true;
            }
            return false;
        });

        // Show the PopupMenu
        popupMenu.show();
    }

    private void navigateToAccountFragment() {
        // Using requireActivity() to ensure the activity is available
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

        // Replace the current fragment with AccountFragment
        AccountFragment accountFragment = new AccountFragment();
        transaction.replace(R.id.nav_host_fragment_activity_main, accountFragment);

        // Allows the user to navigate back
        transaction.addToBackStack(null);

        // Commit the transaction to perform the navigation
        transaction.commit();

        // Update the Bottom Navigation to reflect the selected fragment
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.nav_view);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_account);
        }
    }


    // Remove binding references, since we're not using View Binding here
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
