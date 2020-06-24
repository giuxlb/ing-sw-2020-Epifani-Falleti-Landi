package Client.View.GUI;

import Model.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

public class GUI {
    //mainFrame
    private JFrame mainFrame;

    //chooseNumberOfPlayersWindowElements
    private JLabel numberOfPlayersWindowManager;
    private JLabel numberOfPlayersInformation;
    private JPanel buttonPanel;
    private JButton two;
    private JButton three;

    //loginWindowElements
    private JLabel loginWindowManager;
    private JLabel ipLabel;
    private JTextField ipTextField;
    private JLabel usernameLabel;
    private JTextField usernameTextField;
    private JButton loginNextButton;
    private JLabel loginMessageArea;

    //dateWindowElements
    private JLabel dateWindowManager;
    private JLabel dayLabel;
    private JTextField dayTextField;
    private JLabel monthLabel;
    private JTextField monthTextField;
    private JLabel yearLabel;
    private JTextField yearTextField;
    private JButton dateNextButton;
    private JLabel dateMessageArea;

    //godsWindow elements
    private JLabel godsWindowManager;
    private JButton[] imgGodsButton;

    //mainWindow elements
    private JLabel mainWindowManager;
    private JLabel boardLabel;
    private JButton[][] board;
    private JPanel godPanel;
    private JLabel godImage;
    private JLabel godPower;

    //messageAndUndoBar
    private JPanel messageAndUndoPanel;
    private JTextArea messageArea;
    private JButton undo;

    //
    private JTextArea informationArea;





    public GUI(){
        buildMainFrame();
        buildChooseNumberOfPlayersWindow();
        buildLoginWindow();
        buildDateWindow();
        createMainWindow();
    }

    private void buildBoard(){
        board=new JButton[Board.DIM][Board.DIM];
        //SetBackgound
        ImageIcon currentImg= new ImageIcon("./resources/SantoriniBoard.png");
        Image img = currentImg.getImage();
        Image newImg = img.getScaledInstance(1920,1080, Image.SCALE_SMOOTH);
        ImageIcon finalImg = new ImageIcon(newImg);

        boardLabel = new JLabel(finalImg);
        boardLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        boardLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        boardLabel.setLayout(new GridLayout(5,5));

        for(int i=0;i<Board.DIM;i++){
            for(int j=0;j<Board.DIM;j++){
                board[i][j] = new JButton();
                board[i][j].setContentAreaFilled(false);
                board[i][j].setBorderPainted(true);
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
        ImageIcon currentIcon= new ImageIcon("./resources/MainBackground.jpg");
        Image tmp = currentIcon.getImage();
        Image newIcon = tmp.getScaledInstance(1920,1080, Image.SCALE_SMOOTH);
        ImageIcon finalIcon = new ImageIcon(newIcon);

        mainFrame = new JFrame("Santorini -  Epifani Falleti Landi");
        mainFrame.addWindowListener(new CustomClosing(mainFrame));
        mainFrame.setSize(1920, 1080);

        //Ricordati di impostare l'immagine
        //mainFrame.setIconImage();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    protected void buildGodsWindow(ArrayList<String> gods){
        imgGodsButton = new JButton[gods.size()];

        for(int i=0;i<gods.size();i++){
            ImageIcon currentImg= new ImageIcon("./resources/gods/"+gods.get(i)+".png");
            Image img = currentImg.getImage();
            Image newImg = img.getScaledInstance(120,200, Image.SCALE_SMOOTH);
            ImageIcon finalImg = new ImageIcon(newImg);
            imgGodsButton[i]=new JButton("",finalImg);
        }

        godsWindowManager = new JLabel();
        godsWindowManager.setLayout(new FlowLayout());
        for(int i=0; i<gods.size();i++){
            godsWindowManager.add(imgGodsButton[i]);
        }
    }

    public JLabel getGodsWindowManager() {
        return godsWindowManager;
    }

    public JButton[] getImgGodsButtons() {
        return imgGodsButton;
    }

    protected void buildMessageAndUndoBar(){
        messageArea = new JTextArea("Connected");

        ImageIcon currentIcon= new ImageIcon("./resources/Undo.png");
        Image tmp = currentIcon.getImage();
        //Il bottone deve essere un po' meno largo e deve stare attacato al margine (mi serve un gridBagLayout)
        Image newIcon = tmp.getScaledInstance(50,50, Image.SCALE_SMOOTH);
        ImageIcon finalIcon = new ImageIcon(newIcon);

        undo = new JButton("", finalIcon);

        messageAndUndoPanel = new JPanel();
        messageAndUndoPanel.setLayout(new FlowLayout());
        messageAndUndoPanel.add(messageArea);
        messageAndUndoPanel.add(undo);

    }

    protected void buildChooseNumberOfPlayersWindow() {
        numberOfPlayersInformation = new JLabel("How many players are going to connect?");
        two = new JButton("2");
        three = new JButton("3");

        numberOfPlayersWindowManager = new JLabel();
        numberOfPlayersWindowManager.setLayout(new GridBagLayout());
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 1;
        numberOfPlayersWindowManager.add(numberOfPlayersInformation, gbc1);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 1;
        gbc3.gridy = 2;
        gbc3.insets = new Insets(5,5,5,3);
        gbc3.ipadx=30;
        gbc3.ipady=10;
        buttonPanel.add(two, gbc3);

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 2;
        gbc4.gridy = 2;
        gbc4.insets = new Insets(5,3,5,5);
        gbc4.ipadx=30;
        gbc4.ipady=10;
        buttonPanel.add(three, gbc4);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx=0;
        gbc2.gridy=2;
        numberOfPlayersWindowManager.add(buttonPanel, gbc2);
    }

    protected JButton getTwo() {
        return two;
    }

    protected JButton getThree() {
        return three;
    }

    protected void buildLoginWindow(){
        //Carica immagine di loginWindowManager
        loginWindowManager=new JLabel();
        ipLabel=new JLabel("IP address: ");
        ipTextField = new JTextField();
        usernameLabel = new JLabel("Username: ");
        usernameTextField = new JTextField();
        loginNextButton = new JButton("Next");
        loginMessageArea = new JLabel();
        loginMessageArea.setVisible(false);

        loginWindowManager.setLayout(new GridBagLayout());
        GridBagConstraints ipLabelGBC= new GridBagConstraints();
        ipLabelGBC.gridx=0;
        ipLabelGBC.gridy=0;
        loginWindowManager.add(ipLabel, ipLabelGBC);

        GridBagConstraints ipTextFieldGBC = new GridBagConstraints();
        ipTextFieldGBC.gridx = 1;
        ipTextFieldGBC.gridy=0;
        ipTextFieldGBC.ipadx=100;
        ipTextFieldGBC.ipady=10;
        loginWindowManager.add(ipTextField, ipTextFieldGBC);

        GridBagConstraints usernameLabelGBC = new GridBagConstraints();
        usernameLabelGBC.gridx=0;
        usernameLabelGBC.gridy=1;
        loginWindowManager.add(usernameLabel, usernameLabelGBC);

        GridBagConstraints usernameTextFieldGBC = new GridBagConstraints();
        usernameTextFieldGBC.ipady=10;
        usernameTextFieldGBC.ipadx=100;
        usernameTextFieldGBC.gridx=1;
        usernameTextFieldGBC.gridy=1;
        loginWindowManager.add(usernameTextField, usernameTextFieldGBC);

        GridBagConstraints loginNextButtonGBC = new GridBagConstraints();
        loginNextButtonGBC.gridx=1;
        loginNextButtonGBC.gridy=2;
        loginWindowManager.add(loginNextButton, loginNextButtonGBC);

        GridBagConstraints loginMessageAreaGBC = new GridBagConstraints();
        loginMessageAreaGBC.gridx=0;
        loginMessageAreaGBC.gridy=1;
        loginMessageAreaGBC.gridheight=GridBagConstraints.LAST_LINE_END;
        loginWindowManager.add(loginMessageArea, loginMessageAreaGBC);
    }

    public JLabel getLoginWindowManager() {
        return loginWindowManager;
    }

    public JButton getLoginNextButton() {
        return loginNextButton;
    }

    public JTextField getUsernameTextField() {
        return usernameTextField;
    }

    public JLabel getLoginMessageArea() {
        return loginMessageArea;
    }

    protected void buildDateWindow(){
        monthLabel=new JLabel("Insert mounth of birth (mm) ");
        monthTextField = new JTextField();
        dayLabel = new JLabel("Insert day of birth (gg) ");
        dayTextField = new JTextField();
        yearLabel = new JLabel("Insert year of birth (aaaa)");
        yearTextField = new JTextField();
        dateNextButton = new JButton("Send date");
        dateMessageArea = new JLabel();

        dateWindowManager = new JLabel();
        dateWindowManager.setLayout(new GridBagLayout());
        GridBagConstraints monthLabelGBC= new GridBagConstraints();
        monthLabelGBC.gridy = 0;
        monthLabelGBC.gridx = 0;
        dateWindowManager.add(monthLabel, monthLabelGBC);

        GridBagConstraints monthTextFieldGBC = new GridBagConstraints();
        monthTextFieldGBC.gridy=0;
        monthTextFieldGBC.gridx=1;
        monthTextFieldGBC.ipadx=100;
        monthTextFieldGBC.ipady=10;
        dateWindowManager.add(monthTextField,monthTextFieldGBC);

        GridBagConstraints dayLabelGBC = new GridBagConstraints();
        dayLabelGBC.gridy = 1;
        dayLabelGBC.gridx = 0;
        dateWindowManager.add(dayLabel, dayLabelGBC);

        GridBagConstraints dayTextFieldGBC = new GridBagConstraints();
        dayTextFieldGBC.gridy = 1;
        dayTextFieldGBC.gridx = 1;
        dayTextFieldGBC.ipadx=100;
        dayTextFieldGBC.ipady=10;
        dateWindowManager.add(dayTextField, dayTextFieldGBC);

        GridBagConstraints yearLabelGBC = new GridBagConstraints();
        yearLabelGBC.gridy=2;
        yearLabelGBC.gridx=0;
        dateWindowManager.add(yearLabel, yearLabelGBC);

        GridBagConstraints yearTextFieldGBC = new GridBagConstraints();
        yearTextFieldGBC.gridy=2;
        yearTextFieldGBC.gridx=1;
        yearTextFieldGBC.ipadx=100;
        yearTextFieldGBC.ipady=10;
        dateWindowManager.add(yearTextField, yearTextFieldGBC);

        GridBagConstraints dateNextButtonGBC = new GridBagConstraints();
        dateNextButtonGBC.gridy=3;
        dateNextButtonGBC.gridx=1;
        dateWindowManager.add(dateNextButton, dateNextButtonGBC);

        GridBagConstraints dateMessageAreaGBC = new GridBagConstraints();
        dateMessageAreaGBC.gridy=4;
        dateMessageAreaGBC.gridx=0;
        dateMessageAreaGBC.gridheight=GridBagConstraints.LAST_LINE_END;
        dateWindowManager.add(dateMessageArea, dateMessageAreaGBC);
    }


    public JLabel getDateWindowManager() {
        return dateWindowManager;
    }

    public JTextField getDayTextField() {
        return dayTextField;
    }

    public JTextField getMonthTextField() {
        return monthTextField;
    }

    public JTextField getYearTextField() {
        return yearTextField;
    }

    public JButton getDateNextButton() {
        return dateNextButton;
    }

    public JLabel getDateMessageArea() {
        return dateMessageArea;
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public JButton[][] getBoard() {
        return board;
    }

    public JTextArea getMessageArea() {
        return messageArea;
    }

    public JLabel getNumberOfPlayersWindowManager() {
        return numberOfPlayersWindowManager;
    }

    protected void createMainWindow(){
        mainWindowManager = new JLabel();
        buildMessageAndUndoBar();
        buildBoard();
        buildGodBar();
        informationArea = new JTextArea("Move mouse on board to see more detailed information");

        mainWindowManager.setLayout(new BorderLayout());
        mainWindowManager.add(messageAndUndoPanel, BorderLayout.NORTH);
        mainWindowManager.add(informationArea, BorderLayout.SOUTH);
        mainWindowManager.add(boardLabel);
        mainWindowManager.add(godPanel, BorderLayout.WEST);
    }

    public JLabel getMainWindowManager() {
        return mainWindowManager;
    }

    public JTextArea getInformationArea() {
        return informationArea;
    }

    class CustomClosing implements WindowListener{
        private JFrame mainFrame;

        public CustomClosing(JFrame mainFrame){
           this.mainFrame=mainFrame;
        }

        @Override
        public void windowOpened(WindowEvent e) {
        }

        @Override
        public void windowClosing(WindowEvent e) {
            Object[] choices = {"Yes", "No"};

            ImageIcon currentIcon= new ImageIcon("./resources/WarningSign.png");
            Image tmp = currentIcon.getImage();
            Image newIcon = tmp.getScaledInstance(50,50, Image.SCALE_SMOOTH);
            ImageIcon finalIcon = new ImageIcon(newIcon);

            int choice = JOptionPane.showOptionDialog(mainFrame,
                    "Are you sure to end the game?",
                    "Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    finalIcon,
                    choices,
                    choices[1]);

            if(choice==0){
                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        }

        @Override
        public void windowClosed(WindowEvent e) {

        }

        @Override
        public void windowIconified(WindowEvent e) {

        }

        @Override
        public void windowDeiconified(WindowEvent e) {

        }

        @Override
        public void windowActivated(WindowEvent e) {

        }

        @Override
        public void windowDeactivated(WindowEvent e) {

        }
    }

}