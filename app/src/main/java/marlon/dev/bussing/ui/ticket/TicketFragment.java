package marlon.dev.bussing.ui.ticket;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import marlon.dev.bussing.R;
import marlon.dev.bussing.databinding.FragmentTicketBinding;

public class TicketFragment extends Fragment {

    private FragmentTicketBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        TicketViewModel ticketViewModel =
                new ViewModelProvider(this).get(TicketViewModel.class);

        binding = FragmentTicketBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textTicket;
        ticketViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}