package com.example.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;

//<<<<<<< HEAD
//=======
//>>>>>>> 22c14112945307b11d68bd8cd82d574032223ed1
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        String userId = getIntent().getStringExtra("userId");

        Button btnLogout = findViewById(R.id.btnLogout);

        TextView greeting = findViewById(R.id.greeting);
        String str = getIntent().getStringExtra("name");
        greeting.setText("Hello, " + str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase());
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                sharedPreferences.edit().putBoolean("loggedIn", false).apply();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                startActivity(intent);

            }
        });

        Button btnSell = findViewById(R.id.btnSell);
        btnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentUserProfile = new Intent(getApplicationContext(), SellerActivity.class);
                intentUserProfile.putExtra("userId", userId);

                startActivity(intentUserProfile);
            }
        });

        Button btnBuy = findViewById(R.id.btnBuy);

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectCategory.class);

                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });

    }
}
