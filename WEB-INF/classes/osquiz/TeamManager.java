package osquiz;

import java.io.*;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

/**
 * Handles storage and retrival of team data.
 * @author Rishabh Rao
 */
class TeamManager implements SharedVariablesInterface
{
	/**
	 * A thread-safe method to store the team details into an XML database.
	 * @param t
	 * @param loginTime The time when the team took the quiz
	 * @throws java.lang.Exception
	 */
	public synchronized void saveToTeamsXML(Team t, Date loginTime, String fileUsed) throws Exception
	{
		/* NOTE: THIS FUNCTION IS SYNCHRONIZED, ONLY ONE CLIENT HAS ACCESS TO THE XML FILE AT ANY GIVEN TIME, OTHERS WILL HAVE TO WAIT */

		// File object that stores the team details
		File teamsBase = new File(sv.TEAMS_XML_FILE_LOCATION);

		if(teamsBase.exists())
		{
			// setup the DOM APIs
			DocumentBuilderFactory teamsFactory = DocumentBuilderFactory.newInstance();

			//teamsFactory.setAttribute("indent_number", 4);

			DocumentBuilder teamsBuilder = teamsFactory.newDocumentBuilder();
			Document teamsDoc = teamsBuilder.parse(teamsBase);

			// get the root
			Element teamsRoot = teamsDoc.getDocumentElement();

			// create a new team element
			Element newTeam = teamsDoc.createElement("team");

			// set attributes as team details
			newTeam.setAttribute("teamName", t.getTeamName());
			newTeam.setAttribute("activationCode", t.getActivationCode());

			// using a Long object is a convenient way to convert a long to a String
			Long loginTime_long = loginTime.getTime();
			newTeam.setAttribute("loginTime", loginTime_long.toString());

			// set the logout time to empty, we'll set that later
			newTeam.setAttribute("logoutTime", "N/A");

			// set the file number used
			newTeam.setAttribute("fileUsed", fileUsed);

			// create a new member 1 element
			Element member_1 = teamsDoc.createElement("member");

			// setup member 1 attribues
			member_1.setAttribute("id", t.getId(1));
			member_1.setAttribute("userName", t.getUserName(1));
			member_1.setAttribute("usn", t.getUSN(1));
			member_1.setAttribute("sem", t.getSem(1));
			member_1.setAttribute("branch", t.getBranch(1));
			member_1.setAttribute("email", t.getEmail(1));
			member_1.setAttribute("mobile", t.getMobile(1));

			// add first member to team element
			newTeam.appendChild(member_1);

			// create a new member 1 element
			Element member_2 = teamsDoc.createElement("member");

			// setup member 2 attribues
			member_2.setAttribute("id", t.getId(2));
			member_2.setAttribute("userName", t.getUserName(2));
			member_2.setAttribute("usn", t.getUSN(2));
			member_2.setAttribute("sem", t.getSem(2));
			member_2.setAttribute("branch", t.getBranch(2));
			member_2.setAttribute("email", t.getEmail(2));
			member_2.setAttribute("mobile", t.getMobile(2));

			// add second member to team element
			newTeam.appendChild(member_2);

			// add the team to RegisteredTeams (root) node
			teamsRoot.appendChild(newTeam);

			TransformerFactory teamsTransformerFactory = TransformerFactory.newInstance();
			Transformer teamsTransformer = teamsTransformerFactory.newTransformer();

			teamsTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
			// teamsTransformerFactory.setAttribute("indent-number", 4);

			Source src = new DOMSource(teamsDoc);
			Result dest = new StreamResult(teamsBase);
			teamsTransformer.transform(src, dest);
		}
		else
		{
			throw new FileNotFoundException("INTERNAL SERVER ERROR: XML database file was not found, please contact the application administrator immediately...");
		}

		// teamsquizTeam.toString();
	}

	/**
	 * A thread-safe method to add the logout time data to the teams XML file.
	 * @param activationCodeAgain The activation code which was entered again
	 * @param logoutTime The time when the client submitted their answers
	 * @throws java.lang.Exception
	 */
	public synchronized void saveToTeamsXML(String activationCodeAgain, Date logoutTime, Integer finalScore) throws Exception
	{
		/* NOTE: THIS FUNCTION IS SYNCHRONIZED, ONLY ONE CLIENT HAS ACCESS TO THE XML FILE AT ANY GIVEN TIME, OTHERS WILL HAVE TO WAIT */

		// File object that stores the team details
		File teamsBase = new File(sv.TEAMS_XML_FILE_LOCATION);

		if(teamsBase.exists())
		{
			// setup the DOM APIs
			DocumentBuilderFactory teamsFactory = DocumentBuilderFactory.newInstance();

			//teamsFactory.setAttribute("indent_number", 4);

			DocumentBuilder teamsBuilder = teamsFactory.newDocumentBuilder();
			Document teamsDoc = teamsBuilder.parse(teamsBase);

			// create a list of teams
			NodeList doneQuizTeams = teamsDoc.getElementsByTagName("team");
			if(null == doneQuizTeams)
			{
				throw new Exception("FATAL ERROR: On the second time, the team tag was not found in the database while processing activation code " + activationCodeAgain);
			}

			// create an Element to parse
			Element teamWalker;

			// create a StringBuffer to store the activation codes or login time once activation code is found
			StringBuffer nowActivationCode = new StringBuffer();

			// using a Long object is a convenient way to convert a long to a String
			Long logoutTime_long = logoutTime.getTime();

			int i;
			// parse thru all the registered teams and get the team that corresponds to the activationCodeAgain
			for(i = 0; i < doneQuizTeams.getLength(); i++)
			{
				// get each team detail one by one
				teamWalker = (Element) doneQuizTeams.item(i);

				// get that team's activation code
				nowActivationCode.replace(0, nowActivationCode.length(), teamWalker.getAttribute("activationCode"));

				// if that's the team we're looking for, then ...
				if(0 == activationCodeAgain.compareToIgnoreCase(nowActivationCode.toString()))
				{
					// ... set the logout time attribute
					teamWalker.setAttribute("logoutTime", logoutTime_long.toString());
					teamWalker.setAttribute("finalScore", finalScore.toString());
					
					// we're done
					break;
				}
			}			

			// all done, write the file back
			TransformerFactory teamsTransformerFactory = TransformerFactory.newInstance();
			Transformer teamsTransformer = teamsTransformerFactory.newTransformer();

			teamsTransformer.setOutputProperty(OutputKeys.INDENT, "yes");
			// teamsTransformerFactory.setAttribute("indent-number", 4); // apparently not working

			Source src = new DOMSource(teamsDoc);
			Result dest = new StreamResult(teamsBase);
			teamsTransformer.transform(src, dest);
		}
		else
		{
			throw new FileNotFoundException(sv.ERROR_FILE_NOT_FOUND);
		}

		// teamsquizTeam.toString();
	}
}
