package Client.View.GUI;

import javax.swing.*;

public class ChooseNumPlayerWorker extends SwingWorker {
    public ChooseNumPlayerWorker(int num){
            GUIHandler.playersNumber=num;
            GUIHandler.ready=true;
    }

    @Override
    protected Object doInBackground() throws Exception {
        return null;
    }
}
