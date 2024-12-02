package marlon.dev.bussing.ui.location;

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
import marlon.dev.bussing.databinding.FragmentHistoryBinding;
import marlon.dev.bussing.databinding.FragmentLocationBinding;
import marlon.dev.bussing.ui.history.HistoryFragment;
import marlon.dev.bussing.ui.history.HistoryViewModel;

public class LocationFragment extends Fragment {

    private FragmentLocationBinding binding;

    public static LocationFragment newInstance() {
        return new LocationFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        LocationViewModel locationViewModel =
                new ViewModelProvider(this).get(LocationViewModel.class);

         binding = FragmentLocationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textLocation;
        locationViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}