package osquiz;

import java.io.*;
import java.util.logging.Level;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 *
 * @author Rishabh
 */
class QuestionPaperGenerator implements SharedVariablesInterface
{
	/**
	 * Dynamically generates the question paper from the question paper XML database and sends it to the client.
	 * @param cpw
	 * @throws java.lang.Exception
	 */
	public void sendQuestionPaper(PrintWriter cpw, String fileNumber) throws Exception
	{		
		File questionsBase = new File(sv.QUESTIONS_XML_FILE_LOCATION + fileNumber + ".xml");

		synchronized(sv)
		{
			sv.quizLogger.log(Level.INFO, "Loading " + sv.QUESTIONS_XML_FILE_LOCATION + fileNumber + ".xml" + " for generating questions...");
		}

		if(questionsBase.exists())
		{
			// setup the DOM APIs
			DocumentBuilderFactory questionsFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder questionsBuilder = questionsFactory.newDocumentBuilder();
			Document questionsDoc = questionsBuilder.parse(questionsBase);

			// create a list of all "question" tags
			NodeList questionsNodeList = questionsDoc.getElementsByTagName("question");

			Element questionWalker;
			StringBuffer nowQuestion = new StringBuffer();

//			FLOATING TIMER SCRIPT, DOES NOT WORK

//			cpw.println("<!-- Float script start -->");
//			cpw.println("<!-- Courtesy of SimplytheBest.net - http://simplythebest.net/scripts/ -->");
//			cpw.println("<SCRIPT src=\"" + sv.SERVER_NAME + "/Quiz/resources/float.js\">");
//			cpw.println("</SCRIPT>");
//				cpw.println("<div class=\"Logo\" id=\"Logo\" style=\"L: 6px; POSITION: absolute; TOP: 10px\">");
//					cpw.println("<form name=\"cd\">");
//						cpw.println("<label>Time left: <input id=\"timeLeft\" name=\"timeLeft\" size=\"5\" readonly=\"true\" type=\"text\" value=\"" + sv.QUIZ_DURATION_IN_MINUTES + ":00\" border=\"0\" name=\"disp\"></input></label>");
//					cpw.println("</form>");
//				cpw.println("</div>");
//			cpw.println("<!-- Float script end -->");

			// display the question paper in the page
			// print starting part
			cpw.println("<table border=\"0\" width=\"800\">");
				cpw.println("<tr>");
					cpw.println("<td><img border=\"0\" src=\"" + sv.SERVER_NAME + "/Quiz/resources/quiz_title.jpg\" width=\"800\" height=\"225\" /></td>");
				cpw.println("</tr>");

				cpw.println("<tr align=\"right\">");
					cpw.println("<td>");
						cpw.println("<form name=\"cd\">");
						cpw.println("<label>Time left: <input id=\"txt\" size=\"5\" readonly=\"true\" type=\"text\" value=\"" + sv.QUIZ_DURATION_IN_MINUTES + ":00\" border=\"0\" name=\"disp\"></input></label>");
						cpw.println("</form>");
					cpw.println("</td>");
				cpw.println("</tr>");

				cpw.println("<tr align=\"left\">");
					cpw.println("<td>");
						cpw.println("<form name=\"teamAnswers\" action=\"" + sv.SERVER_NAME + "/Quiz/OSQuizStart\" method=\"POST\">");
							cpw.println("<p><label>Enter your activation code again: <input type=\"text\" name=\"activationCodeAgain\" size=\"25\" maxlength=\"8\" /></label></p>");
					cpw.println("</td>");
				cpw.println("</tr>");

				cpw.println("<tr align=\"center\">");
					cpw.println("<td>");
						cpw.println("<h2>Question Paper</h2>");
					cpw.println("</td>");
				cpw.println("</tr>");

				for(int i = 0; i < questionsNodeList.getLength(); i++)
				{
					// get each question one by one
					questionWalker = (Element) questionsNodeList.item(i);

					// get the first question into the StringBuffer
					nowQuestion.replace(0, nowQuestion.length(), questionWalker.getAttribute("value"));

					// print it on screen using a table
					cpw.println("<tr align=\"left\">");
					cpw.println("<td><p class=\"question\">" + (i + 1) + ". ");
					cpw.print(nowQuestion.toString() + "</p>");

					// display the rest of the 4 options
					for(int j = 1; j <= 4; j++)
					{
						cpw.println("<label><input type=\"radio\" name=\"answer_" + i + "\" value=\"" + questionWalker.getAttribute("option_" + j) + "\"></input> " + questionWalker.getAttribute("option_" + j) + "</label><br />");
					}

					cpw.println("</td>");
					cpw.println("</tr>");
				}

			// put up a submit button
			cpw.println("<tr align=\"center\"><td><input type=\"submit\" value=\"Submit Answers\"></tr></td>");

			// close the tags
			cpw.println("</form>");
			cpw.println("</table>");
		}
		else
		{
			throw new FileNotFoundException(sv.ERROR_FILE_NOT_FOUND);
		}
	}
}
