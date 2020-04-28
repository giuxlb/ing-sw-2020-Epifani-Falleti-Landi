package Controller.Network;

import java.util.concurrent.TimeUnit;


public class ClientEventReceiver implements Runnable {

    private VCEvent fromClient;
    private Integer pingFromClient;
    private ServerNetworkHandler snh;
    private Integer clientIndex;
    private boolean startReceivingEvents;


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
                    }
                }

            }
            snh.virtualView.receivedResponse(fromClient.getBox());
           // System.out.println("Il client "+(this.clientIndex+1)+" ha scritto "+ fromClient.getBox());
        }


    }



    public void setClientIndex(Integer clientIndex) {
        this.clientIndex = clientIndex;
    }

    public synchronized void canReceiveEvents()
    {
        startReceivingEvents = true;
        notifyAll();
    }

    public synchronized void didReceiveEvent(VCEvent event)
    {
        fromClient = event;
        notifyAll();
    }

    public synchronized void didReceivePing(Integer ping)
    {
        pingFromClient = ping;
        notifyAll();
    }

}


