package protocol;

public final class Protocol {
	
	//------- 로그인 관련 프로토콜 ------------------
	public static final String LOGIN = "100";
		public static final String LOGIN_CHK_OK = "101";
		public static final String LOGIN_CHK_NO = "102"; 
		public static final String LOGIN_CHK_OVERLAP = "103";
	
	
	//------- 회원 가입 창에서 사용 되는 프로토콜 -----------------
	public static final String ID_CHK = "110";
	public static final String USERJOIN = "111";
	public static final String ID_CHK_NO = "112";
	public static final String ID_CHK_OK = "113";
	
	//--------- 로그인 후 사용 하는 프로토콜 ---------------------
	public static final String NICK_EDIT = "200";
	public static final String MESSAGE_LIST = "210";
	public static final String SEARCH_FRIEND = "220"; //기본아이디검색
	public static final String SEARCH_FRIEND_AREA = "221"; //지역검색
	public static final String SEARCH_RESULT = "225";
	public static final String SEARCH_RESULT_AREA = "226";
	public static final String PREFERENCES = "230";
	public static final String PREFERENCES_OK = "235";
	public static final String LOGOUT = "240";
	public static final String EXIT = "250";
	
	//------------ 대화 친구추가 등 프로토콜 --------------
	public static final String SEND_MESSAGE = "300";
	public static final String ADD_FRIEND = "310";
	public static final String ADD_FRIEND_OK = "311";
	public static final String ADD_FRIEND_NO = "312";
	public static final String ADD_FRIEND_REQUEST = "315";
	public static final String ADD_FRIEND_RESPONSE = "316";
	public static final String START_CHAT = "330";
	public static final String CHATTING = "335";
	public static final String EXIT_CHAT = "336";
	public static final String INVITE_CHAT = "337";
}	
