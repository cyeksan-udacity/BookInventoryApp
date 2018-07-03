package com.example.android.products;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.products.data.ProductContract;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    ImageView productDetailImage;
    Button uploadImage;

    private final static int SELECT_PHOTO = 200;

    private final static int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 666;

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private Uri mCurrentProductUri;

    private EditText mNameEditText;

    private EditText mPriceEditText;

    private EditText mSupplierNameEditText;

    private EditText mSupplierPhoneEditText;

    public Spinner mQuantitySpinner;

    public static String nameString;
    public static String priceString;
    public static String supplierNameString;
    public static String supplierPhoneString;
    private Bitmap bitmap;


    public static int mQuantity = ProductContract.ProductEntry.QUANTITY0;

    private boolean mProductHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {

            setTitle(getString(R.string.editor_activity_title_new_product));

            invalidateOptionsMenu();
        } else {

            setTitle(getString(R.string.editor_activity_title_edit_product));

            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        mNameEditText = findViewById(R.id.edit_product_name);
        mPriceEditText = findViewById(R.id.edit_pet_weight);
        mQuantitySpinner = findViewById(R.id.spinner_quantity);
        mSupplierNameEditText = findViewById(R.id.edit_supplier_name);
        mSupplierPhoneEditText = findViewById(R.id.edit_supplier_phone);
        uploadImage = findViewById(R.id.select_image);

        mNameEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mQuantitySpinner.setOnTouchListener(mTouchListener);
        mSupplierNameEditText.setOnTouchListener(mTouchListener);
        mSupplierPhoneEditText.setOnTouchListener(mTouchListener);

        setupSpinner();
        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openImageSelector();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            }
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                        return;
                    }

                }
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null) {

            Uri selectedImage = data.getData();

            String[] filePatchColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePatchColumn, null, null, null);

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePatchColumn[0]);

            String picturePath = cursor.getString(columnIndex);

            cursor.close();

            bitmap = BitmapFactory.decodeFile(picturePath);

            bitmap = getBitmapFromUri(selectedImage);

            productDetailImage = findViewById(R.id.product_detail_image);

            productDetailImage.setImageBitmap(bitmap);
        }
    }

    public Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty()) {
            return null;
        }

        productDetailImage = (ImageView) findViewById(R.id.product_detail_image);
        int targetW = productDetailImage.getWidth();
        int targetH = productDetailImage.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e("AddActivity", "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e("AddActivity", "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }

    private void setupSpinner() {

        ArrayAdapter quantitySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_quantity, android.R.layout.simple_spinner_item);

        quantitySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mQuantitySpinner.setAdapter(quantitySpinnerAdapter);

        mQuantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (position != 0) {
                        mQuantity = position;
                    }
                } else {
                    mQuantity = ProductContract.ProductEntry.QUANTITY0;
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mQuantity = ProductContract.ProductEntry.QUANTITY0;
            }
        });
    }

    private void saveProduct() {

        ContentValues values = new ContentValues();
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, mQuantity);

        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME, supplierNameString);
        values.put(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE, supplierPhoneString);
        values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, priceString);

        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            boolean a = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE, byteArray);
        }
        if (mCurrentProductUri == null) {

            Uri newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Intent i = new Intent(EditorActivity.this, CatalogActivity.class);

                i.setData(mCurrentProductUri);

                startActivity(i);

                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                nameString = mNameEditText.getText().toString().trim();
                priceString = mPriceEditText.getText().toString().trim();
                supplierNameString = mSupplierNameEditText.getText().toString().trim();
                supplierPhoneString = mSupplierPhoneEditText.getText().toString().trim();

                if (mQuantity != ProductContract.ProductEntry.QUANTITY0 && !TextUtils.isEmpty(nameString) && !TextUtils.isEmpty(priceString) && !TextUtils.isEmpty(supplierNameString) && !TextUtils.isEmpty(supplierPhoneString)) {

                    saveProduct();
                    finish();
                    return true;

                } else {
                    Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                            Toast.LENGTH_SHORT).show();
                    break;
                }
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE};

        return new CursorLoader(this,
                mCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE);

            String name = cursor.getString(nameColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            Double price = cursor.getDouble(priceColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            int supplierPhone = cursor.getInt(supplierPhoneColumnIndex);

            mNameEditText.setText(name);
            mPriceEditText.setText(Double.toString(price));
            mSupplierNameEditText.setText(supplierName);
            mSupplierPhoneEditText.setText(Integer.toString(supplierPhone));

            byte[] bytesArray = cursor.getBlob(cursor.getColumnIndexOrThrow(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE));
            if (bytesArray != null) {
                bitmap = BitmapFactory.decodeByteArray(bytesArray, 0, bytesArray.length);
                ImageView editorImage = findViewById(R.id.product_detail_image);
                editorImage.setImageBitmap(bitmap);
            }

            switch (quantity) {

                case ProductContract.ProductEntry.QUANTITY1:
                    mQuantitySpinner.setSelection(1);
                    break;
                case ProductContract.ProductEntry.QUANTITY2:
                    mQuantitySpinner.setSelection(2);
                    break;
                case ProductContract.ProductEntry.QUANTITY3:
                    mQuantitySpinner.setSelection(3);
                    break;
                case ProductContract.ProductEntry.QUANTITY4:
                    mQuantitySpinner.setSelection(4);
                    break;
                case ProductContract.ProductEntry.QUANTITY5:
                    mQuantitySpinner.setSelection(5);
                    break;
                case ProductContract.ProductEntry.QUANTITY6:
                    mQuantitySpinner.setSelection(6);
                    break;
                case ProductContract.ProductEntry.QUANTITY7:
                    mQuantitySpinner.setSelection(7);
                    break;
                case ProductContract.ProductEntry.QUANTITY8:
                    mQuantitySpinner.setSelection(8);
                    break;
                case ProductContract.ProductEntry.QUANTITY9:
                    mQuantitySpinner.setSelection(9);
                    break;
                case ProductContract.ProductEntry.QUANTITY10:
                    mQuantitySpinner.setSelection(10);
                    break;
                case ProductContract.ProductEntry.QUANTITY11:
                    mQuantitySpinner.setSelection(11);
                    break;
                case ProductContract.ProductEntry.QUANTITY12:
                    mQuantitySpinner.setSelection(12);
                    break;
                case ProductContract.ProductEntry.QUANTITY13:
                    mQuantitySpinner.setSelection(13);
                    break;
                case ProductContract.ProductEntry.QUANTITY14:
                    mQuantitySpinner.setSelection(14);
                    break;
                case ProductContract.ProductEntry.QUANTITY15:
                    mQuantitySpinner.setSelection(15);
                    break;
                case ProductContract.ProductEntry.QUANTITY16:
                    mQuantitySpinner.setSelection(16);
                    break;
                case ProductContract.ProductEntry.QUANTITY17:
                    mQuantitySpinner.setSelection(17);
                    break;
                case ProductContract.ProductEntry.QUANTITY18:
                    mQuantitySpinner.setSelection(18);
                    break;
                case ProductContract.ProductEntry.QUANTITY19:
                    mQuantitySpinner.setSelection(19);
                    break;
                case ProductContract.ProductEntry.QUANTITY20:
                    mQuantitySpinner.setSelection(20);
                    break;
                default:
                    mQuantitySpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mNameEditText.setText("");
        mPriceEditText.setText("");
        mQuantitySpinner.setSelection(0);
        mSupplierNameEditText.setText("");
        mSupplierPhoneEditText.setText("");

    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
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

    private void deleteProduct() {
        if (mCurrentProductUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

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

    public void openImageSelector() {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO);
    }
}