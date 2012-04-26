import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.GregorianCalendar;

import javax.xml.stream.events.StartDocument;

public class QuoddyUserEmulator {
	public static String baseUrl = new String(
			"http://swsao5001.mpi-sws.org:8080");
	public static final int numberOfClients = 2;
	public static final String userPrefix = "user";
	public static final int numberOfExistingUsers = 13;
	public static final int numberOfStates = 14;
	public static final int warmUpTime = 6000;
	public static final int simulationTime = 60000;
	public static final int tearDownTime = 6000;
	public static final String transitionTable = "default_transitions.csv";
	public static final int maxTransitionsPerSession = 1000;
	public static long endOfSession = 0;
	public static final boolean useThinkTime = false;
	public static final boolean DEBUG = true;

	public static void main(String[] args) {
		//test();
		run();
	}

	public static void run() {
		endOfSession = System.currentTimeMillis() + warmUpTime + simulationTime
				+ tearDownTime;
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

		int dcId = 0; // Integer.parseInt(args[0]);
		int userId = 0;// Integer.parseInt(args[1]);
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

			System.setOut(outputStream);
			System.out.println("Warm up statistics");
			warmUpStats.display_stats("Warm up",
					TimeManagement.diffTimeInMs(startWarmUp, endWarmUp), false);
			System.out.println("Runtime statistics");
			simulationStats.display_stats("Runtime session",
					TimeManagement.diffTimeInMs(startSession, endSession),
					false);
			System.out.println("Teardown statistics");
			tearDownStats.display_stats("Tear down",
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
		UserSession testsession = new UserSession("TestUserSession ", 1, stats);
		testsession.transition.resetToInitialState();
		System.out.println("Test interaction 1");
		start = System.currentTimeMillis();
		testsession.goNextState(0);// goHome(driver);
		// testsession.goNextState(1);//doLogin(driver);
		// testsession.goNextState(2);//doLogout(driver);
		// testsession.goNextState(3);//doUpdateStatus(driver);
		// testsession.goNextState(4);//doListMyUpdates(driver);
		// testsession.goNextState(5);//doListAllUsers(driver);
		// testsession.goNextState(6);//doViewUsersProfile(driver);
		// testsession.goNextState(7);//doAddNewFriend(driver);
		// testsession.goNextState(8);//doViewPendingFriendRequest(driver);
		// testsession.goNextState(9);//doConfirmFriend(driver);
		// testsession.goNextState(10);//doListAllMyFriends(driver);
		// testsession.goNextState(11);//doFollowUser(driver);
		// testsession.goNextState(12);//doListUsersIFollow(driver);
		// testsession.goNextState(13);//doListAllMyFollowers(driver);
		// testsession.goNextState(14);//endOfSession(driver);

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
			out.printf("# %-25s: %-5s %4d %-15s %4d\n",
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
