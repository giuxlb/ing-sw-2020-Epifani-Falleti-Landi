package Client.View;

import Client.View.GUI.GUIHandler;
import Controller.Coordinates;
import Controller.Network.VCEvent;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Adriano Falleti
 */
public class ClientNetworkHandler implements Runnable, ServerObserver {


    private VCEvent fromServer;
    private ServerAdapter adapter;
    private Socket server;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean endGame;
    private String winner;
    private boolean updateView;
    private Integer ping; // can be 1,2 or 3
    private boolean canWrite;
    private boolean serverIsDied;
    private int PlayerID;//can be 0,1 or 2
    private boolean isRead;
    private CLI cli;
    private boolean finish;
    private boolean finishPing;
    private GUIHandler gh;
    private String ip;


    private boolean idArrived;

    public ClientNetworkHandler(CLI cli,String ip){this.cli = cli;this.ip = ip;}

    public ClientNetworkHandler(GUIHandler gh,String ip){
        this.gh=gh;
        this.ip = ip;
    }

    public boolean isUpdateView() {
        return updateView;
    }
    public boolean isIdArrived() {
        return idArrived;
    }

    /**
     * It sets up the connection with the server and prepare the client to receive ping and events from the server and to answer those
     *
     */

    public void run() {

        try {
            server = new Socket(ip,65000);
            output = new ObjectOutputStream(server.getOutputStream());
            input = new ObjectInputStream((server.getInputStream()));
        } catch (IOException e) {
            System.out.println("Server unreachable, because first client left the game before setting up the number of players!");
            return;
        }
        System.out.println("Connected");
        /*qua creo un'istanza della View chiamando il suo costruttore e passandogli questo network handler
        in modo tale da poi permetterci di chiamare i metodi della View sotto che dovranno gestire l'input e l'output
        */
        //22 07 1997
        finish = false;
        finishPing = false;
        Runnable runPing = ()->{
            while(!finishPing)
            {
                synchronized (this) {
                    ping = 0;
                    while (ping == 0 && !finish) {
                        try {
                            wait(10000);
                        } catch (InterruptedException e) {
                        }
                    }
                    if (ping == 0) {

                    }
                }
                if (!finishPing)
                    sendPing();//appena riceve manda indietro il ping per fargli sapere che è ancora attivo


            }
        };
        adapter = new ServerAdapter(server);
        adapter.addObserver(this);
        adapter.setInput(input);
        adapter.setOutput(output);
        adapter.start();
        Thread threadPing = new Thread(runPing);
        threadPing.start();
        canWrite = true;

        while (!finish)
        {
            isRead = false;
            synchronized (this) {

                while (fromServer == null && finish == false) {
                    try {
                        wait();
                    } catch (InterruptedException e) { }

                }
            }
            if (!finish){
                if(this.cli!=null){

                    cli.updateGo();
                }else if(this.gh!=null){
                    System.out.println("Ho chiamato la update go di GH 1");
                    this.gh.updateGo();
                    System.out.println("Ho chiamato la update go di GH 2");
                }else{
                    System.out.println("Errore nella costruzione delle interfacce");
                }
            }

            //System.out.println("è arrivato il comando"+ fromServer.getCommand());
            synchronized (this){
                while(isRead == false && finish == false)
                {
                    try {
                        wait();
                    } catch (InterruptedException e) { }
                }
            }
            //System.out.println("é arrivato alla view l'evento"+ fromServer.getCommand());
            fromServer = null;
            if (!finish)
                adapter.continueToRead();


        }
        finishPing = true;
        adapter.setFinishAdapter(true);
        adapter.continueToRead();//let the adapter continue to read, so that it will go to the condition of the while loop and since the finishAdapter is true, it will end the handleServerConnection method

    }

    /**
     * It notifies the Client Network Handler that an event has arrived from the server
     * @param eventFromServer is the event
     */
    public synchronized void didReceiveVCEvent(VCEvent eventFromServer) {
        fromServer = eventFromServer;
        notifyAll();
    }

    /**
     * It notifies the Client Network Handler that a ping has arrived from the server
     * @param n is the integer exchanged between the client and the server
     */
    public synchronized void didReceivePing(Integer n)
    {
        ping = n;
        notifyAll();
    }

    /**
     * It sets the attribute serverDied to true, which will be observed by the view in order to alert the client
     */
    @Override
    public void serverDied() {
        serverIsDied = true;
    }

    /**
     * It send a VCEvent to the server
     * @param eventToServer is the event sent to the server
     */
    public synchronized void sendVCEvent(VCEvent eventToServer)
    {
        synchronized (this)
        {
            while (canWrite == false)
            {
                try {
                    wait();
                }catch(InterruptedException e){}
            }
        }
        // qui avrò che la canWrite sarà true, quindi lo pongo a false
        canWrite = false;
        //System.out.println("Mando l'evento "+ eventToServer.getBox());
        try {
            output.writeObject(eventToServer);
        } catch (IOException e) {

        }
        canWrite = true;
        notifyAll();
    }

    public synchronized void readByView()
    {
        isRead = true;
        notifyAll();
    }
    /**
     * It answers to the ping sent by the server
     */
    public synchronized void sendPing()
    {
        synchronized (this)
        {
            while (canWrite == false)
            {
                try {
                    wait();
                }catch(InterruptedException e){}
            }
        }
        // qui avrò che la canWrite sarà true, quindi lo pongo a false
        canWrite = false;

        VCEvent pingEventResponse = new VCEvent(ping, VCEvent.Event.ping);
        try {
            output.writeObject(pingEventResponse);
        } catch (IOException e) {

        }

        canWrite = true;
        notifyAll();


    }

    /**
     * Getter for the event from Server
     * @return
     */
    public VCEvent getFromServer() {
        return fromServer;
    }

    /**
     * Getter for the attribute endGame
     * @return
     */
    public boolean isEndGame() {
        return endGame;
    }

    /**
     * Getter for the attribute serverIsDied
     * @return
     */
    public boolean isServerIsDied() {
        return serverIsDied;
    }

    /**
     * Getter for the attribute winner
     * @return
     */
    public String getWinner() {
        return winner;
    }

    public int getPlayerID() {
        return PlayerID;
    }

    public synchronized void setFinish(boolean finish) {
        this.finish = finish;
        notifyAll(); // if the clientNetworkHandler is waiting for an event from server, this will wake up  that wait at line 107
    }
}
