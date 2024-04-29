package com.group01.plantique.model;

import java.text.NumberFormat;

public class FormatCurrency {
    public static String formatCurrency(int amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setGroupingUsed(true); // This will add commas as thousand separators
        return formatter.format(amount) + " đ"; // Append the currency symbol or any string if needed
    }
}
