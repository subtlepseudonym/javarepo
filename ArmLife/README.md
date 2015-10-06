# Conway's Game of Life
This is an implementation of Conway's classic zero player game.
A grid of 'cells' is randomly seeded with a boolean life value (alive or dead).
Each cell lives or dies based upon the life values of its neighbors (from Wikipedia).

1. Any live cell with fewer than two live neighbours dies, as if caused by under-population
2. Any live cell with two or three live neighbours lives on to the next generation.
3. Any live cell with more than three live neighbours dies, as if by over-population.
4. Any dead cell with exactly three live neighbours becomes a live cell, as if by reproduction.

This implementation is fairly simple and only provides options to step through generations one by
one or to step through generations automatically at a rate of about 5 generations per second.
