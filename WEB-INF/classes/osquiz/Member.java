package osquiz;

import javax.servlet.http.HttpServletRequest;

/**
 * The Member class provides an elegant way to store the team member details.
 * @author Rishabh Rao
 */
class Member implements SharedVariablesInterface
{
	protected String userName;
	protected String usn;
	protected String sem;
	protected String branch;
	protected String email;
	protected String mobile;

	protected int id;

	/**
	 * Constructs the member details based on the HTTP POST request by the client.
	 * @param num
	 * @param request
	 */
	Member(int num, HttpServletRequest request) throws Exception
	{
		id = num;

		// get member details
		userName	= new String(request.getParameter("userName_" + id));
		if(null == userName)
		{
			throw new Exception("FATAL ERROR: The user name is missing");
		}

		usn			= new String(request.getParameter("usn_" + id));
		if(null == usn)
		{
			throw new Exception("FATAL ERROR: The USN is missing");
		}

		sem			= new String(request.getParameter("sem_" + id));
		if(null == sem)
		{
			throw new Exception("FATAL ERROR: The semester is missing");
		}

		// TODO: Cannot delete branch in a hurry, so simply assigning "ISE" to it
		branch		= "ISE"; // new String(request.getParameter("branch_" + id));
		if(null == branch)
		{
			throw new Exception("FATAL ERROR: The USN is missing");
		}

		email		= new String(request.getParameter("email_" + id));
		if(null == email)
		{
			throw new Exception("FATAL ERROR: The email address is missing");
		}

		mobile		= new String(request.getParameter("mobile_" + id));
		if(null == mobile)
		{
			throw new Exception("FATAL ERROR: The mobile number is missing");
		}
	}

	/**
	 * Returns the user name of the team member.
	 * @return userName
	 */
	public String getUserName()
	{
		return(userName);
	}

	/**
	 * Returns the USN of the team member.
	 * @return userName
	 */
	public String getUSN()
	{
		return(usn);
	}

	/**
	 * Returns the semester of the team member.
	 * @return sem
	 */
	public String getSem()
	{
		return(sem);
	}

	/**
	 * Returns the branch of the team member.
	 * @return branch
	 */
	public String getBranch()
	{
		return(branch);
	}

	/**
	 * Returns the email address of the team member.
	 * @return email
	 */
	public String getEmail()
	{
		return(email);
	}

	/**
	 * Returns the mobile number of the team member.
	 * @return mobile
	 */
	public String getMobile()
	{
		return(mobile);
	}

	/**
	 * Returns the id of the team member i.e. if the member is the first one, returns 1 else 2.
	 * @return
	 */
	public String getId()
	{
		Integer id_integer = new Integer(id);
		return(id_integer.toString());
	}
}
