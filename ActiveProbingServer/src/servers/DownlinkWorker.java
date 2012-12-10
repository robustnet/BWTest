package servers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.text.SimpleDateFormat;

public class DownlinkWorker extends Thread {
	private Socket client = null;
	
	public void setSocket(Socket client) {
		this.client = client;
	}
	
	public void run() {
		try {
			client.setSoTimeout(Definition.RECV_TIMEOUT);
			//PrintWriter pw = new PrintWriter(client.getOutputStream(), true);
			OutputStream os = client.getOutputStream();
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMdd:HH:mm:ss:SSS");
			long threadId = this.getId();
			String startDate = sDateFormat.format(new Date()).toString();
			System.out.println("[" + startDate + "]" + " Downlink worker <" + threadId + "> Thread starts");

			long start = System.currentTimeMillis();
			long end = System.currentTimeMillis();
			long cur = System.currentTimeMillis();
			int batch = 0;
			byte [] buffer = Utilities.genRandomString(Definition.THROUGHPUT_DOWN_SEGMENT_SIZE).getBytes();
			while(end - start < Definition.DURATION_IPERF_MILLISECONDS && cur - start < Definition.DURATION_IPERF_MILLISECONDS) {
				//pw.println(Utilities.genRandomString(Definition.THROUGHPUT_DOWN_SEGMENT_SIZE)); //2600 larger than MTU
				//pw.flush();
				
				os.write(buffer);
				os.flush();
				batch++;
				if(batch % 50 == 0){
					end = System.currentTimeMillis();					
				}
				cur = System.currentTimeMillis();
			}
			//pw.close();
			os.close();
			client.close();
			String endDate = sDateFormat.format(new Date()).toString();
			System.out.println("[" + endDate + "]" + " Downlink worker <" + threadId + "> Thread ends");
		} catch (IOException e) {
	    	System.out.println("Server failed: port <" + Definition.PORT_DOWNLINK + ">");
	    	return;
	    }
	}
}
