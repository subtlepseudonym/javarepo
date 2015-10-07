# Inverted Index and Ranking Functions
This project can either build an inverted index from JSON or read
it from disk.  The index is then used to query the corpus in the 
JSON file.  Two ranking methods can be used to return query results:
BM25 and Query Likelihood.  These ranking methods can be used via
their respective main methods.  Each main method only takes a single
argument - the location of the JSON file.

<code> java BM25 corpus.json </code>

This was a school project, so the querying method and option to
read the index from disk are not called within the main method
and require a bit of code manipulation to use.

An example index (to be read from disk) and an example corpus have
been included in the repo.
