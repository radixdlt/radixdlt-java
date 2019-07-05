package com.radixdlt.client.application.translate.data.receipt;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.radixdlt.client.application.translate.tokens.TokenTransfer;
import com.radixdlt.client.atommodel.accounts.RadixAddress;
import com.radixdlt.client.core.address.RadixUniverseConfig;
import com.radixdlt.client.core.network.jsonrpc.RadixJsonRpcClient;
import org.bouncycastle.util.encoders.Base64;
import org.junit.Test;
import org.radix.serialization2.DsonOutput;
import org.radix.serialization2.client.GsonJson;
import org.radix.serialization2.client.Serialize;
import org.radix.utils.primitives.Bytes;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

        String expectedJsonString = "{\n" +
                "  \"date\": " + hardcodedDate + ",\n" +
                "  \"items\": [\n" +
                "    {\n" +
                "      \"article\": {\n" +
                "        \"articleId\": \"9043294d-3565-4b7e-ab11-5386be337bda\",\n" +
                "        \"imageUrl\": {\n" +
                "          \"value\": \"https://cdn.pixabay.com/photo/2013/11/05/23/55/coffee-206142_960_720.jpg\"\n" +
                "        },\n" +
                "        \"name\": \"Fine-ground coffee\",\n" +
                "        \"price\": 1.25\n" +
                "      },\n" +
                "      \"quantity\": 2.0\n" +
                "    },\n" +
                "    {\n" +
                "      \"article\": {\n" +
                "        \"articleId\": \"b2bc7616-6976-4690-b33b-1577a126c6c6\",\n" +
                "        \"imageUrl\": {\n" +
                "          \"value\": \"https://cdn.pixabay.com/photo/2019/03/24/14/23/bread-4077812_960_720.jpg\"\n" +
                "        },\n" +
                "        \"name\": \"Croissant\",\n" +
                "        \"price\": 1.5\n" +
                "      },\n" +
                "      \"quantity\": 1.0\n" +
                "    },\n" +
                "    {\n" +
                "      \"article\": {\n" +
                "        \"articleId\": \"4f36a099-2e66-453b-a78c-0efd7e35915a\",\n" +
                "        \"imageUrl\": {\n" +
                "          \"value\": \"https://cdn.pixabay.com/photo/2015/05/07/15/08/pastries-756601_960_720.jpg\"\n" +
                "        },\n" +
                "        \"name\": \"Brownie\",\n" +
                "        \"price\": 1.5\n" +
                "      },\n" +
                "      \"quantity\": 1.0\n" +
                "    }\n" +
                "  ],\n" +
                "  \"merchant\": {\n" +
                "    \"category\": \"RESTAURANT\",\n" +
                "    \"name\": \"Starbucks\",\n" +
                "    \"radixAddress\": \"JH1P8f3znbyrDj8F4RWpix7hRkgxqHjdW2fNnKpR3v6ufXnknor\"\n" +
                "  },\n" +
                "  \"payment\": {\n" +
                "    \"amount\": 5.500\n" +
                "  }\n" +
                "}";

        Receipt receiptFromJson = gson.fromJson(expectedJsonString, Receipt.class);

        assertTrue(receiptFromJson.equals(receipt));

        byte[] receiptAsBytes = receipt.getSerializedJsonBytes();

        String receiptAsBase64String = Base64.toBase64String(receiptAsBytes);

        String expectedReceiptAsBase64String = "eyJtZXJjaGFudCI6eyJuYW1lIjoiU3RhcmJ1Y2tzIiwicmFkaXhBZGRyZXNzIjoiSkgxUDhmM3puYnlyRGo4RjRSV3BpeDdoUmtneHFIamRXMmZObktwUjN2NnVmWG5rbm9yIiwiY2F0ZWdvcnkiOiJSRVNUQVVSQU5UIn0sIml0ZW1zIjpbeyJhcnRpY2xlIjp7Im5hbWUiOiJGaW5lLWdyb3VuZCBjb2ZmZWUiLCJwcmljZSI6MS4yNSwiYXJ0aWNsZUlkIjoiOTA0MzI5NGQtMzU2NS00YjdlLWFiMTEtNTM4NmJlMzM3YmRhIiwiaW1hZ2VVcmwiOnsidmFsdWUiOiJodHRwczovL2Nkbi5waXhhYmF5LmNvbS9waG90by8yMDEzLzExLzA1LzIzLzU1L2NvZmZlZS0yMDYxNDJfOTYwXzcyMC5qcGcifX0sInF1YW50aXR5IjoyLjB9LHsiYXJ0aWNsZSI6eyJuYW1lIjoiQ3JvaXNzYW50IiwicHJpY2UiOjEuNSwiYXJ0aWNsZUlkIjoiYjJiYzc2MTYtNjk3Ni00NjkwLWIzM2ItMTU3N2ExMjZjNmM2IiwiaW1hZ2VVcmwiOnsidmFsdWUiOiJodHRwczovL2Nkbi5waXhhYmF5LmNvbS9waG90by8yMDE5LzAzLzI0LzE0LzIzL2JyZWFkLTQwNzc4MTJfOTYwXzcyMC5qcGcifX0sInF1YW50aXR5IjoxLjB9LHsiYXJ0aWNsZSI6eyJuYW1lIjoiQnJvd25pZSIsInByaWNlIjoxLjUsImFydGljbGVJZCI6IjRmMzZhMDk5LTJlNjYtNDUzYi1hNzhjLTBlZmQ3ZTM1OTE1YSIsImltYWdlVXJsIjp7InZhbHVlIjoiaHR0cHM6Ly9jZG4ucGl4YWJheS5jb20vcGhvdG8vMjAxNS8wNS8wNy8xNS8wOC9wYXN0cmllcy03NTY2MDFfOTYwXzcyMC5qcGcifX0sInF1YW50aXR5IjoxLjB9XSwiZGF0ZSI6MTU2MjMyOTA1Mzk1OSwicGF5bWVudCI6eyJhbW91bnQiOjUuNTAwfX0=";

        assertEquals(expectedReceiptAsBase64String, receiptAsBase64String);

        byte[] receipBytesFromBase64String = Base64.decode(expectedReceiptAsBase64String);

        Receipt receiptFromBytes = Receipt.fromSerializedJsonBytes(receipBytesFromBase64String);

        assertTrue(receiptFromBytes.equals(receipt));
    }
}
