import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

@SuppressWarnings("serial")
public class SearchServlet extends HttpServlet {
	private static final String TITLE = "Search Engine";
	private static Logger log = Log.getRootLogger();
	private QueryInterface queryParser;
	public static final String LAST_VISIT = "Visited";
	public static final String VISIT_COUNT = "Count";
	public static final String QUERY_HISTORY = "Queries";
	public static final String SEPARATOR = "@@@";

	public SearchServlet(QueryInterface queryParser) {
		super();
		this.queryParser = queryParser;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HashMap<String, Cookie> cookies = new HashMap<>();
		Cookie[] cookiesArray = request.getCookies();

		if (cookiesArray != null) {
			for (Cookie c : cookiesArray) {
				cookies.put(c.getName(), c);
			}
		}

		Cookie lastVisit = cookies.get(LAST_VISIT);
		Cookie visitCount = cookies.get(VISIT_COUNT);

		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);

		PrintWriter out = response.getWriter();

		out.printf("<html>%n%n");
		out.printf("<head><title>%s</title></head>%n", TITLE);
		out.printf("<body align=\"center\">%n");
		out.printf("<h1>Hello! Welcome to 212 search engine</h1>%n%n");

		if (visitCount == null || lastVisit == null) {
			visitCount = new Cookie(VISIT_COUNT, "1");
			lastVisit = new Cookie(LAST_VISIT, getDate());

			out.printf("<p>This is your first visit!</p>");
			out.printf("<p>Thank you for visiting.</p>");
		} else {
			String decode = URLDecoder.decode(lastVisit.getValue(), StandardCharsets.UTF_8);
			out.printf("<p>You have visited this website %s times.</p> ", visitCount.getValue());
			out.printf("<p>Your last visit was on %s.</p>", decode);
			int count = Integer.parseInt(visitCount.getValue());
			visitCount.setValue(Integer.toString(count + 1));
			lastVisit.setValue(getDate());
		}
		String encode = URLEncoder.encode(lastVisit.getValue(), StandardCharsets.UTF_8);
		String xss = StringEscapeUtils.escapeHtml4(encode);
		lastVisit.setValue(xss);
		response.addCookie(visitCount);
		response.addCookie(lastVisit);

		printForm(out);
		printSearchHistory(out);

		out.printf("<p>Now is %s.</p>%n", getDate());
		out.printf("%n</body>%n");
		out.printf("</html>%n");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");

		log.info("MessageServlet ID " + this.hashCode() + " handling POST request.");

		// cookie
		HashMap<String, Cookie> cookies = new HashMap<>();
		Cookie[] cookiesArray = request.getCookies();

		if (cookiesArray != null) {
			for (Cookie c : cookiesArray) {
				cookies.put(c.getName(), c);
			}
		}

		Cookie queryHistory = cookies.get(QUERY_HISTORY);

		String query = request.getParameter("query");
		if (query == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		response.setStatus(HttpServletResponse.SC_OK);

		// Avoid XSS attacks
		query = StringEscapeUtils.escapeHtml4(query);
		ArrayList<Result> resultList = new ArrayList<>();
		ArrayList<String> output = new ArrayList<>();
		resultList = queryParser.searchForQueryLine(query);

		for (Result result : resultList) {
			output.add(result.where());
		}

		PrintWriter out = response.getWriter();
		out.printf("<html>%n%n");
		out.printf("<head><title>%s</title></head>%n", TITLE);
		out.printf("<body  align=\"center\">%n");
		out.printf("<h1>Welcome to 212 search engine</h1>%n%n");

		printForm(out);
		printSearchHistory(out);

		int count = 0;

		if (query != null) {
			out.printf("<p>Your query is: \"%s\"</p>%n", query);
			if (output.isEmpty()) {
				out.printf("<p>Result not found.</p>%n");
			} else {
				for (String q : output) {
					out.printf("\t<br>%n");
					count++;
					out.printf("<a href=%s>Result #%d: </a>", q, count);
					out.printf("<a href=%s>%s</a>", q, q);
					out.printf("\t</br>%n");
				}
				out.printf("<p>Here are(is) %d result(s): </p>%n", count);
			}
			String addOn = query;
			if (queryHistory == null) {
				queryHistory = new Cookie(QUERY_HISTORY, "");
			} else {
				addOn = SEPARATOR + query;
			}
			String temp = queryHistory.getValue();
			String encode2 = URLEncoder.encode(addOn, StandardCharsets.UTF_8);
			String xss2 = StringEscapeUtils.escapeHtml4(encode2);
			queryHistory.setValue(temp + xss2);
			response.addCookie(queryHistory);
		} else {
			out.printf("<p>Result not found.</p>%n");
		}

		out.printf("<p>Now is %s.</p>%n", getDate());
		out.printf("%n</body>%n");
		out.printf("</html>%n");
	}

	/**
	 * This method will print the html code of the search box
	 *
	 * @param out the writer to writer html code
	 * @throws IOException
	 */
	private void printForm(PrintWriter out) throws IOException {
		out.printf("<form method=\"post\" align=\"center\"  action=\"?\">%n");
		out.printf("<table cellspacing=\"0\" align=\"center\"   cellpadding=\"2\"%n");
		out.printf("<tr>%n");
		out.printf("\t<td nowrap>Query:</td>%n");
		out.printf("\t<td>%n");
		out.printf("\t\t<input type=\"text\" name=\"query\"  maxlength=\"80\" size=\"50\">%n");
		out.printf("\t</td>%n");
		out.printf("</tr>%n");
		out.printf("</table>%n");
		out.printf("<p><input type=\"submit\"  value=\"Search\"></p>\n%n");
		out.printf("</form>\n%n");
	}

	/**
	 * This method will display a search button which redirect to config page
	 *
	 * @param out the writer to write html code
	 */
	private void printSearchHistory(PrintWriter out) {
		out.printf("<form action=\"/config\" method=\"get\"><input type=\"submit\" value=\"Search History\" /></form>");
	}

	/**
	 * This method will give out the current date
	 *
	 * @return current date
	 */

	private static String getDate() {
		String format = "hh:mm a 'on' EEEE, MMMM dd yyyy";
		DateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(new Date());
	}

}