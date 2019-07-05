package com.radixdlt.client.application.translate.data.receipt;

import com.google.gson.Gson;
import com.radixdlt.client.atommodel.accounts.RadixAddress;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReceiptModelSerializationTests {

    @Test
    public void testSerialization() {
        RadixAddress merchantAddress = mock(RadixAddress.class);
        when(merchantAddress.toString()).thenReturn("JH1P8f3znbyrDj8F4RWpix7hRkgxqHjdW2fNnKpR3v6ufXnknor");
        Merchant starBucks = new Merchant("Starbucks", merchantAddress, MerchantCategory.RESTAURANT);

        long hardcodedDate = 1562329053959L;

        Purchase purchase = new Purchase(starBucks, hardcodedDate);

        purchase.addArticle(PurchasableArticle.fineGroundCoffee(), 2);
        purchase.addArticle(PurchasableArticle.croissant());
        purchase.addArticle(PurchasableArticle.browniePriced(1.5));
        Receipt receipt = purchase.getReceipt();

        Gson gson = new Gson();

        byte[] receiptAsBytes = receipt.getSerializedJsonBytes();

        String receiptAsBase64String = Base64.toBase64String(receiptAsBytes);

        String expectedReceiptAsBase64String = "eyJtZXJjaGFudCI6eyJuYW1lIjoiU3RhcmJ1Y2tzIiwicmFkaXhBZ"
                +
                "GRyZXNzIjoiSkgxUDhmM3puYnlyRGo4RjRSV3BpeDdoUmtneHFIamRXMmZObktwUjN2NnVmWG5rbm9yIiwiY"
                +
                "2F0ZWdvcnkiOiJSRVNUQVVSQU5UIn0sIml0ZW1zIjpbeyJhcnRpY2xlIjp7Im5hbWUiOiJGaW5lLWdyb3VuZ"
                +
                "CBjb2ZmZWUiLCJwcmljZSI6MS4yNSwiYXJ0aWNsZUlkIjoiOTA0MzI5NGQtMzU2NS00YjdlLWFiMTEtNTM4N"
                +
                "mJlMzM3YmRhIiwiaW1hZ2VVcmwiOnsidmFsdWUiOiJodHRwczovL2Nkbi5waXhhYmF5LmNvbS9waG90by8yM"
                +
                "DEzLzExLzA1LzIzLzU1L2NvZmZlZS0yMDYxNDJfOTYwXzcyMC5qcGcifX0sInF1YW50aXR5IjoyLjB9LHsiY"
                +
                "XJ0aWNsZSI6eyJuYW1lIjoiQ3JvaXNzYW50IiwicHJpY2UiOjEuNSwiYXJ0aWNsZUlkIjoiYjJiYzc2MTYtN"
                +
                "jk3Ni00NjkwLWIzM2ItMTU3N2ExMjZjNmM2IiwiaW1hZ2VVcmwiOnsidmFsdWUiOiJodHRwczovL2Nkbi5wa"
                +
                "XhhYmF5LmNvbS9waG90by8yMDE5LzAzLzI0LzE0LzIzL2JyZWFkLTQwNzc4MTJfOTYwXzcyMC5qcGcifX0sI"
                +
                "nF1YW50aXR5IjoxLjB9LHsiYXJ0aWNsZSI6eyJuYW1lIjoiQnJvd25pZSIsInByaWNlIjoxLjUsImFydGljb"
                +
                "GVJZCI6IjRmMzZhMDk5LTJlNjYtNDUzYi1hNzhjLTBlZmQ3ZTM1OTE1YSIsImltYWdlVXJsIjp7InZhbHVlI"
                +
                "joiaHR0cHM6Ly9jZG4ucGl4YWJheS5jb20vcGhvdG8vMjAxNS8wNS8wNy8xNS8wOC9wYXN0cmllcy03NTY2M"
                +
                "DFfOTYwXzcyMC5qcGcifX0sInF1YW50aXR5IjoxLjB9XSwiZGF0ZSI6MTU2MjMyOTA1Mzk1OSwicGF5bWVud"
                +
                "CI6eyJhbW91bnQiOjUuNTAwfX0=";

        assertEquals(expectedReceiptAsBase64String, receiptAsBase64String);

        byte[] receipBytesFromBase64String = Base64.decode(expectedReceiptAsBase64String);

        Receipt receiptFromBytes = Receipt.fromSerializedJsonBytes(receipBytesFromBase64String);

        assertTrue(receiptFromBytes.equals(receipt));
    }
}
