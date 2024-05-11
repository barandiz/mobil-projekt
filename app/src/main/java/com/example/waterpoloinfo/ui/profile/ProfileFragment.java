package com.example.waterpoloinfo.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.waterpoloinfo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class ProfileFragment extends Fragment {
    private TextView displayNameTextView;
    private TextView emailTextView;

    // Bejelentkezett felhasználó lekérése
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        // TextView-k referencia szerinti inicializálása
        displayNameTextView = root.findViewById(R.id.displayNameTextView);
        emailTextView = root.findViewById(R.id.emailTextView);
        if(mAuth.getCurrentUser() != null){
            String uid = mAuth.getCurrentUser().getUid();

            // Firestore-ból az aktuális felhasználó adatainak lekérése
            db.collection("Users").document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Ha az adott dokumentum létezik, lekérjük a user_name mező értékét
                            String userName = documentSnapshot.getString("user_name");
                            String email = documentSnapshot.getString("user_email");

                            // TextView szövegének beállítása a lekért felhasználó nevével
                            displayNameTextView.setText(displayNameTextView.getText() + userName);
                            emailTextView.setText(emailTextView.getText() + email);
                        } else {
                            // Ha az adott dokumentum nem létezik, hibaüzenet megjelenítése
                            displayNameTextView.setText("Név nincs beállítva");
                            emailTextView.setText("Email nincs beállítva");
                            Toast.makeText(requireContext(), "Nem található felhasználói adat.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Ha hiba történik a lekérés során, hibaüzenet megjelenítése
                        Toast.makeText(requireContext(), "Hiba történt a felhasználói adatok lekérése során.", Toast.LENGTH_SHORT).show();
                    });
        }else{
            // Ha az adott dokumentum nem létezik, hibaüzenet megjelenítése
            displayNameTextView.setText("Név nincs beállítva");
            emailTextView.setText("Email nincs beállítva");
        }


        return root;
    }
}
