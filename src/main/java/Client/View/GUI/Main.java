package Client.View.GUI;

import Client.View.GUI.GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    private static String ip;
    public static void main(String[] args){
                GUI GUI=new GUI();
                GUI.getUpperLabel().setText("Please insert server IP address, the press button Next");
                GUI.getLowerLabel().setText("If you send a wrong IP address, you won't be able to play. Close this windows and restart game");
                CustomActionListener nextListener = new CustomActionListener(GUI);
                GUI.getIpButton().addActionListener(nextListener);
                while(nextListener.isGoForward()==false){
                    System.out.println("Attendo inserimento indirizzo ip");
                }
                GUIHandler gh= new GUIHandler(GUI,ip);
                GUI.getUpperLabel().setText("Wait a moment for your friends, please");
                GUI.getLowerLabel().setText("The game will start soon");
                gh.launchConnection();

    }

    static class CustomActionListener implements ActionListener{
        private boolean goForward=false;
        private GUI GUI;

        public CustomActionListener(GUI GUI){
            this.GUI=GUI;
        }

        public boolean isGoForward() {
            return goForward;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ip= GUI.getIpTextField().getText();
            goForward=true;
        }
    }

}
