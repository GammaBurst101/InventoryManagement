package com.example.inventorymanagement;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Register extends AppCompatActivity {

    EditText etEnterName, etEnterEmail, etEnterContactNum, etEnterAddress, etCreatePass, etConfirmPass;

    Button btnRegister, btnReset;

    ProgressBar progressBarUpdate;

    String name, email, address, contactNum, createPass, confirmPass, userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        etEnterName = findViewById(R.id.etEnterName);
        etEnterEmail = findViewById(R.id.etEnterEmail);
        etEnterAddress = findViewById(R.id.etEnterAddress);
        etEnterContactNum = findViewById(R.id.etEnterContactNum);
        etCreatePass = findViewById(R.id.etCreatePass);
        etConfirmPass = findViewById(R.id.etConfirmPass);

        btnRegister = findViewById(R.id.btnRegister);

        progressBarUpdate = findViewById(R.id.progressBarUpdate);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new RegisterAsync().execute();
            }
        });



    }

    class RegisterAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBarUpdate.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            name = etEnterName.getText().toString();
            email = etEnterEmail.getText().toString();
            address = etEnterAddress.getText().toString();
            contactNum = etEnterContactNum.getText().toString();
            createPass = etCreatePass.getText().toString();
            confirmPass = etConfirmPass.getText().toString();

            if(TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(email) ||
                TextUtils.isEmpty((address)) ||
                TextUtils.isEmpty(contactNum) ||
                TextUtils.isEmpty(createPass) ||
                TextUtils.isEmpty(confirmPass)) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast nullEntry = Toast.makeText(getApplicationContext(), "Please Enter all fields!", Toast.LENGTH_SHORT);
                        nullEntry.show();
                    }
                });
            }

            else {

                boolean flagUserAlreadyExists = false, flagVehicleAlreadyExists = false;

                Connection connection = null;
                Statement statementCheck = null, statementCheck1 = null;
                ResultSet resultGet = null, resultCount = null;

                try {

                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection("jdbc:mysql://humaraserver.mysql.database.azure.com:3306/inventory?useSSL=true", "harshit", "Parking@123");
                    statementCheck = connection.createStatement();
                    statementCheck1 = connection.createStatement();

                    String queryGet = String.format("select * from members;");
                    resultGet = statementCheck.executeQuery(queryGet);

                    String queryCount = String.format("select count(*) from members;");
                    resultCount = statementCheck1.executeQuery(queryCount);

                    while(resultGet.next()) {
                        if(email.equals(resultGet.getString("email"))){
                            flagUserAlreadyExists = true;
                            break;
                        }

                    }

                    resultCount.next();

                    int count = resultCount.getInt(1);

                    userId = Integer.toString(count + 1);

                    connection.close();
                    statementCheck.close();
                    statementCheck1.close();
                    resultGet.close();
                    resultCount.close();

                } catch (Exception e) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast toastPasswordIncorrect = Toast.makeText(getApplicationContext(), "CheckEmail Exception!", Toast.LENGTH_SHORT);
                            toastPasswordIncorrect.show();
                        }
                    });


                    e.printStackTrace();
                }

                if (flagUserAlreadyExists || flagVehicleAlreadyExists) {
                    runOnUiThread(new Runnable() {

                        public void run() {

                            Toast alreadyExists = Toast.makeText(getApplicationContext(),
                                    "User with Email ID or vehicleID already exists! Login or use different Email.", Toast.LENGTH_SHORT);

                            alreadyExists.show();
                        }
                    });
                }

                else if (!createPass.equals(confirmPass)) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast toastPasswordIncorrect = Toast.makeText(getApplicationContext(), "Enter same password in both password fields", Toast.LENGTH_SHORT);
                            toastPasswordIncorrect.show();
                        }
                    });

                } else {

                    Connection connection1 = null;
                    Statement statementCreateNew = null;
                    int resultCreateNew;

                    try {

                        connection1 = DriverManager.getConnection("jdbc:mysql://humaraserver.mysql.database.azure.com:3306/inventory?useSSL=true", "harshit", "Parking@123");
                        statementCreateNew = connection1.createStatement();
                        String queryInsert = String.format("insert into members (name,contactNumber,address,email,password) values ('%s','%s','%s','%s','%s');" ,
                                name, contactNum, address, email,createPass);

                        resultCreateNew = statementCreateNew.executeUpdate(queryInsert);

                        runOnUiThread(new Runnable() {
                            public void run() {
                                if (resultCreateNew == 0) {
                                    Toast toastFailure = Toast.makeText(getApplicationContext(), "Something Went Wrong! Please Try Again.", Toast.LENGTH_SHORT);
                                    toastFailure.show();
                                } else {
                                    Toast toastSuccess = Toast.makeText(getApplicationContext(), "Registration Successful!!", Toast.LENGTH_SHORT);
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

                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast toastPasswordIncorrect = Toast.makeText(getApplicationContext(), "InsertAsync Exception!", Toast.LENGTH_SHORT);
                                toastPasswordIncorrect.show();
                            }
                        });
                    }

                    runOnUiThread(new Runnable() {
                        public void run() {

                            etCreatePass.setText(null);
                            etConfirmPass.setText(null);
                        }
                    });

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
