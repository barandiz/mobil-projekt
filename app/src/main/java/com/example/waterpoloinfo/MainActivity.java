package com.example.waterpoloinfo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;

import com.example.waterpoloinfo.ui.register.RegisterFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.waterpoloinfo.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener{
    private static final String LOG_TAG = MainActivity.class.getName();
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;


    private FirebaseAuth mAuth;
    // Hozzáférés a Firestore adatbázishoz
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String USERS_COLLECTION = "Users";
    private static final String FIELD_SELECTED_ACCOUNT_TYPE =  "selected_account_type";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mAuth = FirebaseAuth.getInstance();

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_login, R.id.nav_register ,
                R.id.nav_custom, R.id.nav_logout, R.id.nav_edit, R.id.nav_profile)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        navController.addOnDestinationChangedListener(this);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        checkIfUserIsLoggedIn();

        //Notification átnavigálás
        Intent intent = getIntent();
        String fragmentTag = intent.getStringExtra("fragment");

        if (fragmentTag != null && fragmentTag.equals("home")) {
            navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_home);
        }

    }

    //Másik Lifecycle hook - ellenőrzés a felhasználó állapotáról
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume run.");
        checkIfUserIsLoggedIn();
    }

    //Annak érdekében, hogy a menün mi jelenjen meg, követni kell a felhasználót, hogy be van a jelentkezve
    // A változás hatására szükséges figyelő
    @Override
    public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
        if (destination.getId() == R.id.nav_login || destination.getParent().getId() == R.id.nav_login) {
            // Ha a LoginFragment vagy annak alfragmentumaihoz navigálunk, akkor frissítsük a menüpontok láthatóságát
            checkIfUserIsLoggedIn();
        }
    }

    // Felhasználó bejelentkezési állapotának ellenőrzése
    public void checkIfUserIsLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // A felhasználó be van jelentkezve
            // A bejelentkezési és regisztrációs menüpontok eltávolítása, logout hozzáadása
            binding.navView.getMenu().findItem(R.id.nav_login).setVisible(false);
            binding.navView.getMenu().findItem(R.id.nav_register).setVisible(false);
            binding.navView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            //binding.navView.getMenu().findItem(R.id.nav_profile).setVisible(true);

            // A bejelentkezett felhasználó UID-je alapján lekérdezzük a dokumentumot
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DocumentReference userRef = db.collection(USERS_COLLECTION).document(userId);

            // A dokumentum lekérése a Firestore-ból
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                // Ha a dokumentum létezik, ellenőrizzük a selected_account_type mezőt
                                String selectedAccountType = documentSnapshot.getString(FIELD_SELECTED_ACCOUNT_TYPE);
                                if (selectedAccountType != null && selectedAccountType.equals("editor")) {
                                    Log.d(LOG_TAG, "Selected account type: " + selectedAccountType);
                                    binding.navView.getMenu().findItem(R.id.nav_edit).setVisible(true);

                                } else {
                                    Log.d(LOG_TAG, "Selected account type is null");
                                    binding.navView.getMenu().findItem(R.id.nav_edit).setVisible(false);
                                }
                            } else {
                                Log.d(LOG_TAG, "User document does not exist");
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(LOG_TAG, "Error getting user document", e);
                        }
                    });


            Log.i(LOG_TAG, "User:" + currentUser.getEmail());
        } else {
            // A felhasználó nincs bejelentkezve
            // A kijelentkezés menüpont eltávolítása, login register hozzáadása
            binding.navView.getMenu().findItem(R.id.nav_login).setVisible(true);
            binding.navView.getMenu().findItem(R.id.nav_register).setVisible(true);
            binding.navView.getMenu().findItem(R.id.nav_logout).setVisible(false);
            binding.navView.getMenu().findItem(R.id.nav_edit).setVisible(false);
            //binding.navView.getMenu().findItem(R.id.nav_profile).setVisible(false);
        }
    }


    // Kilépteti a felhasználót és újra megjeleníti a bejelentkezési és regisztrációs menüpontokat
    public void logout(MenuItem menuItem) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            mAuth.signOut();
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Before log out, please log in!", Toast.LENGTH_SHORT).show();
        }
        //Átnavigálás a loginre
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.nav_login);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}