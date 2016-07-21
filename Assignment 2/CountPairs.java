
import java.io.IOException;
import java.util.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.commons.lang.StringUtils;

public class CountPairs {

	public static class Map extends Mapper<LongWritable, Text, Text, Text> {

		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			String[] s = line.replaceAll("[^a-zA-Z\\s]", "").toLowerCase().split(" ");
			for (int i = 0; i + 1 < s.length; i++) {
				Text a = new Text();
				Text b = new Text();
				a.set(s[i]);
				b.set(s[i + 1]);
				context.write(a, b);
			}
		}
	}

	public static java.util.Map<String, Double> sortMapByValue(java.util.Map<String, Double> unsortMap) {
		// Convert Map to List
		List<java.util.Map.Entry<String, Double>> list = new LinkedList<java.util.Map.Entry<String, Double>>(
				unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<java.util.Map.Entry<String, Double>>() {
			public int compare(java.util.Map.Entry<String, Double> o1, java.util.Map.Entry<String, Double> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
		// reverse the list, because we wanr top tree states
		Collections.reverse(list);
		// Convert sorted map back to a Map which contains top three states

		java.util.Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		Iterator<java.util.Map.Entry<String, Double>> it = list.iterator();
		for (int itr = 0; itr < 100 && it.hasNext(); itr++) {

			java.util.Map.Entry<String, Double> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	public static class Reduce extends Reducer<Text, Text, Text, Text> {
		java.util.Map<String, Double> list = new HashMap<String, Double>();

		@Override
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			String a = key.toString();
			java.util.Map<String, Integer> m = new HashMap<String, Integer>();
			int total = 0;
			for (Text x : values) {
				total++;
				String b = x.toString();
				String c = a + " " + b;
				if (m.containsKey(c)) {
					int n = m.get(c);
					m.remove(c);
					n++;
					m.put(c, n);
				} else {
					m.put(c, 1);
				}
			}
			// System.out.println(m.toString());
			for (String x : m.keySet()) {
				Double ans = (double) m.get(x) / (double) total;
				list.put(x, ans);
			}
		}

		@Override
		public void cleanup(Context context) throws IOException, InterruptedException {
			list = sortMapByValue(list);
			for (String lora : list.keySet()) {
				context.write(new Text(lora), new Text(list.get(lora).toString()));
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Job job = Job.getInstance();
		job.setJarByClass(CountPairs.class);
		job.setJobName("CountPairs");

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
