import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Airport {
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br =new BufferedReader(new FileReader(args[0]));
		List<String> l=new ArrayList<String>();
		for(String line;(line=br.readLine())!=null;){
			l.add(line);
		}
		Collections.sort(l);
		FileWriter f =new FileWriter(args[1]);
		f.write("\n");
		f.write("Worst three Airports");
		f.write(l.remove(1));
		f.write("\n");
		f.write(l.remove(1));
		f.write("\n");
		f.write(l.remove(1));
		f.write("\n");
		Collections.reverse(l);
		f.write("Top three Airports");
		f.write("\n");
		f.write(l.remove(1));
		f.write("\n");
		f.write(l.remove(1));
		f.write("\n");
		f.write(l.remove(1));
		f.close();
	}

}
