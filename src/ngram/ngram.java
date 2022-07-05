package ngram;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class ngram {

	class Words extends Vector<Word> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Words()
		{

		}

		public boolean addSorted(Word w)
		{
			if(this.size() == 0)
				return this.add(w);
			if(this.size() < 2)
			{
				if(w.word.compareTo(this.get(0).word) < 0)
					this.add(0, w);
				else
					this.add(w);
				return true;
			}
			boolean added = false; 
			int check = this.size()/2;
			int upper = this.size();
			while(!added) {
				if(w.word.compareTo(this.get(check).word) < 0)
				{
					if(check == 0) {
						this.add(0,w);
						return true;
					} else if(w.word.compareTo(this.get(check-1).word) > 0) { 
						this.add(check,w);
						return true;
					}
					else {
						upper = check;
						check = check/2;
						continue;
					}
				} else if(w.word.compareTo(this.get(check).word) > 0)
				{
					if(check == this.size()-1) {
						this.add(w);
						return true;
					} else if(w.word.compareTo(this.get(check-1).word) < 0) { 
						this.add(check,w);		
						return true;
					}
					else {
						check += (upper-check)/2;
						continue;
					}
				} else if(w.word.compareTo(this.get(check).word) == 0)
				{
					this.get(check).appearence = this.get(check).appearence > w.appearence ? this.get(check).appearence : w.appearence;
					return true;
				}
			}
			return true;
		}

	}

	class Word {
		String word;
		long appearence;

		public Word(String word, long appearence)
		{
			this.word = word; this.appearence = appearence;
		}
	}

	public static void main(String[] args) {
		if(args == null || args.length == 0 || (args[0] != null && args[0].equalsIgnoreCase("help")))
		{
			printHelp();
			return;
		}
		
		String year = "";
		boolean yearFound = false;
		String fileDirectory = "";
		boolean filesFound = false;
		String fileSizes = "";
		boolean filesizesFound = false;
		for(String string: args)
		{
			if(string.startsWith("files")) {
				string = string.replaceFirst("files", "");
				string = string.replaceAll("=", "").replaceAll(" ", "");
				File file = new File(string);
				if(!file.isDirectory() || !file.canRead())
				{
					System.out.println("Invalid file directory location.");
					return;
				} 
				fileDirectory = string;
				filesFound = true;
			} else if(string.startsWith("filesizs")) {
				string = string.replaceFirst("filesizs", "");
				string = string.replaceAll("=", "").replaceAll(" ", "");
				filesizesFound = true;
				fileSizes = string;				
			} else if(string.startsWith("year")) {
				string = string.replaceFirst("year", "");
				string = string.replaceAll("=", "").replaceAll(" ", "");
				if(string.length() != 4)
				{
					System.out.println("Year parameter invalid. Please make sure you are using a 4 digit year; (e.g, 2019)");
					return;
				}
				year = string;
				yearFound = true;
			} else {
				System.out.println("Unknown input : " + string);
			}
		}
		
		if(yearFound && filesFound &&  filesizesFound)
		{
			ngram n = new ngram();
			n.doThisShit(year, fileDirectory, fileSizes);
		} else {
			printHelp();
		}

	}
	
	public static void printHelp()
	{
		System.out.println("Welcome to this project!");
		System.out.println("This is designed to take a list of ngram files from");
		System.out.println("The google books API and give ordered lists based on word usage");
		System.out.println("");
		System.out.println("There are some important args that must be entered for this to work");
		System.out.println("files: The location of the files to read.");
		System.out.println("filesizs: A single item or list of file sizes to produce.");
		System.out.println("year: The year to take the uasage data from.");
		System.out.println("");
		System.out.println("For example:");
		System.out.println("");
		System.out.println("java -jar runner.jar files=\\tmp\\files filesizes=20000,10000,5000 year=2019");
		System.out.println("");
		System.out.println("This will process all files from \\tmp\\files and produce files for the most used ");
		System.out.println("20,000 10,000 and 5,000 words from 2019.");
		
	}

	public void doThisShit(String year, String fileDirectory, String fileSizes)
	{
		Vector<File> fs = new Vector<File>();
		File directory = new File(fileDirectory);
		
		File[] listOfFiles = directory.listFiles();
		for(File f: listOfFiles)
		{
			fs.add(f);
		}

		Words wordList = new Words();
		for(int i = 0 ; i < 4 ; i++)
		{
			File f = fs.get(i);
			try {
				FileReader reader = new FileReader(f);
				try (BufferedReader br = new BufferedReader(reader)) {
					String line;
					while ((line = br.readLine()) != null) {
						String[] wordDetails = line.split("	");
						if(wordDetails[wordDetails.length-1].startsWith(year))
						{
							String worddd = wordDetails[0];
							if(!Character.isAlphabetic(worddd.charAt(0))) {
								continue;
							}
							if(worddd.contains("_")) {
								worddd = worddd.split("_")[0];
							}
							long count = 0;
							try {
								count += Integer.parseInt(wordDetails[wordDetails.length-1].split(",")[2]);
							} catch (Exception e) {}
//							try {
//								count += Integer.parseInt(wordDetails[wordDetails.length-2].split(",")[2]);						
//							} catch (Exception e) {	}
//							try {
//								count += Integer.parseInt(wordDetails[wordDetails.length-3].split(",")[2]);
//							} catch (Exception e) { }



							wordList.addSorted(new Word(worddd, count));
						}
					}
				}
				System.out.println(wordList.get(0).word);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		String selectedWord = wordList.get(0).word;
		long selectedCount = wordList.get(0).appearence;

		for( Word w : wordList)
		{
			if(selectedCount < w.appearence) {
				selectedCount = w.appearence;
				selectedWord = w.word;
			}
		}			

		System.out.println(selectedWord);

		Words newList = quickSortNumber(wordList);

		System.out.println(newList.get(0).word);

		//TODO
		//split file sizes by commas and create files of that size
		printNextX(10, newList, true);
	}


	private void printNextX(int count, Words newList, boolean withNumber) {
		if(count > newList.size()) {
			count = newList.size();
		}
		for(int i = 0; i <  count; i++)
		{
			if(withNumber)
			{
				System.out.println(newList.get(i).word + "  " + newList.get(i).appearence);
			} else {
				System.out.println(newList.get(i).word);
			}
		}
	}

	public Words quickSortNumber(Words words)
	{
		if(words.size() == 0)
		{
			return words;
		}
		Words low = new Words();
		Words high = new Words();
		Words pivit = new Words();

		pivit.add(words.get(words.size()/2));

		for(Word w : words) {
			if(!pivit.get(0).equals(w))
			{
				if(w.appearence > pivit.get(0).appearence)
				{
					high.add(w);
				} else
					if(w.appearence < pivit.get(0).appearence)
					{
						low.add(w);
					} else
					{
						pivit.add(w);
					}
			}
		}

		Words out = new Words();
		out.addAll(quickSortNumber(high));
		out.addAll(pivit);
		out.addAll(quickSortNumber(low));

		return out;
	}

}
