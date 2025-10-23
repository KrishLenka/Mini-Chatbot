import java.util.ArrayList;
import java.util.HashMap;

/**
 * This Mini-Chatbot is a simple text generator that creates sentences using graph algorithms.
 * 
 * It uses the concept of a Markov Chain to generate sentences based on a dictionary of words.
 * Each chain represents a valid sentence structure, and the words are connected based on their types.
 * 
 * The graph is used to store the words, with directed edges representing valid transitions between words of different types.
 * To generate a sentence, it uses Dijkstra's algorithm to find the most likely path through the graph based on the frequency of words.
 */
public class Chatbot {
     
    public static final String[][] SENTENCE_STRUCTURES = {
        {"Subject", "Verb", "Noun"},                              // "I like this"
        {"Subject", "Verb", "Noun", "Adverb"},                    // "Cat eats food quickly"
        {"Subject", "Adverb", "Verb", "Noun"},                    // "Cat quickly eats food"
        {"Subject", "Auxiliary", "Verb", "Preposition", "Noun"},  // "I don't agree with that"
        {"Subject", "Auxiliary", "Verb", "Noun"},                 // "I don't like this"
        {"Subject", "Verb", "Preposition", "Noun"},               // "I agree with that"
        {"Subject", "Auxiliary", "Verb"},                         // "I don't care"
        {"Subject", "Verb", "Adverb"}                             // "I agree completely"
    };
     
    private Word[] adjacencyList;  
     
    /**
     * To complete this method, you will read each line of the given CSV file
     * 
     * Create a HashMap<String, ArrayList<Word>>
     * Each key is a String (the type of word e.g., "Subject", "Verb", etc.)
     * The keys each correspond to an ArrayList of Word objects of that type
     *
     * Build a Word object from each line, then add it to the hash map,
     * in its appropriate arraylist.
     *
     * @param fileName CSV filename to read
     */
    public HashMap<String, ArrayList<Word>> buildDictionary(String fileName) {
        // WRITE YOUR CODE HERE
        StdIn.setFile(fileName);
        HashMap<String, ArrayList<Word>> wordsByType = new HashMap<>();
        while (!StdIn.isEmpty()) {
            String line = StdIn.readLine();
            String[] parts = line.split(",");
            
            String text = parts[0];
            int frequency = Integer.parseInt(parts[1]);
            String type = parts[2];
            
            Word word = new Word(text, frequency, type); 
            
            wordsByType.putIfAbsent(type, new ArrayList<>());
            wordsByType.get(type).add(word);
        }
        return wordsByType;
    }
     
    /**
     * To complete this method, you will build a directed graph of words
     * The edges of this graph will be based on the sentence structures defined in SENTENCE_STRUCTURES
     * 
     * All subjects will be connected to auxillary/verbs, verbs to nouns/prepositions/adverbs, etc. 
     * 
     * @param wordsByType Hashmap of words by type from buildDictionary()
     */
    public void buildWordGraph(HashMap<String, ArrayList<Word>> wordsByType) {
        // WRITE YOUR CODE HERE
        ArrayList<Word> allWords = new ArrayList<>();
        for (ArrayList<Word> wordList : wordsByType.values()) {
            allWords.addAll(wordList);
        }   

        adjacencyList = new Word[allWords.size()];
        for (int i = 0; i < allWords.size(); i++) {
            adjacencyList[i] = allWords.get(i);
        }
        
        for (String[] structure : SENTENCE_STRUCTURES) {
            for (int i = 0; i < structure.length - 1; i++) {
                String fromType = structure[i];
                String toType = structure[i + 1];

                ArrayList<Word> fromWords = wordsByType.getOrDefault(fromType, new ArrayList<>());
                ArrayList<Word> toWords = wordsByType.getOrDefault(toType, new ArrayList<>());

                for (Word fromWord : fromWords) {
                    int fromIndex = allWords.indexOf(fromWord);
                    
                    for (Word toWord : toWords) {
                        boolean edgeExists = false;
                        Word current = adjacencyList[fromIndex].getNext();
                        while (current != null) {
                            if (current.equals(toWord)) {
                                edgeExists = true;
                                break;
                            }
                            current = current.getNext();
                        }
                        
                        if (!edgeExists) {
                            Word edge = new Word(toWord.getText(), toWord.getFrequency(), toWord.getType());
                            edge.setNext(adjacencyList[fromIndex].getNext());
                            adjacencyList[fromIndex].setNext(edge);
                        }
                    }
                }
            }
        }
    }

    /**
     * Extracts subjects from the user's input prompt that exist in our dictionary.
     * This allows the chatbot to respond with some context related to the user's message.
     * 
     * @param prompt User's input message
     */
    private ArrayList<Word> extractSubjectsFromPrompt(String prompt) {
        // WRITE YOUR CODE HERE
        ArrayList<Word> foundSubjects = new ArrayList<>();
        
        // Parse words manually without regex
        ArrayList<String> words = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();
        String lowerPrompt = prompt.toLowerCase();
        
        for (int i = 0; i < lowerPrompt.length(); i++) {
            char c = lowerPrompt.charAt(i);
            if ((c >= 'a' && c <= 'z') || c == ' ') {
                if (c == ' ') {
                    if (currentWord.length() > 0) {
                        words.add(currentWord.toString());
                        currentWord = new StringBuilder();
                    }
                } else {
                    currentWord.append(c);
                }
            }
        }
        if (currentWord.length() > 0) {
            words.add(currentWord.toString());
        }
        
        // Find matching subjects (case-insensitive without equalsIgnoreCase)
        for (String word : words) {
            for (int i = 0; i < adjacencyList.length; i++) {
                if (adjacencyList[i].getType().equals("Subject") && 
                    adjacencyList[i].getText().toLowerCase().equals(word)) {
                    foundSubjects.add(adjacencyList[i]);
                }
            }
        }
        
        return foundSubjects;
    }
    
    /**
     * Boosts words mentioned in the user's prompt to make them more likely to appear in responses.
     * Directly modifies frequency by +100.
     * 
     * @param prompt User's input message
     * @return true if "the" was found before a subject (for capitalization handling)
     */
    public boolean boostWordsFromPrompt(String prompt) {
        // WRITE YOUR CODE HERE
        boolean hasThePrefix = false;
        
        // Parse words manually without regex
        ArrayList<String> words = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();
        String lowerPrompt = prompt.toLowerCase();
        
        for (int i = 0; i < lowerPrompt.length(); i++) {
            char c = lowerPrompt.charAt(i);
            if ((c >= 'a' && c <= 'z') || c == ' ') {
                if (c == ' ') {
                    if (currentWord.length() > 0) {
                        words.add(currentWord.toString());
                        currentWord = new StringBuilder();
                    }
                } else {
                    currentWord.append(c);
                }
            }
        }
        if (currentWord.length() > 0) {
            words.add(currentWord.toString());
        }
        
        // Check for "The" + subject pattern (case-insensitive without equalsIgnoreCase)
        for (int i = 0; i < words.size() - 1; i++) {
            if (words.get(i).equals("the")) {
                // Check if next word is a subject
                for (int j = 0; j < adjacencyList.length; j++) {
                    if (adjacencyList[j].getType().equals("Subject") && 
                        adjacencyList[j].getText().toLowerCase().equals(words.get(i + 1))) {
                        hasThePrefix = true;
                        break;
                    }
                }
            }
        }
        
        // Boost all words mentioned in the prompt by directly modifying frequency
        for (String word : words) {
            // Boost the word itself (case-insensitive without equalsIgnoreCase)
            for (int i = 0; i < adjacencyList.length; i++) {
                if (adjacencyList[i].getText().toLowerCase().equals(word)) {
                    adjacencyList[i].addFrequency(100);
                }
            }
        }
        
        return hasThePrefix;
    }
    
    /**
     * Resets all word frequencies by subtracting the boost amount.
     * Called after generating a response to ensure boosts don't compound.
     */
    public void resetAllBoosts() {
        for (int i = 0; i < adjacencyList.length; i++) {
            adjacencyList[i].addFrequency(-100);
        }
    }
    
    /**
     * Generates a sentence based on the given prompt.
     * 
     * This method uses context from the user's input by identifying subjects
     * mentioned in the prompt and preferring them for the response.
     * 
     * The sentence is constructed by traversing the graph in one of the 
     * valid sentence structures defined in SENTENCE_STRUCTURES.
     * Uses a greedy best-first search approach to find the highest-scoring path:
     * at each step, selects the word with the highest score (frequency + hash variation)
     * that matches the expected word type for that position in the sentence structure.
     * 
     * This is slightly randomized (via hash variation), so a wider variety of sentences are produced.
     * 
     * Note: this is NOT how actual LLMs work. Instead, this is a simple test generator.
     * @param prompt User's input message
     * @param useThePrefix Whether to prefix the sentence with "the" (detected from user input)
     * @return A contextual sentence using subjects from the input when possible
     */
    public String generateMostLikelySentence(String prompt, boolean useThePrefix) {  
        ArrayList<Word> contextualSubjects = extractSubjectsFromPrompt(prompt);
        ArrayList<Word> subjects = new ArrayList<>();
        if (contextualSubjects.isEmpty()) {
            for (int j = 0; j < adjacencyList.length; j++) {
                if (adjacencyList[j].getType().equals("Subject")) {
                    subjects.add(adjacencyList[j]);
                }
            }
        } else {
            subjects = contextualSubjects;
        }

        Word startWordObj;
        if (!contextualSubjects.isEmpty()) {
            startWordObj = contextualSubjects.get(0);
        } else {
            startWordObj = (subjects.isEmpty()) ? null : subjects.get(Math.abs(prompt.hashCode()) % subjects.size());
        }
        if (startWordObj == null) {
            return "I don't have anything to say.";
        }
        String startWord = startWordObj.getText();
        String[] chosenStructure = SENTENCE_STRUCTURES[Math.abs(startWord.hashCode()) % SENTENCE_STRUCTURES.length]; 
        StringBuilder sentence = new StringBuilder();
        if (useThePrefix) {
            sentence.append("the ").append(startWord);
        } else {
            sentence.append(startWord);
        }

        Word curr = null;
        for (int i = 0; i < adjacencyList.length; i++) {
            if (adjacencyList[i] == startWordObj || adjacencyList[i].getText().equalsIgnoreCase(startWord)) {
                curr = adjacencyList[i];
                break;
            }
        }
        
        if (curr == null) {
            return sentence.toString();
        }
        
        curr = curr.getNext();  

        int pos = 1;
        while (pos < chosenStructure.length) { 
            String expectedType = chosenStructure[pos++]; 

            Word bestWord = null;
            int bestScore = Integer.MIN_VALUE;
            
            while (curr != null) {
                if (curr.getType().equals(expectedType)) {
                    int hashVariation = Math.abs((sentence.toString() + curr.getText()).hashCode()) % 50;
                    int score = curr.getFrequency() + hashVariation;
                    
                    if (score > bestScore) {
                        bestScore = score;
                        bestWord = curr;
                    }
                }
                curr = curr.getNext();
            }
            
            if (bestWord != null) {
                sentence.append(" ").append(bestWord.getText()); 
                
                for (int i = 0; i < adjacencyList.length; i++) {
                    if (adjacencyList[i].equals(bestWord)) {
                        curr = adjacencyList[i].getNext();
                        break;
                    }
                }
            }
        } 
        
        String response = sentence.toString();
        if (response.length() > 0) {
            response = Character.toUpperCase(response.charAt(0)) + response.substring(1) + ".";
        }
        
        return response;
    } 

    public Word[] getAdjacencyList() {
        return adjacencyList;
    }
}