package com.example.canonmart.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.canonmart.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "productsDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_FAVORITES = "favorites";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SKU = "sku";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_PRICE = "price";
    private static final String KEY_DESCRIPTION = "longDescription";

    public ProductDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_FAVORITES_TABLE = "CREATE TABLE " + TABLE_FAVORITES +
                "(" +
                KEY_ID + " INTEGER PRIMARY KEY," +
                KEY_NAME + " TEXT," +
                KEY_SKU + " TEXT," +
                KEY_IMAGE + " TEXT," +
                KEY_PRICE + " REAL," +
                KEY_DESCRIPTION + " TEXT" +
                ")";
        db.execSQL(CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrade if needed
    }

    public void addFavorite(Product product) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, product.getName());
        values.put(KEY_SKU, product.getSku());
        values.put(KEY_IMAGE, product.getImage());
        values.put(KEY_PRICE, product.getSalePrice());
        values.put(KEY_DESCRIPTION, product.getLongDescription());
        db.insert(TABLE_FAVORITES, null, values);
        db.close();
    }

    public void removeFavorite(String sku) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_FAVORITES, KEY_SKU + "=?", new String[]{sku});
        db.close();
    }

    public List<Product> getAllFavoriteProducts() {
        List<Product> favoriteProducts = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVORITES, null);
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setName(cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)));
                product.setSku(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SKU)));
                product.setImage(cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE)));
                product.setSalePrice(cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_PRICE)));
                product.setLongDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)));
                favoriteProducts.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return favoriteProducts;
    }

    public boolean isFavorite(String sku) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVORITES + " WHERE " + KEY_SKU + "=?", new String[]{sku});
        boolean isFavorite = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isFavorite;
    }
}
