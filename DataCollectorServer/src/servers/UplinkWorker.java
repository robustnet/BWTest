package servers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.text.SimpleDateFormat;

public class UplinkWorker extends Thread {
	private Socket client = null;
	
	private double[] tps_result;
	public int size = 0;
	public long testStartTime = 0; //test start time, used to determine slow start period
	public long startTime = 0; //start time of this period to calculate throughput
	public final static long SAMPLE_PERIOD = 1000; 
	public final static long SLOW_START_PERIOD = 5000; //empirically set to 5 seconds 
	
	public UplinkWorker() {
		tps_result = new double[]{};
		testStartTime = System.currentTimeMillis();
	}
	
	public void setSocket(Socket client) {
		this.client = client;
	}
	
	public void run() {
		try {
			client.setSoTimeout(Definition.RECV_TIMEOUT);
			client.setTcpNoDelay(true);

			PrintWriter pw = new PrintWriter(client.getOutputStream(), true);
			BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd:HH:mm:ss:SSS");
			long threadId = this.getId();
			String startDate = sDateFormat.format(new Date()).toString();
			System.out.println("[" + startDate + "]" + " Uplink worker <" + threadId + "> Thread starts");

			while (true) {
				String str = br.readLine();
				if (str != null) {
					if (str.equals(Definition.UPLINK_FINISH_MSG))
						break;
					updateSize(str.length());
				}
				else break;
			}

			if (tps_result.length > 0) {
				String result = "";
				for (int i = 0; i < tps_result.length - 1; i++)
					result += tps_result[i] + "#";
				result += tps_result[tps_result.length - 1];
				pw.println(result);
				pw.flush();
			}			
				
			pw.close();
			br.close();
			client.close();
			String endDate = sDateFormat.format(new Date()).toString();
			System.out.println("[" + endDate + "]" + " Uplink worker <" + threadId + "> Thread ends");

		} catch (IOException e) {
			e.printStackTrace();
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
