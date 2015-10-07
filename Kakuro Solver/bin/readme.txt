Connor Demille
27336607

My kakuro solver uses a system of inference to limit the possible values for each
cell on the board.  While most cells will be reduced to a single possible value
fairly quickly, I still have to use backtracking search to fill in the gaps of
more difficult boards.  I also implemented an MRV heuristic for selection of
the next unassigned cell (cell with least number of possible values) as well as
limiting the order domain values of each cell through the above mentioned inference.
My program runs in under a quarter second on boards up to size 16x16 and likely a bit
larger though I haven't tried.  My test cases only had a single solution, however, so
performance may differ depending upon your test cases.