import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Transitions {
	/*
	 * 
	 * TODO: use direct urls instead of find elements in the page
	 */
	public static long doLogin(WebDriver driver) {
		long time = goHome(driver);
		driver.navigate()
				.to(QuoddyUserEmulator.baseUrl + "/quoddy/login/index");
		driver.findElement(By.id("username")).clear();
		driver.findElement(By.id("username")).sendKeys("testuser1");
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("secret");
		// non blocking
		driver.findElement(By.id("login")).click();
		// blocking
		// goHome(driver);
		System.out.println("Login process finished:" + driver.getTitle());
		System.out.println("source code:" + driver.getPageSource());
		return time;
	}

	/*
	 * flush session
	 */
	public static long doLogout(WebDriver driver) {
		long time = System.currentTimeMillis();
		driver.navigate().to(
				QuoddyUserEmulator.baseUrl + "/quoddy/login/logout");
		System.out.println("Logout process finished:" + driver.getTitle());
		System.out.println("source code:" + driver.getPageSource());
		return time;
	}

	/*
     * 
     * */
	public static long doCloseBrowser(WebDriver driver) {
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
	public static long doListMyUpdates(WebDriver driver) {
		long time = System.currentTimeMillis();
		driver.navigate().to(QuoddyUserEmulator.baseUrl + "/quoddy/");
		driver.navigate().to(
				QuoddyUserEmulator.baseUrl + "/quoddy/status/listUpdates");
		System.out
				.println("List updates process finished:" + driver.getTitle());
		System.out.println("source code:" + driver.getPageSource());
		return time;
	}

	public static long doListAllUsers(WebDriver driver) {
		long time = System.currentTimeMillis();
		driver.navigate().to(QuoddyUserEmulator.baseUrl + "/quoddy/user/list");
		System.out.println("Listogin process finished:" + driver.getTitle());
		System.out.println("source code:" + driver.getPageSource());
		return time;
	}

	public static long doListAllMyFriends(WebDriver driver) {
		long time = System.currentTimeMillis();
		driver.navigate().to(
				QuoddyUserEmulator.baseUrl + "/quoddy/user/listFriends");
		System.out.println("List all my friends process finished:"
				+ driver.getTitle());
		System.out.println("source code:" + driver.getPageSource());
		return time;
	}

	public static long doListAllMyFollowers(WebDriver driver) {
		long time = System.currentTimeMillis();
		driver.navigate().to(
				QuoddyUserEmulator.baseUrl + "/quoddy/user/listFollowers");
		System.out.println("List all my followers process finished:"
				+ driver.getTitle());
		System.out.println("source code:" + driver.getPageSource());
		return time;
	}

	public static long doListUsersIFollow(WebDriver driver) {
		long time = System.currentTimeMillis();
		driver.navigate().to(
				QuoddyUserEmulator.baseUrl + "/quoddy/user/listIFollow");
		System.out.println("List people I follow process finished:"
				+ driver.getTitle());
		System.out.println("source code:" + driver.getPageSource());
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
	public static long doViewUsersProfile(WebDriver driver) {
		long time = System.currentTimeMillis();
		// from followers list
		driver.navigate().to(
				QuoddyUserEmulator.baseUrl
						+ "/quoddy/user/viewUser?userId=testuser4");
		System.out.println("View Users Profile process finished:"
				+ driver.getTitle());
		System.out.println("source code:" + driver.getPageSource());

		return time;
	}

	public static long doUpdateStatus(WebDriver driver) {
		long time = System.currentTimeMillis();
		// generate string update test here
		driver.navigate().to(QuoddyUserEmulator.baseUrl + "/quoddy/");
		driver.findElement(By.id("statusText")).clear();
		driver.findElement(By.id("statusText")).sendKeys("Hello quoddy this is my test!!!!");
		driver.findElement(By.id("updateStatusSubmit")).click();
		// blocking
		// goHome(driver);
		System.out.println("update process finished:" + driver.getTitle());
		System.out.println("source code:" + driver.getPageSource());
		return time;
	}

	public static long doFollowUser(WebDriver driver) {
		long time = System.currentTimeMillis();
		// read current page and get one user
		driver.navigate().to(
				QuoddyUserEmulator.baseUrl
						+ "/quoddy/user/addToFollow?userId=testuser7");
		System.out.println("do follow process finished:" + driver.getTitle());
		System.out.println("source code:" + driver.getPageSource());
		return time;
	}

	public static long doAddNewFriend(WebDriver driver) {
		long time = System.currentTimeMillis();
		// read current page and get one user
		driver.navigate().to(
				QuoddyUserEmulator.baseUrl
						+ "/quoddy/user/addToFriends?userId=testuser7");
		System.out.println("do add friend process finished:"
				+ driver.getTitle());
		System.out.println("source code:" + driver.getPageSource());
		return time;
	}

	public static long doConfirmFriend(WebDriver driver) {
		long time = System.currentTimeMillis();
		driver.navigate().to(
				QuoddyUserEmulator.baseUrl
						+ "/quoddy/user/listOpenFriendRequests");
		// read current page and get one user
		driver.navigate().to(
				QuoddyUserEmulator.baseUrl
						+ "/quoddy/user/confirmFriend?confirmId=testuser7");
		System.out.println("do confirm process finished:" + driver.getTitle());
		System.out.println("source code:" + driver.getPageSource());
		return time;
	}

	/*
	 * go to home page - it blocks until the page is fully loaded
	 */
	public static long goHome(WebDriver driver) {
		long time = System.currentTimeMillis();
		driver.navigate().to(QuoddyUserEmulator.baseUrl + "/quoddy/");
		return time;
	}
	public static long endOfSession(WebDriver driver){
		return System.currentTimeMillis();
	}
}
