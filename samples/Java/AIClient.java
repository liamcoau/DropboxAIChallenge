import org.json.simple.*;

import java.io.PrintStream;

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
			while (board._block.checkedLeft(board))
				;
			do {
				while (board._block.checkedDown(board))
					;
				if (maxScore < score(board._block.squares(), board)) {
					rotateNumber = i;
					fromLeft = leftPos;
				}
				while (board._block.checkedUp(board))
					;
				leftPos++;
			} while (board._block.checkedRight(board));
			board._block.rotate();
		}
		board._block._rotation = rotateNumber;
		Positioning.placeBlock(board._block, fromLeft);

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
