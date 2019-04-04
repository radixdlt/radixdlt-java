package com.radixdlt.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.radixdlt.client.application.identity.RadixIdentities;
import com.radixdlt.client.application.identity.RadixIdentity;
import com.radixdlt.client.application.translate.tokens.TokenBalanceState;
import com.radixdlt.client.application.translate.tokens.TokenDefinitionReference;
import com.radixdlt.client.atommodel.accounts.RadixAddress;
import com.radixdlt.client.core.Bootstrap;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.radix.utils.RadixConstants;

public final class RadixCLI {
	private RadixCLI() {
	}

	static class RadixAddressTypeAdapter extends TypeAdapter<RadixAddress> {
		@Override
		public void write(JsonWriter out, RadixAddress address) throws IOException {
			out.value(address.toString());
		}

		@Override
		public RadixAddress read(JsonReader in) throws IOException {
			// implement the deserialization
			return RadixAddress.from(in.nextString());
		}
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
				Gson gson = new GsonBuilder()
					.registerTypeAdapter(RadixAddress.class, new RadixAddressTypeAdapter())
					.excludeFieldsWithoutExposeAnnotation()
					.create();

				if (arguments.get(0).equals("get")) {
					if (arguments.get(1).equals("balance")) {
						TokenBalanceState tokenBalanceState = api.getState(TokenBalanceState.class, api.getMyAddress()).blockingFirst();
						System.out.println(gson.toJson(tokenBalanceState));
						System.exit(0);
					} else if (arguments.get(1).equals("messages")) {
						api.pull();
						api.getMessages()
							.map(gson::toJson)
							.subscribe(System.out::println);
						TimeUnit.SECONDS.sleep(3);
					}
				} else if (arguments.get(0).equals("send")) {
					if (arguments.get(1).equals("tokens")) {
						if (arguments.size() != 6 || !arguments.get(4).equals("to")) {
							System.err.println("send tokens <amount> <token> to <address>");
							System.exit(-1);
						} else {
							BigDecimal amount = new BigDecimal(arguments.get(2));
							String[] ref = arguments.get(3).split("/");
							RadixAddress tokenAddress = RadixAddress.from(ref[0]);
							String iso = ref[2];
							RadixAddress address = RadixAddress.from(arguments.get(5));
							api.transferTokens(address, amount, TokenDefinitionReference.of(tokenAddress, iso)).toCompletable().blockingAwait();
							System.exit(0);
						}
					} else if (arguments.get(1).equals("message")) {
						if (arguments.size() != 5 || !arguments.get(3).equals("to")) {
							System.err.println("send message <message> to <address>");
							System.exit(-1);
						}
						final String message = arguments.get(2);
						final RadixAddress to = RadixAddress.from(arguments.get(4));

						api.sendMessage(message.getBytes(RadixConstants.STANDARD_CHARSET), false, to)
							.toCompletable()
							.blockingAwait();
						System.exit(0);
					}
				} else {
					System.out.println("My address: " + api.getMyAddress());
					System.out.println("My public key: " + api.getMyPublicKey());
				}
			}
		}
	}
}
