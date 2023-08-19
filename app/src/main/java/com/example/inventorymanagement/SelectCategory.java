package com.example.inventorymanagement;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class SelectCategory extends AppCompatActivity {

    ProgressBar progressBarGetCities;

    ListView lvCategories;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);

        progressBarGetCities=findViewById(R.id.progressBarGetCities);
        lvCategories =findViewById(R.id.lvCities);

        userId = getIntent().getStringExtra("userId");

        ArrayAdapter<String> citiesAdapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_my, new ArrayList<String>());

        lvCategories.setAdapter(citiesAdapter);


        new GetCities().execute();


    }

    class GetCities extends AsyncTask<Void, Void, ArrayList<String>>{

        ArrayAdapter<String> citiesAdapter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            citiesAdapter= (ArrayAdapter<String>) lvCategories.getAdapter();
            progressBarGetCities.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {

            ArrayList <String> catergories = new ArrayList<String>();

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://humaraserver.mysql.database.azure.com:3306/inventory?useSSL=true", "harshit", "Parking@123");
                Statement statementLogin = connection.createStatement();

                String queryCategory = String.format("select distinct(category) from products where sellerId != '%s'",userId);
                ResultSet resultSetCategory = statementLogin.executeQuery(queryCategory);

                while(resultSetCategory.next())
                {
                    String category = resultSetCategory.getString(1);
                    catergories.add(category);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return catergories;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            progressBarGetCities.setVisibility(View.GONE);

            citiesAdapter.addAll(strings);

            lvCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(getApplicationContext(), SelectProduct.class);

                    intent.putExtra("category", strings.get(position));
                    intent.putExtra("userId", userId);
                    startActivity(intent);

                }
            });

        }
    }
}