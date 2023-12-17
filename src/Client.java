import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.*;
import java.net.*;

public class Client {

    static FileOutputStream fos;
    static DataInputStream dis;

    public static void client() throws Exception {
        //4293affe-609b-4f00-894d-a6a6a6871409.mshome.net
        // Get IP address of server
        /*InetAddress address = InetAddress.getByName("4293affe-609b-4f00-894d-a6a6a6871409.mshome.net");
        String serverAddress = address.getHostAddress();
        System.out.println("Client: Server IP Address = " + serverAddress);*/

        //Create client socket
        //Socket clientSocket = new Socket(serverAddress, 80);
        Socket clientSocket = new Socket("localhost", 80);
        System.out.println("Client: Client Socket is created on port 80");

//        ImageReceiver.receiveAndDisplayImage(clientSocket);

        // Create IO streams for network socket
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        System.out.println("Client: IO streams are created");

        // Send request to server
        out.println("GET /indx.html HTTP/1.1");
        out.println();
        System.out.println("Client: Request is sent to server");
        StringBuilder responseContent = receiveResponseBufferedReader(in);

//        BufferedInputStream bufferedIn = new BufferedInputStream(clientSocket.getInputStream());
//        int statusCode = receiveResponse(bufferedIn);


        // Check the response code
        if (responseContent.toString().contains("HTTP/1.1 200 OK")) {
            // Save the HTML content to a file
            String fileName = "src/receivedFile.html";
            PrintWriter writer = new PrintWriter(fileName);
            writer.write(responseContent.toString());
            writer.close();
            System.out.println("Client: HTML content is saved to " + fileName);
            writer.close();

            dis = new DataInputStream(clientSocket.getInputStream());
            fos = new FileOutputStream("src/img.png");

            long imageSize = dis.readLong();
            int counter = 0;
            int pixel;
            for (int i = 0; i < imageSize; i++) {
                pixel = dis.read();
                fos.write(pixel);
            }
            fos.flush();
            System.out.println("Client:Image  is saved to src/img.png");

            // Open the HTML file in the default web browser
            openHtmlFile("G:/SJ/Academic%20Subjects/4th%20Year/Network%20Programming/Network_Programming_Project/Network%20Programming%20Project/src/receivedFile.html");

            // Send a response message to the server
            PrintWriter serverOut = new PrintWriter(clientSocket.getOutputStream(), true);
            serverOut.println("I RECEIVED THE INFORMATION OF STUDENT’s PROJECT GROUP");
            serverOut.close();
            System.out.println("Client: Response message is sent to server correct");
        } else if (responseContent.toString().contains("HTTP/1.1 404")) {
            // Send a message to the server
            PrintWriter serverOut = new PrintWriter(clientSocket.getOutputStream(), true);
            serverOut.println("I DID NOT RECEIVED THE INFORMATION OF STUDENT’s PROJECT GROUP");
            serverOut.close();
            System.out.println("Client: Response message is sent to server incorrect");
        }

        // Close client socket
        clientSocket.close();

        // Close IO streams
        fos.close();
        dis.close();
        in.close();
        out.close();
        System.out.println("Client: IO streams and socket are closed");
    }

    public static void openHtmlFile(String filePath) throws Exception {
        // Using Desktop.getDesktop().browse(...);
        Desktop desktop = Desktop.getDesktop();
        if (desktop.isSupported(Desktop.Action.OPEN)) {
            URI uri = new URI("file:///" + filePath);
            desktop.browse(uri);
            System.out.println("Opened file " + filePath + " successfully");
        } else {
            throw new UnsupportedOperationException("Desktop open not supported");
        }

        // Using Runtime.getRuntime().exec("...");
    /*
    String os = System.getProperty("os.name").toLowerCase();
    if (os.contains("win")) {
        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + filePath);
    } else {
        // Add platform-specific code for other OSes
    }
    */
    }

    private static StringBuilder receiveResponseBufferedReader(BufferedReader in) throws IOException {
        // Receive Response
        StringBuilder responseContent = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
            /*if (line.contains("HTTP/1.1 200 OK") || line.contains("Content-Type: text/html") || line.contains("Connection: close")) {
                responseContent.append("\n");
            }*/
            if (line.equals("</html>")) {
                responseContent.append(line).append("\n");
                break;
            } else {
                responseContent.append(line).append("\n");
            }
        }
        System.out.println("Client: Response is received from server");
        return responseContent;
    }

    private static int receiveResponse(BufferedInputStream dataInputStream) {
        // Read the response header and extract status code
        byte[] responseHeader = new byte[1024];
        try {
            dataInputStream.read(responseHeader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String headerString = new String(responseHeader).trim();
        int statusCode = Integer.parseInt(headerString.split(" ")[1]);
        checkResponseCode(dataInputStream, statusCode);
        System.out.println(headerString);
        return statusCode;

    }

    private static void checkResponseCode(BufferedInputStream dataInputStream , int statusCode) {
        // Check for successful response (e.g., 200 OK)
        if (statusCode == HttpURLConnection.HTTP_OK) {
            // Read the remaining data (HTML file)
            StringBuilder html = new StringBuilder();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while (true) {
                try {
                    if (!((bytesRead = dataInputStream.read(buffer)) != -1)) break;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                html.append(new String(buffer, 0, bytesRead));
            }
            System.out.println(html.toString());

        }
    }
}