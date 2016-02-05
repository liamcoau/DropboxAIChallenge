import java.util.*;
import org.json.simple.*;

// This class represents the board state.  It stored the current bitmap,
// block, and preview blocks
public class Board {
	
	public final static int ROWS = 33;
	public final static int COLS = 12;
	
	public int[][] _bitmap;
	public Block _block;
	public Block[] _preview;
	
	public Board(int[][] bitmap, Block block, Block[] preview) {
		_bitmap = bitmap;
		_block = block;
		_preview = preview;
	}
	
	public boolean check(Block block) {
		for (Point square : block.squares()) {
			if (square.i < 0 || square.i >= ROWS ||
				square.j < 0 || square.j >= COLS ||
				_bitmap[square.i][square.j] != 0)
				return false;
		}
		return true;
	}

	// Takes a list of commands to move the current block and drops it at the end.
	// This method appends a drop command to the input list and returns a new
	// board object representing the state of the board for the next piece
	public Board doCommands(ArrayList<String> commands) throws InvalidMoveException {
		_block.resetPosition();
		if (!this.check(_block)) {
			throw new InvalidMoveException();
		}
		commands.add("drop");
		for (String command : commands) {
			if (command.equals("drop")) {
				Board newBoard = this.place();
				return newBoard;
			}
			else {
				_block.doCommand(command);
				if (!this.check(_block)) {
					throw new InvalidMoveException();
				}
			}
		}
		return null;
	}
	
	// Drops the current block as far as it can fall unobstructed, then locks it
	// into the board.  Removes complete rows and returns a new board with the next
	// block drawn from the preview list.
	//
	// Assumes the block starts out in a valid position. This method mutates the current block
	//
	// If there are no blocks left in the preview list, this method will return null
	// This is okay because we don't expect anyone to look that far ahead	
	public Board place() {
		while (_block.checkedDown(this)) {} // move the piece down as far as it will go
		
		// deep copy the bitmap to avoid changing this board's state
		int[][] newBitmap = new int[ROWS][COLS];
		for (int i = 0; i < ROWS; i ++) {
			for (int j = 0; j < COLS; j ++) {
				newBitmap[i][j] = _bitmap[i][j];
			}
		}
		// add the piece to the new bitmap
		for (Point square : _block.squares()) {
			newBitmap[square.i][square.j] = 1;
		}
		// remove rows from the new bitmap
		Board.removeRows(newBitmap);
		
		// generate the preview list for the next turn
		if (_preview.length == 0) {
			System.out.println("There are no blocks left in the preview list! you cant look that far ahead!"); 
			return null;
		}
		Block[] newPreview = new Block[_preview.length - 1];
		for (int i = 0; i < _preview.length-1; i ++) {
			newPreview[i] = _preview[i+1];
		}
		
		// return a new board
		return new Board(newBitmap, _preview[0], newPreview);
	}
	
	// Static helper method that modifies the passed in bitmap by removing complete rows
	// according to standard tetris rules
	public static int[][] removeRows(int[][] bitmap) {
		int readRow = ROWS-1;
		int writeRow = ROWS-1;
		while (readRow >= 0) {
			// if the current row we're reading from is not full
			if (!Board.isRowComplete(bitmap, readRow)) {
				if (readRow != writeRow) { // if we need to shift
					for (int j = 0; j < COLS; j ++) { // copy the read row to the write row
						bitmap[writeRow][j] = bitmap[readRow][j];
					}
				}
				writeRow --;
			}
			readRow --;
		}
		
		// fill in the rest of the rows with 0s
		while (writeRow >= 0) {
			for (int j = 0; j < COLS; j ++) { // copy the read row to the write row
				bitmap[writeRow][j] = 0;
			}
			writeRow --;
		}
		return bitmap;
	}
	public static boolean isRowComplete(int[][] bitmap, int row) {
		for (int j = 0; j < COLS; j ++) {
			if (bitmap[row][j] == 0) {
				return false;
			}
		}
		return true;
	}
	
	public static Board initializeBoardFromJSON(JSONObject obj) {
		@SuppressWarnings("unchecked")
		ArrayList<ArrayList<Long>> rawBitmap = (ArrayList<ArrayList<Long>>)obj.get("bitmap");
		int[][] bitmap = new int[ROWS][COLS];		
		for (int i = 0; i < rawBitmap.size(); i ++) {
			ArrayList<Long> row = rawBitmap.get(i);
			for (int j = 0; j < row.size(); j ++) {
				bitmap[i][j] = rawBitmap.get(i).get(j).intValue();
			}
		}
		
		Block block = Block.initializeBlockFromJSON((JSONObject)obj.get("block"));
		
		JSONArray rawPreview = (JSONArray)obj.get("preview");
		Block[] preview = new Block[rawPreview.size()];
		for (int i = 0; i < preview.length; i ++) {
			preview[i] = Block.initializeBlockFromJSON((JSONObject)rawPreview.get(i));
		}
		
		return new Board(bitmap, block, preview);
	}
}