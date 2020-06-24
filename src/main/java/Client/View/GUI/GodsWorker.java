package Client.View.GUI;

import javax.swing.*;
import java.util.ArrayList;

public class GodsWorker extends SwingWorker {
    public GodsWorker(String god){
        GUIHandler.chosenGods.add(god);
    }

    @Override
    protected Object doInBackground() throws Exception {
        return null;
    }
}
