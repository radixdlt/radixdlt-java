package com.radixdlt.client.application.translate.data.receipt;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class Payment {
    private BigDecimal amount;

    public Payment(BigDecimal amount) {
        this.amount = amount;
    }

    public static Payment fromItems(List<ReceiptItem> receiptItems) {
        BigDecimal cost = receiptItems.stream().map(i -> i.calculateCost()).reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
        return new Payment(cost);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Payment payment = (Payment) o;
        return amount.equals(payment.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }
}
