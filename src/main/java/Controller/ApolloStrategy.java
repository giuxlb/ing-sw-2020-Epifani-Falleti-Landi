package Controller;

public class ApolloStrategy implements TurnStrategy {
    public void move(){
        //strategia movimento apollo
        System.out.println("Movimento di apollo");
    }
    public void build(){
        //standard build
        System.out.println("Build standard");
    }
}
