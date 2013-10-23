package bench;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.spoofax.sunshine.parser.model.IParseTableProvider;
import org.spoofax.sunshine.parser.model.ParserConfig;

public class EntryPoint {

	public static final String PARSE_TABLE_FILE = "FJ.tbl";
	public static final String START_SYMBOL = "Program";
	public static final int PARSE_TIMEOUT = 3000;

	public static void main(String[] args) {
		IParseTableProvider tableProvider = new ClassRelativeParseTableProvider(
				PARSE_TABLE_FILE);
		ParserConfig config = new ParserConfig(START_SYMBOL, tableProvider,
				PARSE_TIMEOUT);

		JSGLRI parser = new JSGLRI(config);
		parser.setUseRecovery(false);
		parser.setImplodeEnabled(false);

		String filename = args[0];
		String contents = readFile(filename);

		long st = System.nanoTime();
		for (int i = 0; i < 100; i++) {
			parser.parse(contents, filename);
		}
		long et = System.nanoTime();
		System.out.println("Parse time: " + (et - st) / (double) 1000000);

	}

	static String readFile(String fPath) {
		BufferedReader reader;
		try {
			reader = Files.newBufferedReader(Paths.get(fPath),
					Charset.defaultCharset());
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			return sb.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
