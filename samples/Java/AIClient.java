import org.json.simple.*;

import java.io.PrintStream;

import java.util.ArrayList;

public class AIClient
{
    public static void main(String[] argv)
    {
        JSONObject obj = (JSONObject)JSONValue.parse(argv[0]);
        run(obj, System.out);
        System.out.flush();
    }

    public static void run(JSONObject jsonObj, PrintStream out)
    {
        Board board = Board.initializeBoardFromJSON(jsonObj);
		int rotateNumber = 0;
		int fromLeft = 0;
		double maxScore = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < 4; i++) {
			int leftPos = 0;
			board._block._rotation = i;
			while (board._block.checkedLeft(board))
				;
			do {
				while (board._block.checkedDown(board))
					;
				double score = score(board._block.squares(), board);
				if (maxScore < score) {
					maxScore = score;
					rotateNumber = i;
					fromLeft = leftPos;
				}
				while (board._block.checkedUp(board))
					;
				leftPos++;
			} while (board._block.checkedRight(board));
		}
		Positioning.placeBlock(board._block, fromLeft, rotateNumber);

    }


    public static boolean isHole(int[][] bitmap, int i, int j) {
        if (i != 0) {
            if (bitmap[i-1][j] == 0) {
                return true;
            }
        }
        return false;
    }

    public static Point[] getLowestSquares(Point[] squares) {
        ArrayList<Integer> jIndexes = new ArrayList<>();
        for (Point square : squares) {
            jIndexes.set(square.j, square.i);
        }

        for (Point square : squares) {

        }
        return squares;
    }

    public static double score(Point[] squares, Board board) {
        int num_squares = squares.length;

        int heightScores = 0;

        for (int i = 0; i < num_squares; i++) {
            heightScores += squares[i].i;
        }

        int rowsCompleted = 0;
        int clearScore = 0;

        int[][] newBitmap = board._bitmap.clone();

        int currentHoles = 0;
        Point[] lowest_squares = getLowestSquares(squares);
        for (Point square : lowest_squares) {
            
        }


        for (int i = 0; i < num_squares; i++) {
            newBitmap[squares[i].i][squares[i].j] = 1;
        }

        for (int i = 0; i< board.ROWS; i++) {
            if (board.isRowComplete(newBitmap, i)) {
                rowsCompleted++;
            }
        }
        clearScore = rowsCompleted*rowsCompleted;

        
        int touchingWallScore = 0;
        for (int i = 0; i < num_squares; i++) {
            if (squares[i].j == 0 || squares[i].j == board.ROWS - 1) {
                touchingWallScore++;
            }
        }


        


        double heightScoreFactor = 4.0;
        double clearScoreFactor = 10.0;
        double touchingWallScoreFactor = 6.0;

        return heightScoreFactor*heightScores + clearScoreFactor*clearScore + touchingWallScoreFactor*touchingWallScore;
    }
}
