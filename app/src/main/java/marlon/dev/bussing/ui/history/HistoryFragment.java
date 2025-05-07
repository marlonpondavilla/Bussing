package marlon.dev.bussing.ui.history;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import marlon.dev.bussing.R;

public class HistoryFragment extends Fragment {

    private ArrayList<CardHistoryLists> cardHistoryLists = new ArrayList<>();
    private RecyclerView recyclerView;
    private CardHistoryAdapter cardHistoryAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private ImageView noTransactionImage;
    private TextView noTransactionText, noTransactionSubText;
    private Button deleteBtn;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);


        recyclerView = view.findViewById(R.id.recyclerView);

        noTransactionImage = view.findViewById(R.id.noTransactionImage);
        noTransactionText = view.findViewById(R.id.noTransactionText);
        noTransactionSubText = view.findViewById(R.id.noTransactionSubText);

        deleteBtn = view.findViewById(R.id.deleteButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cardHistoryAdapter = new CardHistoryAdapter(requireContext(), cardHistoryLists);
        recyclerView.setAdapter(cardHistoryAdapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        cardHistoryAdapter.fetchTicketHistory(this::checkTransactionVisibility);

        deleteBtn.setOnClickListener(v -> {
            cardHistoryAdapter.deleteSelectedItems();
        });


        return view;
    }



    private void checkTransactionVisibility() {
        if (cardHistoryLists.isEmpty()) {
            noTransactionImage.setVisibility(View.VISIBLE);
            noTransactionText.setVisibility(View.VISIBLE);
            noTransactionSubText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noTransactionImage.setVisibility(View.GONE);
            noTransactionText.setVisibility(View.GONE);
            noTransactionSubText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
