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
    private Font defaultFont;

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
    //board elements
    private JLabel boardLabel;
    private SantoriniButton[][] board;
    //godBar elements
    private JLabel godBarManager;
    private JLabel godInfo;
    private SantoriniLabel godImage;
    private ArrayList<JLabel> godPower;

    //Undo
    private JDialog undoDialog;
    private JLabel undoMessage;
    private JLabel undoTimeMessage;
    private JLabel answerManager;
    private JLabel undoImage;
    private JButton yes;
    private JButton no;


    public GUI(){
        mainFrame = new JFrame("Santorini -  Epifani Falleti Landi");
        mainFrame.addWindowListener(new CustomClosing(mainFrame));
        mainFrame.setSize(710, 470);
        mainFrame.setResizable(false);
        defineFont();

        Image mainIcon = null;
        try{
            ImageIcon currentIcon= new ImageIcon(ImageIO.read(getClass().getResource("/MainIcon.jpg")));
            mainIcon = currentIcon.getImage();
        }catch (IOException ex){

        }
        mainFrame.setIconImage(mainIcon);

        mainFrame.setLayout(new BorderLayout());
        upperLabel = new JLabel("Wait a moment, please");
        mainFrame.add(upperLabel, BorderLayout.NORTH);

        waitingLabel = paintScreen("MainBackground.jpg", 700, 400);
        waitingLabel.setLayout(new GridBagLayout());
        mainFrame.add(waitingLabel, BorderLayout.CENTER);

        lowerLabel = new JLabel("The game will start soon");
        mainFrame.add(lowerLabel, BorderLayout.SOUTH);


        ipLabel=new JLabel("IP address: ");
        ipLabel.setFont(defaultFont);
        ipTextField = new JTextField();
        GridBagConstraints ipLabelGBC= new GridBagConstraints();
        ipLabelGBC.gridx=0;
        ipLabelGBC.gridy=0;
        waitingLabel.add(ipLabel,ipLabelGBC);

        GridBagConstraints ipTextFieldGBC = new GridBagConstraints();
        ipTextFieldGBC.gridx = 1;
        ipTextFieldGBC.gridy=0;
        ipTextFieldGBC.ipadx=100;
        ipTextFieldGBC.ipady=10;
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


            ImageIcon warningIcon = createIcon("WarningSign.jpg");

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
            return new JLabel("Unable to load this screen");
        }
    }

    private ImageIcon paintScreenForSantoriniLabel(String name, int width, int height) {
        try{
            ImageIcon currentIcon= new ImageIcon(ImageIO.read(getClass().getResource("/"+name)));
            Image tmp = currentIcon.getImage();
            Image newIcon = tmp.getScaledInstance(width,height, Image.SCALE_SMOOTH);
            ImageIcon finalIcon = new ImageIcon(newIcon);
            return finalIcon;
        }catch (IOException ex){
            return null;
        }
    }

    private ImageIcon paintBackgroundForSantoriniButton(String name) {
        try{
            ImageIcon currentIcon= new ImageIcon(ImageIO.read(getClass().getResource("/"+name)));
            Image tmp = currentIcon.getImage();
            Image newIcon = tmp.getScaledInstance(140,140, Image.SCALE_SMOOTH);
            ImageIcon finalIcon = new ImageIcon(newIcon);
            return finalIcon;
        }catch (IOException ex){
            return null;
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
        numberOfPlayersInformation.setFont(defaultFont);
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
        usernameLabel.setFont(defaultFont);
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
        monthLabel=new JLabel("Mounth of birth (mm) ");
        monthLabel.setFont(defaultFont);
        monthTextField = new JTextField();
        dayLabel = new JLabel("Day of birth (dd) ");
        dayLabel.setFont(defaultFont);
        dayTextField = new JTextField();
        yearLabel = new JLabel("Year of birth (yyyy)");
        yearLabel.setFont(defaultFont);
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
        mainFrame.setSize(1366,728);
        mainFrame.setResizable(true);
        mainFrame.setLocationRelativeTo(null);
        imgGodsButton = new JButton[gods.size()];
        GridBagConstraints[] godBagConstraints= new GridBagConstraints[gods.size()];

        for(int i=0;i<gods.size();i++){
            imgGodsButton[i]=new JButton("",paintGods(gods.get(i)));
            imgGodsButton[i].setContentAreaFilled(false);
            imgGodsButton[i].setBorderPainted(true);
            godBagConstraints[i] = new GridBagConstraints();
        }

        godsWindowManager = paintScreen("GodsScreen.jpg", 1920, 1080);
        godsWindowManager.setLayout(new GridBagLayout());
        int j=0;
        for(int i=0; i<gods.size();i++){
            if(i<5){
                godBagConstraints[i].gridy=1;
                godBagConstraints[i].gridx=j;
                godBagConstraints[i].insets = new Insets(5,3,5,5);
                j++;
            }else if(i==5){
                j=0;
                godBagConstraints[i].gridy=2;
                godBagConstraints[i].gridx=j;
                godBagConstraints[i].insets = new Insets(5,3,5,3);
                j++;
            }else if(i>=6 && i<=9){
                godBagConstraints[i].gridy=2;
                godBagConstraints[i].gridx=j;
                godBagConstraints[i].insets = new Insets(5,3,5,3);
                j++;
            }
            godsWindowManager.add(imgGodsButton[i], godBagConstraints[i]);

            if(gods.size()>3){
                godBagConstraints[10].gridy=3;
                godBagConstraints[10].gridx=1;
                godBagConstraints[10].insets = new Insets(5,3,5,3);
                godsWindowManager.add(imgGodsButton[10], godBagConstraints[10]);

                godBagConstraints[11].gridy=3;
                godBagConstraints[11].gridx=2;
                godBagConstraints[11].insets = new Insets(5,3,5,3);
                godsWindowManager.add(imgGodsButton[11], godBagConstraints[11]);

                godBagConstraints[12].gridy=3;
                godBagConstraints[12].gridx=3;
                godBagConstraints[12].insets = new Insets(5,3,5,3);
                godsWindowManager.add(imgGodsButton[12], godBagConstraints[12]);

                if(gods.size()==14){
                    godBagConstraints[13].gridy=3;
                    godBagConstraints[13].gridx=0;
                    godBagConstraints[13].insets = new Insets(5,3,5,3);
                    godsWindowManager.add(imgGodsButton[13], godBagConstraints[13]);
                }
            }


        }

        mainFrame.add(godsWindowManager, BorderLayout.CENTER);
    }

    private ImageIcon paintGods(String name){
        String path = "/gods/"+name+".jpg";
        try{
            ImageIcon currentImg= new ImageIcon(GUI.class.getResource(path));
            Image img = currentImg.getImage();
            Image newImg = img.getScaledInstance(130,190, Image.SCALE_SMOOTH);
            ImageIcon finalImg = new ImageIcon(newImg);
            return finalImg;
        }catch (Exception ex){
            System.out.println("Problema nel jar");
            return null;
        }

    }

    protected void buildJDialogForFirstPlayer(String god){
        JDialog message = new JDialog(mainFrame);
        JLabel screen = paintScreen("MainBackground.jpg", 1150, 580);
        message.setTitle("Santorini - Epifani Falleti Landi");

        JLabel info = new JLabel("The left card is");
        info.setFont(defaultFont);

        JLabel image = new JLabel(paintGods(god));
        JLabel power = new JLabel();
        power.setFont(defaultFont);

        try {
            readGodsPower(power, god);
        } catch (IOException ex) {
            power.setText("Unable to load god's power");
        }

        screen.setLayout(new GridBagLayout());
        GridBagConstraints infoGBC = new GridBagConstraints();
        infoGBC.gridy=1;
        infoGBC.gridx=1;
        screen.add(info, infoGBC);

        GridBagConstraints imageGBC = new GridBagConstraints();
        imageGBC.gridy = 2;
        imageGBC.gridx = 1;
        imageGBC.insets = new Insets(15, 3, 15, 3);
        imageGBC.ipadx = 150;
        imageGBC.ipady = 210;
        screen.add(image, imageGBC);

        GridBagConstraints powerGBC = new GridBagConstraints();
        powerGBC.gridx = 1;
        powerGBC.gridy = 3;
        screen.add(power, powerGBC);

        message.add(screen);
        message.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        message.setResizable(false);
        message.setSize(1000, 510);
        message.setLocationRelativeTo(null);
        message.setVisible(true);

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
        board=new SantoriniButton[Board.DIM][Board.DIM];
        GridBagConstraints[][] boardCostraints = new GridBagConstraints[Board.DIM][Board.DIM];

        boardLabel = new JLabel("");
        boardLabel.setSize(700, 700);
        boardLabel.setLayout(new GridBagLayout());

        for(int i=0;i<Board.DIM;i++){
            for(int j=0;j<Board.DIM;j++){
                boardCostraints[i][j] = new GridBagConstraints();
                boardCostraints[i][j].gridx=j;
                boardCostraints[i][j].gridy=i;
                board[i][j]= new SantoriniButton(paintBackgroundForSantoriniButton("BoardCell.jpg"));
                board[i][j].setPreferredSize(new Dimension(140,140));
                boardLabel.add(board[i][j], boardCostraints[i][j]);
            }
        }
        SwingUtilities.updateComponentTreeUI(mainFrame);
    }

    public SantoriniButton[][] getBoard() {
        return board;
    }

    private void buildGodBar(){
        godInfo = new JLabel();
        godInfo.setFont(defaultFont);
        godImage = new SantoriniLabel(paintScreenForSantoriniLabel("DefaultGodImage.jpg", 240, 360));
        godBarManager = new JLabel();
        godBarManager.setLayout(new GridBagLayout());

        GridBagConstraints godInfoGBC = new GridBagConstraints();
        godInfoGBC.gridx = 1;
        godInfoGBC.gridy = 0;
        godInfoGBC.insets = new Insets(5,3,3,3);
        godBarManager.add(godInfo, godInfoGBC);

        GridBagConstraints godImageGBC = new GridBagConstraints();
        godImageGBC.gridx = 1;
        godImageGBC.gridy = 1;
        godImageGBC.insets = new Insets(3,3,5,3);
        godBarManager.add(godImage, godImageGBC);

    }

   protected void updateGodBar(String info, String godName){
        godInfo.setText(info);
        godImage.setPath("/gods/" + godName+ ".jpg");
        Graphics g = godImage.getGraphics();
        godImage.paintComponent(g);

        try {
            readGodsPowerForGodBar(godName);
        }catch (IOException e){
            godPower.add(new JLabel("Unable to load god's power"));
        }

        GridBagConstraints[] godPowerConstraints = new GridBagConstraints[godPower.size()];
        for (int i=0;i<godPower.size();i++){
            godPowerConstraints[i] = new GridBagConstraints();
            godPowerConstraints[i].gridx=1;
            godPowerConstraints[i].gridy=i+2;
            godPowerConstraints[i].insets = new Insets(3,3,3,3);
            godBarManager.add(godPower.get(i), godPowerConstraints[i]);
        }

        SwingUtilities.updateComponentTreeUI(mainFrame);
    }

    public JTextField getIpTextField() {
        return ipTextField;
    }

    public JButton getIpButton() {
        return ipButton;
    }

    protected void buildUndoJDialog(){
        undoDialog = new JDialog(mainFrame);
        undoMessage = new JLabel("Do you want to undo your last move?");
        undoTimeMessage = new JLabel("(You have only 5 seconds to choose)");
        answerManager = new JLabel();
        ImageIcon undoIcon = createIcon("Undo.jpg");
        undoImage = new JLabel(undoIcon);
        yes = new JButton("Yes");
        no = new JButton("No");

        undoDialog.setLayout(new GridBagLayout());

        answerManager.setLayout(new GridBagLayout());
        GridBagConstraints undoImageGBC = new GridBagConstraints();
        undoImageGBC.gridx=0;
        undoImageGBC.gridy=2;
        undoImageGBC.insets = new Insets(3,3,3,5);
        answerManager.add(undoImage, undoImageGBC);

        GridBagConstraints yesGBC = new GridBagConstraints();
        yesGBC.gridx=1;
        yesGBC.gridy=2;
        yesGBC.insets = new Insets(3,3,3,3);
        answerManager.add(yes, yesGBC);

        GridBagConstraints noGBC = new GridBagConstraints();
        noGBC.gridx = 2;
        noGBC.gridy = 2;
        noGBC.insets = new Insets(3,3,3,3);
        answerManager.add(no, noGBC);

        GridBagConstraints answerManagerGBC = new GridBagConstraints();
        answerManagerGBC.gridy=3;
        answerManagerGBC.gridx=1;
        answerManagerGBC.insets = new Insets(5,3,3,3);
        answerManagerGBC.ipadx=200;
        answerManagerGBC.ipady=55;
        undoDialog.add(answerManager, answerManagerGBC);

        GridBagConstraints undoMessageGBC = new GridBagConstraints();
        undoMessageGBC.gridx=1;
        undoMessageGBC.gridy=1;
        undoMessageGBC.insets = new Insets(3,3,3,3);
        undoDialog.add(undoMessage, undoMessageGBC);

        GridBagConstraints undoTimeMessageGBC = new GridBagConstraints();
        undoTimeMessageGBC.gridx=1;
        undoTimeMessageGBC.gridy=2;
        undoTimeMessageGBC.insets = new Insets(3,3,5,3);
        undoDialog.add(undoTimeMessage, undoTimeMessageGBC);

        undoDialog.setSize(440, 190);
        undoDialog.setResizable(false);
        undoDialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        undoDialog.setLocationRelativeTo(null);
        undoDialog.setVisible(true);
    }

    public JDialog getUndoDialog() {
        return undoDialog;
    }

    public JLabel getUndoTimeMessage() {
        return undoTimeMessage;
    }

    public JButton getYes() {
        return yes;
    }

    public JButton getNo() {
        return no;
    }

    protected void buildMainWindow(){
        mainWindowManager = paintScreen("GodsScreen.jpg", 1920,1080);
        buildBoard();
        buildGodBar();

        mainWindowManager.setLayout(new BorderLayout());
        mainWindowManager.add(boardLabel, BorderLayout.CENTER);
        mainWindowManager.add(godBarManager, BorderLayout.WEST);

        mainFrame.add(mainWindowManager, BorderLayout.CENTER);
    }

    protected void readGodsPower(JLabel label, String name) throws IOException{
        InputStream is = GUI.class.getResourceAsStream("/gods/"+name);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(isr);
        String line = null;

        while((line = reader.readLine()) != null)
        {
            label.setText(line);
        }

        reader.close();
        isr.close();
        is.close();

    }

    private void readGodsPowerForGodBar(String name) throws IOException{
        godPower = new ArrayList<JLabel>();
        InputStream is = GUI.class.getResourceAsStream("/gods/"+name+"GodBar");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(isr);
        String line = null;

        while((line = reader.readLine()) != null)
        {
            JLabel lineLabel = new JLabel(line);
            lineLabel.setFont(defaultFont);
            godPower.add(lineLabel);
        }

        reader.close();
        isr.close();
        is.close();
    }

    private void defineFont(){
        defaultFont = new Font(Font.SERIF, Font.BOLD, 25);
    }

}