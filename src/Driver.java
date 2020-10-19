import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Driver {

	public static int port;

	/**
	 * Parses the command-line arguments to build and use an in-memory search engine
	 * from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 */
	public static void main(String[] args) {
		QueryInterface queryParser;
		ArgumentMap argument = new ArgumentMap(args);
		InvertedMap index = null;
		ThreadSafeInvertedMap safeIndex = null;
		WorkQueue wq = null;
		WebCrawler crawler = null;

		int threadNum = 0;
		boolean haveThreads = argument.hasFlag("-threads");
		boolean haveURL = argument.hasFlag("-url");
		boolean havePort = argument.hasFlag("-port");

		if (haveURL || havePort) {
			haveThreads = true;
		}

		if (havePort) {
			port = Integer.parseInt(argument.getString("-port", "8080"));
		}

		if (!haveThreads) {
			index = new InvertedMap();
			queryParser = new QueryFileParser(index);
		} else {
			threadNum = Integer.parseInt(argument.getString("-threads", "5"));
			wq = new WorkQueue(threadNum);
			safeIndex = new ThreadSafeInvertedMap();
			index = safeIndex;
			queryParser = new MultiQueryFileParser(safeIndex, wq);
			crawler = new WebCrawler(wq, safeIndex);
		}

		// Parallel with path
		if (haveURL) {
			String seedURL = argument.getString("-url");
			int limit = 0;
			URL seed;
			try {
				seed = new URL(seedURL);
				limit = Integer.parseInt(argument.getString("-limit", "50"));
			} catch (IOException e) {
				System.out.println("Unable to process this URL: " + seedURL);
				return;
			} catch (NumberFormatException e) {
				System.out.println("Unable to process this limit: " + limit);
				return;
			}
			crawler.crawl(seed, limit);
		} else {
			if (argument.hasFlag("-path") && argument.hasValue("-path")) {
				Path inputPath = argument.getPath("-path");
				try {
					if (!haveThreads) {
						InvertedIndexBuilder.buildFromPath(inputPath, index);
					} else {
						InvertedIndexThread.buildHelper(inputPath, safeIndex, wq);
					}
				} catch (IOException e) {
					System.out.println("Unable to build the index from path: " + inputPath);
					return;
				}
			}
		}
		if (havePort) {
			// Start the server

			Server server = new Server(port);

			ServletHandler handler = new ServletHandler();
			handler.addServletWithMapping(new ServletHolder(new SearchServlet(queryParser)), "/");
			handler.addServletWithMapping(new ServletHolder(new CookieConfigServlet()), "/config");
			server.setHandler(handler);
			try {
				server.start();
				server.join();
			} catch (Exception e1) {
				System.out.println("Unable to build the server.");
			}
		} else {
			System.out.println("-port flag not found");

			// If the flag "index" is found, trigger MapWriter
			if (argument.hasFlag("-index")) {
				Path outputPath = argument.getPath("-index", Paths.get("index.json"));
				try {
					index.write(outputPath);
				} catch (IOException e) {
					System.out.println("Unable to write the index to output path: " + outputPath);
					return;
				}
			}

			if (argument.hasFlag("-locations")) {
				Path outputPath = argument.getPath("-locations", Paths.get("location.json"));
				try {
					index.writeLocation(outputPath);
				} catch (IOException e) {
					System.out.println("Unable to write the locations to output path: " + outputPath);
					return;
				}
			}

			if (argument.hasFlag("-search")) {
				Path location = argument.getPath("-search");
				boolean exact = argument.hasFlag("-exact");
				String check = location.toString().toLowerCase();
				if (Files.exists(location)) {
					if (check.endsWith(".txt") || check.endsWith(".text")) {
						try {
							queryParser.searchForQuery(location, exact);
						} catch (IOException e) {
							System.out.println("Unable to search from this query file: " + location);
						}
					}
				}
			}

			if (argument.hasFlag("-results")) {
				Path resultLocation = argument.getPath("-results", Paths.get("results.json"));
				System.out.println(queryParser.getClass());
				try {
					queryParser.toJSON(resultLocation);
				} catch (IOException e) {
					System.out.println("Unable to give the result to this location: " + resultLocation);
				}
			}

			if (wq != null) {
				wq.shutdown();
			}
		}
	}

}
