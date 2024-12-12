package marlon.dev.bussing.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import marlon.dev.bussing.ui.account.AccountFragment;

import java.util.ArrayList;

import marlon.dev.bussing.R;

public class HomeFragment extends Fragment {

    private ArrayList<CardLists> cardLists = new ArrayList<>();
    private RecyclerView recyclerView;
    private CardAdapter cardAdapter;

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

        // Find the clickable FrameLayout
        /*FrameLayout frameLayout = view.findViewById(R.id.frameLayout);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log or debug if this event is being triggered
                Log.d("HomeFragment", "AppBar clicked, navigating to AccountFragment");
                navigateToAccountFragment();
            }
        });*/

        return view;
    }


    // Method to add multiple card items to cardLists
    private void cardData() {
        cardLists.add(new CardLists("BUS01", "Bulacan", "Valenzuela", R.drawable.bus_img, "Obando", "09:30"));
        cardLists.add(new CardLists("BUS02", "Bocaue", "Trinoma", R.drawable.bus_img, "Meycauyan", "13:45"));
        cardLists.add(new CardLists("BUS03", "Quezon City", "Cubao", R.drawable.bus_img, "Caloocan", "18:00"));
        cardLists.add(new CardLists("BUS04", "Taguig", "BGC", R.drawable.bus_img, "Pasig", "12:25"));
        cardLists.add(new CardLists("BUS05", "Manila", "Intramuros", R.drawable.bus_img, "Makati", "10:30"));
    }

    // Method to navigate to the AccountFragment
    /*private void navigateToAccountFragment() {
        AccountFragment accountFragment = new AccountFragment();
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();

        //target fragment
        fragmentTransaction.replace(R.id.fragment_container, accountFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }*/


    // Remove binding references, since we're not using View Binding here
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
