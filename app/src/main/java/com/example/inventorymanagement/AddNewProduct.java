package com.example.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class AddNewProduct extends AppCompatActivity {

    ProgressBar progressBarUpdate;
    EditText etname,etcompany,etcategory,etprice,etquantity;
    Button btnsubmit;

    String name,company,category,price,quantity,userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);

        etname = findViewById(R.id.name);
        etcompany = findViewById(R.id.company);
        etcategory = findViewById(R.id.category);
        etprice = findViewById(R.id.price);
        etquantity = findViewById(R.id.quantity);

        progressBarUpdate = findViewById(R.id.progressBarUpdate);

        userId = getIntent().getStringExtra("userId");

        btnsubmit = findViewById(R.id.submit);

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AddNew().execute();
            }
        });

    }


 class AddNew extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBarUpdate.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            name = etname.getText().toString();
            company = etcompany.getText().toString();
            category = etcategory.getText().toString();
            price = etprice.getText().toString();
            quantity = etquantity.getText().toString();


            if(TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(company) ||
                TextUtils.isEmpty((category)) ||
                TextUtils.isEmpty(price) ||
                TextUtils.isEmpty(quantity)) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast nullEntry = Toast.makeText(getApplicationContext(), "Please Enter all fields!", Toast.LENGTH_SHORT);
                        nullEntry.show();
                    }
                });
            }

            else {
                    try {
                        Connection connection1;
                        Statement statementCreateNew;
                        int resultCreateNew;
                        connection1 = DriverManager.getConnection("jdbc:mysql://humaraserver.mysql.database.azure.com:3306/inventory?useSSL=true", "harshit", "Parking@123");
                        statementCreateNew = connection1.createStatement();
                        String queryInsert = String.format("insert into products (sellerID,name,company,category,price,quantity) values ('%s','%s','%s','%s','%s','%s');" ,
                                userId,name, company,category, price ,quantity);

                        resultCreateNew = statementCreateNew.executeUpdate(queryInsert);

                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (resultCreateNew == 0) {
                                    Toast toastFailure = Toast.makeText(getApplicationContext(), "Something Went Wrong! Please Try Again.", Toast.LENGTH_SHORT);
                                    toastFailure.show();
                                } else {
                                    Toast toastSuccess = Toast.makeText(getApplicationContext(), "SuccessFully Added Product!!", Toast.LENGTH_SHORT);
                                    toastSuccess.show();
                                }
                            }
                        });


                        try {
                            connection1.close();
                            statementCreateNew.close();

                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }


                }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBarUpdate.setVisibility(View.INVISIBLE);
        }
    }
}
