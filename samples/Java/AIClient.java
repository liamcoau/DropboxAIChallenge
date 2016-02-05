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
		int pointI = 0;
		int pointJ = 0;
		double maxScore = Double.NEGATIVE_INFINITY;

		for (int i = 0; i < 4; i++) {
			while (board._block.checkedLeft(board))
				;
			do {
				while (board._block.checkedDown(board))
					;
				double score = score(board._block.squares(), board);
				if (maxScore < score) {
					maxScore = score;
					rotateNumber = board._block._rotation;
					pointI = board._block._translation.i;
					pointJ = board._block._translation.j;
				}
				while (board._block.checkedUp(board))
					;
			} while (board._block.checkedRight(board));
			board._block.rotate();
		}
		board._block._rotation = rotateNumber;
		board._block._translation = new Point(pointI, pointJ);
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
        int minJ = Integer.MAX_VALUE;
        int maxJ = 0;
        for (Point square : squares) {
            if (square.j < minJ) {
                minJ = square.j;
            } else if (square.j > maxJ) {
                maxJ = square.j;
            }
        }

        Point[] bottomPoints = new Point[maxJ - minJ + 1];

        for (Point square : squares) {
            bottomPoints[square.j - minJ] = square;
        }

        for (Point square : squares) {
            if (bottomPoints[square.j - minJ].i < square.i) {
                bottomPoints[square.j - minJ] = square;
            }
        }

        return bottomPoints;
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

        int holesScore = 0;
        Point[] lowest_squares = getLowestSquares(squares);
        for (Point square : lowest_squares) {
            if (newBitmap[square.i + 1][square.j] == 0) {
                holesScore += 1;
            }
        }

        for (int i = 0; i < num_squares; i++) {
            Point square = squares[i];
            //newBitmap[squares[i].i][squares[i].j] = 1;
            newBitmap[square.i][square.j] = 1;
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
        double holesScoreFactor = -2.5;
        double clearScoreFactor = 10.0;
        double touchingWallScoreFactor = 6.0;

        return heightScoreFactor*heightScores + holesScoreFactor*holesScore + clearScoreFactor*clearScore + touchingWallScoreFactor*touchingWallScore;
    }
}
