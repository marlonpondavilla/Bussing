package marlon.dev.bussing.ui.ticket;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.icu.util.Calendar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import marlon.dev.bussing.R;

public class TicketFragment extends Fragment {

    private AutoCompleteTextView departureDropdown, arrivalDropdown, passengersDropdown;
    private EditText dateInput;
    private MaterialButton qrCodeButton;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ticket, container, false);

        departureDropdown = view.findViewById(R.id.departureDropDown);
        arrivalDropdown = view.findViewById(R.id.arrivalDropDown);
        passengersDropdown = view.findViewById(R.id.passengersDropDown);
        dateInput = view.findViewById(R.id.dateInput);
        qrCodeButton = view.findViewById(R.id.qrCodeButton);

        db = FirebaseFirestore.getInstance();

        Bundle bundle = getArguments();
        String fromLocation = bundle != null ? bundle.getString("from", "") : "";
        String toLocation = bundle != null ? bundle.getString("to", "") : "";

        loadRoutesFromFirestore(fromLocation, toLocation);

        ArrayAdapter<String> passengersAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.passengers)
        );
        passengersDropdown.setAdapter(passengersAdapter);

        dateInput.setOnClickListener(v -> showDatePickerDialog());

        qrCodeButton.setOnClickListener(v -> {
            if (!isFormValid()) {
                Snackbar.make(v, "Please complete all required fields before proceeding.", Snackbar.LENGTH_LONG).show();
                return;
            }

            String fromWhere = departureDropdown.getText().toString();
            String toWhere = arrivalDropdown.getText().toString();

            if (fromWhere.equalsIgnoreCase(toWhere)) {
                Snackbar.make(v, "Departure and Arrival locations cannot be the same.", Snackbar.LENGTH_LONG).show();
                return;
            }

            String from = departureDropdown.getText().toString();
            String to = arrivalDropdown.getText().toString();
            String date = dateInput.getText().toString();
            String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            String ticketNumber = generateTicketNumber();
            String passengerType = passengersDropdown.getText().toString().trim().toLowerCase();

            double basePrice = (from.equalsIgnoreCase("Bulacan") && to.equalsIgnoreCase("Cubao")) ? 105.0 : 120.0;
            double discountRate = 0.0;

            switch (passengerType) {
                case "student":
                    discountRate = 0.2;
                    break;
                case "senior":
                    discountRate = 0.3;
                    break;
                case "pwd":
                    discountRate = 0.4;
                    break;
            }

            double discountAmount = basePrice * discountRate;
            double finalPrice = basePrice - discountAmount;

            showPaymentDialog(finalPrice, () -> {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String formattedDateTime = sdf.format(new Date());

                String formattedPrice = String.format(Locale.getDefault(), "%.2f", finalPrice);
                String formattedDiscount = String.format(Locale.getDefault(), "%.2f", discountAmount);

                String ticketDetails = "From: " + from + "\nTo: " + to + "\nUser: " + userName +
                        "\nDate: " + date + "\nTime: " + formattedDateTime +
                        "\nPassenger: " + passengerType + "\nDiscount: ₱" + formattedDiscount +
                        "\nPrice: ₱" + formattedPrice + "\nTicket Number: " + ticketNumber;

                Intent intent = new Intent(getActivity(), GenerateTicket.class);
                intent.putExtra("from", from);
                intent.putExtra("to", to);
                intent.putExtra("userName", userName);
                intent.putExtra("date", date);
                intent.putExtra("dateTime", formattedDateTime);
                intent.putExtra("passengerType", passengerType);
                intent.putExtra("discountAmount", formattedDiscount);
                intent.putExtra("price", formattedPrice);
                intent.putExtra("ticketDetails", ticketDetails);
                intent.putExtra("ticketNumber", ticketNumber);

                saveTicketToFirestore(from, to, userName, date, formattedDateTime, passengerType, formattedDiscount, formattedPrice, ticketNumber);


                startActivity(intent);
            });
        });

        return view;
    }

    private void saveTicketToFirestore(String from, String to, String userName, String date, String dateTime,
                                       String passengerType, String discountAmount, String finalPrice, String ticketNumber) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> ticketData = new HashMap<>();
        ticketData.put("createdAt", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        ticketData.put("from", from);
        ticketData.put("to", to);
        ticketData.put("userName", userName);
        ticketData.put("bookingDate", date);
        ticketData.put("bookingTime", dateTime);
        ticketData.put("passenger", passengerType);
        ticketData.put("discount", discountAmount);
        ticketData.put("price", finalPrice);
        ticketData.put("ticketCode", ticketNumber);
        ticketData.put("uid", userId);

        FirebaseFirestore.getInstance()
                .collection("TicketGeneratedCollection")
                .add(ticketData)
                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Ticket saved: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.e("Firestore", "Error saving ticket", e));
    }

    private void showPaymentDialog(double amountToPay, Runnable onPaymentSuccess) {
        if (!isAdded() || getActivity() == null) {
            Log.e("PaymentDialog", "Fragment is not attached, cannot show dialog.");
            return;
        }

        getActivity().runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Enter Payment Amount");

            final EditText input = new EditText(getActivity());
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            input.setHint(String.format(Locale.getDefault(), "Amount to pay: ₱%.2f", amountToPay));
            input.setBackgroundResource(R.drawable.input_border);
            input.setPadding(40, 20, 20, 20);

            int margin = (int) (20 * getResources().getDisplayMetrics().density);
            LinearLayout layout = new LinearLayout(getActivity());
            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(margin, margin, margin, margin);
            input.setLayoutParams(params);

            layout.addView(input);
            builder.setView(layout);

            builder.setPositiveButton("Pay", (dialog, which) -> {
                String enteredAmountStr = input.getText().toString().trim();
                if (enteredAmountStr.isEmpty()) {
                    Snackbar.make(qrCodeButton, "Please enter an amount.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                double enteredAmount;
                try {
                    enteredAmount = Double.parseDouble(enteredAmountStr);
                } catch (NumberFormatException e) {
                    Snackbar.make(qrCodeButton, "Invalid amount entered.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (enteredAmount < amountToPay) {
                    Snackbar.make(qrCodeButton, "Insufficient amount. Please enter the correct amount.", Snackbar.LENGTH_LONG).show();
                } else {
                    processPayment(amountToPay, onPaymentSuccess);
                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }


    private void processPayment(double amountToPay, Runnable onPaymentSuccess) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference userWalletRef = db.collection("UserWalletsCollection").document(userId);

        Log.d("Firestore", "Starting transaction for user: " + userId);

        db.runTransaction(transaction -> {
            DocumentSnapshot walletSnapshot = transaction.get(userWalletRef);

            if (!walletSnapshot.exists()) {
                Log.e("Firestore", "Wallet not found!");
                throw new FirebaseFirestoreException("Wallet not found", FirebaseFirestoreException.Code.NOT_FOUND);
            }

            Double currentBalance = walletSnapshot.getDouble("balance");
            if (currentBalance == null) {
                Log.e("Firestore", "Balance field is missing!");
                throw new FirebaseFirestoreException("Balance field missing", FirebaseFirestoreException.Code.DATA_LOSS);
            }

            if (currentBalance < amountToPay) {
                Log.e("Firestore", "Insufficient balance! Current: " + currentBalance + ", Required: " + amountToPay);
                throw new FirebaseFirestoreException("Insufficient balance", FirebaseFirestoreException.Code.ABORTED);
            }

            double newBalance = currentBalance - amountToPay;
            Log.d("Firestore", "Updating balance. New balance: " + newBalance);
            transaction.update(userWalletRef, "balance", newBalance);

            return newBalance;
        }).addOnSuccessListener(newBalance -> {
            Log.d("Firestore", "Payment successful. Proceeding to save payment record...");
            savePaymentRecord(userId, amountToPay);
            onPaymentSuccess.run();  // Proceed to generate ticket
        }).addOnFailureListener(e -> {
            Log.e("Firestore", "Payment failed: " + e.getMessage(), e);
            Snackbar.make(qrCodeButton, e.getMessage(), Snackbar.LENGTH_LONG).show();
        });
    }


    private void savePaymentRecord(String userId, double amountPaid) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm ddMMMyy", Locale.getDefault());
        String dateTime = sdf.format(new Date());

        Map<String, Object> paymentDetails = new HashMap<>();
        paymentDetails.put("userId", userId);
        paymentDetails.put("dateTime", dateTime);
        paymentDetails.put("amountPaid", amountPaid);

        db.collection("BussingPaymentsCollection")
                .add(paymentDetails)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Payment recorded successfully! ID: " + documentReference.getId());
                    Snackbar.make(qrCodeButton, "Payment recorded successfully!", Snackbar.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Failed to record payment", e);
                    Snackbar.make(qrCodeButton, "Failed to record payment.", Snackbar.LENGTH_LONG).show();
                });
    }



    private String generateTicketNumber() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault());
        return simpleDateFormat.format(new Date());
    }

    private void loadRoutesFromFirestore(String fromLocation, String toLocation) {
        CollectionReference scheduleRef = db.collection("ScheduleDocumentsCollection");

        scheduleRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Set<String> activeRoutes = new HashSet<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    if ("active".equalsIgnoreCase(document.getString("status"))) {
                        activeRoutes.add(document.getString("from"));
                        activeRoutes.add(document.getString("to"));
                    }
                }

                List<String> routeList = new ArrayList<>(activeRoutes);
                setDropdownAdapter(departureDropdown, routeList);
                setDropdownAdapter(arrivalDropdown, routeList);

                departureDropdown.post(() -> departureDropdown.setText(fromLocation, false));
                arrivalDropdown.post(() -> arrivalDropdown.setText(toLocation, false));

                Log.d("Firestore", "Routes loaded: " + routeList);
            } else {
                Log.e("Firestore", "Error loading routes", task.getException());
            }
        });
    }

    private void setDropdownAdapter(AutoCompleteTextView dropdown, List<String> dataList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, dataList);
        dropdown.setAdapter(adapter);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format("%02d/%02d/%d", selectedMonth + 1, selectedDay, selectedYear);
                    dateInput.setText(formattedDate);
                },
                year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private boolean isFormValid() {
        return !departureDropdown.getText().toString().isEmpty()
                && !arrivalDropdown.getText().toString().isEmpty()
                && !passengersDropdown.getText().toString().isEmpty()
                && !dateInput.getText().toString().isEmpty();
    }
}
