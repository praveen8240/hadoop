WordCount

### 2. Add Hadoop Libraries

1. Right-click on your project > Properties
2. Click on "Java Build Path" > "Libraries" tab > "Add External JARs"
3. Navigate to Hadoop libraries location (typically in `/usr/lib/hadoop/` in Cloudera)
4. Add these key JAR files:
   - hadoop-common.jar
   - hadoop-mapreduce-client-core.jar

### 3. Create the Java Class

1. Right-click on your project > New > Class
2. Package name: `wordcount`
3. Class name: `WordCount`
4. Copy and paste the code from the artifact above

### 4. Export as JAR File

1. Right-click on your project > Export
2. Select "Java" > "JAR file"
3. Select your project and necessary files
4. Specify destination (e.g., `/home/username/wordcount.jar`)
5. Click "Finish"

### 5. Run on Cloudera Hadoop

Open a terminal and run these commands:


# Create input directory on HDFS
hadoop fs -mkdir -p /user/input

# Upload your text file to HDFS
hadoop fs -put /path/to/your/local/textfile.txt /user/input/

# Run the MapReduce job
hadoop jar /home/username/wordcount.jar wordcount.WordCount /user/input /user/output

# View the results
hadoop fs -cat /user/output/part-r-00000

