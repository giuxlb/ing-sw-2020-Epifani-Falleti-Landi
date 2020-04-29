//Manca ancora tutta la logica delle carte
package Client.View;

import Controller.Network.VCEvent;
import Model.Board;
import Model.Player;
import Client.Controller.Controller;
import Controller.Coordinates;
import Controller.Network.VCEvent;
import Model.Color;
import Model.Worker;
import


import java.util.ArrayList;
import java.util.Scanner;

public class CLI {
    public static void main(String[] args){
        System.out.println("Santorini - Epifani Falleti Landi");
        CLI CLI = new CLI();

        //Costruzione dello scanner e del Controlle usati nel main(Una volta finita la CLI posso unificarli con quelli del costruttore di CLI?)
        Scanner mainScanner= new Scanner(System.in);
        Controller mainController = new Controller();

        //Creazione della socket del client
        ClientNetworkHandler cnh = new ClientNetworkHandler();

        //Thread per l'esecuzione principale della CLI
        Thread game = new Thread(cnh);
        game.start();


        /*Quando Adriano ha fatto la modifica in ClientNetworkHandler implenta questo while
        * while(cnh.isIdArrived()==false){
        * //Qui bisogna aggiungere un ritardo per far aspettare il client
        * }
        * */
        CLI.setPlayerID(cnh.getPlayerID());
        if(CLI.getPlayerID()==0) {
            //Aspetta fino a quando non arriva un messaggio
            CLI.waitUpdateView(cnh);

            //Chiede numero giocatori al primo client
            System.out.println("Quanti giocatori giocheranno a questa partita?\nInserire 2 per 2 giocatori, 3 per 3 giocatori");
            int n = mainScanner.nextInt();
            while (mainController.checkNumberOfPlayers(n) == false) {
                System.out.println("Errore, impossibile avviare una partita con questo numero di giocatori, chiedere solo per 2 o 3 giocatori");
                n = mainScanner.nextInt();
            }
            CLI.setPlayersNumber(n);
            CLI.buildEvent(cnh, n, VCEvent.Event.setup_request);

            //Le pedine del primo player sono gialle
            CLI.setColor(Color.ANSI_GREEN);
        }else if(CLI.getPlayerID()==1){
            //Le pedine del secondo player sono verdi
            CLI.setColor(Color.ANSI_GREEN);
        }else if(CLI.getPlayerID()==2){
            //Le pedine di un EVENTUALE terzo player sono viola
            CLI.setColor(Color.ANSI_PURPLE);
        }



        System.out.println("Partita terminata!\n Il vincitore è -> " /*+getWinner()*/);

    }

    //Attributi grafici e testuali della CLI
    private Controller c;
    private Scanner s;
    private int playerID;
    private Color ansiColor;
    private Board b;
    private String move="dove ti vuoi muovere";
    private String build="dove vuoi costruire";
    private Elements e;
    private String xPosition ="y";
    private String yPosition = "x";
    private String movingPhase = "Fase di movimento";
    private String buildingPhase = "Fase di costruzione";
    private int playersNumber;


    //Costruttore della CLI

    /***
     *
     */
    public CLI(){
        s = new Scanner(System.in);
        c = new Controller();
        b = new Board();
        e = new Elements();
    }

    /***
     *
     * @param b
     */
    public void printBoard(Board b){
        for(int i=0;i<5;i++){
            for(int j=0;j<5;j++)  {
                    System.out.print( Color.ANSI_BLUE +"| worker: " + turnWorkerIntoImage(b, i, j) + " height: " + turnHeightIntoImage(b, i, j) + " | ");
                    }
            System.out.println();
        }
    }

    /***
     *
     * @param b
     * @param validPositions
     */
    public void paintBoardCell(Board b, ArrayList<Coordinates> validPositions){
        for(int i=0;i<5;i++){
            for(int j=0;j<5;j++){
                for(Coordinates c: validPositions){
                    if(c.getX()==i && c.getY()==j){
                        System.out.print( Color.ANSI_BLUE +"| worker: " + turnWorkerIntoImage(b, i, j) + " height: " + turnHeightIntoImage(b, i, j) + " | ");
                    }else{
                        System.out.print( Color.ANSI_BLUE +"| worker: " + turnWorkerIntoImage(b, i, j) + " height: " + turnHeightIntoImage(b, i, j) + " | ");
                    }
                }
            }
            System.out.println();
        }
    }

    /***
     *
     * @return
     */
    public int getPlayerID() {
        return playerID;
    }

    /***
     *
     * @param playerID
     */
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    /***
     *
     * @param cnh
     * @param o
     * @param command
     */
    public void buildEvent(ClientNetworkHandler cnh, Object o, VCEvent.Event command){
        VCEvent currentEvent= new VCEvent(o, command);
        cnh.sendVCEvent(currentEvent);
    }

    /***
     *
     * @param cnh
     */
    public void waitUpdateView(ClientNetworkHandler cnh){
        while(cnh.isUpdateView()==false){
            //lo facciamo addormentare
        }
    }

    /***
     *
     * @param cnh
     */
    public void checkEvent(ClientNetworkHandler cnh){
        boolean endGame = false;
        while(endGame==false){
            waitUpdateView(cnh);
            switch (cnh.getFromServer().getCommand()){
                case username_request :
                    System.out.println("Inserisci il tuo nome utente");
                    String username=s.nextLine();
                    buildEvent(cnh, username, VCEvent.Event.username_request);
                    break;
                /*case wrong_username*/
                /*case Date request*/
                case not_your_turn:
                    Object objectPlayer = cnh.getFromServer().getBox();
                    if(objectPlayer instanceof Player){
                        Player p = (Player)objectPlayer;
                        System.out.println("Partita in corso, sta giocando " + p.getUsername() + " con la carta " + p.getGameCard());
                    }else {
                        System.out.println("Errore, player corrotto!");
                    }
                    break;
                case update:
                    Object objectBoard = cnh.getFromServer().getBox();
                    if(objectBoard instanceof Board){
                        Board b = (Board)objectBoard;
                        printBoard(b);
                        setBoard(b);
                        //Abbiamo bisogno che update venga sempre mandato prima di send_cells_move o send_cells_build (Da chiedere a Peppe)
                    }else {
                        System.out.println("Errore, board corrotta!");
                    }
                    break;
                case send_cells_move:
                    sendCells(movingPhase, cnh, VCEvent.Event.send_cells_move);
                    break;
                case send_cells_build:
                    sendCells(buildingPhase, cnh, VCEvent.Event.send_cells_build);
                    break;
                case you_lost:
                    Object objectWinner = cnh.getFromServer().getBox();
                    if(objectWinner instanceof String){
                        String winner = (String) objectWinner;
                        System.out.println("Partita conclusa! Il vincitore è: " + winner);
                    }else{
                        System.out.println("Errore! La stringa del vincitore è corrotta");
                    }
                    System.out.println();
                    VCEvent lose= new VCEvent("Ho perso", VCEvent.Event.you_lost);
                    endGame=true;
                    break;
                case you_won:
                    System.out.println("Congratulazioni, sei il vincitore!");
                    VCEvent win= new VCEvent("Ho vinto", VCEvent.Event.you_won);
                    endGame=true;
                    break;
                    //Da scommentare quando ci sarà l'evento game_ended_foryou
                /*case game_ended_foryou:
                    //Ricorda a Peppe che ci sono due modi per perdere
                    System.out.println("Mi spiace, non puoi più muoverti con nessuno dei tuoi worker");
                    break;*/
                case send_all_cards:
                    System.out.println("Scegli " + getPlayersNumber() + "carte");
                    break;
                case wrongInitialPositionMessage:
                    break;
                case send_chosen_cards:
                    break;
                case choose_initial_position:
                    break;
            }
        }
    }

    /***
     *
     * @param c
     */
    public void setColor(Color c) {
        this.ansiColor = c;
    }

    /***
     *
     * @return
     */
    public Color getColor() {
        return ansiColor;
    }

    /***
     *
     * @return
     */
    public Board getBoard() {
        return b;
    }

    /***
     *
     * @param b
     */
    public void setBoard(Board b) {
        this.b = b;
    }

    /***
     *
     * @return
     */
    public String getStringMove() {
        return move;
    }

    /***
     *
     * @return
     */
    public String getStringBuild() {
        return build;
    }

    /***
     *
     * @param b
     * @param i
     * @param j
     * @return
     */
    public String turnHeightIntoImage(Board b, int i, int j){
        if(b.getBoardHeight(i,j)==0){
            return "   ";
        }else if(b.getBoardHeight(i,j)==1){
            return e.getBuilding(1);
        }else if(b.getBoardHeight(i,j)==2){
            return e.getBuilding(1) + " " + e.getBuilding(2);
        }else if(b.getBoardHeight(i,j)==3){
            return " " + e.getBuilding(3) + " ";
        }else if(b.getBoardHeight(i,j)==4){
            return " " + e.getBuilding(4) + " ";
        }
        return "Errore nella costuzione dell'altezza";
    }

    /***
     *
     * @param b
     * @param i
     * @param j
     * @return
     */
    public String turnWorkerIntoImage(Board b, int i, int j){
        if(b.getBoardWorker(i,j)!=null){
            return " " + e.getWorker() + " ";
        }else{
            return "   ";
        }
    }

    /***
     *
     * @param type
     * @return
     */
    public int chooseCoordinate(String type){
        System.out.println("Inserisci posizione " + type);
        int coordinate = s.nextInt();
        while(c.checkLimits(coordinate)==false){
            System.out.println("Errore! Puoi scegliere solo valori tra 0 e 4, inserisci nuovamente la posizione " + type);
            coordinate=s.nextInt();
        }

        return coordinate;
    }

    /***
     *
     * @param validPositions
     * @param chosenCoordinates
     * @return
     */
    public Integer findIndex(ArrayList<Coordinates> validPositions, Coordinates chosenCoordinates){
        int index=0;
        for(Coordinates c:validPositions){
            if(c.equals(chosenCoordinates)){
                return index;
            }
            index++;
        }
        //Se qualcosa è andato storto il controller lato server riceverà un -1
        return  -1;
    }

    /***
     *
     * @param phase
     * @param cnh
     * @param command
     */
    public void sendCells(String phase, ClientNetworkHandler cnh, VCEvent.Event command){
        System.out.println(phase);
        Object objectValidPositions = cnh.getFromServer().getBox();
        ArrayList<Coordinates> validPositions = (ArrayList<Coordinates>)objectValidPositions;
        //Abbiamo bisogno che update venga sempre mandato prima di send_cells_move o send_cells_build (Da chiedere a Peppe)
        paintBoardCell(b, validPositions );
        //Attenzione: qui non possiamo controllare se il dato arrivi corrotto o meno!
        int x=chooseCoordinate(xPosition);
        int y=chooseCoordinate(yPosition);
        Coordinates chosenCoordinates = new Coordinates(x,y);
        while(c.checkRequestedPosition(validPositions, chosenCoordinates)==false){
            System.out.println("Errore! Mossa non valida, controlla le caselle in rosso per poter eseguire una mossa valida");
        }
        VCEvent replyPosition = new VCEvent(findIndex(validPositions, chosenCoordinates), command);
        cnh.sendVCEvent(replyPosition);
    }

    /***
     *
     * @return
     */
    public int getPlayersNumber() {
        return playersNumber;
    }

    /***
     *
     * @param playersNumber
     */
    public void setPlayersNumber(int playersNumber) {
        this.playersNumber = playersNumber;
    }
}