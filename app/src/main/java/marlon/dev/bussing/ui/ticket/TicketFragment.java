package marlon.dev.bussing.ui.ticket;

import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

import java.util.Random;

import marlon.dev.bussing.R;

public class TicketFragment extends Fragment {

    private AutoCompleteTextView departureDropdown, arrivalDropdown, passengersDropdown;
    private EditText dateInput;
    private MaterialButton qrCodeButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ticket, container, false);

        // Get reference to the AutoCompleteTextView using the correct ID
        departureDropdown = view.findViewById(R.id.departureDropDown);
        arrivalDropdown = view.findViewById(R.id.arrivalDropDown);
        passengersDropdown = view.findViewById(R.id.passengersDropDown);

        // Get the string array from resources
        String[] departureLocations = getResources().getStringArray(R.array.departure);
        String[] arrivalLocations = getResources().getStringArray(R.array.arrival);
        String[] passengersCount = getResources().getStringArray(R.array.passengers);

        // Create an ArrayAdapter for the departure, arrival, and passenger dropdown
        ArrayAdapter<String> departureAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, departureLocations);
        departureDropdown.setAdapter(departureAdapter);

        ArrayAdapter<String> arrivalAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, arrivalLocations);
        arrivalDropdown.setAdapter(arrivalAdapter);

        ArrayAdapter<String> passengersAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, passengersCount);
        passengersDropdown.setAdapter(passengersAdapter);

        // Set the adapter to the AutoCompleteTextView
        departureDropdown.setAdapter(departureAdapter);
        arrivalDropdown.setAdapter(arrivalAdapter);
        passengersDropdown.setAdapter(passengersAdapter);

        // Find the EditText by ID
        dateInput = view.findViewById(R.id.dateInput);
        // Set a click listener to show the date picker dialog
        dateInput.setOnClickListener(v -> showDatePickerDialog());

        // Initialize the QR Code button
        qrCodeButton = view.findViewById(R.id.qrCodeButton);

        qrCodeButton.setOnClickListener(v -> {
            // Check if all required fields are filled
            if(isFormValid()) {
                // Generate a random number for the QR code
                int randomCode = generateRandomCode();

                // Show a Toast message that the QR code is generated
                Toast.makeText(requireContext(), "QR Code generated: " + randomCode, Toast.LENGTH_SHORT).show();

            } else {
                // Show a Toast message prompting the user to fill in all fields
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }

        });

        return view;
    }

    private void showDatePickerDialog() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the selected date as MM/dd/yyyy
                    String formattedDate = String.format("%02d/%02d/%d", selectedMonth + 1, selectedDay, selectedYear);
                    // Set the formatted date in the EditText field
                    dateInput.setText(formattedDate);
                },
                year, month, dayOfMonth);

        // Show the DatePickerDialog
        datePickerDialog.show();
    }

    // Method to generate a random code (for example, between 1000 and 9999)
    private int generateRandomCode() {
        Random random = new Random();
        return random.nextInt(9000) + 1000;  // Generates a number between 1000 and 9999
    }

    //Method to check if all required fields are filled
    private boolean isFormValid(){
        // Check if departure, arrival, passengers, and date are not empty
        boolean isDepartureValid = departureDropdown.getText() != null && !departureDropdown.getText().toString().isEmpty();
        boolean isArrivalValid = arrivalDropdown.getText() != null && !arrivalDropdown.getText().toString().isEmpty();
        boolean isPassengersValid = passengersDropdown.getText() != null && !passengersDropdown.getText().toString().isEmpty();
        boolean isDateValid = dateInput.getText() != null && !dateInput.getText().toString().isEmpty();

        return isDepartureValid && isArrivalValid && isPassengersValid && isDateValid;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        departureDropdown = null;
        arrivalDropdown = null;
        passengersDropdown = null;
        dateInput = null;
        qrCodeButton = null;
    }
}
