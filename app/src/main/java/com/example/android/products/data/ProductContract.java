package com.example.android.products.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ProductContract {

    private ProductContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.products";


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PRODUCTS = "products";

    public static final class ProductEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public final static String TABLE_NAME = "product";

        public final static String _ID = BaseColumns._ID;

        public final static String COLUMN_PRODUCT_NAME = "name";

        public final static String COLUMN_PRODUCT_PRICE = "price";

        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";

        public final static String COLUMN_PRODUCT_IMAGE = "image";

        public static final int QUANTITY0 = 0;
        public static final int QUANTITY1 = 1;
        public static final int QUANTITY2 = 2;
        public static final int QUANTITY3 = 3;
        public static final int QUANTITY4 = 4;
        public static final int QUANTITY5 = 5;
        public static final int QUANTITY6 = 6;
        public static final int QUANTITY7 = 7;
        public static final int QUANTITY8 = 8;
        public static final int QUANTITY9 = 9;
        public static final int QUANTITY10 = 10;
        public static final int QUANTITY11 = 11;
        public static final int QUANTITY12 = 12;
        public static final int QUANTITY13 = 13;
        public static final int QUANTITY14 = 14;
        public static final int QUANTITY15 = 15;
        public static final int QUANTITY16 = 16;
        public static final int QUANTITY17 = 17;
        public static final int QUANTITY18 = 18;
        public static final int QUANTITY19 = 19;
        public static final int QUANTITY20 = 20;

        public static boolean isValidQuantity(int quantity) {
            if (quantity == QUANTITY0 || quantity == QUANTITY1 || quantity == QUANTITY2 || quantity == QUANTITY3
                    || quantity == QUANTITY4 || quantity == QUANTITY5 || quantity == QUANTITY6
                    || quantity == QUANTITY7 || quantity == QUANTITY8 || quantity == QUANTITY9
                    || quantity == QUANTITY10 || quantity == QUANTITY11 || quantity == QUANTITY12
                    || quantity == QUANTITY13 || quantity == QUANTITY14 || quantity == QUANTITY15
                    || quantity == QUANTITY16 || quantity == QUANTITY17 || quantity == QUANTITY18
                    || quantity == QUANTITY19 || quantity == QUANTITY20) {
                return true;
            }
            return false;
        }

        public final static String COLUMN_SUPPLIER_NAME = "supplierName";
        public final static String COLUMN_SUPPLIER_PHONE = "supplierPhone";
    }

}

