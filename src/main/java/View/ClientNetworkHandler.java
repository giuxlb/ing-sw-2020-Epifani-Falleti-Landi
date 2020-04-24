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
    private Integer ping;
    private boolean canWrite;

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
        Runnable runPing = ()->{
            while(true)
            {
                synchronized (this)
                {
                    ping = 0;
                    while(ping == 0)
                    {
                        try{
                            wait(10000);
                        }catch(InterruptedException e)
                        { }
                    }
                    if (ping == 0)
                    {
                        //avvisa la virtual view
                    }
                    synchronized (this)
                    {
                        while (canWrite == false)
                        {
                            try{
                                wait();
                            }catch(InterruptedException e){}
                        }
                    }
                    if (canWrite == true)
                        sendPing();//appena riceve manda indietro il ping per fargli sapere che è ancora attivo
                }
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

    public synchronized void didReceivePing(Integer n)
    {
        ping = n;
        notifyAll();;
    }
    //questo metodo verrà chiamato dalla VIEW
    public synchronized void sendVCEvent(VCEvent eventToServer)
    {
        canWrite = false;
        try {
            output.writeObject(eventToServer);
        }catch (IOException e)
        {
            System.out.println("server has died");
        }
        canWrite = true;
        notifyAll();
    }


    public void sendPing()
    {
        canWrite = false;
        VCEvent pingEventResponse = new VCEvent(ping, VCEvent.Event.ping);
        try {
            output.writeObject(pingEventResponse);
        }catch (IOException e)
        {
            System.out.println("server has died for ping");
        }
        canWrite = true;
        notifyAll();
    }


}
