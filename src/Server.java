import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Server {
    static FileInputStream fis;
    static DataOutputStream os;

    public static void server() throws Exception {
        // Create server socket
        ServerSocket serverSocket = new ServerSocket(80);
        System.out.println("Server: Server socket is created on port 80");


        // Wait for client connection
        Socket clientSocket = serverSocket.accept();
        System.out.println("Server: Client socket is accepted");

        // Get client IP address
        InetAddress address = clientSocket.getInetAddress();
        String clientAddress = address.getHostAddress();
        System.out.println("Server: Client IP Address = " + clientAddress);


        // Create IO streams for network socket
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        OutputStream outStream = clientSocket.getOutputStream();
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        System.out.println("Server: IO streams are created");

        // Read request from client
        String line;
        while ((line = in.readLine()) != null) {
            if (line.equals("")) {
                break;
            }
        }
        System.out.println("Server: Request is read from client");

        BufferedReader r = new BufferedReader(new FileReader("src/indx.html"));

        String fileName = line.split(" ")[0];
        //System.out.println("Server: File name = " + fileName);

        File htmlFile = new File("src/indx.html");
//        byte[] htmlFileBytes = Files.readAllBytes(Path.of("src/indx.html"));
//        sendResponse(outStream, 200, htmlFileBytes);

        BufferedReader fileReader = new BufferedReader(new FileReader(htmlFile));

        StringBuilder header = new StringBuilder();
        header.append("HTTP/1.1 200 OK\r\n");
        header.append("Content-Type: text/html\r\n");
        header.append("Connection: close\r\n");
        if (htmlFile.exists()) {
            // Send HTML content for "index.html"
//            out.println("HTTP/1.1 200 OK");
//            out.println("Content-Type: text/html");
//            out.println("Connection: close");
            out.println(header.toString());
            out.println();
            out.flush();

            //Send Body
            String fileLine;
            while ((fileLine = fileReader.readLine()) != null) {
                out.println(fileLine);
            }
            System.out.println("Server: HTML content is sent to client");
            out.flush();
            //out.close();

            // Sending image
            int i;

            fis = new FileInputStream("src/img2.png");
            File image = new File("src/img2.png");
            long imageLength = image.length();
            os = new DataOutputStream(outStream);
            os.writeLong(imageLength);
            os.flush();
            while ((i = fis.read()) > -1) {
                os.write(i);
            }
            os.flush();
            System.out.println("Server: Image is sent to client");

            //fis.close();
            //os.close();

            String clientResponse = in.readLine();
            System.out.println("Client Response: " + clientResponse);

        } else {
            out.println("HTTP/1.1 404 Not Found");
            out.println("Content-Type: text/plain");
            out.println("Connection: close");
            out.println();
            out.println("404 Not Found - The requested resource is not available.");
            System.out.println("Server: 404 Not Found");
        }

        // Close client socket
        clientSocket.close();

        // Close IO streams
        in.close();
        out.close();
        fis.close();
        os.close();
        System.out.println("Server: Client socket and IO streams is closed");
    }

    private static void sendResponse(OutputStream out, int statusCode, byte[] content) throws IOException {
        // Define HTTP headers
        StringBuilder headers = new StringBuilder();
        headers.append("HTTP/1.1 ").append(statusCode).append(" ");
        switch (statusCode) {
            case 200:
                headers.append("OK");
                break;
            case 404:
                headers.append("Not Found");
                break;
            default:
                headers.append("Internal Server Error");
        }
        headers.append("\r\n");
        headers.append("Content-Length: ").append(content.length).append("\r\n");
        headers.append("Content-Type: text/html; charset=UTF-8\r\n"); // adjust based on your file type
        headers.append("\r\n");

        // Send headers first
        out.write(headers.toString().getBytes());
        out.flush();
        // Send file content
        out.write(content);
        out.flush();
    }

    public static void main(String[] args) throws Exception {
        Server.server();
    }
}

