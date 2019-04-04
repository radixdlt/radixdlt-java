package com.radixdlt.client.core.pow;

import com.radixdlt.client.atommodel.tokens.TokenDefinitionParticle;
import com.radixdlt.client.core.atoms.Atom;
import org.junit.Test;
import org.radix.serialization2.client.Serialize;
import org.radix.utils.primitives.Bytes;

import static junit.framework.TestCase.assertEquals;

public class ProofOfWorkBuilderTest {

	@Test
	public void test() throws ProofOfWorkException {
		int magic = 12345;
		byte[] seed = Bytes.fromHexString("deadbeef00000000deadbeef00000000deadbeef00000000deadbeef00000000");
		ProofOfWork pow = new ProofOfWorkBuilder().build(magic, seed, 16);
		assertEquals(241709, pow.getNonce());
		pow.validate();
	}

	@Test
	public void test1() throws ProofOfWorkException {
		int magic = 1;
		byte[] seed = new byte[32];
		ProofOfWork pow = new ProofOfWorkBuilder().build(magic, seed, 16);
		assertEquals(329920, pow.getNonce());
		pow.validate();
	}


	@Test
	public void testPow() {
		String json = "{\"metaData\":{\"timestamp\":\":str:1234567889\"},\"serializer\":2019665,\"particleGroups\":[{\"serializer\":-67058791,\"particles\":[{\"particle\":{\"address\":\":adr:JEQriWm75u9LNGafY3j38ABsmfQmemaA37CWhVzsRoTRGh82GjV\",\"symbol\":\":str:CCC\",\"granularity\":\":u20:1\",\"serializer\":-1135093134,\"description\":\":str:Cyon Crypto Coin is the worst shit coin\",\"permissions\":{\"burn\":\":str:all\",\"mint\":\":str:all\"},\"name\":\":str:Cyon\"},\"serializer\":-993052100,\"spin\":1}]},{\"serializer\":-67058791,\"particles\":[{\"particle\":{\"planck\":3600,\"amount\":\":u20:1000\",\"tokenDefinitionReference\":\":rri:\\/JEQriWm75u9LNGafY3j38ABsmfQmemaA37CWhVzsRoTRGh82GjV\\/tokens\\/CCC\",\"granularity\":\":u20:1\",\"address\":\":adr:JEQriWm75u9LNGafY3j38ABsmfQmemaA37CWhVzsRoTRGh82GjV\",\"serializer\":1745075425,\"nonce\":1},\"serializer\":-993052100,\"spin\":1}]}]}";

		Atom atom = Serialize.getInstance().fromJson(json, Atom.class);
		byte[] hash = atom.getHash().toByteArray();
		String hashHex = Bytes.toHexString(hash);
		assertEquals("c51e133397f46cf206ce7bd07efba6dca570fac259a8674210a55ff97d69c5db", hashHex);
		int magic = 63799298;
		ProofOfWork pow = new ProofOfWorkBuilder().build(magic, hash, 16);
		assertEquals(31387L, pow.getNonce());
		assertEquals("0000d3e3b77fe6bd03f090044467a8a1d53508965d86c773582998dfa510194d", Bytes.toHexString(pow.work));
	}

	@Test
	public void testNonceNegativeVsPositiveToHelpSwiftLibrary() {

	}
}