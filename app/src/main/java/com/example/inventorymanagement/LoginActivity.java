package com.example.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView btnRegisterPage;

    ProgressBar progressBarLogin;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etCurrPass);

        btnLogin = findViewById(R.id.btnUpdate);
        btnRegisterPage = findViewById(R.id.btnRegisterPage);

        progressBarLogin = findViewById(R.id.progressBarUpdate);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] params = {etEmail.getText().toString().trim(), etPassword.getText().toString()};
                new Login().execute(params);
            }
        });
        btnRegisterPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentRegisterPage = new Intent(getApplicationContext(), Register.class);
                startActivity(intentRegisterPage);
            }
        });
//


    }

    class Login extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressBarLogin.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {

            String email = strings[0], password = strings[1];

            Connection connection = null;
            Statement statementLogin = null;
            ResultSet resultSetUserExists = null;
//            dbConnection db = new dbConnection();

            try {
                Class.forName("com.mysql.jdbc.Driver");

                connection = DriverManager.getConnection("jdbc:mysql://humaraserver.mysql.database.azure.com:3306/inventory?useSSL=true", "harshit", "Parking@123");

//                connection =  db.func();
                statementLogin = connection.createStatement();

                String queryUserExists = String.format("select * from members WHERE email='%s'", email);
                resultSetUserExists = statementLogin.executeQuery(queryUserExists);

                if (resultSetUserExists.next() == false) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast toastUserNotFound = Toast.makeText(getApplicationContext(), "User not found!", Toast.LENGTH_SHORT);
                            toastUserNotFound.show();
                        }
                    });

                } else {
//                    System.out.println(resultSetUserExists.getString((3)));
//                    System.out.println(password);
                    String a = resultSetUserExists.getString(6);
                    if (a.equals(password) == false) {

                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast toastPasswordIncorrect = Toast.makeText(getApplicationContext(), "Incorrect Password!", Toast.LENGTH_SHORT);
                                toastPasswordIncorrect.show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast tostUserFound = Toast.makeText(getApplicationContext(), "User found!", Toast.LENGTH_SHORT);
                                tostUserFound.show();
                            }
                        });

                        String userId = resultSetUserExists.getString(1);
                        String name = resultSetUserExists.getString(2);

                        sharedPreferences.edit().putBoolean("loggedIn", true).apply();
                        sharedPreferences.edit().putString("userId", userId).apply();
                        sharedPreferences.edit().putString("name", name.split(" ",2)[0]).apply();

                        try {
                            resultSetUserExists.close();
                            statementLogin.close();
                            connection.close();

                        } catch (Exception e) {
                        }

                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);

                        intent.putExtra("userId", userId);
                        intent.putExtra("name", name.split(" ",2)[0]);
                        startActivity(intent);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBarLogin.setVisibility(View.INVISIBLE);
        }
    }
}