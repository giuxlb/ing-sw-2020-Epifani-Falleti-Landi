package Controller;

import Model.*;
import jdk.jfr.Event;

import java.util.ArrayList;
import java.util.Calendar;

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


    public VirtualView()
    {
        players = new ArrayList<Player>();
        usernames = new ArrayList<String>();
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
        received = null;


    }


    public String askForUsername(int index)
    {
        Color colore;
        if (index == 1)
            colore = Color.WHITE;
        else {
            colore = Color.BROWN;
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
        received = null;
        return "";

    }

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
        received = null;

        return null;
    }

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
        received = null;
        return chosenCards;
    }

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
        received = null;
        return null;
    }

    public Integer sendAvailableMove(Player p, ArrayList<BoardCell> move_spots)
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
        if (received instanceof Integer)
            return (Integer) received;
        received = null;

        return -1;

    }

    public Integer sendAvailableBuild(Player p, ArrayList<BoardCell> build_spots)
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
        if (received instanceof Integer)
            return (Integer) received;
        received = null;

        return -1;

    }

    public void youLost(Player p)
    {
        VCEvent evento = new VCEvent("Loser", VCEvent.Event.you_lost);
        for (int i = 0; i <numberOfPlayers ; i++) {
            if (p.getUsername().equals(players.get(i).getUsername()))
                serverHandler.sendVCEventTo(evento,i);
        }

    }

    public void youWon(Player p)
    {
        VCEvent evento = new VCEvent("Winner", VCEvent.Event.you_won);
        for (int i = 0; i <numberOfPlayers ; i++) {
            if (p.getUsername().equals(players.get(i).getUsername()))
                serverHandler.sendVCEventTo(evento,i);
        }
    }




    public synchronized void receivedResponse(Object response)
    {
        received = response;
        notifyAll();
    }

    public  void playerDisconnected(int playerIndex)
    {

            connected[playerIndex] = false;
            notifyAll();

    }

    public void setConnectedIndexToTrue(int index) {
       this.connected[index] = true;
    }

    public String getFirstUsername() {
        return firstUsername;
    }

    public Calendar getFirstDate() {
        return firstDate;
    }

    public Integer getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public boolean isSetUpisReady()
    {
        return setUpisReady;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }


}
