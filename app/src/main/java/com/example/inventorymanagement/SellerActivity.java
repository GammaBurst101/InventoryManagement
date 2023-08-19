package com.example.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SellerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);
        Button btnAddNew = findViewById(R.id.sell);
        Button btnViewAll = findViewById(R.id.viewAll);
        Button btnViewSold = findViewById(R.id.viewsold);

        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getApplicationContext(),AddNewProduct.class);
                intent.putExtra("userId",getIntent().getStringExtra("userId"));

                startActivity(intent);
            }
        });
        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ViewAllProducts.class);
                intent.putExtra("userId",getIntent().getStringExtra("userId"));

                startActivity(intent);
            }
        });
        btnViewSold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ViewSoldProducts.class);
                intent.putExtra("userId",getIntent().getStringExtra("userId"));

                startActivity(intent);
            }
        });
    }
}