import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public interface QueryInterface {

	/**
	 * Make the Results to beautiful JSON
	 *
	 * @param outputPath the path of output
	 * @throws IOException if there is a problem
	 */
	public void toJSON(Path outputPath) throws IOException;

	/**
	 * Take in a queryFile and search it using the way exact parameter provided
	 *
	 * @param queryFile the queryFile to process
	 * @param exact     exact search or not
	 * @throws IOException
	 */
	public void searchForQuery(Path queryFile, boolean exact) throws IOException;

	/**
	 *
	 * take in a String of query and stem it and return an ArrayList of Result
	 *
	 * @param query the queryLine to parse
	 * @return An ArrayList of Result
	 * @throws IOException
	 */
	public ArrayList<Result> searchForQueryLine(String query) throws IOException;

}
