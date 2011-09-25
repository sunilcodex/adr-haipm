package annam.lines;

import java.util.Formatter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import annam.game.timer;

public class Lines extends Activity {
    /** Called when the activity is first created. */
	
	private LinesView mView;
	private GameTimer mTimer;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mView = (LinesView) findViewById(R.id.lines);
        
        ImageButton undoButton = (ImageButton) findViewById(R.id.ButtonUndo);
        
        mTimer = new GameTimer();
        mTimer.start();
        undoButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            	setResult(RESULT_OK);
                mView.undo();
            }

        });
        //mView.setTextView((TextView) findViewById(R.id.text));

    }
    private StringBuilder mTimeText = new StringBuilder();;
	private Formatter mGameTimeFormatter = new Formatter(mTimeText);
    void updateTime(){
    	mTimeText.setLength(0);
    	mGameTimeFormatter.format("%02d:%02d", mTimer.getTime() / 60000, mTimer.getTime()/ 1000 % 60);
    	setTitle(mTimeText.toString());
    }
    private final class GameTimer extends timer {
		
		GameTimer() {
    		super(5000);
    	}
		
    	@Override
		protected boolean step(int count, long time) {
    		updateTime();
            // Run until explicitly stopped.
            return false;
        }
        
	}
}