package com.radixdlt.client.application.translate.data.receipt;

import org.radix.serialization2.client.SerializableObject;

public enum MerchantCategory {
    RESTAURANT("Restaurant", 1000),
    FLOWERS("Flowers", 2000);

    private final String name;
    private final int categoryCode;

    MerchantCategory(String name, int categoryCode) {
        this.name = name;
        this.categoryCode = categoryCode;
    }

    public static MerchantCategory valueOf(int categoryCode) {
        for (MerchantCategory category : MerchantCategory.values()) {
            if (category.categoryCode == categoryCode) {
                return category;
            }
        }

        throw new IllegalArgumentException("No category type of value: " + categoryCode);
    }
}