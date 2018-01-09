package oving2;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.*;

public class Ordbok {

    public static Node bygg(String[] ordliste) {
    	Node rootNode = new Node();
    	int index = 0;
    	for (String ord : ordliste) {
    		addChild(rootNode, ord, index);
    		index += ord.length()+1;
    	}
    	return rootNode;
    }
    
    public static void addChild(Node currentNode, String ord, int index) {
    	if (ord.length() <= 1) {
    		currentNode.posisjoner.add(index);
    		return;
    	}
    	else if (currentNode.barn.containsKey(ord.charAt(0))) {
    		addChild(currentNode.barn.get(ord.charAt(0)), ord.substring(1), index);
    	} else {
    		currentNode.barn.put(ord.charAt(0), new Node());
    		addChild(currentNode.barn.get(ord.charAt(0)), ord.substring(1), index);
    	}
    }

    public static ArrayList<Integer> posisjoner(String ord, int index, Node currentNode) {
    	if (index >= ord.length()) {
    		return currentNode.posisjoner;
    	}
    	else if ((ord.charAt(index) == '?')) {
    		ArrayList<Integer> result = new ArrayList<Integer>();
    		for (Node child : currentNode.barn.values()) {
    			result.addAll(posisjoner(ord,index+1, child));
    		}
    		return result;
    	}
    	else if (currentNode.barn.containsKey(ord.charAt(index))) {
    		return (posisjoner(ord, index+1,currentNode.barn.get(ord.charAt(index))));
    	}else {
    		return new ArrayList<Integer>();
    	}
    }


    public static void main(String[] args) {
        try {
            BufferedReader in;
            if (args.length > 0) {
                try {
                    in = new BufferedReader(new FileReader(args[0]));
                } catch (FileNotFoundException e) {
                    System.out.println("File not found: " + args[0]);
                    return;
                }
            } else {
                in = new BufferedReader(new InputStreamReader(System.in));
            }
            StringTokenizer st = new StringTokenizer(in.readLine());
            String[] ord = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) ord[i++] = st.nextToken();
            Node rotNode = bygg(ord);
            String sokeord = in.readLine();
            while (sokeord != null) {
                sokeord = sokeord.trim();
                System.out.print(sokeord + ":");
                ArrayList<Integer> pos = posisjoner(sokeord, 0, rotNode);
                int[] posi = new int[pos.size()];
                for (i = 0; i < posi.length; i++) posi[i] = ((Integer) pos.get(i)).intValue();
                Arrays.sort(posi);
                for (i = 0; i < posi.length; i++) System.out.print(" " + posi[i]);
                System.out.println();
                sokeord = in.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Node {
    public ArrayList<Integer> posisjoner;
    public HashMap<Character,Node> barn;

    public Node() {
        posisjoner = new ArrayList<Integer>();
        barn = new HashMap<Character, Node>();
    }
}