public class Result implements Comparable<Result> { // result

	private final String where; // where
	private int totalMatched; // count
	private final int totalWords; // totalWords

	/**
	 * Initialize the Result
	 *
	 * @param where        where is the query
	 * @param totalMatched how many words matched
	 * @param totalWords   the total number of word
	 */
	public Result(String where, int totalMatched, int totalWords) {
		this.where = where;
		this.totalMatched = totalMatched;
		this.totalWords = totalWords;
	}

	@Override
	public int hashCode() {
		return this.where.hashCode();
	}

	/**
	 * Get the where
	 *
	 * @return where
	 */
	public String where() {
		return this.where;
	}

	/**
	 * Get the totalMatched
	 *
	 * @return totalMatched
	 */
	public int totalMatched() {
		return this.totalMatched;
	}

	/**
	 * Get the totalWords
	 *
	 * @return totalWords
	 */
	public int totalWords() {
		return this.totalWords;
	}

	/**
	 * Calculate the score
	 *
	 * @return score
	 */
	public double score() {
		double score = (double) this.totalMatched / this.totalWords;
		return score;
	}

	/**
	 * Override the compareto method
	 */
	@Override
	public int compareTo(Result other) {
		int result = -Double.compare(this.score(), other.score());
		if (result == 0) {
			result = -(Integer.compare(this.totalMatched(), other.totalMatched));
			if (result == 0) {
				result = this.where().compareTo(other.where());
			}
		}
		return result;
	}

	/**
	 * Override toString method
	 */
	@Override
	public String toString() {
		return "location : " + this.where + " socre: " + this.score() + " totalWords: " + this.totalWords + " occurs: "
				+ this.totalMatched;
	}

	/**
	 * Incrementing totalMatched
	 *
	 */
	public void setMatch(int size) {
		this.totalMatched += size;
	}

}
