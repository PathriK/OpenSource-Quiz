package osquiz;

import java.io.*;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 *
 * @author Rishabh
 */
class AnswerSheetChecker implements SharedVariablesInterface
{
	/**
	 * Opens the question paper XML database, checks the answers, and returns the score
	 * @return score The number of correct answers
	 */
	public synchronized int scoreAnswers(HttpServletRequest clientAnswerSheet, String fileNumber) throws Exception
	{
		int score = 0;

		File questionsBase = new File(sv.QUESTIONS_XML_FILE_LOCATION + fileNumber + ".xml");

		synchronized(sv)
		{
			sv.quizLogger.log(Level.INFO, "Loading " + sv.QUESTIONS_XML_FILE_LOCATION + fileNumber + ".xml for checking answers...");
		}

		if(questionsBase.exists())
		{
			// setup the DOM APIs
			DocumentBuilderFactory questionsFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder questionsBuilder = questionsFactory.newDocumentBuilder();
			Document questionsDoc = questionsBuilder.parse(questionsBase);

			// create a list of all "question" tags
			NodeList questionsNodeList = questionsDoc.getElementsByTagName("question");

			// walks thru the <question> tag and allows access to the answers
			Element answerWalker;

			// two dump locations to store the correct and client answers
			StringBuffer correctAnswer = new StringBuffer();
			StringBuffer clientAnswer = new StringBuffer();

			// go thru all the questions and verify the answers
			for(int i = 0; i < questionsNodeList.getLength(); i++)
			{
				// get each <question> tag one by one
				answerWalker = (Element) questionsNodeList.item(i);

				// store the correct answer into a buffer
				correctAnswer.replace(0, correctAnswer.length(), answerWalker.getAttribute("answer"));

				// the team did not answer the question, skip it
				if(null == clientAnswerSheet.getParameter("answer_" + i))
				{
					continue;
				}
				else // if they answered it, then get their answer
				{
					clientAnswer.replace(0, clientAnswer.length(), clientAnswerSheet.getParameter("answer_" + i));
				}

				// if attribute does not exist, then flag an error
				// might happen in a rare case, where the user edited the XHTML and tried to modify the form
				if(null == clientAnswer.toString())
				{
					throw new Exception("FATAL ERROR: Answer number: " + i + " is missing");
				}

				// else, proceed and correct and give marks to that answer
				if(0 == (correctAnswer.toString()).compareToIgnoreCase(clientAnswer.toString()))
				{
					score++;
				}
			}
		}
		else
		{
			throw new FileNotFoundException(sv.ERROR_FILE_NOT_FOUND);
		}

		return score;
	}

	String getQuestionPaperNumber(String activationCodeAgain) throws Exception
	{
		// File object that stores the team details
		File teamsBase = new File(sv.TEAMS_XML_FILE_LOCATION);

		if(teamsBase.exists())
		{
			// setup the DOM APIs
			DocumentBuilderFactory teamsFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder teamsBuilder = teamsFactory.newDocumentBuilder();
			Document teamsDoc = teamsBuilder.parse(teamsBase);

			// create a list of teams
			NodeList doneQuizTeams = teamsDoc.getElementsByTagName("team");
			if(null == doneQuizTeams)
			{
				throw new Exception("FATAL ERROR: NodeList doneQuizTeams is null; please report the problem immediately to the application administrator.<br />Sorry for the inconvenience.");
			}

			// create an Element to parse
			Element nowMember;

			// create a StringBuffer to store the USNs
			StringBuffer nowActivationCode = new StringBuffer();
			StringBuffer holdFileNumberUsed = new StringBuffer();

			int i;
			// parse thru all the registered teams and get the USNs
			for(i = 0; i < doneQuizTeams.getLength(); i++)
			{
				// take a team member
				nowMember = (Element) doneQuizTeams.item(i);

				// get his/her USN
				nowActivationCode.replace(0, nowActivationCode.length(), nowMember.getAttribute("activationCode"));

				if(0 == activationCodeAgain.compareToIgnoreCase(nowActivationCode.toString()))
				{
					holdFileNumberUsed.replace(0, holdFileNumberUsed.length(), nowMember.getAttribute("fileUsed"));
					return holdFileNumberUsed.toString();
				}
			}

			// it must find the fileUsed variable in the above for loop, or else something is wrong
			throw new Exception("FATAL ERROR: I parsed thru all the teams but your team does not seem to have the fileUsed variable stored; please report the problem immediately to the application administrator.<br />Sorry for the inconvenience.");
		}
		else
		{
			throw new FileNotFoundException(sv.ERROR_FILE_NOT_FOUND);
		}
	}
}
