package Client.View;

import javax.swing.*;
import java.awt.*;

public class GUI {
    public static void main(String[] args){

        //JFrame.setDefaultLookAndFeelDecorated(true);

        JFrame mainWindow= new JFrame("Santorini - Epifani Falleti Landi");

        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton jb1 = new JButton();
        JButton jb2 = new JButton();
        JButton jb3 = new JButton();
        JButton jb4 = new JButton();
        JButton jb5 = new JButton();
        JButton jb6 = new JButton();
        JButton jb7 = new JButton();
        JButton jb8 = new JButton();
        JButton jb9 = new JButton();
        JButton jb10 = new JButton();
        JButton jb11 = new JButton();
        JButton jb12 = new JButton();
        JButton jb13 = new JButton();
        JButton jb14 = new JButton();
        JButton jb15 = new JButton();
        JButton jb16 = new JButton();
        JButton jb17 = new JButton();
        JButton jb18 = new JButton();
        JButton jb19 = new JButton();
        JButton jb20 = new JButton();
        JButton jb21 = new JButton();
        JButton jb22 = new JButton();
        JButton jb23 = new JButton();
        JButton jb24 = new JButton();
        JButton jb25 = new JButton();

        //ImageIcon background = new ImageIcon(mainWindow.getClass().getResource("SantoriniBoard.png"));
        //Image scaledBackground = background.getImage().getScaledInstance(1000,700, Image.SCALE_SMOOTH);
        JPanel mainPanel= new JPanel();


        mainPanel.setLayout(new GridLayout(5,5));
        mainPanel.add(jb1);
        mainPanel.add(jb2);
        mainPanel.add(jb3);
        mainPanel.add(jb4);
        mainPanel.add(jb5);
        mainPanel.add(jb6);
        mainPanel.add(jb7);
        mainPanel.add(jb8);
        mainPanel.add(jb9);
        mainPanel.add(jb10);
        mainPanel.add(jb11);
        mainPanel.add(jb12);
        mainPanel.add(jb13);
        mainPanel.add(jb14);
        mainPanel.add(jb15);
        mainPanel.add(jb16);
        mainPanel.add(jb17);
        mainPanel.add(jb18);
        mainPanel.add(jb19);
        mainPanel.add(jb20);
        mainPanel.add(jb21);
        mainPanel.add(jb22);
        mainPanel.add(jb23);
        mainPanel.add(jb24);
        mainPanel.add(jb25);

        mainWindow.add(mainPanel);

        mainWindow.pack();

        mainWindow.setSize(1000,700);
        mainWindow.setVisible(true);
    }
}
