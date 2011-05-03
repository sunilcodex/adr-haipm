package annam.lines;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class LinesView extends View {
	private final Paint mPaint = new Paint();
	private Paint mLinePaint = new Paint();;
	private float mCellWidth;
	private float mCellHeight;
	
	private final long mMoveDelay = 1;
	
	private long mLastMove;
	private boolean mIsRunning = true;
	
	private TextView mStatusText;
	private static int mItemSize;
	private static int mItemPosition;
	private static int mPositionMove;
	
	private Bitmap mBitmapList[];
	public LinesView(Context context, AttributeSet attrs){
		super(context, attrs);
		setFocusable(true);
		Resources r = this.getContext().getResources();
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ItemView);
		mItemSize = a.getInt(R.styleable.ItemView_ItemSize, 12);
		mItemPosition = 0;
		setLineColor(Color.BLACK);
		setBackgroundColor(Color.WHITE);
		
		mBitmapList = new Bitmap[3];
		
		Drawable draw = r.getDrawable(R.drawable.red);
		mBitmapList[0] = Bitmap.createBitmap(mItemSize*3, mItemSize*3, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mBitmapList[0]);
		draw.setBounds(0, 0, mItemSize*3, mItemSize*3);
		draw.draw(canvas);	
		mPositionMove = 5;
		update();
	}
	public LinesView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
       
	}
	public boolean onTouchEvent(MotionEvent event) {
		mIsRunning = !mIsRunning;
		return true;
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		mCellWidth = (width - getPaddingLeft() - getPaddingRight()) / 9.0f; 
        mCellHeight = (height - getPaddingTop() - getPaddingBottom()) / 9.0f;
        setMeasuredDimension(width, height);
	}
	public void setTextView(TextView view) {
		mStatusText = view;
		
		
		mStatusText.setText(R.string.app_name);
		mStatusText.setVisibility(VISIBLE);
	}
	
	public int getLineColor() {
		return  mLinePaint.getColor();
	}
	
	public void setLineColor(int color) {
		mLinePaint.setColor(color);
	}
	
	public void ShowLines(){
		mStatusText.setText(R.string.app_name);
		mStatusText.setVisibility(VISIBLE);
	}
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int paddingLeft = 2;
		int paddingTop = 2;
		int width = getWidth();
		int height = getHeight();
		
        
        // draw vertical lines
		for (int c=0; c <= 9; c++) { 
			float x = (c * mCellWidth) + paddingLeft;
			canvas.drawLine(x, paddingTop, x, height, mLinePaint);
		}
		
		// draw horizontal lines
		for (int r=0; r <= 9; r++) { 
			float y = r * mCellHeight + paddingTop;
			canvas.drawLine(paddingLeft, y, width, y, mLinePaint); 
		}
		 
		canvas.drawBitmap(	mBitmapList[0], 
				mItemPosition,
				mItemPosition,
        		mPaint);
		
	}
	public void update() {
		long now = System.currentTimeMillis();
		if (now - mLastMove > mMoveDelay && mIsRunning)
		{
			mItemPosition += mPositionMove;
			if (mItemPosition < 0 ||  (mItemPosition + mItemSize*3) > this.getWidth())
				mPositionMove = 0 - mPositionMove;
			
			mLastMove = now;
		}
		mRedrawHandler.sleep(mMoveDelay);
	}
	private RefreshHandler mRedrawHandler = new RefreshHandler();

    class RefreshHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            LinesView.this.update();
            LinesView.this.invalidate();
        }

        public void sleep(long delayMillis) {
        	this.removeMessages(0);
            sendMessageDelayed(obtainMessage(0), delayMillis);
        }
    };
}
