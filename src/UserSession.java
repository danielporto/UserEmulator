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

	protected TransitionTable transitiontable = null; // transitiontable table user
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
	private Stats stats; // Statistics to collect
	// errors, time, ...
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
		userId=id; //thread id not exactly the user in the session
		stats = statistics;
		//driver = new HtmlUnitDriver();
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		
		
		password=QuoddyUserEmulator.userPassword;
		state = new Transitions("INITIAL"); //current username
		
		transitiontable = new TransitionTable(QuoddyUserEmulator.numberOfStates,	QuoddyUserEmulator.numberOfStates+1, 
				statistics,QuoddyUserEmulator.useThinkTime);
		
		if (!transitiontable.ReadExcelTextFile(QuoddyUserEmulator.transitionTable)){
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


	public long goNextState(int state){
	  switch (state){
	  case Transitions.goHome : return this.state.goHome(driver);
	  case Transitions.doLogin: return this.state.doLogin(driver);
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

		while (QuoddyUserEmulator.totalSimulationTime > System.currentTimeMillis()) {
			username=QuoddyUserEmulator.userPrefix+rand.nextInt(QuoddyUserEmulator.numberOfExistingUsers+1);
			nbOfTransitions = QuoddyUserEmulator.maxTransitionsPerSession;//reset user budget
			state.user=username;
			
			System.out.println("Thread " + this.getName()+ ": Starting a new user session for " + username);
			startSession = System.currentTimeMillis();
			// Start from Home Page -- this transitions are not count in the evaluation!!!!
			goNextState(Transitions.goHome);
			transitiontable.resetToInitialState();
			next = transitiontable.getCurrentState();
			/*
			 * keep issuing requests to website while the user have interactions in the budget, 
			 * the user didnt decide to go out from the website (or logout)
			 * */
			while (nbOfTransitions > 0 || !transitiontable.isEndOfSession()) {
				try{
				time = goNextState(next); //get the time before the interaction
				}catch(Exception e){
					e.printStackTrace();
					System.out.println("Something bad happend reset to initial state");
					stats.incrementError(next);
					break;
				}
				stats.updateTime(next, System.currentTimeMillis() - time);
				next = transitiontable.nextState();
				System.out.println("moving to state:"+next);
				nbOfTransitions--;
			}
			
			if ((transitiontable.isEndOfSession()) || (nbOfTransitions == 0)) {
					System.out.println("Thread " + this.getName()+ ": Session of " + username+ " successfully ended");
					endSession = System.currentTimeMillis();
					long sessionTime = endSession - startSession;
					stats.addSessionTime(sessionTime);
			} else {
					System.out.println("Thread " + this.getName()+ ": Session of " + username + " aborted");
			}
		}//big while
	}//method

}
