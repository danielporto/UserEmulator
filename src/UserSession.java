/*
 * RUBiS
 * Copyright (C) 2002, 2003, 2004 French National Institute For Research In Computer
 * Science And Control (INRIA).
 * Contact: jmob@objectweb.org
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or any later
 * version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 *
 * Initial developer(s): Emmanuel Cecchet, Julie Marguerite
 * Contributor(s): Jeremy Philippe
 */

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import net.sourceforge.htmlunit.corejs.javascript.ast.DoLoop;

import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;

import org.openqa.selenium.firefox.FirefoxDriver;
/**
 * RUBiS user session emulator. This class plays a random user session emulating
 * a Web browser.
 * 
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet </a> and <a
 *         href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite </a>
 * @version 1.0
 */
public class UserSession extends Thread {

	protected TransitionTable transition = null; // transition table user
	// for this session
	private String lastHTMLReply = null; // last HTML reply
	// received from
	private Random rand = new Random(); // random number
	// generator
	private int userId; // User id for the
	// current session
	private String username = null; // User name for the
	// current session
	private String password = null; // User password for the
	// current session
	private URL lastURL = null; // Last accessed URL
	private int lastItemId = -1; // This is to deal with
	// back because the
	// itemId cannot be
	// retrieved from the
	// current page
	private int lastUserId = -1; // This is to deal with
	// back because the
	// itemId cannot be
	// retrieved from the
	// current page
	private Stats stats; // Statistics to collect
	// errors, time, ...
	private int debugLevel = 0; // 0 = no debug message,
	private WebDriver driver; // webbrowser
	Transitions state;

	/**
	 * Creates a new <code>UserSession</code> instance.
	 * 
	 * @param threadId
	 *            a thread identifier
	 * @param URLGen
	 *            the URLGenerator to use
	 * @param RUBiS
	 *            rubis.properties
	 * @param statistics
	 *            where to collect statistics
	 */
	public UserSession(String thread_label,int id, Stats statistics) {
		super(thread_label+id);
		stats = statistics;
		debugLevel = 0; // rubis.getMonitoringDebug(); // debugging level: 0 =
						// no debug
		// message, 1 = just error
		// messages, 2 = error
		// messages+HTML pages, 3 =
		// everything!

		//driver = new HtmlUnitDriver();
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		
		username=QuoddyUserEmulator.userPrefix+id;
		password=QuoddyUserEmulator.userPassword;
		
		state = new Transitions(username);

		transition = new TransitionTable(QuoddyUserEmulator.numberOfStates,
				QuoddyUserEmulator.numberOfStates, statistics,QuoddyUserEmulator.useThinkTime);
		if (!transition.ReadExcelTextFile(QuoddyUserEmulator.transitionTable)){
			System.out.println("Failed to read transition table from xls file");
			Runtime.getRuntime().exit(1);
			}
	}

	/**
	 * Internal method that returns the min between last_index and x if x is not
	 * equal to -1.
	 * 
	 * @param last_index
	 *            last_index value
	 * @param x
	 *            value to compare with last_index
	 * @return x if (x <last_index and x!=-1) else last_index
	 */
	private int isMin(int last_index, int x) {
		if (x == -1)
			return last_index;
		if (last_index <= x)
			return last_index;
		else
			return x;
	}

	/**
	 * Computes the URL to be accessed according to the given state. If any
	 * parameter are needed, they are computed from last HTML reply.
	 * 
	 * @param state
	 *            current state
	 * @return URL corresponding to the state
	 */
	// public URL computeURLFromState(int state)
	// {
	// // if (lastHTMLReply != null)
	// // {
	// // if (lastHTMLReply.indexOf("Sorry") != -1) // Nothing matched the
	// request,
	// // // we have to go back
	// // state = transition.backToPreviousState();
	// // }
	//
	// switch (state)
	// {
	// case -1 :
	// // An error occured, reset to home page
	// transition.resetToInitialState();
	// case 0 :
	// // Home Page
	// return urlGen.homePage();
	// case 1 :
	// // Register User Page
	// return urlGen.register();
	// case 2 :
	// // Register the user in the database
	// { // Choose a random nb over already known attributed ids
	// int i = rubis.getNbOfUsers() + rand.nextInt(1000000) + 1;
	// String firstname = "Great" + i;
	// String lastname = "User" + i;
	// String nickname = "user" + i;
	// String email = firstname + "." + lastname + "@rubis.com";
	// String password = "password" + i;
	// String regionName = (String) rubis.getRegions().elementAt(
	// i % rubis.getNbOfRegions());
	//
	// return urlGen.registerUser(firstname, lastname, nickname, email,
	// password, regionName);
	// }
	// case 3 :
	// // Browse Page
	// return urlGen.browse();
	// case 4 :
	// // Browse Categories
	// return urlGen.browseCategories();
	// case 5 :
	// // Browse items in a category
	// { // We randomly pickup a category from the generated data instead of
	// // from the HTML page (faster)
	// int categoryId = rand.nextInt(rubis.getNbOfCategories());
	// String categoryName = (String) rubis.getCategories().elementAt(
	// categoryId);
	// return urlGen.browseItemsInCategory(categoryId, categoryName,
	// extractPageFromHTML(), rubis.getNbOfItemsPerPage());
	// }
	// case 6 :
	// // Browse Regions
	// return urlGen.browseRegions();
	// case 7 :
	// // Browse categories in a region
	// String regionName = (String) rubis.getRegions().elementAt(
	// rand.nextInt(rubis.getNbOfRegions()));
	// return urlGen.browseCategoriesInRegion(regionName);
	// case 8 :
	// // Browse items in a region for a given category
	// { // We randomly pickup a category and a region from the generated data
	// // instead of from the HTML page (faster)
	// int categoryId = rand.nextInt(rubis.getNbOfCategories());
	// String categoryName = (String) rubis.getCategories().elementAt(
	// categoryId);
	// int regionId = rand.nextInt(rubis.getNbOfRegions());
	// return urlGen.browseItemsInRegion(categoryId, categoryName, regionId,
	// extractPageFromHTML(), rubis.getNbOfItemsPerPage());
	// }
	// case 9 :
	// // View an item
	// {
	// int itemId = extractItemIdFromHTML();
	// if (itemId == -1)
	// return computeURLFromState(transition.backToPreviousState()); // Nothing
	// // then go
	// // back
	// else
	// return urlGen.viewItem(itemId);
	// }
	// case 10 :
	// // View user information
	// {
	// int userId = extractIntFromHTML("userId=");
	// if (userId == -1)
	// return computeURLFromState(transition.backToPreviousState()); // Nothing
	// // then go
	// // back
	// else
	// return urlGen.viewUserInformation(userId);
	// }
	// case 11 :
	// // View item bid history
	// return urlGen.viewBidHistory(extractItemIdFromHTML());
	// case 12 :
	// // Buy Now Authentication
	// return urlGen.buyNowAuth(extractItemIdFromHTML());
	// case 13 :
	// // Buy Now confirmation page
	// return urlGen.buyNow(extractItemIdFromHTML(), username, password);
	// case 14 :
	// // Store Buy Now in the database
	// {
	// int maxQty = extractIntFromHTML("name=maxQty value=");
	// if (maxQty < 1)
	// maxQty = 1;
	// int qty = rand.nextInt(maxQty) + 1;
	// return urlGen.storeBuyNow(extractItemIdFromHTML(), userId, qty,
	// maxQty);
	// }
	// case 15 :
	// // Bid Authentication
	// return urlGen.putBidAuth(extractItemIdFromHTML());
	// case 16 :
	// // Bid confirmation page
	// {
	// int itemId = extractItemIdFromHTML();
	// if (itemId == -1)
	// return computeURLFromState(transition.backToPreviousState()); // Nothing
	// // then go
	// // back
	// else
	// return urlGen.putBid(itemId, username, password);
	// }
	// case 17 :
	// // Store Bid in the database
	// { /*
	// * Generate randomly the bid, maxBid and quantity values, all other
	// * values are retrieved from the last HTML reply
	// */
	// int maxQty = extractIntFromHTML("name=maxQty value=");
	// if (maxQty < 1)
	// maxQty = 1;
	// int qty = rand.nextInt(maxQty) + 1;
	// float minBid = extractFloatFromHTML("name=minBid value=");
	// float addBid = rand.nextInt(10) + 1;
	// float bid = minBid + addBid;
	// float maxBid = minBid + addBid * 2;
	// return urlGen.storeBid(extractItemIdFromHTML(), userId, minBid, bid,
	// maxBid, qty, maxQty);
	// }
	// case 18 :
	// // Comment Authentication page
	// return urlGen.putCommentAuth(extractItemIdFromHTML(),
	// extractIntFromHTML("to="));
	// case 19 :
	// // Comment confirmation page
	// return urlGen.putComment(extractItemIdFromHTML(),
	// extractIntFromHTML("to="), username, password);
	// case 20 :
	// // Store Comment in the database
	// { // Generate a random comment and rating
	// String[] staticComment = {
	// "This is a very bad comment. Stay away from this seller !!<br>",
	// "This is a comment below average. I don't recommend this user !!<br>",
	// "This is a neutral comment. It is neither a good or a bad seller !!<br>",
	// "This is a comment above average. You can trust this seller even if it is not the best deal !!<br>",
	// "This is an excellent comment. You can make really great deals with this seller !!<br>"};
	// int[] staticCommentLength = {staticComment[0].length(),
	// staticComment[1].length(), staticComment[2].length(),
	// staticComment[3].length(), staticComment[4].length()};
	// int[] ratingValue = {-5, -3, 0, 3, 5};
	// int rating;
	// String comment;
	//
	// rating = rand.nextInt(5);
	// int commentLength = rand.nextInt(rubis.getCommentMaxLength()) + 1;
	// comment = "";
	// while (staticCommentLength[rating] < commentLength)
	// {
	// comment = comment + staticComment[rating];
	// commentLength -= staticCommentLength[rating];
	// }
	// comment = staticComment[rating].substring(0, commentLength);
	//
	// return urlGen.storeComment(extractItemIdFromHTML(),
	// extractIntFromHTML("name=to value="), userId,
	// ratingValue[rating], comment);
	// }
	// case 21 :
	// // Sell page
	// return urlGen.sell();
	// case 22 :
	// // Select a category to sell item
	// return urlGen.selectCategoryToSellItem(username, password);
	// case 23 :
	// {
	// int categoryId = rand.nextInt(rubis.getNbOfCategories());
	// return urlGen.sellItemForm(categoryId, userId);
	// }
	// case 24 :
	// // Store item in the database
	// {
	// String name;
	// String description;
	// float initialPrice;
	// float reservePrice;
	// float buyNow;
	// int duration;
	// int quantity;
	// int categoryId;
	// String staticDescription =
	// "This incredible item is exactly what you need !<br>It has a lot of very nice features including "
	// +
	// "a coffee option.<br>It comes with a free license for the free RUBiS software, that's really cool. But RUBiS even if it "
	// +
	// "is free, is <B>(C) Rice University/INRIA 2001</B>. It is really hard to write an interesting generic description for "
	// +
	// "automatically generated items, but who will really read this <br>You can also check some cool software available on "
	// +
	// "http://sci-serv.inrialpes.fr. There is a very cool DSM system called SciFS for SCI clusters, but you will need some "
	// +
	// "SCI adapters to be able to run it ! Else you can still try CART, the amazing 'Cluster Administration and Reservation "
	// +
	// "Tool'. All those software are open source, so don't hesitate ! If you have a SCI Cluster you can also try the Whoops! "
	// +
	// "clustered web server. Actually Whoops! stands for something ! Yes, it is a Web cache with tcp Handoff, On the fly "
	// +
	// "cOmpression, parallel Pull-based lru for Sci clusters !! Ok, that was a lot of fun but now it is starting to be quite late "
	// +
	// "and I'll have to go to bed very soon, so I think if you need more information, just go on <h1>http://sci-serv.inrialpes.fr</h1> "
	// +
	// "or you can even try http://www.cs.rice.edu and try to find where Emmanuel Cecchet or Julie Marguerite are and you will "
	// + "maybe get fresh news about all that !!<br>";
	// int staticDescriptionLength = staticDescription.length();
	// int totalItems = rubis.getTotalActiveItems()
	// + rubis.getNbOfOldItems();
	// int i = totalItems + rand.nextInt(1000000) + 1;
	//
	// name = "RUBiS automatically generated item #" + i;
	// int descriptionLength = rand
	// .nextInt(rubis.getItemDescriptionLength()) + 1;
	// description = "";
	// while (staticDescriptionLength < descriptionLength)
	// {
	// description = description + staticDescription;
	// descriptionLength -= staticDescriptionLength;
	// }
	// description = staticDescription.substring(0, descriptionLength);
	// initialPrice = rand.nextInt(5000) + 1;
	// if (rand.nextInt(totalItems) < rubis.getPercentReservePrice()
	// * totalItems / 100)
	// reservePrice = rand.nextInt(1000) + initialPrice;
	// else
	// reservePrice = 0;
	// if (rand.nextInt(totalItems) < rubis.getPercentBuyNow() * totalItems
	// / 100)
	// buyNow = rand.nextInt(1000) + initialPrice + reservePrice;
	// else
	// buyNow = 0;
	// duration = rand.nextInt(7) + 1;
	// if (rand.nextInt(totalItems) < rubis.getPercentUniqueItems()
	// * totalItems / 100)
	// quantity = 1;
	// else
	// quantity = rand.nextInt(rubis.getMaxItemQty()) + 1;
	// categoryId = rand.nextInt(rubis.getNbOfCategories());
	// return urlGen.registerItem(name, description, initialPrice,
	// reservePrice, buyNow, duration, quantity, userId, categoryId);
	// }
	// case 25 :
	// // About Me authentification
	// return urlGen.aboutMe();
	// case 26 :
	// // About Me information page
	// return urlGen.aboutMe(username, password);
	// default :
	// if (debugLevel > 0)
	// System.err.println("Thread " + this.getName()
	// + ": This state is not supported (" + state + ")<br>");
	// return null;
	// }
	// }

	public long goNextState(int state){
	  switch (state){
	  case Transitions.goHome : return this.state.goHome(driver);
	  case Transitions.doLogin: return this.state.doLogin(driver);
	  case Transitions.doLogout: return this.state.doLogout(driver);
	  case Transitions.doUpdateStatus: return this.state.doUpdateStatus(driver);
	  case Transitions.doListMyUpdates: return this.state.doListMyUpdates(driver);
	  case Transitions.doListAllUsers: return this.state.doListAllUsers(driver);
	  case Transitions.doViewUsersProfile: return this.state.doViewUsersProfile(driver);
	  case Transitions.doAddNewFriend: return this.state.doAddNewFriend(driver);
	  case Transitions.doViewPendingFriendRequest: return this.state.doViewPendingFriendRequest(driver);
	  case Transitions.doConfirmFriend: return this.state.doConfirmFriend(driver);
	  case Transitions.doListAllMyFriends: return this.state.doListAllMyFriends(driver);
	  case Transitions.doFollowUser: return this.state.doFollowUser(driver);
	  case Transitions.doListUsersIFollow: return this.state.doListUsersIFollow(driver);
	  case Transitions.doListAllMyFollowers: return this.state.doListAllMyFollowers(driver);
	  case Transitions.endOfSession: return this.state.endOfSession(driver);
	  }
	  return System.currentTimeMillis();
 }

	/**
	 * Emulate a user session using the current transition table.
	 */
	public void run() {
		int nbOfTransitions = 0;
		int next = 0;
		long time = 0;
		long startSession = 0;
		long endSession = 0;

		while (QuoddyUserEmulator.endOfSession > System.currentTimeMillis()) {
			// Select a random user for this session
			userId = rand.nextInt(QuoddyUserEmulator.numberOfClients);
			username = "testuser" + (userId);
			password = "secret";
			nbOfTransitions = QuoddyUserEmulator.maxTransitionsPerSession;
			if (debugLevel > 2)
				System.out.println("Thread " + this.getName()
						+ ": Starting a new user session for " + username
						+ " ...<br>");
			startSession = System.currentTimeMillis();
			// Start from Home Page
			transition.resetToInitialState();
			next = transition.getCurrentState();

			while (QuoddyUserEmulator.endOfSession > System.currentTimeMillis()
					&& !transition.isEndOfSession() && (nbOfTransitions > 0)) {
				// Compute next step and call HTTP server (also measure time
				// spend in
				// server call)

				try{
				time = goNextState(next);
				if(next==Transitions.doLogout){
					System.out.println("Switching from user "+state.user+" to "+);
				}
				}catch(Exception e){
					e.printStackTrace();
					System.out.println("Perhaps we can handle this error by just discarting the interaction and resetting the state");
					System.exit(0);
				}
				stats.updateTime(next, System.currentTimeMillis() - time);
				next = transition.nextState();
				
				nbOfTransitions--;
			}
			
			
			
			if ((transition.isEndOfSession()) || (nbOfTransitions == 0)) {
				if (debugLevel > 2)
					System.out.println("Thread " + this.getName()
							+ ": Session of " + username
							+ " successfully ended<br>");
				endSession = System.currentTimeMillis();
				long sessionTime = endSession - startSession;
				stats.addSessionTime(sessionTime);
			} else {
				if (debugLevel > 2)
					System.out.println("Thread " + this.getName()
							+ ": Session of " + username + " aborted<br>");
			}
		}
	}

}
