package Controller.Network;

import java.io.IOException;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Adriano Falleti
 */
public class ClientAdapter implements Runnable {

    private Socket client;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private List<ClientObserver> observers = new ArrayList<ClientObserver>();
    private int number;//from 0 to 2 maximum in case there are 3 players

    /**
     * Constructor for the server adapter
     * @param client
     * @param n
     */
    public ClientAdapter(Socket client,int n){this.client = client;this.number = n;}

    public int getNumber() {
        return number;
    }

    /**
     * It adds an observer to the Client Adapter
     * @param observer
     */
    public void addObserver(ClientObserver observer)
    {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    /**
     * It removes an observer to the Client Adapter
     * @param observer
     */
    public void removeObserver(ClientObserver observer)
    {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    /**
     * It set the output stream used to send messages to the client
     * @param output
     */
    public void setOutput(ObjectOutputStream output) {
        this.output = output;
    }

    /**
     * It set the input stream to receive messages from the client
     * @param input
     */
    public void setInput(ObjectInputStream input) {
        this.input = input;
    }

    /**
     * It starts the handling of the connection with the client
     */
    public void run() {
        List<ClientObserver> observersCpy;
        synchronized (observers) {
            observersCpy = new ArrayList<ClientObserver>(observers);
        }

        try {
            handleClientConnection();
        } catch (IOException e) {
           for(ClientObserver obs : observersCpy)
               obs.playerDisconnectedNumber(number);
        }
        catch (ClassNotFoundException e)
        {
        }
        try {
            client.close();
        } catch (IOException e) { }

    }

    /**
     * It notifies the observers for a VCEvent
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public synchronized void handleClientConnection() throws IOException,ClassNotFoundException
    {
        List<ClientObserver> observersCpy;
        synchronized (observers) {
            observersCpy = new ArrayList<ClientObserver>(observers);
        }

        while (true)
        {

                VCEvent evento = (VCEvent) input.readObject();
                if (evento != null) {
                    if (evento.getCommand() != VCEvent.Event.ping) {
                        for (ClientObserver observer : observersCpy)
                            observer.didReceiveVCEventFrom(evento, this.number);
                    } else {
                        for (ClientObserver observer : observersCpy)
                            observer.didReceivePingFrom((Integer) evento.getBox(), this.number);
                    }
                }



        }
    }

}
