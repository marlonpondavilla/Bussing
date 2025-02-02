package marlon.dev.bussing.ui.splash_welcome;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import marlon.dev.bussing.MainActivity;
import marlon.dev.bussing.R;
import marlon.dev.bussing.firebase.FirebaseUIActivity;

public class WelcomePage extends AppCompatActivity {

    Button getStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);

        // Check if the user is already signed in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // If the user is signed in, navigate to MainActivity directly
            navigateToMainActivity();
        }
//        else {
//            // If the user is not signed in, show the welcome page
//            getStarted = findViewById(R.id.getStartedButton);
//
//            getStarted.setOnClickListener(v -> {
//                // Redirect to Firebase UI login/signin
//                Intent intent = new Intent(WelcomePage.this, FirebaseUIActivity.class);
//                startActivity(intent);
//            });
//        }
    }

    @Override
    public void onBackPressed() {
        // Don't allow going back to the welcome screen once the user proceeds
        finish();
        super.onBackPressed();
    }

    // Method to navigate to MainActivity
    public void navigateToMainActivity() {
        // Use the flags to clear the back stack and prevent going back to WelcomePage
        Intent intent = new Intent(WelcomePage.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
