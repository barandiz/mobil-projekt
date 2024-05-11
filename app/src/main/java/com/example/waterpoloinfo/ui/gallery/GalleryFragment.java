package com.example.waterpoloinfo.ui.gallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.waterpoloinfo.R;
import com.example.waterpoloinfo.databinding.FragmentGalleryBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        loadImages(root);

        return root;
    }

    private void loadImages(View root){
        @SuppressLint({"ResourceType", "MissingInflatedId", "LocalSuppress"})
        ImageView imageView6 = (ImageView) root.findViewById(R.id.imageView6);
        imageView6.setImageResource(R.drawable.image_6);

        @SuppressLint({"ResourceType", "MissingInflatedId", "LocalSuppress"})
        ImageView imageView7 = (ImageView) root.findViewById(R.id.imageView7);
        imageView7.setImageResource(R.drawable.image_7);

        @SuppressLint({"ResourceType", "MissingInflatedId", "LocalSuppress"})
        ImageView imageView8 = (ImageView) root.findViewById(R.id.imageView8);
        imageView8.setImageResource(R.drawable.image_8);

        @SuppressLint({"ResourceType", "MissingInflatedId", "LocalSuppress"})
        ImageView imageView9 = (ImageView) root.findViewById(R.id.imageView9);
        imageView9.setImageResource(R.drawable.image_9);

        @SuppressLint({"ResourceType", "MissingInflatedId", "LocalSuppress"})
        ImageView imageView10 = (ImageView) root.findViewById(R.id.imageView10);
        imageView10.setImageResource(R.drawable.image_10);

        @SuppressLint({"ResourceType", "MissingInflatedId", "LocalSuppress"})
        ImageView imageView11 = (ImageView) root.findViewById(R.id.imageView11);
        imageView11.setImageResource(R.drawable.image_11);
    }






}