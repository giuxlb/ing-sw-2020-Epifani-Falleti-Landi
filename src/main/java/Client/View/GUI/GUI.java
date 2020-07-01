package Client.View.GUI;

import Client.View.CLI;
import Model.Board;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.util.ArrayList;

public class GUI {
    //mainFrame
    private JFrame mainFrame;
    private JLabel upperLabel;
    private JLabel lowerLabel;
    private JLabel waitingLabel;
    private JLabel ipLabel;
    private JTextField ipTextField;
    private JButton ipButton;

    //numberOfPlayersWindowElements
    private JLabel numberOfPlayersWindowManager;
    private JLabel numberOfPlayersInformation;
    private JLabel buttonLabel;
    private JButton two;
    private JButton three;

    //loginWindowElements
    private JLabel loginWindowManager;
    private JLabel usernameLabel;
    private JTextField usernameTextField;
    private JButton loginNextButton;

    //dateWindowElements
    private JLabel dateWindowManager;
    private JLabel dayLabel;
    private JTextField dayTextField;
    private JLabel monthLabel;
    private JTextField monthTextField;
    private JLabel yearLabel;
    private JTextField yearTextField;
    private JButton dateNextButton;

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
    private JButton undo;

    public GUI(){
        mainFrame = new JFrame("Santorini -  Epifani Falleti Landi");
        mainFrame.addWindowListener(new CustomClosing(mainFrame));
        mainFrame.setSize(710, 438);
        mainFrame.setResizable(false);
        mainFrame.setLayout(new BorderLayout());

        upperLabel = new JLabel("Wait a moment, please");
        waitingLabel = paintScreen("MainBackground.jpg", 700, 400);
        waitingLabel.setLayout(new GridBagLayout());
        lowerLabel = new JLabel("The game will start soon");
        mainFrame.add(upperLabel, BorderLayout.NORTH);
        mainFrame.add(waitingLabel, BorderLayout.CENTER);
        mainFrame.add(lowerLabel, BorderLayout.SOUTH);



        //Ricordati di mettere l'icona
        //mainFrame.setIconImage();

        //Adriano vuole mettere qui la l'indirizzo ip?

        ipLabel=new JLabel("IP address: ");
        ipTextField = new JTextField();
        GridBagConstraints ipLabelGBC= new GridBagConstraints();
        ipLabelGBC.gridx=0;
        ipLabelGBC.gridy=0;


        GridBagConstraints ipTextFieldGBC = new GridBagConstraints();
        ipTextFieldGBC.gridx = 1;
        ipTextFieldGBC.gridy=0;
        ipTextFieldGBC.ipadx=100;
        ipTextFieldGBC.ipady=10;

        waitingLabel.add(ipLabel,ipLabelGBC);
        waitingLabel.add(ipTextField,ipTextFieldGBC);
        ipButton = new JButton("Next");
        GridBagConstraints ipButtonGBC = new GridBagConstraints();
        ipButtonGBC.gridy = 1;
        ipButtonGBC.gridx = 1;
        ipButtonGBC.insets = new Insets(5,3,3,3);
        waitingLabel.add(ipButton,ipButtonGBC);

        //Additional initial settings for Mac
        if(CLI.isAMac()==true){
            try{
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }catch (Exception ex){
                System.out.println("Non riesco a rendere la grafica compatibile su questo mac");
            }
        }

        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    public JFrame getMainFrame() {
        return mainFrame;
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


            ImageIcon warningIcon = createIcon("WarningSign.png");

            int choice = JOptionPane.showOptionDialog(mainFrame,
                    "Are you sure to end the game?",
                    "Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    warningIcon,
                    choices,
                    choices[1]);

            System.out.println(choice);
            if(choice==0){
                mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }else{
                mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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

    protected void destroyWaitingLabel(){
        mainFrame.remove(waitingLabel);
        waitingLabel=null;
    }

    public JLabel getUpperLabel() {
        return upperLabel;
    }

    public JLabel getLowerLabel() {
        return lowerLabel;
    }

    private JLabel paintScreen(String name, int width, int height) {
        try{
            ImageIcon currentIcon= new ImageIcon(ImageIO.read(getClass().getResource("/"+name)));
            Image tmp = currentIcon.getImage();
            Image newIcon = tmp.getScaledInstance(width,height, Image.SCALE_SMOOTH);
            ImageIcon finalIcon = new ImageIcon(newIcon);
            return new JLabel(finalIcon);
        }catch (IOException ex){
            return new JLabel("Unable to load thi screen");
        }
    }

    protected ImageIcon createIcon(String name){
        try{
            ImageIcon currentIcon= new ImageIcon(ImageIO.read(getClass().getResource("/"+name)));
            Image tmp = currentIcon.getImage();
            Image newIcon = tmp.getScaledInstance(40,40, Image.SCALE_SMOOTH);
            ImageIcon finalIcon = new ImageIcon(newIcon);
            return finalIcon;
        }catch (IOException ex){
            return null;
        }
    }


    protected void buildNumberOfPlayersWindow() {
        numberOfPlayersInformation = new JLabel("How many players are going to connect?");
        two = new JButton("2");
        three = new JButton("3");
        numberOfPlayersWindowManager = paintScreen("MainBackground.jpg", 700, 400);
        numberOfPlayersWindowManager.setLayout(new GridBagLayout());
        GridBagConstraints gbc1 = new GridBagConstraints();
        gbc1.gridx = 0;
        gbc1.gridy = 1;
        numberOfPlayersWindowManager.add(numberOfPlayersInformation, gbc1);

        buttonLabel = new JLabel();
        buttonLabel.setLayout(new GridBagLayout());
        GridBagConstraints gbc3 = new GridBagConstraints();
        gbc3.gridx = 1;
        gbc3.gridy = 2;
        gbc3.insets = new Insets(5,5,5,3);
        gbc3.ipadx=30;
        gbc3.ipady=10;
        buttonLabel.add(two, gbc3);

        GridBagConstraints gbc4 = new GridBagConstraints();
        gbc4.gridx = 2;
        gbc4.gridy = 2;
        gbc4.insets = new Insets(5,3,5,5);
        gbc4.ipadx=30;
        gbc4.ipady=10;
        buttonLabel.add(three, gbc4);

        GridBagConstraints gbc2 = new GridBagConstraints();
        gbc2.gridx=0;
        gbc2.gridy=2;
        gbc2.ipadx=200;
        gbc2.ipady=55;
        numberOfPlayersWindowManager.add(buttonLabel, gbc2);
        mainFrame.add(numberOfPlayersWindowManager, BorderLayout.CENTER);
    }

    protected void destroyNumberOfPlayersWindow(){
        mainFrame.remove(numberOfPlayersWindowManager);
        numberOfPlayersInformation=null;
        two=null;
        three=null;
        buttonLabel=null;
        numberOfPlayersWindowManager=null;
    }

    protected JButton getTwo() {
        return two;
    }

    protected JButton getThree() {
        return three;
    }


    protected void buildLoginWindow(){
        loginWindowManager=paintScreen("MainBackground.jpg", 700, 400);
        usernameLabel = new JLabel("Username: ");
        usernameTextField = new JTextField();
        loginNextButton = new JButton("Next");

        loginWindowManager.setLayout(new GridBagLayout());
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
        loginNextButtonGBC.ipadx=30;
        loginNextButtonGBC.ipady=5;
        loginNextButtonGBC.insets = new Insets(5,0,0,0);
        loginWindowManager.add(loginNextButton, loginNextButtonGBC);

        mainFrame.add(loginWindowManager, BorderLayout.CENTER);
    }

    public JButton getLoginNextButton() {
        return loginNextButton;
    }

    public JTextField getUsernameTextField() {
        return usernameTextField;
    }

    protected void destroyLoginWindow(){
        mainFrame.remove(loginWindowManager);
        usernameLabel=null;
        usernameTextField=null;
        loginNextButton=null;
        loginWindowManager=null;
    }

    protected void buildDateWindow(){
        monthLabel=new JLabel("Insert mounth of birth (mm) ");
        monthTextField = new JTextField();
        dayLabel = new JLabel("Insert day of birth (gg) ");
        dayTextField = new JTextField();
        yearLabel = new JLabel("Insert year of birth (aaaa)");
        yearTextField = new JTextField();
        dateNextButton = new JButton("Send date");

        dateWindowManager = paintScreen("MainBackground.jpg", 700, 400);
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
        dateNextButtonGBC.insets = new Insets(5,0,0,0);
        dateWindowManager.add(dateNextButton, dateNextButtonGBC);

        mainFrame.add(dateWindowManager, BorderLayout.CENTER);
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

    protected void destroyDateWindow(){
        mainFrame.remove(dateWindowManager);
        monthLabel=null;
        monthTextField=null;
        dayLabel=null;
        dayTextField=null;
        yearLabel=null;
        yearTextField=null;
        dateNextButton=null;
    }

    protected void buildGodsWindow(ArrayList<String> gods){
        mainFrame.setSize(1920,1080);
        mainFrame.setResizable(true);
        mainFrame.setLocationRelativeTo(null);
        imgGodsButton = new JButton[gods.size()];
        GridBagConstraints[] godBagCostraints= new GridBagConstraints[gods.size()];

        for(int i=0;i<gods.size();i++){
            imgGodsButton[i]=new JButton("",paintGods(gods.get(i)));
            imgGodsButton[i].setContentAreaFilled(false);
            imgGodsButton[i].setBorderPainted(true);
            godBagCostraints[i] = new GridBagConstraints();
        }

        godsWindowManager = paintScreen("GodsScreen.png", 1920, 1080);
        godsWindowManager.setLayout(new GridBagLayout());
        int j=0;
        for(int i=0; i<gods.size();i++){
            if(i<5){
                godBagCostraints[i].gridy=1;
                godBagCostraints[i].gridx=j;
                godBagCostraints[i].insets = new Insets(5,3,5,5);
                j++;
            }else if(i==5){
                j=0;
                godBagCostraints[i].gridy=2;
                godBagCostraints[i].gridx=j;
                godBagCostraints[i].insets = new Insets(5,3,5,3);
                j++;
            }else if(i>=6 && i<=9){
                godBagCostraints[i].gridy=2;
                godBagCostraints[i].gridx=j;
                godBagCostraints[i].insets = new Insets(5,3,5,3);
                j++;
            }
            godsWindowManager.add(imgGodsButton[i], godBagCostraints[i]);

            if(gods.size()>3){
                godBagCostraints[10].gridy=3;
                godBagCostraints[10].gridx=1;
                godBagCostraints[10].insets = new Insets(5,3,5,3);
                godsWindowManager.add(imgGodsButton[10], godBagCostraints[10]);

                godBagCostraints[11].gridy=3;
                godBagCostraints[11].gridx=2;
                godBagCostraints[11].insets = new Insets(5,3,5,3);
                godsWindowManager.add(imgGodsButton[11], godBagCostraints[11]);

                godBagCostraints[12].gridy=3;
                godBagCostraints[12].gridx=3;
                godBagCostraints[12].insets = new Insets(5,3,5,3);
                godsWindowManager.add(imgGodsButton[12], godBagCostraints[12]);

                if(gods.size()==14){
                    godBagCostraints[13].gridy=3;
                    godBagCostraints[13].gridx=0;
                    godBagCostraints[13].insets = new Insets(5,3,5,3);
                    godsWindowManager.add(imgGodsButton[13], godBagCostraints[13]);
                }
            }


        }

        mainFrame.add(godsWindowManager, BorderLayout.CENTER);
    }

    private ImageIcon paintGods(String name){
        try{
            ImageIcon currentImg= new ImageIcon(ImageIO.read(getClass().getResource("/gods/"+name+".png")));
            Image img = currentImg.getImage();
            Image newImg = img.getScaledInstance(130,190, Image.SCALE_SMOOTH);
            ImageIcon finalImg = new ImageIcon(newImg);
            return finalImg;
        }catch (IOException ex){
            return null;
        }

    }

    protected void buildJDialogForFirstPlayer(String god){
        JDialog message = new JDialog(mainFrame);
        JLabel info = new JLabel("The left card is");

        ImageIcon currentImg= new ImageIcon("./resources/gods/"+god+".png");
        Image img = currentImg.getImage();
        Image newImg = img.getScaledInstance(130,190, Image.SCALE_SMOOTH);
        ImageIcon finalImg = new ImageIcon(newImg);
        JLabel image = new JLabel(finalImg);

        FileInputStream fis = null;
        JLabel power = new JLabel();

        try {
            fis = new FileInputStream(god + ".txt");
            ObjectInputStream ois = new ObjectInputStream(fis);
            power.setText((String) ois.readObject());
        } catch (ClassNotFoundException| IOException ex) {
            System.out.println("Errore");
        }

        message.setLayout(new BorderLayout());
        message.add(info, BorderLayout.NORTH);
        message.add(image, BorderLayout.CENTER);

    }

    public JButton[] getImgGodsButtons() {
        return imgGodsButton;
    }

    protected void destroyGodsWindow(int size){
        for(int i=0;i<size;i++){
            godsWindowManager.remove(imgGodsButton[i]);
            imgGodsButton[i]=null;
        }
        mainFrame.remove(godsWindowManager);
        godsWindowManager=null;
    }

    private void buildBoard(){
        board=new JButton[Board.DIM][Board.DIM];

        boardLabel = paintScreen("SantoriniBoard.png", 900, 900);
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

    public JButton[][] getBoard() {
        return board;
    }

    private void buildGodBar(){
        godImage = new JLabel("Loading god's image...");
        godPower = new JLabel("Loading god's power...");
        godPanel = new JPanel();
        godPanel.setLayout(new GridBagLayout());

        GridBagConstraints godImageGBC = new GridBagConstraints();
        godImageGBC.gridx = 0;
        godImageGBC.gridy = 0;
        godPanel.add(godImage, godImageGBC);

        GridBagConstraints godPowerGBC = new GridBagConstraints();
        godPowerGBC.gridx = 0;
        godPowerGBC.gridy = 1;
        godPanel.add(godPower, godPowerGBC);
    }

    protected void updateGodBar(String name, String godName){

        godImage = new JLabel(paintGods(godName));

        try {
            readGodsPower(godName);
        }catch (IOException e){
            godPower.setText("Unable to load god's power");
        }

        SwingUtilities.updateComponentTreeUI(mainFrame);
    }

    public JTextField getIpTextField() {
        return ipTextField;
    }

    public JButton getIpButton() {
        return ipButton;
    }

    public void setIpButton(JButton ipButton) {
        this.ipButton = ipButton;
    }

    private void buildMessageAndUndoBar(){
        mainFrame.remove(upperLabel);

        undo = new JButton("", createIcon("Undo.png"));

        messageAndUndoPanel = new JPanel();
        messageAndUndoPanel.setLayout(new GridBagLayout());
        GridBagConstraints upperLabelGBC = new GridBagConstraints();
        upperLabelGBC.gridx=0;
        upperLabelGBC.gridy=0;
        upperLabelGBC.insets = new Insets(3,3,3,450);
        messageAndUndoPanel.add(upperLabel, upperLabelGBC);

        GridBagConstraints undoGBC = new GridBagConstraints();
        undoGBC.gridx = 1;
        undoGBC.gridy=0;
        undoGBC.insets = new Insets(3,450,3,3);
        messageAndUndoPanel.add(undo, undoGBC);

        mainFrame.add(messageAndUndoPanel, BorderLayout.NORTH);

        SwingUtilities.updateComponentTreeUI(mainFrame);
    }

    public JButton getUndo() {
        return undo;
    }

    protected void buildMainWindow(){
        mainWindowManager = new JLabel();
        buildMessageAndUndoBar();
        buildBoard();
        buildGodBar();

        mainWindowManager.setLayout(new BorderLayout());
        mainWindowManager.add(boardLabel, BorderLayout.CENTER);
        mainWindowManager.add(godPanel, BorderLayout.WEST);

        mainFrame.add(mainWindowManager, BorderLayout.CENTER);
    }

    protected  String readGodsPower(String name) throws IOException{
        InputStream is = GUI.class.getResourceAsStream("/gods/"+name+".txt");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(isr);

        return reader.readLine();
    }

}