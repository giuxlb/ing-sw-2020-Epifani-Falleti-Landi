package Client.View;

import Controller.GameControl;

import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        System.out.println("Cosa vuoi avviare?");
        System.out.println("(1) Server");
        System.out.println("(2) CLI");
        System.out.println("(3) GUI");
        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();
        switch (choice){
            case (1):
                GameControl.main(args);
                break;
            case (2):
                CLI.main(args);
                break;
            case (3):
                Client.View.GUI.Main.main(args);
                break;
            default:
                CLI.main(args);
        }
    }
}
