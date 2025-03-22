package marlon.dev.bussing.ui.wallet;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import marlon.dev.bussing.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserWallet extends AppCompatActivity {

    private TextView balanceText;
    private ImageView exit;
    private DatabaseReference userWalletRef, paymentRef;
    private String userId;
    private static final double DEFAULT_BALANCE = 1000.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_wallet);

        balanceText = findViewById(R.id.walletBalance);
        exit = findViewById(R.id.exitButton);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userWalletRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("walletBalance");
        paymentRef = FirebaseDatabase.getInstance().getReference("BussingPayments");

        fetchWalletBalance();

        exit.setOnClickListener(view -> onBackPressed());
    }

    private void fetchWalletBalance() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference walletRef = db.collection("UserWalletsCollection").document(userId);

        walletRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists() && documentSnapshot.contains("balance")) {
                double balance = documentSnapshot.getDouble("balance");
                balanceText.setText("₱" + balance);
            } else {
                // Correct way to set a field in Firestore
                Map<String, Object> balanceData = new HashMap<>();
                balanceData.put("balance", DEFAULT_BALANCE);
                walletRef.set(balanceData)
                        .addOnSuccessListener(aVoid -> balanceText.setText("₱" + DEFAULT_BALANCE))
                        .addOnFailureListener(e -> showSnackbar("Failed to initialize wallet balance"));
            }
        }).addOnFailureListener(e -> showSnackbar("Failed to load wallet balance"));
    }



    private void deductBalance(double amount) {
        userWalletRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double currentBalance = snapshot.getValue(Double.class);
                    if (currentBalance >= amount) {
                        double newBalance = currentBalance - amount;
                        userWalletRef.setValue(newBalance);

                        storeTransaction(-amount, "deduction");
                        balanceText.setText("₱" + newBalance);
                        showSnackbar("Payment Successful!");
                    } else {
                        showSnackbar("Insufficient Balance!");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                showSnackbar("Transaction failed!");
            }
        });
    }

    private void storeTransaction(double amount, String type) {
        String transactionId = paymentRef.push().getKey();
        String dateTime = new SimpleDateFormat("HH:mm ddMMMyy", Locale.getDefault()).format(new Date());

        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("amount", amount);
        transactionData.put("timestamp", dateTime);
        transactionData.put("type", type);

        paymentRef.child(userId).child("transactions").child(transactionId).setValue(transactionData);
    }


    private void storePaymentRecord(double amount) {
        String dateTime = new SimpleDateFormat("HH:mm ddMMMyy", Locale.getDefault()).format(new Date());

        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("userId", userId);
        paymentData.put("dateTime", dateTime);
        paymentData.put("amountPaid", amount);

        paymentRef.push().setValue(paymentData);
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}
