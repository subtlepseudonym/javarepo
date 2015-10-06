# Hidden Markov Model
This project constructs a markov chain for fair and unfair coins 
from a data file and provides
two calculations on the structure.  This specific markov model assumes
that half the nodes represent fair coins and the other half represent
unfair coins.   The Filter class contains a main
method that recurses through the model and prints the probability of
the final node.  The Viterbi class also contains a main method which
recurses through the model and prints the most likely path.  Both
main methods take a single argument, that path to the data file.

<code> java Viterbi data.txt</code>

The data file should contain four lines.
1. The probability of unfair coins landing heads (decimal)
2. The probability of transition from fair coins to unfair (decimal)
3. The probability of transition from unfair coins to fair (decimal)
4. The observed sequence of coin flips (String ex. "h h t h t")

A sample data file has been included in the repo.
