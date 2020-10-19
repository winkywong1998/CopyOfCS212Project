import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

//Done with comment and javadoc
public class TextFileStemmer {

	// Initialize a SnowballStemmer here to avoid creating to many of it

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 * Uses the English {@link SnowballStemmer.ALGORITHM} for stemming.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @return list of cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see SnowballStemmer.ALGORITHM#ENGLISH
	 * @see #stemLine(String, Stemmer)
	 */
	public static List<String> stemLine(String line) {
		return stemLine(line, new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH));
	}

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 *
	 * @param line    the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return list of cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static List<String> stemLine(String line, Stemmer stemmer) {
		List<String> wordList = new ArrayList<String>();
		String splitList[] = TextParser.parse(line);
		for (String word : splitList) {
			wordList.add(stemmer.stem(word.toLowerCase()).toString());
		}
		return wordList;
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then writes that line to a new file.
	 *
	 * @param inputFile  the input file to parse
	 * @param outputFile the output file to write the cleaned and stemmed words
	 * @throws IOException if unable to read or write to file
	 *
	 * @see #stemLine(String)
	 * @see TextParser#parse(String)
	 */
	public static String[] stemFile(Path inputFile) throws IOException {
		ArrayList<String> allWords = new ArrayList<String>();
		try (var reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);) {
			String line;
			while ((line = reader.readLine()) != null) {
				List<String> stemedLine = stemLine(line);
				for (String word : stemedLine) {
					allWords.add(word);
				}
			}
		}
		return allWords.toArray(new String[0]);
	}

	/**
	 *
	 * Having this method to avoid multiple loop. It will just simply stem the word.
	 *
	 * @param word word to stem
	 * @return the word after stem
	 */
	public static String stemWord(String word, SnowballStemmer stemmer) throws IOException {
		return stemmer.stem(word).toString();
	}

}
