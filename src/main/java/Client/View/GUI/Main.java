package Client.View.GUI;

import Client.View.GUI.GUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    public static void main(String[] args){
        /*SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {*/
                GUI GUI=new GUI();
                String ip = null;

                GUI.getIpButton().addActionListener(new customActionListener(ip,GUI.getIpTextField()));
                System.out.println(ip);
                GUIHandler gh= new GUIHandler(GUI,ip);

                gh.launchConnection();
          /* }
        });*/

    }

    static class customActionListener implements ActionListener{
        private String ip;
        private JTextField ipTextField;
        public customActionListener(String ip,JTextField ipTextField)
        {
            this.ip = ip;
            this.ipTextField = ipTextField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            ip = this.ipTextField.getText();
        }
    }

}
