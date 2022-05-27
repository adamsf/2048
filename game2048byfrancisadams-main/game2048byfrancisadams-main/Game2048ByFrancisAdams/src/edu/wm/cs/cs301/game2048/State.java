package edu.wm.cs.cs301.game2048;


/**
 * 
 * @author Francis Adams
 * derived work
 *
 */
public class State implements GameState {
	
	private int [][] board = new int[4][4]; 
	public State()
	{
		
	}
	
	public State(GameState original) {
		//if a state is passed in, it needs the information from the old state
		//to set up the board.
		//set all board values to that of original
		for (int r = 0; r < 4; r++)
		{
			for (int c = 0; c < 4; c++)
			{
				int origValue = original.getValue(c, r);
				setValue(c, r, origValue);
			}
		}
		
	}

	@Override
	public int getValue(int xCoordinate, int yCoordinate) {
		// get value at board[x][y]
		return board[xCoordinate][yCoordinate];
	}

	@Override
	public void setValue(int xCoordinate, int yCoordinate, int value) {
		// x is rows and y is columns, so set board[x][y] to value
		board[xCoordinate][yCoordinate] = value;

	}

	@Override
	public void setEmptyBoard() {
		//iterate through board and set all values to 0
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{	
				setValue(i, j, 0);
			}
		}
	}

	@Override
	public boolean addTile() {
		// if board is full, tiles cannot be added, return false
		if (isFull() == true)
		{
			return false;
		}
		//setEmptyBoard();
		//generate random coordinate from [0, 3] using math.random()
		int newX = (int)(Math.random() * 4); 
		int newY = (int)(Math.random() * 4); 
		//new tiles are either added in as 2 or 4
		//use math.random() to determine a value for the tile
		int twoOrFour = (int)(Math.random() * 2);
		int curVal = getValue(newX, newY);
		
		while( curVal > 0)
		{
			//keep grabbing new values 
			newX = (int)(Math.random() * 4);
			newY = (int)(Math.random() * 4);
			curVal = getValue(newX, newY);
			//System.out.println("(" + newX + ", " + newY + "), " + getValue(newX, newY));
		}
		
		if (twoOrFour == 0)
		{
			setValue(newX, newY, 4);
		}
		else setValue(newX, newY, 2);
		return true; 
	}

	@Override
	public boolean isFull() {
		// TODO Auto-generated method stub
		for (int i = 0; i < 4; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				// a 0 value on the board means a 2 or 4 can be added
				if (getValue(i, j) == 0)
				{
					return false;
				}
			}
		}
		//no 0's have been found, the board is full
		return true;
	}

	@Override
	public boolean canMerge() {
		//check for horizontal merges
		//only need to iterate through columns 1 and 3
		//and check the column to the left for equal values
		//use r, c for row, column in iteration
		for (int r = 0; r < 4; r++)
		{
			for (int c = 1; c < 4; c+=2)
			{
				int curVal = getValue(r, c);
				int upperVal = getValue(r, c-1); 
				if (curVal == upperVal)
				{
					return true;
				}
			}
		}
		//check for vertical merges
		//only check in rows 1 & 3 for merges
		//and look at row above for equal values
		for (int r = 1; r < 4; r+=2)
		{
			for (int c = 0; c < 4; c++)
			{
				int curVal = getValue(r, c);
				int upperVal = getValue(r-1, c); 
				if (curVal == upperVal)
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean reachedThreshold() {
		// return if the highest value on the board is 2048
		int highestValue = 0;
		for (int r = 0; r < 4; r++)
		{
			for (int c = 0; c < 4; c++)
			{
				if (getValue(c, r) > highestValue)
				{
					highestValue = getValue(c, r);
				}
			}
		}
		return highestValue >= 2048;
	}
	


	@Override
	public int left() {
		/*
		 * max 2 merges per swipe (ex. 2 2 2 2 becomes 4 4 0 0)
		 * check for merges first in each column
		 * if none exist, begin shifting tiles left until they hit the leftmost column
		 * when a merge occurs, add the new value of the merged tile to a sum variable
		 * 
		 * tricky part: row that looks like 2 0 2 0
		 * moving tile by tile will not merge these two event though they should be
		 * solution: offset variable that will search for the next nonzero value
		 * if it equals the current tile, merge
		 * if not, break
		 */
		int sum = 0;
		for (int r = 0; r < 4; r++)
		{
			for (int c = 0; c < 4; c++)
			{
				int curTile = getValue(c, r);
				if (curTile == 0) // not merging anything with 0
				{
					continue;
				}
				int offset = 1;
				while (c + offset < 4)
				{
					int nextTile = getValue(c + offset, r);
					if (nextTile == 0) //go to next adjacent tile
					{
						offset++;
						continue;
					}
					else if (nextTile != curTile && nextTile != 0) 
					{
						//zero comparison may be redundant
						break; //cannot merge, unequal nonzero tiles
					}
					else //merge 
					{
						setValue(c, r, curTile * 2);
						setValue(c + offset, r, 0);
						sum += getValue(c, r);
						break; //after merging there is no need to check for other tiles in this loop
					}
				}
			}
		}
		/*
		 * going back to the 2 2 2 2 example, after the loop above
		 * the row will look like 4 0 4 0
		 * at this point, the second 4 needs to move to column 1 from column 2
		 * 4 4 0 0
		 * use a nested while loop to achieve this
		 */
		for (int r = 0; r < 4; r++)
		{
			for (int c = 0; c < 4; c++)
			{
				int offset = 1; //will be used to find the next nonzero tile
				int curTile = getValue(c, r);
				//if curTile is nonzero, move to next tile 
				while (curTile == 0 && c + offset < 4) //no OOB exceptions
				{
					int nextTile = getValue(c + offset, r);
					if (nextTile != 0)
					{
						setValue(c, r, nextTile);
						setValue(c + offset, r, 0);
					}
					offset++;
				}
			}
		}
		//i should probably modularize this but for now will write out all 4 moves
		return sum;
	}

	@Override
	public int right() {
		int sum = 0;
		//follow similar logic to that outlined in left()
		//but this time, start at column 3 and move backwards
		//offset variable will be subtracted instead of added
		//rows can be iterated top to bottom because no merges occur on different ones
		
		for (int r = 0; r < 4; r++)
		{
			for (int c = 3; c >= 0; c--)
			{
				int curTile = getValue(c, r);
				if (curTile == 0)
				{
					continue; //still no need to merge zeroes
				}
				int offset = 1;
				while (c - offset >= 0 )
				{
					int nextTile = getValue(c - offset, r);
					if (nextTile == 0)
					{
						offset++;
						continue;
					}
					else if (nextTile != curTile && nextTile != 0)
					{
						break;
					}
					else 
					{
						setValue(c, r, curTile * 2);
						setValue(c - offset, r, 0);
						sum += getValue(c, r);
						break;
					}
				}
			}
		}
		
		//begin to shift tiles. as done in first loop,
		//iterate by row, starting with rightmost column
		for (int r = 0; r < 4; r++)
		{
			for (int c = 3; c >= 0; c--)
			{
				int curTile = getValue(c, r);
				int offset = 1;
				while (c - offset >= 0 && curTile == 0)
				{
					int nextTile = getValue(c - offset, r);
					if (nextTile != 0)
					{
						setValue(c, r, nextTile);
						setValue(c - offset, r, 0);
					}
					offset++;
				}
			}
		}
		
		return sum;
	}

	@Override
	public int down() {
		int sum = 0;
		//just like up(), start iterating by column starting at bottom row
		//column iteration order still does not matter
		for (int c = 0; c < 4; c++)
		{
			for (int r = 3; r >= 0; r--)
			{
				int curTile = getValue(c, r);
				if (curTile == 0)
				{
					continue;
				}
				int offset = 1;
				while (r - offset >= 0)
				{
					int nextTile = getValue(c, r - offset);
					if (nextTile == 0)
					{
						offset++;
						continue;
					}
					else if (nextTile != curTile && nextTile != 0)
					{
						break; //cannot merge unequal adjacent tiles
					}
					else //equal tiles means a merge must occur
					{
						setValue(c, r, curTile * 2);
						setValue(c, r - offset, 0);
						sum += getValue(c, r);
						break;
					}
					
				}
			}
		}
		
		//all tiles have been merged as necessary, begin to shift to fill in gaps
		for (int c = 0; c < 4; c++)
		{
			for (int r = 3; r >= 0; r--)
			{
				int offset = 1;
				int curTile = getValue(c, r);
				while (curTile == 0 && r - offset >= 0)
				{
					int nextTile = getValue(c, r - offset);
					if (nextTile != 0)
					{
						setValue(c, r, nextTile);
						setValue(c, r - offset, 0);
					}
					offset++;
				}
			}
		}
		return sum;
	}

	@Override
	public int up() 
	{
		int sum = 0;
		//start in column 0 and apply logic written out in left()
		//iteration order of columns will not matter 
		//row iteration order will
		
		//begin merging process
		for (int c = 0; c < 4; c++)
		{
			for (int r = 0; r < 4; r++)
			{
				int curTile = getValue(c, r);
				if (curTile == 0)
				{
					continue;
				}
				int offset = 1; //add to row r
				while (r + offset < 4)
				{
					int nextTile = getValue(c, r + offset);
					if (nextTile == 0)
					{
						offset++;
						continue;
					}
					else if (nextTile != curTile && nextTile != 0)
					{
						break;
					}
					else 
					{
						setValue(c, r, curTile * 2);
						setValue(c, r + offset, 0);
						sum += getValue(c, r);
						break;
					}
				}
			}
		}
		
		//now that all tiles have been merged, use shifting process
		//in left() to move tiles to correct position
		//move column by column to move rows upward
		for (int c = 0; c < 4; c++)
		{
			for (int r = 0; r < 4; r++)
			{
				int offset = 1;
				int curTile = getValue(c, r);
				while (curTile == 0 && r + offset < 4) //no OOB exceptions
				{
					int nextTile = getValue(c, r + offset);
					if (nextTile != 0)
					{
						setValue(c, r, nextTile);
						setValue(c, r + offset, 0);
					}
					offset++;
				}
			}
		}
		return sum;
	}
	
	
	
	/**
	 * Determine if two states of the 2048 board are equal. Two states
	 * are equal if they have the same values for their corresponding tiles.
	 * @param other the state used for comparison
	 * @return true if board values are equal, false if not.
	 */	
	public boolean equals(Object other)
	{
		//override the equals given in the Object class by doing this
		//modularized for readability
		return equalsState((State)other);
	}
	
	private boolean equalsState(State other)
	{
		if (other == null) return false; //just in case
		for (int x = 0; x < 4; x++)
		{
			for (int y = 0; y < 4; y++)
			{
				if (this.getValue(x, y) != other.getValue(x, y))
				{
					return false;
				}
			}
		}
		return true;
	}
	

}
