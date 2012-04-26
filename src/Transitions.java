import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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
public static final int endOfSession=13;

	
	Random number;
	
	public Transitions(String user){
		this.user=user;//only used to avoid get transitions with same user
	}
	
	public long doLogin(WebDriver driver) {
		long time = goHome(driver);
		driver.navigate()
				.to(QuoddyUserEmulator.baseUrl + "/quoddy/login/index");
		driver.findElement(By.id("username")).clear();
		driver.findElement(By.id("username")).sendKeys(user);
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys(QuoddyUserEmulator.userPassword);
		// non blocking
		driver.findElement(By.id("login")).click();
		// blocking
		// goHome(driver);
		System.out.println("User "+user+" Login process finished");
		return time;
	}

	/*
	 * flush session
	 */
	public long doLogout(WebDriver driver) {
		long time = System.currentTimeMillis();
		driver.navigate().to(QuoddyUserEmulator.baseUrl + "/quoddy/login/logout");
		System.out.println("User "+user+" Logout process finished");
		return time;
	}

	/*
     * 
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

	/*
	 * doListMyUpdates List all my updates
	 */
	public long doListMyUpdates(WebDriver driver) {
		long time = System.currentTimeMillis();
		driver.navigate().to(
				QuoddyUserEmulator.baseUrl + "/quoddy/status/listUpdates");
		if (QuoddyUserEmulator.DEBUG)
			System.out.println("User "+user+" List updates process finished");
		return time;
	}

	public long doListAllUsers(WebDriver driver) {
		long time = System.currentTimeMillis();
		driver.navigate().to(QuoddyUserEmulator.baseUrl + "/quoddy/user/list");
		if (QuoddyUserEmulator.DEBUG) System.out.println("User "+user+" List all users process finished");
		return time;
	}

	public long doListAllMyFriends(WebDriver driver) {
		long time = System.currentTimeMillis();
		driver.navigate().to(
				QuoddyUserEmulator.baseUrl + "/quoddy/user/listFriends");
		if (QuoddyUserEmulator.DEBUG)
			System.out.println("User "+user+" List all my friends process finished");
		return time;
	}

	public long doListAllMyFollowers(WebDriver driver) {
		long time = System.currentTimeMillis();
		driver.navigate().to(
				QuoddyUserEmulator.baseUrl + "/quoddy/user/listFollowers");
		if (QuoddyUserEmulator.DEBUG)
			System.out.println("User "+user+" List all my followers process finished");
		return time;
	}

	public long doListUsersIFollow(WebDriver driver) {
		long time = System.currentTimeMillis();
		driver.navigate().to(
				QuoddyUserEmulator.baseUrl + "/quoddy/user/listIFollow");
		if (QuoddyUserEmulator.DEBUG)
			System.out.println("User "+user+" List people I follow process finished");
		return time;
	}

	// todo
	public static long doViewMyProfile(WebDriver driver) {
		long time = System.currentTimeMillis();
		return time;
	}

	/*
	 * show a user profile example link:
	 * http://swsao5001.mpi-sws.org:8080/quoddy/user/viewUser?userId=testuser7
	 * TODO we need to get list of users
	 */
	public long doViewUsersProfile(WebDriver driver) {
		long time;
		List<WebElement> e;
		boolean linkfound = false;
		String addUserLink = ""; // QuoddyUserEmulator.baseUrl+
									// "/quoddy/user/viewUser?userId=testuser4"

		// read current page
		e = driver.findElements(By.tagName("a"));
		Collections.shuffle(e);// get a random element
		for (WebElement el : e) {
			addUserLink = el.getAttribute("href");
			if (addUserLink.contains("viewUser")) { // do not contain myself: &&
													// !addUserLink.contains(driver....)
				linkfound = true;
				break; // get the first user (it is randomized)
			}
		}
		if (!linkfound) {
			System.out
					.println("User "+user+" There's no user to view details -- attention the home page has temporary users (hope it doesnot affect)");
			for (WebElement el : e) {
				System.out.println("User "+user+" Element: " + el.getAttribute("href"));
			}
			System.exit(0);
		}

		// from followers list
		if (QuoddyUserEmulator.DEBUG)
			System.out.println("User "+user+" Let's go see a user. Go to " + addUserLink);
		time = System.currentTimeMillis();
		driver.navigate().to(addUserLink);
		return time;
	}

	public long doUpdateStatus(WebDriver driver) {
		long time = System.currentTimeMillis();
		// generate string update test here
		driver.navigate().to(QuoddyUserEmulator.baseUrl + "/quoddy/");
		//driver.findElement(By.id("statusText")).clear();
		driver.findElement(By.id("statusText")).sendKeys("Hello quoddy this is my test!!!!");
		driver.findElement(By.id("updateStatusSubmit")).click();
		// blocking
		// goHome(driver);
		System.out.println("User "+user+" update process finished");
		return time;
	}

	public long doFollowUser(WebDriver driver) {
		long time;
		List<WebElement> e;
		boolean linkfound = false;
		String addUserLink = "";// QuoddyUserEmulator.baseUrl+
								// "/quoddy/user/addToFollow?userId=testuser7

		// read current page
		e = driver.findElements(By.tagName("a"));
		for (WebElement el : e) {
			addUserLink = el.getAttribute("href");
			if (addUserLink.contains("addToFollow")) {
				linkfound = true;
				break; // there's only one friend to add
			}
		}
		if (!linkfound) {
			System.out.println("User "+user+" Link to follow a friend was not found!!!");
			for (WebElement el : e) {
				System.out.println("User "+user+" Element: " + el.getAttribute("href"));
			}
			System.exit(0);
		}

		if (QuoddyUserEmulator.DEBUG)
			System.out.println("User "+user+" Let's add a new user. Go to " + addUserLink);

		time = System.currentTimeMillis();
		driver.navigate().to(addUserLink);
		return time;
	}

	/*
	 * review done
	 */
	public long doAddNewFriend(WebDriver driver) {
		long time;
		List<WebElement> e;
		boolean linkfound = false;
		String addUserLink = ""; // QuoddyUserEmulator.baseUrl+
									// "/quoddy/user/addToFriends?userId=testuser7"

		// read current page and get one user
		e = driver.findElements(By.tagName("a"));
		for (WebElement el : e) {
			addUserLink = el.getAttribute("href");
			if (addUserLink.contains("addToFriends")) {
				linkfound = true;
				break; // there's only one friend to add
			}
		}
		if (!linkfound) {
			System.out.println("User "+user+" Link to add a friend was not found!!!");
			for (WebElement el : e) {
				System.out.println("User "+user+" Element: " + el.getAttribute("href"));
			}
			System.exit(0);
		}

		if (QuoddyUserEmulator.DEBUG)
			System.out.println("User "+user+" Let's add a new user. Go to " + addUserLink);
		time = System.currentTimeMillis();
		driver.navigate().to(addUserLink);
		return time;
	}

	public long doViewPendingFriendRequest(WebDriver driver) {
		long time;
		// go to pending requests page
		time = System.currentTimeMillis();
		driver.navigate().to(
				QuoddyUserEmulator.baseUrl
						+ "/quoddy/user/listOpenFriendRequests");
		return time;
	}

	public long doConfirmFriend(WebDriver driver) {
		long time;
		List<WebElement> e;
		boolean linkfound = false;
		String addUserLink = "";

		// from the listOpenFriendRequests page
		// read the element from the page
		e = driver.findElements(By.tagName("a"));
		for (WebElement el : e) {
			addUserLink = el.getAttribute("href");
			// QuoddyUserEmulator.baseUrl+
			// "/quoddy/user/confirmFriend?confirmId=testuser7"
			if (addUserLink.contains("confirmFriend")) {
				linkfound = true;
				break; // there's only one friend to add
			}
		}
		if (!linkfound) {
			System.out
					.println("User "+user+" There was no pending request to confirm!!! going back to home");
			return goHome(driver);
		}
		// go on

		if (QuoddyUserEmulator.DEBUG)
			System.out.println("User "+user+" Let's confirm the user. Go to " + addUserLink);

		// confirm the user
		time = System.currentTimeMillis();
		driver.navigate().to(addUserLink);
		return time;
	}

	/*
	 * go to home page - it blocks until the page is fully loaded
	 */
	public long goHome(WebDriver driver) {
		long time = System.currentTimeMillis();
		driver.navigate().to(QuoddyUserEmulator.baseUrl + "/quoddy/");
		return time;
	}

	public long endOfSession(WebDriver driver) {
		return doLogout(driver);

	}
}
