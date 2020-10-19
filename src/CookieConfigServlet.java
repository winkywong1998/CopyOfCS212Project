import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

@SuppressWarnings("serial")
public class CookieConfigServlet extends HttpServlet {
	private static final String TITLE = "Search Histroy";
	private static Logger log = Log.getRootLogger();
	public static final String SEPARATOR = SearchServlet.SEPARATOR;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.info("GET " + request.getRequestURL().toString());

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		out.printf("<html>%n%n");
		out.printf("<head><title>%s</title></head>%n", TITLE);
		out.printf("<body align=\"center\">%n");
		out.printf("<h1>Search History</h1>%n%n");

		HashMap<String, Cookie> cookies = new HashMap<>();
		Cookie[] cookiesArray = request.getCookies();

		if (cookiesArray != null) {
			for (Cookie c : cookiesArray) {
				cookies.put(c.getName(), c);
			}
		}
		Cookie queryHistory = cookies.get("Queries");

		if (queryHistory == null) {
			out.printf("<p>No search history! </p>%n");
		} else {
			String decode = URLDecoder.decode(queryHistory.getValue(), StandardCharsets.UTF_8);
			String[] historyList = decode.split(SEPARATOR);

			int count = 0;

			if (historyList.length != 0) {
				out.printf("<p>Here is your search history: </p>%n");
				for (String s : historyList) {
					count++;
					out.printf("<p>#%d: %s</p>%n", count, s);
				}
				out.printf("<p>Here are(is) %d history record(s): </p>%n", count);
			}
		}
		out.printf("<form method=\"post\" action=\"%s\">%n", request.getRequestURI());
		out.printf("<input type=\"submit\" value=\"Clear cookies\">%n");
		out.printf("</form>%n");
		out.printf("<form action=\"/\" method=\"get\"><input type=\"submit\" value=\"Back to main page\" /></form>");
		out.printf("%n</body>%n");
		out.printf("</html>%n");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.info("POST " + request.getRequestURL().toString());

		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				cookie.setValue(null);
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
		PrintWriter out = response.getWriter();
		out.printf("<html>%n%n");
		out.printf("<head><title>%s</title></head>%n", TITLE);
		out.printf("<body align=\"center\">%n");
		out.printf("<p>Your seach history has been cleared.</p>%n%n");
		out.printf("<form action=\"/\" method=\"get\"><input type=\"submit\" value=\"Back to main page\" /></form>");
		out.printf("%n</body>%n");
		out.printf("</html>%n");
	}
}
