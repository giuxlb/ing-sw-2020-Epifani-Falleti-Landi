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

public class ServerNetworkHandler implements Runnable, ClientObserver {


    private static ServerSocket server;
    private static Socket[] clients;
    private static ClientAdapter[] adapters;
    private static VCEvent fromClient1;
    private static VCEvent fromClient2;
    private static VCEvent fromClient3;
    private static String[] usernames;
    private static Integer numberOfPlayers;
    public final static int SOCKET_PORT = 7777;
    private static  ObjectOutputStream[] outputs;
    private static ObjectInputStream[] inputs;
    private static Integer pingFromClient1;
    private static Integer pingFromClient2;
    private static Integer pingFromClient3;
    private static VirtualView virtualView;
    private static boolean[] canWrite;

    public ServerNetworkHandler(VirtualView vv)
    {
        clients = new Socket[3];
        adapters = new ClientAdapter[3];
        outputs = new ObjectOutputStream[3];
        inputs = new ObjectInputStream[3];
        virtualView = vv;

        this.run();
    }


    public void run() {
        try{
            server = new ServerSocket(SOCKET_PORT);
        }catch(IOException E)
        {
            System.out.println("cannot open server socket");
            System.exit(1);
            return;
        }
        Runnable runClient1 = ()->{
            while(true)
            {
                synchronized (this)
                {
                    fromClient1 = null;
                    while (fromClient1 == null) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }
                    //qui ho un evento dal client 1 da mandare alla VirtualView
                    virtualView.receivedResponse(fromClient1.getBox());
            }

        };

        Runnable runPingClient1 = ()->{
            while(true)
            {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                VCEvent pingEvent = new VCEvent((Integer) 1, VCEvent.Event.ping);
                sendPingTo(pingEvent,0);
                synchronized (this)
                {
                    pingFromClient1 = 0;
                    while (pingFromClient1 == 0) {
                        try {
                            wait(10000);
                        } catch (InterruptedException e) {
                        }
                    }
                }
                if(pingFromClient1 == 0)
                {
                    virtualView.playerDisconnected(0);
                }



            }

        };
        Runnable runClient2 = ()->{
            while (true) {
                synchronized (this)
                {
                    fromClient2 = null;
                    while (fromClient2 == null) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                        }
                    }
                }
                virtualView.receivedResponse(fromClient2.getBox());
            }

        };

        Runnable runPingClient2 = ()->{
            while(true)
            {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                VCEvent pingEvent = new VCEvent((Integer) 2, VCEvent.Event.ping);
                sendPingTo(pingEvent,1);
                synchronized (this)
                {
                    pingFromClient2 = 0;
                    while (pingFromClient2 == 0) {
                        try {
                            wait(10000);
                        } catch (InterruptedException e) { }
                    }
                }
                    if(pingFromClient2 == 0)
                    {
                        virtualView.playerDisconnected(1);
                    }
                    System.out.println("Pong1");
            }

        };
        Runnable runClient3 = ()->{
            while(true) {
                synchronized (this)
                {
                    fromClient3 = null;
                    while (fromClient3 == null) {
                        try {
                            wait();
                        } catch (InterruptedException e) { }
                    }
                }
                virtualView.receivedResponse(fromClient3.getBox());
            }

        };
        Runnable runPingClient3 = ()->{
            while(true)
            {
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                VCEvent pingEvent = new VCEvent((Integer) 3, VCEvent.Event.ping);
                sendPingTo(pingEvent,2);
                synchronized (this)
                {
                    pingFromClient3 = 0;
                    while (pingFromClient3 == 0) {
                        try {
                            wait(10000);
                        } catch (InterruptedException e) {
                        }
                    }
                }
                    if(pingFromClient3 == 0)
                    {
                        virtualView.playerDisconnected(2);
                    }




            }

        };
        Thread th1 = new Thread(runClient1);
        Thread th2 = new Thread(runClient2);
        Thread th3 = new Thread(runClient3);
        Thread thPing1 = new Thread(runPingClient1);
        Thread thPing2 = new Thread(runPingClient2);
        Thread thPing3 = new Thread(runPingClient3);
        //per ogni client che prendiamo dobbiamo creare salvarci l'adapter nell'array adapters e la sua socket nell'array clientse aggiungere il server come observer
        int counter = 0;
        try {
            clients[counter] = server.accept();
            clients[counter].setSoTimeout(20000);
            virtualView.setConnectedIndexToTrue(counter);
            outputs[counter] = (ObjectOutputStream) clients[counter].getOutputStream();
            inputs[counter] = (ObjectInputStream) clients[counter].getInputStream();
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
        thPing1.start();
        //qui dovrò aspettare un VCEvent dal primo client connesso con il numero di giocatori perchè poi mi serve per accettare gli altri
        VCEvent e1 = new VCEvent(Color.ANSI_YELLOW , VCEvent.Event.setup_request);
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
        th1.start();

        while (numberOfPlayers != 1) {
            try{
                clients[counter] = server.accept();
                clients[counter].setSoTimeout(20000);
                virtualView.setConnectedIndexToTrue(counter);
                outputs[counter] = (ObjectOutputStream) clients[counter].getOutputStream();
                inputs[counter] = (ObjectInputStream) clients[counter].getInputStream();
                adapters[counter] = new ClientAdapter(clients[counter],counter);
                adapters[counter].addObserver(this);
                adapters[counter].setInput(inputs[counter]);
                canWrite[counter] = true;
                adapters[counter].setOutput(outputs[counter]);
                Thread thread = new Thread(adapters[counter]);
                thread.start();
                counter++;
                if (counter == 2) {
                    thPing2.start();
                    th2.start();
                }
                if (counter == 3) {
                    thPing3.start();
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


    public synchronized void didReceiveVCEventFrom(VCEvent eventFromClient, int n) {
        switch (n)
        {
            case 0:
                fromClient1 = eventFromClient;
                notifyAll();
                break;
            case 1:
                fromClient2 = eventFromClient;
                notifyAll();
                break;
            case 2:
                fromClient3 = eventFromClient;
                notifyAll();
            default:
                break;
        }

    }

    public synchronized void didReceivePingFrom(Integer p,int n)
    {
        switch (n)
        {
            case 0:
                pingFromClient1 = p;
                notifyAll();
                break;
            case 1:
                pingFromClient2 = p;
                notifyAll();
                break;
            case 2:
                pingFromClient3 = p;
                notifyAll();
            default:
                break;
        }

    }

    @Override
    public void playerDisconnectedNumber(int index) {
        virtualView.playerDisconnected(index);
    }

    public void sendVCEventTo(VCEvent eventToClient, int clientIndex)
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

    public void sendPingTo(VCEvent pingEvent, int clientIndex)
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
