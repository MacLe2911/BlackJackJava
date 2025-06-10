import java.util.ArrayList;
import java.util.List;

public class Player {
    protected List<Card> hand;
    protected List<Card> secondHand;
    private int cachedHandValue;
    private int cachedSecondHandValue;
    private boolean isHandValueValid;
    private boolean isSecondHandValueValid;
    private int money;
    private int bet;
    private int insuranceBet;
    private int roundsPlayed;
    private int totalWon;
    private int totalLost;
    private int blackjacks;
    private int wins;

    public Player() {
        this.hand = new ArrayList<>();
        this.secondHand = new ArrayList<>();
        this.cachedHandValue = 0;
        this.cachedSecondHandValue = 0;
        this.isHandValueValid = false;
        this.isSecondHandValueValid = false;
        this.money = 1000;
        this.bet = 0;
        this.insuranceBet = 0;
        this.roundsPlayed = 0;
        this.totalWon = 0;
        this.totalLost = 0;
        this.blackjacks = 0;
        this.wins = 0;
    }

    public void addCard(Card card) {
        if (card != null) {
            hand.add(card);
            isHandValueValid = false;
        }
    }

    public void addCardToSecondHand(Card card) {
        if (card != null) {
            secondHand.add(card);
            isSecondHandValueValid = false;
        }
    }

    public int getHandValue() {
        if (isHandValueValid) {
            return cachedHandValue;
        }

        int value = 0;
        int aceCount = 0;

        for (Card card : hand) {
            value += card.getValue();
            if (card.getRank().equals("A")) {
                aceCount++;
            }
        }

        while (value > 21 && aceCount > 0) {
            value -= 10;
            aceCount--;
        }

        cachedHandValue = value;
        isHandValueValid = true;

        return value;
    }

    public int getSecondHandValue() {
        if (isSecondHandValueValid) {
            return cachedSecondHandValue;
        }

        int value = 0;
        int aceCount = 0;

        for (Card card : secondHand) {
            value += card.getValue();
            if (card.getRank().equals("A")) {
                aceCount++;
            }
        }

        while (value > 21 && aceCount > 0) {
            value -= 10;
            aceCount--;
        }

        cachedSecondHandValue = value;
        isSecondHandValueValid = true;

        return value;
    }

    public void showHand() {
        for (Card card : hand) {
            System.out.println(card);
        }
    }

    public List<Card> getHand() {
        return hand;
    }

    public List<Card> getSecondHand() {
        return secondHand;
    }

    public void clearHand() {
        hand.clear();
        secondHand.clear();
        isHandValueValid = false;
        isSecondHandValueValid = false;
    }

    public boolean isBusted() {
        return getHandValue() > 21;
    }

    public boolean isSecondHandBusted() {
        return getSecondHandValue() > 21;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        if (bet >= 0 && bet <= money) {
            this.bet = bet;
        }
    }

    public void clearBet() {
        this.bet = 0;
    }

    public int getInsuranceBet() {
        return insuranceBet;
    }

    public void setInsuranceBet(int bet) {
        this.insuranceBet = bet;
    }

    public void clearInsuranceBet() {
        this.insuranceBet = 0;
    }

    public boolean canDoubleDown() {
        return hand.size() == 2 && secondHand.isEmpty() && money >= bet;
    }

    public boolean canSplit() {
        if (hand.size() == 2 && secondHand.isEmpty() && money >= bet) {
            Card card1 = hand.get(0);
            Card card2 = hand.get(1);
            return card1.getRank().equals(card2.getRank());
        }
        return false;
    }

    public boolean hasSecondHand() {
        return !secondHand.isEmpty();
    }

    public void incrementRoundsPlayed() {
        roundsPlayed++;
    }

    public void addWon(int amount) {
        totalWon += amount;
    }

    public void addLost(int amount) {
        totalLost += amount;
    }

    public void incrementBlackjacks() {
        blackjacks++;
    }

    public void incrementWins() {
        wins++;
    }

    public int getRoundsPlayed() {
        return roundsPlayed;
    }

    public int getTotalWon() {
        return totalWon;
    }

    public int getTotalLost() {
        return totalLost;
    }

    public int getBlackjacks() {
        return blackjacks;
    }

    public int getWins() {
        return wins;
    }

    public double getWinPercentage() {
        return roundsPlayed == 0 ? 0 : (double) wins / roundsPlayed * 100;
    }
}