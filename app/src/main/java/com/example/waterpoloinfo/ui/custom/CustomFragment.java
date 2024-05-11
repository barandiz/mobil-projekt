package com.example.waterpoloinfo.ui.custom;

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

import com.example.waterpoloinfo.R;
import com.example.waterpoloinfo.databinding.FragmentHomeBinding;

public class CustomFragment extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 123; // tetszőleges érték

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_custom, container, false);

        // Ellenőrizzük, hogy meg van-e az engedély a tárhely eléréséhez
        if (checkPermission()) {
            // Ha megvan az engedély, betöltjük a képeket
            loadImages(root);
        } else {
            // Ha nincs meg az engedély, és korábban még nem utasították el, kérjük meg a felhasználót, hogy adjon engedélyt
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                requestPermission();
            } else {
                // Ha korábban elutasították
                showPermissionExplanation();
            }
        }


        return root;
    }

    private void showPermissionExplanation() {
        Toast.makeText(requireContext(), "Az engedély megtagadása miatt a képek betöltése nem lehetséges.", Toast.LENGTH_SHORT).show();
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Ha a felhasználó elfogadja az engedélyt, betöltjük a képeket
                loadImages(getView());
            } else {
                // Ha a felhasználó újra elutasítja az engedélykérést, újra kérik az engedélyt
                requestPermission();
            }
        }
    }

    private void loadImages(View root) {
        @SuppressLint({"ResourceType", "MissingInflatedId", "LocalSuppress"})
        ImageView imageView = (ImageView) root.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.baseline_check_circle_24);
    }

}
