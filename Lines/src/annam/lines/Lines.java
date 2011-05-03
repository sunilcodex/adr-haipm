package annam.lines;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Lines extends Activity {
    /** Called when the activity is first created. */
	
	private LinesView mView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mView = (LinesView) findViewById(R.id.lines);
        
        mView.setTextView((TextView) findViewById(R.id.text));

    }
}