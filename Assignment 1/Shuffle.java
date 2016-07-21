import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

public class Shuffle {

	public static void main(String[] args) throws IOException {
		//create fresh deck
		int[] deck = new int[52];
		for (int i = 0; i < 52; i++) {
			deck[i] = i + 1;
		}
		//shuffle deck
		Random r = new Random();
		for (int i = 0; i < 1000; i++) {
			int a = r.nextInt(52);
			int b = r.nextInt(52);
			int c = deck[a];
			deck[a] = deck[b];
			deck[b] = c;
		}
		//remove x cards from the deck and print to file
		int x = r.nextInt(53);
		System.out.println(x+" cards removed");
		for(int i=0;i<x;i++){
			int n = deck[i] / 4;
			int s = deck[i] % 4;
			if (s == 0) {
			System.out.println("S "+n);
			} else if (s == 1) {
				System.out.println("C "+n);
			} else if (s == 2) {
				System.out.println("H "+n);
			} else if (s == 3) {
				System.out.println("D "+n);
			}
		}
		PrintWriter pw = new PrintWriter(new FileWriter("deck.txt"));
		for (int i = x; i < 52; i++) {
			int n = deck[i] / 4;
			int s = deck[i] % 4;
			if(s!=0){
				n+=1;
			};
			if (s == 0) {
				pw.println("S "+n);
			} else if (s == 1) {
				pw.println("C "+n);
			} else if (s == 2) {
				pw.println("H "+n);
			} else if (s == 3) {
				pw.println("D "+n);
			}
		}
		pw.close();
	}
}