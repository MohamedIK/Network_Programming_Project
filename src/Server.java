import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
public class Server {
    public static void server(){
        try {
            // Step 1: Create ServerSocket on Port 555
            ServerSocket ss = new ServerSocket(555);
            System.out.println("Server is listening on port 555");

            // Step 2: Get the IP Address of the Server
            InetAddress serverAddress = InetAddress.getLocalHost();
            System.out.println("Server IP Address: " + serverAddress.getHostAddress());

            // Step 3: Wait for a Client to Send a Request to the Server
            Socket clientSocket = ss.accept();
            System.out.println("A client is connected to the server");

            // Step 4: Create Input and Output Streams
            BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            System.out.println("IO streams are created");

            // Step 5: Read HTTP Request from Client
            String request = clientReader.readLine();
            System.out.println("Received HTTP Request: " + request);

            // Step 6: Parse the requested HTML file name
            String[] requestParts = request.split(" ");
            String requestedFileName = requestParts[1].substring(1); // Removing the leading '/'
            System.out.println("Requested File Name: " + requestedFileName);

            // Step 7: Check if the requested file exists
            File requestedFile = new File("src/"+requestedFileName);
            if (requestedFile.exists()) {
                System.out.println("The requested file exists");
                // Step 8: Send HTTP Response Header
                clientWriter.println("HTTP/1.1 200 OK");
                clientWriter.println("Content-Type: text/html");
                clientWriter.println();

                // Step 9: Send the HTML file to the client
                BufferedReader fileReader = new BufferedReader(new FileReader(requestedFile));
                String line;
                while ((line = fileReader.readLine()) != null) {
                    clientWriter.println(line);
                }
                System.out.println("The requested file is sent to the client");
                fileReader.close();

                // Step 10: Send the image file to the client
                File imageFile = new File("src/img.png");
                FileInputStream imageIn = new FileInputStream(imageFile);
                OutputStream imageOs = clientSocket.getOutputStream();

                byte[] buffer = new byte[1024 * 1024];
                int bytesRead;
                while ((bytesRead = imageIn.read(buffer)) != -1) {
                    imageOs.write(buffer, 0, bytesRead);
                }
                System.out.println("The image file is sent to the client");

                // Close image resources
                imageIn.close();
                imageOs.close();
            }
             /*
            if (requestedFile.exists()) {
                System.out.println("The requested file exists");

                if (requestedFileName.endsWith(".html")) {
                    // Step 8: Send HTML Response Header
                    clientWriter.println("HTTP/1.1 200 OK");
                    clientWriter.println("Content-Type: text/html");
                    clientWriter.println();

                    // Step 9: Send the HTML file to the client
                    BufferedReader fileReader = new BufferedReader(new FileReader(requestedFile));
                    String line;
                    while ((line = fileReader.readLine()) != null) {
                        clientWriter.println(line);
                    }
                    System.out.println("The requested HTML file is sent to the client");
                    fileReader.close();
                } else if (requestedFileName.endsWith(".png")) {
                    // Step 10: Send Image Response Header
                    clientWriter.println("HTTP/1.1 200 OK");
                    clientWriter.println("Content-Type: image/png");
                    clientWriter.println();

                    // Step 11: Send the image file to the client
                    FileInputStream imageIn = new FileInputStream(requestedFile);
                    OutputStream imageOs = clientSocket.getOutputStream();

                    byte[] buffer = new byte[20 * 1024];
                    int bytesRead;
                    while ((bytesRead = imageIn.read(buffer)) != -1) {
                        imageOs.write(buffer, 0, bytesRead);
                    }
                    System.out.println("The image file is sent to the client");

                    // Close image resources
                    imageIn.close();
                    imageOs.close();
                }
            }*/
            else {
                // Step 11: Send HTTP 404 Not Found Response
                clientWriter.println("HTTP/1.1 404 Not Found");
                clientWriter.println("Content-Type: text/plain");
                clientWriter.println();
                clientWriter.println("404 Not Found - The requested file does not exist.");
            }

            // Step 12: Close Resources
            clientReader.close();
            clientWriter.close();
            clientSocket.close();
            ss.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
