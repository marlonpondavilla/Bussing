package marlon.dev.bussing.ui.splash_welcome;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import marlon.dev.bussing.R;
import marlon.dev.bussing.firebase.FirebaseUIActivity;

public class WelcomePage extends AppCompatActivity {

    Button getStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome_page);

        getStarted = findViewById(R.id.getStartedButton);

        getStarted.setOnClickListener(v -> {
            //redirect to firebase ui login/signin
            Intent intent = new Intent(WelcomePage.this, FirebaseUIActivity.class);
            startActivity(intent);
        });
    }
}