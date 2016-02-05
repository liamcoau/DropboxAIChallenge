#!/usr/bin/env python
#
# Sample dropblox_ai exectuable.
#

import json
import sys
import time

class InvalidMoveError(ValueError):
  pass

# A class representing an (i, j) position on a board.
class Point(object):
  def __init__(self, i=0, j=0):
    self.i = i
    self.j = j

# A class representing a Block object.
class Block(object):
  def __init__(self, center, offsets):
    # The block's center and offsets should not be mutated.
    self.center = Point(center['i'], center['j'])
    self.offsets = tuple(Point(offset['i'], offset['j']) for offset in offsets)
    # To move the block, we can change the Point "translation" or increment
    # the value "rotation".
    self.translation = Point()
    self.rotation = 0

  # A generator that returns a list of squares currently occupied by this
  # block. Takes translations and rotations into account.
  def squares(self):
    if self.rotation % 2:
      for offset in self.offsets:
        yield Point(
          self.center.i + self.translation.i + (2 - self.rotation)*offset.j,
          self.center.j + self.translation.j - (2 - self.rotation)*offset.i,
        )
    else:
      for offset in self.offsets:
        yield Point(
          self.center.i + self.translation.i + (1 - self.rotation)*offset.i,
          self.center.j + self.translation.j + (1 - self.rotation)*offset.j,
        )

  def left(self):
    self.translation.j -= 1

  def right(self):
    self.translation.j += 1

  def up(self):
    self.translation.i -= 1

  def down(self):
    self.translation.i += 1

  def rotate(self):
    self.rotation += 1

  def unrotate(self):
    self.rotation -= 1

  # The checked_* methods below perform an operation on the block
  # only if it's a legal move on the passed in board.  They
  # return True if the move succeeded.
  def checked_left(self, board):
    self.left()
    if board.check(self):
        return True
    self.right()
    return False

  def checked_right(self, board):
    self.right()
    if board.check(self):
        return True
    self.left()
    return False

  def checked_down(self, board):
    self.down()
    if board.check(self):
        return True
    self.up()
    return False

  def checked_up(self, board):
    self.up()
    if board.check(self):
        return True
    self.down()
    return False

  def checked_rotate(self, board):
    self.rotate()
    if board.check(self):
        return True
    self.unrotate()
    return False

  def do_command(self, command):
    assert(command in ('left', 'right', 'up', 'down', 'rotate')), \
        'Unexpected command %s' % (command,)
    getattr(self, command)()

  def do_commands(self, commands):
    for command in commands:
      self.do_command(command)

  def reset_position(self):
    (self.translation.i, self.translation.j) = (0, 0)
    self.rotation = 0

# A class representing a board state. Stores the current block and the
# preview list and handles commands.
class Board(object):
  rows = 33
  cols = 12

  def __init__(self, bitmap, block, preview):
    self.bitmap = bitmap
    self.block = block
    self.preview = preview

  def __repr__(self):
    return str(self)

  def __str__(self):
    return '\n'.join(' '.join('X' if elt else '.' for elt in row) for row in self.bitmap)

  @staticmethod
  def construct_from_json(state_json):
    state = json.loads(state_json)
    block = Block(state['block']['center'], state['block']['offsets'])
    preview = [Block(data['center'], data['offsets']) for data in state['preview']]
    return Board(state['bitmap'], block, preview)

  # Returns True if the block is in valid position - that is, if all of its squares
  # are in bounds and are currently unoccupied.
  def check(self, block):
    for square in block.squares():
      if (square.i < 0 or square.i >= self.rows or
          square.j < 0 or square.j >= self.cols or
          self.bitmap[square.i][square.j]):
        return False
    return True

  # Handles a list of commands to move the current block, and drops it at the end.
  # Appends a 'drop' command to the list if it does not appear, and returns the
  # new Board state object.
  #
  # If the block is ever in an invalid position during this method, throws an
  # InvalidMoveError.
  def do_commands(self, commands):
    self.block.reset_position()
    if not self.check(self.block):
      raise InvalidMoveError()
    commands.append('drop')
    for command in commands:
      if command == 'drop':
        new_board = self.place()
        return new_board
      else:
        self.block.do_command(command)
        if not self.check(self.block):
          raise InvalidMoveError()

  # Drops the current block as far as it can fall unobstructed, then locks it onto the
  # board. Returns a new board with the next block drawn from the preview list.
  #
  # Assumes the block starts out in valid position. This method mutates the current block
  #
  # If there are no blocks left in the preview list, this method will fail badly!
  # This is okay because we don't expect to look ahead that far.
  def place(self):
    while self.check(self.block):
      self.block.down()
    self.block.up()
    # Deep-copy the bitmap to avoid changing this board's state.
    new_bitmap = [list(row) for row in self.bitmap]
    for square in self.block.squares():
      new_bitmap[square.i][square.j] = 1
    new_bitmap = Board.remove_rows(new_bitmap)
    if len(self.preview) == 0:
      print "There are no blocks left in the preview list! You can't look that far ahead."
      return None
    return Board(new_bitmap, self.preview[0], self.preview[1:])

  # A helper method used to remove any full rows from a bitmap. Returns the new bitmap.
  @staticmethod
  def remove_rows(bitmap):
    (rows, cols) = (len(bitmap), len(bitmap[0]))
    new_bitmap = [row for row in bitmap if not all(row)]
    return [cols*[0] for i in range(rows - len(new_bitmap))] + new_bitmap

if __name__ == '__main__':
  if len(sys.argv) == 3:
    # This AI executable will be called with two arguments: a JSON blob of the
    # game state and the number of seconds remaining in this game.
    seconds_left = float(sys.argv[2])

    # current board
    board = Board.construct_from_json(sys.argv[1])

    # current block
    block = board.block

    # very simple AI that moves the current block as far left as possible
    moves = []                  # list of moves to make
    while board.check(block):   # while the block in in a legal position
      block.left()              # move the block left
      moves.append('left')      # append a left command to oure moves list
    if len(moves) > 0:          # remove that last left command, as it got the block into an illegal state
      moves.pop()
    for move in moves:          # print our moves
      print move
    sys.stdout.flush()          # flush stdout


    # this will do the same thing, but with different helper methods
    #while block.checked_left(board):
      #print 'left'
    #sys.stdout.flush()
