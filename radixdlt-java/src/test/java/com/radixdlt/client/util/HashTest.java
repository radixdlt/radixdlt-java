package com.radixdlt.client.util;

import org.radix.crypto.Hash;
import org.junit.Test;
import org.radix.utils.primitives.Bytes;

import java.nio.charset.StandardCharsets;

import static junit.framework.TestCase.assertEquals;

public class HashTest {

    @Test
    public void test_sha256_hash_as_reference_for_other_libraries()  {
        byte[] data = "Hello Radix".getBytes(StandardCharsets.UTF_8);
        byte[] singleHash = Hash.sha256(data);
        byte[] doubleHash = Hash.sha256(singleHash);

        // These hashes as just the result of running the sha256 once and output the values
        // These are then used as reference for other libraries, especially Swift which
        // lacks native Sha256 methods.
        assertEquals("a70ec0d9b7eefd374db5209dcb2c071d89a9620089b617e06066ed8aaf13bb06", Bytes.toHexString(singleHash));
        assertEquals("4cc218de5682abb5765f995184780362fd9dba5b86a60f5412118adbee0b7cb3", Bytes.toHexString(doubleHash));
    }
}