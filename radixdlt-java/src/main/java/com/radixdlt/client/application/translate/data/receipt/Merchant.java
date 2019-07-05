package com.radixdlt.client.application.translate.data.receipt;

import com.radixdlt.client.atommodel.accounts.RadixAddress;

import java.util.Objects;

public class Merchant {
    private String name;
    private String radixAddress; // base58
    private MerchantCategory category;

    public Merchant(String name, RadixAddress address, MerchantCategory category) {
        this.name = name;
        this.radixAddress = address.toString();
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public RadixAddress getAddress() {
        return RadixAddress.from(radixAddress);
    }

    public MerchantCategory getCategory() {
        return category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Merchant merchant = (Merchant) o;
        return name.equals(merchant.name)
                &&
                radixAddress.equals(merchant.radixAddress)
                &&
                category == merchant.category;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, radixAddress, category);
    }
}
