package Client.View.GUI;

import Model.BoardCell;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MainDiProva {
    public static void main(String[] args){
        JFrame prova = new JFrame("Prova");
        prova.setSize(500,500);
        SantoriniButton img = new SantoriniButton(paintBackgroundForSantoriniButton("BoardCell.jpg"));
        prova.add(img);
        prova.setVisible(true);
    }

    private static ImageIcon paintBackgroundForSantoriniButton(String name) {
        try{
            ImageIcon currentIcon= new ImageIcon(ImageIO.read(MainDiProva.class.getResource("/"+name)));
            Image tmp = currentIcon.getImage();
            Image newIcon = tmp.getScaledInstance(140,140, Image.SCALE_SMOOTH);
            ImageIcon finalIcon = new ImageIcon(newIcon);
            return finalIcon;
        }catch (IOException ex){
            return null;
        }
    }
}
