# KMeans Clustering
This project classifies points in n-space to a specific
cluster based upon minimum squared distance.  The main
method is contained within the KMeans class and it takes
two arguments.  The first argument is the location of
the point data and the second argument is an integer
specifying the number of clusters to organizes points
into.

<code> java KMeans data.txt 2 </code>

A small example test data file has been included in the repo.
The data file should consists of point data that is newline
delimited and on each line fields of point data should be
space delimited.  Thus, the data file takes the form:
clusterID coordinateOne coordinateTwo coordinateThree etc
