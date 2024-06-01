package com.example.canonmart.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private String name;
    private String sku;
    private double salePrice;
    private String image;
    private String longDescription;
    private boolean isFavorite;

    public Product() {
        this.name = "";
        this.sku = "";
        this.salePrice = 0.0;
        this.image = "";
        this.longDescription = "";
        this.isFavorite = false;
    }

    protected Product(Parcel in) {
        name = in.readString();
        sku = in.readString();
        salePrice = in.readDouble();
        image = in.readString();
        longDescription = in.readString();
        isFavorite = in.readByte() != 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(sku);
        dest.writeDouble(salePrice);
        dest.writeString(image);
        dest.writeString(longDescription);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
    }
}
