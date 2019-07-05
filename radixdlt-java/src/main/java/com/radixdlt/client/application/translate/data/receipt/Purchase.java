package com.radixdlt.client.application.translate.data.receipt;

import org.checkerframework.checker.nullness.Opt;

import java.util.ArrayList;
import java.util.Optional;
import java.util.OptionalInt;

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

        public Purchase addArticle(PurchasableArticle article) {
            return addArticle(article, 1.0);
        }

        public Purchase merchant(Merchant merchant) {
            this.merchant = merchant;
            return this;
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
