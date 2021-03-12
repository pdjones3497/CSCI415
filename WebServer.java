/**
 * ***
 **
 ** USCA ACSC415 *
 */

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

class WebServer {

    public static void main(String argv[]) throws Exception {
        String clientSentence = " ";
        String serverSentence = " ";
        ServerSocket welcomeSocket = null;

        // Create new serverSocket.
        try {
            welcomeSocket = new ServerSocket(80);

            while (true) {
                // Create client socket, blocking if no incoming request.
                Socket connectionSocket = welcomeSocket.accept();
                System.out.println("Accept Connection From" + connectionSocket.getRemoteSocketAddress());

                // input stream
                Scanner inFromClient = new Scanner(connectionSocket.getInputStream());
 
                // output stream
                DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

                // Get the message from the client.
                // HTTP Request   ---   GET /main.html HTTP/1.1
                clientSentence = inFromClient.nextLine();
                System.out.println("From Client:" + clientSentence);

                // Tokenize this line, and check whether it is a valid request.
                String[] temp = clientSentence.split(" ");
                System.out.println("HTTP Request Method:" + temp[0]);
                System.out.println("File Path: "+ temp[1]);
                
        		String statusLine = "";
        		String headLines1 = "";
        		String headLines2 = "";
        		
                // Get the file if the request is valid.
                if (temp[0].contentEquals("GET") ) {
                	
                    // You need to ignore the request for favicon.ico.
                    if (!temp[1].equals("/favicon.ico") ) {
                    	
                    	// Otherwise get the name of the file   ---   temp[1], and send the file requested by client.     
                    	try {
                    		
                    		// Direct to index.html if client does not request file.
                            if (temp[1].equals("/") )
                            	temp[1] = "/index.html";	
                           
                        	Path inFilePath = Paths.get("src" + temp[1]); // current directory
                      	
                        	// Read file into byte arrays (all in memory!!!).
                        	byte[] buffer = Files.readAllBytes(inFilePath); // Read bytes to a buffer.                        		
                        		
                        	if (temp[1].contains(".pdf") ) {
                        		// Construct the response message and send it to the client.
                        		statusLine = "HTTP/1.1 200 OK\r\n";
                        		headLines1 = "Content-Type: application/pdf\r\n";
                        		headLines2 = "Content-Length:" + buffer.length + "\r\n\r\n";
                        			
                            	//System.out.println("In try, if 1: pdf " + temp[1].contains(".pdf"));
                        	}
                        		
                        	if (temp[1].contains(".txt") || temp[1].contains(".html") ) {
                        		// Construct the response message and send it to the client.
                        		statusLine = "HTTP/1.1 200 OK\r\n";
                        		headLines1= "Content-Type: text/html\r\n";
                        		headLines2= "Content-Length:" + buffer.length + "\r\n\r\n";
                        			
                        		//System.out.println("In try, if 2: text/html " + (temp[1].contains(".txt") || temp[1].contains(".html") ) );
                        	}
                        		
                        	if (temp[1].contains(".jpg") ) {
                        		// Construct the response message and send it to the client.
                        		statusLine = "HTTP/1.1 200 OK\r\n";
                        		headLines1 = "Content-Type: image/jpg\r\n";
                        		headLines2 = "Content-Length:" + buffer.length + "\r\n\r\n";
                        			
                            	//System.out.println("In try, if 1: jpg " + temp[1].contains(".jpg"));
                        	}
                        		
                        	//System.out.println("In try, after if");

                    		// Send the statusLine and headlines.
                    		serverSentence = statusLine + headLines1 + headLines2;
                    		outToClient.writeBytes(serverSentence);
                    			
                        	// Send the entity body.
                        	outToClient.write(buffer,0,buffer.length);
                        	outToClient.flush();
                        		
                    	} catch (InvalidPathException e) {
                    		// Send 404 request if the file is not found.
                    		statusLine = "HTTP/1.1 404 Not Found\r\n";
                    		outToClient.writeBytes(statusLine);

                    		System.err.println(e.getMessage() );
                    	}
                    }
                }

                else {
            		statusLine = "HTTP/1.1 400 Bad Request\r\n";
            		outToClient.writeBytes(statusLine);
            		//System.out.println("In else");
                }
                
                // Close stream and socket.
                inFromClient.close();
                outToClient.close();
                connectionSocket.close();

            }
        } catch (IOException e) {
            System.out.println("Caught Exception " + e.getMessage());
            
        } finally {
            if (welcomeSocket !=null) welcomeSocket.close();  // Not necessary in Java 7 and newer.
            //System.out.println("In finally");
        }
    }
}