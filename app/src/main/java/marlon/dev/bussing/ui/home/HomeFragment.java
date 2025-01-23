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
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    private TextView appBarUsername;
    private TextView appBarEmail;
    private TextView userNameTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

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

        // Get the current date in desired format
        String currentDate = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(new Date());

        // Set the current date in the TextView
        dateTextView.setText(currentDate);

        // Initialize ImageView and TextViews for the app bar profile
        appBarProfile = view.findViewById(R.id.appBarProfile);
        appBarUsername = view.findViewById(R.id.appbarUsername);
        appBarEmail = view.findViewById(R.id.appbarEmail);

        // Initialize the userName TextView (for the "username" section in the home background)
        userNameTextView = view.findViewById(R.id.userName);

        // Update the AppBar and the home background with user info
        updateUserInfo();

        // onclick listener for the profile image to show the dropdown menu
        appBarProfile.setOnClickListener(v -> showPopupMenu(v));

        return view;
    }

    // Method to add multiple card items to cardLists
    private void cardData() {
        cardLists.add(new CardLists("BUS01", "Bulacan", "Valenzuela", R.drawable.bus_front, "Obando", "09:30", "Son Goku", "Vegeta", 20));
        cardLists.add(new CardLists("BUS02", "Bocaue", "Trinoma", R.drawable.bus_front, "Meycauyan", "13:45", "Monkey D. Luffy", "Roronoa Zoro", 10));
        cardLists.add(new CardLists("BUS03", "Quezon City", "Cubao", R.drawable.bus_front, "Caloocan", "18:00", "Uzumaki Naruto", "Uchiha Sasuke", 65));
        cardLists.add(new CardLists("BUS04", "Taguig", "BGC", R.drawable.bus_front, "Pasig", "12:25", "Gon Freecss", "Killua Zoldyck", 48));
        cardLists.add(new CardLists("BUS05", "Manila", "Intramuros", R.drawable.bus_front, "Makati", "10:30", "Itadori Yuji", "Fushiguro Megumi", 89));
    }

    // Method to update the AppBar and home background with user info (username only)
    private void updateUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String username = user.getDisplayName();  // Get the display name
            String email = user.getEmail();  // Get the email
            String photoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;  // Get the photo URL

            if (username != null) {
                // Split the username by spaces and take the first word
                String firstName = username.split(" ")[0];  // This will get the first word of the full name
                userNameTextView.setText(firstName);
            } else {
                userNameTextView.setText("User");
            }

            // Update the AppBar views
            appBarUsername.setText(username != null ? username : "Username");
            appBarEmail.setText(email != null ? email : "username@gmail.com");

            // If a photo URL exists, load it into the ImageView using Glide
            if (photoUrl != null) {
                Glide.with(getContext())
                        .load(photoUrl)
                        .circleCrop()
                        .into(appBarProfile);
            } else {
                // If no photo URL exists, use a default placeholder image
                Glide.with(getContext())
                        .load(R.drawable.user_img)
                        .circleCrop()
                        .into(appBarProfile);
            }
        } else {
            // If the user is not signed in, set default values
            userNameTextView.setText("User");
            appBarUsername.setText("Username");
            appBarEmail.setText("username@gmail.com");
            appBarProfile.setImageResource(R.drawable.user_img);
        }
    }

    // Method to show the PopupMenu when the profile image is clicked
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

    // Navigate to AccountFragment when "Check Account" is clicked
    private void navigateToAccountFragment() {
        // Using requireActivity() to ensure the activity is available
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

        // Replace the current fragment with AccountFragment
        Fragment accountFragment = requireActivity().getSupportFragmentManager().findFragmentByTag(AccountFragment.class.getName());
        if (accountFragment != null) {
            transaction.remove(accountFragment);
        }

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}