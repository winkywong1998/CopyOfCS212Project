import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public class InvertedIndexThread {

	/**
	 * This is a helper method that helps to build InvertedMap
	 *
	 * @param dirPath the path passed in to be verified
	 * @param map     the data structure InvertedMap
	 * @param wq      the workqueue pass in for multithreading
	 * @throws IOException
	 */
	public static void buildHelper(Path dirPath, ThreadSafeInvertedMap map, WorkQueue wq) throws IOException {
		buildFromPath(dirPath, map, wq);
		wq.finish();
	}

	/**
	 * If path is a text file, builds the index from that file. If path is a
	 * directory, builds an inverted index from any text file found within the
	 * directory and its subdirectories.
	 *
	 * @param dirPath the path passed in to be verified
	 * @param map     the data structure InvertedMap
	 * @param wq      the workqueue pass in for multithreading
	 */
	public static void buildFromPath(Path dirPath, ThreadSafeInvertedMap map, WorkQueue wq) throws IOException {
		if (Files.exists(dirPath)) {
			if (Files.isDirectory(dirPath)) {
				try (DirectoryStream<Path> listing = Files.newDirectoryStream(dirPath)) {
					Iterator<Path> iterate = listing.iterator();
					Path file;
					while (iterate.hasNext()) {
						file = iterate.next();
						buildFromPath(file, map, wq);
					}
				}
			} else {
				String check = dirPath.toString().toLowerCase();
				if (check.endsWith(".txt") || check.endsWith(".text")) {
					wq.execute(new BuildTask(dirPath, map));
				}
			}
		}
	}

	/**
	 * This is a inner class which is a Task class, check functionality description
	 * in the inner class method
	 *
	 * @author winky
	 *
	 */

	private static class BuildTask implements Runnable {

		private Path file;
		private ThreadSafeInvertedMap map;

		/**
		 * Take in a file, parse and stem it word by word and put each word into
		 * InvertedIndex
		 *
		 *
		 * @param file file to read and process
		 * @param map  the data structure InvertedMap
		 */
		public BuildTask(Path file, ThreadSafeInvertedMap map) {
			this.file = file;
			this.map = map;
		}

		@Override
		public void run() {
			try {
				InvertedMap local = InvertedIndexBuilder.buildFromFile(file);
				map.addAll(local);
			} catch (IOException e) {
				System.out.println("Unable to index for this file: " + file + "using multithreading");
			}
		}
	}
}
