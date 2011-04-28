package osquiz;


import java.io.*;
import java.util.Date;
import java.util.logging.Level;
import javax.servlet.*;
import javax.servlet.http.*;


/**
 * Handles the submitted answers from the client.
 * @author: Rishabh Rao
 */
public class QuizManagerServlet extends HttpServlet implements SharedVariablesInterface
{
	/**
	 * Initializes the servlet with parameters send by the server.
	 * @param quizServletConfig The ServletConfig object that stores the initialization parameters.
	 */
	@Override public void init(ServletConfig quizServletConfig)
	{
		try
		{
			synchronized(sv)
			{
				sv.quizLogger.log(Level.INFO, "Initializing QuizManagerServlet...");
			}

			sv.initializeFromXML(quizServletConfig.getInitParameter("CONFIG_XML_FILE_LOCATION"));
		}
		catch (Exception ex)
		{
			synchronized(sv)
			{
				sv.quizLogger.log(Level.SEVERE, ex.toString(), ex);
			}

			return;
		}
	}

	/**
	 * Processes the  HTTP POST requests from the client.
	 * @param request A HttpServletRequest object which stores the input form data, among other things.
	 * @param response A HttpServletResponse object which provides a connection to the client to send back any response.
	 * @throws java.io.IOException
	 * @throws javax.servlet.ServletException
	 */
	@Override public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// first get the submission time
		Date logoutTime = new Date();

		synchronized(sv)
		{
			sv.quizLogger.log(Level.INFO, ("IP: " + request.getRemoteAddr() + " - Initiating answer correction session..."));
		}

		// get a output channel to the client
		PrintWriter onClient = response.getWriter();
		// set MIME type as HTML
		response.setContentType("text/html");
		// Set to expire far in the past.
		response.setHeader("Expires", "Tue, 30 May 1989 12:00:00 GMT");
		// Set standard HTTP/1.1 no-cache headers.
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		// Set standard HTTP/1.0 no-cache header.
		response.setHeader("Pragma", "no-cache");

		onClient.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		onClient.println("<!DOCTYPE html PUBLIC \"-//w3c//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
		onClient.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		onClient.println("<head>");

		onClient.println("<META HTTP-EQUIV=\"CACHE-CONTROL\" CONTENT=\"NO-CACHE\">");
		onClient.println("<META HTTP-EQUIV=\"EXPIRES\" CONTENT=\"01 Jan 1970 00:00:00 GMT\">");
		onClient.println("<META HTTP-EQUIV=\"PRAGMA\" CONTENT=\"NO-CACHE\">");

		onClient.println("<script type=\"text/javascript\" src=\"" + sv.SERVER_NAME + "/Quiz/resources/cdtimer.js\"></script>");

		onClient.println("<title>open-source quiz | JSSATE OSUM</title>");
		onClient.println("<link type=\"text/css\" rel=\"stylesheet\" href=\"" + sv.SERVER_NAME + "/Quiz/resources/quizStyle.css\"/>");
		onClient.println("</head>");
		onClient.println("<body><center>");

		try
		{	
			synchronized(sv)
			{
				sv.quizLogger.log(Level.INFO, ("IP: " + request.getRemoteAddr() + " - Validating activation code again..."));
			}

			// validate the activation code
			String activationCodeAgain = new String(request.getParameter("activationCodeAgain"));

			InputDataValidator idv = new InputDataValidator();

			if(0 != activationCodeAgain.compareToIgnoreCase("sun.osum"))
			{
				// check whether this activation code was used to take quiz already
				idv.checkActivationCodeInXML(activationCodeAgain, "takenQuiz");
			}
			
			// check time
			idv.checkTimeDifference(activationCodeAgain, logoutTime);

			// proceed if everything went well
			synchronized(sv)
			{
				sv.quizLogger.log(Level.INFO, ("IP: " + request.getRemoteAddr() + " - Now checking answers..."));
			}

			// check and score the answers
			AnswerSheetChecker ac = new AnswerSheetChecker();
			String fileNumber = ac.getQuestionPaperNumber(activationCodeAgain);
			Integer finalScore = new Integer(ac.scoreAnswers(request, fileNumber));

			// save the score into the registered teams database
			TeamManager toSubmitScores = new TeamManager();
			toSubmitScores.saveToTeamsXML(activationCodeAgain, logoutTime, finalScore);

			// write thank you message
			onClient.println("<table border=\"0\" width=\"800\">");
			onClient.println("<tr>");
			onClient.println("<td><img border=\"0\" src=\"./resources/quiz_title.jpg\" width=\"800\" height=\"225\" /></td>");
			onClient.println("</tr>");
			onClient.println("<tr align=\"left\">");
			onClient.println("<td>");
			onClient.println("<h1>Congratulations!</h1>");
			onClient.println("<br /><h2>You've successfully completed the quiz!</h2>");
			
			onClient.println("<p>If you are selected as one of the winners, then we'll give you a call and/or send you an SMS/email. So, keep an eye out for us!</p>");
			onClient.println("<br /><h2>Thank you very much!</h2>");
			onClient.println("<br /><br /><h3>Good bye!</h3>");
			onClient.println("</td>");
			onClient.println("</tr>");
			onClient.println("</table>");


		}
		catch (Exception ex)
		{
			// ErrorHandler, in essence, returns an error page to the client explaining the error
			synchronized(sv)
			{
				sv.quizLogger.log(Level.WARNING, ("IP: " + request.getRemoteAddr() + " - " + ex.toString()));
			}

			ErrorHandler lerr = new ErrorHandler(onClient);
			lerr.handleError(ex);
		}
		finally
		{
			onClient.println("</center></body>");
			onClient.println("</html>");
			onClient.close();
			return;
		}
	}

	/**
	 * Processes the  HTTP GET requests from the client.
	 * @param request A HttpServletRequest object which stores the input form data, among other things.
	 * @param response A HttpServletResponse object which provides a connection to the client to send back any response.
	 * @throws java.io.IOException
	 * @throws javax.servlet.ServletException
	 */
	@Override public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter onClient = response.getWriter();

		onClient.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		onClient.println("<!DOCTYPE html PUBLIC \"-//w3c//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
		onClient.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		onClient.println("<head>");
		onClient.println("<title>open-source quiz | JSSATE OSUM</title>");
		onClient.println("<link type=\"text/css\" rel=\"stylesheet\" href=\"" + sv.SERVER_NAME + "/Quiz/resources/quizStyle.css\"/>");
		onClient.println("</head>");
		onClient.println("<body><center>");

		try
		{
			throw new Exception("You're not authorized to view this page!<br /><a href=\"" + sv.SERVER_NAME + "/Quiz/index.xhtml\">Click here to go to the login page and register</a> yourselves first!");
		}
		catch(Exception ex)
		{
			synchronized(sv)
			{
				sv.quizLogger.log(Level.WARNING, ("IP: " + request.getRemoteAddr() + " - Tried to access via HTTP GET..."));
			}

			// ErrorHandler, in essence, returns an error page to the client explaining the error
			ErrorHandler quizError = new ErrorHandler(onClient);
			quizError.handleError(ex);
		}
		finally
		{
			onClient.println("</center></body>");
			onClient.println("</html>");
			onClient.close();
			return;
		}
	}
}
