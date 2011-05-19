package annam.game.table;

import java.util.Random;


public class TableGame {
	protected final int TABLE_GAME_NUM_NEXT_ITEMS = 3;
	protected final int TABLE_GAME_MOVING = 2;
	protected final int TABLE_GAME_MOVING_FROM = 0;
	protected final int TABLE_GAME_MOVING_TO = 1;
	
	/** 
	 * The generator random item for table contain
	 */
	private static Random mGenerator;
	/** 
	 * Width of table contain
	 */
	protected int mWidth;
	/** 
	 * Height of table contain
	 */
	protected int mHeight;
	/** 
	 * Max instants of table items's value
	 */
	protected int mNumInstant;
	/** 
	 * Current table contain
	 */
	protected int mContaint[][]; 
	
	/** 
	 * List items will be next on table
	 */
	protected TableItem mNextItems[];
	
	/** 
	 * List items will be next on table
	 */
	protected TableItem mLastGenItems[];
	
	/** 
	 * Last moving coordinate 
	 */
	protected TableItem mLastMoveItems[];
	
	/** 
	 * Finder trace use for find correct move for one item
	 */
	protected static boolean mFinderTrace[][];
	/** 
	 * Number of non zero items in table contain
	 */
	protected int mNonZeroCount;
	/** 
	 * Max number items can store in table contain
	 */
	protected int mMaxCount;
	/** 
	 * Flag to verify table contain can be restore
	 */
	protected boolean mCanRestore;
	/**
     * Initial TableGame
     * 
     * @param width of table
     * @param height of table
     * @param instant max number instant of table value
     * 
     */
	public TableGame(int width, int height, int instant){
		mWidth = width;
		mHeight = height;
		mNumInstant = instant;
		mNonZeroCount = 0;
		mCanRestore = false;
		mMaxCount = mWidth * mHeight;
		mContaint = new int[mWidth][mHeight];
		mFinderTrace = new boolean[mWidth][mHeight];
		
		mNextItems = new TableItem[TABLE_GAME_NUM_NEXT_ITEMS];
		mLastGenItems = new TableItem[TABLE_GAME_NUM_NEXT_ITEMS];
		mLastMoveItems = new TableItem[TABLE_GAME_NUM_NEXT_ITEMS];
		for (int i = 0; i< TABLE_GAME_NUM_NEXT_ITEMS; i++)
		{
			mNextItems[i] = new TableItem();
			mLastGenItems[i] = new TableItem();
			mLastMoveItems[i] = new TableItem();
		}
		mGenerator = new Random();
	}
	/**
     * Generate random all values in TableGame
     * 
     */
	public boolean RandomsTableContent(){
		for (int i = 0; i<mWidth; i++)
			for (int j = 0; j<mHeight; j++)
				mContaint[i][j] = mGenerator.nextInt(mNumInstant );		
		mNonZeroCount = mWidth * mHeight;
		return true;
	}
	/**
     * Generate random some values in TableGame and reset another to zero
     * @param count number of values to generate
     * 
     */
	public boolean RandomsTableContent(int count){
		
		ResetTableContent();
		int x,y,v;
		while (count >0 && mNonZeroCount <= mMaxCount)
		{
			x = mGenerator.nextInt(mWidth);
			y = mGenerator.nextInt(mWidth);
			if (mContaint[x][y] == 0)
			{
				v = mGenerator.nextInt(mNumInstant-1)+1;
				mContaint[x][y]= v;
				count--;
				mNonZeroCount++;
			}
		}
		return true;
	}
	/**
     * Generate random some values in TableGame 
     * @param count number of values to generate
     * 
     */
	public boolean NextTableContent(int count){
		
		int x,y,v;
		//TODO: this code run not good in end game and not verify game over exception 
		while (count >0 && (mNonZeroCount + count) < mMaxCount)
		{
			x = mGenerator.nextInt(mWidth);
			y = mGenerator.nextInt(mHeight);
			//Verify the table contain at (x, y) not equal 0 
			if (mContaint[x][y] == 0)
			{
				count--;
				v = mGenerator.nextInt(mNumInstant-1)+1;
				//mContaint[x][y]= v;
				mNextItems[count].x = x;
				mNextItems[count].y = y;
				mNextItems[count].value = v;
			}
		}
		return true;
	}
	
	/**
     * Restore TableContain
     * remove last additional items and restore last moving
     * 
     * @param count number generate items
     * @author HaiPM
     * Restore last moving
     * Restore last additional items
     * Re-generate next items
     */
	public boolean RestoreTableContent(int count){
		if (mCanRestore == true)
		{
			//restore last moving
			mContaint[mLastMoveItems[TABLE_GAME_MOVING_FROM].x][mLastMoveItems[TABLE_GAME_MOVING_FROM].y] = mContaint[mLastMoveItems[TABLE_GAME_MOVING_TO].x][mLastMoveItems[TABLE_GAME_MOVING_TO].y];
			mContaint[mLastMoveItems[TABLE_GAME_MOVING_TO].x][mLastMoveItems[TABLE_GAME_MOVING_TO].y] = 0;
			//restore last additional items
			for (int i = 0; i< count; i++)
			{
				mContaint[mLastGenItems[i].x][mLastGenItems[i].y] = 0;
				mNonZeroCount--;
			}
			//regenerate next items 
			NextTableContent(count); 
			
			//just restore one time, turn of this flag
			mCanRestore = false;
			
			return true;
		}
		else
			return false;
	}
	/**
     * Update NextItems to TableContain
     * @param count number of values to generate
     * 
     * @author HaiPM
     *  Update mNextItems into table mContaint
     */
	public boolean UpdateNextTableContent(int count){
		int x=0,y=0;
		x = mGenerator.nextInt(mWidth);
		y = mGenerator.nextInt(mWidth);
		for (int i = 0; i< count; i++)
		{
			//Not generate over table contain
			if (mNonZeroCount >= mMaxCount) return false;
			
			if (mContaint[mNextItems[i].x][mNextItems[i].y] == 0)
			{//item at ([mNextItems[i].x][mNextItems[i].y]) is zero
				mContaint[mNextItems[i].x][mNextItems[i].y] = mNextItems[i].value;
				
			}
			else
			{//item at ([mNextItems[i].x][mNextItems[i].y]) is non zero, re-generate another items
				//TODO: this code run not good in end game and not verify game over exception 
				while (mContaint[x][y] != 0)
				{
					x = mGenerator.nextInt(mWidth);
					y = mGenerator.nextInt(mWidth);
				}
				mNextItems[i].x = x;
				mNextItems[i].y = y;
				//still reuse period generate vale
				mContaint[x][y] = mNextItems[i].value;	
			}
			mLastGenItems[i].x = mNextItems[i].x;
			mLastGenItems[i].y = mNextItems[i].y;
			mLastGenItems[i].value = mNextItems[i].value;
			//Increase number of non zero items in table contain 
			//TODO: not verify game over yet
			mNonZeroCount++;
		}
		return true;
	}
	/**
     * Get value on Table game at (x, y) position 
     * @param x 
     * @param y
     * 
     */
	public int GetInstantAt(int x, int y){
		if (x<mWidth && y<mHeight)
			return mContaint[x][y];
		return 0;
	}
	/**
     * Set value on Table game at (x, y) position 
     * @param x of position
     * @param y of position
     * @param value at position
     * 
     */
	public boolean SetInstantAt(int x, int y, int value){
		if (x<mWidth && y<mHeight && value < mNumInstant)
		{
			mContaint[x][y] = value;
			return true;
		}
		return false;
	}
	/**
     * Reset all value on TableGame to zero
     * 
     */
	public boolean ResetTableContent(){
		mGenerator = new Random();
		for (int i = 0; i<mWidth; i++)
			for (int j = 0; j<mHeight; j++)
				mContaint[i][j] = 0;	
		mNonZeroCount = 0;
		return true;
	}
	/**
     * Check lines find the way is correct
     * @param fromX of from position
     * @param fromY of from position
     * @param toX of to position
     * @param toY of to position
     * 
     */
	public boolean IsExitTheWay(int fromX, int fromY, int toX, int toY){
		/*Don't move*/
		boolean result = false;
		if (fromX == toX && fromY == toY)
			result = false;
		else
		{
			//Reset trace table
			for (int i = 0; i< mWidth; i++)
				for (int j = 0; j< mHeight; j++)
					mFinderTrace[i][j] = false;
			
			result = FindTheWay(fromX, fromY, toX, toY);
		}
		if (result == true)
		{
			//If exist the way, store the moving trace
			mLastMoveItems[TABLE_GAME_MOVING_FROM].x = fromX;
			mLastMoveItems[TABLE_GAME_MOVING_FROM].y = fromY;
			mLastMoveItems[TABLE_GAME_MOVING_TO].x = toX;
			mLastMoveItems[TABLE_GAME_MOVING_TO].y = toY;
			//Turn on this flag to remark ability restoring 
			mCanRestore = true;
		}
		return result;
	}
	/**
     * Find the way from one position to another position
     * @param fromX of from position
     * @param fromY of from position
     * @param toX of to position
     * @param toY of to position
     * 
     */
	public boolean FindTheWay(int fromX, int fromY, int toX, int toY){
		boolean result = false;
		//found the way :)
		if (fromX == toX && fromY == toY)
			return true;
		//not find yet
		int xMoveRight = fromX + 1;
		int xMoveLeft = fromX - 1;
		int yMoveDown = fromY + 1;
		int yMoveUp = fromY - 1;
		//try on right hand if can do
		if (	xMoveRight < mWidth && 
				mContaint[xMoveRight][fromY] == 0 && 
				mFinderTrace[xMoveRight][fromY] == false)
		{
			mFinderTrace[xMoveRight][fromY] = true;
			result |= FindTheWay(xMoveRight, fromY, toX, toY);
			if (result == true)return result;
		}
		//try on down if can do
		if (	yMoveDown < mHeight &&
				mContaint[fromX][yMoveDown] == 0 &&
				mFinderTrace[fromX][yMoveDown] == false)
		{
			mFinderTrace[fromX][yMoveDown] = true;
			result |= FindTheWay(fromX, yMoveDown, toX, toY);
			if (result == true)return result;
		}
		//try on upper if can do
		if (	yMoveUp >= 0 &&
				mContaint[fromX][yMoveUp] == 0 &&
				mFinderTrace[fromX][yMoveUp] == false)
		{
			mFinderTrace[fromX][yMoveUp] = true;
			result |= FindTheWay(fromX, yMoveUp, toX, toY);
			if (result == true)return result;
		}
		//try on left hand if can do
		if (	xMoveLeft >= 0 && 
				mContaint[xMoveLeft][fromY] == 0 && 
				mFinderTrace[xMoveLeft][fromY] == false)
		{
			mFinderTrace[xMoveLeft][fromY] = true;
			result |= FindTheWay(xMoveLeft, fromY, toX, toY);
			if (result == true)return result;
		}
		//not found
		return false;
	}
	/**
     * Check lines valid 
     * verify all position follow 4 drives
     *        .  .  .  .
     *      . . .
     *    .   .   .
     *  .     .     .
     * @param validNum Number of same item valid on a line
     * 
     */
	public boolean ValidLines(int validNum){
		//assure that can't find any valid lines
		boolean result = false;
		
		int i,j,k;
		
		//clear the trace finder
		for (i=0; i<mWidth; i++)
			for (j=0; j<mHeight; j++)
				mFinderTrace[i][j] = false;
		
		for (i=0; i<mWidth; i++)
			for (j=0; j<mHeight; j++)
				if(mContaint[i][j] !=0)
				{
					
					int temp = 0;
					temp = ValidToRight(i, j, mContaint[i][j]);
					if( temp >= validNum)
					{
						result = true;
						for (k=0; k< temp; k++)
							mFinderTrace[i+k][j] = true;
					}
					temp = ValidToRightDown(i, j, mContaint[i][j]);
					if( temp >= validNum)
					{
						result = true;
						for (k=0; k< temp; k++)
							mFinderTrace[i+k][j+k] = true;
					}
					
					temp = ValidToDownLeft(i, j, mContaint[i][j]);
					if( temp >= validNum)
					{
						result = true;
						for (k=0; k< temp; k++)
							mFinderTrace[i-k][j+k] = true;
					}
					
					temp = ValidToDown(i, j, mContaint[i][j]);
					if( temp >= validNum)
					{
						result = true;
						for (k=0; k< temp; k++)
							mFinderTrace[i][j+k] = true;
					}
				}
		return result;
	}
	
	
	private int ValidToRight(int fromX, int fromY, int value){
		if ((fromX + 1) < mWidth && mContaint[fromX+1][fromY] == value)
			return ValidToRight(fromX+1, fromY, value)+1;
		else
			return 1;
	}
	
	
	private int ValidToRightDown(int fromX, int fromY, int value){
		if ((fromX + 1) < mWidth && (fromY+1) < mHeight && mContaint[fromX+1][fromY+1] == value)
			return ValidToRightDown(fromX+1, fromY+1, value)+1;
		else
			return 1;
	}
	private int ValidToDown(int fromX, int fromY, int value){
		if ((fromY + 1) < mHeight && mContaint[fromX][fromY+1] == value)
			return ValidToDown(fromX, fromY+1, value)+1;
		else
			return 1;
	}
	private int ValidToDownLeft(int fromX, int fromY, int value){
		if ((fromX - 1) >= 0 && (fromY+1) < mHeight && mContaint[fromX-1][fromY+1] == value)
			return ValidToDownLeft(fromX-1, fromY+1, value)+1;
		else
			return 1;
	}
	/**
     * Clear valid line
     *  
     */
	public int ClearValidLines(){
		int result = 0;
		for (int i=0; i<mWidth; i++)
			for (int j=0; j<mHeight; j++)
				if (mFinderTrace[i][j] == true)
				{
					mContaint[i][j] = 0;
					mFinderTrace[i][j] = false;
					mNonZeroCount--;
					result++;
				}
		//After clear, remark that table can't restore 
		if (result > 0) mCanRestore = false;
		return result;
	}
	public TableItem GetNextItem(int index){
		return mNextItems[index];
	}
	public TableItem GetLastItem(int index){
		return mLastMoveItems[index];
	}
}
