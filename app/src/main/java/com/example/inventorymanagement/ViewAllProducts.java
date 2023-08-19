package com.example.inventorymanagement;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class ViewAllProducts extends AppCompatActivity {

    ProgressBar progressBarProducts;

    ListView lvCategories;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_products);

        progressBarProducts =findViewById(R.id.progressBarGetCities);
        lvCategories =findViewById(R.id.lvCities);

        userId = getIntent().getStringExtra("userId");

        ArrayAdapter<String> citiesAdapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_my, new ArrayList<String>());

        lvCategories.setAdapter(citiesAdapter);


        new GetCities().execute();


    }

    class GetCities extends AsyncTask<Void, Void, ArrayList<String>>{

        ArrayAdapter<String> productsAdapter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            productsAdapter = (ArrayAdapter<String>) lvCategories.getAdapter();
            progressBarProducts.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {

            ArrayList <String> products = new ArrayList<String>();

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://humaraserver.mysql.database.azure.com:3306/inventory?useSSL=true", "harshit", "Parking@123");
                Statement statementLogin = connection.createStatement();

                String queryCategory = String.format("select name,company from products where sellerId = '%s'",getIntent().getStringExtra("userId"));
                ResultSet resultSetCategory = statementLogin.executeQuery(queryCategory);

                while(resultSetCategory.next())
                {
                    String product = resultSetCategory.getString(1) + "   (" + resultSetCategory.getString(2) + ")";
                    products.add(product);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return products;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            progressBarProducts.setVisibility(View.GONE);

            productsAdapter.addAll(strings);

//            lvCategories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                    Intent intent = new Intent(getApplicationContext(), SelectProduct.class);
//
//                    intent.putExtra("category", strings.get(position));
//                    intent.putExtra("userId", userId);
//                    startActivity(intent);
//
//                }
//            });

        }
    }
}