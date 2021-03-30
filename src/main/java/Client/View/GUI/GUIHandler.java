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
    private int playersNumber;
    private Object playersNumberLock;
    private int playerID;
    private Object lock;
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
    private boolean undoBool;

    public GUIHandler(GUI GUI,String ip){
        this.GUI = GUI;
        this.ip = ip;

        //Initial settings
        playersNumber=0;
        myUsername=null;
       //playersNumberLock=new Object();
        playerID=0;
        godsCounter = 0;
        myGod = null;
        checkSendCells=false;
        checkUpdate=false;
        undoBool=false;
        lock = new Object();
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


   private synchronized void checkEvent(ClientNetworkHandler cnh) {
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
                    playersNumber=-1;
                    GUI.destroyWaitingLabel();
                    GUI.buildNumberOfPlayersWindow();
                    SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
                    CustomNumberOfPlayersListener twoListener = new CustomNumberOfPlayersListener(this,2);
                    GUI.getTwo().addActionListener(twoListener);
                    CustomNumberOfPlayersListener threeListener = new CustomNumberOfPlayersListener(this,3);
                    GUI.getThree().addActionListener(threeListener);
                    synchronized (this){
                        while(playersNumber==-1) {
                            GUI.getLowerLabel().setText("Wait...");
                                try {
                                    wait();
                                } catch (InterruptedException e) {
                                    System.out.println("Errore nella wait del scelta del numero dei giocatori");
                                }
                            }
                    }
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
                    LoginNextButtonCustomActionListener usernameListener=new LoginNextButtonCustomActionListener(this, GUI.getUsernameTextField());
                    GUI.getLoginNextButton().addActionListener(usernameListener);
                    synchronized (this){
                        while(myUsername==null){
                            try{
                                wait();
                            }catch (InterruptedException e){
                                System.out.println("Errore nella wait della scelta dello username del giocatore corrente");
                            }
                        }
                    }

                    GUI.getLowerLabel().setText("Wait...");
                    buildEvent(cnh, myUsername, VCEvent.Event.username_request);
                    break;
                case wrong_username:
                    GUI.getLowerLabel().setText("This username has been already chosen. Insert a new one, please");
                    SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
                    LoginNextButtonCustomActionListener wrongUsernameListener=new LoginNextButtonCustomActionListener(this, GUI.getUsernameTextField());
                    GUI.getLoginNextButton().addActionListener(wrongUsernameListener);
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
                    //GUI.updateGodBar("Your opponent's card is: ", currentPlayerInformation.get(1).toUpperCase());
                    SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
                    break;
                case update:
                    if(playerID!=1 && checkUpdate==false){
                        GUI.getUpperLabel().setText("");
                        GUI.getLowerLabel().setText("");
                        GUI.destroyGodsWindow(godsSize);
                        GUI.buildMainWindow();
                        //GUI.updateGodBar("Your card is: ", myGod.toUpperCase());
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
                    closeCellsWorkers();
                    int controlAsk=findIndex(positionWorkers, currentCoordinate);
                    while(controlAsk==-1){
                        GUI.getLowerLabel().setText("Invalid selection! Please select another cell");
                        controlAsk=selectNewCell(positionWorkers);
                    }
                    currentCoordinate = new Coordinates(-1,-1);
                    GUI.getLowerLabel().setText("Wait...");
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
                    //GUI.updateGodBar("Your card is: ", myGod.toUpperCase());
                    SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
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
                    if(!undoBool)  {
                        GUI.buildUndoJDialog();
                        undoBool=true;
                    }
                    choose=-1;
                    GUI.getUndoDialog().setVisible(true);
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
                    ImageIcon endGameIcon = GUI.createIcon("Lost.jpg");
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
                    ImageIcon winIcon = GUI.createIcon("Win.jpg");
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
                    ImageIcon endGameForYouIcon = GUI.createIcon("Lost.jpg");
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
                    ImageIcon godPowerActivationIcon = GUI.createIcon("QuestionSign.jpg");
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
                        GUI.buildJDialogForFirstPlayer(myGod.toUpperCase());
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
                    ImageIcon disconnectionIcon = GUI.createIcon("WarningSign.jpg");

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
                Color worker = null;
                if (b.getBoardWorker(i, j) != null) {
                    worker = (b.getBoardWorker(i, j)).getColor();
                }
                switch (b.getBoardHeight(i, j)) {
                    case 0:
                        if (b.getBoardWorker(i, j) != null) {
                            if (worker.equals(Model.Color.ANSI_YELLOW)) {
                                updateSantoriniButton(i,j, "/BLUE.jpg");
                            } else if (worker.equals(Model.Color.ANSI_WHITE)) {
                                updateSantoriniButton(i,j, "/WHITE.jpg");
                            } else if (worker.equals(Model.Color.ANSI_PURPLE)) {
                                updateSantoriniButton(i,j, "/BROWN.jpg");
                            }
                        }else if(b.getBoardWorker(i, j) == null){
                            updateSantoriniButton(i,j, "/BoardCell.jpg");
                        }
                        break;
                    case 1:
                        if (b.getBoardWorker(i, j) != null) {
                            if (worker.equals(Color.ANSI_YELLOW)) {
                                updateSantoriniButton(i,j, "/BLUEUNO.jpg");
                            } else if (worker.equals(Color.ANSI_WHITE)) {
                                updateSantoriniButton(i,j, "/WHITEUNO.jpg");
                            } else if (worker.equals(Color.ANSI_PURPLE)) {
                                updateSantoriniButton(i,j, "/BROWNUNO.jpg");
                             }
                        } else if (b.getBoardWorker(i, j) == null) {
                            updateSantoriniButton(i,j, "/UNO.jpg");
                        }
                        break;
                    case 2:
                        if (b.getBoardWorker(i, j) != null) {
                            if (worker.equals(Color.ANSI_YELLOW)) {
                                updateSantoriniButton(i,j, "/BLUEDUE.jpg");
                            } else if (worker.equals(Color.ANSI_WHITE)) {
                                updateSantoriniButton(i,j, "/WHITEDUE.jpg");
                            } else if (worker.equals(Color.ANSI_PURPLE)) {
                                updateSantoriniButton(i,j, "/BROWNDUE.jpg");
                            }
                        } else if (b.getBoardWorker(i, j) == null) {
                            updateSantoriniButton(i,j, "/DUE.jpg");
                        }
                        break;
                    case 3:
                        if (b.getBoardWorker(i, j) != null) {
                            if (worker.equals(Color.ANSI_YELLOW)) {
                                updateSantoriniButton(i,j, "/BLUETRE.jpg");
                            } else if (worker.equals(Color.ANSI_WHITE)) {
                                updateSantoriniButton(i,j, "/WHITETRE.jpg");
                            } else if (worker.equals(Color.ANSI_PURPLE)) {
                                updateSantoriniButton(i,j, "/BROWNTRE.jpg");
                            }
                        } else if (b.getBoardWorker(i, j) == null) {
                            updateSantoriniButton(i,j, "/TRE.jpg");
                        }
                        break;
                    case 4:
                        updateSantoriniButton(i,j, "/QUATTRO.jpg");
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
        private GUIHandler gh;
        private JTextField usernameTextField;

        public LoginNextButtonCustomActionListener(GUIHandler gh, JTextField usernameTextField){
            this.gh=gh;
            this.usernameTextField=usernameTextField;
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            ThreadUsername tu = new ThreadUsername(gh, usernameTextField);
            Thread t= new Thread(tu);
            t.start();
        }
    }



    class CustomNumberOfPlayersListener implements ActionListener{
        private GUIHandler gh;
        private int chosenNumberOfPlayers;

        public CustomNumberOfPlayersListener(GUIHandler gh, int chosenNumberOfPlayers){
            this.gh=gh;
            this.chosenNumberOfPlayers=chosenNumberOfPlayers;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ThreadOptionButton tob = new ThreadOptionButton(gh, chosenNumberOfPlayers);
            Thread t = new Thread(tob);
            t.start();
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
        private int playersNumber;

        public SentGodsMouseListener(String chosenGod, JLabel lowerLabel, int playersNumber) {
            this.chosenGod = chosenGod;
            this.lowerLabel=lowerLabel;
            this.playersNumber=playersNumber;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(checkIfGodisAlreadyChosen(chosenGods, chosenGod)==true){
                lowerLabel.setText("Pay attention, you can't choose again this god. Select another one, please");
            }else {
                gw[godsCounter] = new GodsWorker(chosenGod, GUI.getUpperLabel(), this.playersNumber);
                godsCounter++;
            }
        }


        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {



        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if(chosenGods.size()<playersNumber){
                try {
                    GUI.readGodsPower(GUI.getLowerLabel(), chosenGod);
                }catch (IOException ex){
                    GUI.getLowerLabel().setText("Unable to read god's power");
                }
            }

        }

        @Override
        public void mouseExited(MouseEvent e) {
            if(chosenGods.size()<playersNumber) {
                lowerLabel.setText("");
            }
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
        }



        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if(myGod==null){
                try {
                    GUI.readGodsPower(GUI.getLowerLabel(), chosenGod);
                }catch (IOException ex){
                    GUI.getLowerLabel().setText("Unable to read god's power");
                }
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if(myGod==null){
                lowerLabel.setText("");
            }
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
        closeCellsWorkers();
        int control=findIndex(validPositions,currentCoordinate);
        while(control==-1){
            GUI.getLowerLabel().setText("Invalid move! Please select another cell");
            control=selectNewCell(validPositions);
        }
        currentCoordinate =  new Coordinates(-1,-1);
        GUI.getLowerLabel().setText("Wait...");
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
                                        updateSantoriniButton(i, j, "/RedBlue.jpg");
                                    } else if (worker.equals(Model.Color.ANSI_WHITE)) {
                                        updateSantoriniButton(i, j, "/RedWhite.jpg");
                                    } else if (worker.equals(Model.Color.ANSI_PURPLE)) {
                                        updateSantoriniButton(i, j, "/RedBrown.jpg");
                                    }
                                } else if (b.getBoardWorker(i, j) == null) {
                                    updateSantoriniButton(i, j, "/Red0.jpg");
                                }
                                break;
                            case 1:
                                if (b.getBoardWorker(i, j) != null) {
                                    if (worker.equals(Color.ANSI_YELLOW)) {
                                        updateSantoriniButton(i, j, "/RedBlue1.jpg");
                                    } else if (worker.equals(Color.ANSI_WHITE)) {
                                        updateSantoriniButton(i, j, "/RedWhite1.jpg");
                                    } else if (worker.equals(Color.ANSI_PURPLE)) {
                                        updateSantoriniButton(i, j, "/RedBrown1.jpg");
                                    }
                                } else if (b.getBoardWorker(i, j) == null) {
                                    updateSantoriniButton(i, j, "/Red1.jpg");
                                }
                                break;
                            case 2:
                                if (b.getBoardWorker(i, j) != null) {
                                    if (worker.equals(Color.ANSI_YELLOW)) {
                                        updateSantoriniButton(i, j, "/RedBlue2.jpg");
                                    } else if (worker.equals(Color.ANSI_WHITE)) {
                                        updateSantoriniButton(i, j, "/RedWhite2.jpg");
                                    } else if (worker.equals(Color.ANSI_PURPLE)) {
                                        updateSantoriniButton(i, j, "/RedBrown2.jpg");
                                    }
                                } else if (b.getBoardWorker(i, j) == null) {
                                    updateSantoriniButton(i, j, "/Red2.jpg");
                                }
                                break;
                            case 3:
                                if (b.getBoardWorker(i, j) != null) {
                                    if (worker.equals(Color.ANSI_YELLOW)) {
                                        updateSantoriniButton(i, j, "/RedBlue3.jpg");
                                    } else if (worker.equals(Color.ANSI_WHITE)) {
                                        updateSantoriniButton(i, j, "/RedWhite3.jpg");
                                    } else if (worker.equals(Color.ANSI_PURPLE)) {
                                        updateSantoriniButton(i, j, "/RedBrown3.jpg");
                                    }
                                } else if (b.getBoardWorker(i, j) == null) {
                                    updateSantoriniButton(i, j, "/Red3.jpg");
                                }
                                break;
                            case 4:
                                updateSantoriniButton(i, j, "/Red4.jpg");
                                break;
                            default:
                                break;
                        }
                    }
    }

    private void createGodsListener(int size, ArrayList<String> sentGods, JButton[] godsButton, JLabel lowerLabel){
        for (int i=0;i<size;i++){
            System.out.println("Aggiungo al bottone il listener");
            godsButton[i].addMouseListener(new SentGodsMouseListener(sentGods.get(i), lowerLabel, playersNumber));
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
            if(currentCoordinate.getX()==-1 && currentCoordinate.getY()==-1) {
                lowerLabel.setText("Coordinate (x: " + j + ", coordinate y: " + i + "), height: " + height);
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if(currentCoordinate.getX()==-1 && currentCoordinate.getY()==-1){
                lowerLabel.setText("");
            }
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

    private int selectNewCell(ArrayList<Coordinates> validPositions){
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
        closeCellsWorkers();
        return findIndex(validPositions,currentCoordinate);
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

    public void setPlayersNumber(int playersNumber){
        synchronized (this){
            this.playersNumber=playersNumber;
            notifyAll();
        }
    }

    public void setUsername(JTextField usernameTextField){
        synchronized (this){
            myUsername=new String(usernameTextField.getText());
            notifyAll();
        }

    }

    public void setDate(){
        synchronized (this){
            notifyAll();
        }
    }

}