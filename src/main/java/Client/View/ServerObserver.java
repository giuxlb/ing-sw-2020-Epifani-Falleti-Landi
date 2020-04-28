package Client.View;

import Controller.Network.VCEvent;

/**
 * @author Adriano Falleti
 */
public interface ServerObserver {
    public void didReceiveVCEvent(VCEvent event);
    public void didReceivePing(Integer p);
    public void serverDied();
}
