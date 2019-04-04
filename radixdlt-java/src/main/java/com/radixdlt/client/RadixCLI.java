package com.radixdlt.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.radixdlt.client.application.identity.RadixIdentities;
import com.radixdlt.client.application.identity.RadixIdentity;
import com.radixdlt.client.application.translate.tokens.TokenBalanceState;
import com.radixdlt.client.core.Bootstrap;
import java.io.PrintWriter;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public final class RadixCLI {
	private RadixCLI() {
	}

	public static void main(String[] args) throws Exception {
		Option keyFile = new Option("k", "keyfile", true, "location of keyfile.");
		Option unencryptedFileOption = new Option("u", "unencryptedkeyfile", true, "location of keyfile.");
		Option passwordOption = new Option("p", "password", true, "password");

		Options options = new Options();
		options.addOption(keyFile);
		options.addOption(passwordOption);
		options.addOption(unencryptedFileOption);

		CommandLineParser parser = new DefaultParser();
		// parse the command line arguments
		CommandLine line = parser.parse(options, args);

		final List<String> arguments = line.getArgList();
		if (!arguments.isEmpty()) {
			if (arguments.get(0).equals("generatekey")) {
				final String password = line.getOptionValue("p");
				if (password == null) {
					System.err.println("password required");
					System.exit(-1);
				}

				PrintWriter writer = new PrintWriter(System.out);
				RadixIdentities.createNewEncryptedIdentity(writer, password);
				writer.flush();
				writer.close();
			} else {
				final RadixIdentity identity;

				if (line.getOptionValue("k") != null) {
					final String keyfile = line.getOptionValue("k");
					final String password = line.getOptionValue("p");

					if (password == null) {
						System.err.println("password required");
						System.exit(-1);
					}

					identity = RadixIdentities.loadOrCreateEncryptedFile(keyfile, password);
				} else if (line.getOptionValue("u") != null) {
					final String keyfile = line.getOptionValue("u");
					identity = RadixIdentities.loadOrCreateFile(keyfile);
				} else {
					System.err.println("key required");
					System.exit(-1);
					return;
				}

				RadixApplicationAPI api = RadixApplicationAPI.create(Bootstrap.LOCALHOST, identity);

				if (arguments.get(0).equals("get")) {
					TokenBalanceState tokenBalanceState = api.getState(TokenBalanceState.class, api.getMyAddress())
						.blockingFirst();
					Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
					System.out.println(gson.toJson(tokenBalanceState));
					System.exit(0);
				} else {
					System.out.println("My address: " + api.getMyAddress());
					System.out.println("My public key: " + api.getMyPublicKey());
				}
			}
		}
	}
}
