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
}
