package com.radixdlt.client.application.translate.data.receipt;

import org.radix.serialization2.client.SerializableObject;

import java.math.BigDecimal;
import java.util.Objects;

public class ReceiptItem {

    private PurchasableArticle article;

    // Using double  instead of int for representation of articles that can be broken
    // down into decimal values, e.g. 1,5 meters of rope
    private double quantity;

    private ReceiptItem(PurchasableArticle article, double quantity) {
        this.article = article;
        this.quantity = quantity;
    }

    public static ReceiptItem articleAndQuantity(PurchasableArticle article, double quantity) {
        return new ReceiptItem(article, quantity);
    }

    public static ReceiptItem one(PurchasableArticle article) {
        return ReceiptItem.articleAndQuantity(article, 1.0);
    }

    public PurchasableArticle getArticle() {
        return article;
    }

    public double getQuantity() {
        return quantity;
    }

    public BigDecimal calculateCost() {
        return BigDecimal.valueOf(quantity).multiply(BigDecimal.valueOf(article.getPrice()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReceiptItem that = (ReceiptItem) o;
        return Double.compare(that.quantity, quantity) == 0 &&
                article.equals(that.article);
    }

    @Override
    public int hashCode() {
        return Objects.hash(article, quantity);
    }
}
