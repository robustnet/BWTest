/**
 * @author Yuanyuan Zhou
 * @date Oct 30, 2012
 * @organization University of Michigan, Ann Arbor
 */

package edu.umich.datacollector.activeprobing;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

public class RTT {
	
	private double[] rtts;
	public RTT() {
		rtts = new double[]{};
	}
	
	public void runTest() {
		for(int i = 1; i <= 16 ; i++){
			rtts = Utilities.pushResult(rtts, unitTest(Definition.SERVER_NAME));
		}
		Utilities.writeToSDCard("MLAB_RTT:<median:" + Utilities.roundDouble(Utilities.getMedian(rtts)) + 
								"><max:" + Utilities.roundDouble(Utilities.getMax(rtts)) + 
								"><min:" + Utilities.roundDouble(Utilities.getMin(rtts)) +
								"><stddev:" + Utilities.roundDouble(Utilities.getStandardDeviation(rtts)) +
								"><sample:" + rtts.length + ">;\n", Definition.RESULT_FILENAME);
		
	}
	/**
	 * 
	 * @param host
	 * @return rtt of TCP Handshake in milliseconds
	 */
	public static long unitTest(String host){
		long rtt = 0;

		long start, end;
		Socket tcpSocket = new Socket();

		try {
			tcpSocket.setSoTimeout(Definition.TCP_TIMEOUT_IN_MILLI);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		SocketAddress remoteAddr = new InetSocketAddress(host, Definition.PORT_UPLINK);

		start = System.currentTimeMillis();
		try {
			tcpSocket.connect(remoteAddr, Definition.TCP_TIMEOUT_IN_MILLI);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//test connect time
		end = System.currentTimeMillis();

		try {
			tcpSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		rtt = end - start;
		return rtt;
	}
	
	
}
