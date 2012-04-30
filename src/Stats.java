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
 * Contributor(s): 
 */
import java.io.PrintStream;
import java.text.DecimalFormat;

/**
 * This class provides thread-safe statistics. Each statistic entry is composed as follow:
 * <pre>
 * count     : statistic counter
 * error     : statistic error counter
 * minTime   : minimum time for this entry (automatically computed)
 * maxTime   : maximum time for this entry (automatically computed)
 * totalTime : total time for this entry
 * </pre>
 *
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

public class Stats
{
  private int nbOfStats;
  private int count[];
  private int error[];
  private long minTime[];
  private long maxTime[];
  private long totalTime[];
  private int  nbSessions;   // Number of sessions succesfully ended
  private long sessionsTime; // Sessions total duration
  private Histogram [] interaction_response_time;
  int interaction_response_time_max=60000;
  int interaction_response_time_binsize=10;
  /**
   * Creates a new <code>Stats</code> instance.
   * The entries are reset to 0.
   *
   * @param NbOfStats number of entries to create
   */
  public Stats(int NbOfStats)
  {
    nbOfStats = NbOfStats;
    count = new int[nbOfStats];
    error = new int[nbOfStats];
    minTime = new long[nbOfStats];
    maxTime = new long[nbOfStats];
    totalTime = new long[nbOfStats];
    interaction_response_time = new Histogram[nbOfStats];
    
    for (int h=0;h<interaction_response_time.length;h++) {
    	interaction_response_time[h] = new Histogram(interaction_response_time_max/interaction_response_time_binsize, interaction_response_time_binsize);
	 }
    
    reset();
  }


  /**
   * Resets all entries to 0
   */
  public synchronized void reset()
  {
    int i;

    for (i = 0 ; i < nbOfStats ; i++)
    {
      count[i] = 0;
      error[i] = 0;
      minTime[i] = Long.MAX_VALUE;
      maxTime[i] = 0;
      totalTime[i] = 0;
      interaction_response_time[i].clear();
    }
    nbSessions = 0;
    sessionsTime = 0;
    
    
  }

  /**
   * Add a session duration to the total sessions duration and
   * increase the number of succesfully ended sessions.
   *
   * @param time duration of the session
   */
  public synchronized void addSessionTime(long time)
  {
    nbSessions++;
    if (time < 0)
    {
      System.err.println("Negative time received in Stats.addSessionTime("+time+")<br>\n");
      return ;
    }
    sessionsTime = sessionsTime + time;
  }

 /**
   * Increment the number of succesfully ended sessions.
   */
  public synchronized void addSession()
  {
    nbSessions++;
  }


  /**
   * Increment an entry count by one.
   *
   * @param index index of the entry
   */
  public synchronized void incrementCount(int index)
  {
    count[index]++;
  }


  /**
   * Increment an entry error by one.
   *
   * @param index index of the entry
   */
  public synchronized void incrementError(int index)
  {
    error[index]++;
  }


  /**
   * Add a new time sample for this entry. <code>time</code> is added to total time
   * and both minTime and maxTime are updated if needed.
   *
   * @param index index of the entry
   * @param time time to add to this entry
   */
  public synchronized void updateTime(int index, long time)
  {
    if (time < 0)
    {
      System.err.println("Negative time received in Stats.updateTime("+time+")<br>\n");
      return ;
    }
    totalTime[index] += time;
    if (time > maxTime[index])
      maxTime[index] = time;
    if (time < minTime[index])
      minTime[index] = time;
    
    interaction_response_time[index].add((int)time);
  }


  /**
   * Get current count of an entry
   *
   * @param index index of the entry
   *
   * @return entry count value
   */
  public synchronized int getCount(int index)
  {
    return count[index];
  }
  public synchronized int getCountHistogram(int index)
  {
    return interaction_response_time[index].samples();
  }

  /**
   * Get current error count of an entry
   *
   * @param index index of the entry
   *
   * @return entry error value
   */
  public synchronized int getError(int index)
  {
    return error[index];
  }


  /**
   * Get the minimum time of an entry
   *
   * @param index index of the entry
   *
   * @return entry minimum time
   */
  public synchronized long getMinTime(int index)
  {
    return minTime[index];
  }


  /**
   * Get the maximum time of an entry
   *
   * @param index index of the entry
   *
   * @return entry maximum time
   */
  public synchronized long getMaxTime(int index)
  {
    return maxTime[index];
  }


  /**
   * Get the total time of an entry
   *
   * @param index index of the entry
   *
   * @return entry total time
   */
  public synchronized long getTotalTime(int index)
  {
    return totalTime[index];
  }
  
  public synchronized long getTotalTimeHistogram(int index)
  {
    return interaction_response_time[index].sum();
  }

  /**
   * Get the total number of entries that are collected
   *
   * @return total number of entries
   */
  public int getNbOfStats()
  {
    return nbOfStats;
  }


  /**
   * Adds the entries of another Stats object to this one.
   *
   * @param anotherStat stat to merge with current stat
   */
  public synchronized void merge(Stats anotherStat)
  {
    if (this == anotherStat)
    {
      System.out.println("You cannot merge a stats with itself");
      return;
    }
    if (nbOfStats != anotherStat.getNbOfStats())
    {
      System.out.println("Cannot merge stats of differents sizes.");
      return;
    }
    for (int i = 0 ; i < nbOfStats ; i++)
    {
      count[i] += anotherStat.getCount(i);
      error[i] += anotherStat.getError(i);
      if (minTime[i] > anotherStat.getMinTime(i))
        minTime[i] = anotherStat.getMinTime(i);
      if (maxTime[i] < anotherStat.getMaxTime(i))
        maxTime[i] = anotherStat.getMaxTime(i);
      totalTime[i] += anotherStat.getTotalTime(i);
      interaction_response_time[i].copy(anotherStat.interaction_response_time[i]);
    }
    nbSessions   += anotherStat.nbSessions;
    sessionsTime += anotherStat.sessionsTime;
    
  }


  /**
   * Display an HTML table containing the stats for each state.
   * Also compute the totals and average throughput
   *
   * @param title table title
   * @param sessionTime total time for this session
   * @param exclude0Stat true if you want to exclude the stat with a 0 value from the output
   */
  public void display_stats(PrintStream out, String title, long sessionTime, boolean exclude0Stat)
  {
    int counts = 0;
    int errors = 0;
    long time = 0;
    String percentOfTotal="";
    String countss="";
    String errorss="";
    String minTimes="";
    String maxTimes="";
    String avgTimes="";
    String samples="";
    String averages="";
    String stddeviations="";
    
    
    DecimalFormat df = new DecimalFormat("#.##");
    out.println(title+" statistics");
    out.printf("%-27s | %-10s | %-7s | %-7s | %-10s | %-10s | %-10s | %-10s | %-10s ms | %-10s \n",
    		"State name", "% of total", "Count","Errors","Min Time","Max Time","Avg Time","SAMPLES","AVERAGE","STANDARD DEVIATION");

    // Display stat for each state
    for (int i = 0 ; i < getNbOfStats() ; i++)
    {
      counts += count[i];
      errors += error[i];
      time += totalTime[i];
    }

    for (int i = 0 ; i < getNbOfStats() ; i++)
    {
      if ((exclude0Stat && count[i] != 0) || (!exclude0Stat))
      {
        //LINE NAME
        if ((counts > 0) && (count[i] > 0))
          percentOfTotal= (100*count[i]/counts+" %");
        else
        	percentOfTotal= ("0 %");

        //COUNT
        countss=count[i]+"";
        //ERRORs
        errorss=error[i]+"";
        
        if (minTime[i] != Long.MAX_VALUE)
          minTimes=minTime[i]+" ms";
        else
        	minTimes="0"+" ms";;
        
        maxTimes=maxTime[i]+" ms";
        if (count[i] != 0)
         avgTimes=(totalTime[i]/count[i]+" ms");
        else
        	avgTimes=("0 ms");
        
        if(interaction_response_time[i].samples()>0){
        	samples=interaction_response_time[i].samples()+"";
        	averages=df.format(interaction_response_time[i].average())+" ms";
            stddeviations=df.format(interaction_response_time[i].standard_deviation())+"";
        }
        else{
        	samples="0";
        	averages="0 m";
        	stddeviations="0";	
        }
      }
      out.printf("%-27s | %-10s | %-7s | %-7s | %-10s | %-10s | %-10s | %-10s | %-10s ms | %-10s \n",
	  TransitionTable.getStateName(i), percentOfTotal, countss,errors,minTimes,maxTimes,avgTimes,samples,averages,stddeviations);

    }

    // Display total   
    if (counts > 0)
    {
//      out.print("<TR><TD><div align=left><B>Total</B></div><TD><div align=right><B>100 %</B></div><TD><div align=right><B>"+counts+
//                       "</B></div><TD><div align=right><B>"+errors+ "</B></div><TD><div align=center>-</div><TD><div align=center>-</div><TD><div align=right><B>");
//      counts += errors;
//      out.println(time/counts+" ms</B></div>");
      
        out.printf("%-27s | %-10s | %-7s | %-7s | %-10s | %-10s | %-10s | %-10s | %-10s ms | %-10s \n",
    		  "Totals", "100 %", counts+"",errors+"","-","-",time/(counts+errors) +" ms" ,"","","");
      // Display stats about sessions
      out.println("Average throughput "+1000*counts/sessionTime+" req/s");
      out.println("Completed sessions "+nbSessions);
      out.println("Total time "+sessionsTime/1000L+" seconds");
      out.print("Average session time");
      if (nbSessions > 0)
        out.print(sessionsTime/(long)nbSessions/1000L+" seconds\n");
      else
        out.print("0 second\n");
    }
    out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
  }

}
