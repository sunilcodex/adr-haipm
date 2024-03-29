package annam.lines;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import annam.game.table.TableGame;
import annam.game.table.TableItem;

public class LinesView extends View {
	private final Paint mPaint = new Paint();
	private Paint mLinePaint = new Paint();
	private Paint mGameScorePaint = new Paint();
	private Paint mNextPaint = new Paint();
	private Paint mPaintStaticsBacground = new Paint();
	
	private Resources mResources;
	private int mColorStaticsBackground;
	private int mColorStaticsText;
	
	//Game constant
	private final float ANLINES_TABLE_SIZE = 9.0f;
	private final int ANLINES_PADDING_CELL = 1;
	private final int ANLINES_MAX_INSTANT = 8;
	private final int ANLINES_START_GAME_NUMBER = 5;
	private final int ANLINES_NEXT_NUMBER = 3;
	private final int ANLINES_VALID_NUMBER = 5;
	//Game running
	private boolean mIsSelected = false;
	private boolean mIsTobeClear = false;
	private boolean mIsSelectedSmall = true;
	private int mSelectCellX = -1;
	private int mSelectCellY = -1;
	private int mDestinationCellX = -1;
	private int mDestinationCellY = -1;
	private boolean mIsMoving = false;
	
	//View game setting
	private TableGame mTalbeGame;
	private float mTableSize;
	private float mCellWidth;
	private float mCellHeight;
	private float mStatisticsHeight;
		
	private final long mMoveDelay = 5000; 
	
	private TextView mStatusText;
	private static float mItemSize;
	private static float mItemSmallHeight;
	private static float mItemNextHeight;
	
	private TextView mScoreView;
	private Bitmap mBitmapList[];
	private Bitmap mBitmapNextList[];
	private Bitmap mSelectedBitmapList[];
	
	RectF mRectStatics;
	//private Button mButtonUndo;
	
	public LinesView(Context context, AttributeSet attrs){
		super(context, attrs);
		setFocusable(true);
				
		setLineColor(Color.BLACK);
		setBackgroundColor(Color.WHITE);
		
		mBitmapList = new Bitmap[ANLINES_MAX_INSTANT];
		mBitmapNextList = new Bitmap[ANLINES_MAX_INSTANT];
		mSelectedBitmapList = new Bitmap[ANLINES_MAX_INSTANT];
		
		mTalbeGame = new TableGame((int)ANLINES_TABLE_SIZE, (int)ANLINES_TABLE_SIZE, ANLINES_MAX_INSTANT);
		mTalbeGame.RandomsTableContent(ANLINES_START_GAME_NUMBER);
		mTalbeGame.NextTableContent(ANLINES_NEXT_NUMBER);
		
		mResources = this.getContext().getResources();
		
		mColorStaticsBackground = mResources.getColor(R.color.COLOR_STATICS_BACKGROUND);
		mPaintStaticsBacground.setColor(mColorStaticsBackground);
		
		mColorStaticsText = mResources.getColor(R.color.COLOR_STATICS_TEXT);
		mGameScorePaint.setColor(mColorStaticsText);
		
		update();
	}

	
	public void undo(){
		mTalbeGame.RestoreTableContent(ANLINES_NEXT_NUMBER);
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int)event.getX();
		int y = (int)event.getY();
		if (event.getAction() == MotionEvent.ACTION_UP)
		{
			if (setSelectedCell(x, y)){
				mIsSelected = true;
				mIsSelectedSmall = true;
			}
		}
		
		return true;
	}
	protected boolean setSelectedCell(int x, int y){
		int xSelected=-1;
		int ySelected = -1;
		
		xSelected = x / (int)mCellWidth;
		ySelected = y / (int)mCellHeight;
		if (xSelected > 8) xSelected = 8;
		if (ySelected > 8) ySelected = 8;
		//Touch is indication moving
		if(mTalbeGame.GetInstantAt(xSelected, ySelected) == 0)
		{
			if (mIsSelected == true && mIsMoving == false)
			{
				if (mTalbeGame.IsExitTheWay(mSelectCellX, mSelectCellY, xSelected, ySelected))
				{
					mDestinationCellX = xSelected;
					mDestinationCellY = ySelected;
					mIsMoving = true;
					mIsSelected = false;
					
				}
				else
				{
					//TODO notify moving error "incorrect moving"
				}
			}
			return false;
			
		}
		//Touch is select item
		else
		{
			mSelectCellX = xSelected;
			mSelectCellY = ySelected;
			return true;
		}
	}
	
	protected void measureViewSize(int widthMeasureSpec, int heightMeasureSpec){
		//measure table size
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
				
		if (width > height) {
			mTableSize = height ; 
			mStatisticsHeight = (width - height)/2; 
		}
		else {
			mTableSize = width;
			mStatisticsHeight = (height - width)/2;
		}
		
		//measure cell size
		mCellWidth = (mTableSize - getPaddingLeft() - getPaddingRight()) / ANLINES_TABLE_SIZE; 
        mCellHeight = (mTableSize - getPaddingTop() - getPaddingBottom()) / ANLINES_TABLE_SIZE;
        
        mItemSize = mCellWidth - ANLINES_PADDING_CELL;
        mItemSmallHeight = mItemSize*3/4;
        mItemNextHeight = mItemSize/2;
        
        //measure BALL size
        loadImage();
        
        //measure Game statistics
        mRectStatics = new RectF(0, mTableSize, mTableSize, mTableSize+mStatisticsHeight);
        
        
	}
	/**
     * Function to set the specified Drawable as the tile for a particular
     * integer key.
     * 
     * @param key
     * @param tile
     * @author HaiPM
     * Load current bitmap
     * Load selected bitmap
     * Load next bitmap
     */
    public void loadImage() {
    	    	
    	Resources r = this.getContext().getResources();
    	for (int i =0; i<ANLINES_MAX_INSTANT; i++)
    	{
    		//Load current bitmap
	    	Drawable draw = r.getDrawable((R.drawable.circle_0)+i);
	    	    	
	        Bitmap bitmap = Bitmap.createBitmap((int)mItemSize, (int)mItemSize, Bitmap.Config.ARGB_8888);
	        Canvas canvas = new Canvas(bitmap);
	        draw.setBounds(ANLINES_PADDING_CELL, ANLINES_PADDING_CELL, (int)mItemSize, (int)mItemSize);
	        draw.draw(canvas); 
	        
	        mBitmapList[i] = bitmap; 
	        
	        //Load selected bitmap
	        Drawable selectedDraw = r.getDrawable((R.drawable.circle_0)+i);
	        
	        Bitmap selectedBitmap = Bitmap.createBitmap((int)mItemSize, (int)mItemSmallHeight, Bitmap.Config.ARGB_8888);
	        Canvas selectedCanvas = new Canvas(selectedBitmap);
	        selectedDraw.setBounds(ANLINES_PADDING_CELL, ANLINES_PADDING_CELL, (int)mItemSize, (int)mItemSmallHeight);
	        selectedDraw.draw(selectedCanvas); 
	        
	        mSelectedBitmapList[i] = selectedBitmap;
	        //Load next bitmap
	        
	        Drawable NextDraw = r.getDrawable((R.drawable.circle_0)+i);
	        
	        Bitmap NextBitmap = Bitmap.createBitmap((int)mItemNextHeight, (int)mItemNextHeight, Bitmap.Config.ARGB_8888);
	        Canvas NextCanvas = new Canvas(NextBitmap);
	        NextDraw.setBounds(ANLINES_PADDING_CELL, ANLINES_PADDING_CELL, (int)mItemNextHeight, (int)mItemNextHeight);
	        NextDraw.draw(NextCanvas); 
	        
	        mBitmapNextList[i] = NextBitmap;
    	}
    	
    }
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		measureViewSize(widthMeasureSpec, heightMeasureSpec);
		
		setMeasuredDimension((int)mTableSize, (int)(mTableSize + mStatisticsHeight));
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
	/**
     * Draw Lines game statistics
     * @param canvas to draw
     * @author HaiPM
     * Draw game score
     * Draw game time
     * Draw next items view
     */
	
	private void DrawGameStatistics(Canvas canvas){
		//TODO on coding
		
		//1. Draw background statistics
		canvas.drawRect(mRectStatics, mPaintStaticsBacground);
		
		//2. Draw score
		
		mGameScorePaint.setTextSize(mStatisticsHeight/2);
		Long mGameScore = (long) 99999;
		canvas.drawText(mGameScore.toString(), 0, mTableSize + mStatisticsHeight, mGameScorePaint);
		//3. Draw next items
	}
	/**
     * Draw Lines table contain
     * @param canvas to draw
     * @author HaiPM
     * Draw selected item
     * Draw current items
     * Draw next items
     */
	
	private void DrawLinesTable(Canvas canvas){
		for (int i = 0; i<(int)ANLINES_TABLE_SIZE; i++)
			for (int j = 0; j<(int)ANLINES_TABLE_SIZE; j++)
			{
				int color = mTalbeGame.GetInstantAt(i, j);
				if (color>0 )
				{
					if(		mIsSelected == true && 
							mIsSelectedSmall == true && 
							i == mSelectCellX && 
							j == mSelectCellY)
						//Draw selected item
						canvas.drawBitmap(	mSelectedBitmapList[color], 
							0 + ANLINES_PADDING_CELL + i * mCellWidth,
							0 + ANLINES_PADDING_CELL + j * mCellHeight + mCellHeight - mItemSmallHeight,
			        		mPaint);
					else //Draw current items
						canvas.drawBitmap(	mBitmapList[color], 
								0 + ANLINES_PADDING_CELL + i * mCellWidth,
								0 + ANLINES_PADDING_CELL + j * mCellHeight,
				        		mPaint);
				}
			}
		//Draw next items 
				
		for (int k = 0; k < ANLINES_NEXT_NUMBER; k++)
		{ 
			TableItem temp = new TableItem(); 
			temp = mTalbeGame.GetNextItem(k);
			canvas.drawBitmap(	mBitmapNextList[temp.value], 
					ANLINES_PADDING_CELL + mCellWidth/5 + temp.x * mCellWidth,
					ANLINES_PADDING_CELL + mCellHeight/5 + temp.y * mCellHeight,
					mNextPaint);
			
		}
	}
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int paddingLeft = 0;
		int paddingTop = 0;
		        
        // draw vertical lines
		for (int c=0; c <= 9; c++) { 
			float x = (c * mCellWidth) + paddingLeft;
			canvas.drawLine(x, paddingTop, x, mTableSize, mLinePaint);
		}
		
		// draw horizontal lines
		for (int r=0; r <= 9; r++) { 
			float y = r * mCellHeight + paddingTop;
			canvas.drawLine(paddingLeft, y, mTableSize, y, mLinePaint); 
		}
		// draw lines balls
		DrawLinesTable(canvas); 
		/** draw game statistics 
		 * TODO draw time, score, next color
		 */
		DrawGameStatistics(canvas);
		
		
	}
	public void update() {
		//Selected cell animation update
		mIsSelectedSmall = !mIsSelectedSmall; 
		
		//Moving cell animation update
		if (mIsMoving == true){ 
			mTalbeGame.SetInstantAt(mDestinationCellX, mDestinationCellY, mTalbeGame.GetInstantAt(mSelectCellX, mSelectCellY));
			mTalbeGame.SetInstantAt(mSelectCellX, mSelectCellY, 0);
			mIsMoving = false;
			//Check valid lines
			if(mTalbeGame.ValidLines(ANLINES_VALID_NUMBER) == true)
			{
				mIsTobeClear = true;
				mTalbeGame.ClearValidLines();
			}
			else
			{
				mTalbeGame.UpdateNextTableContent(ANLINES_NEXT_NUMBER);
				mTalbeGame.NextTableContent(ANLINES_NEXT_NUMBER);
			}
			
		}
		if (mIsTobeClear == true){		
			this.postInvalidate();
			mIsTobeClear = false;
		}
		
		//Set delay for next update
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
