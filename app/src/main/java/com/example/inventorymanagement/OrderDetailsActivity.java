package com.example.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class OrderDetailsActivity extends AppCompatActivity {
    String Address,PhoneNumber,Quantity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        EditText address = findViewById(R.id.Address);
        EditText phoneNumber = findViewById(R.id.phoneNumber);
        EditText quantity = findViewById(R.id.quantity);
        TextView mxquantity = findViewById(R.id.dpquantity);
        Button submit = findViewById(R.id.submit);

        String str = getIntent().getStringExtra("price");

        mxquantity.setText("Available: " + getIntent().getStringExtra("quantity"));

        Address = address.getText().toString();
        PhoneNumber = phoneNumber.getText().toString();
        Quantity = quantity.getText().toString();

        if(TextUtils.isEmpty(Address)){
            Address = getIntent().getStringExtra("address");
        }
        if(TextUtils.isEmpty(PhoneNumber)){
            PhoneNumber = getIntent().getStringExtra("phonenumber");
        }
        if(TextUtils.isEmpty(Quantity)){
            Quantity = "1";
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Payment.class);
                intent.putExtra("name",getIntent().getStringExtra("name"));
                intent.putExtra("company",getIntent().getStringExtra("company"));
                intent.putExtra("address",Address);
                intent.putExtra("phonenumber",PhoneNumber);
                intent.putExtra("avquantity",getIntent().getStringExtra("quantity"));
                intent.putExtra("btquantity",Quantity);
                intent.putExtra("price",getIntent().getStringExtra("price"));
                intent.putExtra("userid",getIntent().getStringExtra("userid"));
                intent.putExtra("sellerid",getIntent().getStringExtra("sellerid"));
                startActivity(intent);
            }
        });
    }
}