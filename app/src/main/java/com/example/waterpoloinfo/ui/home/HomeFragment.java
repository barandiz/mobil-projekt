package com.example.waterpoloinfo.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.waterpoloinfo.R;
import com.example.waterpoloinfo.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private TextView textView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        textView = root.findViewById(R.id.homeTextView);


        return root;
    }

    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}