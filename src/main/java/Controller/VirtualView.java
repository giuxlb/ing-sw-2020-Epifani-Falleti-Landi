package Controller;

import Controller.Network.ServerNetworkHandler;
import Controller.Network.VCEvent;
import Model.*;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author Adriano Falleti
 */
public class VirtualView {
    private ServerNetworkHandler serverHandler;
    private ArrayList<Player> players;
    private ArrayList<String> usernames;
    private String firstUsername;
    private Calendar firstDate;
    private Integer numberOfPlayers;
    private Object received;
    private boolean setUpisReady;
    private boolean[] connected;

    /**
     * It creates the ServerNetworkHandler and prepares the information about the firstPlayer for the GameControl
     */
    public VirtualView()
    {
          players = new ArrayList<Player>();
        usernames = new ArrayList<String>();
        connected = new boolean[3];
        serverHandler = new ServerNetworkHandler(this);

        while(true) {
            synchronized (this) {
                received = null;
                while (received == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
                if (received instanceof String) {
                    firstUsername = (String) received;
                    usernames.add(firstUsername);
                    received = null;
                } else if (received instanceof Calendar) {
                    firstDate = (Calendar) received;
                    received = null;
                } else if (received instanceof Integer) {
                    numberOfPlayers = (Integer) received;
                    break;//esce dal loop
                }
            }
        }
        //una volta qui abbiamo settato tutto e settiamo setUpisReady e così il gameControl può leggere
        //firstUsername, firstDate e numberOfPlayers
        setUpisReady = true;



    }

    /**
     * It asks to a client for username
     * @param index identifies the client who will receive the request
     * @return
     */
    public String askForUsername(int index)
    {
        Color colore;
        if (index == 1)
            colore = Color.ANSI_GREEN;
        else {
            colore = Color.ANSI_PURPLE;
        }

        VCEvent evento = new VCEvent(colore, VCEvent.Event.username_request);
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
        if (received instanceof String)
            return (String) received;
        usernames.add((String) received);

        return "";

    }

    /**
     * It asks to a client for username
     * @param index identifies the client who will receive the request
     * @return
     */
    public Calendar askForDate(int index)
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
        if (received instanceof Calendar)
            return (Calendar) received;


        return null;
    }

    /**
     * It notifies the clientl that now it's not their turn
     * @param playersNotPlaying are the players that won't play during this turn
     */
    public void notYourTurn(Player[] playersNotPlaying)
    {
        VCEvent evento = new VCEvent("Wait", VCEvent.Event.not_your_turn);
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
     * It sends to the client the update of the board after a move or a build of another player
     * @param playersToUpdate are the players who need the update
     * @param board is the updated board
     */
    public void upload(Player[] playersToUpdate, Board board)
    {
        VCEvent evento = new VCEvent(board, VCEvent.Event.update);
        for (int i = 0; i <numberOfPlayers ; i++) {
            for (int j = 0; j < numberOfPlayers-1; j++) {
                if (players.get(i).getUsername().equals(playersToUpdate[j].getUsername()))
                {
                    serverHandler.sendVCEventTo(evento,i);
                }
            }
        }
    }

    /**
     * It sends all the cards to the first client connected
     * @param p is the first client connected
     * @param cards are all the cards implemented in the game
     * @return 2 or 3 cards, depending on the number of players of the game
     */
    public ArrayList<Card> sendAllCards(Player p, ArrayList<Card> cards)
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
        ArrayList<Card> chosenCards = (ArrayList<Card>) received;

        return chosenCards;
    }

    /**
     * It sends the cards chosen from the first player to the other clients
     * @param p is the player who will receive the chosen cards from the first client
     * @param chosenCards are the cards chosen
     * @return the card chosen from the player p
     */
    public Card sendChosenCards(Player p,ArrayList<Card> chosenCards)
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
        if (received instanceof Card)
            return (Card) received;

        return null;
    }

    /**
     * It sends the card chosen to the corresponding player
     * @param p is the player
     * @param c is the card chosen by player p
     */
    public void sendYourCard(Player p, Card c)
    {
        VCEvent evento = new VCEvent(c, VCEvent.Event.send_chosen_cards);
        for (int i = 0; i <numberOfPlayers ; i++) {
            if (p.getUsername().equals(players.get(i).getUsername()))
                serverHandler.sendVCEventTo(evento,i);
        }
    }

    /**
     * It asks the initial position to the player p for a particular worker
     * @param p is the player
     * @param worker_Index identifies which worker the player is moving
     * @return the coordiantes chosen for that worker
     */
    public Coordinates askInitialPosition(Player p, int worker_Index)
    {
        Integer workerIndex = Integer.valueOf(worker_Index);
        VCEvent evento = new VCEvent(worker_Index, VCEvent.Event.choose_initial_position);
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
        if (received instanceof Coordinates)
            return (Coordinates) received;
        return null;

    }

    /**
     * It sends to the client a message because it has chosen a wrong initial position
     * @param p is the player
     */
    public void sendMessageWrongPosition(Player p)
    {
        VCEvent evento = new VCEvent("ERRORE", VCEvent.Event.wrongInitialPositionMessage);
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

    /**
     * It notifies the player p that he has lost
     * @param p
     */
    public void youLost(Player p)
    {
        VCEvent evento = new VCEvent("Loser", VCEvent.Event.you_lost);
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
     * Getter for the username of the first client
     * @return
     */
    public String getFirstUsername() {
        return firstUsername;
    }

    /**
     * Getter for the birthdate of the first client
     * @return
     */
    public Calendar getFirstDate() {
        return firstDate;
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
