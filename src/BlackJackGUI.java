import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class BlackJackGUI extends Application {
    private GameLogic gameLogic;
    private Text playerLabel;
    private Text dealerLabel;
    private HBox playerCards;
    private HBox secondHandCards;
    private HBox dealerCards;
    private Text moneyLabel;
    private TextField betField;
    private Button placeBetButton;
    private Button hitButton;
    private Button standButton;
    private Button resetButton;
    private Button doubleDownButton;
    private Button splitButton;
    private HBox betPanel;

    @Override
    public void start(Stage primaryStage) {
        gameLogic = new GameLogic();

        BorderPane root = new BorderPane();

        // Ustawienie ikony programu
        Image icon = new Image(getClass().getResourceAsStream("icon.png"));
        primaryStage.getIcons().add(icon);

        // Górny panel - karty krupiera
        VBox topPanel = new VBox(10);
        topPanel.setAlignment(Pos.CENTER);
        dealerLabel = new Text("Karty krupiera: ");
        dealerLabel.setFont(Font.font(20));
        dealerLabel.setStyle("-fx-fill: white;");
        dealerCards = new HBox(10);
        dealerCards.setAlignment(Pos.CENTER);
        topPanel.getChildren().addAll(dealerLabel, dealerCards);

        // Środkowy panel - karty gracza
        VBox centerPanel = new VBox(10);
        centerPanel.setAlignment(Pos.CENTER);
        playerLabel = new Text("Twoje karty: ");
        playerLabel.setFont(Font.font(20));
        playerLabel.setStyle("-fx-fill: white;");
        playerCards = new HBox(10);
        playerCards.setAlignment(Pos.CENTER);
        secondHandCards = new HBox(10);
        secondHandCards.setAlignment(Pos.CENTER);
        centerPanel.getChildren().addAll(playerLabel, playerCards, secondHandCards);

        // Dolny panel - zakłady i przyciski gry
        VBox bottomPanel = new VBox(15);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setPadding(new Insets(10));

        // Panel zakładów
        betPanel = new HBox(10);
        betPanel.setAlignment(Pos.CENTER);
        betPanel.setStyle("-fx-background-color: #006400; -fx-border-color: gold; -fx-border-width: 2; -fx-padding: 10;");

        moneyLabel = new Text("Żetony: " + gameLogic.getPlayer().getMoney());
        moneyLabel.setFont(Font.font(16));
        moneyLabel.setStyle("-fx-fill: gold;");

        betField = new TextField("0");
        betField.setPrefWidth(80);
        betField.setFont(Font.font(14));

        // Przyciski żetonów
        Button chip10 = new Button("10");
        chip10.setStyle("-fx-background-color: #FF3333; -fx-text-fill: white; -fx-font-size: 14; -fx-shape: 'M0,50 A50,50 0 1,1 100,50 A50,50 0 1,1 0,50 Z';");
        chip10.setPrefSize(50, 50);
        chip10.setOnAction(e -> updateBetField(10));

        Button chip50 = new Button("50");
        chip50.setStyle("-fx-background-color: #33CC33; -fx-text-fill: white; -fx-font-size: 14; -fx-shape: 'M0,50 A50,50 0 1,1 100,50 A50,50 0 1,1 0,50 Z';");
        chip50.setPrefSize(50, 50);
        chip50.setOnAction(e -> updateBetField(50));

        Button chip100 = new Button("100");
        chip100.setStyle("-fx-background-color: #3333FF; -fx-text-fill: white; -fx-font-size: 14; -fx-shape: 'M0,50 A50,50 0 1,1 100,50 A50,50 0 1,1 0,50 Z';");
        chip100.setPrefSize(50, 50);
        chip100.setOnAction(e -> updateBetField(100));

        Button chip500 = new Button("500");
        chip500.setStyle("-fx-background-color: #000000; -fx-text-fill: white; -fx-font-size: 14; -fx-shape: 'M0,50 A50,50 0 1,1 100,50 A50,50 0 1,1 0,50 Z';");
        chip500.setPrefSize(50, 50);
        chip500.setOnAction(e -> updateBetField(500));

        placeBetButton = new Button("Postaw zakład");
        placeBetButton.setFont(Font.font(16));
        placeBetButton.setStyle("-fx-background-color: #FFD700; -fx-text-fill: black;");

        betPanel.getChildren().addAll(moneyLabel, new Text("Zakład: "), betField, chip10, chip50, chip100, chip500, placeBetButton);

        // Panel przycisków gry
        HBox buttonPanel = new HBox(20);
        buttonPanel.setAlignment(Pos.CENTER);
        hitButton = new Button("Dobierz kartę");
        standButton = new Button("Pass");
        resetButton = new Button("Resetuj grę");
        doubleDownButton = new Button("Double Down");
        splitButton = new Button("Split");

        hitButton.setFont(Font.font(16));
        standButton.setFont(Font.font(16));
        resetButton.setFont(Font.font(16));
        doubleDownButton.setFont(Font.font(16));
        splitButton.setFont(Font.font(16));

        buttonPanel.getChildren().addAll(hitButton, standButton, doubleDownButton, splitButton, resetButton);

        bottomPanel.getChildren().addAll(betPanel, buttonPanel);

        // Ustawienie tła przypominającego stół kasynowy
        root.setStyle("-fx-background-color: #006400;");

        // Obsługa przycisków
        placeBetButton.setOnAction(e -> {
            try {
                int betAmount = Integer.parseInt(betField.getText());
                if (betAmount > 0 && betAmount <= gameLogic.getPlayer().getMoney()) {
                    gameLogic.placeBet(betAmount);
                    gameLogic.startGame();
                    updateUI();
                    String blackjackResult = gameLogic.getBlackjackResult();
                    if (blackjackResult != null) {
                        showResultAlert(blackjackResult);
                    } else {
                        toggleBetting(true);
                    }
                } else {
                    playerLabel.setText("Nieprawidłowy zakład! Maks: " + gameLogic.getPlayer().getMoney());
                }
            } catch (NumberFormatException ex) {
                playerLabel.setText("Wpisz poprawną liczbę!");
            } catch (IllegalStateException ex) {
                playerLabel.setText(ex.getMessage());
            }
        });

        hitButton.setOnAction(e -> {
            gameLogic.playerHits();
            updateUI();
            if (gameLogic.isPlayerBusted()) {
                if (gameLogic.isPlayingSecondHand() && !gameLogic.getPlayer().isBusted()) {
                    gameLogic.setPlayingSecondHand(false);
                    updateUI();
                } else {
                    gameLogic.dealerTurn();
                    updateUI();
                    String result = gameLogic.calculateRoundResult();
                    showResultAlert(result);
                    toggleBetting(false);
                }
            }
        });

        standButton.setOnAction(e -> {
            System.out.println("Stand: PlayingSecondHand=" + gameLogic.isPlayingSecondHand() + ", FirstHandCompleted=" + gameLogic.isFirstHandCompleted() + ", HasSecondHand=" + gameLogic.getPlayer().hasSecondHand());
            if (!gameLogic.isPlayingSecondHand() && gameLogic.getPlayer().hasSecondHand() && !gameLogic.isFirstHandCompleted()) {
                gameLogic.setFirstHandCompleted(true);
                gameLogic.setPlayingSecondHand(true);
                updateUI();
            } else {
                if (gameLogic.isPlayingSecondHand()) {
                    gameLogic.setPlayingSecondHand(false);
                }
                gameLogic.dealerTurn();
                updateUI();
                String result = gameLogic.calculateRoundResult();
                showResultAlert(result);
                toggleBetting(false);
            }
        });

        doubleDownButton.setOnAction(e -> {
            gameLogic.playerDoubleDown();
            if (gameLogic.getPlayer().hasSecondHand() && !gameLogic.isPlayingSecondHand()) {
                gameLogic.setPlayingSecondHand(true);
                updateUI();
            } else {
                updateUI();
                String result = gameLogic.calculateRoundResult();
                showResultAlert(result);
                toggleBetting(false);
            }
        });

        splitButton.setOnAction(e -> {
            gameLogic.splitHand();
            updateUI();
            toggleBetting(true);
        });

        resetButton.setOnAction(e -> {
            gameLogic.resetGame(); // Pełny reset gry
            updateUI();
            toggleBetting(false);
        });

        // Wyłącz przyciski gry na początku
        toggleBetting(false);

        // Rozmieszczenie paneli w oknie
        root.setTop(topPanel);
        root.setCenter(centerPanel);
        root.setBottom(bottomPanel);

        updateUI();

        // Utworzenie i pokazanie sceny
        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Blackjack - Leśniewski Maciej");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Wyświetlenie okna dialogowego z wynikiem rundy
    private void showResultAlert(String result) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Wynik rundy");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.showAndWait();
        updateUI(); // Aktualizuj interfejs po zamknięciu okna
    }

    // Aktualizacja interfejsu
    private void updateUI() {
        String labelText = gameLogic.isPlayingSecondHand() ? "Druga ręka: " : "Pierwsza ręka: ";
        labelText += gameLogic.getPlayer().getHand().isEmpty() ? 0 : (gameLogic.isPlayingSecondHand() ? gameLogic.getPlayer().getSecondHandValue() : gameLogic.getPlayer().getHandValue());
        playerLabel.setText(labelText);
        dealerLabel.setText("Karty krupiera: " + (gameLogic.isDealerTurnCompleted() && !gameLogic.getDealer().getHand().isEmpty() ? gameLogic.getDealer().getHandValue() : "?"));
        moneyLabel.setText("Żetony: " + gameLogic.getPlayer().getMoney());
        betField.setText(String.valueOf(gameLogic.getPlayer().getBet()));

        playerCards.getChildren().clear();
        secondHandCards.getChildren().clear();
        dealerCards.getChildren().clear();

        for (Card card : gameLogic.getPlayer().getHand()) {
            playerCards.getChildren().add(loadCardImage(card));
        }

        for (Card card : gameLogic.getPlayer().getSecondHand()) {
            secondHandCards.getChildren().add(loadCardImage(card));
        }

        List<Card> dealerHand = gameLogic.getDealer().getHand();
        for (int i = 0; i < dealerHand.size(); i++) {
            if (i == 0 && !gameLogic.isDealerTurnCompleted()) {
                dealerCards.getChildren().add(loadBackCardImage());
            } else {
                dealerCards.getChildren().add(loadCardImage(dealerHand.get(i)));
            }
        }

        // Wizualne oznaczenie aktywnej ręki
        if (gameLogic.isPlayingSecondHand()) {
            playerCards.setStyle("-fx-border-color: transparent; -fx-border-width: 2;");
            secondHandCards.setStyle("-fx-border-color: gold; -fx-border-width: 2;");
        } else {
            playerCards.setStyle("-fx-border-color: gold; -fx-border-width: 2;");
            secondHandCards.setStyle("-fx-border-color: transparent; -fx-border-width: 2;");
        }

        doubleDownButton.setDisable(!gameLogic.getPlayer().canDoubleDown());
        splitButton.setDisable(!gameLogic.getPlayer().canSplit());
    }

    // Wczytanie grafiki karty
    private ImageView loadCardImage(Card card) {
        String imagePath = "/" + card.getRank() + "_of_" + card.getSuit() + ".png";
        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(80);
            imageView.setFitHeight(120);
            return imageView;
        } catch (Exception e) {
            System.err.println("Nie znaleziono pliku: " + imagePath);
            return new ImageView();
        }
    }

    // Wczytanie grafiki tyłu karty
    private ImageView loadBackCardImage() {
        String imagePath = "/back.png";
        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(80);
            imageView.setFitHeight(120);
            return imageView;
        } catch (Exception e) {
            System.err.println("Nie znaleziono pliku: " + imagePath);
            return new ImageView();
        }
    }

    // Aktualizacja pola zakładu po kliknięciu żetonu
    private void updateBetField(int amount) {
        try {
            int currentBet = Integer.parseInt(betField.getText());
            currentBet += amount;
            if (currentBet <= gameLogic.getPlayer().getMoney()) {
                betField.setText(String.valueOf(currentBet));
            } else {
                playerLabel.setText("Nie masz wystarczająco żetonów!");
            }
        } catch (NumberFormatException e) {
            betField.setText(String.valueOf(amount));
        }
    }

    // Przełączanie między panelem zakładów a przyciskami gry
    private void toggleBetting(boolean inGame) {
        betField.setDisable(inGame);
        placeBetButton.setDisable(inGame);
        hitButton.setDisable(!inGame);
        standButton.setDisable(!inGame);
        resetButton.setDisable(!inGame);
        doubleDownButton.setDisable(!inGame || !gameLogic.getPlayer().canDoubleDown());
        splitButton.setDisable(!inGame || !gameLogic.getPlayer().canSplit());
    }

    public static void main(String[] args) {
        launch(args);
    }
}