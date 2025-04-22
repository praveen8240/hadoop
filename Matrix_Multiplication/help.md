MatrixMultiplication
Add these key JAR files:

- hadoop-common.jar
- hadoop-mapreduce-client-core.jar

### 3. Create the Java Class

1. Right-click on your project > New > Class
2. Package name: `matrixmultiplication`
3. Class name: `MatrixMultiplication`
4. Copy and paste the code from the artifact above

### 4. Export as JAR File

1. Right-click on your project > Export
2. Select "Java" > "JAR file"
3. Select your project and necessary files
4. Specify destination (e.g., `/home/username/matrix-mult.jar`)
5. Click "Finish"

### 5. Prepare Input Data

Create input files for your matrices in the following format:

- Each line represents one matrix element: `row,column,value`
- Files for Matrix A should start with "A" (e.g., "A-matrix.txt")
- Files for Matrix B should start with "B" (e.g., "B-matrix.txt")

Example for a 3x3 Matrix A:

```
0,0,1.0
0,1,2.0
0,2,3.0
1,0,4.0
1,1,5.0
1,2,6.0
2,0,7.0
2,1,8.0
2,2,9.0
```

Example for a 3x3 Matrix B:

```
0,0,9.0
0,1,8.0
0,2,7.0
1,0,6.0
1,1,5.0
1,2,4.0
2,0,3.0
2,1,2.0
2,2,1.0
```


# Create input directory on HDFS
hadoop fs -mkdir -p /user/matrix/input

# Upload your matrix files to HDFS
hadoop fs -put /path/to/A-matrix.txt /user/matrix/input/
hadoop fs -put /path/to/B-matrix.txt /user/matrix/input/

# Run the MapReduce job
hadoop jar /home/username/matrix-mult.jar matrixmultiplication.MatrixMultiplication /user/matrix/input /user/matrix/output

# View the results
hadoop fs -cat /user/matrix/output/part-r-00000

