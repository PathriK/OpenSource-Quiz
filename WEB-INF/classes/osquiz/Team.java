package osquiz;

import javax.servlet.http.HttpServletRequest;

/**
 * The Team class provides an elegant way to store the team details.
 * @author Rishabh Rao
 */
class Team implements SharedVariablesInterface
{
	protected String teamName;
	protected String activationCode;

	protected Member player_1;
	protected Member player_2;

	/**
	 * Constructs the team details based on the HTTP POST request by the client.
	 * @param request
	 */
	Team(HttpServletRequest request) throws Exception
	{
		player_1 = new Member(1, request);
		player_2 = new Member(2, request);

		// get team details
		teamName = new String(request.getParameter("teamName"));
		// if attribute does not exist, then flag an error
		// might happen in a rare case, where the user edited the XHTML and tried to modify the form
		if(null == teamName)
		{
			throw new Exception("FATAL ERROR: The team name is missing");
		}

		activationCode = new String(request.getParameter("activationCode"));
		if(null == activationCode)
		{
			throw new Exception("FATAL ERROR: The activation code is missing");
		}
	}

	/**
	 * Returns the team's name.
	 * @return teamName
	 */
	public String getTeamName()
	{
		return teamName;
	}

	/**
	 * Returns the activation code used by the team.
	 * @return activationCode
	 */
	public String getActivationCode()
	{
		return activationCode;
	}

	/**
	 * Returns the name of the member based on Member.id field.
	 * @param member
	 * @return Member.userName
	 */
	public String getUserName(int member)
	{
		return(member == 1 ? player_1.getUserName() : player_2.getUserName());
	}

	/**
	 * Returns the USN of the member based on Member.id field.
	 * @param member
	 * @return Member.userName
	 */
	public String getUSN(int member)
	{
		return(member == 1 ? player_1.getUSN() : player_2.getUSN());
	}

	/**
	 * Returns the semester of the member based on Member.id field.
	 * @param member
	 * @return Member.sem
	 */
	public String getSem(int member)
	{
		return(member == 1 ? player_1.getSem() : player_2.getSem());
	}

	/**
	 * Returns the branch of the member based on Member.id field.
	 * @param member
	 * @return Member.branch
	 */
	public String getBranch(int member)
	{
		return(member == 1 ? player_1.getBranch() : player_2.getBranch());
	}

	/**
	 * Returns the email address of the member based on Member.id field.
	 * @param member
	 * @return Member.email
	 */
	public String getEmail(int member)
	{
		return(member == 1 ? player_1.getEmail() : player_2.getEmail());
	}

	/**
	 * Returns the mobile number of the member based on Member.id field.
	 * @param member
	 * @return Member.mobile
	 */
	public String getMobile(int member)
	{
		return(member == 1 ? player_1.getMobile() : player_2.getMobile());
	}

	/**
	 * Returns the id of the member based on Member.id field.
	 * @param member
	 * @return Member.branch
	 */
	public String getId(int member)
	{
		return(member == 1 ? player_1.getId() : player_2.getId());
	}
}
