package com.radixdlt.client.application.translate.data.receipt;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Purchase {
        private Merchant merchant;
        private ArrayList<ReceiptItem> items = new ArrayList<>();
        private Optional<Long> hardCodedDateForTesting;

        public Purchase() {

        }

        public Purchase(Merchant merchant) {
            this.merchant = merchant;
            this.hardCodedDateForTesting = Optional.empty();
        }

        Purchase(Merchant merchant, long date) {
            this.merchant = merchant;
            this.hardCodedDateForTesting = Optional.of(Long.valueOf(date));
        }

        public Purchase addArticle(PurchasableArticle article, double quantity) {
            ReceiptItem receiptItem = ReceiptItem.articleAndQuantity(article, quantity);
            items.add(receiptItem);
            return this;
        }

        public Purchase addArticles(List<PurchasableArticle> articleList) {
            articleList.forEach(this::addArticle);
            return this;
        }

        public Purchase addArticle(PurchasableArticle article) {
            return addArticle(article, 1.0);
        }

        public Purchase merchant(Merchant merchant) {
            this.merchant = merchant;
            return this;
        }

        public double costOfArticles() {
            return items.stream().map(i -> i.calculateCost()).reduce(BigDecimal.ZERO, BigDecimal::add).doubleValue();
        }

        public Receipt getReceipt() {
            Payment payment = Payment.fromItems(items);
            final long date;
            if (hardCodedDateForTesting.isPresent()) {
                date = hardCodedDateForTesting.get().longValue();
            } else {
                date = System.currentTimeMillis();
            }
            return new Receipt(merchant, items, date, payment);
        }
}
