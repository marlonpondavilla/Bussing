package marlon.dev.bussing.ui.history;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import marlon.dev.bussing.R;
import marlon.dev.bussing.databinding.FragmentHistoryBinding;
import marlon.dev.bussing.ui.home.CardAdapter;
import marlon.dev.bussing.ui.home.CardLists;


public class HistoryFragment extends Fragment {

    private ArrayList<CardHistoryLists> cardHistoryLists = new ArrayList<>();
    private RecyclerView recyclerView;
    private CardHistoryAdapter cardHistoryAdapter;


    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        Button showSpinnerButton = view.findViewById(R.id.spinnerButton);
        Spinner spinner = view.findViewById(R.id.showSpinner);

        //setup spinner option
        String[] options = {"try1", "try2", "try3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, options);
        spinner.setAdapter(adapter);

        //button click listener to show spinner
        showSpinnerButton.setOnClickListener(v -> spinner.performClick());



        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}