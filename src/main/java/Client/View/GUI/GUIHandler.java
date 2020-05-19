package Client.View.GUI;
import javax.swing.*;

import Client.View.ClientNetworkHandler;
import Client.View.Data;
import Controller.Coordinates;
import Controller.Network.VCEvent;
import Model.Board;
import Model.SocketBoardCell;
import Model.Worker;

import java.awt.*;
import java.util.ArrayList;


public class GUIHandler {
    //GUI elements to handle
    private GUI GUI;

    //To connect
    private ClientNetworkHandler cnh;
    private Thread game;
    private boolean updateView;

    //Support elements
    private Board b;
    //ATTENZIONE: potrebbe non servire
    private int playersNumber;

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
        /*while(endGame==false){
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
                    recreateBoardfromSocketBoardCell(socketBoardCell);
                    turnModelBoardintoGUIBoard(b);
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
                    break
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


                    if (choose == 1) {
                        firstTimeHeMoves = 1;
                        this.b.deepCopy(copy);
                    }




                    buildEvent(cnh,choose, VCEvent.Event.undo_request);
                    break;
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
                    Object objectGod = evento.getBox();
                    String god_name = (String) objectGod;
                    switch (god_name.toUpperCase()){
                        case "ARTEMIS":
                            messageArea.setText("Vuoi attivare l'effetto di Artemis e fare un secondo movimento?");
                            break;
                        case "ATLAS":
                            messageArea.setText("Vuoi attivare l'effetto di Atlas e costruire una cupola?");
                            break;
                        case "DEMETER":
                            messageArea.setText("Vuoi attivare l'effetto di Demeter e fare una seconda costruzione?");
                            break;
                        case "HEPHAESTUS":
                            messageArea.setText("Vuoi attivare l'effetto di Hephaestus e costruire una seconda volta sullo stesso blocco?");
                            break;
                        case "PROMETHEUS":
                            messageArea.setText("Vuoi attivare l'effetto di Prometheus? (Costruisci prima e dopo il movimento però non puoi salire di livello)");
                            break;
                        case "HESTIA":
                            messageArea.setText("Vuoi attivare l'effetto di Hestia e fare una seconda costruzione, ma non in una casella perimetrale?");
                            break;
                        case "TRITON":
                            messageArea.setText("Vuoi attivare l'effetto di Triton e fare una altro movimento?");
                            break;
                        case "ARES":
                            messageArea.setText("Vuoi attivare l'effetto di Ares e rimuovere un blocco libero (non una cupola) con il worker che non hai mosso?");
                            break;
                    }
                    boolean temp = false;
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
                        //chosenGod=validCard(chosenGod,sentGods);
                        this.myCard=chosenGod;
                        System.out.println();
                        buildEvent(cnh, chosenGod, VCEvent.Event.send_chosen_cards);
                    }
                    break;
                case player_disconnected_game_ended:
                    Object objectPlayerDisconnected= evento.getBox();
                    String playerDisconnected = (String) objectPlayerDisconnected;
                    //o sarebbe meglio una finestra di dialogo
                    messageArea.setText("Ops! Il giocatore " + playerDisconnected + " si è disconnesso, purtroppo la partita terminerà ora");
                    buildEvent(cnh, "ho ricevuto la disconnessione di un client", VCEvent.Event.player_disconnected_game_ended);
                    endGame=true;
                    break;
                default:
                    System.out.println("Errore nel protocollo");
                    break;
            }

        }
    }*/




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

}
