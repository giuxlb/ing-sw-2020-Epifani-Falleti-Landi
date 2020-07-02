package Client.View.GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class SantoriniLabel extends JLabel {
    private String path;

    public SantoriniLabel(ImageIcon image) {
        super(image);
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        try{
            ImageIcon currentImg= new ImageIcon(ImageIO.read(getClass().getResource(path)));
            Image img = currentImg.getImage();
            Image newImg = img.getScaledInstance(240,360, Image.SCALE_SMOOTH);
            ImageIcon finalImg = new ImageIcon(newImg);
            finalImg.paintIcon(this, g, 0, 0);
        } catch (IOException ex){

        }
    }


}
