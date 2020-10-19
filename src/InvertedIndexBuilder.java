
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class InvertedIndexBuilder {

	/**
	 * If path is a text file, builds the index from that file. If path is a
	 * directory, builds an inverted index from any text file found within the
	 * directory and its subdirectories.
	 *
	 * @param dirPath the path passed in to be verified
	 * @param map     the data structure InvertedMap
	 */
	public static void buildFromPath(Path dirPath, InvertedMap map) throws IOException {
		if (Files.exists(dirPath)) {
			if (Files.isDirectory(dirPath)) {
				try (DirectoryStream<Path> listing = Files.newDirectoryStream(dirPath)) {
					Iterator<Path> iterate = listing.iterator();
					Path file;
					while (iterate.hasNext()) {
						file = iterate.next();
						buildFromPath(file, map);
					}
				}
			} else {
				String check = dirPath.toString().toLowerCase();
				if (check.endsWith(".txt") || check.endsWith(".text")) {
					buildFromFile(dirPath, map);
				}
			}
		}
	}

	/**
	 * Take in a file, parse and stem it word by word and put each word into
	 * InvertedIndex
	 *
	 *
	 * @param file file to read and process
	 * @param map  the data structure InvertedMap
	 */
	public static void buildFromFile(Path file, InvertedMap map) throws IOException {
		String name = file.toString();
		SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		int count = 0;
		try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);) {
			String line;
			while ((line = reader.readLine()) != null) {
				String splitList[] = TextParser.parse(line.toLowerCase());
				for (String stem : splitList) {
					String words = TextFileStemmer.stemWord(stem, stemmer);
					map.add(words, name, ++count);
				}
			}
		}
	}

	/**
	 * Take in a file, use buildFromFile to convert it into a IntertedMap and return
	 * it
	 *
	 * @param file the file you want to build InvertedMap
	 * @return the InvertedMap of the file
	 * @throws IOException
	 */
	public static InvertedMap buildFromFile(Path file) throws IOException {
		InvertedMap temp = new InvertedMap();
		buildFromFile(file, temp);
		return temp;
	}
}
