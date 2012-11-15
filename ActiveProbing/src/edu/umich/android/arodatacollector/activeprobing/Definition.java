/**
 * @author Yuanyuan Zhou
 * @date Oct 30, 2012
 * @organization University of Michigan, Ann Arbor
 */

package edu.umich.android.arodatacollector.activeprobing;

public class Definition {
	public static final String SERVER_NAME = "owl.eecs.umich.edu";
	public static final String RESULT_FILENAME = "activeprobing.txt";
	public static final int PORT_DOWNLINK = 5001;
	public static final int PORT_UPLINK = 5002;
	public static final int TCP_TIMEOUT_IN_MILLI = 10000; // 5 seconds for timeout
	public static final int TP_DURATION_IN_MILLI = 16000; // 16 seconds for throughput tests
	public static final int THROUGHPUT_UP_SEGMENT_SIZE = 1347;
	public static final String UPLINK_FINISH_MSG = "*#*";
	
	// data limitation
	public static final int UP_DATA_LIMIT_BYTE = 10*(int)Math.pow(2, 10)*(int)Math.pow(2, 10); // uplink limitation (10 MB)
	public static final int DOWN_DATA_LIMIT_BYTE = 10*(int)Math.pow(2, 10)*(int)Math.pow(2, 10); // downlink limitation (10 MB)
}
