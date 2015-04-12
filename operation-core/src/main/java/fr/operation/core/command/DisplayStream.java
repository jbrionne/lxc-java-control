package fr.operation.core.command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DisplayStream implements Callable<String> {

	private static final Logger LOG = LoggerFactory
			.getLogger(DisplayStream.class);

	private final InputStream inputStream;

	public DisplayStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public String call() throws Exception {
		String ligne = "";
		StringBuilder sB = new StringBuilder();
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
				(inputStream)));) {
			int i = 0;
			while ((ligne = br.readLine()) != null) {
				LOG.info("- " + ligne);
				sB.append(ligne).append(System.lineSeparator());
				i++;
			}
		} catch (IOException e) {
			LOG.error(ligne, e);
			Thread.currentThread().interrupt();
		}
		return sB.toString();
	}
}
