package oving1;
import java.io.*;
import java.util.*;

public class sprengstoff {

    public static void main(String args[]) {
        BufferedReader in;
        if (args.length > 0) {
            try {
              in = new BufferedReader(new FileReader(args[0]));
            }
            catch (FileNotFoundException e) {
                System.out.println("Kunne ikke åpne filen " + args[0]);
                return;
            }
        }
        else {
            in = new BufferedReader(new InputStreamReader(System.in));
        }
        try {
        	int max = -1;
        	while (in.ready()) {
        		int current = Integer.parseInt(in.readLine());
        		max = (current > max)? current: max;
        	}
            
            System.out.println(max);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}