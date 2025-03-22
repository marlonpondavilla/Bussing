package marlon.dev.bussing.ui.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import marlon.dev.bussing.R;

public class WelcomeWallet extends AppCompatActivity {

    TextInputEditText password;
    TextView name;
    MaterialButton continueBtn;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_wallet);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name = findViewById(R.id.userName);
        password = findViewById(R.id.passwordTextInput);
        continueBtn = findViewById(R.id.continueButton);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String userName = user.getDisplayName();
            name.setText(userName != null && !userName.isEmpty() ? userName : "User");
        } else {
            name.setText("Not logged in");
        }

        continueBtn.setOnClickListener(view -> verifyUser());
    }

    private void verifyUser() {
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            showSnackbar("User not logged in!");
            return;
        }

        boolean isGoogleUser = false;
        if (user.getProviderData().size() > 1) {
            String provider = user.getProviderData().get(1).getProviderId();
            isGoogleUser = provider.equals("google.com");
        }

        if (isGoogleUser) {
            // Re-authenticate using Google credentials
            reauthenticateWithGoogle(user);
        } else {
            // Re-authenticate using email & password
            verifyPassword(user);
        }
    }

    private void verifyPassword(FirebaseUser user) {
        String enteredPassword = password.getText().toString().trim();

        if (enteredPassword.isEmpty()) {
            password.setError("Enter your password");
            return;
        }

        auth.signInWithEmailAndPassword(user.getEmail(), enteredPassword)
                .addOnSuccessListener(authResult -> {
                    showSnackbar("Access Granted!");
                    navigateToWallet();
                })
                .addOnFailureListener(e -> {
                    password.setError("Incorrect password");
                    showSnackbar("Invalid password!");
                });
    }

    private void reauthenticateWithGoogle(FirebaseUser user) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            String idToken = account.getIdToken();

            if (idToken != null) {
                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                user.reauthenticate(credential)
                        .addOnSuccessListener(unused -> {
                            showSnackbar("Google re-authentication successful!");
                            navigateToWallet();
                        })
                        .addOnFailureListener(e -> {
                            showSnackbar("Google re-authentication failed!");
                        });
            } else {
                showSnackbar("Google ID token is null. Please sign in again.");
            }
        } else {
            showSnackbar("No Google account found. Please sign in again.");
        }
    }

    private void navigateToWallet() {
        Intent intent = new Intent(WelcomeWallet.this, UserWallet.class);
        startActivity(intent);
        finish(); // Close WelcomeWallet to prevent going back
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}
