package Client.View.GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class SantoriniButton extends JButton {
    private String path;

    public void setPath(String path) {
        this.path = path;
    }

    public SantoriniButton(String text, ImageIcon image){
        super(text, image);
    }

    protected void paintComponent(Graphics g){
        try{
            ImageIcon currentImg= new ImageIcon(ImageIO.read(getClass().getResource(path)));
            Image img = currentImg.getImage();
            Image newImg = img.getScaledInstance(140, 140, Image.SCALE_SMOOTH);
            ImageIcon finalImg = new ImageIcon(newImg);
            finalImg.paintIcon(this, g, 0, 0);
        } catch (IOException ex){

        }
    }
}
