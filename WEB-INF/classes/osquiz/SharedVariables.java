package osquiz;

import java.io.File;
import java.util.logging.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 *
 * @author Rishabh
 */
class SharedVariables
{
	// all use the default access specifier, public inside package

	// our server URL
	String SERVER_NAME;

	ErrorHandler quizError;

	Logger quizLogger = Logger.getLogger("quizLogger");

	// XML database file locations
	String QUESTIONS_XML_FILE_LOCATION;
	String TEAMS_XML_FILE_LOCATION;
	String ACTIVATION_CODES_XML_FILE_LOCATION;

	FileHandler infoLogHandler;
	String INFO_LOG_FILE_LOCATION;
	int INFO_LOG_FILE_SIZE_LIMIT;
	int INFO_LOG_FILE_COUNT;

	// some integer constants
	int MIN_FIELD_LENGTH;
	int ACTIVATION_CODE_LENGTH;
	int MOBILE_NUMBER_LENGTH = 10;
	int USN_LENGTH = 10;
	long QUIZ_DURATION;
	int QUIZ_DURATION_IN_MINUTES;
	int MAX_QUESTIONS_DATABASE_FILES;

	// regular expressions
	String MOBILE_REGEX = new String("[0-9]*");
	String EMAIL_REGEX = new String("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]+");
	String USN_REGEX = new String("[1-4][a-zA-Z][a-zA-Z][0-9][0-9][a-zA-Z][a-zA-Z][0-9][0-9][0-9]");

	// error messages
	String ERROR_FILE_NOT_FOUND = new String("FATAL INTERNAL SERVER ERROR: A required database file was not found; please report the problem immediately to the application administrator.<br />Sorry for the inconvenience.");

	/**
	 * Initializes all the constants used in the package from the configuration file located at cfl.
	 * @param cfl
	 * @throws java.lang.Exception
	 */
	public void initializeFromXML(String cfl) throws Exception
	{
		File configBase = new File(cfl);

		// setup the DOM APIs
		DocumentBuilderFactory configFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder configBuilder = configFactory.newDocumentBuilder();
		Document configDoc = configBuilder.parse(configBase);

		// create an Element to store the Variable element
		Element configVar;

		// get the server location
		configVar = configDoc.getElementById("SERVER_NAME");
		SERVER_NAME = new String(configVar.getAttribute("value"));
		System.err.println("SERVER_NAME = " + SERVER_NAME);
		if(SERVER_NAME.isEmpty())
		{
			throw new Exception("INTERNAL SERVER ERROR: Variable SERVER_NAME is not defined in the Configuration file: " + cfl);
		}

		// get the question paper location
		configVar = configDoc.getElementById("QUESTIONS_XML_FILE_LOCATION");
		QUESTIONS_XML_FILE_LOCATION = new String(configVar.getAttribute("value"));
		System.err.println("QUESTIONS_XML_FILE_LOCATION = " + QUESTIONS_XML_FILE_LOCATION);
		if(QUESTIONS_XML_FILE_LOCATION.isEmpty())
		{
			throw new Exception("INTERNAL SERVER ERROR: Variable QUESTIONS_XML_FILE_LOCATION is not defined in the Configuration file: " + cfl);
		}

		// get the team details storage location
		configVar = configDoc.getElementById("TEAMS_XML_FILE_LOCATION");
		TEAMS_XML_FILE_LOCATION = new String(configVar.getAttribute("value"));
		System.err.println("SERVER_NAME = " + TEAMS_XML_FILE_LOCATION);
		if(TEAMS_XML_FILE_LOCATION.isEmpty())
		{
			throw new Exception("INTERNAL SERVER ERROR: Variable TEAMS_XML_FILE_LOCATION is not defined in the Configuration file: " + cfl);
		}

		// get the activation codes location
		configVar = configDoc.getElementById("ACTIVATION_CODES_XML_FILE_LOCATION");
		ACTIVATION_CODES_XML_FILE_LOCATION = new String(configVar.getAttribute("value"));
		System.err.println("ACTIVATION_CODES_XML_FILE_LOCATION = " + ACTIVATION_CODES_XML_FILE_LOCATION);
		if(ACTIVATION_CODES_XML_FILE_LOCATION.isEmpty())
		{
			throw new Exception("INTERNAL SERVER ERROR: Variable ACTIVATION_CODES_XML_FILE_LOCATION is not defined in the Configuration file: " + cfl);
		}

		// get the INFO log file location
		configVar = configDoc.getElementById("INFO_LOG_FILE_LOCATION");
		INFO_LOG_FILE_LOCATION = new String(configVar.getAttribute("value"));
		INFO_LOG_FILE_SIZE_LIMIT = Integer.parseInt(configVar.getAttribute("LOG_FILE_SIZE_LIMIT"));
		INFO_LOG_FILE_COUNT = Integer.parseInt(configVar.getAttribute("LOG_FILE_COUNT"));
		System.err.println("INFO_LOG_FILE_LOCATION = " + INFO_LOG_FILE_LOCATION + " " + INFO_LOG_FILE_SIZE_LIMIT + " " + INFO_LOG_FILE_COUNT);
		if(INFO_LOG_FILE_LOCATION.isEmpty())
		{
			throw new Exception("INTERNAL SERVER ERROR: Variable INFO_LOG_FILE_LOCATION is not defined in the Configuration file: " + cfl);
		}

		// setup logger - log file bindings
		infoLogHandler = new FileHandler(INFO_LOG_FILE_LOCATION, INFO_LOG_FILE_SIZE_LIMIT, INFO_LOG_FILE_COUNT, true);
		infoLogHandler.setLevel(Level.INFO);
		quizLogger.addHandler(infoLogHandler);


		// get the minimum field length
		configVar = configDoc.getElementById("MIN_FIELD_LENGTH");
		MIN_FIELD_LENGTH = Integer.parseInt(configVar.getAttribute("value"));
		System.err.println("MIN_FIELD_LENGTH = " + MIN_FIELD_LENGTH);

		// get the activation code length
		configVar = configDoc.getElementById("ACTIVATION_CODE_LENGTH");
		ACTIVATION_CODE_LENGTH = Integer.parseInt(configVar.getAttribute("value"));
		System.err.println("ACTIVATION_CODE_LENGTH = " + ACTIVATION_CODE_LENGTH);

		// get the quiz duration
		configVar = configDoc.getElementById("QUIZ_DURATION_IN_MINUTES");
		QUIZ_DURATION_IN_MINUTES = Integer.parseInt(configVar.getAttribute("value"));
		// convert to milliseconds, one minute extra for communication delays etcS
		QUIZ_DURATION = (long) ((QUIZ_DURATION_IN_MINUTES + 1) * 60 * 1000);
		System.err.println("QUIZ_DURATION = " + QUIZ_DURATION);

		// get the max number of questions database files
		configVar = configDoc.getElementById("MAX_QUESTIONS_DATABASE_FILES");
		MAX_QUESTIONS_DATABASE_FILES = Integer.parseInt(configVar.getAttribute("value"));
		System.err.println("MAX_QUESTIONS_DATABASE_FILES = " + MAX_QUESTIONS_DATABASE_FILES);
	}
}
