import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class QueryFileParser implements QueryInterface {

	private final TreeMap<String, ArrayList<Result>> results;
	private final InvertedMap map;

	/**
	 * Initialize the data structure
	 *
	 * @param QueryLine the query line to deal with
	 * @param resultSet a set of result
	 */
	public QueryFileParser(InvertedMap map) {
		this.results = new TreeMap<String, ArrayList<Result>>();
		this.map = map;
	}

	/**
	 * Make the Results to beautiful JSON
	 *
	 * @param outputPath the path of output
	 * @throws IOException if there is a problem
	 */
	@Override
	public void toJSON(Path outputPath) throws IOException {
		TreeJSONWriter.asResultNestedObject(results, outputPath);
	}

	/**
	 * Take in a queryFile and search it using the way exact parameter provided
	 *
	 * @param queryFile the queryFile to process
	 * @param exact     exact search or not
	 * @throws IOException
	 */
	@Override
	public void searchForQuery(Path queryFile, boolean exact) throws IOException {
		var stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		try (BufferedReader reader = Files.newBufferedReader(queryFile, StandardCharsets.UTF_8)) {
			String line = "";
			while ((line = reader.readLine()) != null) {
				TreeSet<String> queryWords = new TreeSet<String>();
				for (String word : TextParser.parse(line)) {
					queryWords.add(stemmer.stem(word).toString());
				}
				if (!queryWords.isEmpty()) {
					search(queryWords, exact);
				}
			}
		}
	}

	/**
	 * take in a queryLine and parse it
	 *
	 * @param queryLine the queryLine to parse
	 * @return an ArrayList of Result
	 * @throws IOException
	 */
	@Override
	public ArrayList<Result> searchForQueryLine(String query) throws IOException {
		var stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		TreeSet<String> queryWords = new TreeSet<String>();
		for (String word : TextParser.parse(query)) {
			queryWords.add(stemmer.stem(word).toString());
		}
		if (!queryWords.isEmpty()) {
			search(queryWords, false);
		}
		ArrayList<Result> resultList = new ArrayList<>();
		if (!this.results.values().isEmpty()) {
			for (var element : this.results.values()) {
				resultList.addAll(element);
			}
		}
		return resultList;
	}

	/**
	 * Verified if the user want exact search or not, and do the corresponding
	 * search to the ArrayList of query line
	 *
	 * @param exact the boolean that decided if the users want to do exact search
	 * @param line  ArrayList of query line
	 * @return A Result data structure
	 */
	private void search(TreeSet<String> line, boolean exact) {
		String joined = String.join(" ", line);
		if (results.containsKey(joined)) {
			return;
		}
		if (exact) {
			this.results.put(joined, map.exactSearch(line));
		} else {
			this.results.put(joined, map.partialSearch(line));
		}
	}

}
