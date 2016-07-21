import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class CalcAvg {
	public static class Map extends Mapper<LongWritable, Text, Text, Text> {

		boolean IsInt(String input) {
			Boolean num = false;
			try {
				Integer.parseInt(input);
				num = true;
			} catch (Exception e) {
			}
			return num;
		}

		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

			String line = value.toString();
			String[] input = line.split(",");

			if (IsInt(input[19]) && IsInt(input[20])) {
				Text dest_air = new Text();
				Text orig_air = new Text();
				Text taxi_org = new Text();
				Text taxi_dest = new Text();

				dest_air.set(input[17]);
				orig_air.set(input[16]);

				taxi_org.set(input[20]);
				taxi_dest.set(input[19]);

				context.write(orig_air, taxi_org);
				context.write(dest_air, taxi_dest);
			}
		}
	}

	public static class Reduce extends Reducer<Text, Text, Text, Text> {

		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

			int tcount = 0;
			int tsum = 0;

			for (Text value : values) {
				tcount++;
				tsum += Integer.parseInt(value.toString());
			}
			// calculate avg
			Double avg = (double) tsum / (double) tcount;
			Text avg_val = new Text();
			avg_val.set(avg.toString());
			context.write(key, avg_val);
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "Average Taxi Time");
		job.setJarByClass(CalcAvg.class);
		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}