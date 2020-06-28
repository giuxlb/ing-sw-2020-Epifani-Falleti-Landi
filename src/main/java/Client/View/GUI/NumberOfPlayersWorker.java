package Client.View.GUI;

import javax.swing.*;

public class NumberOfPlayersWorker extends SwingWorker {
    public NumberOfPlayersWorker(int num){
            GUIHandler.playersNumber=num;
            GUIHandler.ready=true;
    }

    @Override
    protected Object doInBackground() throws Exception {
        return null;
    }
}
