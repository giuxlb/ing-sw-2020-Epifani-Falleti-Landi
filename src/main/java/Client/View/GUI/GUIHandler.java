package Client.View.GUI;
import javax.swing.*;

import Client.Controller.Controller;
import Client.View.ClientNetworkHandler;
import Client.View.Data;
import Controller.Coordinates;
import Controller.Network.VCEvent;
import Model.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import static java.awt.Color.red;


public class GUIHandler {
    //GUI elements to handle
    private GUI GUI;

    //To connect
    private ClientNetworkHandler cnh;
    private Thread game;
    private boolean updateView;

    //Support elements
    private String ipAddress;
    protected static int playersNumber;
    private int playerID;
    protected static boolean ready;
    private String myUsername;
    private Data myDate;
    private String myColor;
    private ArrayList<String> sentGods;
    private int godsSize;
    protected static ArrayList<String> chosenGods;
    protected static String myGod;
    private boolean checkSendCells;
    private boolean checkUpdate;
    private Board b;
    private String movingPhase = "go on";
    private String buildingPhase = "build on";
    private String removePhase = "remove";
    protected static Coordinates previousCoordinate;
    protected static Coordinates currentCoordinate;
    private BoardCellWorker[][] bcw;

    public GUIHandler(GUI GUI){
        this.GUI = GUI;

        //Initial settings
        playersNumber=0;
        playerID=0;
        checkSendCells=false;
        checkUpdate=false;
        bcw= new BoardCellWorker[Board.DIM][Board.DIM];
        previousCoordinate = new Coordinates(0,-1);
        currentCoordinate = new Coordinates(-1,0);

    }

    public void launchConnection(){
        //Creazione della socket del client
        cnh = new ClientNetworkHandler(this);

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


   private void checkEvent(ClientNetworkHandler cnh)  {
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
                    GUI.getUpperLabel().setText("Insert first month (mm), then day (dd) and finally year (yy) of your own date of birth. Press Next to send");
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

                    break;
                case not_your_turn:
                    Object objectCurrentPlayerInformation = evento.getBox();
                    ArrayList<String> currentPlayerInformation= (ArrayList<String>) objectCurrentPlayerInformation;
                    GUI.getUpperLabel().setText(currentPlayerInformation.get(0) + " is playing with god " + currentPlayerInformation.get(1));
                    GUI.updateGodBar(currentPlayerInformation.get(1));
                    break;
                case update:
                    if(playerID!=1 && checkUpdate==false){
                        GUI.getUpperLabel().setText("");
                        GUI.getLowerLabel().setText("");
                        GUI.destroyGodsWindow(godsSize);
                        GUI.buildMainWindow();
                        GUI.updateGodBar(myGod);
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
                    createBoardCellMouseListener(GUI.getBoard(), positionWorkers);
                    while(ready==false || (previousCoordinate.getX()==currentCoordinate.getX() && previousCoordinate.getY()==currentCoordinate.getY())){
                        System.out.println("Attendo che il player scelga dove posizionarsi");
                    }
                    GUIHandler.ready=false;
                    previousCoordinate.setCoordinates(currentCoordinate.getX(),currentCoordinate.getY());
                    unpaintBoardCell(positionWorkers);
                    SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
                    GUI.getLowerLabel().setText("Wait...");
                    closeCellsWorkers();
                    buildEvent(cnh,findIndex(positionWorkers, currentCoordinate), VCEvent.Event.ask_for_worker);
                    break;
                case send_cells_move:
                    if(playerID==1 && checkSendCells==false){
                            GUI.getUpperLabel().setText("");
                            GUI.getLowerLabel().setText("");
                            GUI.destroyGodsWindow(godsSize);
                            GUI.buildMainWindow();
                            GUI.updateGodBar(myGod);
                            SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
                            checkSendCells=true;
                            this.b = new Board();
                            turnModelBoardintoGUIBoard(b);
                    }
                    sendCells(movingPhase, cnh, VCEvent.Event.send_cells_move,evento);
                    break;
                case send_cells_build:
                    sendCells(buildingPhase, cnh, VCEvent.Event.send_cells_build,evento);
                    break;
                case send_cells_remove:
                    sendCells(removePhase,cnh, VCEvent.Event.send_cells_remove,evento);
                    break;
                /*case undo_request:/*
                    GUI.getMessageArea().setText("You can undo your move only in about 5 seconds. If you want to confirm press the button aside, please");
                    long start = System.currentTimeMillis();
                    while (true){
                        try {
                            if (!(((System.currentTimeMillis()-start) < 5000) && !reader.ready())) break;
                        } catch (IOException ex) { }
                    }
                    try {
                        if (reader.ready())
                            choose = Integer.parseInt(reader.readLine());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }


                    if (choose == 1) {
                        firstTimeHeMoves = 1;
                        this.b.deepCopy(copy);
                    }




                    buildEvent(cnh,choose, VCEvent.Event.undo_request);
                    break;*/
                case you_lost:
                    Object objectWinner = evento.getBox();
                    String winner = (String) objectWinner;
                    ImageIcon endGameIcon = GUI.createIcon("lost.png");
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
                    ImageIcon winIcon = GUI.createIcon("win.png");
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
                    ImageIcon endGameForYouIcon = GUI.createIcon("lost.png");
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
                    GUI.destroyDateWindow();
                    GUI.buildGodsWindow(sentGods);
                    SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
                    chosenGods = new ArrayList<>(2);
                    createGodsListener(godsSize, sentGods, chosenGods, GUI.getImgGodsButtons(), GUI.getLowerLabel());
                    while(playersNumber!=chosenGods.size()){
                        System.out.println("Sto aspettando che il primo player scelga le divinità");
                    }
                    GUI.getLowerLabel().setText("Wait...");
                    buildEvent(cnh, chosenGods, VCEvent.Event.send_all_cards);
                    break;
                case send_chosen_cards:
                    GUI.getUpperLabel().setText("Select the god who prefer");
                    GUI.getLowerLabel().setText("");
                    Object objectSentGods = evento.getBox();
                    sentGods= (ArrayList<String>)  objectSentGods;
                    godsSize = sentGods.size();
                    if(godsSize==1){
                        GUIHandler.myGod=sentGods.get(0);
                        GUI.buildJDialogForFirstPlayer(myGod);
                        buildEvent(cnh, sentGods.get(0), VCEvent.Event.send_chosen_cards);
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

    //Da aggiornare una volta completate le risorse grafiche
    private void turnModelBoardintoGUIBoard(Board b){
        for(int i=0;i<Board.DIM;i++){
            for(int j=0;j<Board.DIM;j++){
                if (b.getBoardWorker(i,j)==null){
                    System.out.println(GUI.getBoard());
                    GUI.getBoard()[i][j].setText("Worker: null   " + "Height: " + b.getBoardHeight(i,j));
                }else{
                    GUI.getBoard()[i][j].setText(b.getBoardWorker(i,j).getColor() + "Worker: w   " + "Height: " + b.getBoardHeight(i,j));
                }
            }
        }
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

    private void createGodsListener(int size, ArrayList<String> sentGods, ArrayList<String> chosenGods, JButton[] godsButton, JLabel lowerLabel){
        for (int i=0;i<size;i++){
            System.out.println("Aggiungo al bottone il listener");
            godsButton[i].addMouseListener(new sentGodsMouseListener(i, sentGods, chosenGods, lowerLabel));
        }
    }

    private void createGodListener(int size, ArrayList<String> sentGods, JButton[] godsButton, JLabel lowerLabel){
        for (int i=0;i<size;i++){
            System.out.println("Aggiungo al bottone il listener");
            godsButton[i].addMouseListener(new sentGodMouseListener(i, sentGods, lowerLabel));
        }
    }


    class sentGodsMouseListener implements MouseListener {
        private int index;
        private ArrayList<String> gods;
        private ArrayList<String> chosenGods;
        private JLabel lowerLabel;

        public sentGodsMouseListener(int index, ArrayList<String> gods, ArrayList<String> chosenGods, JLabel lowerLabel) {
            this.index = index;
            this.gods = gods;
            this.chosenGods=chosenGods;
            this.lowerLabel=lowerLabel;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(checkIfGodisAlreadyChosen(chosenGods, gods.get(index))==true){
                lowerLabel.setText("Pay attention, you can't choose again this god. Select another one, please");
            }else{
                GodsWorker gw = new GodsWorker(gods.get(index));
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if(checkIfGodisAlreadyChosen(chosenGods, gods.get(index))==true){
                lowerLabel.setText("Pay attention, you can't choose again this god. Select another one, please");
            }else{
                GodsWorker gw = new GodsWorker(gods.get(index));
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {



        }

        @Override
        public void mouseEntered(MouseEvent e) {
            try {
                FileInputStream fis = null;

                fis = new FileInputStream("./resources/gods/" + gods.get(index) + ".txt");
                ObjectInputStream ois = new ObjectInputStream(fis);
                lowerLabel.setText((String ) ois.readObject());
                ois.close();
                fis.close();
            } catch (ClassNotFoundException| IOException ex) {
                lowerLabel.setText("Unable to load god's power");
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            lowerLabel.setText("");
        }
    }

    class sentGodMouseListener implements MouseListener{
        private int index;
        private ArrayList<String> gods;
        private JLabel lowerLabel;

        public sentGodMouseListener(int index, ArrayList<String> gods, JLabel lowerLabel) {
            this.index=index;
            this.gods=gods;
            this.lowerLabel=lowerLabel;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            GodWorker gw= new GodWorker(gods.get(index));
        }

        @Override
        public void mousePressed(MouseEvent e) {
            GodWorker gw= new GodWorker(gods.get(index));
        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {
            try {
                FileInputStream fis = null;

                fis = new FileInputStream("./resources/gods/" + gods.get(index) + ".txt");
                ObjectInputStream ois = new ObjectInputStream(fis);
                lowerLabel.setText((String ) ois.readObject());
                ois.close();
                fis.close();
            } catch (ClassNotFoundException| IOException ex) {
                lowerLabel.setText("Unable to load god's power");
                ex.printStackTrace();
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            lowerLabel.setText("");
        }
    }

    public void sendCells(String phase, ClientNetworkHandler cnh, VCEvent.Event command, VCEvent evento){
        GUIHandler.ready=false;
        GUI.getUpperLabel().setText("Select the red cell where you want to " + phase);
        GUI.getLowerLabel().setText("");
        Object objectValidPositions = evento.getBox();
        ArrayList<Coordinates> validPositions = (ArrayList<Coordinates>)objectValidPositions;
        paintBoardCell(validPositions);
        SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
        createBoardCellMouseListener(GUI.getBoard(), validPositions);
        while(ready==false || (previousCoordinate.getX()==currentCoordinate.getX() && previousCoordinate.getY()==currentCoordinate.getY())){
            System.out.println("Attendo che il player scelga dove posizionarsi");
        }
        GUIHandler.ready=false;
        previousCoordinate.setCoordinates(currentCoordinate.getX(),currentCoordinate.getY());
        unpaintBoardCell(validPositions);
        SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
        GUI.getLowerLabel().setText("Wait...");
        closeCellsWorkers();
        int control=findIndex(validPositions,currentCoordinate);
        while(control==-1){
            GUI.getLowerLabel().setText("Invalid move! Please select another cell");
            selectNewCell(validPositions);
            control=findIndex(validPositions, currentCoordinate);
        }
        buildEvent(cnh,control,command);

    }

    private void paintBoardCell(ArrayList<Coordinates> validPositions){
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                for (Coordinates c: validPositions){
                    if(c.getX()==i && c.getY()==j){
                        GUI.getBoard()[i][j].setBackground(java.awt.Color.red);
                    }
                }
            }
        }
    }

    private void unpaintBoardCell(ArrayList<Coordinates> validPositions){
        for (int i = 0; i < Board.DIM; i++) {
            for (int j = 0; j < Board.DIM; j++) {
                for (Coordinates c: validPositions){
                    if(c.getX()==i && c.getY()==j){
                        GUI.getBoard()[i][j].setBackground(null);
                    }
                }
            }
        }
    }

    private void createBoardCellMouseListener(JButton[][] GUIBoard, ArrayList<Coordinates> validPositions){
        for (int i=0;i<Board.DIM;i++){
            for(int j=0;j<Board.DIM; j++){
                for (Coordinates c: validPositions){
                    if(c.getX()==i && c.getY()==j){
                        GUIBoard[i][j].addMouseListener(new BoardCellMouseListener(GUI.getLowerLabel(), i, j, b.getBoardHeight(i,j), b.getBoardWorker(i,j), validPositions));
                    }
                }
            }
        }
    }

    class BoardCellMouseListener implements MouseListener {
        int i, j, height;
        JLabel lowerLabel;
        Worker w;
        ArrayList<Coordinates> validPosition;

        public BoardCellMouseListener(JLabel lowerLabel, int i, int j, int height, Worker w, ArrayList<Coordinates> validPosition){
            this.lowerLabel=lowerLabel;
            this.i=i;
            this.j=j;
            this.height=height;
            this.w=w;
            this.validPosition=validPosition;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(findIndex(validPosition, new Coordinates(i,j))!=-1){
                bcw[i][j] = new BoardCellWorker(i, j);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if(findIndex(validPosition, new Coordinates(i,j))!=-1){
                bcw[i][j] = new BoardCellWorker(i, j);
            }
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
        ready=false;
        paintBoardCell(validPositions);
        SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
        createBoardCellMouseListener(GUI.getBoard(), validPositions);
        while(ready==false || (previousCoordinate.getX()==currentCoordinate.getX() && previousCoordinate.getY()==currentCoordinate.getY())){
            System.out.println("Attendo che il player scelga dove posizionarsi");
        }
        GUIHandler.ready=false;
        previousCoordinate.setCoordinates(currentCoordinate.getX(),currentCoordinate.getY());
        unpaintBoardCell(validPositions);
        SwingUtilities.updateComponentTreeUI(GUI.getMainFrame());
        GUI.getLowerLabel().setText("Wait...");
        closeCellsWorkers();
    }

}
