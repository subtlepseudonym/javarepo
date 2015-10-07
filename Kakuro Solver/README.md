# Kakuro Solver
Kakuro Solver does exactly what it sounds like: it solves
kakuro puzzles!  If you've never heard of kakuro puzzles,
they're a bit like a cross between sudoku and crossword
puzzles.  And they're super fun!  Check them out
<a href="http://www.kakuroconquest.com">here!</a>

This project is an extension of a project I implemented in
school.  The original project was an implementation of
backtracking search as a means of solving the reasonably
small state space of a kakuro board.  This method took
about two seconds to solve an average sized board and
became significantly slower as the board size grew.  To
fix this issue, I have included an expert system that cuts
the search time considerably, taking just under 0.2 seconds
for average sized boards and scaling to larger boards very
efficiently.  The main method is contained within the
Kakuro Solver class and requires just one argument: the
location of the board txt file.

<code> java KakuroSolver board.json </code>

Being that this was at one time a school project, a good
number of the classes still contain my professor's
copyright notice which does allow for modification and
redistribution given that the notice is retained.

A collection of example boards are included in the repo.
