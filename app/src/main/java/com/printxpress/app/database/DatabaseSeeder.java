package com.printxpress.app.database;

import com.printxpress.app.database.entities.Notification;
import com.printxpress.app.database.entities.Product;
import com.printxpress.app.database.entities.Promotion;
import java.util.Arrays;
import java.util.List;

public final class DatabaseSeeder {

    private DatabaseSeeder() {}

    public static void seedProducts(AppDatabase db) {
        if (db.productDao().count() >= 24) {
            return;
        }

        List<Product> catalog = Arrays.asList(
            new Product("Standard Business Cards", "Business Cards", "350gsm full-colour cards", 1200, "Standard", "Glossy,Matte,Silk"),
            new Product("Premium Business Cards", "Business Cards", "Luxury textured finish", 2500, "Standard", "Glossy,Matte,Silk"),
            new Product("Square Business Cards", "Business Cards", "Modern rounded square cards", 1800, "65x65mm", "Glossy,Matte"),
            new Product("Folded Business Cards", "Business Cards", "Double-space folded cards", 3200, "90x55mm", "Matte,Silk"),
            new Product("Flyers A5", "Flyers", "Full-colour compact flyers", 800, "A5", "Glossy,Matte"),
            new Product("Flyers A4", "Flyers", "Full-colour large flyers", 1500, "A4", "Glossy,Matte"),
            new Product("DL Tri-fold Flyers", "Flyers", "Three-panel marketing leaflet", 1350, "DL", "Glossy,Matte,Silk"),
            new Product("Flyers A6", "Flyers", "Pocket-size promotional flyers", 650, "A6", "Glossy,Matte"),
            new Product("Banner 2x4ft", "Banners", "Outdoor vinyl banner", 3500, "2x4ft", "Standard"),
            new Product("Banner 3x6ft", "Banners", "Large outdoor vinyl banner", 6000, "3x6ft", "Standard"),
            new Product("Pull-up Banner", "Banners", "Portable retractable display", 12500, "2x6ft", "Standard"),
            new Product("X-Stand Banner", "Banners", "Lightweight event display", 8500, "2x5ft", "Standard"),
            new Product("Poster A3", "Posters", "High-resolution promotional poster", 1800, "A3", "Glossy,Matte"),
            new Product("Poster A2", "Posters", "Large-format wall poster", 2800, "A2", "Glossy,Matte"),
            new Product("Poster A1", "Posters", "Oversized campaign poster", 4500, "A1", "Glossy,Matte"),
            new Product("Stickers Sheet", "Stickers", "Mixed sticker sheet", 950, "A4", "Glossy,Matte"),
            new Product("Vinyl Sticker Pack", "Stickers", "Weatherproof vinyl sticker set", 1600, "Small,Medium", "Glossy,Matte"),
            new Product("Die-cut Stickers", "Stickers", "Custom contour-cut stickers", 1250, "Small,Medium,Large", "Glossy,Matte"),
            new Product("T-Shirt Print", "T-Shirts", "Custom garment print", 2200, "S,M,L,XL", "Standard"),
            new Product("Premium T-Shirt Print", "T-Shirts", "Heavyweight premium cotton", 3500, "S,M,L,XL", "Standard"),
            new Product("Front & Back T-Shirt", "T-Shirts", "Coordinated two-sided print", 4200, "S,M,L,XL", "Standard"),
            new Product("Mug Print", "Mugs", "Full-wrap ceramic mug", 1800, "Standard", "Standard"),
            new Product("Magic Mug", "Mugs", "Heat-reveal colour-changing mug", 2800, "Standard", "Standard"),
            new Product("Panoramic Photo Mug", "Mugs", "Edge-to-edge photo wrap", 2300, "Standard", "Standard")
        );

        for (Product product : catalog) {
            if (db.productDao().getByName(product.name) == null) {
                db.productDao().insert(product);
            }
        }
    }

    public static void seedNotifications(AppDatabase db, long uid) {
        if (db.notificationDao().count(uid) > 0) {
            return;
        }

        long now = System.currentTimeMillis();
        db.notificationDao().insertAll(Arrays.asList(
            new Notification(uid, "Welcome to PrintXpress", "Your order and promotion updates will appear here.", "welcome", false, now),
            new Notification(uid, "Festive Season", "20% off selected print packages this week.", "promotion", false, now - 3600000)
        ));
    }

    public static void seedPromotions(AppDatabase db) {
        if (db.promotionDao().count() > 0) {
            return;
        }

        long now = System.currentTimeMillis();
        db.promotionDao().insertAll(Arrays.asList(
            new Promotion("Business Starter", "500 premium cards + 250 flyers", "15% OFF", true, now),
            new Promotion("Event Pack", "2 banners + 100 posters", "20% OFF", true, now - 1),
            new Promotion("Festive Merchandise", "Custom mugs and t-shirts", "10% OFF", true, now - 2)
        ));
    }
}
