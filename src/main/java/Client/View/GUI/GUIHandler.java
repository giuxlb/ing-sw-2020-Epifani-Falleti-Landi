package Client.View.GUI;
import javax.swing.*;

import Client.View.ClientNetworkHandler;
import Client.View.Data;
import Controller.Coordinates;
import Controller.Network.VCEvent;
import Model.Board;
import Model.Color;
import Model.SocketBoardCell;
import Model.Worker;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Scanner;


public class GUIHandler {
    //GUI elements to handle
    private GUI GUI;

    //To connect
    private ClientNetworkHandler cnh;
    private Thread game;
    private boolean updateView;

    //Support elements
    protected static int playersNumber;
    protected static boolean ready;
    private String ipAddress;
    private String myUsername;
    private Data myDate;
    private Board b;
    private ArrayList<String> sentGods;
    protected static ArrayList<String> chosenGods;
    protected static String myGod;
    protected static Coordinates currentCoordinate;
    private BoardCellWorker[][] bcw;

    public GUIHandler(GUI GUI){
        this.GUI = GUI;

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
        // Board copy = new Board();
        // int firstTimeHeMoves = 1;
        while(endGame==false){
            update();
            VCEvent evento = cnh.getFromServer();
            cnh.readByView();
            switch (evento.getCommand()){
                /*case send_color:
                    Object objcectColorName = evento.getBox();
                    if(objcectColorName instanceof String){
                        this.myColor = (String) objcectColorName;
                        System.out.println("I tuoi worker saranno di colore " + myColor);
                    }else{
                        System.out.println("Errore! La stringa che dovrebbe rappresentare in nome del mio colore è arrivata corrotta");
                    }
                    break;*/
                case setup_request:
                    ready=false;
                    GUI.getMainFrame().add(GUI.getNumberOfPlayersWindowManager());
                    ButtonTwoCustomActionListener twoListener = new ButtonTwoCustomActionListener();
                    GUI.getTwo().addActionListener(twoListener);
                    ButtonThreeCustomActionListener threeListener = new ButtonThreeCustomActionListener();
                    GUI.getThree().addActionListener(threeListener);
                    System.out.println("Sto per entrare nel while");
                    while(GUIHandler.ready==false){
                        System.out.println("Attendo scelta...");
                    }
                    buildEvent(cnh, playersNumber, VCEvent.Event.setup_request);
                    break;
                case username_request :
                    GUI.getMainFrame().remove(GUI.getNumberOfPlayersWindowManager());
                    GUI.getMainFrame().add(GUI.getLoginWindowManager());
                    LoginNextButtonCustomActionListener usernameListener=new LoginNextButtonCustomActionListener();
                    GUI.getLoginNextButton().addActionListener(usernameListener);
                    while (usernameListener.getGoForward()==false){
                    }
                    buildEvent(cnh, myUsername, VCEvent.Event.username_request);
                    break;
                case wrong_username:
                    GUI.getLoginMessageArea().setText(Color.ANSI_RED + "This username has been already chosen. Insert a new one, please");
                    LoginNextButtonCustomActionListener wrongUsernameListener=new LoginNextButtonCustomActionListener();
                    GUI.getLoginNextButton().addActionListener(wrongUsernameListener);
                    while (wrongUsernameListener.getGoForward()==false){
                        try{
                            Thread.sleep(5000);
                        }catch (InterruptedException e){

                        }
                    }
                    buildEvent(cnh, myUsername, VCEvent.Event.wrong_username);
                    break;
                case date_request:
                    GUI.getMainFrame().remove(GUI.getLoginWindowManager());
                    GUI.getMainFrame().add(GUI.getDateWindowManager());
                    DateNextButtonCustomActionListener dateListener=new DateNextButtonCustomActionListener();
                    GUI.getDateNextButton().addActionListener(dateListener);
                    while (dateListener.isGoForward()==false){
                    }
                    buildEvent(cnh, myDate, VCEvent.Event.date_request);
                    break;
                case not_your_turn:
                    Object objectCurrentPlayerInformation = evento.getBox();
                    ArrayList<String> currentPlayerInformation= (ArrayList<String>) objectCurrentPlayerInformation;
                    GUI.getMessageArea().setText(currentPlayerInformation.get(0) + " is playing with god " + currentPlayerInformation.get(1));
                    break;
                case update:
                    Object objectBoardCell = evento.getBox();
                    ArrayList<SocketBoardCell> socketBoardCell = (ArrayList<SocketBoardCell>) objectBoardCell;
                    recreateBoardfromSocketBoardCell(socketBoardCell);
                    turnModelBoardintoGUIBoard(b);
                    break;/*
                case ask_for_worker:
                    //ATTENZIONE: Una parte di ask_for_worker contiene del codice duplicato con sendCells(). CORREGGERE!!!
                    Object objectChoices = evento.getBox();
                    ArrayList<Coordinates> positionWorkers = (ArrayList<Coordinates>) objectChoices;
                    System.out.println("Puoi muovere i seguenti worker: ");
                    for (Coordinates c: positionWorkers) {
                        System.out.println("Worker in posizione "+ c.toString());
                    }
                    System.out.println("Quale worker vuoi muovere?");
                    int x=chooseCoordinate(xPosition);
                    s.nextLine();
                    int y=chooseCoordinate(yPosition);
                    s.nextLine();
                    buildEvent(cnh,findIndex(positionWorkers,chosenCoordinates), VCEvent.Event.ask_for_worker);
                    break;
                case send_cells_move:
                    sendCells(movingPhase, cnh, VCEvent.Event.send_cells_move,evento);
                    break;
                case send_cells_build:
                    sendCells(buildingPhase, cnh, VCEvent.Event.send_cells_build,evento);
                    break;
                case send_cells_remove:
                    sendCells(removePhase,cnh, VCEvent.Event.send_cells_remove,evento);
                    break;*/
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
                    GUI.getMessageArea().setText("Partita conclusa il vincitore è " + winner);
                    buildEvent(cnh,"ho perso", VCEvent.Event.you_lost);
                    endGame=true;
                    break;
                case you_won:
                    GUI.getMessageArea().setText("Congratulazioni, sei il vincitore!");
                    buildEvent(cnh,"ho vinto", VCEvent.Event.you_won);
                    endGame=true;
                    break;
                case game_ended_foryou:
                    GUI.getMessageArea().setText("Mi spiace, non puoi più muoverti con nessuno dei tuoi worker");
                    break;
                case number_of_players:
                    playersNumber = (Integer)evento.getBox();
                    break;
                case ask_for_divinity_activation:
                    Object[] choices = {"Yes", "No"};
                    int choice = JOptionPane.showOptionDialog(GUI.getMainFrame(),
                                    "Do you want to use " + myGod + "'s power ?",
                                    "Card activation",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.QUESTION_MESSAGE,
                                    null, //Ricordati di mettere l'icona
                                    choices,
                                    choices[1]);

                    buildEvent(cnh, turnChoiceIntoCorrectOutput(choice), VCEvent.Event.ask_for_divinity_activation);
                case send_all_cards:
                    Object objectGods = evento.getBox();
                    sentGods= (ArrayList<String>)  objectGods;
                    GUI.buildGodsWindow(sentGods);
                    GUI.getMainFrame().remove(GUI.getDateWindowManager());
                    GUI.getMainFrame().add(GUI.getGodsWindowManager());

                    break;
                case send_chosen_cards:
                    Object objectSentGods = evento.getBox();
                    sentGods= (ArrayList<String>)  objectSentGods;
                    if(sentGods.size()==1){
                        GUIHandler.myGod=sentGods.get(0);
                        buildEvent(cnh, sentGods.get(0), VCEvent.Event.send_chosen_cards);
                    }else{
                        GUI.buildGodsWindow(sentGods);
                        GUI.getMainFrame().remove(GUI.getDateWindowManager());
                        GUI.getMainFrame().add(GUI.getGodsWindowManager());
                    }
                    break;
                case player_disconnected_game_ended:
                    Object objectPlayerDisconnected= evento.getBox();
                    String playerDisconnected = (String) objectPlayerDisconnected;
                    //o sarebbe meglio una finestra di dialogo? Da vedere quando testerò la GUI
                    GUI.getMessageArea().setText("Ops! Il giocatore " + playerDisconnected + " si è disconnesso, purtroppo la partita terminerà ora");
                    buildEvent(cnh, "ho ricevuto la disconnessione di un client", VCEvent.Event.player_disconnected_game_ended);
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
                    GUI.getBoard()[i][j].setText("Worker: null\n" + "Height: " + b.getBoardHeight(i,j));
                }else{
                    GUI.getBoard()[i][j].setText(b.getBoardWorker(i,j).getColor() + "Worker: w\n" + "Height: " + b.getBoardHeight(i,j));
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



    class ButtonTwoCustomActionListener implements ActionListener{

        public ButtonTwoCustomActionListener(){

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ChooseNumPlayerWorker button2= new ChooseNumPlayerWorker(2);
        }
    }

    class ButtonThreeCustomActionListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            ChooseNumPlayerWorker button3= new ChooseNumPlayerWorker(3);
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

}
