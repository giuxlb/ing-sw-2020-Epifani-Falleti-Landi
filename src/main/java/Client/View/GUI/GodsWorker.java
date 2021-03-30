package Client.View.GUI;

import javax.swing.*;
import java.util.ArrayList;

public class GodsWorker extends SwingWorker {
    private JLabel upperLabel;

    public GodsWorker(String god, JLabel upperLabel, int playersNumber){
        if(GUIHandler.chosenGods.size()<=playersNumber){
            this.upperLabel = upperLabel;
            GUIHandler.chosenGods.add(god.toUpperCase());
            upperLabel.setText("You have selected god " + god.toLowerCase() + ", You have to choose "+ (playersNumber-GUIHandler.chosenGods.size()) + " god!");
            GUIHandler.ready=true;
        }

    }

    @Override
    protected Object doInBackground() throws Exception {
        return null;
    }
}
