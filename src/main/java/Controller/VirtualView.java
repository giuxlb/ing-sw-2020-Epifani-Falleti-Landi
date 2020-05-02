package Controller;

import Controller.Network.ServerNetworkHandler;
import Controller.Network.VCEvent;
import Model.*;
import Client.View.Data;
import java.util.ArrayList;
import java.util.Calendar;

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



    private boolean okFromClient;


    /**
     * It creates the ServerNetworkHandler and prepares the information about the firstPlayer for the GameControl
     */
    public VirtualView()
    {
        this.players = new ArrayList<Player>();
        this.connected = new boolean[3];
        this.serverHandler = new ServerNetworkHandler(this);
        Thread serverThread = new Thread(serverHandler);
        serverThread.start();
        this.setUpisReady = false;
        this.okFromClient = false;

    }



    public int playerNumber()
    {
        VCEvent evento = new VCEvent(null, VCEvent.Event.setup_request);
        serverHandler.sendVCEventTo(evento,0);
        synchronized (this) {
            received = null;
            while (received == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
        if (received instanceof Integer) {
            numberOfPlayers = (Integer) received;
            setUpisReady = true;
            System.out.println("continuo a ricevere eventi...");
            serverHandler.setPlayerNumber(numberOfPlayers);
            return numberOfPlayers.intValue();
        }
        return -1;

    }


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
            while(received == null)
            {
                try{
                    wait();
                }
                catch(InterruptedException e){}
            }

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
        VCEvent evento = new VCEvent("Data", VCEvent.Event.date_request);
        serverHandler.sendVCEventTo(evento,index);
        synchronized(this) {
            received = null;
            while(received == null)
            {
                try{
                    wait();
                }
                catch(InterruptedException e){}
            }

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
        VCEvent evento = new VCEvent(playerPlaying, VCEvent.Event.not_your_turn);
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
        VCEvent evento = new VCEvent(board, VCEvent.Event.update);
        for (int i = 0; i <numberOfPlayers ; i++)
            serverHandler.sendVCEventTo(evento,i);


    }

    /**
     * It sends all the cards to the first client connected
     * @param p is the first client connected
     * @param cards are all the cards implemented in the game
     * @return 2 or 3 cards, depending on the number of players of the game
     */
    public ArrayList<String> sendAllCards(Player p, ArrayList<String> cards)
    {
        VCEvent evento = new VCEvent(cards, VCEvent.Event.send_all_cards);
        for (int i = 0; i <numberOfPlayers ; i++) {
            if (p.getUsername().equals(players.get(i).getUsername()))
                serverHandler.sendVCEventTo(evento,i);
        }
        synchronized(this) {
            received = null;
            while(received == null)
            {
                try{
                    wait();
                }
                catch(InterruptedException e){}
            }

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
        VCEvent evento = new VCEvent(chosenCards, VCEvent.Event.send_chosen_cards);
        for (int i = 0; i <numberOfPlayers ; i++) {
            if (p.getUsername().equals(players.get(i).getUsername()))
                serverHandler.sendVCEventTo(evento,i);
        }
        synchronized(this) {
            received = null;
            while(received == null)
            {
                try{
                    wait();
                }
                catch(InterruptedException e){}
            }

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
        VCEvent evento = new VCEvent(move_spots, VCEvent.Event.send_cells_move);
        for (int i = 0; i <numberOfPlayers ; i++) {
            if (p.getUsername().equals(players.get(i).getUsername()))
                serverHandler.sendVCEventTo(evento,i);
        }
        synchronized(this) {
            received = null;
            while(received == null)
            {
                try{
                    wait();
                }
                catch(InterruptedException e){}
            }

        }
        if (received instanceof Integer) {
            int x = ((Integer) received).intValue();
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
        VCEvent evento = new VCEvent(build_spots, VCEvent.Event.send_cells_build);
        for (int i = 0; i <numberOfPlayers ; i++) {
            if (p.getUsername().equals(players.get(i).getUsername()))
                serverHandler.sendVCEventTo(evento,i);
        }
        synchronized(this) {
            received = null;
            while(received == null)
            {
                try{
                    wait();
                }
                catch(InterruptedException e){}
            }

        }
        if (received instanceof Integer) {
            int x = ((Integer) received).intValue();
            return x;
        }


        return -1;

    }

    public Coordinates askForWorker(Player p, ArrayList<Coordinates> workersPosition)
    {
        VCEvent evento = new VCEvent(workersPosition, VCEvent.Event.ask_for_worker);
        for (int i = 0; i <numberOfPlayers ; i++) {
            if (p.getUsername().equals(players.get(i).getUsername()))
                serverHandler.sendVCEventTo(evento,i);
        }
        synchronized(this) {
            received = null;
            while(received == null)
            {
                try{
                    wait();
                }
                catch(InterruptedException e){}
            }

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

    public void player_disconnected_game_ended(Player p)
    {
        VCEvent evento = new VCEvent(p.getUsername(), VCEvent.Event.player_disconnected_game_ended);
        for (int i = 0; i <numberOfPlayers ; i++) {
            if (!(p.getUsername().equals(players.get(i).getUsername())))
                serverHandler.sendVCEventTo(evento,i);
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

    /**
     * It notifies the GameControl that a player has disconnected
     * @param playerIndex
     */
    public synchronized void playerDisconnected(int playerIndex)
    {

            connected[playerIndex] = false;
            notifyAll();

    }

    /**
     * Setter for the boolean array connected
     * @param index identifies the client
     */
    public void setConnectedIndexToTrue(int index) {
       this.connected[index] = true;
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
        this.players = players;
    }


}
