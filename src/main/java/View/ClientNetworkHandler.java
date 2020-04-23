package View;

import Controller.VCEvent;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.io.IOException;


public class ClientNetworkHandler implements Runnable, ServerObserver {

    private VCEvent fromServer;
    private ServerAdapter adapter;
    private Socket server;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean endGame;
    private String winner;
    private boolean updateView;

    public ClientNetworkHandler(){this.run();}

    public boolean isUpdateView() {
        return updateView;
    }

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
        adapter = new ServerAdapter(server);
        adapter.addObserver(this);
        adapter.setInput(input);
        adapter.setOutput(output);
        Thread thread = new Thread(adapter);
        thread.start();

        while (true)
        {
            synchronized (this)
            {
                fromServer = null;
                while (fromServer == null) {
                    updateView = false;
                    try {
                        wait();
                    } catch (InterruptedException e) { }

                }
                updateView = true;
                //qui l'evento dal server sarà arrivato e ora devo gestirlo con una switch sul suo comando per chiamare il metodo
                //della view corrispondente. Poi la view chiamerà il metodo sendVCEvent passandogli il VCEvent da mandare al Server

                //UNA SOLUZIONE ALTERNATIVA POTREBBE ESSERE ANCHE MANDARE DIRETTAMENTE L'EVENTO ALLA VIEW, MA DIPENDE
                //DA COME ALFREDO VUOLE IMPLEMENTARE LA CLI/GUI
            }
        }

    }


    public synchronized void didReceiveVCEvent(VCEvent eventFromServer) {
        fromServer = eventFromServer;
        notifyAll();
    }

    //questo metodo verrà chiamato dalla VIEW
    public void sendVCEvent(VCEvent eventToServer)
    {
        try {
            output.writeObject(eventToServer);
        }catch (IOException e)
        {
            System.out.println("server has died");
        }
    }
}
