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

        // the following "AI" moves a piece as far left as possible
        while (board._block.checkedLeft(board)) {
            out.println("left");
        }
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
