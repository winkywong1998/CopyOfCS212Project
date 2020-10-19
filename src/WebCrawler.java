import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class WebCrawler {

	private final WorkQueue worker;
	private final ThreadSafeInvertedMap map;
	HashSet<URL> pendingLink = new HashSet<URL>();

	public WebCrawler(WorkQueue worker, ThreadSafeInvertedMap map) {
		this.worker = worker;
		this.map = map;
		this.pendingLink = new HashSet<URL>();
	}

	/**
	 * take in a seed URL and crawl
	 *
	 * @param seed  the seed URL to crawl
	 * @param limit the maximum amount of link
	 */
	public void crawl(URL seed, int limit) {
		pendingLink.add(seed);
		worker.execute(new CrawlerTask(seed, limit));
		worker.finish();
	}

	/**
	 * This is a inner class which is a Task class, check functionality description
	 * in the inner class method
	 *
	 * @author winky
	 *
	 */
	private class CrawlerTask implements Runnable {

		private final URL oneURL;
		private final int limit;

		/**
		 * Take in a URL, parse it into a pure HTML text and store it in an InvertedMap.
		 * Loop through the InvertedMap, find the links(no more than maximum amount) and
		 * fire up a new task for each link
		 *
		 * @param url
		 * @param limit
		 */
		public CrawlerTask(URL url, int limit) {
			this.oneURL = url;
			this.limit = limit;

		}

		@Override
		public void run() {
			String html = null;
			try {
				html = HTMLFetcher.fetchHTML(oneURL, 3);
				if (html == null) {
					return;
				}
				if (html != null) {
					int start = 1;
					InvertedMap local = new InvertedMap();
					SnowballStemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
					for (String text : TextParser.parse(HTMLCleaner.stripHTML(html))) {
						local.add(stemmer.stem(text).toString(), oneURL.toString(), start++);
					}
					map.addAll(local);
					ArrayList<URL> links = LinkParser.listLinks(oneURL, LinkParser.fetchHTML(oneURL));
					for (URL link : links) {
						synchronized (pendingLink) {
							if (pendingLink.size() < limit) {
								if (!pendingLink.contains(link)) {
									pendingLink.add(link);
									worker.execute(new CrawlerTask(link, limit));
								}
							}
						}
					}
				}
			} catch (IOException e) {
				System.out.println("Unable to crawl from this seed URL: " + oneURL);
			}

		}

	}

}
