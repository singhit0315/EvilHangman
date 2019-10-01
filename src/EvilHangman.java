import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class EvilHangman {

    // map to store all dictionary words where key is length of word and
    // value is a list of all words of that length
    public static Map<Integer, List<String>> dictionary = new HashMap<Integer, List<String>>();

    // state of the game
    public static String state;

    static boolean letterMatched = false;


    // Load all dictionary words from file google-10000-english.txt
    private static void loadDictionaryWord() {
        try {
            Scanner fileReader = new Scanner(new File("google-10000-english.txt"));
            String word;
            while (fileReader.hasNextLine()) {
                word = fileReader.nextLine().toUpperCase();
                int wordLen = word.length();
                if (dictionary.containsKey(wordLen)) {
                    dictionary.get(wordLen).add(word);
                } else {
                    List<String> words = new ArrayList<String>();
                    words.add(word);
                    dictionary.put(wordLen, words);
                }
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("ERROR11: " + e.getMessage());
        }
    }

    private static String evalGuessOnWord(String word, List<Character> guessedLetters, char guessLetter) {
        String result = "";
        for (int i = 0; i < word.length(); i++) {
            if (guessLetter == word.charAt(i) || guessedLetters.contains(word.charAt(i))) {
                result += word.charAt(i);
                //letterMatched = true;
            } else {
                result += "-";
            }
        }
        return result;
    }

    // Evaluate a user guess letter and return the new family of words
    private static List<String> evalGuess(List<String> wordList, List<Character> guessedLetters, char guessLetter) {

        String oldState = state;

        Map<String, List<String>> newFamilies = new HashMap<String, List<String>>();

        //boolean letterNotFound = true;

        for (String word : wordList) {

            //if(!word.contains(guessLetter+"")) continue;
            //letterNotFound = false;
            String result = evalGuessOnWord(word, guessedLetters, guessLetter);
            if (newFamilies.containsKey(result)) {
                newFamilies.get(result).add(word);
            } else {
                List<String> words = new ArrayList<String>();
                words.add(word);
                newFamilies.put(result, words);
            }
        }

        //if(letterNotFound) return wordList;

        List<String> newWordList = null;
        int maxCount = 0;
        for (String result : newFamilies.keySet()) {
            if (maxCount < newFamilies.get(result).size()) {
                maxCount = newFamilies.get(result).size();
                state = result;
                newWordList = newFamilies.get(result);
            }
        }
        letterMatched = !oldState.equals(state);
        return newWordList;
    }


    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        //-------------------Read all inputs for the game from user -------------
        // Read length of secret word from user
        System.out.print("Enter the length of word to choose: ");
        int wordLength = input.nextInt();
        input.nextLine();

        // Read the number of player guesses
        System.out.print("Enter the number of guesses allowed: ");
        int guessesLeft = input.nextInt();
        input.nextLine();

        ////------------------- Read the words from dictionary file  -------------
        loadDictionaryWord();

        //------------------- Play the game ---------------------------------------
        List<String> secretWords = dictionary.get(wordLength);

        Random rand = new Random();
        boolean gameOver = false;
        int guessesMade = 0;
        List<Character> guessedLetters = new ArrayList<Character>();
        state = "";
        for (int i = 0; i < wordLength; i++) {
            state += "-";
        }

        // loop of each player guess
        while (!gameOver) {
            letterMatched = false;
            Collections.sort(guessedLetters);
            System.out.println("\nPossible words = " + secretWords.size());
            System.out.println("Guesses remaining: " + (guessesLeft - guessesMade));
            System.out.println("Guessed Letters = " + guessedLetters.toString());
            System.out.println("Current State = " + state);
            System.out.print("Please enter your guess: ");
            char guessLetter = input.nextLine().toUpperCase().charAt(0);

            if (!guessedLetters.contains(guessLetter)) {
                secretWords = evalGuess(secretWords, guessedLetters, guessLetter);
                if (!letterMatched)
                    guessesLeft--;
                gameOver = (guessesLeft == 0);
                guessedLetters.add(guessLetter);
            }
            if (!state.contains("-")) {
                System.out.println("You won! Congratualtions.");
                return;
            }
        }
        input.close();
        System.out.println("You lose! The word was: " + secretWords.get(rand.nextInt(secretWords.size())));
    }

}
