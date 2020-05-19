package Client.View.GUI;

import javax.swing.*;

public class DateOfBirth {
    private JLabel dayLabel;
    private JTextField dayTextField;
    private JLabel mounthLabel;
    private JTextField mounthTextField;
    private JLabel yearLabel;
    private JTextField yearTextLabel;
    private JButton insertion;
    private JLabel warning;

    public DateOfBirth(){
        JFrame dateFrame = new JFrame("Date of Birth");
        //Gestione dell'errore se l'utente chiude la finestra
        mounthLabel=new JLabel("Insert mounth of birth (mm) ");
        mounthTextField = new JTextField();
        dayLabel = new JLabel("Insert day of birth (gg) ");
        dayTextField = new JTextField();
        yearLabel = new JLabel("Insert year of birth (aaaa)");
        insertion = new JButton("Send date");
        warning = new JLabel();






    }

}
