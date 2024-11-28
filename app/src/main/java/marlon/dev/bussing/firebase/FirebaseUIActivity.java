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

        // Always show the sign-in UI regardless of the user's authentication state
        initializeSignIn();
    }

    // Start the sign-in process
    private void initializeSignIn() {
        // Set up the authentication providers for the sign-in process
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(), // Google Sign-In provider
                new AuthUI.IdpConfig.EmailBuilder().build(),  // Email/Password provider
                new AuthUI.IdpConfig.PhoneBuilder().build()   // Phone authentication provider
        );

        // Build the sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)  // Available sign-in providers
                .setIsSmartLockEnabled(false)      // Disable Smart Lock to force account selection
                .setLogo(R.drawable.ic_launcher_background)   // Optional: Add a custom logo
                .setTheme(R.style.Theme_Bussing)       // Optional: Use custom theme
                .build();

        // Launch the sign-in intent
        signInLauncher.launch(signInIntent);
    }

    // Handle the result from FirebaseUI sign-in
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                // User signed in successfully, navigate to MainActivity
                navigateToMainActivity();
            }
        } else {
            // Handle sign-in failure
            Toast.makeText(this, "Sign-in failed", Toast.LENGTH_SHORT).show();
        }
    }

    // Navigate to MainActivity
    private void navigateToMainActivity() {
        Intent intent = new Intent(FirebaseUIActivity.this, MainActivity.class);
        startActivity(intent);
        finish();  // Finish FirebaseUIActivity to prevent going back to it
    }
}
