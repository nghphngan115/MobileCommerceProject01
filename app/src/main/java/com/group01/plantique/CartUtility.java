package com.group01.plantique;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.group01.plantique.model.Product;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CartUtility {
    private static final String CART_PREFS = "CartPrefs";

    public static void addToCart(Context context, Product product) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        // Lấy danh sách sản phẩm từ SharedPreferences
        String json = sharedPreferences.getString("cart", null);
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        ArrayList<Product> cartProducts = gson.fromJson(json, type);

        if (cartProducts == null) {
            cartProducts = new ArrayList<>();
        }

        // Thêm sản phẩm vào danh sách giỏ hàng
        cartProducts.add(product);

        // Lưu lại danh sách giỏ hàng vào SharedPreferences
        String updatedCartJson = gson.toJson(cartProducts);
        editor.putString("cart", updatedCartJson);
        editor.apply();
    }

    public static ArrayList<Product> getCartProducts(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("cart", null);
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void saveCartProducts(Context context, ArrayList<Product> cartProducts) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cartProducts);
        editor.putString("cart", json);
        editor.apply();
    }
}
