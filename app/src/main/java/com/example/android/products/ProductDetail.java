package com.example.android.products;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.products.data.ProductContract;

import static com.example.android.products.PetCursorAdapter.bitmap;

public class ProductDetail extends AppCompatActivity {

    Integer quantity;
    TextView nameTV;
    TextView priceTV;
    TextView quantityTV;
    TextView supplierNameTV;
    TextView supplierPhoneTV;
    Uri mCurrentUri;
    Uri mNewUri;
    Button editButton;
    Button minusButton;
    Button plusButton;
    Button deleteButton;
    Button contactButton;
    public String supplierPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();

        Intent i = getIntent();
        mNewUri = i.getData();
        nameTV = (TextView) findViewById(R.id.product_name);
        priceTV = (TextView) findViewById(R.id.product_price);
        quantityTV = (TextView) findViewById(R.id.product_quantity);
        supplierNameTV = (TextView) findViewById(R.id.supplier_name);
        supplierPhoneTV = (TextView) findViewById(R.id.supplier_phone);
        editButton = (Button) findViewById(R.id.edit_button);
        minusButton = (Button) findViewById(R.id.minus_button);
        plusButton = (Button) findViewById(R.id.plus_button);
        deleteButton = (Button) findViewById(R.id.delete_button);
        contactButton = (Button) findViewById(R.id.contact_button);

        ImageView productImage = (ImageView) findViewById(R.id.product_detail_image);

        Cursor c = managedQuery(mCurrentUri, null, null, null, "name");

        if (c.moveToFirst()) {
            do {
                String productName = c.getString(c.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME));
                String productPrice = c.getString(c.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE));
                String productQuantity = c.getString(c.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY));
                String supplierName = c.getString(c.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME));
                supplierPhone = c.getString(c.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE));
                quantity = c.getInt(c.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY));

                byte[] imageBytes = c.getBlob(c.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE));

                nameTV.setText(" " + productName);
                priceTV.setText(" " + productPrice);
                quantityTV.setText(" " + productQuantity.toString());
                supplierNameTV.setText(" " + supplierName);
                supplierPhoneTV.setText(" " + supplierPhone);

                if (imageBytes != null) {
                    bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    productImage.setImageBitmap(bitmap);

                }

            } while (c.moveToNext());
        }

        Cursor cNew = managedQuery(mNewUri, null, null, null, "name");

        if (cNew.moveToFirst()) {
            do {
                String productName = cNew.getString(cNew.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME));
                String productPrice = cNew.getString(cNew.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE));
                String productQuantity = cNew.getString(cNew.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY));
                String supplierName = cNew.getString(cNew.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME));
                String supplierPhone = cNew.getString(cNew.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE));
                quantity = cNew.getInt(cNew.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY));

                nameTV.setText(" " + productName);
                priceTV.setText(" " + productPrice);
                quantityTV.setText(" " + productQuantity);
                supplierNameTV.setText(" " + supplierName);
                supplierPhoneTV.setText(" " + supplierPhone);

            } while (cNew.moveToNext());
        }

        editButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(ProductDetail.this, EditorActivity.class);

                intent.setData(mCurrentUri);
                startActivity(intent);
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                if (quantity > 0) {

                    quantity = quantity - 1;

                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
                    getContentResolver().update(mCurrentUri, values, null, null);

                    quantityTV.setText(quantity.toString());
                }
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (quantity < 20) {

                    quantity = quantity + 1;

                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
                    getContentResolver().update(mCurrentUri, values, null, null);
                    quantityTV.setText(quantity.toString());
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDeleteConfirmationDialog();
            }
        });

        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + supplierPhone));
                startActivity(intent);
            }
        });

    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deletePet();
            }
        });


        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePet() {
        if (mCurrentUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentUri, null, null);

            if (rowsDeleted == 0) {

                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

}


