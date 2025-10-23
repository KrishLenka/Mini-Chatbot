# Mini-Chatbot
Mini-Chatbot employing graph algorithms and a Markov Chain model in Java with simple conversational memory.

## Compilation and Running

### Compile the program:
```bash
javac -d bin src/*.java
```

### Run the program:
java -cp bin Driver

### Clean before recompiling:
rm -rf bin/*

## How it Works
**Sentence Structures:**
The bot uses 8 different sentence patterns:
1. Subject + Verb + Noun
2. Subject + Verb + Noun + Adverb
3. Subject + Adverb + Verb + Noun
4. Subject + Auxiliary + Verb + Preposition + Noun
5. Subject + Auxiliary + Verb + Noun
6. Subject + Verb + Preposition + Noun
7. Subject + Auxiliary + Verb
8. Subject + Verb + Adverb


**buildDictionary()**
The buildDictionary() method takes in a csv file in the format:
Word, Frequency, Type
For each row, the method will create a "word node" and append it to a HashMap where each key is a word type (Subject, Verb, Preposition, etc.).

Note: The intent of the assignment was to have collision resolution done using separate chaining via linked lists of word nodes. However, for the sake of simplicity and efficiency, the collision resolution is done using separate chaining via ArrayList of word nodes.

**buildWordGraph()**
Using the HashMap created in buildDictionary(), this method loops through every sentence structure, creating valid markov chains with every word in the HashMap. For instance, the first sentence structure is Subject + Verb + Noun. This method will go through every word node under the Subject key in the hashmap. For every Subject Word node, the method will go through every word node under the Verb key, and so on. In the end, there will be a permutation of adjacency lists with every possible sentence.

**extractSubjectsFromPrompt()**
**boostWordsFromPrompt()**
**resetAllBoosts()**
**generateMostLikelySentence()**
**getAdjacencyList()**