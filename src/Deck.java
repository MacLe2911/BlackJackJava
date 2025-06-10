import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cardList;
    private String[] suits={"clubs", "diamonds", "hearts", "spades"};
    private String[] ranks={"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    public Deck() {
        cardList = new ArrayList<Card>();
        for (String suit : suits) {
            for (String rank : ranks) {
                cardList.add(new Card(suit,rank));
            }
        }
        shuffle();
    }
    public void shuffle() { //tasowanie talii
        Collections.shuffle(cardList);
    }
    public Card drawCard() //dobieranie kart
    {
        if(!cardList.isEmpty()) {
            return cardList.remove(0);
        }
        return null;
    }
}
