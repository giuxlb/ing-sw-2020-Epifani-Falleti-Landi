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



    public void run()
    {
        try {
            output = new ObjectOutputStream(server.getOutputStream());
            input = new ObjectInputStream(server.getInputStream());
            handleServerConnection();
        } catch (IOException e) {
            System.out.println("server has died");
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

        while (true)
        {
            VCEvent evento = (VCEvent) input.readObject();
            if (evento != null)
                for (ServerObserver observer : observersCpy)
                    observer.didReceiveVCEvent(evento);
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

