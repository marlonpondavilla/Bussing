package marlon.dev.bussing.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.android.material.imageview.ShapeableImageView;

import marlon.dev.bussing.R;
import marlon.dev.bussing.ui.Profile.EditProfile;
import marlon.dev.bussing.ui.setup_account.SignIn;
import marlon.dev.bussing.ui.ticket.GenerateTicket;
import marlon.dev.bussing.ui.wallet.WelcomeWallet;

public class AccountFragment extends Fragment {

    private TextView usernameTextView;
    private TextView emailTextView;
    private ShapeableImageView profileImageView;
    private Button signOutButton, editProfile, ticketBtn, walletBtn;

    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private GoogleSignInClient googleSignInClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        // Initialize Google Sign-In Client
        googleSignInClient = GoogleSignIn.getClient(getContext(),
                new com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.client_id))
                        .requestEmail()
                        .build());

        // Bind views
        usernameTextView = root.findViewById(R.id.username);
        emailTextView = root.findViewById(R.id.userEmail);
        profileImageView = root.findViewById(R.id.userProfile);
        signOutButton = root.findViewById(R.id.signOutButton);
        editProfile = root.findViewById(R.id.editProfileButton);
        ticketBtn = root.findViewById(R.id.ticketButton);
        walletBtn = root.findViewById(R.id.walletButton);

        // Fetch latest ticket when button is clicked
        ticketBtn.setOnClickListener(view -> fetchLatestTicket());

        // Handle edit profile button click
        editProfile.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), EditProfile.class);
            startActivity(intent);
        });

        walletBtn.setOnClickListener(view -> {
            Intent intent = new Intent(requireContext(), WelcomeWallet.class);
            startActivity(intent);
        });

        // Display user info if logged in
        if (currentUser != null) {
            String fullName = currentUser.getDisplayName();
            usernameTextView.setText(fullName != null && !fullName.isEmpty() ? fullName : "User");
            emailTextView.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "No email available");

            // Load profile image
            Glide.with(this)
                    .load(currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl() : R.drawable.default_user1)
                    .circleCrop()
                    .into(profileImageView);
        } else {
            usernameTextView.setText("Not logged in");
            emailTextView.setText("");
            profileImageView.setImageResource(R.drawable.default_user1);
        }

        // Sign out button logic
        signOutButton.setOnClickListener(v -> signOut());

        return root;
    }

    // Fetch the latest ticket from Firestore and open GenerateTicket
    private void fetchLatestTicket() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        db.collection("tickets")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot latestTicket = queryDocumentSnapshots.getDocuments().get(0);

                        // Extract ticket details
                        String ticketNumber = latestTicket.getString("ticketNumber");
                        String from = latestTicket.getString("from");
                        String to = latestTicket.getString("to");
                        String dateTime = latestTicket.getString("dateTime");
                        String qrCodeImage = latestTicket.getString("qrCodeImage");

                        // Start GenerateTicket activity and pass ticket details
                        Intent intent = new Intent(getActivity(), GenerateTicket.class);
                        intent.putExtra("ticketNumber", ticketNumber);
                        intent.putExtra("from", from);
                        intent.putExtra("to", to);
                        intent.putExtra("dateTime", dateTime);
                        intent.putExtra("qrCodeImage", qrCodeImage);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), "No ticket found!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch ticket!", Toast.LENGTH_SHORT).show();
                });
    }

    // Sign out and redirect to SignInActivity
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        googleSignInClient.signOut().addOnCompleteListener(getActivity(), task -> {
            Intent intent = new Intent(getContext(), SignIn.class);
            startActivity(intent);
            getActivity().finish(); // Prevent returning to AccountFragment
            Toast.makeText(getContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();
        });
    }
}
