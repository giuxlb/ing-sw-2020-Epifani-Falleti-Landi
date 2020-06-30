package Client.View.GUI;

import javax.swing.*;
import java.util.ArrayList;

public class GodsWorker extends SwingWorker {
    private boolean ready = false;
    public GodsWorker(String god){
        GUIHandler.chosenGods.add(god);
        GUIHandler.ready=true;


    }

    @Override
    protected Object doInBackground() throws Exception {
        return null;
    }
}
