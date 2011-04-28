package osquiz;

import java.io.*;
import java.util.Date;
import java.util.logging.Level;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

/**
 * Validates the data submitted by the client.
 * @author: Rishabh Rao
 */
class InputDataValidator implements SharedVariablesInterface
{
	/**
	 * Performs basic validation of the data.
	 * @param t A Team object which must be validated.
	 * @throws java.lang.Exception
	 */
	public void validateTeam(Team t) throws Exception
	{
		// validate team name
		if(t.getTeamName().isEmpty() || t.getTeamName().length() < sv.MIN_FIELD_LENGTH)
		{
			throw new Exception("The team name must be at least 3 characters long");
		}

		// validate both member names
		if(t.getUserName(1).isEmpty() || t.getUserName(1).length() < sv.MIN_FIELD_LENGTH)
		{
			throw new Exception("Member 1's name must be at least " + sv.MIN_FIELD_LENGTH + " characters long");
		}
		
		if(t.getUserName(2).isEmpty() || t.getUserName(2).length() < sv.MIN_FIELD_LENGTH)
		{
			throw new Exception("Member 2's name must be at least " + sv.MIN_FIELD_LENGTH + " characters long");
		}

		if(t.getUSN(1).isEmpty() || t.getUSN(1).length() != sv.USN_LENGTH || !(t.getUSN(1).matches(sv.USN_REGEX)))
		{
			throw new Exception("Member 1's USN is not in the proper format<br />You entered <em>" + t.getUSN(1) + "</em>");
		}

		if(t.getUSN(2).isEmpty() || t.getUSN(2).length() != sv.USN_LENGTH || !(t.getUSN(2).matches(sv.USN_REGEX)))
		{
			throw new Exception("Member 2's USN is not in the proper format<br />You entered <em>" + t.getUSN(2) + "</em>");
		}
		
		if(t.getMobile(1).isEmpty() || t.getMobile(1).length() != sv.MOBILE_NUMBER_LENGTH || !(t.getMobile(1).matches(sv.MOBILE_REGEX)))
		{
			throw new Exception("Member 1's mobile number is not in the proper format<br />You entered <em>" + t.getMobile(1) + "</em>");
		}
		
		if(t.getMobile(2).isEmpty() || t.getMobile(2).length() != sv.MOBILE_NUMBER_LENGTH || !(t.getMobile(2).matches(sv.MOBILE_REGEX)))
		{
			throw new Exception("Member 2's mobile number is not in the proper format<br />You entered <em>" + t.getMobile(2) + "</em>");
		}

		if(t.getEmail(1).isEmpty() || !(t.getEmail(1).matches(sv.EMAIL_REGEX)))
		{
			throw new Exception("Member 1's email address is not in the proper format<br />You entered <b>" + t.getEmail(1) + "</b>");
		}
		
		if(t.getEmail(2).isEmpty() || !(t.getEmail(2).matches(sv.EMAIL_REGEX)))
		{
			throw new Exception("Member 2's email address is not in the proper format<br />You entered <b>" + t.getEmail(2) + "</b>");
		}

		// check whether they've already registered
		isAlreadyRegistered(t.getUSN(1));
		isAlreadyRegistered(t.getUSN(2));
		
		// check for secret activation code
		if(0 != (t.getActivationCode()).compareToIgnoreCase("sun.osum"))
		{
			// check activation code in XML database
			checkActivationCodeInXML(t.getActivationCode(), "loggedIn");
		}
	}

	/**
	 * A thread-safe method to read the XML database and check whether the activation code exists and has not been already used.
	 * @param code The activation code that must be checked for existance.
	 * @param attrType The type of event: loggedIn or takenQuiz
	 * @throws java.lang.Exception The known exceptions are FileNotFoundException and ParserConfigurationException, but for safety, it handles any exception.
	 */
	public synchronized void checkActivationCodeInXML(String code, String attrType) throws Exception
	{
		/* NOTE: THIS FUNCTION IS SYNCHRONIZED, ONLY ONE CLIENT HAS ACCESS TO THE XML FILE AT ANY GIVEN TIME, OTHERS WILL HAVE TO WAIT */

		if(null == code)
		{
			throw new IllegalArgumentException("The activation code provided is 'null'...");
		}

		if(code.isEmpty() || code.length() != sv.ACTIVATION_CODE_LENGTH)
		{
			throw new Exception("The activation code must be exactly " + sv.ACTIVATION_CODE_LENGTH + " characters long");
		}

		File codeBase = new File(sv.ACTIVATION_CODES_XML_FILE_LOCATION);

		synchronized(sv)
		{
			sv.quizLogger.log(Level.INFO, "Loading " + sv.ACTIVATION_CODES_XML_FILE_LOCATION + " for validating activation code, " + code + "...");
		}

		if(codeBase.exists())
		{
			// setup the DOM APIs
			DocumentBuilderFactory codesFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder codesBuilder = codesFactory.newDocumentBuilder();
			Document codesDoc = codesBuilder.parse(codeBase);

			// try to find the activation code in the XML

			// create a list of all "code" tags
			NodeList codesNodeList = codesDoc.getElementsByTagName("code");

			Element codeWalker;
			String nowCode;

			// linearly search for the activation code
			for(int i = 0; i < codesNodeList.getLength(); i++)
			{
				// get an item one by one
				codeWalker = (Element) codesNodeList.item(i);

				// get the attribute
				nowCode = codeWalker.getAttribute("value");

				// search linearly thru the database for the given code, case ignored
				if(0 == nowCode.compareToIgnoreCase(code))
				{
					// if attrType is takenQuiz, first check whether they've logged in using that code
					// this happens when, they log in with one valid activation code and they submit the answers with another valid activation code
					if(0 == attrType.compareToIgnoreCase("takenQuiz"))
					{
						if(0 == (codeWalker.getAttribute("loggedIn")).compareToIgnoreCase("no"))
						{
							break; // break and fall down and throw the  Execption below
						}
					}

					// check if that code was used to log in/take quiz once already
					if(0 == (codeWalker.getAttribute(attrType)).compareToIgnoreCase("yes"))
					{
						// the activation code was already used, so break and fall down and throw the Exception below
						break;
					}

					// if everything went well, set the loggedIn/takenQuiz flag to yes, so that the code won't be used again
					codeWalker.setAttribute(attrType, "yes");
					
					// write everything to file
					// setup the transformer APIs
					TransformerFactory codesTransformerFactory = TransformerFactory.newInstance();
					Transformer codesTransformer = codesTransformerFactory.newTransformer();

					codesTransformer.setOutputProperty(OutputKeys.INDENT, "yes");

					// source is this DOM object
					DOMSource codesSource = new DOMSource(codesDoc);

					// destination is the standard output on the server
					StreamResult codesDest =  new StreamResult(codeBase);

					// perform the transformation ie write to file
					codesTransformer.transform(codesSource, codesDest);

					// then silently return from the function, without raising any exceptions
					return;
				}
			}

			// if u parse thru the database and don't find the code, then we have a problem!
			throw new Exception("This activation code that you've entered is invalid.");
		}
		else
		{
			throw new FileNotFoundException(sv.ERROR_FILE_NOT_FOUND);
		}
	}

	/**
	 * Checks whether the given USN has already registered
	 * @param usn The student USN to search in the teamDetails.xml
	 * @throws Exception
	 */
	public void isAlreadyRegistered(String usn) throws Exception
	{
		/* NOT SYNCHRONIZED, DON'T KNOW IF THIS'LL CAUSE PROBLEMS */

		// File object that stores the team details
		File teamsBase = new File(sv.TEAMS_XML_FILE_LOCATION);

		if(teamsBase.exists())
		{
			// setup the DOM APIs
			DocumentBuilderFactory teamsFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder teamsBuilder = teamsFactory.newDocumentBuilder();
			Document teamsDoc = teamsBuilder.parse(teamsBase);

			// create a list of teams
			NodeList doneQuizTeams = teamsDoc.getElementsByTagName("member");
			if(null == doneQuizTeams)
			{
				throw new Exception("FATAL ERROR: NodeList doneQuizTeams is null; please report the problem immediately to the application administrator.<br />Sorry for the inconvenience.");
			}

			// create an Element to parse
			Element nowMember;

			// create a StringBuffer to store the USNs
			StringBuffer nowUSN = new StringBuffer();

			int i;
			// parse thru all the registered teams and get the USNs
			for(i = 0; i < doneQuizTeams.getLength(); i++)
			{
				// take a team member
				nowMember = (Element) doneQuizTeams.item(i);

				// get his/her USN
				nowUSN.replace(0, nowUSN.length(), nowMember.getAttribute("usn"));

				// if it's equal to the given USN, the flag an ERROR
				if(0 == usn.compareToIgnoreCase(nowUSN.toString()))
				{
					throw new Exception("The USN, " + usn + ", is already registered.");
				}
			}

			// the student hasn't registered already so no problem
			// silently return
		}
		else
		{
			throw new FileNotFoundException(sv.ERROR_FILE_NOT_FOUND);
		}
	}

	/**
	 * Validates time difference between login and logout time
	 * @param activationCodeAgain The activation code which was entered again
	 * @param logoutTime The time when the client submitted their answers
	 * @throws java.lang.Exception
	 */
	public void checkTimeDifference(String activationCodeAgain, Date logoutTime) throws Exception
	{
		/* NOT SYNCHRONIZED, DON'T KNOW IF THIS'LL CAUSE PROBLEMS */

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
				throw new Exception("FATAL ERROR: On the second time, the team tag was not found in the database while processing activation code " + activationCodeAgain);
			}

			// create an Element to parse
			Element teamWalker;

			// create a StringBuffer to store the activation codes or login time once activation code is found
			StringBuffer nowActivationCode = new StringBuffer();
			StringBuffer teamLoginTime_str = new StringBuffer();

			int i;
			// parse thru all the registered teams and get the loginTime that corresponds to the activationCodeAgain
			for(i = 0; i < doneQuizTeams.getLength(); i++)
			{
				// get each team detail one by one
				teamWalker = (Element) doneQuizTeams.item(i);

				nowActivationCode.replace(0, nowActivationCode.length(), teamWalker.getAttribute("activationCode"));

				if(0 == activationCodeAgain.compareToIgnoreCase(nowActivationCode.toString()))
				{
					// we found the correct activation code
					// now get the login time
					teamLoginTime_str.replace(0, teamLoginTime_str.length(), teamWalker.getAttribute("loginTime"));
					if(null == teamLoginTime_str)
					{
						throw new Exception("FATAL ERROR: Login time was not found for the team with activation code " + activationCodeAgain);
					}

					// we got what we wanted
					break;
				}
			}

			// if we traversed the whole file and didn't get the activation code, then there's a problem
			if(i >= doneQuizTeams.getLength())
			{
				throw new Exception("FATAL ERROR: The activation code was not found the the database while processing activation code: " + activationCodeAgain);
			}

			// using Long is a convenient way to convert a long to a String
			long teamLogoutTime = logoutTime.getTime();

			// parse 
			long teamLoginTime = Long.parseLong(teamLoginTime_str.toString());

			// check time difference
			if((teamLogoutTime - teamLoginTime) > sv.QUIZ_DURATION)
			{
				throw new Exception("Time is up for team with activation code: " + activationCodeAgain);
			}

			// else silently return
		}
		else
		{
			throw new FileNotFoundException(sv.ERROR_FILE_NOT_FOUND);
		}
	}
}
