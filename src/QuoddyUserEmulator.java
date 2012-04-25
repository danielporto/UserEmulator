import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;

import javax.xml.stream.events.StartDocument;

public class QuoddyUserEmulator {
	public static String baseUrl = new String(
			"http://swsao5001.mpi-sws.org:8080");
	public static final int numberOfClients = 2;
	public static final int numberOfExistingUsers = 13;
	public static final int numberOfStates = 14;
	public static final int warmUpTime = 6000;
	public static final int simulationTime = 60000;
	public static final int tearDownTime = 6000;
	public static final String transitionTable = "default_transitions.csv";
	public static final int maxTransitionsPerSession = 1000;
	public static long endOfSession = 0;
	public static final boolean useThinkTime=false; 
	public static final boolean DEBUG=true;

	public static void main(String[] args) {
		test();
		//run();
	}
	public static void run() {
		endOfSession = System.currentTimeMillis() + warmUpTime + simulationTime
				+ tearDownTime;
		GregorianCalendar startDate;
		GregorianCalendar endDate;
		GregorianCalendar upRampDate;
		GregorianCalendar runSessionDate;
		GregorianCalendar downRampDate;
		GregorianCalendar endDownRampDate;

		String reportDir = "";
		String tmpDir = "/tmp/";

		int dcId = 0; // Integer.parseInt(args[0]);
		int userId = 0;//Integer.parseInt(args[1]);
		System.out
				.println("Quoddy client emulator - (C) Max Planck Institute for Software System 2012\n");
		reportDir = "benchmark/quoddy_dcId" + dcId + "_userId_" + userId + "_"
				+ TimeManagement.currentDateToString() + "/";
		createReportDir(reportDir);

		startDate = new GregorianCalendar();
		Stats stats = new Stats(numberOfStates);
		Stats warmUpStats = new Stats(numberOfStates);
		Stats simulationStats = new Stats(numberOfStates);
		Stats tearDownStats = new Stats(numberOfStates);
		Stats allStats = new Stats(numberOfStates);
		UserSession[] sessions = new UserSession[numberOfClients];

		
		
		//INITIALIZE ALL USERS
		for (int i = 0; i < numberOfClients; i++) {
			sessions[i] = new UserSession("UserSession ",i,stats);
			sessions[i].start();
		}
		
		
		upRampDate = new GregorianCalendar();
		// get data from warm up period
		try {
			Thread.currentThread().sleep(warmUpTime);
		} catch (java.lang.InterruptedException ie) {
			System.err.println("ClientEmulator has been interrupted.");
		}
		warmUpStats.merge(stats);
		stats.reset();

		// get data from simulation session time
		try {
			Thread.currentThread().sleep(simulationTime);
		} catch (java.lang.InterruptedException ie) {
			System.err.println("ClientEmulator has been interrupted.");
		}
		simulationStats.merge(stats);
		stats.reset();

		// get data from tear down time
		try {
			Thread.currentThread().sleep(tearDownTime);
		} catch (java.lang.InterruptedException ie) {
			System.err.println("ClientEmulator has been interrupted.");
		}
		tearDownStats.merge(stats);
		stats.reset();
		endDownRampDate = new GregorianCalendar();

	}// main
	
	/*
	 * transitions:
	 * 
	 * 
	 * */
	public static void test(){
		long start;
		
		Stats stats = new Stats(numberOfStates);
		UserSession testsession = new UserSession("TestUserSession ",1,stats);
		testsession.transition.resetToInitialState();
		System.out.println("Test interaction 1");
		start = System.currentTimeMillis();
		testsession.goNextState(0);//goHome(driver);
//		testsession.goNextState(1);//doLogin(driver);
//		testsession.goNextState(2);//doLogout(driver);
//		testsession.goNextState(3);//doUpdateStatus(driver);
//		testsession.goNextState(4);//doListMyUpdates(driver);
//		testsession.goNextState(5);//doListAllUsers(driver);
//		testsession.goNextState(6);//doViewUsersProfile(driver);
//		testsession.goNextState(7);//doAddNewFriend(driver);
//		testsession.goNextState(8);//doViewPendingFriendRequest(driver);
//		testsession.goNextState(9);//doConfirmFriend(driver);
//		testsession.goNextState(10);//doListAllMyFriends(driver);
////		testsession.goNextState(11);//doFollowUser(driver);
//		testsession.goNextState(12);//doListUsersIFollow(driver);
//		testsession.goNextState(13);//doListAllMyFollowers(driver);
//		testsession.goNextState(14);//endOfSession(driver);
		
		System.out.println("Testing features last for "+(System.currentTimeMillis()-start)+" milliseconds");
	}
	public static void createReportDir(String reportDir) {
		System.out.println("Creating report directory " + reportDir);
		File dir = new File(reportDir);
		dir.mkdirs();
		if (!dir.isDirectory()) {
			System.out.println("Unable to create " + reportDir
					+ " using current directory instead");
			reportDir = "./";
		} else
			try {
				reportDir = dir.getCanonicalPath() + "/";
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

}
