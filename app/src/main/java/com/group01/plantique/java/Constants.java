package com.group01.plantique.java;

import com.group01.plantique.model.Category;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static List<Category> productCategories = new ArrayList<>();

    public static void updateCategories(List<Category> categories) {
        productCategories.clear();
        productCategories.addAll(categories);
    }
}

