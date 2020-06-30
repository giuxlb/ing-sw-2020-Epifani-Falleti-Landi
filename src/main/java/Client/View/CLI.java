package Client.View;

import Controller.Network.VCEvent;
import Model.*;
import Client.Controller.Controller;
import Controller.Coordinates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class CLI {
    public static void main(String[] args){
        System.out.println("Santorini - Epifani Falleti Landi");
        System.out.println("Inserisci l'indirizzo del server:");
        Scanner addrScan = new Scanner(System.in);
        String addr = addrScan.nextLine();
        if(isAMac()==true){
            CLI CLI = new CLIForMac("Mac");

            ClientNetworkHandler cnh = new ClientNetworkHandler(CLI,addr);
            if(cnh==null){
                System.out.println("Indirizzo IP non valido, il gioco terminerà ora");
                System.exit(0);
            }
            //Thread per l'esecuzione principale della CLI
            Thread game = new Thread(cnh);
            game.start();

            //Ciclo di gioco
            CLI.checkEvent(cnh);

            //Messagio di arriverci
            System.out.println("Partita terminata, grazie per aver giocato");
            cnh.setFinish(true);

        }else{
            CLI CLI = new CLI("Win");
            ClientNetworkHandler cnh = new ClientNetworkHandler(CLI,addr);
            if(cnh==null){
                System.out.println("Indirizzo IP non valido, il gioco terminerà ora");
                System.exit(0);
            }
            //Thread per l'esecuzione principale della CLI
            Thread game = new Thread(cnh);
            game.start();

            //Ciclo di gioco
            CLI.checkEvent(cnh);

            //Messaggio di arriverci
            System.out.println("Partita terminata, grazie per aver giocato");
            cnh.setFinish(true);

        }


        //Creazione della socket del client



    }

    //Attributi grafici e testuali della CLI
    private ClientNetworkHandler cnh;
    private Controller c;
    private Scanner s;
    private String myColor;
    private Board b;
    private String move="dove ti vuoi muovere";
    private String build="dove vuoi costruire";
    private String sendCellsMove = "dove puoi muoverti";
    private String sendCellsBuild = "dove puoi costruire";
    private Elements e;
    private String xPosition ="y";
    private String yPosition = "x";
    private String movingPhase = "Fase di movimento";
    private String buildingPhase = "Fase di costruzione";
    private String removePhase = "Fase di rimozione";
    private int playersNumber;
    private int giorno;
    private int mese;
    private int anno;
    private Data dateOfBirth;
    private ArrayList<String> chosenGods = new ArrayList<String>();
    private String myCard;
    private boolean updateView;
    private String address;

    //Costruttore della CLI
    /***
     *
     */
    public CLI(String OS){
        //cnh = new ClientNetworkHandler(CLI);
        s = new Scanner(System.in);
        c = new Controller();
        b = new Board();
        e = new Elements();
    }

    public CLI(){

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
        System.out.println();
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
      // Board copy = new Board();
      // int firstTimeHeMoves = 1;
        while(endGame==false){
            update();
            VCEvent evento = cnh.getFromServer();
            cnh.readByView();
            switch (evento.getCommand()){
                case send_color:
                    Object objcectColorName = evento.getBox();
                    this.myColor = (String) objcectColorName;
                    System.out.println("I tuoi worker saranno di colore " + myColor);
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
                    System.out.println("Partita in corso, sta giocando " + currentPlayerInformation.get(0) +
                            " con la carta " + currentPlayerInformation.get(1));
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
                           }else{
                               b.setBoardWorker(i,j,null);
                           }
                           counter++;
                        }

                    }
                    printBoard(b);
                    break;
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
                case send_cells_remove:
                    sendCells(removePhase,cnh, VCEvent.Event.send_cells_remove,evento);
                    break;

                case undo_request:
                    System.out.println("Hai 5 secondi per fare l'undo della mossa appena fatta. Scrivi 1 per fare undo o 0 per non farlo");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    long start = System.currentTimeMillis();
                    int choose = 0;
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

/*
                    if (choose == 1) {
                        firstTimeHeMoves = 1;
                        this.b.deepCopy(copy);
                    }


 */



                    buildEvent(cnh,choose, VCEvent.Event.undo_request);
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
                case ask_for_divinity_activation:
                    Object objectGod = evento.getBox();
                    String god_name = (String) objectGod;
                    switch (god_name.toUpperCase()){
                        case "ARTEMIS":
                            System.out.println("Vuoi attivare l'effetto di Artemis e fare un secondo movimento?");
                            break;
                        case "ATLAS":
                            System.out.println("Vuoi attivare l'effetto di Atlas e costruire una cupola?");
                            break;
                        case "DEMETER":
                            System.out.println("Vuoi attivare l'effetto di Demeter e fare una seconda costruzione?");
                            break;
                        case "HEPHAESTUS":
                            System.out.println("Vuoi attivare l'effetto di Hephaestus e costruire una seconda volta sullo stesso blocco?");
                            break;
                        case "PROMETHEUS":
                            System.out.println("Vuoi attivare l'effetto di Prometheus? (Costruisci prima e dopo il movimento però non puoi salire di livello)");
                            break;
                        case "HESTIA":
                            System.out.println("Vuoi attivare l'effetto di Hestia e fare una seconda costruzione, ma non in una casella perimetrale?");
                            break;
                        case "TRITON":
                            System.out.println("Vuoi attivare l'effetto di Triton e fare una altro movimento?");
                            break;
                        case "ARES":
                            System.out.println("Vuoi attivare l'effetto di Ares e rimuovere un blocco libero (non una cupola) con il worker che non hai mosso?");
                            break;
                    }
                    boolean temp = false;
                    //Se ne deve occupare il clientSideController
                    while(temp == false) {
                        System.out.println("(Y) per attivare, (N) per non attivare:");
                        String response = s.nextLine();
                        switch (response.toUpperCase()) {
                            case "Y":
                                temp = true;
                                buildEvent(cnh, 1, VCEvent.Event.ask_for_divinity_activation);
                                break;
                            case "N":
                                temp = true;
                                buildEvent(cnh, 0, VCEvent.Event.ask_for_divinity_activation);
                                break;
                            default:
                                System.out.println("Input non valido, riprova.");
                                break;
                        }
                    }
                    break;
                case send_all_cards:
                    System.out.println("Hai a disposizione le seguenti divinità:");
                    Object objectGods = evento.getBox();
                    ArrayList<String> gods= (ArrayList<String>)  objectGods;
                    for(String god:gods){
                        System.out.print(god + " ");
                    }
                    System.out.println("");
                    System.out.println("Scegli " + playersNumber + " carte");

                    for(int i=0;i<playersNumber;i++){
                        System.out.print("Scegli la " + (i+1) + "° carta -> ");
                        String card = s.nextLine();
                        //card=validCard(card,gods);
                        //card=isAlreadyChosen(i, card, chosenGods);
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
                        System.out.println("La tua carta divinità sarà " + sentGods.get(0)); //Operazione sulla GUI
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
                        String chosenGod= s.nextLine(); //Operazione GUI
                        //chosenGod=validCard(chosenGod,sentGods);
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
                    endGame=true;
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
                case ANSI_WHITE:
                    return " " + Color.ANSI_WHITE + e.getWorker() + " ";
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
        System.out.println("Le celle in rosso ti dicono " /*+ specification*/);
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

    /***
     *
     * @param insertion
     * @param gods
     * @return
     */
    String validCard(String insertion, ArrayList<String> gods){
        String convertedInsertion= insertion.toUpperCase();
        while(c.isInArrayListOfGods(convertedInsertion,gods)==false){
            System.out.println("Errore! Nome divinià non valido. Scegli una divinità tra quelle presenti!");
            System.out.print("Reinserisci il nome della divinità -> ");
            convertedInsertion=s.nextLine();
            convertedInsertion.toUpperCase();
        }

        return insertion;
    }

    /***
     *
     * @param index
     * @param insertion
     * @param gods
     * @return
     */
    String isAlreadyChosen(int index, String insertion, ArrayList<String> gods){
        String convertedInsertion=insertion;
        while(c.isInArrayListOfGods(convertedInsertion, gods)==true){
            System.out.println("Errore! Non puoi selezionare una divinità due volte, per favore seleziona una nuova divinità");
            System.out.print("Scegli la " + (index+1) + "°" + "carta -> ");
            convertedInsertion=s.nextLine();
            convertedInsertion.toUpperCase();
        }

        return convertedInsertion;
    }

    public static boolean isAMac(){
        String OS = System.getProperty("os.name").toLowerCase();
        if(OS.indexOf("mac")>=0){
            return true;
        }
        return false;
    }

    public Elements getE() {

        return e;
    }
}