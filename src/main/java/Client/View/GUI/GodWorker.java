package Client.View.GUI;

import javax.swing.*;

public class GodWorker extends SwingWorker {
    public GodWorker(String god){
        GUIHandler.myGod=god;
        GUIHandler.ready=true;
    }

    @Override
    protected Object doInBackground() throws Exception {
        return null;
    }
}
