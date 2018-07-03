package com.example.android.products.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.products.data.ProductContract.ProductEntry.TABLE_NAME;

public class ProductDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "product.db";

    private static final int DATABASE_VERSION = 1;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + com.example.android.products.data.ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + com.example.android.products.data.ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + " TEXT, "
                + com.example.android.products.data.ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER, "
                + com.example.android.products.data.ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL, "
                + com.example.android.products.data.ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + com.example.android.products.data.ProductContract.ProductEntry.COLUMN_SUPPLIER_PHONE + " INTEGER, "
                + com.example.android.products.data.ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE + " BLOB);";

        database.execSQL(SQL_CREATE_PRODUCT_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}