public class Card {
    //Single card
    private String suit;
    private String rank;
    private String imagePath;

    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
        this.imagePath = "images/" + rank + "_of_" + suit + ".png";
    }
    public String getRank() {
        return rank;
    }

    public String getSuit() {
        return suit;
    }

    public String getImagePath() {
        return imagePath;
    }
    public int getValue()
    {
        switch (rank)
        {
            case "J": case "Q": case "K":
                return 10;
            case "A":
                return 11; //11 or 1 depends, changing in game logic
            default:
                return Integer.parseInt(rank);
        }
    }
    @Override
    public String toString()
    {
        return rank + " of " + suit;
    }
}
