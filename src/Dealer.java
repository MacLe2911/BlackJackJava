public class Dealer extends Player{
    public void play(Deck deck){
        while(getHandValue()<17){
            addCard(deck.drawCard());
        }
    }
    public void showFirstCard(){
        if(!hand.isEmpty()){
            System.out.println(hand.getFirst());
        }
    }
}
