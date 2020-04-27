package Client.View;

import Controller.Network.VCEvent;

public interface ServerObserver {
    public void didReceiveVCEvent(VCEvent event);
    public void didReceivePing(Integer p);
    public void serverDied();
}
