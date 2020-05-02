package Client.View;

import Controller.Network.VCEvent;
import Controller.Network.VCEvent.Event;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

/**
 * @author Adriano Falleti
 */
public class ServerAdapter implements Runnable {



    private Socket server;
    private ObjectOutputStream output;
    private ObjectInputStream input;



    private boolean continueReading;


    private List<ServerObserver> observers = new ArrayList<ServerObserver>();

    public ServerAdapter(Socket server)
    {
        this.server = server;
    }

    /**
     * It set the output stream used to send messages to the server
     * @param output
     */
    public void setOutput(ObjectOutputStream output) {
        this.output = output;
    }

    /**
     * It set the input stream to receive messages from the server
     * @param input
     */
    public void setInput(ObjectInputStream input) {
        this.input = input;
    }
    /**
     * It starts the handling of the connection with the server
     */
    public void run()
    {
        List<ServerObserver> observersCpy;
        synchronized (observers) {
            observersCpy = new ArrayList<ServerObserver>(observers);
        }
        continueReading = true;
        try {
            handleServerConnection();
        } catch (IOException e) {
            for(ServerObserver obs: observersCpy)
                obs.serverDied();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            server.close();
        } catch (IOException e) { }
    }

    /**
     * It notifies the observers for a VCEvent
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private synchronized void handleServerConnection() throws IOException, ClassNotFoundException
    {
        List<ServerObserver> observersCpy;
        synchronized (observers) {
            observersCpy = new ArrayList<ServerObserver>(observers);
        }


        while (true)
        {

                VCEvent evento = (VCEvent) input.readObject();
                continueReading = false;
                if (evento != null) {
                    if (evento.getCommand() != Event.ping) {
                        System.out.println("Adapter vede " + evento.getCommand());
                        for (ServerObserver observer : observersCpy)
                            observer.didReceiveVCEvent(evento);
                         waitToContinue();
                    }
                    else {
                        for (ServerObserver observer : observersCpy)
                            observer.didReceivePing((Integer) evento.getBox());
                    }
                }


        }



    }


    /**
     * It adds an observer to the Client Adapter
     * @param observer
     */
    public void addObserver(ServerObserver observer)
    {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    /**
     * It removes an observer to the Client Adapter
     * @param observer
     */
    public void removeObserver(ServerObserver observer)
    {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    public void waitToContinue()
    {
        synchronized (this)
        {
            while(continueReading == false)
            {
                try
                {
                    wait();
                }catch(InterruptedException e){}
            }
        }
    }
    public synchronized void continueToRead()
    {
        continueReading = true;
        notifyAll();
    }

}

