package View;

import Controller.VCEvent;
import Controller.VCEvent.Event;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class ServerAdapter implements Runnable {



    private Socket server;
    private ObjectOutputStream output;
    private ObjectInputStream input;


    private List<ServerObserver> observers = new ArrayList<ServerObserver>();

    public ServerAdapter(Socket server)
    {
        this.server = server;
    }

    public void setOutput(ObjectOutputStream output) {
        this.output = output;
    }

    public void setInput(ObjectInputStream input) {
        this.input = input;
    }

    public void run()
    {
        List<ServerObserver> observersCpy;
        synchronized (observers) {
            observersCpy = new ArrayList<ServerObserver>(observers);
        }
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

    private synchronized void handleServerConnection() throws IOException, ClassNotFoundException
    {
        List<ServerObserver> observersCpy;
        synchronized (observers) {
            observersCpy = new ArrayList<ServerObserver>(observers);
        }


        while (true) {

                VCEvent evento = (VCEvent) input.readObject();
                if (evento != null) {
                    if (evento.getCommand() != Event.ping)
                        for (ServerObserver observer : observersCpy)
                            observer.didReceiveVCEvent(evento);
                    else {
                        for (ServerObserver observer : observersCpy)
                            observer.didReceivePing((Integer) evento.getBox());
                    }
                }


        }



    }



    public void addObserver(ServerObserver observer)
    {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    public void removeObserver(ServerObserver observer)
    {
        synchronized (observers) {
            observers.remove(observer);
        }
    }



}

