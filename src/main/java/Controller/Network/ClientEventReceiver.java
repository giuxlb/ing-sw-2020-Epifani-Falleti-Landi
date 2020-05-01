package Controller.Network;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Adriano Falleti
 */
public class ClientEventReceiver implements Runnable {

    private VCEvent fromClient;
    private Integer pingFromClient;
    private ServerNetworkHandler snh;
    private Integer clientIndex;
    private boolean startReceivingEvents;

    /**
     * Constructor for ClientEventReceiver
     * @param server
     * @param index
     */
    public ClientEventReceiver(ServerNetworkHandler server,int index)
    {
        this.clientIndex = index;
        this.snh = server;
        startReceivingEvents = false;
    }

    @Override
    public void run() {

        Runnable runPingClient = ()->{
            while(true)
            {
                VCEvent pingEvent = new VCEvent( clientIndex+1, VCEvent.Event.ping);

                snh.sendPingTo(pingEvent, clientIndex);

                synchronized (this) {
                    pingFromClient = 0;
                    while (pingFromClient == 0) {
                        try {
                            wait(10000);
                        } catch (InterruptedException e) {
                        }
                    }
                }
                if(pingFromClient == 0)
                {
                    snh.virtualView.playerDisconnected(clientIndex);

                }
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }

        };
        Thread pingThread = new Thread(runPingClient);
        pingThread.start();
        sendID(clientIndex.intValue());


        synchronized (this)
        {
            while(startReceivingEvents == false)
            {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }

        while (true)
        {
            synchronized (this)
            {
                fromClient = null;
                while(fromClient == null)
                {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        System.out.println("Errore");
                        e.printStackTrace();
                    }
                }

            }
            System.out.println("Ho ricevuto "+fromClient.getBox());
            // se il client ha perso,ha vinto oppure riceve il messaggio che un giocatore si è disconnesso e quindi la partita è finita
            if ((fromClient.getCommand() == VCEvent.Event.you_lost) || (fromClient.getCommand() == VCEvent.Event.you_won) || (fromClient.getCommand() == VCEvent.Event.player_disconnected_game_ended))
            {
                try {
                    snh.getClients()[clientIndex].close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                snh.virtualView.receivedResponse(fromClient.getBox());
            }

        }


    }


    /**
     * It allows the receiver to receive events
     */
    public synchronized void canReceiveEvents()
    {
        startReceivingEvents = true;
        notifyAll();
    }

    /**
     * It notifies the receiver that a event is arrived
     * @param event is the event arrived from the client
     */
    public synchronized void didReceiveEvent(VCEvent event)
    {
        fromClient = event;
        notifyAll();
    }

    /**
     * It notifies the receiver that a event is arrived
     * @param ping is the ping that server and clients send to each other
     */
    public synchronized void didReceivePing(Integer ping)
    {
        pingFromClient = ping;
        notifyAll();
    }

    public void sendID(int index)
    {
        while(true) {
            snh.sendVCEventTo(new VCEvent(index, VCEvent.Event.id), index);
            synchronized (this) {
                fromClient = null;
                while (fromClient == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
            if (fromClient.getBox().equals("OK"))
                break;
        }
        snh.idSent();
    }
}


