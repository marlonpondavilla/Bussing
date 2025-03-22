package marlon.dev.bussing.ui.setup_account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import marlon.dev.bussing.MainActivity;
import marlon.dev.bussing.R;

public class SignIn extends AppCompatActivity {

    FirebaseAuth auth;
    GoogleSignInClient gsc;
    FirebaseDatabase database;
    TextView toSignUp;
    TextInputEditText email, password;
    Button gsignInBtn, loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        email = findViewById(R.id.emailTextInput);
        password = findViewById(R.id.passwordTextInput);
        gsignInBtn = findViewById(R.id.continueWithGoogleBtn);
        loginBtn = findViewById(R.id.loginButton);
        toSignUp = findViewById(R.id.toSignUp);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        gsignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gSignIn();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = email.getText().toString();
                String pass = password.getText().toString();

                if(user.isEmpty() || pass.isEmpty()){
                    Toast.makeText(SignIn.this, "Email and Password cannot be empty!", Toast.LENGTH_SHORT).show();
                }  else {
                    auth.signInWithEmailAndPassword(user, pass)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    if (firebaseUser != null) {
                                        ensureWalletExists(firebaseUser.getUid()); // Check or create wallet
                                    }

                                    Toast.makeText(SignIn.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("user_email", user);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(SignIn.this, "Invalid email or password!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        toSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, SignUp.class);
                startActivity(intent);
                finish();
            }
        });
    }

    int RC_SIGN_IN = 40;

    private void gSignIn() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            if (task != null) {
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    auth(account.getIdToken());
                } catch (ApiException e) {
                    Toast.makeText(SignIn.this, "Sign in was cancelled.",  Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void auth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();

                            if (user != null) {
                                ensureWalletExists(user.getUid()); // Check or create wallet
                            }

                            // Pass the required parameters to the constructor
                            User users = new User();
                            users.setUserId(user.getUid());
                            users.setName(user.getDisplayName());
                            users.setProfile(user.getPhotoUrl().toString());

                            // Save the user data to the Firebase database
                            database.getReference().child("Users").child(user.getUid()).setValue(users);

                            // Redirect to the homepage
                            Intent intent = new Intent(SignIn.this, MainActivity.class);
                            startActivity(intent);

                        } else {
                            Toast.makeText(SignIn.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void ensureWalletExists(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userWalletRef = db.collection("UserWalletsCollection").document(userId);

        userWalletRef.get().addOnSuccessListener(documentSnapshot -> {
            if (!documentSnapshot.exists()) {
                // Wallet doesn't exist, create one
                createWalletForUser(userId);
            } else {
                Log.d("Firestore", "Wallet already exists for user: " + userId);
            }
        }).addOnFailureListener(e -> Log.e("Firestore", "Failed to check wallet existence", e));
    }

    // Create a new wallet for the user
    private void createWalletForUser(String userId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userWalletRef = db.collection("UserWalletsCollection").document(userId);

        Map<String, Object> walletData = new HashMap<>();
        walletData.put("balance", 1000.0); // Initial balance

        userWalletRef.set(walletData)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Wallet created for user: " + userId))
                .addOnFailureListener(e -> Log.e("Firestore", "Failed to create wallet", e));
    }

}