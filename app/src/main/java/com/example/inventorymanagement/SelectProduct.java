package com.example.inventorymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class SelectProduct extends AppCompatActivity {
    ProgressBar progressBarGetBuildings;

    ListView lvProducts;
    String category, userId;
    String Address,PhoneNumber,sellerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);

        progressBarGetBuildings=findViewById(R.id.progressBarGetBuildings);
        lvProducts =findViewById(R.id.lvBuildings);

        category = getIntent().getStringExtra("category");
        userId = getIntent().getStringExtra("userId");

        ArrayAdapter<String> buildingsAdapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_my, new ArrayList<String>());

        lvProducts.setAdapter(buildingsAdapter);


        new GetBuildings().execute();


    }

    class GetBuildings extends AsyncTask<Void, Void, ArrayList <Pair<Pair<String,String>,Pair<String, Integer>>>> {

        ArrayAdapter<String> productAdapter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            productAdapter = (ArrayAdapter<String>) lvProducts.getAdapter();
            progressBarGetBuildings.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList <Pair<Pair<String,String>,Pair<String, Integer>>> doInBackground(Void... voids) {

            ArrayList <Pair<Pair<String,String>,Pair<String, Integer>>> product_id = new ArrayList<Pair<Pair<String,String>,Pair<String, Integer>>>();

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://humaraserver.mysql.database.azure.com:3306/inventory?useSSL=true", "harshit", "Parking@123");
                Statement statementLogin = connection.createStatement();
                Statement statementAddress = connection.createStatement();

                String queryProducts = String.format("select name,company,price,quantity,sellerId from products where category='%s' and sellerId != '%s'", category,userId);
                String queryAddress = String.format("select address,contactNumber from members where userId = '%s'", userId);
                ResultSet resultSetProducts = statementLogin.executeQuery(queryProducts);
                ResultSet resultSetAddress = statementAddress.executeQuery(queryAddress);

                while(resultSetProducts.next())
                {
                    String name = resultSetProducts.getString(1);
                    String company = resultSetProducts.getString(2);
                    String price = resultSetProducts.getString(3);
                    int quantity = resultSetProducts.getInt(4);
                    sellerId = resultSetProducts.getString(5);
                    product_id.add(new Pair(new Pair(name,company), new Pair(price, quantity)));
                }
                resultSetAddress.next();
                Address = resultSetAddress.getString(1);
                PhoneNumber = resultSetAddress.getString(2);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return product_id;
        }

        @Override
        protected void onPostExecute(ArrayList <Pair<Pair<String,String>,Pair<String, Integer>>> strings) {
            super.onPostExecute(strings);
            progressBarGetBuildings.setVisibility(View.GONE);

            ArrayList <String> buildings = new ArrayList<String>();

            for(int i=0;i<strings.size();i++){
                String  temp = strings.get(i).first.first + " (" + strings.get(i).first.second + ") " +
                "      Price: " + strings.get(i).second.first + "        Available :" + strings.get(i).second.second;
                buildings.add(temp);
            }

            productAdapter.addAll(buildings);

            lvProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    Intent intent = new Intent(getApplicationContext(), OrderDetailsActivity.class);

                    intent.putExtra("name", strings.get(position).first.first);
                    intent.putExtra("company", strings.get(position).first.second);
                    intent.putExtra("price", strings.get(position).second.first);
                    intent.putExtra("quantity", strings.get(position).second.second.toString());
                    intent.putExtra("userid",userId);
                    intent.putExtra("category",category);
                    intent.putExtra("address",Address);
                    intent.putExtra("phonenumber",PhoneNumber);
                    intent.putExtra("sellerid",sellerId);


                    startActivity(intent);

                }
            });

        }
    }
}