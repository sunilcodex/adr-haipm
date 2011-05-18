package annam.lines;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Lines extends Activity {
    /** Called when the activity is first created. */
	
	private LinesView mView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mView = (LinesView) findViewById(R.id.lines);
        Button undoButton = (Button) findViewById(R.id.ButtonUndo);
        
        undoButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	setResult(RESULT_OK);
                mView.undo();
            }

        });
        //mView.setTextView((TextView) findViewById(R.id.text));

    }
}