import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;

 
public class Driver extends JFrame {

    public static final String FILENAME = "lexicon.csv";
    
    private Chatbot chat;
    private JTextArea chatArea;
    private JTextField inputField;
    private JScrollPane scrollPane; 
    private JButton sendChatButton; 
    
    public Driver() {  
        chat = new Chatbot();
        HashMap<String, ArrayList<Word>> dictionary = chat.buildDictionary(FILENAME); 
        chat.buildWordGraph(dictionary);

        createChatWindow();

        addGPTChat("Welcome to BabyGPT!", false);
        addGPTChat("I'm a simple text generator that creates sentences using graph algorithms.", false);
        addGPTChat("Send a message, and I will generate a response.", false);
        addGPTChat("To create sentences, I traverse your graph to string together words.", false);
        addGPTChat("These may not necessarily make sense, but they will be (close to) valid sentences!\n", false);
        addGPTChat("Disclaimer: My responses are algorithmically generated and do not reflect understanding or opinions related to your input. I generate sentences based on graph algorithms, not the meaning of what you type.\n", false);
    }
     
    private void createChatWindow() {
        setTitle("BabyGPT");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        chatArea.setBackground(new Color(248, 249, 250));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Chat"));
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
        
        JPanel textInputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        inputField.setFont(new Font("Times New Roman", Font.PLAIN, 14)); 
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    processUserInput();
                }
            }
        });
        
        sendChatButton = new JButton("Send");
        sendChatButton.setPreferredSize(new Dimension(80, 35));
        sendChatButton.setBackground(new Color(0, 123, 255));
        sendChatButton.setForeground(Color.WHITE);
        sendChatButton.setFocusPainted(false);
        sendChatButton.addActionListener(e -> processUserInput());
        
        textInputPanel.add(inputField, BorderLayout.CENTER);
        textInputPanel.add(sendChatButton, BorderLayout.EAST);
        
        inputPanel.add(controlPanel, BorderLayout.NORTH);
        inputPanel.add(textInputPanel, BorderLayout.CENTER);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        
        add(mainPanel); 
    }
     

    
    private void processUserInput() {
        String userInput = inputField.getText().trim();
        if (userInput.isEmpty()) return;
        
        addUserChat(userInput);
        inputField.setText(""); 
         
        // Boost words from user input and check for "the" prefix
        boolean hasThePrefix = chat.boostWordsFromPrompt(userInput);
        
        // Generate response with boosted words
        String response = chat.generateMostLikelySentence(userInput, hasThePrefix); 
        
        // Reset boosts after generation (so they only apply to one sentence)
        chat.resetAllBoosts();
        
        addGPTChat(response+"\n", true);
    }
     
    
    private void addUserChat(String prompt) {
        String timestamp = java.time.LocalTime.now().toString().substring(0, 5);
        String msg = "[" + timestamp + "] You: " + prompt + "\n";
        chatArea.append(msg);
        SwingUtilities.invokeLater(() -> {
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
    
    private void addGPTChat(String response, boolean animate) {
        String timestamp = java.time.LocalTime.now().toString().substring(0, 5);
        String msg = "[" + timestamp + "] BabyGPT: ";
        if (animate) {
            inputField.setEditable(false);
            chatArea.append(msg);
            startTypingAnimation(response+"\n");
        } else {
            chatArea.append(msg + response + "\n");
        }  
        SwingUtilities.invokeLater(() -> {
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    private void startTypingAnimation(String message) {  
        new Timer(37, new ActionListener() {
            int index = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < message.length()) {
                    chatArea.append(String.valueOf(message.charAt(index)));
                    index++;
                    chatArea.setCaretPosition(chatArea.getDocument().getLength());
                } else {
                    inputField.setEditable(true);
                    ((Timer)e.getSource()).stop();
                }
            }
        }).start();  
    }   
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Driver().setVisible(true);
        });
    }
}
