package Client.View.GUI;

import Model.Board;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GUI {

    //mainFrame elements
    private JFrame mainFrame;
    private JPanel boardPanel;
    private JLabel boardLabel;
    private JButton[][] board;
    private JPanel godPanel;
    private JLabel godImage;
    private JLabel godPower;
    private JTextArea messageArea;
    private JLabel backgroud;



    public GUI(){
        buildMainFrame();
    }

    private void buildBoard(){
        board=new JButton[Board.DIM][Board.DIM];
        //boardPanel = new JPanel();
        //boardPanel.setLayout(new GridLayout(5,5));
        //SetBackgound
        ImageIcon currentImg= new ImageIcon("./resources/SantoriniBoard.png");
        Image img = currentImg.getImage();
        Image newImg = img.getScaledInstance(1920,1080, Image.SCALE_SMOOTH);
        ImageIcon finalImg = new ImageIcon(newImg);
        //backgroud = new JLabel(finalImg);
        //backgroud.setHorizontalTextPosition(SwingConstants.CENTER);
        //backgroud.setHorizontalTextPosition(SwingConstants.CENTER);
        //mainFrame.add(backgroud);

        boardLabel = new JLabel(finalImg);
        boardLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        boardLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        boardLabel.setLayout(new GridLayout(5,5));
        boardLabel.setOpaque(false);

        for(int i=0;i<Board.DIM;i++){
            for(int j=0;j<Board.DIM;j++){
                board[i][j] = new JButton();
                board[i][j].setOpaque(false);
                //boardPanel.add(board[i][j]);
                boardLabel.add(board[i][j]);
            }
        }
    }

    private void buildGodBar(){
        godImage = new JLabel();
        godPower = new JLabel();
        godPanel = new JPanel();
        godPanel.setLayout(new BorderLayout());
        godPanel.add(godImage, BorderLayout.CENTER);
        godPanel.add(godPower, BorderLayout.SOUTH);
    }

    private void buildMainFrame(){
        //Bisogna implementare anche la finestra di inserimento dell'indirizzo IP
        mainFrame = new JFrame("Santorini -  Epifani Falleti Landi");
        //Ricordati di impostare l'immagine
        //mainFrame.setIconImage();
        //Ricordati di aggiungere WindowClosingListener
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        buildBoard();
        buildGodBar();
        messageArea = new JTextArea("Connected");



        mainFrame.setLayout(new BorderLayout());
        messageArea.setOpaque(false);
        mainFrame.add(messageArea, BorderLayout.NORTH);
       // boardPanel.setOpaque(false);
        //mainFrame.add(boardPanel, BorderLayout.CENTER);
        mainFrame.add(boardLabel);
        godPanel.setOpaque(false);
        mainFrame.add(godPanel, BorderLayout.WEST);

        //mainFrame.pack();
        mainFrame.setSize(1920, 1080);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

    }

    /*private void buildGodsFrame(){
        //godsFrame elements
        JFrame godsFrame;
        JPanel[] supportPanel;
        JButton[] imgGodsButton;
        JLabel[] descriptionGodPower;

        int i;
        int godsNumber;

        godsFrame = new JFrame("Avaible gods");

        godsNumber=gods.size();
        supportPanel= new JPanel[godsNumber];
        imgGodsButton = new JButton[godsNumber];
        descriptionGodPower = new JLabel[godsNumber];

        for(i=0;i<godsNumber;i++){
            ImageIcon currentImg= new ImageIcon("./resources/gods/"+gods.get(i)+".png");
            Image img = currentImg.getImage();
            Image newImg = img.getScaledInstance(120,200, Image.SCALE_SMOOTH);
            ImageIcon finalImg = new ImageIcon(newImg);
            imgGodsButton[i]=new JButton("",finalImg);
        }

        for(i=0;i<godsNumber;i++){
            descriptionGodPower[i]=new JLabel();
            //Ricordati di settare questa label al potere del dio caricato da file
            descriptionGodPower[i].setText("Description of power of this god");
        }



        for(i=0;i<godsNumber;i++){
            supportPanel[i]=new JPanel();
            supportPanel[i].setLayout(new GridLayout(1,1));
            supportPanel[i].add(imgGodsButton[i]);
            supportPanel[i].add(descriptionGodPower[i]);
        }

        godsFrame.setLayout(new FlowLayout());
        for(i=0; i<godsNumber;i++){
            godsFrame.add(supportPanel[i]);
        }


        godsFrame.setSize(1920,1080);
        godsFrame.setLocationRelativeTo(null);
        godsFrame.setVisible(true);
    }*/

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public JPanel getBoardPanel() {
        return boardPanel;
    }

    public JButton[][] getBoard() {
        return board;
    }

    public JPanel getGodPanel() {
        return godPanel;
    }

    public JLabel getGodImage() {
        return godImage;
    }

    public JLabel getGodPower() {
        return godPower;
    }

    public JTextArea getMessageArea() {
        return messageArea;
    }
}
