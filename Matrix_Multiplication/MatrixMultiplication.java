// MatrixMultiplication.java
package matrixmultiplication;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MatrixMultiplication {

    // Mapper class
    public static class MatrixMapper extends Mapper<LongWritable, Text, Text, Text> {
        private Text outputKey = new Text();
        private Text outputValue = new Text();

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            // Get the filename to determine which matrix we're processing
            String fileName = ((FileSplit) context.getInputSplit()).getPath().getName();
            String line = value.toString();
            String[] tokens = line.split(",");
            
            if (fileName.startsWith("A")) {
                // Matrix A: emit (i,k) -> A:j,value
                int i = Integer.parseInt(tokens[0]);
                int j = Integer.parseInt(tokens[1]);
                double val = Double.parseDouble(tokens[2]);
                
                // Get dimensions from configuration
                int n = Integer.parseInt(context.getConfiguration().get("n"));
                
                // For each element in A, emit for combination with all possible elements in B
                for (int k = 0; k < n; k++) {
                    outputKey.set(i + "," + k);
                    outputValue.set("A," + j + "," + val);
                    context.write(outputKey, outputValue);
                }
            } else if (fileName.startsWith("B")) {
                // Matrix B: emit (i,k) -> B:j,value
                int j = Integer.parseInt(tokens[0]);
                int k = Integer.parseInt(tokens[1]);
                double val = Double.parseDouble(tokens[2]);
                
                // Get dimensions from configuration
                int m = Integer.parseInt(context.getConfiguration().get("m"));
                
                // For each element in B, emit for combination with all possible elements in A
                for (int i = 0; i < m; i++) {
                    outputKey.set(i + "," + k);
                    outputValue.set("B," + j + "," + val);
                    context.write(outputKey, outputValue);
                }
            }
        }
    }

    // Reducer class
    public static class MatrixReducer extends Reducer<Text, Text, Text, Text> {
        private Text outputValue = new Text();

        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            Map<Integer, Double> matrixA = new HashMap<>();
            Map<Integer, Double> matrixB = new HashMap<>();
            
            // Process all the values
            for (Text val : values) {
                String[] valTokens = val.toString().split(",");
                String matrix = valTokens[0];
                int j = Integer.parseInt(valTokens[1]);
                double value = Double.parseDouble(valTokens[2]);
                
                if (matrix.equals("A")) {
                    matrixA.put(j, value);
                } else {
                    matrixB.put(j, value);
                }
            }
            
            // Calculate the product for this cell
            double result = 0.0;
            for (Map.Entry<Integer, Double> entryA : matrixA.entrySet()) {
                int j = entryA.getKey();
                Double valA = entryA.getValue();
                Double valB = matrixB.get(j);
                
                if (valB != null) {
                    result += valA * valB;
                }
            }
            
            // Only emit non-zero results
            if (result != 0.0) {
                outputValue.set(Double.toString(result));
                context.write(key, outputValue);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        
        // Set matrix dimensions (m x k) * (k x n) = (m x n)
        // These should be configured based on your matrices
        conf.set("m", "3");  // Number of rows in matrix A
        conf.set("k", "3");  // Number of columns in A / rows in B
        conf.set("n", "3");  // Number of columns in matrix B
        
        Job job = Job.getInstance(conf, "matrix multiplication");
        job.setJarByClass(MatrixMultiplication.class);
        job.setMapperClass(MatrixMapper.class);
        job.setReducerClass(MatrixReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}