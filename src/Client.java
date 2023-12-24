import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;
public class Client {
    static Socket clientSocket;
    static FileOutputStream fos;
    static DataInputStream dis;
    static PrintWriter out;
    static BufferedReader in;
    static String imagePath = "src/img.png";
    static String CLIENT_FILE_PATH = "src/receivedFile.html";
    static String HOST_ADDRESS = "localhost";
    static int PORT = 80;
    static String SERVER_FILE_DIR = "/indx.html";

    private static void initIO(Socket clientSocket) throws IOException {
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        dis = new DataInputStream(clientSocket.getInputStream());
        fos = new FileOutputStream(imagePath);
        System.out.println("Client: IO streams are created");
    }
    public static void closeIO() throws IOException {
        fos.close();
        dis.close();
        in.close();
        out.close();
    }

    public static void client() throws Exception {
        //Create client socket
        clientSocket = new Socket(HOST_ADDRESS, PORT);
        System.out.println("Client: Client Socket is created on port "+PORT);

        // Create IO streams for network socket
        initIO(clientSocket);

        // Send request to server
        out.println("GET %s HTTP/1.1".formatted(SERVER_FILE_DIR));
        out.println()
        ;
        System.out.println("Client: Request is sent to server");
        // Receive response
        //StringBuilder responseContent = receiveResponseBufferedReader(in);

        ArrayList<StringBuilder> response = receiveResponse(in);
        String responseHeaders = response.get(0).toString();
        String responseBody = response.get(1).toString();

        // Check the response code
        if (responseHeaders.contains("HTTP/1.1 200 OK")) {
            // Save the HTML content to a file
            saveHTMLFile(CLIENT_FILE_PATH,responseBody);
            // Receive image from server and save it
            receiveImage(clientSocket,imagePath);
            // Open the HTML file in the default web browser
            openHtmlFileInBrowser(CLIENT_FILE_PATH.toString());
            //Send Confirmation message
            out.println("I RECEIVED THE INFORMATION OF STUDENT’s PROJECT GROUP");
            System.out.println("Client: Response message is sent to server correct");
        } else if (responseHeaders.contains("HTTP/1.1 404")) {
            out.println("I DID NOT RECEIVED THE INFORMATION OF STUDENT’s PROJECT GROUP");
            System.out.println("Client: Response message is sent to server incorrect");
        }

        // Close IO streams
        closeIO();
        // Close client socket
        clientSocket.close();
        System.out.println("Client: IO streams and socket are closed");
    }

    static void saveHTMLFile(String filePath,String text) throws FileNotFoundException {
        PrintWriter fileWriter = new PrintWriter(filePath);
        fileWriter.write(text);
        fileWriter.close();
        System.out.println("Client: HTML content is saved to " + filePath);
    }

    static void receiveImage(Socket clientSocket,String filePath) throws IOException {
        long imageSize = dis.readLong();
        int pixel;
        for (int i = 0; i < imageSize; i++) {
            pixel = dis.read();
            fos.write(pixel);
        }
        fos.flush();
        System.out.println("Client:Image  is saved to src/img.png");
    }
    public static void openHtmlFileInBrowser(String filePath) throws Exception {
        // Using Desktop.getDesktop().browse(...);
        Desktop desktop = Desktop.getDesktop();
        if (desktop.isSupported(Desktop.Action.OPEN)) {
            URI uri = Paths.get(filePath).toUri();
            desktop.browse(uri);
            System.out.println("Opened file " + filePath + " successfully");
        } else {
            throw new UnsupportedOperationException("Desktop open not supported");
        }
    }

    private static ArrayList<StringBuilder> receiveResponse(BufferedReader in) throws IOException {
        boolean isHTML = false;
        ArrayList<StringBuilder> response = new ArrayList<StringBuilder>();
        // Receive Response
        StringBuilder responseHeaders = new StringBuilder();
        StringBuilder responseBody = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
            if (line.equals("</html>")) {
                responseHeaders.append(line).append("\n");
                break;
            }
            if(line.contains("<!DOCTYPE html>"))
                isHTML = true;
            if (isHTML) {
                responseBody.append(line).append("\n");
            } else {
                responseHeaders.append(line).append("\n");
            }
        }
        System.out.println("Client: Response is received from server");
        response.add(responseHeaders);
        response.add(responseBody);
        return response;
    }

    public static void main(String[] args) throws Exception {
        client();
    }
}
