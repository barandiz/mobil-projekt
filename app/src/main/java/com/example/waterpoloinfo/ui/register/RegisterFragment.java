package com.example.waterpoloinfo.ui.register;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.getIntent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.waterpoloinfo.NotificationHandler;
import com.example.waterpoloinfo.R;
import com.example.waterpoloinfo.ui.login.LoginFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;


public class RegisterFragment extends Fragment {

    private static final String LOG_TAG = RegisterFragment.class.getName();
    private static final String PREF_KEY = RegisterFragment.class.getPackage().toString();
    EditText userNameEditText;
    EditText userEmailEditText;
    EditText passwordEditText;
    EditText passwordConfirmEditText;
    RadioGroup accountTypeGroup;

    private SharedPreferences preferences;
    private NotificationHandler mNotificationHandler;

    //regisztráció végén a firebassel való csatlakozáshoz
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register, container, false);

        //Ezzel érjük el a firebase adta lehetőségeket
        mAuth = FirebaseAuth.getInstance();

        //Milyen kulcs-értéket kapok
        //Bundle bundle = getIntent().getExtras();
        //int secret_key = bundle.getInt("SECRET_KEY");
        //Vagy ez a megoldás, bármelyik jó

        /*int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);
        if(secret_key != 99){
            finish();
        }*/

        // Initialize views
        userNameEditText = root.findViewById(R.id.userNameEditText);
        userEmailEditText = root.findViewById(R.id.userEmailEditText);
        passwordEditText = root.findViewById(R.id.passwordEditText);
        passwordConfirmEditText = root.findViewById(R.id.passwordAgainEditText);
        accountTypeGroup = root.findViewById(R.id.accountTypeGroup);

        Button registerButton = root.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        Button cancelButton = root.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        preferences = getActivity().getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        String userName = preferences.getString("userName", "");
        String password = preferences.getString("password", "");

        mNotificationHandler = new NotificationHandler(requireContext());


        Log.i(LOG_TAG, "onCreate");
        return root;
    }

    public void register() {
        String userName = userNameEditText.getText().toString();
        String userEmail = userEmailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordAgain = passwordConfirmEditText.getText().toString();
        int selectedAccountType = accountTypeGroup.getCheckedRadioButtonId();

        // Validate input fields
        if ( TextUtils.isEmpty(userName) || TextUtils.isEmpty(userEmail) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(passwordAgain) ||
                selectedAccountType == -1) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(passwordAgain)){
            Toast.makeText(getContext(), "Nem egyenlő a jelszó és megerősítése", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "Nem egyenlő a jelszó és megerősítése");
            return;
        }

        //Radiogroup kiválasztott adat elkérésem kiválaszott id,
        // keresés és nevét elkérése, hogy tudjuk mi van kiválasztva
        //? Lehet hogy nem lesz jó
        /*int checkedId = accountTypeGroup.getCheckedRadioButtonId();
        RadioButton radioButton = accountTypeGroup.findViewById(checkedId);
        String accountType = radioButton.getText().toString();*/

        String accountType = selectedAccountType == R.id.editorRadioButton ? "editor" : "player";



        Log.i(LOG_TAG, "Regisztrált: " + userName + ", email: "+ userEmail);

        //User létrehozása, új listener létrehoz
        mAuth.createUserWithEmailAndPassword(userEmail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //Task mit jelent, úgy kell eljárni
                //Ha végzett a felhasználó a bejelentkzeéssel ez hívódik meg
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "User created successfully");

                    // Felhasználó sikeresen létrehozva, elérhető az authResult.getuser()
                    FirebaseUser user = mAuth.getCurrentUser();
                    // Ha a felhasználó sikeresen létrejött, mentheted az adatait a Firestore adatbázisban
                    saveUserToFirestore(user.getUid(), userName, userEmail, accountType);

                    // Átnavigálás a RegisterFragment-hez
                    String message = "Sikeres regisztráció " + userName +"!\n"+
                            "Kérjük jelentkezzen be!";
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();

                    NavHostFragment.findNavController(RegisterFragment.this)
                            .navigate(R.id.action_registerFragment_to_homeFragment);


                    mNotificationHandler.send("Üdvözöljük " + userName +"!\n" + "Sikeres regisztráció!");

                } else {
                    Log.d(LOG_TAG, "User wasn't created successfully");
                    //Tényleges hiba kiíratása
                    Toast.makeText(getContext(), "Invalid email or password!"+ task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });


        String message = "User Name: " + userName + "\n" +
                "Email: " + userEmail + "\n" +
                "Account Type: " + accountType;
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    public void cancel() {
        // Ha a fragmentet önállóan használod
        NavController navController = Navigation.findNavController(requireView());

        // Navigáció a HomeFragment-re
        navController.navigate(R.id.nav_home);
    }


    public void saveUserToFirestore(String userId, String userName, String userEmail, String accountType) {
        // Hozzáférés a Firestore adatbázishoz
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Új felhasználó objektum létrehozása
        Map<String, Object> user = new HashMap<>();
        user.put("user_name", userName);
        user.put("user_email", userEmail);
        user.put("selected_account_type", accountType);

        // Firestore-ban új dokumentum létrehozása a "users" kollekcióban, a felhasználó UID-jével
        db.collection("Users").document(userId)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(LOG_TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LOG_TAG, "Error writing document", e);
                    }
                });
    }

    //Ellenőrzések
    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

}