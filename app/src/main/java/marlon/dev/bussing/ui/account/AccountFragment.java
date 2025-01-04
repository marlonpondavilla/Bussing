package marlon.dev.bussing.ui.account;

import static android.app.Activity.RESULT_OK;

import androidx.activity.result.ActivityResultLauncher;
import androidx.lifecycle.ViewModelProvider;

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
import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;

import marlon.dev.bussing.R;
import marlon.dev.bussing.databinding.FragmentAccountBinding;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private AccountViewModel accountViewModel;
    private ActivityResultLauncher<Intent> signInLauncher;

    // AppBar views
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

        try {
            // Initialize the AppBar views here, after the layout is inflated
            appBarProfile = requireActivity().findViewById(R.id.appBarProfile);
            appBarUsername = requireActivity().findViewById(R.id.appbarUsername);
            appBarEmail = requireActivity().findViewById(R.id.appbarEmail);
        } catch (Exception e) {
            Log.e("AccountFragment", "AppBar views not found in activity layout", e);
        }

        final Button signOutButton = binding.signOutButton;
        final Button switchAccountButton = binding.switchAccountButton;

        // Observe the sign-in status
        accountViewModel.isSignedIn().observe(getViewLifecycleOwner(), isSignedIn -> {
            if (isSignedIn) {
                signOutButton.setVisibility(View.VISIBLE);
                updateUserInfo();
            } else {
                signOutButton.setVisibility(View.GONE);
                resetUserInfo();
            }
        });

        signOutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "Signed out", Toast.LENGTH_SHORT).show();
            startFirebaseUISignIn();
        });

        switchAccountButton.setOnClickListener(v -> startFirebaseUISignIn());
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
                Toast.makeText(getContext(), "Signed in successfully!", Toast.LENGTH_SHORT).show();
                updateUserInfo();
            }
        } else {
            Toast.makeText(getContext(), "Sign-in failed", Toast.LENGTH_SHORT).show();
        }
    }

    // Update user information on the UI, both in the Fragment and the AppBar
    private void updateUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || binding == null) return;

        String username = user.getDisplayName();
        String email = user.getEmail();
        String photoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;

        // Update user info in the main content
        binding.username.setText(username != null ? username : "Username");
        binding.userEmail.setText(email != null ? email : "username@gmail.com");
        if (photoUrl != null) {
            Glide.with(this)
                    .load(photoUrl)
                    .circleCrop()
                    .into(binding.userProfile);
        } else {
            Glide.with(this)
                    .load(R.drawable.user_img)
                    .circleCrop()
                    .into(binding.userProfile);
        }

        // Update AppBar views safely
        if (appBarUsername != null && appBarEmail != null && appBarProfile != null) {
            appBarUsername.setText(username != null ? username : "Username");
            appBarEmail.setText(email != null ? email : "username@gmail.com");
            if (photoUrl != null) {
                Glide.with(this)
                        .load(photoUrl)
                        .circleCrop()
                        .into(appBarProfile);
            } else {
                Glide.with(this)
                        .load(R.drawable.user_img)
                        .circleCrop()
                        .into(appBarProfile);
            }
        }
    }

    // Reset user information when signed out
    private void resetUserInfo() {
        if (binding == null) return;

        binding.username.setText("Username");
        binding.userEmail.setText("username@gmail.com");
        binding.userProfile.setImageResource(R.drawable.user_img);

        if (appBarUsername != null) appBarUsername.setText("Username");
        if (appBarEmail != null) appBarEmail.setText("username@gmail.com");
        if (appBarProfile != null) appBarProfile.setImageResource(R.drawable.user_img);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove observers and set binding to null
        accountViewModel.isSignedIn().removeObservers(getViewLifecycleOwner());
        binding = null;
    }
}
