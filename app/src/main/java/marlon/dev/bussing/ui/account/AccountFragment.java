package marlon.dev.bussing.ui.account;

import static android.app.Activity.RESULT_OK;

import androidx.activity.result.ActivityResultLauncher;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

import marlon.dev.bussing.R;
import marlon.dev.bussing.databinding.FragmentAccountBinding;

public class AccountFragment extends Fragment {

    private FragmentAccountBinding binding;
    private AccountViewModel accountViewModel;

    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);

        binding = FragmentAccountBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Button signOutButton = binding.signOutButton;

        // Observe the sign-in status to update UI
        accountViewModel.isSignedIn().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSignedIn) {
                if (isSignedIn) {
                    // If signed in, show the sign-out button
                    signOutButton.setVisibility(View.VISIBLE);
                } else {
                    // If not signed in, hide the sign-out button
                    signOutButton.setVisibility(View.GONE);
                }
            }
        });

        signOutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getContext(), "Signed out", Toast.LENGTH_SHORT).show();

            // After sign-out, show the FirebaseUI sign-in screen
            startFirebaseUISignIn();
        });

        // Initialize the ActivityResultLauncher for sign-in
        signInLauncher = registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                result -> onSignInResult(result)
        );

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Start the FirebaseUI sign-in flow
    private void startFirebaseUISignIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        // Build the sign-in intent for FirebaseUI
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build();

        signInLauncher.launch(signInIntent);
    }

    // Handle the result from FirebaseUI sign-in
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                Intent intent = new Intent(getContext(), marlon.dev.bussing.firebase.FirebaseUIActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        } else {
            Toast.makeText(getContext(), "Sign-in failed", Toast.LENGTH_SHORT).show();
        }
    }
}