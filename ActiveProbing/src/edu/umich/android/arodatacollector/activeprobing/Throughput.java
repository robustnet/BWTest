/**
 * @author Yuanyuan Zhou
 * @date Oct 30, 2012
 * @organization University of Michigan, Ann Arbor
 */

package edu.umich.android.arodatacollector.activeprobing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class Throughput {
	private double[] tps_result;
	public int size = 0;
	public long testStartTime = 0; //test start time, used to determine slow start period
	public long startTime = 0; //start time of this period to calculate throughput
	public final static long SAMPLE_PERIOD = 1000; 
	public final static long SLOW_START_PERIOD = 5000; //empirically set to 5 seconds 
	
	public Throughput() {
		tps_result = new double[]{};
		testStartTime = System.currentTimeMillis();
	}
	
	public void runTest(boolean isDown) {
		String type;
		if (isDown) {
			downlink();
			type = "DOWN";
		} else {
			uplink();
			type = "UP";
		}
		Utilities.writeToSDCard("MLAB_THROUGHPUT_" + type + ":<median:" +
				Utilities.roundDouble(Utilities.getMedian(tps_result)) +
				"><max:" + Utilities.roundDouble(Utilities.getMax(tps_result)) +
				"><min:" + Utilities.roundDouble(Utilities.getMin(tps_result)) +
				"><sample:" + tps_result.length + ">;" + "\n", Definition.RESULT_FILENAME);
	}
	
	private void downlink() {
		Socket tcpSocket = null;
		BufferedReader br = null;
		try {
			tcpSocket = new Socket();
			SocketAddress remoteAddr = new InetSocketAddress(Definition.SERVER_NAME, Definition.PORT_DOWNLINK);
			tcpSocket.connect(remoteAddr, Definition.TCP_TIMEOUT_IN_MILLI);
			tcpSocket.setSoTimeout(Definition.TCP_TIMEOUT_IN_MILLI);
			tcpSocket.setTcpNoDelay(true);
			br = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		try {
			int read_bytes = 0;
			do {
				String str = br.readLine();
				read_bytes = str.length();
				updateSize(str.length());
			} while (read_bytes >= 0);
			br.close();
			tcpSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	private void uplink() {
		Socket tcpSocket = null;
		PrintWriter pw = null;
		BufferedReader br = null;
		try {
			tcpSocket = new Socket();
			SocketAddress remoteAddr = new InetSocketAddress(Definition.SERVER_NAME, Definition.PORT_UPLINK);
			tcpSocket.connect(remoteAddr, Definition.TCP_TIMEOUT_IN_MILLI);
			pw = new PrintWriter(tcpSocket.getOutputStream(), true);
			br = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
			tcpSocket.setSoTimeout(Definition.TCP_TIMEOUT_IN_MILLI);
			tcpSocket.setTcpNoDelay(true);
		} catch (Exception e){
			e.printStackTrace();
			return;
		}

		long startTime = System.currentTimeMillis();
		long endTime = System.currentTimeMillis();

		//Test lasts 16 seconds - Junxian
		try {
			do {
				pw.println(Utilities.genRandomString(Definition.THROUGHPUT_UP_SEGMENT_SIZE));
				pw.flush();
				endTime = System.currentTimeMillis();
			} while ((endTime - startTime) < Definition.TP_DURATION_IN_MILLI);

			pw.println(Definition.UPLINK_FINISH_MSG);
			String str = br.readLine();
			String [] tps_result_str = str.split("#");
			for (int i = 0; i < tps_result_str.length; i++) {
				double throughput = Double.valueOf(tps_result_str[i]);
				tps_result = Utilities.pushResult(tps_result, throughput);
			}
			
			pw.close();
			br.close();
			tcpSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
	}
	
	private void updateSize(int delta) {
		double gtime = System.currentTimeMillis() - testStartTime;
		if (gtime < SLOW_START_PERIOD) //ignore slow start
			return;
		if (startTime == 0) {
			startTime = System.currentTimeMillis();
			size = 0;
		}
		size += delta;
		double time = System.currentTimeMillis() - startTime;
		if (time < SAMPLE_PERIOD) {
			return;
		} else {
			double throughput = Utilities.roundDouble((double)size * 8.0 / time); //time is in milli, so already kbps
			System.out.println("_throughput: " + throughput + " kbps_Time(sec): " + (gtime / 1000.0));
			tps_result = Utilities.pushResult(tps_result, throughput);
			size = 0;
			startTime = System.currentTimeMillis();
		}	
	}
}