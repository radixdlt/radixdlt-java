package com.radixdlt.client.core.crypto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DiffieHellmanTest {
    @Test
    public void testDiffieHellman() {
        ECKeyPair alice = ECKeyPairGenerator.newInstance().generateKeyPair();
        ECKeyPair bob = ECKeyPairGenerator.newInstance().generateKeyPair();

        ECPublicKey aliceToBob = alice.diffieHellman(bob.getPublicKey());
        ECPublicKey bobToAlice = bob.diffieHellman(alice.getPublicKey());
        assertEquals(aliceToBob.toHexString(), bobToAlice.toHexString());
    }
}
