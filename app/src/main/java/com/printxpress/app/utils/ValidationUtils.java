package com.printxpress.app.utils;

import java.util.Calendar;

public final class ValidationUtils {

    private ValidationUtils() {}

    public static boolean validSriLankanPhone(String value) {
        return value != null && value.matches("07\\d{8}");
    }

    public static boolean passesLuhn(String number) {
        if (number == null || !number.matches("\\d{13,16}")) {
            return false;
        }
        int sum = 0;
        boolean alternate = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = number.charAt(i) - '0';
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n -= 9;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }

    public static boolean validExpiry(String expiry, int currentMonth, int currentYear) {
        if (expiry == null || !expiry.matches("\\d{2}/\\d{2}")) {
            return false;
        }
        int month = Integer.parseInt(expiry.substring(0, 2));
        int year = 2000 + Integer.parseInt(expiry.substring(3));
        return month >= 1 && month <= 12
            && (year > currentYear || (year == currentYear && month >= currentMonth));
    }

    public static boolean validExpiry(String expiry) {
        Calendar now = Calendar.getInstance();
        return validExpiry(expiry, now.get(Calendar.MONTH) + 1, now.get(Calendar.YEAR));
    }

    public static double orderTotal(double basePrice, int quantity) {
        if (quantity < 1 || basePrice < 0) {
            return 0;
        }
        return basePrice * quantity;
    }
}
