package osquiz;

import java.io.PrintWriter;

/**
 * Handles all recoverable errors made by the client.
 * @author Rishabh Rao
 */
class ErrorHandler implements SharedVariablesInterface
{
	PrintWriter errorWriter;

	ErrorHandler(PrintWriter pw)
	{
		errorWriter = pw;
	}
	/**
	 * Sends an XHTML error page containing the reason to the client explaining the error.
	 * @param ex The exception that occured
	 */
	public void handleError(Exception ex)
	{
		errorWriter.println("<table border=\"0\" width=\"800\">");
		errorWriter.println("<tr>");
		errorWriter.println("<td><img border=\"0\" src=\"./resources/quiz_title.jpg\" width=\"800\" height=\"225\" /></td>");
		errorWriter.println("</tr>");
		errorWriter.println("<tr align=\"left\">");
		errorWriter.println("<td>");
		errorWriter.println("<h1>Uh oh! Houston, we have a problem!</h1>");
		errorWriter.println("<h2>Greetings, I'm your friendly-neighbourhood ErrorHandler!<br />It seems that we've run into a little problem...</h2>");
		errorWriter.println("<p>If you're using a <b>JavaScript</b>-enabled browser, I recommend that you <b>enable</b> JavaScript.</p>");
		errorWriter.println("<p>Furthermore, there's no need to panic though, I'm here to help you!</p>");
		errorWriter.println("<p><p>Can you make sense of the error description below?</p></p>");
		errorWriter.println("<p class=\"error\">" + ex.toString() + "</p>");
		errorWriter.println("<p><p>Well, if you can, then try to correct the error by yourself... that shouldn't be too hard now, right?</p></p>");
		errorWriter.println("<p><p>I also suggest that you use your browser's <b>Back</b> button to go back and correct the errors.</p></p>");
		errorWriter.println("<p></p><h3>But if you're sure you've done everything correctly and this problem <em>still persists</em>, please send an email to the application administrator, Rishabh Rao (7th ISE), <b>rishabhsrao [-A-T-] gmail [-D-O-T-] com</b><br /><br />Explain the nature of your problem <b>along with the complete stack trace</b> shown below.<br />Be sure you include '<b>open-source quiz</b>' in the subject line.</h3>");
		errorWriter.println("<p></p><p><b>Stack Trace:</b><br />");
		ex.printStackTrace(errorWriter);
		errorWriter.println("</p>");
		errorWriter.println("</td>");
		errorWriter.println("</tr>");
		errorWriter.println("</table>");
	}
}
