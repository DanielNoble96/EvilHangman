/*  Student information for assignment:
 *
 *  On my honor, <NAME>, this programming assignment is my own work
 *  and I have not provided this code to any other student.
 *
 *  Name:
 *  email address:
 *  UTEID:
 *  Section 5 digit ID: 
 *  Grader name:
 *  Number of slip days used on this assignment:
 */

// add imports as necessary

import java.util.Set;
import java.util.*;
import java.util.TreeMap;

/**
 * Manages the details of EvilHangman. This class keeps
 * tracks of the possible words from a dictionary during
 * rounds of hangman, based on guesses so far.
 *
 */
public class HangmanManager {

    // instance vars
	Set<String> words; 						// all the words 
	ArrayList<String> activeWords; 				// the eligible active words
	Set<Character> guessedLetters; 			// all the letters that have been guessed
	int wordLength;							// length of the word
	int numGuesses;							// number of guesses left
	int numWords; 							// number of eligible words
	int diff;								// game difficulty
	String pattern; 						// pattern of the thing
	

    /**
     * Create a new HangmanManager from the provided set of words and phrases.
     * pre: words != null, words.size() > 0
     * @param words A set with the words for this instance of Hangman.
     * @param debugOn true if we should print out debugging to System.out.
     */
    public HangmanManager(Set<String> words, boolean debugOn) {
    	this.words = words;
    }

    /**
     * Create a new HangmanManager from the provided set of words and phrases. 
     * Debugging is off.
     * pre: words != null, words.size() > 0
     * @param words A set with the words for this instance of Hangman.
     */
    public HangmanManager(Set<String> words) {
    	this.words = words;
    	
    }


    /**
     * Get the number of words in this HangmanManager of the given length.
     * pre: none
     * @param length The given length to check.
     * @return the number of words in the original Dictionary with the given length
     */
    public int numWords(int length) {
    	int n = 0;
    	Iterator<String> itr = words.iterator();
    	while (itr.hasNext()) {
    		if (itr.next().length() == length) {
    			n++;
    		}
    	}
        return n;
    }


    /**
     * Get for a new round of Hangman. Think of a round as a complete game of Hangman.
     * @param wordLen the length of the word to pick this time. numWords(wordLen) > 0
     * @param numGuesses the number of wrong guesses before the player loses the round. numGuesses >= 1
     * @param diff The difficulty for this round.
     */
    public void prepForRound(int wordLen, int numGuesses, HangmanDifficulty diff) {
    	// set up difficulty, word length, number of guesses
    	this.diff = diff.ordinal();
    	wordLength = wordLen;
    	this.numGuesses = numGuesses;
    	// set up sets for the guessed letters and active words from the dictionary
    	guessedLetters = new HashSet<Character>();
    	activeWords = new ArrayList<String>();
    	for (String s : words) {
    		if (s.length() == wordLength) {
    			activeWords.add(s);
    		}
    	}
    	// set up the current pattern
    	pattern = "";
    	for (int i = 0; i < wordLen; i++) {
    		pattern += "-";
    	}
    	numWords = activeWords.size();
    }


    /**
     * The number of words still possible (live) based on the guesses so far. Guesses will eliminate possible words.
     * @return the number of words that are still possibilities based on the original dictionary and the guesses so far.
     */
    public int numWordsCurrent() {
        return activeWords.size();
    }


    /**
     * Get the number of wrong guesses the user has left in this round (game) of Hangman.
     * @return the number of wrong guesses the user has left in this round (game) of Hangman.
     */
    public int getGuessesLeft() {
        return numGuesses;
    }


    /**
     * Return a String that contains the letters the user has guessed so far during this round.
     * The String is in alphabetical order. The String is in the form [let1, let2, let3, ... letN].
     * For example [a, c, e, s, t, z]
     * @return a String that contains the letters the user has guessed so far during this round.
     */
    public String getGuessesMade() {
        return guessedLetters.toString();
    }


    /**
     * Check the status of a character.
     * @param guess The characater to check.
     * @return true if guess has been used or guessed this round of Hangman, false otherwise.
     */
    public boolean alreadyGuessed(char guess) {
    	boolean guessed = false;
    	if (guessedLetters.contains(guess)) {
    		guessed = true;
    	}
        return guessed;
    }


    /**
     * Get the current pattern. The pattern contains '-''s for unrevealed (or guessed)
     * characters and the actual character for "correctly guessed" characters.
     * @return the current pattern.
     */
    public String getPattern() {
        return pattern;
    }


    // pre: !alreadyGuessed(ch)
    // post: return a tree map with the resulting patterns and the number of
    // words in each of the new patterns.
    // the return value is for testing and debugging purposes
    /**
     * Update the game status (pattern, wrong guesses, word list), based on the give
     * guess.
     * @param guess pre: !alreadyGuessed(ch), the current guessed character
     * @return return a tree map with the resulting patterns and the number of
     * words in each of the new patterns.
     * The return value is for testing and debugging purposes.
     */
    public TreeMap<String, Integer> makeGuess(char guess) {
    	// Strings will be the possible patterns, and ArrayLists will be the possible words in each
    	TreeMap<String, ArrayList<String>> families = new TreeMap<String, ArrayList<String>>();
    	TreeMap<String, Integer> patternCount = new TreeMap<String, Integer>();
    	guessedLetters.add(guess);
 
   	    // get a set of all possible patterns
    	Set<String> allPatterns = new HashSet<String>();
    	for (String s : activeWords) {
    		String wordPattern = generateWordPattern(s, guess);
    		allPatterns.add(wordPattern);
    	}
    	
    	// go through each of the possible patterns and find all of the words/number of them 
    	// put the pattern (key) and value (list of words) into TreeMap families
    	for (String el : allPatterns) {
    		ArrayList<String> addWords = new ArrayList<String>();
    		for (String s : activeWords) {
    			String Pattern = generateWordPattern(s, guess);
    			if (Pattern.equals(el)) {
    				addWords.add(s);
    			}
    		}
    		families.put(el, addWords);
			patternCount.put(el, addWords.size());
    	}
    	// update pattern: find which pattern in patternCount has largest key 
    	
    	// update activeWords
    	findHardest(patternCount, families);
    	if (!pattern.contains(Character.toString(guess))) {
    		numGuesses--;
    	}
    	
    	
        return patternCount;
    }
    // helper function to find the hardest pattern
	public void findHardest(TreeMap<String, Integer> patternCount, TreeMap<String, ArrayList<String>> families) {
    	if (oneMaxFamily(patternCount)) {
    		activeWords = families.get(maxPattern(patternCount));
    	} else {
    		activeWords = families.get(tieBreaker(patternCount, families));
    	}
    }
    
    // helper function to get the max family 
    public String maxPattern(TreeMap<String, Integer> patternCount) {
    	String clone = "";
    	int max = 0;
    	for (Map.Entry<String, Integer> entry : patternCount.entrySet()) {
    		if (entry.getValue() > max) {
    			max = entry.getValue();
    			pattern = entry.getKey();
    			clone = entry.getKey();
    		}
    	}
    
    	return clone;
    }
    // helper function to check if there is only one maximum family
    public boolean oneMaxFamily(TreeMap<String, Integer> patternCount) {
    	int count = 0;
    	int max = 0;
    	for (Map.Entry<String, Integer> entry : patternCount.entrySet()) {
    		if (entry.getValue() > max) {
    			max = entry.getValue();
    		}
    	}
    	for (Map.Entry<String, Integer> entry : patternCount.entrySet()) {
    		if (entry.getValue() == max) {
    			count++;
    		}
    	}
    	return (count == 1);
    }
    
    // helper function that returns a string of the pattern that will be used
    public String tieBreaker(TreeMap<String, Integer> patternCount, TreeMap<String, ArrayList<String>> families) {
    	int max = 0;
    	int moreDashes = 0;
    	int revealedChars = -1;
    	String theChosenPattern = "";
    	ArrayList<String> storeMaxPatterns = new ArrayList<String>();
    	for (Map.Entry<String, Integer> entry : patternCount.entrySet()) {
    		if (entry.getValue() > max) {
    			max = entry.getValue();
    		}
    	}
    	
    	for (Map.Entry<String, Integer> entry : patternCount.entrySet()) {
    		if (entry.getValue() == max) {
    			storeMaxPatterns.add(entry.getKey());
    		}
    	}
    	
    	for (String s : storeMaxPatterns) {
    		for (int i = 0; i < s.length(); i++) {
    			if (s.charAt(i) == '-') {
    				moreDashes++;
    			}
    		}
    		if (moreDashes > revealedChars) {
    			revealedChars = moreDashes;
    			theChosenPattern = s;
    		}
    	}
    	return theChosenPattern;
    }
    
    // helper function that generates an updated word pattern for a given guess
    public String generateWordPattern(String word, char guess) {
    	String sequence = "";
    	for (int i = 0; i < word.length(); i++) {
    		if (pattern.charAt(i) != '-') {
    			sequence += pattern.charAt(i);
    		} else if (word.charAt(i) == guess) {
    			sequence += guess;
    		} else {
    			sequence += '-';
    		}
    	}
    	return sequence;
    }

    /**
     * Return the secret word this HangmanManager finally ended up picking for this round.
     * If there are multiple possible words left one is selected at random.
     * <br> pre: numWordsCurrent() > 0
     * @return return the secret word the manager picked.
     */
    public String getSecretWord() {
        return "DEFAULT";
    }
}