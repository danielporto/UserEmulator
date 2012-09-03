import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.GregorianCalendar;

import javax.xml.stream.events.StartDocument;

import org.xml.sax.SAXException;

public class QuoddyUserEmulator {
	public static String baseUrl = "http://localhost:8080";
	public static String appname = "/quoddy";// "/rubis_servlets";//"/quoddy2-0.1";//
	// "http://swsao5001.mpi-sws.org:8080/quoddy2-0.1");
	public static int numberOfClients = 5;
	public static final String userPrefix = "user";
	public static final String userPassword = "secret";
	public static final int numberOfExistingUsers = 1000;
	public static final int numberOfStates = 14;// check Transitions class
	public static int warmUpTime = 1 * 60000;
	public static int simulationTime = 1 * 60000;
	public static int tearDownTime = 1 * 60000;
	public static final String transitionTable = "default_transitions.csv";
	public static final int maxTransitionsPerSession = 100;
	public static long totalSimulationTime = 0;
	public static final boolean useThinkTime = false;
	public static final boolean DEBUG = false;
	public static final boolean DEBUG2 = false;
	public static int dcId = 0;
	public static int userId = 0;
	public static boolean getResources = false; //disable all resources download
	public static final boolean getImages = true; //fine grained resource download
	public static final boolean getCss = true;
	public static final boolean getJavascript = true;

	public static void main(String[] args) {
		//test();
		 if (args.length < 7) {
		 System.out.println("QuoddyEmulator dcId userId webproxyHost userNum warmUpTime, simulationTime, tearDownTime [download_resources]");
		 System.exit(-1);
		 }
		 dcId = Integer.parseInt(args[0]);
		 userId = Integer.parseInt(args[1]);
		 baseUrl = new String(args[2]);
		 System.out.println("I am a user at dc " + dcId + " and my id is " +
		 userId + " my proxy is " + baseUrl+appname);
		 numberOfClients = Integer.parseInt(args[3]);
		 warmUpTime = Integer.parseInt(args[4]);
		 simulationTime = Integer.parseInt(args[5]);
		 tearDownTime = Integer.parseInt(args[6]);
		 if (args.length == 8){
			 getResources = Boolean.parseBoolean(args[7]);
		 }
		 if(getResources) System.out.println("All the page resources is going to be downloaded");
		 else System.out.println("Ignoring all css, images and javascript files");
		 run();
	}

	public static void run() {
		totalSimulationTime = System.currentTimeMillis() + warmUpTime
				+ simulationTime + tearDownTime;
		GregorianCalendar startDate;
		GregorianCalendar endDate;
		GregorianCalendar startWarmUp;
		GregorianCalendar endWarmUp;
		GregorianCalendar startSession;
		GregorianCalendar endSession;
		GregorianCalendar startTearDown;
		GregorianCalendar endTearDown;

		startDate = new GregorianCalendar();
		String reportDir = "";
		String tmpDir = "/tmp/";

		System.out
				.println("Quoddy client emulator - (C) Max Planck Institute for Software System 2012\n");
		reportDir = "benchmark/quoddy_dcId" + dcId + "_userId_" + userId + "_"
				+ TimeManagement.currentDateToString() + "/";
		createReportDir(reportDir);

		Stats stats = new Stats(numberOfStates);
		Stats warmUpStats = new Stats(numberOfStates);
		Stats simulationStats = new Stats(numberOfStates);
		Stats tearDownStats = new Stats(numberOfStates);
		Stats allStats = new Stats(numberOfStates);
		UserSession[] sessions = new UserSession[numberOfClients];

		// INITIALIZE ALL USERS
		for (int i = 0; i < numberOfClients; i++) {
			sessions[i] = new UserSession("UserSession ", i, stats);
			sessions[i].start();
		}

		startWarmUp = new GregorianCalendar();
		System.out.println("ClientEmulator: Switching to ** WARM UP **");
		// get data from warm up period
		try {
			Thread.currentThread().sleep(warmUpTime);
		} catch (java.lang.InterruptedException ie) {
			System.err.println("ClientEmulator has been interrupted.");
		}
		warmUpStats.merge(stats);
		stats.reset();
		endWarmUp = new GregorianCalendar();

		System.out.println("ClientEmulator: Switching to ** SESSION **");
		// get data from simulation session time
		startSession = new GregorianCalendar();
		try {
			Thread.currentThread().sleep(simulationTime);
		} catch (java.lang.InterruptedException ie) {
			System.err.println("ClientEmulator has been interrupted.");
		}
		simulationStats.merge(stats);
		stats.reset();
		endSession = new GregorianCalendar();
		System.out.println("ClientEmulator: Switching to ** TEAR DOWN **");
		// get data from tear down time
		startTearDown = new GregorianCalendar();
		try {
			Thread.currentThread().sleep(tearDownTime);
		} catch (java.lang.InterruptedException ie) {
			System.err.println("ClientEmulator has been interrupted.");
		}
		tearDownStats.merge(stats);
		stats.reset();
		endTearDown = new GregorianCalendar();

		// Tell the clients to stop browsing
		System.out.println("QuoddyEmulator: close browsers");
		for (int i = 0; i < numberOfClients; i++) {
			try {
				sessions[i].join(2000);
			} catch (java.lang.InterruptedException ie) {
				System.err.println("Thread " + i + " has been interrupted.");
			}
		}

		// Prepare the output data
		PrintStream outputStream;
		try {
			outputStream = new PrintStream(new FileOutputStream(reportDir
					+ "result.txt"));

			simulationStats.display_histogram(outputStream,
					"Runtime session Histogram");
			System.setOut(outputStream);
			warmUpStats.display_stats(outputStream, "Warm up",
					TimeManagement.diffTimeInMs(startWarmUp, endWarmUp), false);
			simulationStats.display_stats(outputStream, "Runtime session",
					TimeManagement.diffTimeInMs(startSession, endSession),
					false);
			tearDownStats.display_stats(outputStream, "Tear down",
					TimeManagement.diffTimeInMs(startTearDown, endTearDown),
					false);
			printGnuPlotFile(outputStream, simulationStats, baseUrl);
		} catch (Exception e) {
			System.err.println("Problem generating gnuplot file");
			e.printStackTrace();
		}

	}// main

	/*
	 * transitions:
	 */
	public static void test() {
		long start;

		Stats stats = new Stats(numberOfStates);
		UserSession testsession = new UserSession("user", 1, stats);

		System.out.println("Test interaction 1");
		start = System.currentTimeMillis();
		double avg = 0;
		long time;

		try {
				testsession.state.resetToInitialPage(testsession.driver);
				testsession.transitiontable.resetToInitialState();
				time = testsession.goNextState(1);/*login*/	
				System.out.println("Time to process "+ Transitions.stateToString[1] + " = "	+ (System.currentTimeMillis() - time));
				
				time = testsession.goNextState(0);/*goHome(driver);*/
				System.out.println("Time to process "+ Transitions.stateToString[0] + " = "	+ (System.currentTimeMillis() - time));
				//for(int i=0; i<1000;i++){
					//time = testsession.goNextState(2);//doUpdateStatus(driver);
					//System.out.println("Time to process "+ Transitions.stateToString[2] + " = "	+ (System.currentTimeMillis() - time));
				//}
				
				time = testsession.goNextState(3); //doListMyUpdates(driver);
				System.out.println("Time to process "+ Transitions.stateToString[3] + " = "	+ (System.currentTimeMillis() - time));
				
				time = testsession.goNextState(4);
				System.out.println("Time to process "+Transitions.stateToString[4] + " = " + (System.currentTimeMillis() - time));//doListAllUsers(driver);
			
				time = testsession.goNextState(5);//doViewUsersProfile(driver);
				System.out.println("Time to process "+Transitions.stateToString[5] + " = " + (System.currentTimeMillis() - time));
				
				time = testsession.goNextState(6);//doAddNewFriend(driver);
				System.out.println("Time to process "+Transitions.stateToString[6] + " = " + (System.currentTimeMillis() - time));
				
			    time = testsession.goNextState(7);//doViewPendingFriendRequest(driver);
				System.out.println("Time to process "+Transitions.stateToString[7] + " = " + (System.currentTimeMillis() - time));
				
				
				//time = testsession.goNextState(8);//doConfirmFriend(driver);
				//System.out.println("Time to process "+Transitions.stateToString[8] + " = " + (System.currentTimeMillis() - time));
			
			    time = testsession.goNextState(9);//doListAllMyFriends(driver);
				System.out.println("Time to process "+Transitions.stateToString[9] + " = " + (System.currentTimeMillis() - time));
				
				time = testsession.goNextState(5);//doViewUsersProfile(driver); //repeat due the doFollow
				System.out.println("Time to process "+Transitions.stateToString[5] + " = " + (System.currentTimeMillis() - time));
				time = testsession.goNextState(10);//doFollowUser(driver);
				System.out.println("Time to process "+Transitions.stateToString[10] + " = " + (System.currentTimeMillis() - time));
				
			
				time = testsession.goNextState(11);//doListUsersIFollow(driver);
				System.out.println("Time to process "+Transitions.stateToString[11] + " = " + (System.currentTimeMillis() - time));
				
				
				time = testsession.goNextState(12);//doListAllMyFollowers(driver);
				System.out.println("Time to process "+Transitions.stateToString[12] + " = " + (System.currentTimeMillis() - time));
				//System.out.println(testsession.driver.getPageSource());
				
			// testsession.goNextState(13);//endOfSession(driver);
			// for(int i=0; i<1000;i++)
			// avg+=testsession.goNextState(14);//testing
			// System.out.println("Average="+avg/1000);
		} catch (ItemNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Testing features last for "
				+ (System.currentTimeMillis() - start) + " milliseconds");
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

	public static void printGnuPlotFile(PrintStream out, Stats stat,
			String proxy) {
		int i;
		double throughput = 0;
		double latency = 0;
		long lat = 0, last = 0;
		double sim_time = 0;

		int errorqty = 0;
		int valid_interactions = 0;
		long totallatency = 0;
		out.println("# Info");
		for (i = 0; i < stat.getNbOfStats(); i++) {
			// out.printf("# "+
			// TransitionTable.getStateName(i)+": ERROR "+stat.getError(i)+" NInteractions"+stat.getCountHistogram(i));
			out.printf("# %-26s: %-5s %4d %-15s %4d\n",
					TransitionTable.getStateName(i), "ERROR", stat.getError(i),
					"Interactions", stat.getCountHistogram(i));
			errorqty += stat.getError(i);
			valid_interactions += stat.getCountHistogram(i);
			totallatency += stat.getTotalTimeHistogram(i);
		}
		out.println("#-----------------------------------------------------------------------");
		out.println("# Total Errors=" + errorqty + " Interactions="
				+ valid_interactions);

		latency = ((double) totallatency / valid_interactions);
		throughput = 1000 * ((double) valid_interactions / simulationTime);

		int totalaborts = 0, totaltransactions = 0, totaltxmudaborts = 0, totaltxmudredtxn = 0, totaltxmudbluetxn = 0, totalcommtiedtxn = 0;
		totalaborts = 0;// getAborts(proxy);
		totaltransactions = 0;// getTransactions(proxy);
		totaltxmudaborts = 0;// getTxMudAborts(proxy);
		totaltxmudredtxn = 0;// getTxMudRedTransactions(proxy);
		totaltxmudbluetxn = 0;// getTxMudBlueTransactions(proxy);
		totalcommtiedtxn = valid_interactions;

		out.println("#Proxies #Users #DBTransactions #TXCommited #TXMudRedTnx #TXMudBlueTnx #TXMudAbort #DBAborts #Interactions #Latency #Throughput #SimTime");
		out.format("%8d %6d %15d %11d %12d %13d %11d %6d %14d", 1,
				numberOfClients, totaltransactions, totalcommtiedtxn,
				totaltxmudredtxn, totaltxmudbluetxn, totaltxmudaborts,
				totalaborts, valid_interactions);
		out.format("%9.3f\t %6.3f %8.3f\n", latency, throughput,
				((double) simulationTime / 1000));

	}

}
