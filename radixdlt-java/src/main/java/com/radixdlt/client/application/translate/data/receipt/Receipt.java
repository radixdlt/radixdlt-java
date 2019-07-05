package com.radixdlt.client.application.translate.data.receipt;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.radixdlt.client.application.translate.tokens.TokenTransfer;
import com.radixdlt.client.application.translate.tokens.TokenUnitConversions;
import com.radixdlt.client.atommodel.accounts.RadixAddress;
import org.radix.serialization2.DsonOutput;
import org.radix.serialization2.SerializerId2;
import org.radix.serialization2.client.SerializableObject;
import org.radix.utils.RadixConstants;
import sun.security.krb5.internal.PAData;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Receipt {

    private Merchant merchant;

    private List<ReceiptItem> items;

    private long date;

    private Payment payment;

    Receipt(Merchant merchant, List<ReceiptItem> items, long date, Payment payment) {

        Objects.requireNonNull(merchant);
        Objects.requireNonNull(items);
        Objects.requireNonNull(payment);

        if (items.isEmpty()) {
            throw new IllegalArgumentException("Items cannot be empty");
        }

        this.merchant = merchant;
        this.items = items;
        this.date = date;
        this.payment = payment;
    }

    public BigDecimal getAmountToTransfer() {
        return BigDecimal.ZERO;
    }

    public byte[] getSerializedJsonBytes() {
        Gson gson = new Gson();
        String jsonString = gson.toJson(this);
        return jsonString.getBytes(RadixConstants.STANDARD_CHARSET);
    }

    public static Receipt fromSerializedJsonBytes(byte[] bytes) {
        String jsonString = new String(bytes, RadixConstants.STANDARD_CHARSET);
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Receipt.class);
    }

    public RadixAddress merchantRadixAddress() {
        return merchant.getAddress();
    }

    public Merchant getMerchant() {
        return merchant;
    }

    public List<ReceiptItem> getItems() {
        return items;
    }

    public long getDate() {
        return date;
    }

    public Payment getPayment() {
        return payment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Receipt receipt = (Receipt) o;
        return date == receipt.date &&
                merchant.equals(receipt.merchant) &&
                items.equals(receipt.items) &&
                payment.equals(receipt.payment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(merchant, items, date, payment);
    }
}
