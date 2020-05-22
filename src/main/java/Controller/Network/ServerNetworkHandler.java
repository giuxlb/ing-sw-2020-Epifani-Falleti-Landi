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
import java.net.SocketOption;
/**
 * @author Adriano Falleti
 */

public class ServerNetworkHandler  implements Runnable, ClientObserver {


    public ServerSocket getServer() {
        return server;
    }

    private ServerSocket server;
    private Socket[] clients;
    private ClientAdapter[] adapters;
    private Integer numberOfPlayers;
    public final static int SOCKET_PORT = 65000;
    private ObjectOutputStream[] outputs;
    private ObjectInputStream[] inputs;
    public  VirtualView virtualView;
    private boolean[] canWrite;
    private ClientEventReceiver receiver1;
    private ClientEventReceiver receiver2;
    private ClientEventReceiver receiver3;
    private boolean idIsSent;


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
        //la run deve essere chiamata dalla Virtual View creando un thread e chiamando la start su quel thread
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




        //per ogni client che prendiamo dobbiamo  salvarci l'adapter nell'array adapters e la sua socket nell'array clients e aggiungere il server come observer
        int counter;
       do {
            counter = 0;
            System.out.println(counter);
            receiver1 = new ClientEventReceiver(this,0);
            canWrite[counter] = false;
            this.numberOfPlayers = 0;
           try {
               if (clients[counter] != null) {
                   System.out.println("Client a 0 non è null, la chiudo");
                   clients[counter].close();
                   clients[counter] = null;
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
           try {
                clients[counter] = server.accept();
                System.out.println(clients[counter]);
                clients[counter].setSoTimeout(20000);
                System.out.println("Accetto una connessione");
                virtualView.setConnectedIndexToTrue(counter);
                outputs[counter] = new ObjectOutputStream(clients[counter].getOutputStream());
               System.out.println("L'output stream c'è!");
                inputs[counter] = new ObjectInputStream(clients[counter].getInputStream());
                adapters[counter] = new ClientAdapter(clients[counter], counter);
                adapters[counter].addObserver(this);
                adapters[counter].setInput(inputs[counter]);
                adapters[counter].setOutput(outputs[counter]);
                canWrite[counter] = true;
                adapters[counter].start();
                counter++;
            } catch (IOException e) {
                System.out.println("connection dropped");
            }
            receiver1.canReceiveEvents();
            receiver1.start();

            //qui dovrò aspettare un VCEvent dal primo client connesso con il numero di giocatori perchè poi mi serve per accettare gli altri
            synchronized (this) {
                numberOfPlayers = 1;
                while (numberOfPlayers == 1 && !checkFinishFlags()) {
                    try {
                        wait(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }


        }  while (numberOfPlayers  == 1);
        int n = numberOfPlayers;

        System.out.println(n);
        while (numberOfPlayers > 1) {
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
                adapters[counter].start();
                counter++;
                if (counter == 2) {
                    receiver2.canReceiveEvents();
                    receiver2.start();
                }
                if (counter == 3) {
                    receiver3.canReceiveEvents();
                   receiver3.start();
                }
            }catch(IOException e)
            {
                System.out.println("connection dropped");
            }
            numberOfPlayers--;

        }
        numberOfPlayers = n;

        while (checkFinishFlags() == false)
        {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Chiudo tutto...");
        virtualView.closeAll();

    }

    /**
     * Notifies the threads built with ClientEventReceiver when an event arrives from a client
     * @param eventFromClient is the event arrived
     * @param n is the index that identifies the client who sent the event
     */
    public  void didReceiveVCEventFrom(VCEvent eventFromClient, int n) {
        switch (n)
        {
            case 0:
                receiver1.didReceiveEvent(eventFromClient);
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
    public synchronized void playerDisconnectedNumber(int index) {
        switch (index){
            case 0:
                this.receiver1.setFinishClientReceiver(true);
                this.receiver1.setFinishPing(true);
                this.adapters[index].setFinishClientAdapter(true);
                break;
            case 1:
                this.receiver2.setFinishClientReceiver(true);
                this.receiver2.setFinishPing(true);
                this.adapters[index].setFinishClientAdapter(true);
                break;
            case 2:
                this.receiver3.setFinishClientReceiver(true);
                this.receiver3.setFinishPing(true);
                this.adapters[index].setFinishClientAdapter(true);
                break;
            default:break;
        }
        notifyAll();
        this.virtualView.playerDisconnected(index);
    }

    /**
     * It sends an event to a client
     * @param eventToClient is the event which will be sent to the client
     * @param clientIndex identifies the client who will receive the event
     */
    public synchronized void sendVCEventTo(VCEvent eventToClient, int clientIndex)
    {

            System.out.println("Mando " + eventToClient.getCommand());
            
            synchronized (this) {
                while (this.canWrite[clientIndex] == false) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
            // qui avrò che la canWrite sarà true, quindi lo pongo a false
            this.canWrite[clientIndex] = false;
            if (clientIndex == 0 || outputs[clientIndex] != null) {
            try {
                this.outputs[clientIndex].writeObject(eventToClient);
            } catch (IOException e) {

                System.out.println("Scollego perchè non riesco a mandare un evento");
               // e.printStackTrace();
                this.virtualView.playerDisconnected(clientIndex);
            }
            this.canWrite[clientIndex] = true;
            notifyAll();
        }
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
            while (this.canWrite[clientIndex] == false)
            {
                try {
                    wait(1000);
                }catch(InterruptedException e){}
            }
        }
        // qui avrò che la canWrite sarà true, quindi lo pongo a false
        this.canWrite[clientIndex] = false;

        try {
            this.outputs[clientIndex].writeObject(pingEvent);
        } catch (IOException e) {
            System.out.println("Scollego perchè non riesco a mandare il ping");
           this.virtualView.playerDisconnected(clientIndex);
        }
        this.canWrite[clientIndex] = true;
        notifyAll();

    }
/*
    public synchronized void idSent()
    {
        idIsSent = true;
        notifyAll();
    }

 */
    public synchronized void setPlayerNumber(int number)
    {
        numberOfPlayers = number;
        notifyAll();
    }
    public Socket[] getClients() {
        return clients;
    }

    public ClientAdapter[] getAdapters() {
        return adapters;
    }

    public boolean checkFinishFlags()
    {

        for (int i = 0; i < this.numberOfPlayers ; i++) {

            if (this.adapters[i].isFinishClientAdapter() == true) {
                return true; // ancora non si è scollegato nessuno
            }
        }

        return false;
    }
}
