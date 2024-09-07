package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.context = getApplicationContext();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        replaceFragment(new HomeFragment());


        BottomNavigationView nav=findViewById(R.id.nav);

       nav.setOnNavigationItemSelectedListener(item -> {
           int itemId= item.getItemId();
           if (itemId==R.id.home)
               replaceFragment(new HomeFragment());
           if (itemId==R.id.scan)
               replaceFragment(new ScanFragment());
           if (itemId==R.id.settings)
               replaceFragment(new SettingsFragment());
           return true;
       });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager manager=getSupportFragmentManager();
        FragmentTransaction transaction=manager.beginTransaction();
        transaction.replace(R.id.fragment_container,fragment);
        transaction.commit();
    }

    //returns the context of MainActivity
    public static Context getAppContext() {
        return MainActivity.context;
    }


}