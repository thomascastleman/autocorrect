import java.util.*;
import java.io.*;

class Lev {
	public static ArrayList<ArrayList<String>> dictionary = new ArrayList<ArrayList<String>>();
	public static char[] alphabet = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u','v', 'w', 'x', 'y', 'z'};

	public static Scanner input = new Scanner(System.in);

	public static void organizeDictionary() {
		ArrayList<String> words = new ArrayList<String>();
        File f = new File("C:\\Users\\thoma\\Desktop\\Java\\Autocorrect\\dictionary.txt");
        Scanner scan = null;
  
  		// get English words from text file
        try {
            scan = new Scanner(f);
            while (scan.hasNextLine()) {
                words.add(scan.nextLine());
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
        }

        // get length of longest word in dictionary
        int max = words.get(0).length();
        for (int i = 1; i < words.size(); i++) {
        	if (words.get(i).length() > max) {
        		max = words.get(i).length();
        	}
        }

        // initialize length-organized dictionary with empty arraylists
        for (int i = 0; i <= max; i++) {
        	dictionary.add(new ArrayList<String>());
        }

        // add words to dictionary based on length
        for (int i = 0; i < words.size(); i++) {
        	dictionary.get(words.get(i).length()).add(words.get(i));
        }
	}

	public static void main(String[] args) {
		organizeDictionary();

		while (true) {
        	System.out.println("\n");
        	if (input.hasNextLine()) {
	        	String s = input.nextLine();
	        	ArrayList<String> words = getPossibleCorrections(s);
	        }
        }
	}

	public static ArrayList<String> getPossibleCorrections(String misspell) {
		// get tree of similar strings
		ArrayList<ArrayList<String>> tree = getTree(misspell, 3);
		// hashmap of possible word matches from the tree, paired with their frequencies in the tree
		HashMap<String, Integer> possible = new HashMap<String, Integer>();

		// for every layer
		for (int l = 0; l < tree.size(); l++) {
			ArrayList<String> layer = tree.get(l);

			// for every word
			for (int w = 0; w < layer.size(); w++) {
				String word = layer.get(w);

				// get all dictionary words of same length
				ArrayList<String> sameLength = dictionary.get(word.length());

				for (int d = 0; d < sameLength.size(); d++) {
					String attemptedCorrection = sameLength.get(d);

					// check if words are equal
					if (attemptedCorrection.equals(word)) {

						// if correction not already in hashmap
						if (possible.get(attemptedCorrection) == null) {
							// add to possible with freq of 1
							possible.put(attemptedCorrection, 1);
						} else {
							// else increment freq of this correction by 1
							possible.put(attemptedCorrection, possible.get(attemptedCorrection) + 1);
						}
					}
				}
			}
		}



		// now, sort data
		ArrayList<String> sortedWords = new ArrayList<String>();
		ArrayList<Integer> sortedFreq = new ArrayList<Integer>();

		ArrayList<String> keys = new ArrayList<String>(possible.keySet());

		while (keys.size() > 0) {
			String keyWithHighest = keys.get(0);

			// loop through and get key with highest frequency
			for (int i = 0; i < keys.size(); i++) {

				if (possible.get(keys.get(i)) > possible.get(keyWithHighest)) {
					keyWithHighest = keys.get(i);
				}
				
			}

			// push to sorted arraylists
			sortedWords.add(keyWithHighest);
			sortedFreq.add(possible.get(keyWithHighest));

			// remove from keys
			keys.remove(keys.indexOf(keyWithHighest));
		}

		for (int i = 0; i < (sortedWords.size() > 5 ? 5 : sortedWords.size()); i++) {
			System.out.println(sortedWords.get(i) + ": " + sortedFreq.get(i));
		}

		return sortedWords;
	}

	public static ArrayList<ArrayList<String>> getTree(String word, int depth) {
		ArrayList<ArrayList<String>> tree = new ArrayList<ArrayList<String>>();


		for (int i = 0; i < depth; i++) {
			tree.add(new ArrayList<String>());
		}

		tree.get(0).add(word);

		// for every layer in tree
		for (int l = 0; l < tree.size() - 1; l++) {
			ArrayList<String> layer = tree.get(l);

			// for every word in a given layer
			for (int s = 0; s < layer.size(); s++) {

				// get all close strings
				ArrayList<String> closeStrings = getCloseStrings(layer.get(s));

				// add to next layer
				for (int i = 0; i < closeStrings.size(); i++) {
					tree.get(l + 1).add(closeStrings.get(i));
				}
			}
		}

		return tree;
	}

	public static ArrayList<String> getCloseStrings(String word) {

		ArrayList<String> children = new ArrayList<String>();

		// for every character
		for (int w = 0; w < word.length(); w++) {

			String left = word.substring(0, w);
			String inclusiveRight = word.substring(w);
			String exclusiveRight = word.substring(w + 1);

			// removals
			children.add(left + exclusiveRight);

			for (int c = 0; c < alphabet.length; c++) {
				// insertions
				String ins = left + alphabet[c] + inclusiveRight;
				children.add(ins);

				// alterations
				if (alphabet[c] != word.charAt(w)) {
					String alt = left + alphabet[c] + exclusiveRight;
					children.add(alt);
				}
			}
		}

		return children;
	}		
}