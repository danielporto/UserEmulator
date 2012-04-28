/*
 * 
 * Quoddy emulator

 * Author Daniel Porto
 * 
 * Based on RUBiS Client emulator
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * Initial developer(s): Emmanuel Cecchet, Julie Marguerite
 * Contributor(s): 
 */
 

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.NumberFormatException;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Stack;

/**
 * This class provides support for transitions between Quoddy web site pages.
 * A matrix contains probabilities of transition from one state to another.
 * A ReadExcelTextFile() method generates a matrix from an Excel file saved
 * as text with tab separator. The text file must have the following format :
 * <pre>
 * RUBiS Transition Table <tab> Name of transition set
 *
 * "To >>>
 * From vvvv  "[tab]Home[tab]Register[tab]...list of column headers...[tab]About Me
 * goHome                     				     	[tab]probability_1_1[tab]probability_1_2[tab]...[tab]probability_1_3[tab]transition_waiting_time
 * Login                     				     	[tab]probability_2_1[tab]probability_1_2[tab]...[tab]probability_1_3[tab]transition_waiting_time
 * Logout                     				     	[tab]probability_3_1[tab]probability_1_2[tab]...[tab]probability_1_3[tab]transition_waiting_time
 * Update status on his or her own profile          [tab]probability_4_1[tab]probability_1_2[tab]...[tab]probability_1_3[tab]transition_waiting_time
 * My updates (read all his or her own updates)     [tab]probability_5_1[tab]probability_1_2[tab]...[tab]probability_1_3[tab]transition_waiting_time
 * List all users                                	[tab]probability_6_1[tab]probability_1_2[tab]...[tab]probability_1_3[tab]transition_waiting_time
 * View a user's profile                     		[tab]probability_7_1[tab]probability_1_2[tab]...[tab]probability_1_3[tab]transition_waiting_time
 * Add to friends                     				[tab]probability_8_1[tab]probability_1_2[tab]...[tab]probability_1_3[tab]transition_waiting_time
 * Confirm pending friend request                   [tab]probability_9_1[tab]probability_1_2[tab]...[tab]probability_1_3[tab]transition_waiting_time
 * List all my friends                     			[tab]probability_10_1[tab]probability_1_2[tab]...[tab]probability_1_3[tab]transition_waiting_time
 * Follow somebody                     				[tab]probability_11_1[tab]probability_1_2[tab]...[tab]probability_1_3[tab]transition_waiting_time
 * List all people I follow (People|follow)         [tab]probability_12_1[tab]probability_1_2[tab]...[tab]probability_1_3[tab]transition_waiting_time
 * List all followers                     			[tab]probability_13_1[tab]probability_1_2[tab]...[tab]probability_1_3[tab]transition_waiting_time
 * Logout  											[tab]probability_14_1[tab]probability_1_2[tab]...[tab]probability_1_3[tab]transition_waiting_time
 * ...
 *
 * </pre>
 * Everything after the <code>End of Session</code> line is ignored.
 * <code>Initial state</code> is Home page (column 1).
 * <code>probability_x_y</code> determines the probability to go from state x to state y.
 * 
 * There are 2 extra lines compared to colums. These lines are:
 * <code>Back probability:</code> probability to go back to last page (like the back button of the browser)
 * <code>End of session:</code> probability that the user ends the session (leave the web site).
 * </pre>
 *
 * @author <a href="mailto:cecchet@rice.edu">Emmanuel Cecchet</a> and <a href="mailto:julie.marguerite@inrialpes.fr">Julie Marguerite</a>
 * @version 1.0
 */

public class TransitionTable
{
  private int    nbColumns;
  private int    nbRows;
  private float  transitions[][];
  private int    transitionsTime[];
  private String tableName = null;
  private Random rand = new Random();
  private Stack  previousStates = new Stack();
  private int    currentState = 0;
  private Stats  stats;
  private boolean useTPCWThinkTime;
  private static String[] stateNames;

  /**
   * Creates a new <code>TransitionTable</code> instance.
   */
  
  public TransitionTable(int columns, int rows, Stats statistics, boolean UseTPCWThinkTime)
  {
    nbColumns = columns;
    nbRows = rows;
    stats = statistics;
    transitions = new float[nbColumns][nbRows];
    transitionsTime = new int[nbRows];
    useTPCWThinkTime = UseTPCWThinkTime;
  }
  
  /**
   * Get the name of the transition table as defined in file.
   *
   * @return name of the transition table.
   */
  public String getTableName() 
  {
    return tableName;
  }


  /**
   * Resets the current state to initial state (home page).
   */
  public void resetToInitialState()
  {
    currentState = Transitions.doLogin;//login
    stats.incrementCount(currentState);
  }


  /**
   * Return the current state value (row index). 
   *
   * @return current state value (0 means initial state)
   */
  public int getCurrentState()
  {
    return currentState;
  }


  /**
   * Return the previous state value (row index). 
   *
   * @return previous state value (-1 means no previous state)
   */
  public int getPreviousState()
  {
    if (previousStates.empty())
      return -1;
    else
    {
      Integer state = (Integer)previousStates.peek();
      return state.intValue();
    }
  }


  /**
   * Go back to the previous state and return the value of the new state
   *
   * @return new state value (-1 means no previous state)
   */
  public int backToPreviousState()
  {
    if (previousStates.empty())
      return -1;
    else
    {
      Integer state = (Integer)previousStates.pop();
      currentState = state.intValue();
      return currentState;
    }
  }


  /**
   * Returns true if the 'End of Session' state has been reached
   *
   * @return true if current state is 'End of Session'
   */
  public boolean isEndOfSession()
  {
    return currentState == (nbRows-1);
  }
  

  /**
   * Return the current state name
   *
   * @return current state name
   */
  public String getCurrentStateName()
  {
    return stateNames[currentState];
  }
  

  /**
   * Return a state name
   *
   * @return current state name
   */
  public static String getStateName(int state)
  {
    return stateNames[state];
  }
  

  /**
   * Compute a next state from current state according to transition matrix.
   *
   * @return value of the next state
   */
  public int nextState()
  {
    int   beforeStep = currentState;
    float step = rand.nextFloat();
    float cumul = 0;
    int   i;

    for (i = 0 ; i < nbRows ; i++)
    {
    	
      cumul = cumul + transitions[currentState][i];
      //System.out.println("checking the probability cumul"+cumul+" transition "+transitions[currentState][i]+" step"+step);
      if (step < cumul)
      {
        currentState = i;
        break;
      }
    }
    //System.out.println("previous state:"+beforeStep+" next state "+currentState);
    // Deal with Back to previous state
//    if (currentState == nbRows-2)
//    { 
//      if (previousStates.empty())
//        System.out.println("Error detected: Trying to go back but no previous state is available (currentState:"+currentState+", beforeStep:"+beforeStep);
//      else
//      { // Back adds both stats of back and new state but only sleep "back waiting time"
//        // and return the new state (after back).
//        stats.incrementCount(currentState); // Add back state stat
//        try
//        {
//          if (useTPCWThinkTime)
//            Thread.currentThread().sleep((long)((float)TPCWthinkTime()));
//          else
//            Thread.currentThread().sleep((long)((float)transitionsTime[currentState]));
//        }
//        catch (java.lang.InterruptedException ie)
//        {
//          System.err.println("Thread "+Thread.currentThread().getName()+" has been interrupted.");
//        }
//        Integer previous = (Integer)previousStates.pop();
//        currentState = previous.intValue();
////        System.out.println("Thread "+Thread.currentThread().getName()+": Going back from "+stateNames[beforeStep]+" to "+stateNames[currentState]+"<br>\n");
//        stats.incrementCount(currentState); // Add new state stat
//        return currentState;
//      }
//    }
//    else
//    { // Add this state to history (previousStates) if needed
//      if (!isEndOfSession())
//      { // If there is no probability to go back from this state, just empty the stack
//        if (transitions[currentState][nbRows-2] == 0)
//          previousStates.removeAllElements();
//        else // else add the previous state to the history just in case we go back !
//          previousStates.push(new Integer(beforeStep));
////        System.out.println("Thread "+Thread.currentThread().getName()+": "+stateNames[beforeStep]+" -> "+stateNames[currentState]+"<br>\n");
//      }
//    }
      
      
    stats.incrementCount(currentState);
    try
    {
      if (useTPCWThinkTime)
        Thread.currentThread().sleep((long)((float)TPCWthinkTime()));
      else
        Thread.currentThread().sleep((long)((float)transitionsTime[currentState]));
    }
    catch (java.lang.InterruptedException ie)
    {
      System.err.println("Thread "+Thread.currentThread().getName()+" has been interrupted.");
    }
    return currentState;
  }

  
  /**
   * Read the matrix transition from a file conforming to the
   * format described in the class description.
   *
   * @param filename name of the file to read the matrix from
   * @return true upon success else false
   */
  public boolean ReadExcelTextFile(String filename)
  { 
    BufferedReader reader;
    int            i = 0;
    int            j = 0;

    // Try to open the file
    try
    {
      //reader = new BufferedReader(new FileReader(filename));
      reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filename)));
    }
    catch (Exception f)
    {
      System.err.println("File "+filename+" not found.");
      return false;
    }

    // Now read the file using tab (\t) as field delimiter
    try
    {
      // Header
      StringTokenizer st = new StringTokenizer(reader.readLine(), "\t");
      String s = st.nextToken(); // Should be 'Quoddy Transition Table'
      tableName = st.nextToken();
      System.out.println("Reading "+tableName+" from "+filename);
      reader.readLine(); // Empty line
      //reader.readLine(); // To >>>
      reader.readLine(); // Column headers

      stateNames = new String[nbRows];
      // Read the matrix
      for (i = 0 ; i < nbRows ; i++)
      {
        st = new StringTokenizer(reader.readLine(), "\t");
        stateNames[i] = st.nextToken();
        //System.out.println("reading transition table element:"+stateNames[i]);
        for (j = 0 ; j < nbColumns ; j++)
        {
          Float f = new Float(st.nextToken());
          transitions[j][i] = f.floatValue();
        }
        // Last column is transition_waiting_time
        Integer t = new Integer(st.nextToken());
        transitionsTime[i] = t.intValue();
      }
      reader.close();
    }
    catch (IOException ioe)
    {
      System.err.println("An error occured while reading "+filename+". ("+ioe.getMessage()+")");
      return false;
    }
    catch (NoSuchElementException nsu)
    {
      System.err.println("File format error in file "+filename+" when reading line "+i+", column "+j+". ("+nsu.getMessage()+")");
      return false;
    }
    catch (NumberFormatException ne)
    {
      System.err.println("Number format error in file "+filename+" when reading line "+i+", column "+j+". ("+ne.getMessage()+")");
      return false;
    }
    System.out.println("Transition matrix successfully build.");
    return true;
  }


  /**
   * Display the transition matrix on the standard output.
   * This function is only provided for debugging purposes.
   */
  protected void displayMatrix()
  {
    int i,j;

    System.out.println("\n<h3><br>### Transition table ###</h3>\n");
    System.out.println("Transition set: '"+tableName+"'<br>\n");
    System.out.println("<TABLE border=\"1\" summary=\"transition table\"><TBODY>\n");
    System.out.println("<THEAD><TR><TH>State name");
    for (j = 0 ; j < nbColumns ; j++)
      System.out.print("<TH>"+stateNames[j]);
    System.out.print("<TH>Transition time");
    for (i = 0 ; i < nbRows ; i++)
    {
      System.out.print("\n<TR><TD><div align=left><B>"+stateNames[i]+"</B></div>");
      for (j = 0 ; j < nbColumns ; j++)
        System.out.print("<TD><div align=right>"+Float.toString(transitions[j][i])+"</div>");
      System.out.print("<TD><div align=right>"+Float.toString(transitionsTime[i])+"</div>");
    }
    System.out.println("\n</TBODY></TABLE>\n");
    System.out.println();
  }


  // Negative exponential distribution used by
  //  TPC-W spec for Think Time (Clause 5.3.2.1) and USMD (Clause 6.1.9.2)
  private long TPCWthinkTime()
  {
    double r = rand.nextDouble();
    if (r < (double)4.54e-5)
      return ((long) (r+0.5));
    return  ((long) ((((double)-7000.0)*Math.log(r))+0.5));
  }

}
