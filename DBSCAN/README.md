# Density Based Scan
This algorithm scans a scatter plot and classifies points into an
arbitrary number of classes based upon distance.  The main method
is in the DBSCAN class and takes two arguments.

<code>java DBSCAN data.txt epsINT</code>

The first argument is space separated data.  The first value is a
String defining the <em>actual</em> class of the point.  Following
this is a list of coordinates.  This project can handle points of
any number of coordinates so long as every point is the same dimension.

An example of data has been included in the repo.
