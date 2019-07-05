package com.radixdlt.client.application.translate.data.receipt;


import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class PurchasableArticle {
    private String name;
    private double price;
    private String articleId;
    private Optional<String> imageUrl;

    private PurchasableArticle(@NonNull String name, double price, @NonNull String articleId, @Nullable String imageUrl) {
        this.name = name;
        this.price = price;
        this.articleId = articleId;
        this.imageUrl = Optional.ofNullable(imageUrl);
    }

    public static PurchasableArticle of(
            @NonNull String name,
            double price,
            @Nullable String imageUrl,
            @Nullable String articleId
    ) {
        String indeedArticleId = Optional.ofNullable(articleId).orElse(UUID.randomUUID().toString());
        return new PurchasableArticle(name, price, indeedArticleId, imageUrl);
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getArticleId() {
        return articleId;
    }

    public Optional<String> getImageUrl() {
        return imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PurchasableArticle that = (PurchasableArticle) o;
        return Double.compare(that.price, price) == 0
                &&
                name.equals(that.name)
                &&
                articleId.equals(that.articleId)
                &&
                imageUrl.equals(that.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, articleId, imageUrl);
    }

    public static PurchasableArticle fineGroundCoffeePriced(double price) {
        // Pixabay has "Free for commercial use", more info: https://pixabay.com/service/license/
        String fineGroundCoffeeImageUrl = "https://cdn.pixabay.com/photo/2013/11/05/23/55/coffee-206142_960_720.jpg";
        return PurchasableArticle.of("Fine-ground coffee", price, fineGroundCoffeeImageUrl, "9043294d-3565-4b7e-ab11-5386be337bda");
    }

    public static PurchasableArticle fineGroundCoffee() {
        return PurchasableArticle.fineGroundCoffeePriced(1.25);
    }

    public static PurchasableArticle browniePriced(double price) {
        // Pixabay has "Free for commercial use", more info: https://pixabay.com/service/license/
        String brownieImageUrl = "https://cdn.pixabay.com/photo/2015/05/07/15/08/pastries-756601_960_720.jpg";
        return PurchasableArticle.of("Brownie", price, brownieImageUrl, "4f36a099-2e66-453b-a78c-0efd7e35915a");
    }

    public static PurchasableArticle brownie() {
        return PurchasableArticle.browniePriced(1.75);
    }

    public static PurchasableArticle croissantPriced(double price) {
        // Pixabay has "Free for commercial use", more info: https://pixabay.com/service/license/
        String croissantImageUrl = "https://cdn.pixabay.com/photo/2019/03/24/14/23/bread-4077812_960_720.jpg";
        return PurchasableArticle.of("Croissant", price, croissantImageUrl, "b2bc7616-6976-4690-b33b-1577a126c6c6");
    }

    public static PurchasableArticle croissant() {
        return PurchasableArticle.croissantPriced(1.5);
    }

    public static PurchasableArticle macaronPriced(double price) {
        // Pixabay has "Free for commercial use", more info: https://pixabay.com/service/license/
        String macaronImageUrl = "https://cdn.pixabay.com/photo/2017/07/28/14/23/macarons-2548810_960_720.jpg";
        return PurchasableArticle.of("Macaron", price, macaronImageUrl, "9043294d-3565-4b7e-ab11-5386be337bda");
    }

    public static PurchasableArticle macaron() {
        return PurchasableArticle.macaronPriced(2.5);
    }
}