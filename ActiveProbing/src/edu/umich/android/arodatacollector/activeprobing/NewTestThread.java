/**
 * @author Yuanyuan Zhou
 * @date Oct 30, 2012
 * @organization University of Michigan, Ann Arbor
 */

package edu.umich.android.arodatacollector.activeprobing;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewTestThread extends Thread {
	
	public void run() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String nowStr = dateFormat.format(new Date()); 
		Utilities.writeToSDCard(nowStr + "\n", Definition.RESULT_FILENAME);
		RTT rtt = new RTT();
		rtt.runTest();
		nowStr = dateFormat.format(new Date());
		Utilities.writeToSDCard(nowStr + "\n", Definition.RESULT_FILENAME);
		Throughput tps_down = new Throughput();
		tps_down.runTest(true);
		nowStr = dateFormat.format(new Date());
		Utilities.writeToSDCard(nowStr + "\n", Definition.RESULT_FILENAME);
		Throughput tps_up = new Throughput();
		tps_up.runTest(false);
	}
}
