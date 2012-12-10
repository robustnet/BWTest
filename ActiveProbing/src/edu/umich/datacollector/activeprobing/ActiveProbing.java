/**
 * @author Yuanyuan Zhou
 * @date Oct 30, 2012
 * @organization University of Michigan, Ann Arbor
 */

package edu.umich.datacollector.activeprobing;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ActiveProbing extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_probing);
        Button testButton = (Button)findViewById(R.id.test_button);
        testButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
		        NewTestThread newTestThread = new NewTestThread();
		        newTestThread.start();
			}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_active_probing, menu);
        return true;
    }
    

}
