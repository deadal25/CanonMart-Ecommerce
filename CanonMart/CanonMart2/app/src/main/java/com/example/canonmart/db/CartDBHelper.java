package com.example.canonmart.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.canonmart.model.CartItem;
import com.example.canonmart.model.Product;

import java.util.ArrayList;
import java.util.List;

public class CartDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cart1.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CART = "cart";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_IMAGE = "image";

    public CartDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CART_TABLE = "CREATE TABLE " + TABLE_CART + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_PRICE + " REAL,"
                + COLUMN_QUANTITY + " INTEGER,"
                + COLUMN_IMAGE + " TEXT" + ")";
        db.execSQL(CREATE_CART_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }

    public void addCartItem(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Hapus item dengan nama yang sama sebelum menambahkan yang baru
        db.delete(TABLE_CART, COLUMN_NAME + " = ?", new String[]{product.getName()});

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, product.getName());
        values.put(COLUMN_PRICE, product.getSalePrice());
        values.put(COLUMN_QUANTITY, 1);
        values.put(COLUMN_IMAGE, product.getImage());
        db.insert(TABLE_CART, null, values);

        db.close();
    }

    public void removeCartItem(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, COLUMN_NAME + " = ?", new String[]{name});
        db.close();
    }

    public void removeAllCartItemsWithName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, COLUMN_NAME + " = ?", new String[]{name});
        db.close();
    }

    public boolean isCartItem(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CART, new String[]{COLUMN_ID}, COLUMN_NAME + " = ?", new String[]{name}, null, null, null);
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    public int getCartItemQuantity(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        int quantity = 0;
        Cursor cursor = db.query(TABLE_CART, new String[]{COLUMN_QUANTITY}, COLUMN_NAME + " = ?", new String[]{name}, null, null, null);
        if (cursor.moveToFirst()) {
            quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
        }
        cursor.close();
        db.close();
        return quantity;
    }

    public void updateCartItemQuantity(String name, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUANTITY, quantity);
        db.update(TABLE_CART, values, COLUMN_NAME + " = ?", new String[]{name});
        db.close();
    }


    public List<CartItem> getAllCartItems() {
        List<CartItem> cartItemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CART, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE));
                cartItemList.add(new CartItem(name, price, quantity, imageUrl));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cartItemList;
    }

    public void deleteAllItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, null, null);
        db.close();
    }


}
