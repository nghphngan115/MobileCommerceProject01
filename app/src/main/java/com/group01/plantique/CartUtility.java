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

    public static void saveCartProducts(Context context, ArrayList<Product> cartProducts) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cartProducts);
        editor.putString("cart", json);
        editor.apply();
    }

    public static ArrayList<Product> getCartProducts(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CART_PREFS, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("cart", null);
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        return json != null ? gson.fromJson(json, type) : new ArrayList<>();
    }
}

