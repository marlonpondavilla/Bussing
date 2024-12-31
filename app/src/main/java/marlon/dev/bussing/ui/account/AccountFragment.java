package marlon.dev.bussing.ui.account;

import static android.app.Activity.RESULT_OK;

import androidx.activity.result.ActivityResultLauncher;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.bumptech.glide.Glide;  // Import Glide

import java.util.Arrays;
import java.util.List;

import marlon.dev.bussing.R;
import marlon.dev.bussing.databinding.FragmentAccountBinding;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private AccountViewModel accountViewModel;

    private ActivityResultLauncher<Intent> signInLauncher;

    // Access the AppBar views
    private ShapeableImageView appBarProfile;
    private TextView appBarUsername;
    private TextView appBarEmail;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize the ActivityResultLauncher for sign-in
        signInLauncher = registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                result -> onSignInResult(result)
        );

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the AppBar views here, after the layout is inflated
        appBarProfile = getActivity().findViewById(R.id.appBarProfile);
        appBarUsername = getActivity().findViewById(R.id.appbarUsername);
        appBarEmail = getActivity().findViewById(R.id.appbarEmail);

        final Button signOutButton = binding.signOutButton;
        final Button switchAccountButton = binding.switchAccountButton;

        // Observe the sign-in status
        accountViewModel.isSignedIn().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSignedIn) {
                if (isSignedIn) {
                    // If signed in, show the sign-out button
                    signOutButton.setVisibility(View.VISIBLE);
                    updateUserInfo(); // Update user info when signed in
                } else {
                    // If not signed in, hide the sign-out button
                    signOutButton.setVisibility(View.GONE);
                    // Optionally reset the profile details when signed out
                    binding.username.setText("Username");
                    binding.userEmail.setText("username@gmail.com");
                    binding.userProfile.setImageResource(R.drawable.user_img);
                    appBarUsername.setText("Username");
                    appBarEmail.setText("username@gmail.com");
                    appBarProfile.setImageResource(R.drawable.user_img); // Reset AppBar image
                }
            }
        });

        signOutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();  // Sign out the user
            Toast.makeText(getContext(), "Signed out", Toast.LENGTH_SHORT).show();
            startFirebaseUISignIn();
        });

        switchAccountButton.setOnClickListener(view1 -> startFirebaseUISignIn());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Start the FirebaseUI sign-in flow
    private void startFirebaseUISignIn() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.PhoneBuilder().build()
            );

            Intent signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setIsSmartLockEnabled(false)
                    .build();

            signInLauncher.launch(signInIntent);
        }
    }

    // Handle the result from FirebaseUI sign-in
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // Successfully signed in
                Toast.makeText(getContext(), "Signed in successfully!", Toast.LENGTH_SHORT).show();
                updateUserInfo();
            }
        } else {
            Toast.makeText(getContext(), "Sign-in failed, account fragment", Toast.LENGTH_SHORT).show();
        }
    }

    // Update user information on the UI, both in the Fragment and the AppBar
    private void updateUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String username = user.getDisplayName(); // User's display name (if available)
            String email = user.getEmail(); // User's email address
            String photoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null; // User's photo URL (if available)

            // Update user info in the main content
            binding.username.setText(username != null ? username : "Username");
            binding.userEmail.setText(email != null ? email : "username@gmail.com");
            if (photoUrl != null) {
                Glide.with(getContext())
                        .load(photoUrl)
                        .circleCrop()  // Optional: Circle crop the image for a rounded profile image
                        .into(binding.userProfile);  // Update profile image in Fragment
            } else {
                Glide.with(getContext())
                        .load(R.drawable.user_img) // Default placeholder image
                        .circleCrop()
                        .into(binding.userProfile);
            }

            // Update user info in the AppBar
            appBarUsername.setText(username != null ? username : "Username");
            appBarEmail.setText(email != null ? email : "username@gmail.com");
            if (photoUrl != null) {
                Glide.with(getContext())
                        .load(photoUrl)
                        .circleCrop()
                        .into(appBarProfile);
            } else {
                Glide.with(getContext())
                        .load(R.drawable.user_img)
                        .circleCrop()
                        .into(appBarProfile);
            }
        }
    }
}
