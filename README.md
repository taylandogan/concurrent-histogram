# concurrent-histogram
Concurrently builds a histogram out of given intervals and values.

Build the project: ant -buildfile build.xml
Then run the main class: java -jar HistogramBuilder.jar

In 'resources' folder, you may find the test files I have used. For each file in the folder, I initialize a new Thread instance and proceed with building the histogram.
You may edit these files (or add new files) to 'resources' folder for further testing.

However, for the consistency and correctness of unit tests, you should not alter the file 'test_histogram.txt'.

Example: Given the intervals and values:
[3, 4.1)
[8.5, 8.7)
[0, 1.1)
[31.5, 41.27)
40.1
8.1
8.2
30
31.51
1
41.27

Output of printHistogram():
[0, 1.1): 1
[3, 4.1): 0
[8.5, 8.7): 0
[31.5, 41.27): 2

sample mean: 24.203
sample variance: 422.243

outliers: 8.1, 8.2, 30, 41.27

TODO: Ant cannot find junit.jar. Fix ant test target.
