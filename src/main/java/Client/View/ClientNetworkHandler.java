package Client.View;

import Controller.Network.VCEvent;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.io.IOException;

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
    private int playerID;//can be 0,1 or 2



    public boolean isUpdateView() {
        return updateView;
    }

    /**
     * It sets up the connection with the server and prepare the client to receive ping and events from the server and to answer those
     *
     */
    public void run() {

        try {
            server = new Socket("127.0.0.1",7777);
            output = new ObjectOutputStream(server.getOutputStream());
            input = new ObjectInputStream((server.getInputStream()));
        } catch (IOException e) {
            System.out.println("server unreachable");
            return;
        }
        System.out.println("Connected");
        /*qua creo un'istanza della View chiamando il suo costruttore e passandogli questo network handler
        in modo tale da poi permetterci di chiamare i metodi della View sotto che dovranno gestire l'input e l'output
        */
        Runnable runPing = ()->{
            while(true)
            {
                synchronized (this) {
                    ping = 0;
                    while (ping == 0) {
                        try {
                            wait(10000);
                        } catch (InterruptedException e) {
                        }
                    }
                    if (ping == 0) {

                    }
                }
                    sendPing();//appena riceve manda indietro il ping per fargli sapere che è ancora attivo

            }
        };
        adapter = new ServerAdapter(server);
        adapter.addObserver(this);
        adapter.setInput(input);
        adapter.setOutput(output);
        Thread thread = new Thread(adapter);
        thread.start();
        Thread threadPing = new Thread(runPing);
        threadPing.start();
        canWrite = true;

        while (true)
        {
            synchronized (this) {
                    fromServer = null;
                    while (fromServer == null) {
                        updateView = false;
                        try {
                            wait();
                        } catch (InterruptedException e) {
                        }

                    }
            }
                updateView = true;

                //qui l'evento dal server sarà arrivato e ora devo gestirlo con una switch sul suo comando per chiamare il metodo
                //della view corrispondente. Poi la view chiamerà il metodo sendVCEvent passandogli il VCEvent da mandare al Server

                //UNA SOLUZIONE ALTERNATIVA POTREBBE ESSERE ANCHE MANDARE DIRETTAMENTE L'EVENTO ALLA VIEW, MA DIPENDE
                //DA COME ALFREDO VUOLE IMPLEMENTARE LA CLI/GUI

        }

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


        playerID = n.intValue()-1;
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

        try {
            output.writeObject(eventToServer);
        } catch (IOException e) {
            System.out.println("server has died for vcevent");
        }
        canWrite = true;
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
            System.out.println("server has died for ping");
        }

        canWrite = true;
        notifyAll();


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

    /***
     * Get playerID
     * @return ID of current player of the client
     */
    public int getPlayerID() {
        return playerID;
    }

    /***
     *
     * @return
     */
    public VCEvent getFromServer() {
        return fromServer;
    }
}
