import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
public class Client {
    public static void client(){
        try {
            // Step 1: Get Local Host Address
            InetAddress serverAddress = InetAddress.getLocalHost();
            System.out.println("Server IP Address: " + serverAddress.getHostAddress());

            // Step 2: Create a Socket to Connect to the Server
            Socket clientSocket = new Socket(serverAddress.getHostAddress(), 555);
            System.out.println("Client is connected to the server");

            // Step 3: Create Input and Output Streams
            BufferedReader serverReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter serverWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            System.out.println("IO streams are created");

            // Step 4: Prepare and Send HTTP GET Request
            String getRequest = "GET /indx.html HTTP/1.1";
            serverWriter.println(getRequest);
            serverWriter.println();

            // Step 5: Read HTTP Response Header from Server
            String responseHeader = serverReader.readLine();
            System.out.println("Received HTTP Response Header: " + responseHeader);

            // Step 6: Check if the response is OK (200)
            if (responseHeader.contains("200 OK")) {
                // Step 7: Read and Save the HTML file
                StringBuilder htmlContent = new StringBuilder();
                String line;
                boolean contentStarted = false;

                while ((line = serverReader.readLine()) != null) {
                    if (line.isEmpty()) {
                        contentStarted = true;
                        continue;
                    }

                    if (contentStarted) {
                        htmlContent.append(line).append("\n");
                    }
                }

                // Save HTML content to a file
                BufferedWriter fileWriter = new BufferedWriter(new FileWriter("indx.html"));
                fileWriter.write(htmlContent.toString());
                fileWriter.close();
                System.out.println("The HTML file is saved");

                // Step 8: Receive and Save the image file
                byte[] buffer = new byte[1024 * 1024];
                int bytesRead;
                InputStream imageIn = clientSocket.getInputStream();
                FileOutputStream imageOs = new FileOutputStream("img.png");

                while ((bytesRead = imageIn.read(buffer)) != -1) {
                    imageOs.write(buffer, 0, bytesRead);
                }

                // Close image resources
                imageIn.close();
                imageOs.close();
                System.out.println("The image file is saved");

                // Step 9: Open the HTML file in the default browser
                Desktop desktop = Desktop.getDesktop();
                File htmlFile = new File("indx.html");
                desktop.open(htmlFile);

                // Step 10: Send a message to the server
                serverWriter.println("POST / HTTP/1.1");
                serverWriter.println("I RECEIVED THE INFORMATION OF STUDENT's PROJECT GROUP");
                serverWriter.println();
            } else {
                // Step 11: Handle HTTP 404 Not Found response
                System.out.println("404 Not Found - The requested file does not exist.");
            }

            // Step 12: Close Resources
            serverReader.close();
            serverWriter.close();
            clientSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
