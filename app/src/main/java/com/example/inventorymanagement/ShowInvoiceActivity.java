package com.example.inventorymanagement;

        import androidx.appcompat.app.AppCompatActivity;

        import android.Manifest;
        import android.content.Context;
        import android.content.Intent;
        import android.os.Bundle;
        import android.print.PrintAttributes;
        import android.print.PrintDocumentAdapter;
        import android.print.PrintManager;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.itextpdf.text.BaseColor;
        import com.itextpdf.text.Chunk;
        import com.itextpdf.text.Document;
        import com.itextpdf.text.DocumentException;
        import com.itextpdf.text.Element;
        import com.itextpdf.text.Font;
        import com.itextpdf.text.PageSize;
        import com.itextpdf.text.Paragraph;
        import com.itextpdf.text.pdf.BaseFont;
        import com.itextpdf.text.pdf.PdfWriter;
        import com.itextpdf.text.pdf.draw.LineSeparator;
        import com.itextpdf.text.pdf.draw.VerticalPositionMark;
        import com.karumi.dexter.Dexter;
        import com.karumi.dexter.PermissionToken;
        import com.karumi.dexter.listener.PermissionDeniedResponse;
        import com.karumi.dexter.listener.PermissionGrantedResponse;
        import com.karumi.dexter.listener.PermissionRequest;
        import com.karumi.dexter.listener.single.PermissionListener;

        import java.io.File;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;

public class ShowInvoiceActivity extends AppCompatActivity {

//    String name, category, vehicleId, slotId, paymentId, parkingId, userId, date, duration, city, building;
    String productname, company,name,address,deliverydate,amount,paymentId,orderId,userId;

    TextView tvProductName, tvcompany, tvName, tvAddress, tvDelivery, tvAmount, tvPaymentId;

    Button btnGoHome, btnPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_invoice);

        productname = getIntent().getStringExtra("productname");
        company = getIntent().getStringExtra("company");
        name = getIntent().getStringExtra("name");
        address = getIntent().getStringExtra("address");
        deliverydate = getIntent().getStringExtra("delivery");
        amount = getIntent().getStringExtra("amount");
        paymentId = getIntent().getStringExtra("paymentId");
        orderId = getIntent().getStringExtra("orderId");
        userId = getIntent().getStringExtra("userId");

        tvProductName = findViewById(R.id.tvName);
        tvcompany = findViewById(R.id.tvEmail);
        tvName = findViewById(R.id.tvVehicleId);
        tvAddress = findViewById(R.id.tvSlotId);
        tvDelivery = findViewById(R.id.tvPaymentId);
        tvAmount = findViewById(R.id.tvInvoiceDate);
        tvPaymentId = findViewById(R.id.tvInvoiceDuration);

        btnGoHome = findViewById(R.id.btnGoHome);
        btnPrint = findViewById(R.id.btnPrint);


        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted

                        btnPrint.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                createPDFFile(Common.getAppPath(ShowInvoiceActivity.this) + "invoice.pdf");
                            }
                        });

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            // navigate user to app settings

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast tostUserNotFound = Toast.makeText(getApplicationContext(), "Permission is required!", Toast.LENGTH_SHORT);
                                    tostUserNotFound.show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();


        btnGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("userId", userId);
                intent.putExtra("name", name);

                startActivity(intent);

            }
        });

        tvProductName.setText("Product\t: " + productname);
        tvcompany.setText("Category\t: " + company);
        tvName.setText("Customer\t: " + name);
        tvDelivery.setText("Delivery Date\t: " + deliverydate);
        tvAddress.setText("Address\t: " + address);
        tvAmount.setText("Amount\t: " + "$" + amount);
        tvPaymentId.setText("Payment ID\t: " + paymentId);


    }

    private void createPDFFile(String path) {
        if (new File(path).exists())
            new File(path).delete();

        try {
            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream(path));

            document.open();

            document.setPageSize(PageSize.A4);
            document.addCreationDate();

            BaseColor colorAccent = new BaseColor(0, 153, 204, 255);
            float fontSize = 20.0f;
            float valueFontSize = 26.0f;

            // Custom Font
            BaseFont fontName = BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);

            // Create Title of Document
            Font titleFont = new Font(fontName, 36.0f, Font.NORMAL, BaseColor.BLACK);
            addNewItem(document, "Payment Details", Element.ALIGN_CENTER, titleFont);

            // Add more

            Font orderNumberFont = new Font(fontName, fontSize, Font.NORMAL, colorAccent);
            addNewItem(document, "Payment ID:", Element.ALIGN_LEFT, orderNumberFont);

            Font orderNumberValueFont = new Font(fontName, valueFontSize, Font.NORMAL, BaseColor.BLACK);
            addNewItem(document, paymentId, Element.ALIGN_LEFT, orderNumberValueFont);

            addLineSeperator(document);

            addNewItem(document, "Product: ", Element.ALIGN_LEFT, orderNumberFont);
            addNewItem(document, productname, Element.ALIGN_LEFT, orderNumberValueFont);
            addNewItem(document, company, Element.ALIGN_LEFT, orderNumberValueFont);

            addLineSeperator(document);

            addNewItem(document, "User Name:", Element.ALIGN_LEFT, orderNumberFont);
            addNewItem(document, name, Element.ALIGN_LEFT, orderNumberValueFont);

            addLineSeperator(document);

            // Add product order details
            addLineSpace(document);
            // Item 1
            addNewItem(document, address, Element.ALIGN_LEFT, orderNumberValueFont);
            addItemWithLeftAndRight(document, "Delivery Date",deliverydate, orderNumberValueFont, orderNumberValueFont);

            addLineSeperator(document);

            // Total
            addLineSpace(document);
            addLineSpace(document);

            addItemWithLeftAndRight(document, "Total", "\u20B9 " + amount, titleFont, orderNumberValueFont);

            // Close
            document.close();
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();

            printPDF();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printPDF() {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

        try {
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(ShowInvoiceActivity.this, Common.getAppPath(ShowInvoiceActivity.this) + "invoice.pdf");
            printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build());
        } catch (Exception ex) {
            Log.e("DEV", ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void addItemWithLeftAndRight(Document document, String textLeft, String textRight, Font textLeftFont, Font textRightFont) {
        Chunk chunkTextLeft = new Chunk(textLeft, textLeftFont);
        Chunk chunkTextRight = new Chunk(textRight, textRightFont);

        Paragraph p = new Paragraph(chunkTextLeft);
        p.add((new Chunk(new VerticalPositionMark())));
        p.add(chunkTextRight);
        try {
            document.add(p);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    private void addLineSeperator(Document document) throws DocumentException {
        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 60));
        addLineSpace(document);
        document.add(new Chunk(lineSeparator));
        addLineSpace(document);
    }

    private void addLineSpace(Document document) throws DocumentException {
        document.add(new Paragraph(""));
    }

    private void addNewItem(Document document, String text, int align, Font font) throws DocumentException {
        Chunk chunk = new Chunk(text, font);

        Paragraph paragraph = new Paragraph(chunk);
        paragraph.setAlignment(align);

        document.add(paragraph);

    }
}