/*
 * Example JavaFX Application
 */
package org.example;

import org.example.CoolTCPClient;
import org.example.NameGenerator;

import java.awt.FlowLayout;
import java.text.ParseException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.commons.cli.*; // Import necessary classes

public class ChatApp {
    private static CoolTCPClient client;
    public static void main(String[] args) {
        Options options = new Options();

        String server = "localhost"; // Default server address
        String port = "12345"; // Default server port

        // Add a help option (-h or --help)
        Option helpOption = new Option("h", "help", false, "Print this help message");
        Option serverOption = new Option("s", "server", true, "Server address (default: localhost)");
        Option portOption = new Option("p", "port", true, "Server port (default: 12345)");
        options.addOption(serverOption);
        options.addOption(portOption);
        options.addOption(helpOption);
        // Step 3: Create a parser
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null; // Object to hold the parsed command line

        try {
            // Step 4: Parse the command line arguments
            cmd = parser.parse(options, args);
        } catch (Exception e) {
            System.out.println("Error parsing command line arguments: " + e.getMessage());
            System.exit(1);
        }

        if (cmd != null) {

            // Check if the help option was used
            if (cmd.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("java CliExample", options);
                // Don't continue processing if help was requested
                System.exit(0);
            }
            if (cmd.hasOption("s")) {
                server = cmd.getOptionValue("s");
            }
            if (cmd.hasOption("p")) {
                port = cmd.getOptionValue("p");
            }
        }
        NameGenerator nameGenerator = new NameGenerator();
        String name = nameGenerator.generateName();
        if (server != null && port != null) {
            try {
                int portNumber = Integer.parseInt(port);
                client = new CoolTCPClient(server, portNumber);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number: " + port);
                System.exit(1);
            }
        } else {
            client = new CoolTCPClient();
        }
        client.SendMessage(name + " has joined the chat!");
        JFrame frame = new JFrame("Chat Application");
        JPanel titlePanel = new JPanel();
        JPanel contentPanel = new JPanel();
        JPanel inputPanel = new JPanel();
        JLabel titleLabel = new JLabel("Welcome to the Chat Application!", JLabel.CENTER);
        javax.swing.JTextField inpTextField = new javax.swing.JTextField(30);
        JTextArea textArea = new JTextArea(10, 30);
        textArea.setEditable(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(titleLabel);
        titleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(textArea);
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        inputPanel.add(inpTextField);
        frame.setLayout(new FlowLayout());
        frame.add(titlePanel);
        frame.add(contentPanel);
        frame.add(inputPanel);
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null); // Center the window on the screen

        inpTextField.addActionListener(e -> {
            String message = inpTextField.getText();
            client.SendMessage(name + ": " + message);
            inpTextField.setText("");
        });
        while (true) {
            String message = client.recieveMessage();
            if (message != null && !message.isEmpty()) {
                textArea.append(message);
            }
            try {
                Thread.sleep(100); // Polling interval
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
