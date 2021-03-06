/**
 * @author Yuanyuan Zhou
 * @date Oct 30, 2012
 * @organization University of Michigan, Ann Arbor
 */

package edu.umich.datacollector.activeprobing;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.util.Log;

public class Throughput {
	private double[] tps_result;
	public int size = 0;
	public int totalSendSize = 0; // uplink accumulative data
	public int totalRevSize = 0;  // downlink accumulative data
	public long testStartTime = 0; //test start time, used to determine slow start period
	public long startTime = 0; //start time of this period to calculate throughput
	public final static long SAMPLE_PERIOD = 1000; 
	public final static long SLOW_START_PERIOD = 5000; //empirically set to 5 seconds 
	
	public Throughput() {
		tps_result = new double[]{};
		testStartTime = System.currentTimeMillis();
	}
	
	public void runTest(boolean isDown) {
		totalRevSize = 0;
		totalSendSize = 0;
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
		//BufferedReader br = null;
		InputStream is = null;
		try {
			tcpSocket = new Socket();
			SocketAddress remoteAddr = new InetSocketAddress(Definition.SERVER_NAME, Definition.PORT_DOWNLINK);
			tcpSocket.connect(remoteAddr, Definition.TCP_TIMEOUT_IN_MILLI);
			tcpSocket.setSoTimeout(Definition.TCP_TIMEOUT_IN_MILLI);
			tcpSocket.setTcpNoDelay(true);
			is = tcpSocket.getInputStream();
			//br = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		try {
			int read_bytes = 0;
			Log.d("BWTest", "Before downlink bandwidth test");
			String str;
			byte [] buffer = new byte[10000];
			while (((read_bytes = is.read(buffer)) > -1)/*str = br.readLine()) != null*/ && totalRevSize < Definition.DOWN_DATA_LIMIT_BYTE){
				//read_bytes = str.length();
				//updateSize(str.length());
				updateSize(read_bytes);
				totalRevSize += read_bytes;
			} 
			Log.d("BWTest", "Finish downlink bandwidth test");
			if (totalRevSize >= Definition.DOWN_DATA_LIMIT_BYTE) {
				Log.d("BWTest", "Detect downlink exceeding limitation " + (double)Definition.DOWN_DATA_LIMIT_BYTE/(Math.pow(2, 10)*Math.pow(2, 10)) + " MB");
			}
			else
				Log.d("BWTest", "Total download data is " + (double)totalRevSize/(Math.pow(2, 10)*Math.pow(2, 10)) + " MB");
			is.close();
			//br.close();
			tcpSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	private void uplink() {
		Socket tcpSocket = null;
		// PrintWriter pw = null;
		// BufferedReader br = null;
		InputStream is = null;
		OutputStream os = null;
		try {
			tcpSocket = new Socket();
			SocketAddress remoteAddr = new InetSocketAddress(Definition.SERVER_NAME, Definition.PORT_UPLINK);
			tcpSocket.connect(remoteAddr, Definition.TCP_TIMEOUT_IN_MILLI);
			// pw = new PrintWriter(tcpSocket.getOutputStream(), true);
			os = tcpSocket.getOutputStream();
			// br = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
			is = tcpSocket.getInputStream();
			tcpSocket.setSoTimeout(Definition.TCP_TIMEOUT_IN_MILLI);
			tcpSocket.setTcpNoDelay(true);
		} catch (Exception e){
			e.printStackTrace();
			return;
		}

		long startTime = System.currentTimeMillis();
		long endTime = System.currentTimeMillis();

		//Test lasts 16 seconds - Junxian
		byte[] buffer = new byte[Definition.THROUGHPUT_UP_SEGMENT_SIZE];
		try {
			do {
				buffer = Utilities.genRandomString(Definition.THROUGHPUT_UP_SEGMENT_SIZE).getBytes();
				os.write(buffer);
				os.flush();
				//pw.println(Utilities.genRandomString(Definition.THROUGHPUT_UP_SEGMENT_SIZE));
				//pw.flush();
				endTime = System.currentTimeMillis();
				totalSendSize += Definition.THROUGHPUT_UP_SEGMENT_SIZE;
			} while ((endTime - startTime) < Definition.TP_DURATION_IN_MILLI && totalSendSize < Definition.UP_DATA_LIMIT_BYTE);
			
			if (totalSendSize >= Definition.UP_DATA_LIMIT_BYTE) {
				Log.d("BWTest", "Detect uplink exceeding limitation " + (double)Definition.UP_DATA_LIMIT_BYTE/(Math.pow(2, 10)*Math.pow(2, 10)) + " MB");
			}
			else
				Log.d("BWTest", "Total upload data is " + (double)totalSendSize/(Math.pow(2, 10)*Math.pow(2, 10)) + " MB");
			byte[] lastbuffer = Definition.FINISH_MSG.getBytes();
			os.write(lastbuffer);
			os.flush();
			//pw.println(Definition.UPLINK_FINISH_MSG);
			// String str = br.readLine();
			int resultLen = is.read(buffer);
			Log.d("BWTest", "Last message length is " + resultLen);
			// convert byte into string
			byte[] convertResult = new byte[resultLen];
			for (int i = 0; i < resultLen; i++) {
				convertResult[i] = buffer[i];
			}
			String recvResult = new String(convertResult);
			Log.d("BWTest", "Uplink result is " + recvResult.toString());
			String [] tps_result_str = recvResult.toString().split("#");
			for (int i = 0; i < tps_result_str.length; i++) {
				double throughput = Double.valueOf(tps_result_str[i]);
				if (throughput == 0)
					continue;
				tps_result = Utilities.pushResult(tps_result, throughput);
			}
			
			// pw.close();
			// br.close();
			os.close();
			is.close();
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
