package marlon.dev.bussing.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.List;

import marlon.dev.bussing.MainActivity;
import marlon.dev.bussing.R;

public class FirebaseUIActivity extends AppCompatActivity {

    // Declare the ActivityResultLauncher to handle sign-in result
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui);

        // Initialize the ActivityResultLauncher for sign-in
        signInLauncher = registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                    @Override
                    public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                        onSignInResult(result);
                    }
                }
        );

        // Check if the user is already signed in, and navigate accordingly
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            navigateToMainActivity();
        } else {
            initializeSignIn();
        }
    }

    // Start the sign-in process
    private void initializeSignIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build()
        );

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(true)
                .setLogo(R.drawable.ic_launcher_background)
                .setTheme(R.style.Theme_Bussing)
                .build();

        signInLauncher.launch(signInIntent);
    }

    // Handle the result from FirebaseUI sign-in
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // Send these to the DB and fetch to account fragment
                String userName = user.getDisplayName();
                String userUiD = user.getUid();
                navigateToMainActivity();
            }
        } else {
            Toast.makeText(this, "Sign-in failed, firebase activity", Toast.LENGTH_SHORT).show();
        }
    }

    // Simplified method to navigate to the MainActivity
    private void navigateToMainActivity() {
        Intent intent = new Intent(FirebaseUIActivity.this, MainActivity.class);
        // Add flags to clear any previous activities from the back stack and prevent returning to this activity
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Sign-out button clicked
    public void signOutBtnClicked() {
        signOut();
    }

    // Handle sign-out process
    public void signOut() {
        FirebaseAuth.getInstance().signOut();  // Clear the current user's session

        // After sign-out, navigate to MainActivity
        navigateToMainActivity();
    }
}
