Step-by-Step Code Example:
1. MatrixMultiplication.java (Main Driver Class)
java
Copy
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MatrixMultiplication {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: MatrixMultiplication <input_path> <output_path>");
            System.exit(-1);
        }

        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Matrix Multiplication");
        job.setJarByClass(MatrixMultiplication.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setMapperClass(MatrixMultiplicationMapper.class);
        job.setReducerClass(MatrixMultiplicationReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
2. MatrixMultiplicationMapper.java (Mapper Class)
java
Copy
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class MatrixMultiplicationMapper extends Mapper<Object, Text, Text, IntWritable> {

    private static final String A_PREFIX = "A";  // For Matrix A
    private static final String B_PREFIX = "B";  // For Matrix B

    @Override
    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString();
        String[] tokens = line.split(",");
        
        // Check if this is Matrix A or Matrix B
        String matrixType = tokens[0].trim();
        int i = Integer.parseInt(tokens[1].trim()); // Row or Column Index
        int j = Integer.parseInt(tokens[2].trim()); // Column or Row Index
        int val = Integer.parseInt(tokens[3].trim()); // Matrix Element Value

        if (matrixType.equals("A")) {
            // For Matrix A (i, j, value), we emit (i, k) pairs for each element in A
            for (int k = 0; k < 3; k++) { // Assume B is 3x3 for example; adapt for general cases
                context.write(new Text(i + "," + k), new IntWritable(val));
            }
        } else if (matrixType.equals("B")) {
            // For Matrix B (i, j, value), we emit (i, k) pairs for each element in B
            for (int k = 0; k < 3; k++) {  // Again adapt for general cases
                context.write(new Text(i + "," + k), new IntWritable(val));
            }
        }
    }
}
3. MatrixMultiplicationReducer.java (Reducer Class)
java
Copy
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class MatrixMultiplicationReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int sum = 0;
        for (IntWritable value : values) {
            sum += value.get();
        }
        context.write(key, new IntWritable(sum));
    }
}
4. Input Format Example
You would feed the matrices in a CSV format:

Matrix A (3x3):

css
Copy
A, 0, 0, 1
A, 0, 1, 2
A, 0, 2, 3
A, 1, 0, 4
A, 1, 1, 5
A, 1, 2, 6
A, 2, 0, 7
A, 2, 1, 8
A, 2, 2, 9
Matrix B (3x3):

css
Copy
B, 0, 0, 1
B, 0, 1, 2
B, 0, 2, 3
B, 1, 0, 4
B, 1, 1, 5
B, 1, 2, 6
B, 2, 0, 7
B, 2, 1, 8
B, 2, 2, 9
5. Output Format Example
For the output of the final matrix C (which will be of size 3x3):

Copy
0,0   30
0,1   36
0,2   42
1,0   66
1,1   81
1,2   96
2,0   102
2,1   126
2,2   150
Step 5: Compilation and Execution
Compile the Java files:

bash
Copy
javac -classpath `hadoop classpath` -d . MatrixMultiplication.java MatrixMultiplicationMapper.java MatrixMultiplicationReducer.java
jar cf matrix_multiplication.jar MatrixMultiplication*.class
Run the Hadoop job:

bash
Copy
hadoop jar matrix_multiplication.jar MatrixMultiplication input_path output_path
