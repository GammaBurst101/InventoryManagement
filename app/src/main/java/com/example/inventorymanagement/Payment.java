package com.example.inventorymanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class Payment extends Activity implements PaymentResultListener {

    Button btnPay;
    TextView tvAmountToPay, deliveryDate, productName, Address;

    String userId, name, address, password, contactNum, email, slot ,company,delAddress,deliverydate,productname,sellerId;

    String companyName = "PSR Pvt. Ltd.";


    ProgressBar progressbar;

    Integer amount,price,quantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e("Error"+Thread.currentThread().getStackTrace()[2],paramThrowable.getLocalizedMessage());
            }
        });

        setContentView(R.layout.activity_payment);



        btnPay = findViewById(R.id.btnPay);
        tvAmountToPay = findViewById(R.id.tvAmountToPay);
        deliveryDate = findViewById(R.id.tvSlot);
        progressbar = findViewById(R.id.progressBarPay);
        productName = findViewById(R.id.tvSelectedDate);
        Address = findViewById(R.id.tvDuration);

        userId = getIntent().getStringExtra("userid");
        productname = getIntent().getStringExtra("name");
        delAddress = getIntent().getStringExtra("address");
        String str = getIntent().getStringExtra("price");
        sellerId = getIntent().getStringExtra("sellerid");
        price = Integer.parseInt(str.substring(1,str.length()));
        quantity = Integer.parseInt(getIntent().getStringExtra("btquantity"));
        amount = price*quantity;

        tvAmountToPay.setText("Amount to Pay : " + Integer.toString(amount));


          productName.setText(getIntent().getStringExtra("name") + " (" + getIntent().getStringExtra("company") + ")");
          Address.setText(getIntent().getStringExtra("address"));
          deliveryDate.setText("2022-01-01");
          deliverydate = "2022-01-01";
          company = getIntent().getStringExtra("company");


        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PayAsync().execute();
            }
        });

    }

    @Override
    public void onPaymentSuccess(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Processing Payment");

        builder.setMessage(s);

        builder.show();


        String params[] = {s};
        new EnterIntoDatabase().execute(params);

    }




    class EnterIntoDatabase extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            btnPay.setText("Processing Payment, Please Wait");
            btnPay.setEnabled(false);

        }

        @Override
        protected Void doInBackground(String... params) {

            String paymentId = params[0];

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection("jdbc:mysql://humaraserver.mysql.database.azure.com:3306/inventory?useSSL=true", "harshit", "Parking@123");
                Statement statementLogin = connection.createStatement();

                String enterRegistration = String.format("INSERT INTO `orders` (`buyerId`,`sellerId`, `name`, `company`, `price`, `address`, `amount`, `delivery`,`invoiceId`,`quantity`) VALUES ('%s','%s', '%s', '%s', '%s', '%s', '%s', '%s','%s','%s');", userId,sellerId, productname, company, price, delAddress,amount,deliverydate, paymentId,quantity );
                int resultCheckAvailability = statementLogin.executeUpdate(enterRegistration);

                Intent intent = new Intent(getApplicationContext(), ShowInvoiceActivity.class);

                String am = amount.toString();
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("productname", productname);
                intent.putExtra("company", company);
                intent.putExtra("price", price);
                intent.putExtra("amount", am);
                intent.putExtra("quantity", quantity);
                intent.putExtra("delivery", deliverydate);
                intent.putExtra("paymentId", paymentId);
                intent.putExtra("address", delAddress);
                intent.putExtra("userid", userId);
                intent.putExtra("sellerid",sellerId);

//                System.out.println("userID : "+userId);




                startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            btnPay.setText("Pay");

            btnPay.setEnabled(true);
        }
    }


    @Override
    public void onPaymentError(int i, String s) {

        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();


    }
class PayAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressbar.setVisibility(View.VISIBLE);
        }


        @Override
        protected Void doInBackground(Void... voids) {

            Connection connection = null;
            Statement statementGetVal = null;
            ResultSet resultSetGetVal = null;

            try {

                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://humaraserver.mysql.database.azure.com:3306/inventory?useSSL=true", "harshit", "Parking@123");
                statementGetVal = connection.createStatement();

                String queryGetVal = String.format("select * from members WHERE userId = '%s';", userId);
                resultSetGetVal = statementGetVal.executeQuery(queryGetVal);

                while(resultSetGetVal.next()) {
                    name = resultSetGetVal.getString("name");
                    email = resultSetGetVal.getString("email");
                    address = resultSetGetVal.getString("address");
                    contactNum = resultSetGetVal.getString("contactNumber");
                    password = resultSetGetVal.getString(3);
                }

//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//
//                        // Stuff that updates the UI
//                        etName.setText(name);
//                        etEmail.setText(email);
//                        etAddress.setText(address);
//                        etContactNum.setText(contactNum);
//                        etVehicleId.setText(vehicleId);
//                    }
//                });


                try {

                    resultSetGetVal.close();
                    statementGetVal.close();
                    connection.close();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            } catch (Exception e) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast toastPasswordIncorrect = Toast.makeText(getApplicationContext(), "GetAsync Exception!", Toast.LENGTH_SHORT);
                        toastPasswordIncorrect.show();
                    }
                });


                e.printStackTrace();
            }

            Checkout checkout = new Checkout();

            checkout.setKeyID("rzp_test_G3SP6nMsC777Aa");

            checkout.setImage(R.drawable.logo);

            JSONObject object = new JSONObject();

            try {
                object.put("name", companyName);

                object.put("description", slot);

                object.put("theme.color", "#000000");

                object.put("currency", "INR");

                object.put("amount", amount*100);

                object.put("prefill.contact", contactNum);

                object.put("prefill.email", email);

                object.put("send_sms_hash", false);

                checkout.open(Payment.this, object);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressbar.setVisibility(View.INVISIBLE);
        }
    }
}
