package marlon.dev.bussing.ui.ticket;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.SimpleDateFormat;
import java.util.Locale;

import marlon.dev.bussing.R;

public class GenerateTicket extends AppCompatActivity {

    ImageView back;
    private ImageView qrCodeImage;
    private TextView fromText, toText, userNameText, bookingTime, bookingDate, passenger, discountText, fareText, ticketNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_generate_ticket);

        // Initialize UI
        qrCodeImage = findViewById(R.id.qrCode);
        fromText = findViewById(R.id.fromTicket);
        toText = findViewById(R.id.toWhereTicket);
        userNameText = findViewById(R.id.passengerName);
        bookingTime = findViewById(R.id.bookingTime);
        bookingDate = findViewById(R.id.bookingDate);
        passenger = findViewById(R.id.passenger);
        discountText = findViewById(R.id.discount);
        fareText = findViewById(R.id.total);
        ticketNo = findViewById(R.id.ticketNo);

        // Get data from Intent
        String from = getIntent().getStringExtra("from");
        String to = getIntent().getStringExtra("to");
        String userName = getIntent().getStringExtra("userName");
        String date = getIntent().getStringExtra("date");
        String dateTime = getIntent().getStringExtra("dateTime");
        String passengerType = getIntent().getStringExtra("passengerType");
        String discountAmount = getIntent().getStringExtra("discountAmount");
        String finalFare = getIntent().getStringExtra("price");
        String ticketNumber = getIntent().getStringExtra("ticketNumber");
        String ticketDetails = getIntent().getStringExtra("ticketDetails");

        // Format the date properly
        String formattedDate = formatDate(date);

        // Format passenger type (capitalize first letter)
        String formattedPassengerType = passengerType.substring(0, 1).toUpperCase() + passengerType.substring(1);

        // Set data in UI
        fromText.setText(from);
        toText.setText(to);
        userNameText.setText(userName);
        bookingDate.setText(formattedDate);
        bookingTime.setText(dateTime);
        passenger.setText(formattedPassengerType);
        discountText.setText("₱" + discountAmount);
        fareText.setText("₱" + finalFare);
        ticketNo.setText(ticketNumber);

        // Generate QR Code
        generateQRCode(ticketDetails);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(Color.parseColor("#143ac1"));
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        back = findViewById(R.id.backButton);
        back.setOnClickListener(view -> onBackPressed());
    }

    // Method to format date from "MM/dd/yyyy" to "ddMMMyy"
    private String formatDate(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("ddMMMyy", Locale.getDefault());
            return outputFormat.format(inputFormat.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
            return date; // Fallback to original format if parsing fails
        }
    }

    // Method to generate QR Code
    private void generateQRCode(String text) {
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        try {
            BitMatrix bitMatrix = new com.google.zxing.MultiFormatWriter().encode(
                    text, BarcodeFormat.QR_CODE, 400, 400);
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            qrCodeImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
