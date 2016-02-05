public class Positioning {
	public static void placeBlock(Block block, int fromLeft, int rotateNumber) {
		int delta;
		block.resetPosition();
		block._rotation = rotateNumber;
		for (int i = 0; i < rotateNumber; i++) {
			System.out.println("rotate");
		}
		int leftmost = block.squares()[0].j;
		for (Point point : block.squares()) {
			if (leftmost > point.j) {
				leftmost = point.j;
			}
		}
		delta = fromLeft - leftmost;
		if (delta > 0) {
			for (int i = 0; i < delta; i++) {
				System.out.println("right");
			}
		} else {
			for (int i = 0; i > delta; i--) {
				System.out.println("left");
			}
		}
	}
}
