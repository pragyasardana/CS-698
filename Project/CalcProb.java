import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class CalcProb {
	public static class Map extends Mapper<LongWritable, Text, Text, Text> {
		public static boolean isInteger(String s) {
			boolean isValidInteger = false;
			try {
				Integer.parseInt(s);

				// s is a valid integer

				isValidInteger = true;
			} catch (NumberFormatException ex) {
				// s is not an integer
			}

			return isValidInteger;
		}

		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			String[] val = line.split(",");
			if (isInteger(val[14])) {
				int arrDelay = Integer.parseInt((val[14]));
				Text k = new Text();
				Text v = new Text();
				k.set(val[8]);
				if (arrDelay > 0) {
					// yes delay
					v.set("Y");
				} else {
					// no dealy
					v.set("N");
				}
				context.write(k, v);
			}
		}
	}

	public static class Reduce extends Reducer<Text, Text, Text, Text> {
		@Override
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			int total = 0;
			int delay = 0;
			for (Text x : values) {
				if (x.toString().equals("Y")) {
					delay++;
				}
				total++;
			}
			Double prob = (double) delay / (double) total;
			Text v = new Text();
			v.set(prob.toString());
			context.write(v,key);
		}
	}

	public static void main(String[] args) throws Exception {
		Job job = Job.getInstance();
		job.setJarByClass(CalcProb.class);
		job.setJobName("ProbFlight");

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.waitForCompletion(true);

	}

}
