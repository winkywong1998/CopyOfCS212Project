import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;

public class TreeJSONWriter {

	/**
	 * Writes several tab <code>\t</code> symbols using the provided {@link Writer}.
	 *
	 * @param times  the number of times to write the tab symbol
	 * @param writer the writer to use
	 * @throws IOException if the writer encounters any issues
	 */
	public static void indent(int times, Writer writer) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Writes the element surrounded by quotes using the provided {@link Writer}.
	 *
	 * @param element the element to quote
	 * @param writer  the writer to use
	 * @throws IOException if the writer encounters any issues
	 */
	public static void quote(String element, Writer writer) throws IOException {
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Returns the set of elements formatted as a pretty JSON array of numbers.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static String asArray(TreeSet<Integer> elements) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of numbers to the
	 * specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 */
	public static void asArray(TreeSet<Integer> elements, Path path) throws IOException {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of numbers using
	 * the provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 */
	public static void asArray(TreeSet<Integer> elements, Writer writer, int level) throws IOException {
		writer.write('[');
		writer.write(System.lineSeparator());
		if (!elements.isEmpty()) {
			for (Integer element : elements.headSet(elements.last())) {
				indent(level + 1, writer);
				writer.write(element.toString());
				writer.write(',');
				writer.write(System.lineSeparator());
			}
		}
		indent(level + 1, writer);
		writer.write(elements.last().toString());
		writer.write(System.lineSeparator());
		indent(level, writer);
		writer.write(']');
	}

	/**
	 * Returns the map of elements formatted as a pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static String asObject(TreeMap<String, Integer> elements) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the map of elements formatted as a pretty JSON object to the specified
	 * file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static void asObject(TreeMap<String, Integer> elements, Path path) throws IOException {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the map of elements as a pretty JSON object using the provided
	 * {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 */
	public static void asObject(TreeMap<String, Integer> elements, Writer writer, int level) throws IOException {
		writer.write('{');
		writer.write(System.lineSeparator());
		if (!elements.isEmpty()) {
			for (String key : elements.headMap(elements.lastKey(), false).keySet()) {
				indent(level + 1, writer);
				String keySave = key;
				quote(key, writer);
				writer.write(": ");
				writer.write(elements.get(keySave).toString());
				writer.write(',');
				writer.write(System.lineSeparator());
			}
		}
		indent(level + 1, writer);
		String keySave = elements.lastKey();
		quote(elements.lastKey(), writer);
		writer.write(": ");
		writer.write(elements.get(keySave).toString());
		writer.write(System.lineSeparator());
		writer.write('}');
	}

	/**
	 * Returns the nested map of elements formatted as a nested pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static String asNestedObject(TreeMap<String, TreeSet<Integer>> elements) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the nested map of elements formatted as a nested pretty JSON object to
	 * the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements, Path path) throws IOException {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the nested map of elements as a nested pretty JSON object using the
	 * provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	private static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements, Writer writer, int level)
			throws IOException {
		writer.write('{');
		writer.write(System.lineSeparator());
		if (!elements.isEmpty()) {
			for (String key : elements.headMap(elements.lastKey(), false).keySet()) {
				indent(level + 1, writer);
				String keySave = key;
				quote(key, writer);
				writer.write(": ");
				asArray(elements.get(keySave), writer, level + 1);
				writer.write(',');
				writer.write(System.lineSeparator());
			}
		}
		indent(level + 1, writer);
		String keySave = elements.lastKey();
		quote(elements.lastKey(), writer);
		writer.write(": ");
		asArray(elements.get(keySave), writer, level + 1);
		writer.write(System.lineSeparator());
		indent(level, writer);
		writer.write('}');
	}

	/**
	 * Returns the high nested map of elements formatted as a nested pretty JSON
	 * object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return containing the elements in pretty JSON format
	 */
	public static String asHighNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asHighNestedObject(elements, writer, 0);
			return writer.toString();
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the map of elements formatted as a pretty JSON object to the specified
	 * file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 */
	public static void asHighNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asHighNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the high nested map of elements as a nested pretty JSON object using
	 * the provided and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 */
	public static void asHighNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Writer writer,
			int level) throws IOException {
		writer.write('{');
		writer.write(System.lineSeparator());

		if (!elements.isEmpty()) {
			for (String key : elements.headMap(elements.lastKey(), false).keySet()) {
				indent(level + 1, writer);
				quote(key, writer);
				writer.write(": ");
				asNestedObject(elements.get(key), writer, level + 1);
				writer.write(',');
				writer.write(System.lineSeparator());
			}
			indent(level + 1, writer);
			quote(elements.lastKey(), writer);
			writer.write(": ");
			asNestedObject(elements.get(elements.lastKey()), writer, level + 1);
			writer.write(System.lineSeparator());
		}
		indent(level, writer);
		writer.write('}');
	}

	/**
	 * Writes the map of elements formatted as a pretty JSON object to the specified
	 * file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 */
	public static void asResultNestedObject(TreeMap<String, ArrayList<Result>> result, Path path) throws IOException {

		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asResultNestedObject(result, writer, 0);
		}
	}

	/**
	 * Writes the nested map of the search Result as a nested pretty JSON object
	 * using the provided and indentation level.
	 *
	 * @param finalResults Data structure that stores the final restult
	 * @param writer       The writer to use
	 * @param level        The initial indentation level
	 * @throws IOException If the writer encounters any issues
	 */
	public static void asResultNestedObject(TreeMap<String, ArrayList<Result>> finalResults, Writer writer, int level)
			throws IOException {
		writer.write('[');
		writer.write(System.lineSeparator());
		if (!finalResults.isEmpty()) {
			for (String queryLine : finalResults.keySet()) {
				indent(level + 1, writer);
				writer.write('{');
				writer.write(System.lineSeparator());
				indent(level + 2, writer);
				quote("queries", writer);
				writer.write(": ");
				quote(queryLine, writer);
				writer.write(",");
				writer.write(System.lineSeparator());
				indent(level + 2, writer);
				quote("results", writer);
				writer.write(": ");
				// list
				asResultList(finalResults, writer, level, queryLine);
				// list
				writer.write(System.lineSeparator());
				indent(level + 1, writer);
				writer.write('}');
				if (!queryLine.equals(finalResults.lastKey())) {
					writer.write(',');
				}
				writer.write(System.lineSeparator());
			}
			if (level == 0) {
				writer.write(']');
			} else {
				indent(level, writer);
				writer.write(']');
			}
		}
	}

	/**
	 * Writes the list in the result as a nested pretty JSON object using the
	 * provided and indentation level.
	 *
	 * @param finalResults Data structure that stores the final restult
	 * @param writer       The writer to use
	 * @param level        The initial indentation level
	 * @param queryLine    Each line of the query
	 * @throws IOException If the writer encounters any issues
	 */
	public static void asResultList(TreeMap<String, ArrayList<Result>> finalResults, Writer writer, int level,
			String queryLine) throws IOException {
		writer.write('[');
		if (!finalResults.get(queryLine).isEmpty()) {
			indent(level, writer);
			writer.write(System.lineSeparator());
			indent(level + 1, writer);
			Iterator<Result> it = finalResults.get(queryLine).iterator();
			while (it.hasNext()) {
				indent(level + 2, writer);
				writer.write('{');
				writer.write(System.lineSeparator());
				indent(level + 4, writer);
				Result r = it.next();
				// Query
				asResultQuery(writer, level, r);
				// Query
				writer.write(System.lineSeparator());
				indent(level + 3, writer);
				writer.write('}');
				if (it.hasNext()) {
					writer.write(',');
				}
				writer.write(System.lineSeparator());
				indent(level + 1, writer);
			}
		} else {
			writer.write(System.lineSeparator());
			indent(level + 1, writer);
		}
		indent(level + 1, writer);
		writer.write(']');
	}

	/**
	 * Writes the object in the list as a nested pretty JSON object using the
	 * provided and indentation level.
	 *
	 * @param writer The writer to use
	 * @param level  The initial indentation level
	 * @param r      The data structure that stores result
	 * @throws IOException If the writer encounters any issues
	 */
	public static void asResultQuery(Writer writer, int level, Result r) throws IOException {
		quote("where", writer);
		writer.write(": ");
		quote(r.where(), writer);
		writer.write(",");
		writer.write(System.lineSeparator());
		indent(level + 4, writer);
		quote("count", writer);
		writer.write(": ");
		writer.write(String.valueOf(r.totalMatched()));
		writer.write(",");
		writer.write(System.lineSeparator());
		indent(level + 4, writer);
		quote("score", writer);
		writer.write(": ");
		DecimalFormat FORMATTER = new DecimalFormat("0.000000");
		writer.write(FORMATTER.format(r.score()));
	}
}
