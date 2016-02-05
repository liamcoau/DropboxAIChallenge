import org.json.simple.*;

// This class represents an i, j position on the board
public class Point {
	public int i;
	public int j;
	
	public Point() {
		i = 0;
		j = 0;
	}
	
	public Point(int i_in, int j_in) {
		i = i_in;
		j = j_in;
	}
	
	public String toString() {
		return "("+i+", "+j+")";
	}
	
	public static Point initializePointFromJSON(JSONObject obj) {
		return new Point(((Long)obj.get("i")).intValue(), ((Long)obj.get("j")).intValue());
	}
}