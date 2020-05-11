package Controller;

import Controller.Network.ServerNetworkHandler;
import Controller.Network.VCEvent;
import Model.*;
import Client.View.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import Client.View.CLI;
/**
 * @author Adriano Falleti
 */
public class VirtualView {
    private ServerNetworkHandler serverHandler;
    private ArrayList<Player> players;
    private Integer numberOfPlayers;
    private Object received;
    private boolean setUpisReady;
    private boolean[] connected;
    private boolean undoOn;
    private Object undoReceived;

    public boolean isStartNewGame() {
        return startNewGame;
    }

    private boolean startNewGame;



    private boolean okFromClient;


    /**
     * It creates the ServerNetworkHandler and prepares the information about the firstPlayer for the GameControl
     */
    public VirtualView()
    {
        this.players = new ArrayList<Player>();
        players.add(new Player("client 1",new Data(1,1,1998)));
        players.add(new Player("client 2",new Data(1,1,1998)));
        players.add(new Player("client 3",new Data(1,1,1998)));
        this.connected = new boolean[3];
        this.serverHandler = new ServerNetworkHandler(this);
        Thread serverThread = new Thread(serverHandler);
        serverThread.start();
        this.undoOn = false;
        this.setUpisReady = false;
        this.okFromClient = false;
        for (int i = 0; i < 3; i++) {
            connected[i] = true;
        }
        this.startNewGame=false;

    }


    /***
     * It sends the setup_request only to the first client conneceted
     * @return
     */
    public int playerNumber()
    {

        System.out.println("Chiamo la playerNumber");
        synchronized (this)
        {
            while (this.connected[0] == false)
            {
                System.out.println("Aspetto il collegamento...");
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("È collegato...");
        VCEvent evento = new VCEvent(null, VCEvent.Event.setup_request);
        System.out.println("La virtual view manda la set up request ");
        serverHandler.sendVCEventTo(evento,0);
        synchronized (this) {
            received = null;
            while (received == null && checkConnections()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
        if (checkConnections() == false) {
            return -1;
        }
        if (received instanceof Integer) {
            numberOfPlayers = (Integer) received;
            if (numberOfPlayers == 2)
                connected[2] = false;
            setUpisReady = true;
            System.out.println("continuo a ricevere eventi...");
            serverHandler.setPlayerNumber(numberOfPlayers);
            return numberOfPlayers.intValue();
        }
        return -1;

    }

    /***
     * It sends the color of workers tha a player is using to him
     * @param c identifies the color
     * @param index identifies the client
     */
    public void sendColor(String c,int index)
    {
        VCEvent evento = new VCEvent(c, VCEvent.Event.send_color);
        serverHandler.sendVCEventTo(evento,index);

    }
    /**
     * It asks to a client for username
     * @param index identifies the client who will receive the request
     * @param wasWrong
     * @return
     */
    public String askForUsername(int index,boolean wasWrong)
    {
        if (checkConnections() == false) {
            return null;
        }
        VCEvent evento;
        if (wasWrong == false) {
             evento = new VCEvent("username", VCEvent.Event.username_request);
        }
        else{
            evento =  new VCEvent("", VCEvent.Event.wrong_username);
        }
        serverHandler.sendVCEventTo(evento,index);
        synchronized(this) {
            received = null;
            while(received == null && checkConnections())
            {
                try{
                    wait();
                }
                catch(InterruptedException e){}
            }

        }
        if (checkConnections() == false) {
            return null;
        }
        System.out.println(received);
        if (received instanceof String)
            return (String) received;

        return "";

    }

    /**
     * It asks to a client for username
     * @param index identifies the client who will receive the request
     * @return
     */
    public Data askForDate(int index)
    {
        if (checkConnections() == false) {
            return null;
        }
        VCEvent evento = new VCEvent("Data", VCEvent.Event.date_request);
        serverHandler.sendVCEventTo(evento,index);
        synchronized(this) {
            received = null;
            while(received == null && checkConnections())
            {
                try{
                    wait();
                }
                catch(InterruptedException e){}
            }

        }
        if (checkConnections() == false) {
            return null;
        }
        if (received instanceof Data)
            return (Data) received;


        return null;
    }

    /**
     * It notifies the clientl that now it's not their turn
     * @param playersNotPlaying are the players that won't play during this turn
     */
    public void notYourTurn(Player[] playersNotPlaying,Player playerPlaying)
    {
        ArrayList<String> player = new ArrayList<String>();
        player.add(playerPlaying.getUsername());
        player.add(playerPlaying.getGameCard());
        VCEvent evento = new VCEvent(player, VCEvent.Event.not_your_turn);
        for (int i = 0; i <numberOfPlayers ; i++) {
            for (int j = 0; j < numberOfPlayers-1; j++) {
                if (players.get(i).getUsername().equals(playersNotPlaying[j].getUsername()))
                {
                    serverHandler.sendVCEventTo(evento,i);
                }
            }
        }

    }

    /**
     * It sends to all the clients the update of the board after a move or a build of another player
     * @param board is the updated board
     */
    public void upload(Board board)
    {
        for(int i = 0;i<5;i++){
            for(int j = 0;j<5;j++){
                System.out.print("| "+ board.getBoardGame()[i][j].getWorkerBuilder() +" |");
            }
            System.out.println(" ");
        }

        ArrayList<SocketBoardCell> socketBoard = new ArrayList<SocketBoardCell>();
        for (int i = 0; i < 5 ; i++) {
            for (int j = 0; j < 5; j++) {
                socketBoard.add(new SocketBoardCell(board.getBoardHeight(i,j),board.getBoardWorker(i,j)));
            }
        }

        VCEvent evento = new VCEvent(socketBoard, VCEvent.Event.update);

        for (int i = 0; i <numberOfPlayers ; i++)
            serverHandler.sendVCEventTo(evento,i);


    }

    public void sendNumberOfPlayer()
    {
        VCEvent e = new VCEvent(numberOfPlayers, VCEvent.Event.number_of_players);
        for (int i = 1; i <numberOfPlayers ; i++) {
            serverHandler.sendVCEventTo(e,i);
        }
    }

    public int askDivinityActivation(Player p, String divinity){
        if (checkConnections() == false) {
            return -1;
        }
        VCEvent evento = new VCEvent(divinity, VCEvent.Event.ask_for_divinity_activation);
        for(int i = 0; i<numberOfPlayers;i++){
            if (p.getUsername().equals(players.get(i).getUsername()))
                serverHandler.sendVCEventTo(evento,i);
        }
        synchronized (this){
            received = null;
            while (received == null && checkConnections()){
                try{
                    wait();
                }
                catch (InterruptedException e){}
            }
        }
        if (checkConnections() == false) {
            return -1;
        }
        if (received instanceof Integer) {
            int x = ((Integer) received).intValue();
            return x;
        }
        return -1;
    }

    /**
     * It sends all the cards to the first client connected
     * @param p is the first client connected
     * @param cards are all the cards implemented in the game
     * @return 2 or 3 cards, depending on the number of players of the game
     */
    public ArrayList<String> sendAllCards(Player p, ArrayList<String> cards)
    {
        if (checkConnections() == false) {
            return null;
        }
        System.out.println("Mando la sendAllCards");
        VCEvent evento = new VCEvent(cards, VCEvent.Event.send_all_cards);
        for (int i = 0; i <numberOfPlayers ; i++) {
            if (p.getUsername().equals(players.get(i).getUsername()))
                serverHandler.sendVCEventTo(evento,i);
        }
        synchronized(this) {
            received = null;
            while(received == null && checkConnections())
            {
                try{
                    wait();
                }
                catch(InterruptedException e){}
            }

        }
        if (checkConnections() == false) {
            return null;
        }
        ArrayList<String> chosenCards = (ArrayList<String>) received;

        return chosenCards;
    }

    /**
     * It sends the cards chosen from the first player to the other clients
     * @param p is the player who will receive the chosen cards from the first client
     * @param chosenCards are the cards chosen
     * @return the card chosen from the player p
     */
    public String sendChosenCards(Player p,ArrayList<String> chosenCards)
    {
        if (checkConnections() == false) {
            return null;
        }
        ArrayList<String> cards = new ArrayList<String>();
        for (int i = 0; i <chosenCards.size(); i++)
        {
            cards.add(chosenCards.get(i));
        }
        VCEvent evento = new VCEvent(cards, VCEvent.Event.send_chosen_cards);
        for (int i = 0; i <numberOfPlayers ; i++) {
            if (p.getUsername().equals(players.get(i).getUsername()))
                serverHandler.sendVCEventTo(evento,i);
        }
        synchronized(this) {
            received = null;
            while(received == null && checkConnections())
            {
                try{
                    wait();
                }
                catch(InterruptedException e){}
            }

        }
        if (checkConnections() == false) {
            return null;
        }
        if (received instanceof String)
            return (String) received;

        return null;
    }

    /**
     * It sends the card chosen to the corresponding player
     * @param p is the player
     * @param c is the card chosen by player p
     */
    public void sendYourCard(Player p, String c)
    {
        VCEvent evento = new VCEvent(c, VCEvent.Event.send_your_card);
        for (int i = 0; i <numberOfPlayers ; i++) {
            if (p.getUsername().equals(players.get(i).getUsername()))
                serverHandler.sendVCEventTo(evento,i);
        }
    }


    /**
     * It sends all the spots where a player can move his workers
     * @param p identifies the player
     * @param move_spots identifies the movespots
     * @return it returns the index that identifies the position in the ArrayList move_spots chosen by the player
     */
    public int sendAvailableMove(Player p, ArrayList<Coordinates> move_spots)
    {
        int index = -1;
        if (checkConnections() == false) {
            return -1;
        }
        ArrayList<Coordinates> coordinate = new ArrayList<Coordinates>();
        for (int i = 0; i <move_spots.size(); i++)
        {
            coordinate.add(move_spots.get(i));
        }
        VCEvent evento = new VCEvent(coordinate, VCEvent.Event.send_cells_move);
        for (int i = 0; i <numberOfPlayers ; i++) {
            if (p.getUsername().equals(players.get(i).getUsername())) {
                index = i;
                serverHandler.sendVCEventTo(evento, i);
            }
        }
        synchronized(this) {
            received = null;
            while(received == null && checkConnections())
            {
                try{
                    wait();
                }
                catch(InterruptedException e){}
            }

        }
        if (checkConnections() == false)
            return -1;
        //System.out.println("Ricevo..."+received);

        if (received instanceof Integer) {
            /*
            if (undoOn)
            {
               int response = sendUndoRequest(index);
               if (response == 1)
                   return sendAvailableMove(p,move_spots);
               else if (response == -1)
                   return -1;
            }

             */

            int x = ((Integer) received).intValue();
            System.out.println("Ritorno " + move_spots.get(x).toString());
            return x;

        }



        return -1;

    }
    /**
     * It sends all the spots where a player can build with his worker
     * @param p identifies the player
     * @param build_spots identifies the build_spots
     * @return it returns the index that identifies the position in the ArrayList build_spots chosen by the player
     */
    public int sendAvailableBuild(Player p, ArrayList<Coordinates> build_spots)
    {
        int index = -1;
        if (checkConnections() == false) {
            return -1;
        }
        ArrayList<Coordinates> coordinate = new ArrayList<Coordinates>();
        for (int i = 0; i <build_spots.size(); i++)
        {
            coordinate.add(build_spots.get(i));
        }
        VCEvent evento = new VCEvent(coordinate, VCEvent.Event.send_cells_build);
        for (int i = 0; i <numberOfPlayers ; i++) {
            if (p.getUsername().equals(players.get(i).getUsername())) {
                index = i;
                serverHandler.sendVCEventTo(evento, i);
            }
        }
        synchronized(this) {
            received = null;
            while(received == null && checkConnections())
            {
                try{
                    wait();
                }
                catch(InterruptedException e){}
            }

        }
        if (checkConnections() == false) {
            return -1;
        }
        if (received instanceof Integer) {
            /*
            if (undoOn)
            {
                int response = sendUndoRequest(index);
                if (response == 1)
                    return sendAvailableMove(p,build_spots);
                else if (response == -1)
                    return -1;
            }

             */
            int x = ((Integer) received).intValue();
            return x;
        }


        return -1;

    }

    public int sendAvailableRemove(Player p, ArrayList<Coordinates> build_spots)
    {
        int index = -1;
        if (checkConnections() == false) {
            return -1;
        }
        ArrayList<Coordinates> coordinate = new ArrayList<Coordinates>();
        for (int i = 0; i <build_spots.size(); i++)
        {
            coordinate.add(build_spots.get(i));
        }
        VCEvent evento = new VCEvent(coordinate, VCEvent.Event.send_cells_remove);
        for (int i = 0; i <numberOfPlayers ; i++) {
            if (p.getUsername().equals(players.get(i).getUsername())) {
                index = i;
                serverHandler.sendVCEventTo(evento, i);
            }
        }
        synchronized(this) {
            received = null;
            while(received == null && checkConnections())
            {
                try{
                    wait();
                }
                catch(InterruptedException e){}
            }

        }
        if (checkConnections() == false) {
            return -1;
        }
        if (received instanceof Integer) {
            /*
            if (undoOn)
            {
                int response = sendUndoRequest(index);
                if (response == 1)
                    return sendAvailableMove(p,build_spots);
                else if (response == -1)
                    return -1;
            }

            */
            int x = ((Integer) received).intValue();
            return x;
        }


        return -1;

    }

    public int sendUndoRequest(Player p)
    {
        if (checkConnections() == false) {
            return -1;
        }
        VCEvent evento = new VCEvent("Undo", VCEvent.Event.undo_request);
        for (int i = 0; i <numberOfPlayers ; i++) {
            if (p.getUsername().equals(players.get(i).getUsername()))
                serverHandler.sendVCEventTo(evento, i);

        }
        synchronized(this) {
            undoReceived = null;
            while(undoReceived == null && checkConnections())
            {
                try{
                    wait();
                }
                catch(InterruptedException e){}
                if (undoReceived == null && checkConnections()) // se ancora non ho ricevuto nulla e sono ancora tutti collegat, vuol dire che il tempo è scaduto
                    return 0;
            }

        }
        if (checkConnections() == false) {
            return -1;
        }
        if (undoReceived instanceof Integer) {
            int x = ((Integer) undoReceived).intValue();
            return x;
        }

        return -1;

    }
    /***
     * It asks to the player, which worker he wants to use for moving/building
     * @param p is the player who will receive this request
     * @param workersPosition contains the positions of player p's workers
     * @return
     */
    public Coordinates askForWorker(Player p, ArrayList<Coordinates> workersPosition)
    {
        if (checkConnections() == false) {
            return null;
        }
        ArrayList<Coordinates> coordinate = new ArrayList<Coordinates>();
        for (int i = 0; i <workersPosition.size(); i++)
        {
            coordinate.add(workersPosition.get(i));
        }
        VCEvent evento = new VCEvent(coordinate, VCEvent.Event.ask_for_worker);
        for (int i = 0; i <numberOfPlayers ; i++) {
            if (p.getUsername().equals(players.get(i).getUsername()))
                serverHandler.sendVCEventTo(evento,i);
        }
        synchronized(this) {
            received = null;
            while(received == null && checkConnections())
            {
                try{
                    wait();
                }
                catch(InterruptedException e){}
            }

        }
        if (checkConnections() == false) {
            return null;
        }
        if (received instanceof Integer) {
            int x = ((Integer) received).intValue();
            return workersPosition.get(x);
        }
        return null;


    }


    /**
     * It notifies the player p that he has lost
     * @param p
     */
    public void youLost(Player p,boolean playerBlocked,Player winner)
    {
        VCEvent evento;
        if (playerBlocked == false) {
             evento = new VCEvent(winner.getUsername(), VCEvent.Event.you_lost);
        }
        else{
            String vincitore = "";
            if (winner != null)
                vincitore = winner.getUsername();
            evento = new VCEvent(vincitore, VCEvent.Event.game_ended_foryou);
        }
        for (int i = 0; i <numberOfPlayers ; i++) {
            if (p.getUsername().equals(players.get(i).getUsername()))
                serverHandler.sendVCEventTo(evento,i);
        }

    }

    /**
     * It notifies the player p that he has won
     * @param p
     */
    public void youWon(Player p)
    {
        VCEvent evento = new VCEvent("Winner", VCEvent.Event.you_won);
        for (int i = 0; i <numberOfPlayers ; i++) {
            if (p.getUsername().equals(players.get(i).getUsername()))
                serverHandler.sendVCEventTo(evento,i);
        }
    }

    /***
     * It notifies to player p that a player has disconnected, so the game is ended with no winner
     * If  numberOfPlayers is null, it means the first client has disconnected,before sending the number of players he wants for the game
     * @param p is the player to notify
     */
    public void player_disconnected_game_ended(Player p)
    {
        if (numberOfPlayers != null) {
            VCEvent evento = new VCEvent(p.getUsername(), VCEvent.Event.player_disconnected_game_ended);
            for (int i = 0; i < numberOfPlayers; i++) {
                if (!(p.getUsername().equals(players.get(i).getUsername())))
                    serverHandler.sendVCEventTo(evento, i);
            }
        }
    }

    /**
     *It notifies one of the method of the virtual view that has arrived a response from the client
     * @param response
     */
    public synchronized void receivedResponse(Object response)
    {
        received = response;
        notifyAll();
    }

    /***
     * It notifies the method sendUndoRequest of the virtual view that has arrived a response from the client about undo
     * @param response
     */
    public synchronized void undoReceivedResponse(Object response)
    {
        undoReceived = response;
        notifyAll();
    }

    /**
     * It notifies the GameControl that a player has disconnected
     * @param playerIndex
     */
    public synchronized void playerDisconnected(int playerIndex)
    {
        if (connected[playerIndex] == true) {
            System.out.println("Sto scollegando un player...");
            this.connected[playerIndex] = false;
            notifyAll();
            player_disconnected_game_ended(players.get(playerIndex));
        }

    }

    /**
     * Setter for the boolean array connected
     * @param index identifies the client
     */
    public synchronized void setConnectedIndexToTrue(int index) {
       this.connected[index] = true;
       if (index == 0) {
           notifyAll();
       }
        for (int i = 0; i <3 ; i++) {
            System.out.println(connected[i]);
        }
    }


    /**
     * Getter for the number of players chosen by the first client
     * @return
     */
    public Integer getNumberOfPlayers() {
        return numberOfPlayers;
    }

    /**
     * Getter to notify the GameControl that the set-up is ready
     * @return
     */
    public boolean isSetUpisReady()
    {
        return setUpisReady;
    }

    /**
     * Setter for the array of the players
     * @param players
     */
    public void setPlayers(ArrayList<Player> players) {
        this.players = null;
        this.players = new ArrayList<Player>();
        for (int i = 0; i < players.size(); i++) {
            this.players.add(players.get(i));
        }
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }
    public boolean checkConnections()
    {
        int x = 3;
        if (numberOfPlayers != null)
            x = numberOfPlayers;
        for (int i = 0; i <x ; i++) {
            if (this.connected[i] == false) {
                return false;
            }
        }
        return true;
    }

    public void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }

    public boolean isUndoOn() {
        return undoOn;
    }

    public void closeAll(){
        try {
            this.serverHandler.getServer().close();
        }catch (Exception e){}
        this.startNewGame=true;
    }
}
