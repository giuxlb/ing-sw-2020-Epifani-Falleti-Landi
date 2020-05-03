package Client.View;

import Controller.Network.VCEvent;
import Model.*;
import Client.Controller.Controller;
import Controller.Coordinates;

import java.util.ArrayList;
import java.util.Scanner;

public class CLI {
    public static void main(String[] args){
        System.out.println("Santorini - Epifani Falleti Landi");
        CLI CLI = new CLI();

        //Creazione della socket del client
        ClientNetworkHandler cnh = new ClientNetworkHandler(CLI);

        //Thread per l'esecuzione principale della CLI
        Thread game = new Thread(cnh);
        game.start();

        //Ciclo di gioco
        CLI.checkEvent(cnh);

        //Messagio di arriverci
        System.out.println("Partita terminata, grazie per aver giocato");

    }

    //Attributi grafici e testuali della CLI
    private ClientNetworkHandler cnh;
    private Controller c;
    private Scanner s;
    private String myColor;
    private Board b;
    private String move="dove ti vuoi muovere";
    private String build="dove vuoi costruire";
    private Elements e;
    private String xPosition ="y";
    private String yPosition = "x";
    private String movingPhase = "Fase di movimento";
    private String buildingPhase = "Fase di costruzione";
    private int playersNumber;
    private int giorno;
    private int mese;
    private int anno;
    private Data dateOfBirth;
    private ArrayList<String> chosenGods = new ArrayList<String>();
    private String myCard;
    private boolean updateView;

    //Costruttore della CLI
    /***
     *
     */
    public CLI(){
        //cnh = new ClientNetworkHandler(CLI);
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
                    System.out.print( Color.ANSI_BLUE +"| worker: " + turnWorkerIntoColoredImage(b, i, j) + Color.ANSI_BLUE + " height: " + turnHeightIntoImage(b, i, j) + " | ");
                    }
            System.out.println();
        }
    }

    /***
     *
     * @param b
     * @param validPositions
     */
    public void paintBoardCell(Board b, ArrayList<Coordinates> validPositions) {
        boolean isToPaint= false;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                    for (Coordinates c: validPositions){
                        if(c.getX()==i && c.getY()==j){
                            isToPaint=true;
                        }
                    }
                if (isToPaint==true) {
                    System.out.print(Color.ANSI_RED + "| worker: " + turnWorkerIntoColoredImage(b, i, j) + Color.ANSI_RED + " height: " + turnHeightIntoImage(b, i, j) + " | ");
                    isToPaint=false;
                } else {
                    System.out.print(Color.ANSI_BLUE + "| worker: " + turnWorkerIntoColoredImage(b, i, j) + Color.ANSI_BLUE + " height: " + turnHeightIntoImage(b, i, j) + " | ");
                }
            }
            System.out.println();
        }
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

    /***
     *
     * @param cnh
     */
    public void checkEvent(ClientNetworkHandler cnh)  {
        boolean endGame = false;
        while(endGame==false){
            update();
            VCEvent evento = cnh.getFromServer();
            cnh.readByView();
            switch (evento.getCommand()){
                case send_color:
                    Object objcectColorName = evento.getBox();
                    if(objcectColorName instanceof String){
                        this.myColor = (String) objcectColorName;
                        System.out.println("I tuoi worker saranno di colore " + myColor);
                    }else{
                        System.out.println("Errore! La stringa che dovrebbe rappresentare in nome del mio colore è arrivata corrotta");
                    }
                    break;
                case setup_request:
                    System.out.println("Quanti giocatori giocheranno a questa partita?\nInserire 2 per 2 giocatori, 3 per 3 giocatori");
                    int n = s.nextInt();
                    s.nextLine();
                    while (c.checkNumberOfPlayers(n) == false) {
                        System.out.println("Errore, impossibile avviare una partita con questo numero di giocatori, chiedere solo per 2 o 3 giocatori");
                        n = s.nextInt();
                        s.nextLine();
                    }
                    this.playersNumber=n;
                    buildEvent(cnh, n, VCEvent.Event.setup_request);
                    break;
                case username_request :
                    System.out.println("Inserisci il tuo nome utente");
                    String username=s.nextLine();
                    buildEvent(cnh, username, VCEvent.Event.username_request);
                    break;
                case wrong_username:
                    Object objectWrongUsername = evento.getBox();
                    System.out.println("Errore! Hai inserito uno username già scelto da un altro giocatore\nReinserire username");
                    String newUsermane= s.nextLine();
                    buildEvent(cnh,newUsermane, VCEvent.Event.wrong_username);
                    break;
                case date_request:
                    insertDate();
                    dateOfBirth = new Data(giorno,mese,anno);
                    s.nextLine();
                    while(c.controllaData(dateOfBirth)==false){
                        System.out.println("Data non valida, per favore inserire nuovamente");
                        insertDate();
                        s.nextLine();
                        this.dateOfBirth = new Data(giorno,mese,anno);
                    }
                    buildEvent(cnh,dateOfBirth, VCEvent.Event.date_request);
                    break;
                case not_your_turn:
                    Object objectCurrentPlayerInformation = evento.getBox();
                    ArrayList<String> currentPlayerInformation= (ArrayList<String>) objectCurrentPlayerInformation;
                    System.out.println("Partita in corso, sta giocando " + currentPlayerInformation.get(0) + " con la carta " + currentPlayerInformation.get(0));
                    break;
                case update:
                    Object objectBoardCell = evento.getBox();
                    ArrayList<SocketBoardCell> socketBoardCell = (ArrayList<SocketBoardCell>) objectBoardCell;

                    int counter = 0;
                    for (int i = 0; i <5 ; i++) {
                        for (int j = 0; j < 5; j++) {
                            b.setBoardHeight(i,j,socketBoardCell.get(counter).getHeight());
                           if (socketBoardCell.get(counter).getWorkerColor() != null)
                           {
                               b.setBoardWorker(i,j,new Worker(i,j,socketBoardCell.get(counter).getWorkerColor()));
                           }
                           counter++;
                        }

                    }
                    printBoard(b);
                    break;
                case ask_for_worker:
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
                    Coordinates chosenCoordinates = new Coordinates(x,y);
                    while(c.checkRequestedPosition(positionWorkers, chosenCoordinates)==false){
                        System.out.println("Errore! Selezione non valida");
                        x=chooseCoordinate(xPosition);
                        s.nextLine();
                        y=chooseCoordinate(yPosition);
                        s.nextLine();
                        chosenCoordinates = new Coordinates(x,y);
                    }
                    buildEvent(cnh,findIndex(positionWorkers,chosenCoordinates), VCEvent.Event.ask_for_worker);
                    break;
                case send_cells_move:
                    sendCells(movingPhase, cnh, VCEvent.Event.send_cells_move,evento);
                    break;
                case send_cells_build:
                    sendCells(buildingPhase, cnh, VCEvent.Event.send_cells_build,evento);
                    break;
                case you_lost:
                    Object objectWinner = evento.getBox();
                    if(objectWinner instanceof String){
                        String winner = (String) objectWinner;
                        System.out.println("Partita conclusa! Il vincitore è: " + winner);
                    }else{
                        System.out.println("Errore! La stringa del vincitore è corrotta");
                    }
                    System.out.println();
                    buildEvent(cnh,"ho perso", VCEvent.Event.you_lost);
                    endGame=true;
                    break;
                case you_won:
                    System.out.println("Congratulazioni, sei il vincitore!");
                    buildEvent(cnh,"ho vinto", VCEvent.Event.you_won);
                    endGame=true;
                    break;
                case game_ended_foryou:
                    System.out.println("Mi spiace, non puoi più muoverti con nessuno dei tuoi worker");
                    break;
                case number_of_players:
                    playersNumber = (Integer)evento.getBox();
                    break;
                case send_all_cards:
                    System.out.println("Hai a disposizione le seguenti divinità:");
                    Object objectGods = evento.getBox();
                    //Non possiamo controllare se l'ArrayList di Stringhe sia corrotto o meno
                    ArrayList<String> gods= (ArrayList<String>)  objectGods;
                    for(String god:gods){
                        System.out.print(god + " ");
                    }
                    System.out.println("");
                    System.out.println("Scegli " + playersNumber + "carte");

                    for(int i=0;i<playersNumber;i++){
                        System.out.print("Scegli la " + (i+1) + "°" + "carta ->");
                        String card = s.nextLine();

                        //Ricordati di chiamare il controller pure qui
                        chosenGods.add(card);
                    }
                    System.out.println();
                    buildEvent(cnh,chosenGods, VCEvent.Event.send_all_cards);
                    break;
                case send_chosen_cards:
                    System.out.println("Puoi scegliere tra le seguenti divinità:");
                    Object objectSentGods = evento.getBox();
                    //Non possiamo controllare se l'ArrayList di Stringhe sia corrotto o meno
                    ArrayList<String> sentGods= (ArrayList<String>)  objectSentGods;
                    if(sentGods.size()==1){
                        System.out.println("La tua carta divinità sarà " + sentGods.get(0));
                        this.myCard=sentGods.get(0);
                        //In realtà non ci sarebbe bisogno dello statement successivo
                        buildEvent(cnh, sentGods.get(0), VCEvent.Event.send_chosen_cards);
                    }else{
                        for(String god:sentGods){
                            System.out.print(god + " ");
                        }
                        System.out.println();
                        System.out.print("Digita il nome della divinità che preferisci ->");
                        //Il controller deve controllare che effettivamente la divinità scelta sia un elemento delle divinità ricevute
                        String chosenGod= s.nextLine();
                        this.myCard=chosenGod;
                        System.out.println();
                        buildEvent(cnh, chosenGod, VCEvent.Event.send_chosen_cards);
                    }
                    break;
                case player_disconnected_game_ended:
                    Object objectPlayerDisconnected= evento.getBox();
                    if(objectPlayerDisconnected instanceof String){
                        String playerDisconnected = (String) objectPlayerDisconnected;
                        System.out.println("Ops! Il giocatore " + playerDisconnected + " si è disconnesso, purtroppo la partita terminerà ora");
                    }else{
                        System.out.println("Errore! La stringa del nome del giocatore che è disconnesso è corrotta");
                    }
                    buildEvent(cnh, "ho ricevuto la disconnessione di un client", VCEvent.Event.player_disconnected_game_ended);
                    break;
                default:
                    System.out.println("Errore nel protocollo");
                    break;
            }

        }
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
            return e.getBuilding(0);
        }else if(b.getBoardHeight(i,j)==1){
            return " " + e.getBuilding(1) + " ";
        }else if(b.getBoardHeight(i,j)==2){
            return " " + e.getBuilding(2) + " ";
        }else if(b.getBoardHeight(i,j)==3){
            return e.getBuilding(3) + " " + e.getBuilding(1);
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
    public String turnWorkerIntoColoredImage(Board b, int i, int j){
        if(b.getBoardWorker(i,j)!=null){
            switch (b.getBoardWorker(i,j).getColor()){
                case ANSI_YELLOW:
                    return " " + Color.ANSI_YELLOW + e.getWorker() + " ";
                case ANSI_GREEN:
                    return " " + Color.ANSI_GREEN + e.getWorker() + " ";
                case ANSI_PURPLE:
                    return " " + Color.ANSI_PURPLE + e.getWorker() + " ";
            }
        }else{
            return "    ";
        }

        return "Renderizzazione e colorazione del worker non riuscita";
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
            if((c.getX() == chosenCoordinates.getX()) && (c.getY() == chosenCoordinates.getY())){
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
    public void sendCells(String phase, ClientNetworkHandler cnh, VCEvent.Event command,VCEvent evento){
        System.out.println(phase);
        Object objectValidPositions = evento.getBox();
        //Attenzione: qui non possiamo controllare se il dato arrivi corrotto o meno!
        ArrayList<Coordinates> validPositions = (ArrayList<Coordinates>)objectValidPositions;
        paintBoardCell(b, validPositions );
        int x=chooseCoordinate(xPosition);
        s.nextLine();
        int y=chooseCoordinate(yPosition);
        s.nextLine();
        Coordinates chosenCoordinates = new Coordinates(x,y);
        while(c.checkRequestedPosition(validPositions, chosenCoordinates)==false){
            System.out.println("Errore! Mossa non valida, controlla le caselle in rosso per poter eseguire una mossa valida");
            x=chooseCoordinate(xPosition);
            s.nextLine();
            y=chooseCoordinate(yPosition);
            s.nextLine();
            chosenCoordinates = new Coordinates(x,y);
        }
        buildEvent(cnh,findIndex(validPositions, chosenCoordinates),command);
    }

    /***
     *
     */
    public void insertDate(){
        System.out.print("Inserire il giorno in cui si è nati in formato gg (solo numerico) ->");
        this.giorno=s.nextInt();
        System.out.print("Inserire il mese in cui si è nati in formato mm (solo numerico) ->");
        this.mese=s.nextInt();
        System.out.print("Inserire l'anno in cui si è nati in formato aaaa (solo numerico) ->");
        this.anno=s.nextInt();
        System.out.println();
    }

}