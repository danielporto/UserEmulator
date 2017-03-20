import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
import com.meterware.httpunit.WebImage;
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
		String url = UserEmulator.baseUrl+UserEmulator.appname;
		//System.out.println("URL:" + url);
		time = System.currentTimeMillis();
		driver.navigate().to(url);
		if (UserEmulator.getResources)
			downloadAllPageResources(driver,UserEmulator.getImages,UserEmulator.getCss,UserEmulator.getJavascript);
		time = System.currentTimeMillis() - time;
		System.out.println("User " + user + " Test process finished Latency:" + time);
		//System.out.println(resp.getText());
		return time;
	}

	/**
	 * it consider that Im right after reset to initial state, at login page
	 * @param driver - client browser
	 * @return time - the time elapsed do fetch all elements of the new page (including redirects)
	 * @throws IOException 
	 * */
	public long doLogin(WebDriver driver) throws IOException {
		//		long time;
		//		String url=UserEmulator.baseUrl +UserEmulator.appname+ "/login/index?username="+user+"&password="+UserEmulator.userPassword;
		//		time=System.currentTimeMillis();
		//		driver.navigate().to(url);
		//		String str = driver.getPageSource();
		//		if (UserEmulator.DEBUG){
		//			System.out.println("User "+user+" Login process finished");
		//			//System.out.println(driver.getPageSource());
		//			//System.out.println("Time to process:"+(System.currentTimeMillis()-time));
		//		}
		//		return time;

		long time;
		driver.findElement(By.id("username")).clear();
		driver.findElement(By.id("username")).sendKeys(user);
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys(UserEmulator.userPassword);
		WebElement el = driver.findElement(By.id("login"));
		time=System.currentTimeMillis();
		el.click();
		if (UserEmulator.getResources)
			downloadAllPageResources(driver,UserEmulator.getImages,UserEmulator.getCss,UserEmulator.getJavascript);
		//System.out.println("User "+user+" Login process finished");
		return time;
	}

	/**
	 * @param driver - client browser
	 * @return time - the time elapsed do fetch all elements of the new page (including redirects)
	 * @throws IOException 
	 * */
	public long doLogout(WebDriver driver) throws IOException {
		long time = System.currentTimeMillis();
		driver.navigate().to(UserEmulator.baseUrl +UserEmulator.appname+ "/login/logout");
		if (UserEmulator.getResources)
			downloadAllPageResources(driver,UserEmulator.getImages,UserEmulator.getCss,UserEmulator.getJavascript);
		if (UserEmulator.DEBUG) {
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
	 * @throws IOException 
	 */
	public long doListMyUpdates(WebDriver driver) throws IOException {
		long time;// = System.currentTimeMillis();
		String url=	UserEmulator.baseUrl +UserEmulator.appname+ "/status/listUpdates";
		time = System.currentTimeMillis();
		driver.navigate().to(url);
		if (UserEmulator.getResources)
			downloadAllPageResources(driver,UserEmulator.getImages,UserEmulator.getCss,UserEmulator.getJavascript);
		if (UserEmulator.DEBUG){
			System.out.println("User "+user+" List updates process finished");
			System.out.println("Time to process:"+(System.currentTimeMillis()-time));	
		}
		return time;
	}

	/**
	 * @param driver - client browser
	 * @return time - the time elapsed do fetch all elements of the new page (including redirects)
	 * @throws IOException 
	 */
	public long doListAllUsers(WebDriver driver) throws IOException {
		//		long time;// = System.currentTimeMillis();
		//		String link;// = UserEmulator.baseUrl + UserEmulator.appname+"/user/list";
		//		//driver.get(link);
		//		link=UserEmulator.appname+"/user/list";
		//		String page=driver.getPageSource();
		//		WebElement el=driver.findElement (By.tagName(link));
		//		time = System.currentTimeMillis();
		//		el.click();
		//		if (UserEmulator.DEBUG) {
		//			System.out.println("User "+user+" List all users process finished "+link);
		//			//System.out.println(driver.getPageSource());	
		//			System.out.println("Time to process:"+(System.currentTimeMillis()-time));
		//		};
		//		return time;
		long time;
		time = System.currentTimeMillis();
		driver.navigate().to(UserEmulator.baseUrl + "/quoddy/user/list");
		if (UserEmulator.getResources)
			downloadAllPageResources(driver,UserEmulator.getImages,UserEmulator.getCss,UserEmulator.getJavascript);
		if (UserEmulator.DEBUG) 
			System.out.println("User "+user+" List all users process finished");
		return time;
	}

	/**
	 * @param driver - client browser
	 * @return time - the time elapsed do fetch all elements of the new page (including redirects)
	 * @throws IOException 
	 */
	public long doListAllMyFriends(WebDriver driver) throws IOException {
		//		long time;// = System.currentTimeMillis();
		//		//String link = UserEmulator.baseUrl +UserEmulator.appname+ "/user/listFriends";
		//		//driver.get(link);
		//		WebElement el = driver.findElement(By.partialLinkText("listFriends"));
		//		time = System.currentTimeMillis();
		//		el.click();
		//		if (UserEmulator.getImages)
		//			downloadImages(driver);
		//		if (UserEmulator.DEBUG){
		//			System.out.println("User "+user+" List all my friends process finished");
		//			//		System.out.println(driver.getPageSource());
		//			System.out.println("Time to process:"+(System.currentTimeMillis()-time));
		//		}
		//		return time;
		long time;
		time = System.currentTimeMillis();
		String url = UserEmulator.baseUrl +UserEmulator.appname+ "/user/listFriends";
		driver.navigate().to(url);
		if (UserEmulator.getResources)
			downloadAllPageResources(driver,UserEmulator.getImages,UserEmulator.getCss,UserEmulator.getJavascript);
		if (UserEmulator.DEBUG) 
			System.out.println("User "+user+" List all users process finished");
		return time;
	}

	/**
	 * @param driver - client browser
	 * @return time - the time elapsed do fetch all elements of the new page (including redirects)
	 * @throws IOException 
	 */
	public long doListAllMyFollowers(WebDriver driver) throws IOException {
		//		long time;// = System.currentTimeMillis();
		//		WebElement el = driver.findElement(By.partialLinkText("listFollowers"));
		//		String link= el.getText();//UserEmulator.baseUrl +UserEmulator.appname+ "/user/listFollowers";
		//
		//		time = System.currentTimeMillis();
		//		el.click();
		//		if (UserEmulator.getImages)
		//			downloadImages(driver);
		//		//driver.get(link);
		//		if (UserEmulator.DEBUG){
		//			System.out.println("User "+user+" List all my followers process finished:"+link);
		//			System.out.println("Time to process:"+(System.currentTimeMillis()-time));
		//		}
		//		return time;
		long time;
		time = System.currentTimeMillis();
		driver.navigate().to(UserEmulator.baseUrl +UserEmulator.appname+ "/user/listFollowers");
		if (UserEmulator.getResources)
			downloadAllPageResources(driver,UserEmulator.getImages,UserEmulator.getCss,UserEmulator.getJavascript);
		if (UserEmulator.DEBUG) 
			System.out.println("User "+user+" List all users process finished");
		return time;

	}

	/**
	 * @param driver - client browser
	 * @return time - the time elapsed do fetch all elements of the new page (including redirects)
	 * @throws IOException 
	 */
	public long doListUsersIFollow(WebDriver driver) throws IOException {
		//		long time;// = System.currentTimeMillis();
		//		String str=	UserEmulator.baseUrl +UserEmulator.appname+ "/user/listIFollow";
		//		WebElement el = driver.findElement(By.partialLinkText("listIFollow"));
		//		time = System.currentTimeMillis();
		//		el.click();
		//		if (UserEmulator.getImages)
		//			downloadImages(driver);
		//		//driver.get(str);
		//		if (UserEmulator.DEBUG){
		//			System.out.println("User "+user+" List people I follow process finished in "+str);
		//			System.out.println("Time to process:"+(System.currentTimeMillis()-time));
		//
		//		}
		//		return time;
		long time;
		time = System.currentTimeMillis();
		driver.navigate().to(UserEmulator.baseUrl +UserEmulator.appname+ "/user/listIFollow");
		if (UserEmulator.getResources)
			downloadAllPageResources(driver,UserEmulator.getImages,UserEmulator.getCss,UserEmulator.getJavascript);
		if (UserEmulator.DEBUG) 
			System.out.println("User "+user+" List all users process finished");
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
	//		String addUserLink = ""; // UserEmulator.baseUrl+
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
	//		if (UserEmulator.DEBUG)
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
	 * @throws IOException 
	 */
	public long doViewUsersProfile(WebDriver driver) throws ItemNotFoundException, IOException {
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
		driver.navigate().to(UserEmulator.baseUrl+ link);
		if (UserEmulator.getResources)
			downloadAllPageResources(driver,UserEmulator.getImages,UserEmulator.getCss,UserEmulator.getJavascript);
		if (UserEmulator.DEBUG){
			System.out.println("User " + user + " Let's go see a user. Go to "+ UserEmulator.baseUrl + link);
			System.out.println("Time to process:"+(System.currentTimeMillis()-time));
		}
		return time;
	}

	public long doUpdateStatus(WebDriver driver) throws IOException {
		long time;// = System.currentTimeMillis();
		// generate string update test here
		//driver.navigate().to(UserEmulator.baseUrl +UserEmulator.appname+ "/");
		//driver.findElement(By.id("statusText")).clear();
		driver.findElement(By.id("statusText")).sendKeys(getRandomStatusString());
		WebElement el = driver.findElement(By.id("updateStatusSubmit"));
		time = System.currentTimeMillis();
		el.click();
		// blocking
		if (UserEmulator.getResources)
			downloadAllPageResources(driver,UserEmulator.getImages,UserEmulator.getCss,UserEmulator.getJavascript);
		//System.out.println("User "+user+" update process finished:");
		return time;
	}
	//	public long doUpdateStatus(WebDriver driver) throws IOException,
	//	SAXException {
	//		long time = System.currentTimeMillis();
	//		String str = UserEmulator.baseUrl+ UserEmulator.appname	+ "/status/updateStatus?statusText=" + getRandomStatusString();
	//		// generate string update test here
	//		driver.navigate().to(str);
	//		System.out.println("User " + user + " update process finished");
	//		return time;
	//	}

	//	public long doFollowUser(WebDriver driver) throws ItemNotFoundException {
	//		long time;
	//		List<WebElement> e;
	//		boolean linkfound = false;
	//		String addUserLink = "";// UserEmulator.baseUrl+
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
	//		if (UserEmulator.DEBUG)
	//			System.out.println("User "+user+" Let's add a new user. Go to " + addUserLink);
	//
	//		time = System.currentTimeMillis();
	//		driver.navigate().to(addUserLink);
	//		return time;
	//	}

	public long doFollowUser(WebDriver driver) throws ItemNotFoundException, IOException {
		// UserEmulator.baseUrl+"/quoddy/user/addToFollow?userId=testuser7
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

		link = UserEmulator.baseUrl+e[0];// there's only one friend to add
		time = System.currentTimeMillis();
		driver.navigate().to(link);
		if (UserEmulator.getResources)
			downloadAllPageResources(driver,UserEmulator.getImages,UserEmulator.getCss,UserEmulator.getJavascript);
		if (UserEmulator.DEBUG){
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
	//		String addUserLink = ""; // UserEmulator.baseUrl+
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
	//		if (UserEmulator.DEBUG)
	//			System.out.println("User "+user+" Let's add a new user. Go to " + addUserLink);
	//		time = System.currentTimeMillis();
	//		driver.navigate().to(addUserLink);
	//		return time;
	//	}
	public long doAddNewFriend(WebDriver driver) throws ItemNotFoundException, IOException {
		// UserEmulator.baseUrl "/quoddy/user/addToFriends?userId=testuser7"
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

		link = UserEmulator.baseUrl+e[0];//only one user
		time = System.currentTimeMillis();
		driver.navigate().to(link);
		if (UserEmulator.getResources)
			downloadAllPageResources(driver,UserEmulator.getImages,UserEmulator.getCss,UserEmulator.getJavascript);
		if (UserEmulator.DEBUG){
			System.out.println("User "+user+" Let's add a new user. Go to " + link);
		}
		return time;
	}

	public long doViewPendingFriendRequest(WebDriver driver) throws IOException {
		long time;
		// go to pending requests page
		String link=UserEmulator.baseUrl+ UserEmulator.appname+"/user/listOpenFriendRequests";
		time = System.currentTimeMillis();
		driver.navigate().to(link);
		if (UserEmulator.getResources)
			downloadAllPageResources(driver,UserEmulator.getImages,UserEmulator.getCss,UserEmulator.getJavascript);
		if (UserEmulator.DEBUG) {
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
	//			// UserEmulator.baseUrl+
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
	//		if (UserEmulator.DEBUG)
	//			System.out.println("User "+user+" Let's confirm the user. Go to " + addUserLink);
	//
	//		// confirm the user
	//		time = System.currentTimeMillis();
	//		driver.navigate().to(addUserLink);
	//		return time;
	//	}

	public long doConfirmFriend(WebDriver driver) throws ItemNotFoundException, IOException {
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

		link = UserEmulator.baseUrl +  e[(number.nextInt(e.length))];
		// go on
		// confirm the user
		time = System.currentTimeMillis();
		driver.navigate().to(link);
		if (UserEmulator.getResources)
			downloadAllPageResources(driver,UserEmulator.getImages,UserEmulator.getCss,UserEmulator.getJavascript);
		if (UserEmulator.DEBUG){
			System.out.println("User "+user+" Let's confirm the user. Go to " + link);
			//System.out.println("Time to process:"+(System.currentTimeMillis()-time));
		}
		return time;
	}
	/*
	 * go to home page - it blocks until the page is fully loaded
	 */
	public long goHome(WebDriver driver) throws IOException {
		long time;// = System.currentTimeMillis();
		String link=UserEmulator.baseUrl +UserEmulator.appname+ "/";
		time = System.currentTimeMillis();
		driver.navigate().to(link);
		if (UserEmulator.getResources)
			downloadAllPageResources(driver,UserEmulator.getImages,UserEmulator.getCss,UserEmulator.getJavascript);
		if (UserEmulator.DEBUG){
			System.out.println("User "+user+" Let's confirm the user. Go to " + link);
		}
		return time;
	}

	public long endOfSession(WebDriver driver) throws IOException {
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


	public long resetToInitialPage(WebDriver driver){
		long time;
		String str=UserEmulator.baseUrl + "/quoddy/login/index";
		time=System.currentTimeMillis();
		driver.navigate().to(str);
		return time;
	}

	public int downloadImages(WebDriver driver) throws IOException {
		ArrayList<String> resources = new ArrayList<String>();
		String pagesource = driver.getPageSource();
		//System.out.println(pagesource);
		String strtmp;
		byte[] buffer = new byte[4096];
		URL imageURL = null;
		String str = "";
		InputStream imageStream;
		BufferedInputStream inImage;
		// Look for any image to download
		strtmp = "<img src=\"";
		int indeximages = pagesource.indexOf(strtmp);
		while (indeximages != -1) {
			int startQuote = indeximages + strtmp.length(); 
			int endQuote = pagesource.indexOf("\"", startQuote + 1);
			//System.out.println("download this:"	+ pagesource.substring(startQuote, endQuote));
			resources.add(pagesource.substring(startQuote, endQuote));
			indeximages = pagesource.indexOf(strtmp, endQuote);
		}
		strtmp = "<img style=\"float:left;\" src=\"";
		indeximages = pagesource.indexOf(strtmp);
		while (indeximages != -1) {
			int startQuote = indeximages + strtmp.length(); 
			int endQuote = pagesource.indexOf("\"", startQuote + 1);
			resources.add(pagesource.substring(startQuote, endQuote));
			indeximages = pagesource.indexOf(strtmp, endQuote);
		}

		//let's start downloading the page

		for (int i = 0; i < resources.size(); i++) {
			str = UserEmulator.baseUrl + resources.get(i);
			//System.out.println("Downloading item:"+str);
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
	public int downloadAllPageResources(WebDriver driver, boolean images, boolean css, boolean javascript) throws IOException {
		ArrayList<String> resources = new ArrayList<String>();
		String pagesource = driver.getPageSource();
		//System.out.println(pagesource);
		String strtmp;
		byte[] buffer = new byte[4096];
		URL resourceURL = null;
		String str = "";
		InputStream resourceStream;
		BufferedInputStream inResource;

		if(images){// Look for any image to download
			strtmp = "<img src=\"";
			int indexresource = pagesource.indexOf(strtmp);
			while (indexresource != -1) {
				int startQuote = indexresource + strtmp.length(); 
				int endQuote = pagesource.indexOf("\"", startQuote + 1);
				//System.out.println("download this:"	+ pagesource.substring(startQuote, endQuote));
				resources.add(pagesource.substring(startQuote, endQuote));
				indexresource = pagesource.indexOf(strtmp, endQuote);
			}
			strtmp = "<img style=\"float:left;\" src=\"";
			indexresource = pagesource.indexOf(strtmp);
			while (indexresource != -1) {
				int startQuote = indexresource + strtmp.length(); 
				int endQuote = pagesource.indexOf("\"", startQuote + 1);
				resources.add(pagesource.substring(startQuote, endQuote));
				indexresource = pagesource.indexOf(strtmp, endQuote);
			}
		}
		if(css){// Look for any css to download
			strtmp = "<link rel=\"stylesheet\" type=\"text/css\" href=\"";
			int indeximages = pagesource.indexOf(strtmp);
			while (indeximages != -1) {
				int startQuote = indeximages + strtmp.length(); 
				int endQuote = pagesource.indexOf("\"", startQuote + 1);
				//System.out.println("download this:"	+ pagesource.substring(startQuote, endQuote));
				resources.add(pagesource.substring(startQuote, endQuote));
				indeximages = pagesource.indexOf(strtmp, endQuote);
			}
		}//download css
		if(javascript){// Look for any javascript to download
			strtmp = "<script type=\"text/javascript\" src=\"";
			int indeximages = pagesource.indexOf(strtmp);
			while (indeximages != -1) {
				int startQuote = indeximages + strtmp.length(); 
				int endQuote = pagesource.indexOf("\"", startQuote + 1);
				//System.out.println("download this:"	+ pagesource.substring(startQuote, endQuote));
				resources.add(pagesource.substring(startQuote, endQuote));
				indeximages = pagesource.indexOf(strtmp, endQuote);
			}
		}//download javascript


		//let's start downloading the page resources
		for (int i = 0; i < resources.size(); i++) {
			str = UserEmulator.baseUrl + resources.get(i);
			//System.out.println("Downloading item:"+str);
			try {
				resourceURL = new URL(str);
				resourceStream = resourceURL.openStream();
				inResource = new BufferedInputStream(resourceStream, 4096);
				while (inResource.read(buffer, 0, buffer.length) != -1)
					; // Just download, skip data
				inResource.close();
				resourceStream.close();
			} catch (IOException ioe) {
				System.err.println("Error while downloading resource " + resourceURL+ " (" + ioe.getMessage() + ")");
			}
		}
		return 0;
	}

	public String getRandomStatusString() {
		return "Hello quoddy this is my test!!!!";
	}
}
