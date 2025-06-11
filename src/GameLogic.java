public class GameLogic {
    private Player player;
    private Dealer dealer;
    private Deck deck;
    private boolean dealerTurnCompleted;
    private boolean playingSecondHand;
    private boolean firstHandCompleted;

    public GameLogic() {
        this.player = new Player();
        this.dealer = new Dealer();
        this.deck = new Deck();
        this.dealerTurnCompleted = false;
        this.playingSecondHand = false;
        this.firstHandCompleted = false;
    }

    public void placeBet(int amount) {
        player.setBet(amount);
        player.setMoney(player.getMoney() - amount);
    }

    public void startGame() {
        if (player.getBet() <= 0) {
            throw new IllegalStateException("Musisz postawić zakład!");
        }
        player.clearHand();
        dealer.clearHand();
        deck = new Deck();
        dealerTurnCompleted = false;
        playingSecondHand = false;
        firstHandCompleted = false;

        player.addCard(deck.drawCard());
        player.addCard(deck.drawCard());

        dealer.addCard(deck.drawCard());
        dealer.addCard(deck.drawCard());
    }

    public void playerHits() {
        if (playingSecondHand) {
            player.addCardToSecondHand(deck.drawCard());
        } else {
            player.addCard(deck.drawCard());
        }
    }

    public void playerDoubleDown() {
        if (player.canDoubleDown()) {
            int additionalBet = player.getBet();
            player.setMoney(player.getMoney() - additionalBet);
            player.setBet(player.getBet() * 2);
            if (playingSecondHand) {
                player.addCardToSecondHand(deck.drawCard());
            } else {
                player.addCard(deck.drawCard());
            }
            if (!playingSecondHand) {
                firstHandCompleted = true;
            }
            // dealerTurn(); //Usuniete na potrzeby animacji
        }
    }

    public void splitHand() {
        if (player.canSplit()) {
            int additionalBet = player.getBet();
            player.setMoney(player.getMoney() - additionalBet);
            player.addCardToSecondHand(player.getHand().remove(1));
            player.addCard(deck.drawCard());
            player.addCardToSecondHand(deck.drawCard());
        }
    }

    public void dealerTurn() {
        System.out.println("Dealer Turn Started: Dealer Hand=" + dealer.getHand());
        dealer.play(deck);
        dealerTurnCompleted = true;
        System.out.println("Dealer Turn Completed: Dealer Hand=" + dealer.getHand() + ", Value=" + dealer.getHandValue());
    }

    public boolean canOfferInsurance() {
        boolean dealerHasAceUp = !dealer.getHand().isEmpty() && dealer.getHand().get(1).getRank().equals("A");
        boolean playerHasNotBoughtInsurance = player.getInsuranceBet() == 0;
        return dealerHasAceUp && playerHasNotBoughtInsurance;
    }

    public void buyInsurance() {
        int insuranceBet = player.getBet() / 2;
        if (player.getMoney() >= insuranceBet) {
            player.setMoney(player.getMoney() - insuranceBet);
            player.setInsuranceBet(insuranceBet);
        }
    }

    public boolean isPlayerBusted() {
        return playingSecondHand ? player.isSecondHandBusted() : player.isBusted();
    }

    public boolean isDealerBusted() {
        return dealer.isBusted();
    }

    public boolean hasBlackjack(Player player) {
        return player.getHand().size() == 2 && player.getHandValue() == 21;
    }

    public String getBlackjackResult() {
        if (hasBlackjack(player)) {
            int originalBet = player.getBet();
            if (hasBlackjack(dealer)) {
                player.setMoney(player.getMoney() + originalBet);
                player.clearBet();
                player.incrementRoundsPlayed();
                return "Remis! Odzyskujesz " + originalBet + " żetonów.";
            } else {
                int winnings = (int)(originalBet * 2.5);
                player.setMoney(player.getMoney() + winnings);
                player.clearBet();
                player.incrementRoundsPlayed();
                player.incrementBlackjacks();
                player.addWon(winnings);
                player.incrementWins();
                return "Blackjack! Wygrałeś " + winnings + " żetonów!";
            }
        }
        return null;
    }

    public String calculateRoundResult() {
        StringBuilder result = new StringBuilder();
        int originalBet = player.getBet();

        player.incrementRoundsPlayed();

        if (player.getInsuranceBet() > 0 && hasBlackjack(dealer)) {
            int insuranceWin = player.getInsuranceBet() * 3;
            player.setMoney(player.getMoney() + insuranceWin);
            result.append("Ubezpieczenie: Wygrałeś ").append(insuranceWin).append(" żetonów!\n");
        } else if (player.getInsuranceBet() > 0) {
            result.append("Ubezpieczenie: Straciłeś ").append(player.getInsuranceBet()).append(" żetonów.\n");
        }
        player.clearInsuranceBet();

        result.append("Pierwsza ręka: ");
        if (player.isBusted()) {
            result.append("Przegrałeś! Straciłeś ").append(originalBet).append(" żetonów.\n");
            player.addLost(originalBet);
        } else if (hasBlackjack(player)) {
            if (hasBlackjack(dealer)) {
                player.setMoney(player.getMoney() + originalBet);
                result.append("Remis! Odzyskujesz ").append(originalBet).append(" żetonów.\n");
            } else {
                int winnings = (int)(originalBet * 2.5);
                player.setMoney(player.getMoney() + winnings);
                result.append("Blackjack! Wygrałeś ").append(winnings).append(" żetonów!\n");
                player.incrementBlackjacks();
                player.addWon(winnings);
                player.incrementWins();
            }
        } else if (dealer.isBusted()) {
            int winnings = originalBet * 2;
            player.setMoney(player.getMoney() + winnings);
            result.append("Wygrałeś! Zdobyłeś ").append(winnings).append(" żetonów!\n");
            player.addWon(winnings);
            player.incrementWins();
        } else {
            int playerValue = player.getHandValue();
            int dealerValue = dealer.getHandValue();
            if (playerValue > dealerValue) {
                int winnings = originalBet * 2;
                player.setMoney(player.getMoney() + winnings);
                result.append("Wygrałeś! Zdobyłeś ").append(winnings).append(" żetonów!\n");
                player.addWon(winnings);
                player.incrementWins();
            } else if (dealerValue > playerValue) {
                result.append("Przegrałeś! Straciłeś ").append(originalBet).append(" żetonów.\n");
                player.addLost(originalBet);
            } else {
                player.setMoney(player.getMoney() + originalBet);
                result.append("Remis! Odzyskujesz ").append(originalBet).append(" żetonów.\n");
            }
        }

        if (player.hasSecondHand()) {
            result.append("Druga ręka: ");
            if (player.isSecondHandBusted()) {
                result.append("Przegrałeś! Straciłeś ").append(originalBet).append(" żetonów.\n");
                player.addLost(originalBet);
            } else if (player.getSecondHand().size() == 2 && player.getSecondHandValue() == 21) {
                if (hasBlackjack(dealer)) {
                    player.setMoney(player.getMoney() + originalBet);
                    result.append("Remis! Odzyskujesz ").append(originalBet).append(" żetonów.\n");
                } else {
                    int winnings = (int)(originalBet * 2.5);
                    player.setMoney(player.getMoney() + winnings);
                    result.append("Blackjack! Wygrałeś ").append(winnings).append(" żetonów!\n");
                    player.incrementBlackjacks();
                    player.addWon(winnings);
                    player.incrementWins();
                }
            } else if (dealer.isBusted()) {
                int winnings = originalBet * 2;
                player.setMoney(player.getMoney() + winnings);
                result.append("Wygrałeś! Zdobyłeś ").append(winnings).append(" żetonów!\n");
                player.addWon(winnings);
                player.incrementWins();
            } else {
                int playerValue = player.getSecondHandValue();
                int dealerValue = dealer.getHandValue();
                if (playerValue > dealerValue) {
                    int winnings = originalBet * 2;
                    player.setMoney(player.getMoney() + winnings);
                    result.append("Wygrałeś! Zdobyłeś ").append(winnings).append(" żetonów!\n");
                    player.addWon(winnings);
                    player.incrementWins();
                } else if (dealerValue > playerValue) {
                    result.append("Przegrałeś! Straciłeś ").append(originalBet).append(" żetonów.\n");
                    player.addLost(originalBet);
                } else {
                    player.setMoney(player.getMoney() + originalBet);
                    result.append("Remis! Odzyskujesz ").append(originalBet).append(" żetonów.\n");
                }
            }
        }

        player.clearBet();
        playingSecondHand = false;
        firstHandCompleted = false;
        player.clearHand();
        dealer.clearHand();
        dealerTurnCompleted = false;
        System.out.println("Round Result: " + result.toString());
        return result.toString();
    }

    public void resetGame() {
        this.player = new Player();
        this.dealer = new Dealer();
        this.deck = new Deck();
        this.dealerTurnCompleted = false;
        this.playingSecondHand = false;
        this.firstHandCompleted = false;
    }

    public Player getPlayer() {
        return player;
    }

    public Dealer getDealer() {
        return dealer;
    }

    public Deck getDeck() {
        return deck;
    }

    public boolean isDealerTurnCompleted() {
        return dealerTurnCompleted;
    }

    public boolean isPlayingSecondHand() {
        return playingSecondHand;
    }

    public void setPlayingSecondHand(boolean playingSecondHand) {
        this.playingSecondHand = playingSecondHand;
    }

    public boolean isFirstHandCompleted() {
        return firstHandCompleted;
    }

    public void setFirstHandCompleted(boolean firstHandCompleted) {
        this.firstHandCompleted = firstHandCompleted;
    }
}