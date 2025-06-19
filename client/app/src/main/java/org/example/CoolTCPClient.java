package org.example;

import java.net.Socket;

public class CoolTCPClient {
    private String SERVER_ADDRESS = "localhost"; // Default server address
    private int SERVER_PORT = 12345; // Default server port
    private Socket SOCKET;
    public CoolTCPClient() {
        try {
            SOCKET = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to server at " + SERVER_ADDRESS + ":" + SERVER_PORT);
            // Additional code to handle communication with the server can be added here
        } catch (Exception e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }
    public CoolTCPClient(String serverAddress, int serverPort) {
        this.SERVER_ADDRESS = serverAddress;
        this.SERVER_PORT = serverPort;
        try {
            SOCKET = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to server at " + SERVER_ADDRESS + ":" + SERVER_PORT);
        } catch (Exception e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }
    public Socket getSocket() {
        return SOCKET;
    }
    public void SendMessage(String message) {
        try {
            if (SOCKET != null && !SOCKET.isClosed()) {
                SOCKET.getOutputStream().write(message.getBytes());
                SOCKET.getOutputStream().flush();
                System.out.println("Message sent: " + message);
            } else {
                System.err.println("Socket is not connected.");
            }
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
    public void CloseConnection() {
        try {
            if (SOCKET != null && !SOCKET.isClosed()) {
                SOCKET.close();
                System.out.println("Connection closed.");
            }
        } catch (Exception e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
    public String recieveMessage() {
        StringBuilder message = new StringBuilder();
        try {
            if (SOCKET != null && !SOCKET.isClosed()) {
                byte[] buffer = new byte[1024];
                int bytesRead = SOCKET.getInputStream().read(buffer);
                if (bytesRead > 0) {
                    message.append(new String(buffer, 0, bytesRead));
                    System.out.println("Message received: " + message);
                } else {
                    System.out.println("No message received.");
                }
            } else {
                System.err.println("Socket is not connected.");
            }
        } catch (Exception e) {
            System.err.println("Error receiving message: " + e.getMessage());
        }
        return message.toString();
    }
}
