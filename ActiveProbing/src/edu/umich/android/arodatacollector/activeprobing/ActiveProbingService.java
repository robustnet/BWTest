/**
 * @author Yuanyuan Zhou
 * @date Oct 30, 2012
 * @organization University of Michigan, Ann Arbor
 */

package edu.umich.android.arodatacollector.activeprobing;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ActiveProbingService extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
    	int ret = super.onStartCommand(intent, flags, startId);
        NewTestThread newTestThread = new NewTestThread();
        newTestThread.start();
        return ret;
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    }
    
}
