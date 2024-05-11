package com.example.waterpoloinfo.ui.login;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.waterpoloinfo.MainActivity;
import com.example.waterpoloinfo.R;
import com.example.waterpoloinfo.ui.register.RegisterFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {
    private static final String LOG_TAG = LoginFragment.class.getName();
    private static final String PREF_KEY = Fragment.class.getPackage().toString();
    //private static final int RC_SIGN_IN = 123;
    private static final int SECRET_KEY = 99;

    EditText userNameET;
    EditText passwordET;
    private SharedPreferences preferences;
    private FirebaseAuth mAuth;
    //Google felé egy request
    private GoogleSignInClient mGoogleSignInClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        //Emaillel enged be
        userNameET = root.findViewById(R.id.editTextEmail);
        passwordET = root.findViewById(R.id.editTextPassword);

        mAuth = FirebaseAuth.getInstance();

        Button loginButton = root.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(v); // Meghívjuk a login metódust a gombra kattintáskor
            }
        });

        Button guestLoginButton = root.findViewById(R.id.guestLoginButton);
        guestLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAsGuest(v); // Meghívjuk a login metódust a gombra kattintáskor
            }
        });

        Button registerButton = root.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(LoginFragment.this)
                        .navigate(R.id.action_registerFragment_to_loginFragment);
            }
        });



        return root;
    }

    public void login(View view) {
        // Az authentikációt és a bejelentkezést megvalósító kód...
        String userName = userNameET.getText().toString();
        String password = passwordET.getText().toString();

        //Log.i(LOG_TAG, "bejelentkezett: " + userName + ", jelszó: "+ password);
        //elkérem a szövegét kiírom logba

        //Be kell Léptetni a felhasználót, ha már bejelentkezett, addOnCompleteListener-tudjuk mi lesz a kimenetel
        mAuth.signInWithEmailAndPassword(userName, password).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //Log.d(LOG_TAG, "Login done!");
                    Log.d(LOG_TAG, userName +" loged in successfully.");
                    Toast.makeText(getContext(), userName +" logged in successfully.", Toast.LENGTH_LONG).show();

                    // Sikeres bejelentkezés esetén navigálás a főmenüre
                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.nav_home);

                    // Felhasználó bejelentkezése után frissítsük a menüpontok láthatóságát
                    ((MainActivity) requireActivity()).checkIfUserIsLoggedIn();
                } else {
                    Log.d(LOG_TAG, "User log in failed.");
                    //Toast.makeText(MainActivity.this, "Invalid email or password!", Toast.LENGTH_LONG).show();
                    Toast.makeText(getContext(), "User log in failed:", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    public void loginAsGuest(View view) {
        mAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(LOG_TAG, "Login as guest done!");
                    Toast.makeText(getContext(), "User logged in successfully:", Toast.LENGTH_LONG).show();
                    // Sikeres bejelentkezés esetén navigálás a főmenüre
                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.nav_home);
                    // Felhasználó bejelentkezése után frissítsük a menüpontok láthatóságát
                    ((MainActivity) requireActivity()).checkIfUserIsLoggedIn();
                } else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }



}
