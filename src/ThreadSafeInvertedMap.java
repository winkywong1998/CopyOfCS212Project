import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

public class ThreadSafeInvertedMap extends InvertedMap {

	private final ReadWriteLock lock;

	/**
	 * This is a thread safe version of InvertedMap class
	 */
	public ThreadSafeInvertedMap() {
		super();
		lock = new ReadWriteLock();
	}

	@Override
	public int totalWord(String location) {
		lock.lockReadOnly();
		try {
			return super.totalWord(location);
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public void add(String word, String location, int position) {
		lock.lockReadWrite();
		try {
			super.add(word, location, position);
		} finally {
			lock.unlockReadWrite();
		}
	}

	@Override
	public void addAll(InvertedMap temp) {
		lock.lockReadWrite();
		try {
			super.addAll(temp);
		} finally {
			lock.unlockReadWrite();
		}
	}

	@Override
	public String toString() {
		lock.lockReadOnly();
		try {
			return super.toString();
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public void write(Path outputPath) throws IOException {
		lock.lockReadOnly();
		try {
			super.write(outputPath);
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public void writeLocation(Path outputPath) throws IOException {
		lock.lockReadOnly();
		try {
			super.writeLocation(outputPath);
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public boolean contains(String word) {
		lock.lockReadOnly();
		try {
			return super.contains(word);
		} finally {
			lock.unlockReadOnly();
		}

	}

	@Override
	public boolean contains(String word, String location) {
		lock.lockReadOnly();
		try {
			return super.contains(word, location);
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public boolean contains(String word, String location, int position) {
		lock.lockReadOnly();
		try {
			return super.contains(word, location, position);

		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public int words() {
		lock.lockReadOnly();
		try {
			return super.words();
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public int locations(String word) {
		lock.lockReadOnly();
		try {
			return super.locations(word);
		} finally {
			lock.unlockReadOnly();
		}

	}

	@Override
	public int positions(String word, String location) {
		lock.lockReadOnly();
		try {
			return super.positions(word, location);
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public ArrayList<Result> partialSearch(Collection<String> line) {
		lock.lockReadOnly();
		try {
			return super.partialSearch(line);
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public ArrayList<Result> exactSearch(Collection<String> line) {
		lock.lockReadOnly();
		try {
			return super.exactSearch(line);
		} finally {
			lock.unlockReadOnly();
		}
	}

}