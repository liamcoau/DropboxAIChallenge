import java.util.HashSet;

import org.json.simple.*;

// This class represents a block object
public class Block {
	
	public final static String[] VALID_COMMANDS = {"left", "right", "up", "down", "rotate"};
	public static HashSet<String> VALID_COMMAND_SET = null;
	
	// The block's center and offsets should not be mutated
	private Point _center;
	private Point[] _offsets;
	
	// To move the block, change the translation or increment rotation
	public Point _translation;
	public int _rotation;
	
	public Block(Point center, Point[] offsets) {
			_center = center;
			_offsets = offsets;
			_translation = new Point();
			_rotation = 0;
			
			// initialize the singleton VALID_COMMAND_SET
			if (VALID_COMMAND_SET == null) {
				VALID_COMMAND_SET = new HashSet<String>();
				for (String command : VALID_COMMANDS)
					VALID_COMMAND_SET.add(command);
			}
	}
	
	// A generator that returns a list of squares currently occupied by
	// this block.  Takes translations and rotations into account
	public Point[] squares() {
		Point[] toReturn = new Point[_offsets.length];

		if (_rotation % 2 == 1) {
			for (int i = 0; i < _offsets.length; i ++) {
				toReturn[i] = new Point(
						_center.i + _translation.i + (2 - _rotation)*_offsets[i].j,
						_center.j + _translation.j - (2 - _rotation)*_offsets[i].i
						);
			}
		}
		else {
			for (int i = 0; i < _offsets.length; i ++) {
				toReturn[i] = new Point(
						_center.i + _translation.i + (1 - _rotation)*_offsets[i].i,
						_center.j + _translation.j + (1 - _rotation)*_offsets[i].j
						);
			}
		}
		return toReturn;
	}
	
	public void left() {
		_translation.j--;
	}
	public void right() {
		_translation.j++;
	}
	public void up() {
		_translation.i--;
	}
	public void down() {
		_translation.i++;
	}
	public void rotate() {
		_rotation++;
	}
	public void unrotate() {
		_rotation--;
	}
	
	// The checked* methods below perform an operation on the block
	// only if it's a legal move on the passed in board. They return
	// true if the move succeeded	
	public boolean checkedLeft(Board board) {
		this.left();
		if (board.check(this)) {
			return true;
		}
		this.right();
		return false;
	}
	
	public boolean checkedRight(Board board) {
		this.right();
		if (board.check(this)) {
			return true;
		}
		this.left();
		return false;
	}
	
	public boolean checkedDown(Board board) {
		this.down();
		if (board.check(this)) {
			return true;
		}
		this.up();
		return false;
	}
	
	public boolean checkedUp(Board board) {
		this.up();
		if (board.check(this)) {
			return true;
		}
		this.down();
		return false;
	}
	
	public boolean checkedRotate(Board board) {
		this.rotate();
		if (board.check(this)) {
			return true;
		}
		this.unrotate();
		return false;
	}

	public String toString() {
		String toReturn = "Squares: ";
		for (Point square : this.squares()) {
			toReturn += square + " ";
		}
		return toReturn;
	}
	
	public void doCommand(String command) {
		assert(VALID_COMMAND_SET.contains(command));
		if (command.equals("left")) {
			this.left();
		} else if (command.equals("right")) {
			this.right();
		} else if (command.equals("down")) {
			this.down();
		} else if (command.equals("up")) {
			this.up();
		} else if (command.equals("rotate")) {
			this.rotate();
		}
	}
	
	public void doCommands(String[] commands) {
		for (String command : commands) {
			this.doCommand(command);
		}
	}
	
	public void resetPosition() {
		_translation = new Point();
		_rotation = 0;
	}
	
	public static Block initializeBlockFromJSON(JSONObject obj) {
		JSONObject rawCenter = (JSONObject)obj.get("center");
		JSONArray rawOffsets = (JSONArray)obj.get("offsets");
		
		Point center = Point.initializePointFromJSON(rawCenter);
		Point[] offsets = new Point[rawOffsets.size()];
		for (int i = 0; i < offsets.length; i ++) {
			offsets[i] = Point.initializePointFromJSON((JSONObject)rawOffsets.get(i));
		}
		
		return new Block(center, offsets);
	}
}
