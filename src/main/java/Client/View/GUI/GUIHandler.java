package Client.View.GUI;
import javax.swing.*;

import Client.Controller.Controller;
import Client.View.ClientNetworkHandler;
import Client.View.Data;
import Controller.Coordinates;
import Controller.Network.VCEvent;
import Model.*;
import Model.Color;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class GUIHandler {
    //GUI elements to handle
    private GUI GUI;

    //To connect
    private ClientNetworkHandler cnh;
    private Thread game;
    private boolean updateView;

    //Support elements
    private String ip;
    protected static int playersNumber;
    private int playerID;
    protected static boolean ready;
    private String myUsername;
    private Data myDate;
    private String myColor;
    private ArrayList<String> sentGods;
    private int godsSize;
    private int godsCounter;
    private GodsWorker[] gw;
    protected static ArrayList<String> chosenGods;
    protected static String myGod;
    private boolean checkSendCells;
    private boolean checkUpdate;
    private Board b;
    private String movingPhase = "go on";
    private String buildingPhase = "build on";
    private String removePhase = "remove";
    protected static Coordinates currentCoordinate;
    private BoardCellWorker[][] bcw;
    protected static int choose;

    public GUIHandler(GUI GUI,String ip){
        this.GUI = GUI;
        this.ip = ip;

        //Initial settings
        playersNumber=0;
        playerID=0;
        godsCounter = 0;
        checkSendCells=false;
        checkUpdate=false;
    }

    public void launchConnection(){
        //Creazione della socket del client
        cnh = new ClientNetworkHandler(this,this.ip);

        //Thread per l'esecuzione principale della CLI
        game = new Thread(cnh);
        game.start();

        //Ciclo di gioco
        checkEvent(cnh);

        cnh.setFinish(true);
    }

    public synchronized void updateGo()
    {
        updateView = true;
        notifyAll();
    }
    public void update()
    {
        synchronized (this)
        {
            while(updateView == false)
            {
                try{
                    wait();
                }
                catch(InterruptedException e){}
            }
        }
        updateView = false;

    }


   private void checkEvent(ClientNetworkHandler cnh) {
       boolean endGame = false;
       Controller clientSideController = new Controller();
        // Board copy = new Board();
        // int firstTimeHeMoves = 1;
        while(endGame==false){
            update();
            VCEvent evento = cnh.getFromServer();
            cnh.readByView();
            switch (evento.getCommand()){
                case send_color:
                    System.out.println("Assegnamento del colore");
                    Object objcectColorName = evento.getBox();
                    this.myColor = (String) objcectColorName;
                    playerID=understandPlayerIDFromColor(this.myColor);
                    System.out.println("Il tuo playerID è " + playerID);
                    break;
                case setup_request:
                    GUI.getUpperLabel().setText("Press button 2 if you want to play with another person or button 3 to play with two friends");
                    GUI.getLowerLabel().setText("Other options aren't allowed");
                    playerID=1;
                    ready=false;
                    GUI.destroyWaitingLabel();
                    GUI.buildNumberOfPlayersWindow();
                    SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
                    CustomNumberOfPlayersListener twoListener = new CustomNumberOfPlayersListener(2);
                    GUI.getTwo().addActionListener(twoListener);
                    CustomNumberOfPlayersListener threeListener = new CustomNumberOfPlayersListener(3);
                    GUI.getThree().addActionListener(threeListener);
                    while(GUIHandler.ready==false){
                        System.out.println("Attendo scelta...");
                    }
                    GUI.getLowerLabel().setText("Wait...");
                    buildEvent(cnh, playersNumber, VCEvent.Event.setup_request);
                    break;
                case username_request :
                    if(playerID==1){
                        GUI.destroyNumberOfPlayersWindow();
                    }else{
                        GUI.destroyWaitingLabel();
                    }
                    GUI.getUpperLabel().setText("Insert a username in the box below, then press button Next");
                    GUI.getLowerLabel().setText("Please remember that you can't insert an another player's username");
                    GUI.buildLoginWindow();
                    SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
                    LoginNextButtonCustomActionListener usernameListener=new LoginNextButtonCustomActionListener();
                    GUI.getLoginNextButton().addActionListener(usernameListener);
                    while (usernameListener.getGoForward()==false){
                        System.out.println("Attendo che il giocatore scelga il suo username");
                    }
                    GUI.getLowerLabel().setText("Wait...");
                    buildEvent(cnh, myUsername, VCEvent.Event.username_request);
                    break;
                case wrong_username:
                    GUI.getLowerLabel().setText("This username has been already chosen. Insert a new one, please");
                    SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
                    LoginNextButtonCustomActionListener wrongUsernameListener=new LoginNextButtonCustomActionListener();
                    GUI.getLoginNextButton().addActionListener(wrongUsernameListener);
                    while (wrongUsernameListener.getGoForward()==false){
                        System.out.println("Attendo che il giocatore reinserisca il suo username");
                    }
                    buildEvent(cnh, myUsername, VCEvent.Event.wrong_username);
                    GUI.getLowerLabel().setText("Wait...");
                    break;
                case date_request:
                    GUI.getUpperLabel().setText("Insert first month (mm), then day (dd) and finally year (yyyy) of your own date of birth. Press Next to send");
                    GUI.getLowerLabel().setText("The youngest player will be the first");
                    GUI.destroyLoginWindow();
                    GUI.buildDateWindow();
                    SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
                    requestDate();
                    while(clientSideController.controllaData(myDate)==false){
                        GUI.getLowerLabel().setText("Invalid date format, please try again");
                        requestDate();
                    }
                    GUI.getLowerLabel().setText("Wait...");
                    buildEvent(cnh, myDate, VCEvent.Event.date_request);
                    break;
                case turnNumber:
                    Object objectPlayerID = evento.getBox();
                    playerID = ((Integer) objectPlayerID).intValue();
                    break;
                case not_your_turn:
                    Object objectCurrentPlayerInformation = evento.getBox();
                    ArrayList<String> currentPlayerInformation= (ArrayList<String>) objectCurrentPlayerInformation;
                    GUI.getUpperLabel().setText(currentPlayerInformation.get(0).toLowerCase() + " is playing with god " + currentPlayerInformation.get(1).toLowerCase());
                    GUI.updateGodBar("Your opponent's card is: ", currentPlayerInformation.get(1).toLowerCase());
                    SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
                    break;
                case update:
                    if(playerID!=1 && checkUpdate==false){
                        GUI.getUpperLabel().setText("");
                        GUI.getLowerLabel().setText("");
                        GUI.destroyGodsWindow(godsSize);
                        GUI.buildMainWindow();
                        GUI.updateGodBar("Your card is: ", myGod.toLowerCase());
                        SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
                        checkUpdate = true;
                    }
                    Object objectBoardCell = evento.getBox();
                    ArrayList<SocketBoardCell> socketBoardCell = (ArrayList<SocketBoardCell>) objectBoardCell;
                    recreateBoardfromSocketBoardCell(socketBoardCell);
                    turnModelBoardintoGUIBoard(b);
                    break;
                case ask_for_worker:
                    ready=false;
                    GUI.getUpperLabel().setText("Select a worker, you will find additional information at the bottom ");
                    GUI.getLowerLabel().setText("");
                    Object objectChoices = evento.getBox();
                    ArrayList<Coordinates> positionWorkers = (ArrayList<Coordinates>) objectChoices;
                    paintBoardCell(positionWorkers);
                    SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
                    currentCoordinate = new Coordinates(-1,-1);
                    bcw = new BoardCellWorker[Board.DIM][Board.DIM];
                    createBoardCellMouseListener(bcw, GUI.getBoard(), positionWorkers);
                    while(currentCoordinate.getX()==-1 && currentCoordinate.getY()==-1){
                        System.out.println("Attendo che il player scelga quale worker usare");
                    }
                    turnModelBoardintoGUIBoard(b);
                    SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
                    GUI.getLowerLabel().setText("Wait...");
                    closeCellsWorkers();
                    int controlAsk=findIndex(positionWorkers, currentCoordinate);
                    while(controlAsk==-1){
                        GUI.getLowerLabel().setText("Invalid slection! Please select another cell");
                        selectNewCell(positionWorkers);
                        controlAsk=findIndex(positionWorkers, currentCoordinate);
                    }
                    currentCoordinate = new Coordinates(-1,-1);
                    buildEvent(cnh,controlAsk, VCEvent.Event.ask_for_worker);
                    break;
                case send_cells_move:
                    GUI.getUpperLabel().setText("");
                    GUI.getLowerLabel().setText("");
                    if(playerID==1 && checkSendCells==false){
                            GUI.destroyGodsWindow(godsSize);
                            GUI.buildMainWindow();
                            SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
                            this.b = new Board();
                            turnModelBoardintoGUIBoard(b);
                            checkSendCells=true;
                    }
                    GUI.updateGodBar("Your card is: ", myGod.toLowerCase());
                    ready=false;
                    sendCells(movingPhase, cnh, VCEvent.Event.send_cells_move,evento);
                    break;
                case send_cells_build:
                    sendCells(buildingPhase, cnh, VCEvent.Event.send_cells_build,evento);
                    break;
                case send_cells_remove:
                    sendCells(removePhase,cnh, VCEvent.Event.send_cells_remove,evento);
                    break;
                case undo_request:
                    GUI.buildUndoJDialog();
                    choose=-1;
                    SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
                    UndoCustomListener uclYes = new UndoCustomListener(1);
                    GUI.getYes().addActionListener(uclYes);
                    UndoCustomListener uclNo = new UndoCustomListener(0);
                    GUI.getNo().addActionListener(uclNo);
                    long start = System.currentTimeMillis();
                    while (true){

                            if ((System.currentTimeMillis()-start) >= 5000 || choose!=-1) {
                                break;
                            }else{
                                GUI.getUndoTimeMessage().setText((5-TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()-start)) + " seconds left to choose");
                            }

                    }
                    GUI.getUndoDialog().setVisible(false);
                    SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());

                    if (choose==-1){
                            choose = 0;
                    }

                    buildEvent(cnh,choose, VCEvent.Event.undo_request);
                    break;
                case you_lost:
                    Object objectWinner = evento.getBox();
                    String winner = (String) objectWinner;
                    ImageIcon endGameIcon = GUI.createIcon("Lost.png");
                    Object[] defeatChoices = {"Ok"};
                    int defeat = JOptionPane.showOptionDialog(GUI.getMainFrame(),
                            winner + " is the winner. The game will end now!",
                            "It's over",
                            JOptionPane.YES_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            endGameIcon,
                            defeatChoices,
                            defeatChoices
                    );
                    System.exit(0);
                    buildEvent(cnh,"ho perso", VCEvent.Event.you_lost);
                    endGame=true;
                    break;
                case you_won:
                    ImageIcon winIcon = GUI.createIcon("Win.png");
                    Object[] winChoices = {"Ok"};
                    int win = JOptionPane.showOptionDialog(GUI.getMainFrame(),
                            "You are the winner. Thanks to have played",
                            "Congratulations",
                            JOptionPane.YES_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            winIcon,
                            winChoices,
                            winChoices
                    );
                    System.exit(0);
                    buildEvent(cnh,"ho vinto", VCEvent.Event.you_won);
                    endGame=true;
                    break;
                case game_ended_foryou:
                    ImageIcon endGameForYouIcon = GUI.createIcon("Lost.png");
                    Object[] options = {"Ok"};
                    int lost = JOptionPane.showOptionDialog(GUI.getMainFrame(),
                            "You can't move anymore. It's over for you",
                            "It's over",
                            JOptionPane.YES_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            endGameForYouIcon,
                            options,
                            options
                    );
                    GUI.getUpperLabel().setText("You can still watch the game");
                    break;
                case number_of_players:
                    playersNumber = (Integer)evento.getBox();
                    break;
                case ask_for_divinity_activation:
                    ImageIcon godPowerActivationIcon = GUI.createIcon("QuestionSign.png");
                    Object[] choices = {"Yes", "No"};
                    int choice = JOptionPane.showOptionDialog(GUI.getMainFrame(),
                                    "Do you want to use " + myGod + "'s power?",
                                    "Card activation",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    godPowerActivationIcon,
                                    choices,
                                    choices[1]);

                    buildEvent(cnh, turnChoiceIntoCorrectOutput(choice), VCEvent.Event.ask_for_divinity_activation);
                    break;
                case send_all_cards:
                    GUI.getUpperLabel().setText("You have to choose " + playersNumber + " card");
                    GUI.getLowerLabel().setText("");
                    ready=false;
                    Object objectGods = evento.getBox();
                    sentGods= (ArrayList<String>)  objectGods;
                    godsSize = sentGods.size();
                    gw = new GodsWorker[godsSize];
                    GUI.destroyDateWindow();
                    GUI.buildGodsWindow(sentGods);
                    SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
                    chosenGods = new ArrayList<>(2);
                    createGodsListener(godsSize, sentGods, GUI.getImgGodsButtons(), GUI.getLowerLabel());
                    while(playersNumber!=chosenGods.size()){
                        System.out.println("Sto aspettando che il primo player scelga le divinità");
                    }
                    GUI.getLowerLabel().setText("Wait...");
                    closeGodsWorkers();
                    buildEvent(cnh, chosenGods, VCEvent.Event.send_all_cards);
                    break;
                case send_chosen_cards:
                    GUI.getUpperLabel().setText("Select the god who prefer");
                    GUI.getLowerLabel().setText("");
                    Object objectSentGods = evento.getBox();
                    sentGods= (ArrayList<String>)  objectSentGods;
                    godsSize = sentGods.size();
                    if(godsSize==1){
                        myGod=sentGods.get(0);
                        GUI.buildJDialogForFirstPlayer(myGod.toLowerCase());
                        buildEvent(cnh, myGod, VCEvent.Event.send_chosen_cards);
                    }else{
                        ready=false;
                        GUI.destroyDateWindow();
                        GUI.buildGodsWindow(sentGods);
                        SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
                        createGodListener(godsSize, sentGods, GUI.getImgGodsButtons(), GUI.getLowerLabel());
                        while(ready==false){
                            System.out.println("Sto aspettando che il client scelga la sua divinità");
                        }
                        GUI.getLowerLabel().setText("Wait...");
                        buildEvent(cnh, myGod, VCEvent.Event.send_chosen_cards);
                    }
                    break;
                case player_disconnected_game_ended:
                    ImageIcon disconnectionIcon = GUI.createIcon("WarningSign.png");

                    Object objectPlayerDisconnected= evento.getBox();
                    String playerDisconnected = (String) objectPlayerDisconnected;
                    Object[] disconnectionChoices = {"Ok"};
                    int disconnectionChoice = JOptionPane.showOptionDialog(GUI.getMainFrame(),
                            "Ops! Player " + playerDisconnected + " is offline. The game will end now!",
                            "Disconnection detected",
                            JOptionPane.YES_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            disconnectionIcon,
                            disconnectionChoices,
                            disconnectionChoices
                    );
                    System.exit(0);
                    endGame=true;
                    break;
                default:
                    System.out.println("Errore nel protocollo");
                    break;
            }

        }
    }


    private void recreateBoardfromSocketBoardCell(ArrayList<SocketBoardCell> socketBoardCell) {
        this.b = new Board();

        int counter = 0;
        for (int i = 0; i < Board.DIM; i++) {
            for (int j = 0; j < Board.DIM; j++) {
                b.setBoardHeight(i, j, socketBoardCell.get(counter).getHeight());
                if (socketBoardCell.get(counter).getWorkerColor() != null) {
                    b.setBoardWorker(i, j, new Worker(i, j, socketBoardCell.get(counter).getWorkerColor()));
                } else {
                    b.setBoardWorker(i, j, null);
                }
                counter++;
            }

        }
    }

    private void turnModelBoardintoGUIBoard(Board b){
        for(int i=0;i<Board.DIM;i++) {
            for (int j = 0; j < Board.DIM; j++) {
                /*if (b.getBoardWorker(i, j) == null) {
                    GUI.getBoard()[i][j].setText("Worker: null" + "    Height:" + b.getBoardHeight(i, j));
                } else {
                    GUI.getBoard()[i][j].setText("Worker: " + b.getBoardWorker(i, j) + "    Height:" + b.getBoardHeight(i, j));
                }*/
                Color worker = null;
                if (b.getBoardWorker(i, j) != null) {
                    worker = (b.getBoardWorker(i, j)).getColor();
                }
                switch (b.getBoardHeight(i, j)) {
                    case 0:
                        if (b.getBoardWorker(i, j) != null) {
                            if (worker.equals(Model.Color.ANSI_YELLOW)) {
                                updateSantoriniButton(i,j, "/DeusExMachina/blue.jpg");
                            } else if (worker.equals(Model.Color.ANSI_WHITE)) {
                                updateSantoriniButton(i,j, "/DeusExMachina/white.jpg");
                            } else if (worker.equals(Model.Color.ANSI_PURPLE)) {
                                updateSantoriniButton(i,j, "/DeusExMachina/brown.jpg");
                            }
                        }else if(b.getBoardWorker(i, j) == null){
                            updateSantoriniButton(i,j, "/BoardCell.jpg");
                        }
                        break;
                    case 1:
                        if (b.getBoardWorker(i, j) != null) {
                            if (worker.equals(Color.ANSI_YELLOW)) {
                                updateSantoriniButton(i,j, "/DeusExMachina/blue1.jpg");
                            } else if (worker.equals(Color.ANSI_WHITE)) {
                                updateSantoriniButton(i,j, "/DeusExMachina/white1.jpg");
                            } else if (worker.equals(Color.ANSI_PURPLE)) {
                                updateSantoriniButton(i,j, "/DeusExMachina/brown1.jpg");
                             }
                        } else if (b.getBoardWorker(i, j) == null) {
                            updateSantoriniButton(i,j, "/DeusExMachina/1.jpg");
                        }
                        break;
                    case 2:
                        if (b.getBoardWorker(i, j) != null) {
                            if (worker.equals(Color.ANSI_YELLOW)) {
                                updateSantoriniButton(i,j, "/DeusExMachina/blue2.jpg");
                            } else if (worker.equals(Color.ANSI_WHITE)) {
                                updateSantoriniButton(i,j, "/DeusExMachina/white2.jpg");
                            } else if (worker.equals(Color.ANSI_PURPLE)) {
                                updateSantoriniButton(i,j, "/DeusExMachina/brown2.jpg");
                            }
                        } else if (b.getBoardWorker(i, j) == null) {
                            updateSantoriniButton(i,j, "/DeusExMachina/2.jpg");
                        }
                        break;
                    case 3:
                        if (b.getBoardWorker(i, j) != null) {
                            if (worker.equals(Color.ANSI_YELLOW)) {
                                updateSantoriniButton(i,j, "/DeusExMachina/blue3.jpg");
                            } else if (worker.equals(Color.ANSI_WHITE)) {
                                updateSantoriniButton(i,j, "/DeusExMachina/white3.jpg");
                            } else if (worker.equals(Color.ANSI_PURPLE)) {
                                updateSantoriniButton(i,j, "/DeusExMachina/brown3.jpg");
                            }
                        } else if (b.getBoardWorker(i, j) == null) {
                            updateSantoriniButton(i,j, "/DeusExMachina/3.jpg");
                        }
                        break;
                    case 4:
                        updateSantoriniButton(i,j, "/DeusExMachina/4.jpg");
                        break;
                    default:
                        break;
                }
            }
        }

        SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
    }

    private void updateSantoriniButton(int i, int j, String path){
        GUI.getBoard()[i][j].setPath(path);
        Graphics g = GUI.getBoard()[i][j].getGraphics();
        GUI.getBoard()[i][j].paintComponent(g);
    }

    private void buildEvent(ClientNetworkHandler cnh, Object o, VCEvent.Event command){
        VCEvent currentEvent= new VCEvent(o, command);
        cnh.sendVCEvent(currentEvent);
    }

    private int turnChoiceIntoCorrectOutput(int choice){
        if(choice==0){
            return 1;
        }else if(choice==1){
            return 0;
        }else if(choice==-1){
            return 0;
        }else{
            return -1;
        }
    }

    class LoginNextButtonCustomActionListener implements ActionListener{
        private boolean goForward;

        public LoginNextButtonCustomActionListener(){
            goForward=false;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            myUsername=GUI.getUsernameTextField().getText();
            goForward=true;
        }

        public boolean getGoForward(){
            return goForward;
        }
    }



    class CustomNumberOfPlayersListener implements ActionListener{
        private int chosenNumberOfPlayers;

        public CustomNumberOfPlayersListener(int chosenNumberOfPlayers){
            this.chosenNumberOfPlayers=chosenNumberOfPlayers;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            NumberOfPlayersWorker npw= new NumberOfPlayersWorker(chosenNumberOfPlayers);
        }
    }


    class DateNextButtonCustomActionListener implements ActionListener{
        private boolean goForward;

        public DateNextButtonCustomActionListener(){
            goForward=false;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int month = Integer.parseInt(GUI.getMonthTextField().getText());
            int day = Integer.parseInt(GUI.getDayTextField().getText());
            int year = Integer.parseInt(GUI.getYearTextField().getText());
            myDate = new Data(day,month,year);
            goForward=true;
        }

        public boolean isGoForward() {
            return goForward;
        }
    }

    private void createGodListener(int size, ArrayList<String> sentGods, JButton[] godsButton, JLabel lowerLabel){
        for (int i=0;i<size;i++){
            System.out.println("Aggiungo al bottone il listener");
            godsButton[i].addMouseListener(new SentGodMouseListener(sentGods.get(i), lowerLabel));
        }
    }


    class SentGodsMouseListener implements MouseListener {
        private String chosenGod;
        private JLabel lowerLabel;

        public SentGodsMouseListener(String chosenGod, JLabel lowerLabel) {
            this.chosenGod = chosenGod;
            this.lowerLabel=lowerLabel;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(checkIfGodisAlreadyChosen(chosenGods, chosenGod)==true){
                lowerLabel.setText("Pay attention, you can't choose again this god. Select another one, please");
            }else {
                gw[godsCounter] = new GodsWorker(chosenGod, GUI.getUpperLabel());
                godsCounter++;
            }
        }


        @Override
        public void mousePressed(MouseEvent e) {
            if(checkIfGodisAlreadyChosen(chosenGods, chosenGod)==true){
                lowerLabel.setText("Pay attention, you can't choose again this god. Select another one, please");
            }else {
                gw[godsCounter] = new GodsWorker(chosenGod, GUI.getUpperLabel());
                godsCounter++;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {



        }

        @Override
        public void mouseEntered(MouseEvent e) {
            try {
                GUI.readGodsPower(GUI.getLowerLabel(), chosenGod);
            }catch (IOException ex){
                GUI.getLowerLabel().setText("Unable to read god's power");
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            lowerLabel.setText("");
        }
    }

    class SentGodMouseListener implements MouseListener{
        private String chosenGod;
        private JLabel lowerLabel;

        public SentGodMouseListener(String chosenGod, JLabel lowerLabel) {
            this.chosenGod = chosenGod;
            this.lowerLabel=lowerLabel;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            GodWorker singleGodWorker= new GodWorker(chosenGod);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            GodWorker singleGodWorker= new GodWorker(chosenGod);
        }



        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {
            try {
                GUI.readGodsPower(GUI.getLowerLabel(), chosenGod);
            }catch (IOException ex){
                GUI.getLowerLabel().setText("Unable to read god's power");
            }

        }

        @Override
        public void mouseExited(MouseEvent e) {
            lowerLabel.setText("");
        }
    }

    public void sendCells(String phase, ClientNetworkHandler cnh, VCEvent.Event command, VCEvent evento){
        GUI.getUpperLabel().setText("Select the red cell where you want to " + phase);
        GUI.getLowerLabel().setText("");
        Object objectValidPositions = evento.getBox();
        ArrayList<Coordinates> validPositions = (ArrayList<Coordinates>)objectValidPositions;
        bcw = new BoardCellWorker[Board.DIM][Board.DIM];
        paintBoardCell(validPositions);
        SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
        currentCoordinate = new Coordinates(-1,-1);
        createBoardCellMouseListener(bcw, GUI.getBoard(), validPositions);
        while(currentCoordinate.getX()==-1 && currentCoordinate.getY()==-1){
            System.out.println("Attendo che il client selezioni una cella");
        }
        turnModelBoardintoGUIBoard(b);
        SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
        GUI.getLowerLabel().setText("Wait...");
        closeCellsWorkers();
        int control=findIndex(validPositions,currentCoordinate);
        while(control==-1){
            GUI.getLowerLabel().setText("Invalid move! Please select another cell");
            selectNewCell(validPositions);
            control=findIndex(validPositions, currentCoordinate);
        }
        currentCoordinate =  new Coordinates(-1,-1);
        buildEvent(cnh,control,command);

    }

    private void paintBoardCell(ArrayList<Coordinates> validPositions) {
        int i, j;
                for (Coordinates c : validPositions) {
                        Color worker = null;
                        i=c.getX();
                        j=c.getY();
                        if (b.getBoardWorker(i, j) != null) {
                            worker = (b.getBoardWorker(i, j)).getColor();
                        }
                        switch (b.getBoardHeight(i, j)) {
                            case 0:
                                if (b.getBoardWorker(i, j) != null) {
                                    if (worker.equals(Model.Color.ANSI_YELLOW)) {
                                        updateSantoriniButton(i, j, "/DeusExMachina/RedBlue.jpg");
                                    } else if (worker.equals(Model.Color.ANSI_WHITE)) {
                                        updateSantoriniButton(i, j, "/DeusExMachina/RedWhite.jpg");
                                    } else if (worker.equals(Model.Color.ANSI_PURPLE)) {
                                        updateSantoriniButton(i, j, "/DeusExMachina/RedBrown.jpg");
                                    }
                                } else if (b.getBoardWorker(i, j) == null) {
                                    updateSantoriniButton(i, j, "/DeusExMachina/Red0.jpg");
                                }
                                break;
                            case 1:
                                if (b.getBoardWorker(i, j) != null) {
                                    if (worker.equals(Color.ANSI_YELLOW)) {
                                        updateSantoriniButton(i, j, "/DeusExMachina/RedBlue1.jpg");
                                    } else if (worker.equals(Color.ANSI_WHITE)) {
                                        updateSantoriniButton(i, j, "/DeusExMachina/RedWhite1.jpg");
                                    } else if (worker.equals(Color.ANSI_PURPLE)) {
                                        updateSantoriniButton(i, j, "/DeusExMachina/RedBrown1.jpg");
                                    }
                                } else if (b.getBoardWorker(i, j) == null) {
                                    updateSantoriniButton(i, j, "/DeusExMachina/Red1.jpg");
                                }
                                break;
                            case 2:
                                if (b.getBoardWorker(i, j) != null) {
                                    if (worker.equals(Color.ANSI_YELLOW)) {
                                        updateSantoriniButton(i, j, "/DeusExMachina/RedBlue2.jpg");
                                    } else if (worker.equals(Color.ANSI_WHITE)) {
                                        updateSantoriniButton(i, j, "/DeusExMachina/RedWhite2.jpg");
                                    } else if (worker.equals(Color.ANSI_PURPLE)) {
                                        updateSantoriniButton(i, j, "/DeusExMachina/RedBrown2.jpg");
                                    }
                                } else if (b.getBoardWorker(i, j) == null) {
                                    updateSantoriniButton(i, j, "/DeusExMachina/Red2.jpg");
                                }
                                break;
                            case 3:
                                if (b.getBoardWorker(i, j) != null) {
                                    if (worker.equals(Color.ANSI_YELLOW)) {
                                        updateSantoriniButton(i, j, "/DeusExMachina/RedBlue3.jpg");
                                    } else if (worker.equals(Color.ANSI_WHITE)) {
                                        updateSantoriniButton(i, j, "/DeusExMachina/RedWhite3.jpg");
                                    } else if (worker.equals(Color.ANSI_PURPLE)) {
                                        updateSantoriniButton(i, j, "/DeusExMachina/RedBrown3.jpg");
                                    }
                                } else if (b.getBoardWorker(i, j) == null) {
                                    updateSantoriniButton(i, j, "/DeusExMachina/Red3.jpg");
                                }
                                break;
                            case 4:
                                updateSantoriniButton(i, j, "/DeusExMachina/Red4.jpg");
                                break;
                            default:
                                break;
                        }
                    }
    }

    private void createGodsListener(int size, ArrayList<String> sentGods, JButton[] godsButton, JLabel lowerLabel){
        for (int i=0;i<size;i++){
            System.out.println("Aggiungo al bottone il listener");
            godsButton[i].addMouseListener(new SentGodsMouseListener(sentGods.get(i), lowerLabel));
        }
    }

    private void createBoardCellMouseListener(BoardCellWorker[][] bcw, JButton[][] GUIBoard, ArrayList<Coordinates> validPositions){
        for (int i=0;i<Board.DIM;i++){
            for(int j=0;j<Board.DIM; j++){
                for(Coordinates c: validPositions){
                    if(c.getX()==i && c.getY()==j){
                        GUIBoard[i][j].addMouseListener(new BoardCellMouseListener(bcw, GUI.getLowerLabel(), i, j, b.getBoardHeight(i,j), b.getBoardWorker(i,j)));
                    }
                }
            }
        }
    }

    class BoardCellMouseListener implements MouseListener {
        int i, j, height;
        JLabel lowerLabel;
        Worker w;
        BoardCellWorker[][] bcw;

        public BoardCellMouseListener(BoardCellWorker[][] bcw, JLabel lowerLabel, int i, int j, int height, Worker w){
            this.bcw=bcw;
            this.lowerLabel=lowerLabel;
            this.i=i;
            this.j=j;
            this.height=height;
            this.w=w;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            bcw[i][j] = new BoardCellWorker(i, j);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            bcw[i][j] = new BoardCellWorker(i, j);
        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if(w!=null){
                lowerLabel.setText("Coordinate (x: " + j + ", coordinate y: "+ i + "), height: " + height + ", worker: present");
            }else{
                lowerLabel.setText("Coordinate (x: " + j + ", coordinate y: "+ i + "), height: " + height + " worker: absent");
            }

        }

        @Override
        public void mouseExited(MouseEvent e) {
            lowerLabel.setText("");
        }
    }


    private Integer findIndex(ArrayList<Coordinates> validPositions, Coordinates chosenCoordinates){
        int index=0;
        System.out.println(chosenCoordinates);
        for(Coordinates c:validPositions){
            if((c.getX() == chosenCoordinates.getX()) && (c.getY() == chosenCoordinates.getY())){
                return index;
            }
            index++;
        }
        //Se qualcosa è andato storto il controller lato server riceverà un -1
        return  -1;
    }

    private int understandPlayerIDFromColor(String color){
        int ID;

        switch (color){
            case "yellow":
                ID=1;
                break;
            case "white":
                ID=2;
                break;
            case "purple":
                ID=3;
                break;
            default:
                System.out.println("Non ho capito questo giocatore che playerID ha");
                ID=-1;
        }

        return ID;
    }

    private void requestDate(){
        DateNextButtonCustomActionListener dateListener=new DateNextButtonCustomActionListener();
        GUI.getDateNextButton().addActionListener(dateListener);
        while (dateListener.isGoForward()==false){
            System.out.println("Attendo che il giocatore inserisca la sua data di nascita");
        }
    }

    private void closeGodsWorkers(){
        for(GodsWorker w: gw){
            w=null;
        }
    }

    private void closeCellsWorkers(){
        for(int i=0;i<Board.DIM;i++){
            for (int j=0;j<Board.DIM;j++){
                bcw[i][j]=null;
            }
        }

    }

    private boolean checkIfGodisAlreadyChosen(ArrayList<String> chosenGods, String chosenGod){
        for (String god: chosenGods){
            if(god.equals(chosenGod)){
                return true;
            }
        }

        return false;
    }

    private void selectNewCell(ArrayList<Coordinates> validPositions){
        paintBoardCell(validPositions);
        SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
        currentCoordinate = new Coordinates(-1,-1);
        bcw = new BoardCellWorker[Board.DIM][Board.DIM];
        createBoardCellMouseListener(bcw, GUI.getBoard(), validPositions);
        while(currentCoordinate.getY()==-1 && currentCoordinate.getX()==-1){
            System.out.println("Attendo che il player scelga la cella");
        }
        turnModelBoardintoGUIBoard(b);
        SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
        GUI.getLowerLabel().setText("Wait...");
        closeCellsWorkers();
        if(findIndex(validPositions, currentCoordinate)!=-1){
            currentCoordinate = new Coordinates(-1,-1);
        }
    }

    class UndoCustomListener implements ActionListener{
        private int choose;

        public UndoCustomListener(int choose){
            this.choose=choose;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            UndoWorker uw= new UndoWorker(choose);
        }

    }

}
