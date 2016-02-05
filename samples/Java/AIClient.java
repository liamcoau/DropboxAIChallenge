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

        // the following "AI" moves a piece as far left as possible
        while (board._block.checkedLeft(board)) {
            out.println("left");
        }
    }

    public static double score(Board board) {
        Point[] squares = board._block.squares();
        int num_blocks = squares.length;

        int heightScores = 0;

        for (int i = 0; i < squares.length; i++) {
            heightScores += squares[i].i;
        }

        int rowsCompleted = 0;
        int clearScore = 0;

        int[][] newBitmap = new int[board.ROWS][board.COLS];
        for (int i = 0; i < board.ROWS; i ++) {
            for (int j = 0; j < board.COLS; j ++) {
                newBitmap[i][j] = board._bitmap[i][j];
            }
        }
        for (int i = 0; i< board.ROWS; i++) {
            if (board.isRowComplete(newBitmap, i)) {
                rowsCompleted++;
            }
        }
        clearScore = rowsCompleted*rowsCompleted;

        double heightScoreFactor = 4.0;
        double rowsCompletedFactor = 10.0;

        return heightScoreFactor + rowsCompletedFactor;
    }
}
