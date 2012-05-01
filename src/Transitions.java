import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.xml.sax.SAXException;

import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebLink;
import com.meterware.httpunit.WebResponse;

public class Transitions {
	String user;

	public static final int goHome=0;
	public static final int doLogin=1;
	public static final int doUpdateStatus=2;
	public static final int doListMyUpdates=3;
	public static final int doListAllUsers=4;
	public static final int doViewUsersProfile=5;
	public static final int doAddNewFriend=6;
	public static final int doViewPendingFriendRequest=7;
	public static final int doConfirmFriend=8;
	public static final int doListAllMyFriends=9;
	public static final int doFollowUser=10;
	public static final int doListUsersIFollow=11;
	public static final int doListAllMyFollowers=12;
	public static final int endOfSession=13; //must be the last one aways!!!
	public static final int test=14; 
	public static final String [] stateToString = {"goHome", "doLogin","doUpdateStatus", "doListMyUpdates",
												"doListAllUsers","doViewUsersProfile", "doAddNewFriend", "doViewPendingFriendRequest",
												"doConfirmFriend","doListAllMyFriends","doFollowUser",
												"doListUsersIFollow","doListAllMyFollowers","endOfSession","test"};

	Random number;

	public Transitions(String user){
		this.user=user;//only used to avoid get transitions with same user
		number = new Random();
	}
/*
 * it returns the time elapsed do fetch all elements of the new page (including redirects
 * */
	public long doTest(WebDriver driver) throws IOException, SAXException {
		long time;
		String url = QuoddyUserEmulator.baseUrl+QuoddyUserEmulator.appname;
		//System.out.println("URL:" + url);
		time = System.currentTimeMillis();
		driver.navigate().to(url);
		time = System.currentTimeMillis() - time;
		System.out.println("User " + user + " Test process finished Latency:" + time);
		//System.out.println(resp.getText());
		return time;
	}
	
	/**
	 * it consider that Im right after reset to initial state, at login page
	 * @param driver - client browser
	 * @return time - the time elapsed do fetch all elements of the new page (including redirects)
	 * */
	public long doLogin(WebDriver driver) {
//		long time;
//		String url=QuoddyUserEmulator.baseUrl +QuoddyUserEmulator.appname+ "/login/index?username="+user+"&password="+QuoddyUserEmulator.userPassword;
//		time=System.currentTimeMillis();
//		driver.navigate().to(url);
//		String str = driver.getPageSource();
//		if (QuoddyUserEmulator.DEBUG){
//			System.out.println("User "+user+" Login process finished");
//			//System.out.println(driver.getPageSource());
//			//System.out.println("Time to process:"+(System.currentTimeMillis()-time));
//		}
//		return time;
		
		  long time;
          driver.findElement(By.id("username")).clear();
          driver.findElement(By.id("username")).sendKeys(user);
          driver.findElement(By.id("password")).clear();
          driver.findElement(By.id("password")).sendKeys(QuoddyUserEmulator.userPassword);
          WebElement el = driver.findElement(By.id("login"));
          time=System.currentTimeMillis();
          el.click();
          System.out.println("User "+user+" Login process finished");
          return time;
	}

	/**
	 * @param driver - client browser
	 * @return time - the time elapsed do fetch all elements of the new page (including redirects)
	 * */
	public long doLogout(WebDriver driver) {
		long time = System.currentTimeMillis();
		driver.navigate().to(QuoddyUserEmulator.baseUrl +QuoddyUserEmulator.appname+ "/login/logout");
		if (QuoddyUserEmulator.DEBUG) {
			System.out.println("User "+user+" Logout process finished");
			System.out.println("Time to process:"+(System.currentTimeMillis()-time));
		}
		return time;
	}

	/**
	 * @param driver - client browser
	 * @return time - the time elapsed do fetch all elements of the new page (including redirects)
	 * */
	public long doCloseBrowser(WebDriver driver) {
		long time = System.currentTimeMillis();
		StringBuffer verificationErrors = new StringBuffer();
		driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			System.out.println(verificationErrorString);
		}
		return time;
	}

	/**
	 * doListMyUpdates List all my updates
	 * 
	 * @param driver - client browser
	 * @return time - the time elapsed do fetch all elements of the new page (including redirects)
	 */
	public long doListMyUpdates(WebDriver driver) {
		long time;// = System.currentTimeMillis();
		String url=	QuoddyUserEmulator.baseUrl +QuoddyUserEmulator.appname+ "/status/listUpdates";
		time = System.currentTimeMillis();
		driver.navigate().to(url);
		if (QuoddyUserEmulator.DEBUG){
			System.out.println("User "+user+" List updates process finished");
			System.out.println("Time to process:"+(System.currentTimeMillis()-time));	
		}
		return time;
	}

	/**
	 * @param driver - client browser
	 * @return time - the time elapsed do fetch all elements of the new page (including redirects)
	 */
	public long doListAllUsers(WebDriver driver) {
//		long time;// = System.currentTimeMillis();
//		String link;// = QuoddyUserEmulator.baseUrl + QuoddyUserEmulator.appname+"/user/list";
//		//driver.get(link);
//		link=QuoddyUserEmulator.appname+"/user/list";
//		String page=driver.getPageSource();
//		WebElement el=driver.findElement (By.tagName(link));
//		time = System.currentTimeMillis();
//		el.click();
//		if (QuoddyUserEmulator.DEBUG) {
//			System.out.println("User "+user+" List all users process finished "+link);
//			//System.out.println(driver.getPageSource());	
//			System.out.println("Time to process:"+(System.currentTimeMillis()-time));
//		};
//		return time;
		long time;
		time = System.currentTimeMillis();
        driver.navigate().to(QuoddyUserEmulator.baseUrl + "/quoddy/user/list");
        if (QuoddyUserEmulator.DEBUG) 
        	System.out.println("User "+user+" List all users process finished");
        return time;
	}

	/**
	 * @param driver - client browser
	 * @return time - the time elapsed do fetch all elements of the new page (including redirects)
	 */
	public long doListAllMyFriends(WebDriver driver) {
		long time;// = System.currentTimeMillis();
		//String link = QuoddyUserEmulator.baseUrl +QuoddyUserEmulator.appname+ "/user/listFriends";
		//driver.get(link);
		WebElement el = driver.findElement(By.partialLinkText("listFriends"));
		time = System.currentTimeMillis();
		el.click();
		if (QuoddyUserEmulator.DEBUG){
			System.out.println("User "+user+" List all my friends process finished");
			//		System.out.println(driver.getPageSource());
			System.out.println("Time to process:"+(System.currentTimeMillis()-time));
		}
		return time;
	}

	/**
	 * @param driver - client browser
	 * @return time - the time elapsed do fetch all elements of the new page (including redirects)
	 */
	public long doListAllMyFollowers(WebDriver driver) {
		long time;// = System.currentTimeMillis();
		WebElement el = driver.findElement(By.partialLinkText("listFollowers"));
		String link= el.getText();//QuoddyUserEmulator.baseUrl +QuoddyUserEmulator.appname+ "/user/listFollowers";
		
		time = System.currentTimeMillis();
		el.click();
		//driver.get(link);
		if (QuoddyUserEmulator.DEBUG){
			System.out.println("User "+user+" List all my followers process finished:"+link);
			System.out.println("Time to process:"+(System.currentTimeMillis()-time));
		}
		return time;
	}

	/**
	 * @param driver - client browser
	 * @return time - the time elapsed do fetch all elements of the new page (including redirects)
	 */
	public long doListUsersIFollow(WebDriver driver) {
		long time;// = System.currentTimeMillis();
		String str=	QuoddyUserEmulator.baseUrl +QuoddyUserEmulator.appname+ "/user/listIFollow";
		WebElement el = driver.findElement(By.partialLinkText("listIFollow"));
		time = System.currentTimeMillis();
		el.click();
		//driver.get(str);
		if (QuoddyUserEmulator.DEBUG){
			System.out.println("User "+user+" List people I follow process finished in "+str);
			System.out.println("Time to process:"+(System.currentTimeMillis()-time));

		}
		return time;
	}

	// todo
	public static long doViewMyProfile(WebDriver driver) {
		long time = System.currentTimeMillis();
		return time;
	}

	/*
	 * show a user profile example link:
	 * http://swsao5001.mpi-sws.org:8080/user/viewUser?userId=testuser7
	 * TODO we need to get list of users
	 */
	//	public long doViewUsersProfile(WebDriver driver) throws ItemNotFoundException {
	//		long time;
	//		List<WebElement> e;
	//		boolean linkfound = false;
	//		String addUserLink = ""; // QuoddyUserEmulator.baseUrl+
	//									// "/quoddy/user/viewUser?userId=testuser4"
	//		int index=0;
	//
	//		// read current page
	//		//System.out.println(driver.getPageSource());
	//		e = driver.findElements(By.tagName("a"));
	//		time=System.currentTimeMillis();
	//		System.out.println(driver.getPageSource());
	//		time=System.currentTimeMillis()-time;
	//		System.out.println("time to find "+time);
	//		Collections.shuffle(e);// get a random element
	//		for (WebElement el : e) {
	//			addUserLink = el.getAttribute("href");
	//			if (addUserLink.contains("viewUser")) { // do not contain myself: &&
	//													// !addUserLink.contains(driver....)
	//				linkfound = true;
	//				break; // get the first user (it is randomized)
	//			}
	//		}
	//		if (!linkfound) {
	//			System.out
	//					.println("User "+user+" There's no user to view details -- attention the home page has temporary users (hope it doesnot affect)");
	////			for (WebElement el : e) {
	////				System.out.println("User "+user+" Element: " + el.getAttribute("href"));
	////			}
	//			//System.exit(0);
	//			throw new ItemNotFoundException();
	//			
	//		}
	//
	//		// from followers list
	//		if (QuoddyUserEmulator.DEBUG)
	//			System.out.println("User "+user+" Let's go see a user. Go to " + addUserLink);
	//		time = System.currentTimeMillis();
	//		driver.navigate().to(addUserLink);
	//		time=System.currentTimeMillis()-time;
	//		System.out.println("time to download "+time);
	//		return time;
	//	}
	/**
	 * @param driver - client browser
	 * @return time - the time elapsed do fetch all elements of the new page (including redirects)
	 */
	public long doViewUsersProfile(WebDriver driver) throws ItemNotFoundException {
		long time;
		String page = driver.getPageSource(); // read current page
		String[] e;
		String link;

//		time=System.currentTimeMillis();
		e = getMatchingLinks(page,"viewUser");
		//time=System.currentTimeMillis()-time;
		//System.out.println("time to find "+time);
		if (e.length <= 0) {
			System.out
			.println("User "
					+ user
					+ " There's no user to view details -- attention the home page has temporary users (hope it doesnot affect)");
			throw new ItemNotFoundException();
		}

		link = e[(number.nextInt(e.length))];

		// from followers list

		time = System.currentTimeMillis();
		driver.navigate().to(QuoddyUserEmulator.baseUrl+ link);
		if (QuoddyUserEmulator.DEBUG){
			System.out.println("User " + user + " Let's go see a user. Go to "+ QuoddyUserEmulator.baseUrl + link);
			System.out.println("Time to process:"+(System.currentTimeMillis()-time));
		}
		return time;
	}

		public long doUpdateStatus(WebDriver driver) {
			long time;// = System.currentTimeMillis();
			// generate string update test here
			//driver.navigate().to(QuoddyUserEmulator.baseUrl +QuoddyUserEmulator.appname+ "/");
			//driver.findElement(By.id("statusText")).clear();
			driver.findElement(By.id("statusText")).sendKeys(getRandomStatusString());
			WebElement el = driver.findElement(By.id("updateStatusSubmit"));
			time = System.currentTimeMillis();
			el.click();
			// blocking
			System.out.println("User "+user+" update process finished:"+el.getText() );
			return time;
		}
//	public long doUpdateStatus(WebDriver driver) throws IOException,
//	SAXException {
//		long time = System.currentTimeMillis();
//		String str = QuoddyUserEmulator.baseUrl+ QuoddyUserEmulator.appname	+ "/status/updateStatus?statusText=" + getRandomStatusString();
//		// generate string update test here
//		driver.navigate().to(str);
//		System.out.println("User " + user + " update process finished");
//		return time;
//	}

	//	public long doFollowUser(WebDriver driver) throws ItemNotFoundException {
	//		long time;
	//		List<WebElement> e;
	//		boolean linkfound = false;
	//		String addUserLink = "";// QuoddyUserEmulator.baseUrl+
	//								// "/quoddy/user/addToFollow?userId=testuser7
	//
	//		// read current page
	//		e = driver.findElements(By.tagName("a"));
	//		for (WebElement el : e) {
	//			addUserLink = el.getAttribute("href");
	//			if (addUserLink.contains("addToFollow")) {
	//				linkfound = true;
	//				break; // there's only one friend to add
	//			}
	//		}
	//		if (!linkfound) {
	//			throw new ItemNotFoundException();
	////			System.out.println("User "+user+" Link to follow a friend was not found!!!");
	////			for (WebElement el : e) {
	////				System.out.println("User "+user+" Element: " + el.getAttribute("href"));
	////			}
	////			System.exit(0);
	//		}
	//
	//		if (QuoddyUserEmulator.DEBUG)
	//			System.out.println("User "+user+" Let's add a new user. Go to " + addUserLink);
	//
	//		time = System.currentTimeMillis();
	//		driver.navigate().to(addUserLink);
	//		return time;
	//	}

	public long doFollowUser(WebDriver driver) throws ItemNotFoundException {
		// QuoddyUserEmulator.baseUrl+"/quoddy/user/addToFollow?userId=testuser7
		long time;
		String page = driver.getPageSource(); // read current page
		String[] e;
		String link;

		//time=System.currentTimeMillis();
		e = getMatchingLinks(page,"addToFollow");
		//time=System.currentTimeMillis()-time;
		//System.out.println("time to find "+time);
		if (e.length <= 0) {
			System.out
			.println("User "
					+ user
					+ " There's no user to view details -- attention the home page has temporary users (hope it doesnot affect)");
			throw new ItemNotFoundException();
		}

		link = QuoddyUserEmulator.baseUrl+e[0];// there's only one friend to add
		time = System.currentTimeMillis();
		driver.navigate().to(link);
		if (QuoddyUserEmulator.DEBUG){
			System.out.println("User "+user+" Let's add a new user. Go to "+link);
		}
		return time;
	}

	/*
	 * review done
	 */
	//	public long doAddNewFriend(WebDriver driver) throws ItemNotFoundException {
	//		long time;
	//		List<WebElement> e;
	//		boolean linkfound = false;
	//		String addUserLink = ""; // QuoddyUserEmulator.baseUrl+
	//									// "/quoddy/user/addToFriends?userId=testuser7"
	//
	//		// read current page and get one user
	//		e = driver.findElements(By.tagName("a"));
	//		for (WebElement el : e) {
	//			addUserLink = el.getAttribute("href");
	//			if (addUserLink.contains("addToFriends")) {
	//				linkfound = true;
	//				break; // there's only one friend to add
	//			}
	//		}
	//		if (!linkfound) {
	//			throw new ItemNotFoundException();
	////			System.out.println("User "+user+" Link to add a friend was not found!!!");
	////			for (WebElement el : e) {
	////				System.out.println("User "+user+" Element: " + el.getAttribute("href"));
	////			}
	////			System.exit(0);
	//		}
	//
	//		if (QuoddyUserEmulator.DEBUG)
	//			System.out.println("User "+user+" Let's add a new user. Go to " + addUserLink);
	//		time = System.currentTimeMillis();
	//		driver.navigate().to(addUserLink);
	//		return time;
	//	}
	public long doAddNewFriend(WebDriver driver) throws ItemNotFoundException {
		// QuoddyUserEmulator.baseUrl "/quoddy/user/addToFriends?userId=testuser7"
		long time;
		String page = driver.getPageSource(); // read current page
		String[] e;
		String link;

		//time=System.currentTimeMillis();
		e = getMatchingLinks(page,"addToFriends");
		//time=System.currentTimeMillis()-time;
		//System.out.println("time to find "+time);
		if (e.length <= 0) {
			System.out
			.println("User "
					+ user
					+ " There's no user to addToFriends -- attention the home page has temporary users (hope it doesnot affect)");
			throw new ItemNotFoundException();
		}

		link = QuoddyUserEmulator.baseUrl+e[0];//only one user
		time = System.currentTimeMillis();
		driver.navigate().to(link);
		if (QuoddyUserEmulator.DEBUG){
			System.out.println("User "+user+" Let's add a new user. Go to " + link);
		}
		return time;
	}

	public long doViewPendingFriendRequest(WebDriver driver) {
		long time;
		// go to pending requests page
		String link=QuoddyUserEmulator.baseUrl+ QuoddyUserEmulator.appname+"/user/listOpenFriendRequests";
		time = System.currentTimeMillis();
		driver.navigate().to(link);
		if (QuoddyUserEmulator.DEBUG) {
			System.out.println("User "+user+" Let's see pending requests. Go to " +link);
			
		}
		return time;
	}

	//	public long doConfirmFriend(WebDriver driver) throws ItemNotFoundException {
	//		long time;
	//		List<WebElement> e;
	//		boolean linkfound = false;
	//		String addUserLink = "";
	//
	//		// from the listOpenFriendRequests page
	//		// read the element from the page
	//		e = driver.findElements(By.tagName("a"));
	//		for (WebElement el : e) {
	//			addUserLink = el.getAttribute("href");
	//			// QuoddyUserEmulator.baseUrl+
	//			// "/quoddy/user/confirmFriend?confirmId=testuser7"
	//			if (addUserLink.contains("confirmFriend")) {
	//				linkfound = true;
	//				break; // there's only one friend to add
	//			}
	//		}
	//		if (!linkfound) {
	//			throw new ItemNotFoundException();
	////			System.out
	////					.println("User "+user+" There was no pending request to confirm!!! going back to home");
	////			return goHome(driver);
	//		}
	//		// go on
	//
	//		if (QuoddyUserEmulator.DEBUG)
	//			System.out.println("User "+user+" Let's confirm the user. Go to " + addUserLink);
	//
	//		// confirm the user
	//		time = System.currentTimeMillis();
	//		driver.navigate().to(addUserLink);
	//		return time;
	//	}
	
	public long doConfirmFriend(WebDriver driver) throws ItemNotFoundException {
		long time;
		String page = driver.getPageSource(); // read current page
		String[] e;
		String link;

		// from the listOpenFriendRequests page
		// read the element from the page
		//time=System.currentTimeMillis();
		e = getMatchingLinks(page,"confirmFriend");
		//time=System.currentTimeMillis()-time;
		//System.out.println("time to find "+time);
		if (e.length <= 0) {
			System.out
			.println("User "
					+ user
					+ " There's no user to confirmFriend -- attention the home page has temporary users (hope it doesnot affect)");
			throw new ItemNotFoundException();
		}

		link = e[(number.nextInt(e.length))];
		// go on
		// confirm the user
		time = System.currentTimeMillis();
		driver.navigate().to(link);
		if (QuoddyUserEmulator.DEBUG){
			System.out.println("User "+user+" Let's confirm the user. Go to " + link);
			//System.out.println("Time to process:"+(System.currentTimeMillis()-time));
		}
		return time;
	}
	/*
	 * go to home page - it blocks until the page is fully loaded
	 */
	public long goHome(WebDriver driver) {
		long time;// = System.currentTimeMillis();
		String link=QuoddyUserEmulator.baseUrl +QuoddyUserEmulator.appname+ "/";
		time = System.currentTimeMillis();
		driver.navigate().to(link);
		if (QuoddyUserEmulator.DEBUG){
			System.out.println("User "+user+" Let's confirm the user. Go to " + link);
		}
		return time;
	}

	public long endOfSession(WebDriver driver) {
		return doLogout(driver);

	}
	/*
	 * tested with viewUser
	 * */
	public String [] getMatchingLinks(String HTMLpage,String match){
		ArrayList<String> resources = new ArrayList<String>();
		String strtmp,link, array[];
		strtmp = "<a href=\"";//<a href="/quoddy/user/viewUser?userId=user9738">
		int index = HTMLpage.indexOf(strtmp);

		int startQuote,endQuote; 
		while (index != -1) {
			startQuote = index + strtmp.length(); 
			endQuote = HTMLpage.indexOf("\"", startQuote + 1);
			link=HTMLpage.substring(startQuote, endQuote);
			if(link.contains(match))
				resources.add(link);
			index = HTMLpage.indexOf(strtmp, endQuote);
		}
		array= new String[resources.size()];
		resources.toArray(array);
		return  array;
	}
	public String getRandomStatusString() {
		return "Helloquoddythisismytest";
	}
	
	public long resetToInitialPage(WebDriver driver){
		long time;
		String str=QuoddyUserEmulator.baseUrl + "/quoddy/login/index";
		 time=System.currentTimeMillis();
        driver.navigate().to(str);
        return time;
	}
}
