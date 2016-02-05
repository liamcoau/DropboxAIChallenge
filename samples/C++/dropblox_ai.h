#include "json/reader.h"
#include "json/elements.h"

#include <sstream>
#include <vector>

using namespace json;
using namespace std;

#define ROWS 33
#define COLS 12
#define PREVIEW_SIZE 5

typedef int Bitmap[ROWS][COLS];

class Board;

class Point {
 public:
  int i;
  int j;
};

class Block {
 public:
  // The size of a block is the number of squares in the block.
  // We pre-allocate 10 Points for offsets, but only use `size` of them.
  // The block's center, size and offsets should not be mutated.
  Point center;
  int size;
  Point offsets[10];
  // To move the block, we can change the Point "translation" or increment
  // the value "rotation".
  Point translation;
  int rotation;

  Block(Object& raw_block);
  void left();
  void right();
  void up();
  void down();
  void rotate();

  // The checked_* methods below perform an operation on the block
  // only if it's a legal move on the passed in board.  They
  // return true if the move succeeded.
  //
  // The block is still assumed to start in a legal position.
  bool checked_left(const Board& board);
  bool checked_right(const Board& board);
  bool checked_up(const Board& board);
  bool checked_down(const Board& board);
  bool checked_rotate(const Board& board);

  // Performs a command or a list of commands to move a block. A command is one of
  // "left", "right", "up", "down", "rotate".
  void do_command(const string& command);
  void do_commands(const vector<string>& commands);

  void reset_position();

 private:
  // This isn't a standard function, just used to reverse rotation when it fails.
  void unrotate();
};

class Board {
 public:
  int rows;
  int cols;
  Bitmap bitmap;
  Block* block;
  vector<Block*> preview;

  Board(Object& state);

  // Returns true if the `query` block is in valid position - that is, if all of
  // its squares are in bounds and are currently unoccupied.
  bool check(const Block& query) const;

  // Resets the block's position, moves it according to the given commands, then
  // drops it onto the board. Returns a pointer to the new board state object.
  //
  // Throws an exception if the block is ever in an invalid position.
  //
  // A command is one of "left", "right", "up", "down", "rotate".
  Board* do_commands(const vector<string>& commands);

  // Drops the block from whatever position it is currently at. Returns a
  // pointer to the new board state object, with the next block drawn from the
  // preview list.
  //
  // Assumes the block starts out in valid position.
  // This method translates the current block downwards.
  //
  // If there are no blocks left in the preview list, this method will fail badly!
  // This is okay because we don't expect to look ahead that far.
  Board* place();

  // A static method that takes in a new_bitmap and removes any full rows from it.
  // Mutates the new_bitmap in place.
  static void remove_rows(Bitmap* new_bitmap);
 
 private:
  Board();
};
