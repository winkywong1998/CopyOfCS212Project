import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class MultiQueryFileParser implements QueryInterface {

	private final TreeMap<String, ArrayList<Result>> results;
	private final InvertedMap map;
	private final WorkQueue wq;

	/**
	 * Initialize the data structure
	 *
	 * @param QueryLine the query line to deal with
	 * @param resultSet a set of result
	 */
	public MultiQueryFileParser(InvertedMap map, WorkQueue wq) {
		this.results = new TreeMap<String, ArrayList<Result>>();
		this.map = map;
		this.wq = wq;
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
	 * take in a queryFile and stem each line, then store them
	 *
	 * @param queryFile
	 * @return
	 * @throws IOException
	 */
	@Override
	public void searchForQuery(Path queryFile, boolean exact) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(queryFile, StandardCharsets.UTF_8)) {
			String line = "";
			while ((line = reader.readLine()) != null) {
				wq.execute(new SearchTask(exact, line));
			}
		}
		wq.finish();
	}

	/**
	 * take in a queryLine and parse it
	 *
	 * @param queryLine the queryLine to parse
	 * @return an ArrayList of Result
	 * @throws IOException
	 */
	@Override
	public ArrayList<Result> searchForQueryLine(String queryLine) throws IOException {
		wq.execute(new SearchTask(false, queryLine));
		wq.finish();
		ArrayList<Result> resultList = new ArrayList<>();
		if (!this.results.values().isEmpty()) {
			for (var element : this.results.values()) {
				resultList.addAll(element);
			}
			this.results.clear();
		}
		return resultList;
	}

	private class SearchTask implements Runnable {
		private boolean exact;
		private String query;

		/**
		 * Verified if the user want exact search or not, and do the corresponding
		 * search to the ArrayList of query line
		 *
		 * @param query query line
		 * @param exact the boolean that decided if the users want to do exact search
		 *
		 *
		 */

		public SearchTask(boolean exact, String query) {
			this.exact = exact;
			this.query = query;
		}

		@Override
		public void run() {
			SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			List<String> list = Arrays.asList(TextParser.parse(query));
			TreeSet<String> queryWords = new TreeSet<String>();
			for (String word : list) {
				queryWords.add(stemmer.stem(word).toString());
			}

			String queryLine = String.join(" ", queryWords);

			synchronized (results) {
				if (results.containsKey(queryLine)) {
					return;
				}
			}
			if (!queryWords.isEmpty()) {
				ArrayList<Result> result;
				if (exact) {
					result = map.exactSearch(queryWords);
				} else {
					result = map.partialSearch(queryWords);
				}
				synchronized (results) {
					results.put(queryLine, result);
				}
			}
		}
	}
}
