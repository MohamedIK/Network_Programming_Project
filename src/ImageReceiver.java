import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;

public class ImageReceiver {
    public static BufferedImage receiveImage(Socket socket) throws IOException {
        // Receive data size (optional)
        int dataSize = socket.getInputStream().read();

        // Receive image data
        byte[] imageData = new byte[dataSize];
        socket.getInputStream().read(imageData);

        // Convert bytes back to image
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));

        return image;
    }

    public static void receiveAndDisplayImage(Socket socket) throws IOException {
        // Use the existing receiveImage method
        BufferedImage image = receiveImage(socket);

        // Display the image based on your platform (e.g., Swing, JavaFX)
        // Replace this with your specific implementation
        JFrame frame = new JFrame("Received Image");
        JLabel label = new JLabel(new ImageIcon(image));
        frame.getContentPane().add(label);
        frame.pack();
        frame.setVisible(true);
    }
}
