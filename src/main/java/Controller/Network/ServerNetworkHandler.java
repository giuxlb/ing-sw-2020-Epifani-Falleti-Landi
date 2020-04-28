package Controller.Network;


import Controller.VirtualView;
import Model.Color;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * @author Adriano Falleti
 */

public class ServerNetworkHandler implements Runnable, ClientObserver {


    private static ServerSocket server;
    private static Socket[] clients;
    private static ClientAdapter[] adapters;
    private static VCEvent fromClient1;
    private static Integer numberOfPlayers;
    public final static int SOCKET_PORT = 7777;
    private static  ObjectOutputStream[] outputs;
    private static ObjectInputStream[] inputs;
    public static VirtualView virtualView;
    private static boolean[] canWrite;
    private static ClientEventReceiver receiver1;
    private static ClientEventReceiver receiver2;
    private static ClientEventReceiver receiver3;

    public ServerNetworkHandler(VirtualView vv)
    {
        clients = new Socket[3];
        adapters = new ClientAdapter[3];
        outputs = new ObjectOutputStream[3];
        inputs = new ObjectInputStream[3];
        canWrite = new boolean[3];
        virtualView = vv;
        receiver1 = new ClientEventReceiver(this,0);
        receiver2 = new ClientEventReceiver(this,1);
        receiver3 = new ClientEventReceiver(this,2);
        //la run deve essere chiamata dal gamecontrol creando un thread e chiamando la start su quel thread
    }

    /**
     * It accepts the first client from who receive the number of players for the game and then set up the server
     * to communicate with clients
     */
    public void run() {
        try{
            server = new ServerSocket(SOCKET_PORT);
        }catch(IOException E)
        {
            System.out.println("cannot open server socket");
            System.exit(1);
            return;
        }

        Thread th1 = new Thread(receiver1);
        Thread th2 = new Thread(receiver2);
        Thread th3 = new Thread(receiver3);

        //per ogni client che prendiamo dobbiamo creare salvarci l'adapter nell'array adapters e la sua socket nell'array clientse aggiungere il server come observer
        int counter = 0;
        try {
            clients[counter] = server.accept();
            clients[counter].setSoTimeout(20000);
            virtualView.setConnectedIndexToTrue(counter);
            outputs[counter] = new ObjectOutputStream(clients[counter].getOutputStream());
            inputs[counter] = new ObjectInputStream( clients[counter].getInputStream());
            adapters[counter] = new ClientAdapter(clients[counter], counter);
            adapters[counter].addObserver(this);
            adapters[counter].setInput(inputs[counter]);
            adapters[counter].setOutput(outputs[counter]);
            canWrite[counter] = true;
            Thread thread1 = new Thread(adapters[counter]);
            thread1.start();
            counter++;
        }
        catch(IOException e)
        {
            System.out.println("connection dropped");
        }

        //qui dovrò aspettare un VCEvent dal primo client connesso con il numero di giocatori perchè poi mi serve per accettare gli altri
        VCEvent e1 = new VCEvent(Color.ANSI_YELLOW , VCEvent.Event.setup_request);
        th1.start();
        while(true){
            sendVCEventTo(e1,0);

            synchronized (this)
            {
                fromClient1 = null;
                while (fromClient1 == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) { }
                }
            }
            if (fromClient1.getBox() instanceof String) {
                virtualView.receivedResponse(fromClient1.getBox());
                e1 = null;
                e1 = new VCEvent("Data", VCEvent.Event.setup_request);
            }
            else if (fromClient1.getBox() instanceof Calendar)
            {
                virtualView.receivedResponse(fromClient1.getBox());
                e1 = null;
                e1 = new VCEvent("NumeroGiocatori", VCEvent.Event.setup_request);
            }
            if (fromClient1.getBox() instanceof Integer) // se il box è un Integer allora vuol dire che ci avrà mandato il numero di giocatori
                break;
        }
        //a questo punto ho in fromClient1 il numero dei giocatori
        virtualView.receivedResponse(fromClient1.getBox());
        numberOfPlayers = (Integer) fromClient1.getBox();
        receiver1.canReceiveEvents();

        while (numberOfPlayers != 1) {
            try{
                clients[counter] = server.accept();
                clients[counter].setSoTimeout(20000);
                virtualView.setConnectedIndexToTrue(counter);
                outputs[counter] = new ObjectOutputStream( clients[counter].getOutputStream());
                inputs[counter] = new ObjectInputStream( clients[counter].getInputStream());
                adapters[counter] = new ClientAdapter(clients[counter],counter);
                adapters[counter].addObserver(this);
                adapters[counter].setInput(inputs[counter]);
                canWrite[counter] = true;
                adapters[counter].setOutput(outputs[counter]);
                Thread thread = new Thread(adapters[counter]);
                thread.start();
                counter++;
                if (counter == 2) {
                    receiver2.canReceiveEvents();
                    th2.start();
                }
                if (counter == 3) {
                    receiver3.canReceiveEvents();
                    th3.start();
                }
            }catch(IOException e)
            {
                System.out.println("connection dropped");
            }
            numberOfPlayers--;

        }
        //una volta arrivato qui devo ritornare alla VirtualView il setup con il primo nome  e il numero di giocatori
        //poi la VirtualView dovrà chiedermi di chiedergli lo username e io avrò già tutto pronto per mandare la richiesta e ricever
        //la risposta




    }

    /**
     * Notifies the threads built with ClientEventReceiver when an event arrives from a client
     * @param eventFromClient is the event arrived
     * @param n is the index that identifies the client who sent the event
     */
    public synchronized void didReceiveVCEventFrom(VCEvent eventFromClient, int n) {
        switch (n)
        {
            case 0:
                fromClient1 = eventFromClient;
                receiver1.didReceiveEvent(eventFromClient);
                notifyAll();
                break;
            case 1:
               receiver2.didReceiveEvent(eventFromClient);
                break;
            case 2:
                receiver3.didReceiveEvent(eventFromClient);
            default:
                break;
        }

    }

    /**
     *
     * @param p is the integer sent between the nth client and server
     * @param n is the index that identifies the client who sent the ping
     */
    public void didReceivePingFrom(Integer p,int n)
    {
        switch (n)
        {
            case 0:
              receiver1.didReceivePing(p);
                break;
            case 1:
               receiver2.didReceivePing(p);
                break;
            case 2:
                receiver3.didReceivePing(p);
            default:
                break;
        }

    }

    /**
     * It notifies the Virtual view that a player has just disconnected from the server
     * @param index identifies the client disconnected
     */
    @Override
    public void playerDisconnectedNumber(int index) {
        virtualView.playerDisconnected(index);
    }

    /**
     * It sends an event to a client
     * @param eventToClient is the event which will be sent to the client
     * @param clientIndex identifies the client who will receive the event
     */
    public synchronized void sendVCEventTo(VCEvent eventToClient, int clientIndex)
    {
        synchronized (this)
        {
            while (canWrite[clientIndex] == false)
            {
                try {
                    wait();
                }catch(InterruptedException e){}
            }
        }
        // qui avrò che la canWrite sarà true, quindi lo pongo a false
        canWrite[clientIndex] = false;

        try {
            outputs[clientIndex].writeObject(eventToClient);
        } catch (IOException e) {
            System.out.println("client has died");
        }
        canWrite[clientIndex] = true;
        notifyAll();
    }

    /**
     * It sends the ping to a client
     * @param pingEvent is the event with the attribute command set to .ping
     * @param clientIndex identifies the client who will receive the ping
     */
    public synchronized void sendPingTo(VCEvent pingEvent, int clientIndex)
    {
        synchronized (this)
        {
            while (canWrite[clientIndex] == false)
            {
                try {
                    wait();
                }catch(InterruptedException e){}
            }
        }
        // qui avrò che la canWrite sarà true, quindi lo pongo a false
        canWrite[clientIndex] = false;

        try {
            outputs[clientIndex].writeObject(pingEvent);
        } catch (IOException e) {
            System.out.println("client has died");
        }
        canWrite[clientIndex] = true;
        notifyAll();

    }







}
