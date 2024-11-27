package marlon.dev.bussing.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

import marlon.dev.bussing.R;

public class FirebaseUIActivity extends AppCompatActivity {

    // Declare the ActivityResultLauncher to handle sign-in result
    private ActivityResultLauncher<Intent> signInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_ui);  // Set the layout file here

        // Initialize the ActivityResultLauncher
        signInLauncher = registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                    @Override
                    public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                        onSignInResult(result);
                    }
                }
        );

        // Set up the sign-in button
        Button signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignInFlow();
            }
        });
    }

    // Method to start sign-in flow
    private void startSignInFlow() {
        // Set up the authentication providers for the sign-in process
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build()  // For Google sign-in
                // Add other providers like Email, Phone, etc. if needed
        );

        // Build the sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();

        // Launch the sign-in intent
        signInLauncher.launch(signInIntent);
    }

    // Handle the sign-in result
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        // Check if sign-in was successful
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // Handle the authenticated user (navigate to the main activity, etc.)
        } else {
            // Handle sign-in failure
        }
    }
}

