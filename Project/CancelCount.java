import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class CancelCount {
	public static class Map extends Mapper<LongWritable, Text, Text, LongWritable> {
		

		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			String[] val = line.split(",");
			if(!(val[22].equals("NA")||val[22].isEmpty())){
				if(val[22].toString().equals("A")){
					Text t=new Text();
					t.set("Carrier");
					context.write(t, new LongWritable(1));
				}
				else if(val[22].toString().equals("B")){
					Text t=new Text();
					t.set("Weather");
					context.write(t, new LongWritable(1));
				}
				else if(val[22].toString().equals("C")){
					Text t=new Text();
					t.set("NAS");
					context.write(t, new LongWritable(1));
				}
				else if(val[22].toString().equals("D")){
					Text t=new Text();
					t.set("Security");
					context.write(t, new LongWritable(1));
				}
			}
		}
	}

	public static class Reduce extends Reducer<Text, LongWritable, Text, LongWritable> {
		@Override
		public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
			long total=0;
			for(LongWritable i:values){
				total+=i.get();
			}
			context.write(key,new LongWritable(total));
			
		}
	}

	public static void main(String[] args) throws Exception {
		Job job = Job.getInstance();
		job.setJarByClass(CancelCount.class);
		job.setJobName("CancelCount");

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);

		job.setMapperClass(Map.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.waitForCompletion(true);

	}

}
