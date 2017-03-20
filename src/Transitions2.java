import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.xml.sax.SAXException;

import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebImage;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebResponse;

public class Transitions2 {
	String user;

	public static final int goHome = 0;
	public static final int doLogin = 1;
	public static final int doUpdateStatus = 2;
	public static final int doListMyUpdates = 3;
	public static final int doListAllUsers = 4;
	public static final int doViewUsersProfile = 5;
	public static final int doAddNewFriend = 6;
	public static final int doViewPendingFriendRequest = 7;
	public static final int doConfirmFriend = 8;
	public static final int doListAllMyFriends = 9;
	public static final int doFollowUser = 10;
	public static final int doListUsersIFollow = 11;
	public static final int doListAllMyFollowers = 12;
	public static final int endOfSession = 13; // must be the last one aways!!!
	public static final int test = 14;
	public static final String [] stateToString = {"goHome", "doLogin","doUpdateStatus", "doListMyUpdates",
		"doListAllUsers","doViewUsersProfile", "doAddNewFriend", "doViewPendingFriendRequest",
		"doConfirmFriend","doListAllMyFriends","doFollowUser",
		"doListUsersIFollow","doListAllMyFollowers","endOfSession","test"};
	Random number;

	public Transitions2(String user) {
		this.user = user;// only used to avoid get transitions with same user
		number = new Random();
	}

	public long doTest(WebConversation driver) throws IOException, SAXException {
		long time;
		String url = UserEmulator.baseUrl+UserEmulator.appname;
		//System.out.println("URL:" + url);
		time = System.currentTimeMillis();
		WebResponse resp = driver.getResponse(url);
		time = System.currentTimeMillis() - time;
		if (UserEmulator.getImages)	downloadImages(driver);
		System.out.println("User " + user + " Test process finished Latency:" + time);
		//System.out.println(resp.getText());
		return time;
	}

	public long doLogin(WebConversation driver) throws IOException,
			SAXException {
		long time = System.currentTimeMillis();// goHome(driver);
		String url = UserEmulator.baseUrl + UserEmulator.appname
				+ "/login/login?username=" + user + "&password="
				+ UserEmulator.userPassword;
		System.out.println("URL:" + url);
		WebResponse resp = driver.getResponse(url);
		if (UserEmulator.getImages)
			downloadImages(driver);
		System.out.println("User " + user + " Login process finished:" + url);

		// System.out.println(resp.getText());
		return time;
	}

	/*
	 * flush session
	 */
	public long doLogout(WebConversation driver) throws IOException,
			SAXException {
		long time = System.currentTimeMillis();
		WebResponse resp = driver.getResponse(UserEmulator.baseUrl
				+ UserEmulator.appname + "/login/logout");
		if (UserEmulator.getImages)
			downloadImages(driver);
		System.out.println("User " + user + " Logout process finished");
		return time;
	}

	/*
	 * TODO
	 */
	public long doCloseBrowser(WebConversation driver) {
		long time = System.currentTimeMillis();
		// StringBuffer verificationErrors = new StringBuffer();
		// driver.quit();
		// String verificationErrorString = verificationErrors.toString();
		// if (!"".equals(verificationErrorString)) {
		// System.out.println(verificationErrorString);
		// }
		return time;
	}

	/*
	 * doListMyUpdates List all my updates
	 */
	public long doListMyUpdates(WebConversation driver) throws IOException,
			SAXException {
		long time = System.currentTimeMillis();
		WebResponse resp = driver.getResponse(UserEmulator.baseUrl
				+ UserEmulator.appname + "/status/listUpdates");
		if (UserEmulator.getImages)
			downloadImages(driver);
		if (UserEmulator.DEBUG)
			System.out.println("User " + user
					+ " List updates process finished");
		return time;
	}

	public long doListAllUsers(WebConversation driver) throws IOException,
			SAXException {
		long time = System.currentTimeMillis();
		String url = UserEmulator.baseUrl + UserEmulator.appname
				+ "/user/list";
		WebResponse resp = driver.getResponse(url);
		if (UserEmulator.getImages)
			downloadImages(driver);
		if (UserEmulator.DEBUG)
			System.out.println("User " + user
					+ " List all users process finished url:" + url);
		// System.out.println(resp.getText());
		return time;
	}

	public long doListAllMyFriends(WebConversation driver) throws IOException,
			SAXException {
		long time = System.currentTimeMillis();
		WebResponse resp = driver.getResponse(UserEmulator.baseUrl
				+ UserEmulator.appname + "/user/listFriends");
		if (UserEmulator.getImages)
			downloadImages(driver);
		if (UserEmulator.DEBUG)
			System.out.println("User " + user
					+ " List all my friends process finished");
		return time;
	}

	public long doListAllMyFollowers(WebConversation driver)
			throws IOException, SAXException {
		long time = System.currentTimeMillis();
		WebResponse resp = driver.getResponse(UserEmulator.baseUrl
				+ UserEmulator.appname + "/user/listFollowers");
		if (UserEmulator.getImages)
			downloadImages(driver);
		if (UserEmulator.DEBUG)
			System.out.println("User " + user
					+ " List all my followers process finished");
		return time;
	}

	public long doListUsersIFollow(WebConversation driver) throws IOException,
			SAXException {
		long time = System.currentTimeMillis();
		WebResponse resp = driver.getResponse(UserEmulator.baseUrl
				+ UserEmulator.appname + "/user/listIFollow");
		if (UserEmulator.getImages)
			downloadImages(driver);
		if (UserEmulator.DEBUG)
			System.out.println("User " + user
					+ " List people I follow process finished");
		return time;
	}

	// todo
	public static long doViewMyProfile(WebConversation driver) {
		long time = System.currentTimeMillis();
		return time;
	}

	/*
	 * show a user profile example link:
	 * http://swsao5001.mpi-sws.org:8080/user/viewUser?userId=testuser7 TODO we
	 * need to get list of users
	 */
	public long doViewUsersProfile(WebConversation driver)
			throws ItemNotFoundException, SAXException, IOException {
		long time;
		WebResponse page = driver.getCurrentPage(); // read current page
		WebLink[] e;
		WebLink link;

		e = page.getMatchingLinks(WebLink.MATCH_URL_STRING, "viewUser");
		if (e.length <= 0) {
			System.out
					.println("User "
							+ user
							+ " There's no user to view details -- attention the home page has temporary users (hope it doesnot affect)");
			throw new ItemNotFoundException();
		}

		link = e[(number.nextInt(e.length))];

		// from followers list
		if (UserEmulator.DEBUG)
			System.out.println("User " + user + " Let's go see a user. Go to "
					+ UserEmulator.baseUrl + link.getURLString());
		time = System.currentTimeMillis();
		WebResponse resp = driver.getResponse(UserEmulator.baseUrl
				+ link.getURLString());
		if (UserEmulator.getImages)
			downloadImages(driver);
		// System.out.println(driver.getCurrentPage().getText());
		return time;
	}

	public long doUpdateStatus(WebConversation driver) throws IOException,
			SAXException {
		long time = System.currentTimeMillis();
		// generate string update test here
		WebResponse resp = driver.getResponse(UserEmulator.baseUrl
				+ UserEmulator.appname
				+ "/status/updateStatus?statusText=" + getRandomStatusString());
		if (UserEmulator.getImages)
			downloadImages(driver);
		System.out.println("User " + user + " update process finished");
		return time;
	}

	public long doFollowUser(WebConversation driver)
			throws ItemNotFoundException, SAXException, IOException {
		long time;
		WebResponse page = driver.getCurrentPage(); // read current page
		WebLink[] e;
		WebLink link;// UserEmulator.baseUrl+
						// "/quoddy/user/addToFollow?userId=testuser7

		// read current page
		e = page.getMatchingLinks(WebLink.MATCH_URL_STRING, "addToFollow");
		if (e.length <= 0) {
			System.out.println("User " + user
					+ " Link to follow a friend was not found!!!");
			throw new ItemNotFoundException();
		}

		link = e[(number.nextInt(e.length))];

		if (UserEmulator.DEBUG)
			System.out.println("User " + user + " Let's go see a user. Go to "
					+ UserEmulator.baseUrl + link.getURLString());

		time = System.currentTimeMillis();
		WebResponse resp = driver.getResponse(UserEmulator.baseUrl
				+ link.getURLString());
		if (UserEmulator.getImages)
			downloadImages(driver);
		return time;
	}

	/*
	 * review done
	 */
	public long doAddNewFriend(WebConversation driver)
			throws ItemNotFoundException, SAXException, IOException {
		long time;
		WebResponse page = driver.getCurrentPage(); // read current page
		WebLink[] e;
		WebLink link; // UserEmulator.baseUrl+
						// "/quoddy/user/addToFriends?userId=testuser7"

		e = page.getMatchingLinks(WebLink.MATCH_URL_STRING, "addToFriends");
		if (e.length <= 0) {
			System.out.println("User " + user
					+ " Link to add a friend was not found!!!");
			throw new ItemNotFoundException();
		}
		link = e[(number.nextInt(e.length))];
		if (UserEmulator.DEBUG)
			System.out.println("User " + user + " Let's go see a user. Go to "
					+ UserEmulator.baseUrl + link.getURLString());
		time = System.currentTimeMillis();
		WebResponse resp = driver.getResponse(UserEmulator.baseUrl
				+ link.getURLString());
		if (UserEmulator.getImages)
			downloadImages(driver);
		return time;
	}

	public long doViewPendingFriendRequest(WebConversation driver)
			throws IOException, SAXException {
		long time;
		// go to pending requests page
		time = System.currentTimeMillis();
		WebResponse resp = driver.getResponse(UserEmulator.baseUrl
				+ UserEmulator.appname + "/user/listOpenFriendRequests");
		if (UserEmulator.getImages)
			downloadImages(driver);
		return time;
	}

	public long doConfirmFriend(WebConversation driver)
			throws ItemNotFoundException, IOException, SAXException {
		long time;
		WebResponse page = driver.getCurrentPage(); // read current page
		WebLink[] e;
		WebLink link;

		// from the listOpenFriendRequests page
		// read the element from the page

		e = page.getMatchingLinks(WebLink.MATCH_URL_STRING, "confirmFriend");
		if (e.length <= 0) {
			System.out
					.println("User "
							+ user
							+ " There was no pending request to confirm!!! going back to home");
			throw new ItemNotFoundException();
		}

		link = e[(number.nextInt(e.length))];
		// from followers list
		if (UserEmulator.DEBUG)
			System.out.println("User " + user + " Let's go see a user. Go to "
					+ UserEmulator.baseUrl + link.getURLString());
		time = System.currentTimeMillis();
		WebResponse resp = driver.getResponse(UserEmulator.baseUrl
				+ link.getURLString());
		if (UserEmulator.getImages)
			downloadImages(driver);
		return time;
	}

	/*
	 * go to home page - it blocks until the page is fully loaded
	 */
	public long goHome(WebConversation driver) throws IOException, SAXException {
		long time = System.currentTimeMillis();
		WebResponse resp = driver.getResponse(UserEmulator.baseUrl
				+ UserEmulator.appname + "/home/index");
		if (UserEmulator.getImages)
			downloadImages(driver);
		// System.out.println(resp.getText());
		return time;
	}

	public long endOfSession(WebConversation driver) throws IOException,
			SAXException {
		return doLogout(driver);

	}

	public int downloadImages(WebConversation driver) throws SAXException,
			IOException {
		WebImage[] images = driver.getCurrentPage().getImages();

		byte[] buffer = new byte[4096];
		URL imageURL = null;
		String str = "";

		if (images == null)
			return 0;
		InputStream imageStream;
		BufferedInputStream inImage;
		for (int i = 0; i < images.length; i++) {
			str = UserEmulator.baseUrl + images[i].getSource();
			// System.out.println("Downloading item:"+str);
			try {
				imageURL = new URL(str);
				imageStream = imageURL.openStream();
				inImage = new BufferedInputStream(imageStream, 4096);
				while (inImage.read(buffer, 0, buffer.length) != -1)
					; // Just download, skip data
				inImage.close();
				imageStream.close();
			} catch (IOException ioe) {
				System.err.println("Error while downloading image " + imageURL
						+ " (" + ioe.getMessage() + ")<br>");
			}
		}
		return 0;
	}
	public String getRandomStatusString() {
		return "Hello quoddy this is my test!!!!";
	}
}
