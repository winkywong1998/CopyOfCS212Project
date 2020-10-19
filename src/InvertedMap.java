import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

//Done with comment and javadoc
public class InvertedMap {

	/**
	 * Declare the data structure is a TreeMap contains word, location, position
	 */
	// word, location, position
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> map;
	private final TreeMap<String, Integer> wordTotal;

	/**
	 * Initialize
	 */
	public InvertedMap() {
		this.map = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
		this.wordTotal = new TreeMap<>();
	}

	/**
	 * Count the word in this specific location
	 *
	 * @param location  the location to count
	 * @param wordTotal the result of counting
	 */
	private void incrementWord(String location) {
		if (wordTotal.containsKey(location)) {
			wordTotal.put(location, wordTotal.get(location) + 1);
		} else {
			wordTotal.put(location, 1);
		}
	}

	/**
	 * Give the result of word count in specific location
	 *
	 * @param location the location to get
	 * @return the total words in this location
	 */
	public int totalWord(String location) {
		return wordTotal.getOrDefault(location, 0);
	}

	/**
	 * Add the word
	 *
	 * @param word     the word you want to add
	 * @param location the loading location
	 * @param position the position word was found
	 */
	public void add(String word, String location, int position) {
		map.putIfAbsent(word, new TreeMap<String, TreeSet<Integer>>());
		TreeMap<String, TreeSet<Integer>> mapWord = map.get(word);
		mapWord.putIfAbsent(location, new TreeSet<Integer>());
		TreeSet<Integer> mapPosition = mapWord.get(location);
		mapPosition.add(position);
		incrementWord(location);
	}

	/**
	 * Add a temporary inverted index to this index
	 *
	 * @param index inverted index
	 */
	public void addAll(InvertedMap index) {
		for (String word : index.map.keySet()) {
			if (this.map.containsKey(word)) {
				for (String path : index.map.get(word).keySet()) {
					if (this.map.get(word).containsKey(path)) {
						this.map.get(word).get(path).addAll(index.map.get(word).get(path));
					} else {
						this.map.get(word).put(path, index.map.get(word).get(path));
					}
				}
			} else {
				this.map.put(word, index.map.get(word));
			}
		}

		for (String path : index.wordTotal.keySet()) {
			if (!this.wordTotal.containsKey(path)) {
				this.wordTotal.put(path, index.wordTotal.get(path));
			} else {
				this.wordTotal.put(path, this.wordTotal.get(path) + index.wordTotal.get(path));
			}
		}
	}

	/**
	 * Override toString method
	 */
	@Override
	public String toString() {
		return map.toString();
	}

	/**
	 * This method is going to use TreeJsonWriter to create an output
	 *
	 * @param map the invertedMap you want to write
	 */
	public void write(Path outputPath) throws IOException {
		try (var writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
			writer.write(TreeJSONWriter.asHighNestedObject(map));
		}
	}

	/*
	 * DONE Make this more general by adding more methods:
	 */
	/**
	 * write for the "-location" function
	 *
	 * @param outputPath the output path of the result
	 * @throws IOException
	 */
	public void writeLocation(Path outputPath) throws IOException {
		try (var writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {
			writer.write(TreeJSONWriter.asObject(wordTotal));
		}
	}

	/**
	 *
	 * Check if the word is in the invertedMap
	 *
	 * @param word the word to check
	 * @return true if it's in the map, else false
	 */
	public boolean contains(String word) {
		return map.containsKey(word);
	}

	/**
	 *
	 * Check if the word is in this path of invertedMap
	 *
	 * @param word the word to check
	 * @param path the path to check
	 * @return
	 */
	public boolean contains(String word, String location) {
		if (contains(word)) {
			if (map.get(word).containsKey(location)) {
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * Check if position of the word is in this path of invertedMap
	 *
	 * @param word     the word to check
	 * @param path     the path to check
	 * @param location the location to check
	 * @return
	 */
	public boolean contains(String word, String location, int position) {
		if (contains(word, location)) {
			if (map.get(word).get(location).contains(position)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * count the total word in this invertedMap
	 *
	 * @return count of the total word in this invertedMap
	 */
	public int words() {
		return map.size();
	}

	/**
	 * count the location of this word in this invertedMap
	 *
	 * @param word the word to check
	 * @return count of the location of this word in this invertedMap
	 */
	public int locations(String word) {
		if (map.containsKey(word)) {
			return map.get(word).size();
		} else {
			return 0;
		}
	}

	/**
	 * count the position of the location of this word in this invertedMap
	 *
	 * @param word     the word to check
	 * @param location the location to check
	 * @return count of position of the location of this word in this invertedMap
	 */
	public int positions(String word, String location) {
		return (contains(word, location) ? map.get(word).get(location).size() : 0);
	}

	/**
	 * Do exact search to a line of query
	 *
	 * @param line to search
	 *
	 * @return An ArrayList of Result data structure
	 */
	public ArrayList<Result> exactSearch(Collection<String> queryLine) {
		ArrayList<Result> results = new ArrayList<>();
		HashMap<String, Result> hashMap = new HashMap<>();
		for (String word : queryLine) {
			if (map.containsKey(word)) {
				searchWork(hashMap, word, results);
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 * Do partial search to a line of query
	 *
	 * @param line to search
	 * @return An ArrayList of Result data structure
	 */
	public ArrayList<Result> partialSearch(Collection<String> queryLine) {
		ArrayList<Result> results = new ArrayList<>();
		HashMap<String, Result> hashMap = new HashMap<>();
		for (String word : queryLine) {
			for (String key : map.tailMap(word).keySet()) {
				if (key.startsWith(word)) {
					searchWork(hashMap, key, results);
				} else {
					break;
				}
			}
		}
		Collections.sort(results);
		return results;
	}

	/**
	 *
	 * @param result hashMap that stores the Result and it's corresponding key
	 * @param key    the single search query
	 * @param list   the arrayList that stores Result
	 *
	 *               The helper method that do the search
	 */
	private void searchWork(HashMap<String, Result> result, String key, ArrayList<Result> list) {
		TreeMap<String, TreeSet<Integer>> wordPosition = map.get(key);
		for (String location : wordPosition.keySet()) {
			if (result.containsKey(location)) {
				result.get(location).setMatch(wordPosition.get(location).size());
			} else {
				int totalWords = totalWord(location);
				int totalMatched = wordPosition.get(location).size();
				Result r = new Result(location, totalMatched, totalWords);
				list.add(r);
				result.put(location, r);
			}
		}
	}
}
