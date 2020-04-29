package Client.Controller;

import Controller.Coordinates;
import java.util.ArrayList;

public class Controller {
    public boolean validOperation(){
        return false;
    }

    public boolean checkLimits(int choice){
        if(choice>=0 && choice<=4){
            return true;
        }else{
            return false;
        }
    }

    public boolean checkNumberOfPlayers(int n){
        if(n==2 || n==3){
            return true;
        }

        return false;
    }

    public boolean checkRequestedPosition(ArrayList<Coordinates> validPositions, Coordinates requestedPosition){
        for(Coordinates c:validPositions){
            if(c.equals(requestedPosition)){
                return true;
            }
        }

        return false;
    }


}