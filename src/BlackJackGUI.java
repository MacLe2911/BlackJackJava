import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;
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
    private Button insuranceButton;
    private Button statsButton;
    private HBox betPanel;

    private StackPane mainStackPane;
    private BorderPane gamePane;
    private VBox shuffleOverlayPane;
    private ProgressBar shuffleProgressBar;
    private Text shufflingStatusText;

    private int prevPlayerHandSize = 0;
    private int prevPlayerSecondHandSize = 0;
    private int prevDealerHandSize = 0;

    // Odtwarzacz dla dźwięku tasowania
    private MediaPlayer shuffleSoundPlayer;

    @Override
    public void start(Stage primaryStage) {
        gameLogic = new GameLogic();
        prepareShuffleSound(); // Przygotuj dźwięk na starcie

        gamePane = new BorderPane();
        gamePane.setStyle("-fx-background-color: linear-gradient(to bottom, #004d00, #000000);");

        //ustawienie ikony aplikaci
        Image icon = new Image(getClass().getResourceAsStream("icon.png"));
        primaryStage.getIcons().add(icon);

        // panel krupiera
        VBox topPanel = new VBox(10);
        topPanel.setAlignment(Pos.CENTER);
        topPanel.setPadding(new Insets(10));
        topPanel.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3); -fx-border-color: linear-gradient(to bottom, #FFD700, #FFA500); -fx-border-width: 2; -fx-border-radius: 10;");
        dealerLabel = new Text("Karty krupiera: ");
        dealerLabel.setFont(Font.font("Verdana", 22));
        dealerLabel.setFill(Color.WHITE);
        dealerLabel.setEffect(new DropShadow(5, Color.BLACK));
        dealerCards = new HBox(10);
        dealerCards.setAlignment(Pos.CENTER);
        topPanel.getChildren().addAll(dealerLabel, dealerCards);

        // panel gracza
        VBox centerPanel = new VBox(10);
        centerPanel.setAlignment(Pos.CENTER);
        centerPanel.setPadding(new Insets(10));
        centerPanel.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3); -fx-border-color: linear-gradient(to bottom, #FFD700, #FFA500); -fx-border-width: 2; -fx-border-radius: 10;");
        playerLabel = new Text("Twoje karty: ");
        playerLabel.setFont(Font.font("Verdana", 22));
        playerLabel.setFill(Color.WHITE);
        playerLabel.setEffect(new DropShadow(5, Color.BLACK));
        playerCards = new HBox(10);
        playerCards.setAlignment(Pos.CENTER);
        secondHandCards = new HBox(10);
        secondHandCards.setAlignment(Pos.CENTER);
        centerPanel.getChildren().addAll(playerLabel, playerCards, secondHandCards);

        //panel dolny
        VBox bottomPanel = new VBox(15);
        bottomPanel.setAlignment(Pos.CENTER);
        bottomPanel.setPadding(new Insets(15));
        bottomPanel.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3); -fx-border-color: linear-gradient(to bottom, #FFD700, #FFA500); -fx-border-width: 2; -fx-border-radius: 10;");

        // zaklady
        betPanel = new HBox(10);
        betPanel.setAlignment(Pos.CENTER);
        betPanel.setPadding(new Insets(10));
        betPanel.setStyle("-fx-background-color: linear-gradient(to bottom, #006400, #003300); -fx-border-color: gold; -fx-border-width: 2; -fx-border-radius: 10;-fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 2);");

        moneyLabel = new Text("Żetony: " + gameLogic.getPlayer().getMoney());
        moneyLabel.setFont(Font.font("Verdana", 18));
        moneyLabel.setFill(Color.GOLD);
        moneyLabel.setEffect(new DropShadow(5, Color.BLACK));

        betField = new TextField("0");
        betField.setPrefWidth(80);
        betField.setFont(Font.font("Verdana", 14));
        betField.setStyle("-fx-background-color: linear-gradient(to bottom, #FFFFFF, #E0E0E0); -fx-border-color: gold; -fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 5;");

        // przyciski do obstawiania
        Button chip10 = createChipButton("10", "#FF3333", "#CC0000");
        Button chip50 = createChipButton("50", "#33CC33", "#009900");
        Button chip100 = createChipButton("100", "#3333FF", "#0000CC");
        Button chip500 = createChipButton("500", "#000000", "#333333");

        placeBetButton = new Button("Postaw zakład");
        placeBetButton.setFont(Font.font("Verdana", 16));
        placeBetButton.setStyle("-fx-background-color: linear-gradient(to bottom, #FFD700, #FFA500); -fx-text-fill: black; -fx-border-color: #DAA520; -fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 2);");
        placeBetButton.setOnMouseEntered(e -> placeBetButton.setStyle("-fx-background-color: linear-gradient(to bottom, #FFFF99, #FFCC00); -fx-text-fill: black; -fx-border-color: #DAA520; -fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 2);"));
        placeBetButton.setOnMouseExited(e -> placeBetButton.setStyle("-fx-background-color: linear-gradient(to bottom, #FFD700, #FFA500); -fx-text-fill: black; -fx-border-color: #DAA520; -fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 2);"));

        betPanel.getChildren().addAll(moneyLabel, new Text("Zakład: "), betField, chip10, chip50, chip100, chip500, placeBetButton);

        HBox buttonPanel = new HBox(20);
        buttonPanel.setAlignment(Pos.CENTER);
        buttonPanel.setPadding(new Insets(10));

        hitButton = createGameButton("Dobierz kartę");
        standButton = createGameButton("Pass");
        resetButton = createGameButton("Resetuj grę");
        doubleDownButton = createGameButton("Double Down");
        splitButton = createGameButton("Split");
        insuranceButton = createGameButton("Ubezpieczenie");
        statsButton = createGameButton("Statystyki");

        buttonPanel.getChildren().addAll(hitButton, standButton, doubleDownButton, splitButton, insuranceButton, statsButton, resetButton);
        bottomPanel.getChildren().addAll(betPanel, buttonPanel);

        gamePane.setTop(topPanel);
        gamePane.setCenter(centerPanel);
        gamePane.setBottom(bottomPanel);

        // animacja tasowania
        shuffleProgressBar = new ProgressBar(0);
        shuffleProgressBar.setPrefWidth(300);
        shufflingStatusText = new Text("Tasowanie talii...");
        shufflingStatusText.setFont(Font.font("Verdana", 18));
        shufflingStatusText.setFill(Color.WHITE);

        shuffleOverlayPane = new VBox(20, shufflingStatusText, shuffleProgressBar);
        shuffleOverlayPane.setAlignment(Pos.CENTER);
        shuffleOverlayPane.setPadding(new Insets(20));
        shuffleOverlayPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-background-radius: 10;");
        shuffleOverlayPane.setVisible(false);
        shuffleOverlayPane.setManaged(false);

        mainStackPane = new StackPane();
        mainStackPane.getChildren().addAll(gamePane, shuffleOverlayPane);

        // po postawieniu zakladu
        placeBetButton.setOnAction(e -> {
            try {
                int betAmount = Integer.parseInt(betField.getText());
                if (betAmount > 0 && betAmount <= gameLogic.getPlayer().getMoney()) {
                    gameLogic.placeBet(betAmount);
                    moneyLabel.setText("Żetony: " + gameLogic.getPlayer().getMoney());
                    betField.setText(String.valueOf(gameLogic.getPlayer().getBet()));

                    betField.setDisable(true);
                    placeBetButton.setDisable(true);
                    hitButton.setDisable(true);
                    standButton.setDisable(true);
                    doubleDownButton.setDisable(true);
                    splitButton.setDisable(true);
                    insuranceButton.setDisable(true);

                    shuffleOverlayPane.setVisible(true);
                    shuffleOverlayPane.setManaged(true);
                    shuffleProgressBar.setProgress(0);

                    // Odtwórz dźwięk tasowania
                    if (shuffleSoundPlayer != null) {
                        shuffleSoundPlayer.stop(); // Zatrzymaj, jeśli grał wcześniej
                        shuffleSoundPlayer.play(); // Odtwórz od początku
                    }

                    Timeline timeline = new Timeline(
                            new KeyFrame(Duration.ZERO, new KeyValue(shuffleProgressBar.progressProperty(), 0)),
                            new KeyFrame(Duration.seconds(1.5), new KeyValue(shuffleProgressBar.progressProperty(), 1))
                    );
                    timeline.setOnFinished(event -> {
                        Platform.runLater(() -> {
                            shuffleOverlayPane.setVisible(false);
                            shuffleOverlayPane.setManaged(false);

                            prevPlayerHandSize = 0;
                            prevPlayerSecondHandSize = 0;
                            prevDealerHandSize = 0;

                            gameLogic.startGame();
                            updateUI();

                            //sprawdzenie blackjacka
                            String blackjackResult = gameLogic.getBlackjackResult();
                            if (blackjackResult != null) {
                                showResultAlert(blackjackResult);
                                toggleBetting(false);
                            } else if (gameLogic.canOfferInsurance()) {
                                playerLabel.setText("Krupier ma Asa! Kup ubezpieczenie?");
                                toggleBetting(true);
                            } else {
                                playerLabel.setText("Twoje karty: " + gameLogic.getPlayer().getHandValue());
                                toggleBetting(true);
                            }
                        });
                    });
                    timeline.play();

                } else {
                    playerLabel.setText("Nieprawidłowy zakład! Maks: " + gameLogic.getPlayer().getMoney());
                }
            } catch (NumberFormatException ex) {
                playerLabel.setText("Wpisz poprawną liczbę!");
            } catch (IllegalStateException ex) {
                playerLabel.setText(ex.getMessage());
            }
        });

        // po dobraniu karty
        hitButton.setOnAction(e -> {
            gameLogic.playerHits();
            updateUI();
            if (gameLogic.isPlayerBusted()) {
                if (gameLogic.isPlayingSecondHand() && !gameLogic.getPlayer().isBusted()) {
                    gameLogic.setPlayingSecondHand(false);
                    updateUI();
                    prevPlayerHandSize = gameLogic.getPlayer().getHand().size();
                } else {
                    animateAndResolveRound();
                }
            }
        });

        // po spasowaniu
        standButton.setOnAction(e -> {
            if (!gameLogic.isPlayingSecondHand() && gameLogic.getPlayer().hasSecondHand() && !gameLogic.isFirstHandCompleted()) {
                gameLogic.setFirstHandCompleted(true);
                gameLogic.setPlayingSecondHand(true);
                updateUI();
            } else {
                animateAndResolveRound();
            }
        });

        // double down
        doubleDownButton.setOnAction(e -> {
            gameLogic.playerDoubleDown();
            updateUI();
            if (gameLogic.getPlayer().isBusted()) {
                animateAndResolveRound();
            } else {
                if (gameLogic.getPlayer().hasSecondHand() && !gameLogic.isPlayingSecondHand()) {
                    gameLogic.setPlayingSecondHand(true);
                    updateUI();
                } else {
                    animateAndResolveRound();
                }
            }
        });

        //split
        splitButton.setOnAction(e -> {
            int currentHandSize = gameLogic.getPlayer().getHand().size();
            prevPlayerHandSize = currentHandSize > 0 ? currentHandSize -1 : 0;
            prevPlayerSecondHandSize = 0;
            gameLogic.splitHand();
            updateUI();
            toggleBetting(true);
        });

        //ubezpieczenie
        insuranceButton.setOnAction(e -> {
            gameLogic.buyInsurance();
            updateUI();
            playerLabel.setText("Twoje karty: " + gameLogic.getPlayer().getHandValue());
            toggleBetting(true);
        });

        statsButton.setOnAction(e -> showStatsWindow());

        // reset
        resetButton.setOnAction(e -> {
            prevPlayerHandSize = 0;
            prevPlayerSecondHandSize = 0;
            prevDealerHandSize = 0;
            gameLogic.resetGame();
            updateUI();
            toggleBetting(false);
        });

        toggleBetting(false);
        updateUI();

        Scene scene = new Scene(mainStackPane, 1100, 600);
        primaryStage.setTitle("Blackjack - Leśniewski Maciej");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void prepareShuffleSound() {
        try {
            URL resource = getClass().getResource("shuffle.wav");
            if (resource == null) {
                System.err.println("Nie można znaleźć pliku dźwiękowego: shuffle.wav");
                return;
            }
            String soundPath = resource.toExternalForm();
            Media sound = new Media(soundPath);
            shuffleSoundPlayer = new MediaPlayer(sound);
        } catch (Exception e) {
            System.err.println("Błąd podczas ładowania dźwięku tasowania: " + e.getMessage());
            e.printStackTrace();
            shuffleSoundPlayer = null;
        }
    }

    private void animateAndResolveRound() {
        hitButton.setDisable(true);
        standButton.setDisable(true);
        doubleDownButton.setDisable(true);
        splitButton.setDisable(true);
        insuranceButton.setDisable(true);

        ImageView holeCardView = (ImageView) dealerCards.getChildren().get(0);
        Card holeCard = gameLogic.getDealer().getHand().get(0);
        Image faceImage = loadCardImage(holeCard).getImage();

        //animacja obrotu i odkrycia karty
        RotateTransition rotation1 = new RotateTransition(Duration.millis(300), holeCardView);
        rotation1.setAxis(Rotate.Y_AXIS);
        rotation1.setFromAngle(0);
        rotation1.setToAngle(90);
        rotation1.setInterpolator(Interpolator.EASE_IN);
        rotation1.setOnFinished(e -> holeCardView.setImage(faceImage));

        RotateTransition rotation2 = new RotateTransition(Duration.millis(300), holeCardView);
        rotation2.setAxis(Rotate.Y_AXIS);
        rotation2.setFromAngle(270);
        rotation2.setToAngle(360);
        rotation2.setInterpolator(Interpolator.EASE_OUT);

        SequentialTransition flipAnimation = new SequentialTransition(rotation1, rotation2);

        flipAnimation.setOnFinished(e -> {
            gameLogic.dealerTurn();
            updateUI();
            PauseTransition pause = new PauseTransition(Duration.millis(800));
            pause.setOnFinished(pauseEvent -> {
                Platform.runLater(() -> {
                    String result = gameLogic.calculateRoundResult();
                    showResultAlert(result);
                    toggleBetting(false);
                });
            });
            pause.play();
        });
        flipAnimation.play();
    }

    private Button createChipButton(String text, String colorStart, String colorEnd) {
        Button button = new Button(text);
        int chipValue = Integer.parseInt(text);

        button.setStyle(String.format("-fx-background-color: linear-gradient(to bottom, %s, %s); -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-shape: 'M0,50 A50,50 0 1,1 100,50 A50,50 0 1,1 0,50 Z'; -fx-background-radius: 50; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 2); ", colorStart, colorEnd));
        button.setPrefSize(50, 50);
        button.setOnMouseEntered(e -> button.setStyle(String.format("-fx-background-color: linear-gradient(to bottom, %s, %s); -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-shape: 'M0,50 A50,50 0 1,1 100,50 A50,50 0 1,1 0,50 Z'; -fx-background-radius: 50; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 15, 0, 0, 3);", lightenColor(colorStart), lightenColor(colorEnd))));
        button.setOnMouseExited(e -> button.setStyle(String.format("-fx-background-color: linear-gradient(to bottom, %s, %s); -fx-text-fill: white; -fx-font-size: 14; -fx-font-weight: bold; -fx-shape: 'M0,50 A50,50 0 1,1 100,50 A50,50 0 1,1 0,50 Z'; -fx-background-radius: 50; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 2);", colorStart, colorEnd)));

        button.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                updateBetField(chipValue);
            } else if (e.getButton() == MouseButton.SECONDARY) {
                decreaseBetField(chipValue);
            }
        });
        return button;
    }

    private Button createGameButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Verdana", 16));
        button.setStyle("-fx-background-color: linear-gradient(to bottom, #4682B4, #1E90FF); -fx-text-fill: white; -fx-border-color: #4169E1; -fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 2);");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: linear-gradient(to bottom, #87CEEB, #00B7EB); -fx-text-fill: white; -fx-border-color: #4169E1; -fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 15, 0, 0, 3);"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: linear-gradient(to bottom, #4682B4, #1E90FF); -fx-text-fill: white; -fx-border-color: #4169E1; -fx-border-width: 2; -fx-background-radius: 10; -fx-border-radius: 10; -fx-padding: 10 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 2);"));
        return button;
    }

    private String lightenColor(String hex) {
        Color color = Color.web(hex);
        double factor = 1.3;
        double r = Math.min(color.getRed() * factor, 1.0);
        double g = Math.min(color.getGreen() * factor, 1.0);
        double b = Math.min(color.getBlue() * factor, 1.0);
        return String.format("#%02X%02X%02X", (int)(r * 255), (int)(g * 255), (int)(b * 255));
    }

    private void showStatsWindow() {
        Stage statsStage = new Stage();
        statsStage.setTitle("Statystyki gry");
        VBox statsPanel = new VBox(10);
        statsPanel.setAlignment(Pos.CENTER);
        statsPanel.setPadding(new Insets(15));
        statsPanel.setStyle("-fx-background-color: linear-gradient(to bottom, #004d00, #000000); -fx-border-color: linear-gradient(to bottom, #FFD700, #FFA500); -fx-border-width: 2; -fx-border-radius: 10;");

        TextArea statsText = new TextArea();
        statsText.setEditable(false);
        statsText.setFont(Font.font("Verdana", 14));
        statsText.setPrefWidth(300);
        statsText.setPrefHeight(200);
        statsText.setStyle("-fx-control-inner-background: linear-gradient(to bottom, #FFFFFF, #E0E0E0); -fx-border-color: gold; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0, 0, 2);");

        Player player = gameLogic.getPlayer();
        statsText.setText(String.format("Statystyki gry:\n\nRozegrane rundy: %d\nWygrane żetony: %d\nPrzegrane żetony: %d\nLiczba blackjacków: %d\nProcent wygranych: %.2f%%",
                player.getRoundsPlayed(), player.getTotalWon(), player.getTotalLost(), player.getBlackjacks(), player.getWinPercentage()));

        statsPanel.getChildren().add(statsText);
        Scene statsScene = new Scene(statsPanel, 350, 250);
        statsStage.setScene(statsScene);
        statsStage.show();
    }

    private void showResultAlert(String result) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Wynik rundy");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.getDialogPane().setStyle("-fx-background-color: linear-gradient(to bottom, #004d00, #000000); -fx-font-family: Verdana;");
        alert.getDialogPane().lookup(".content.label").setStyle("-fx-text-fill: #FFFFFF; -fx-font-size: 14; -fx-font-family: Verdana; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 5, 0, 0, 1);");
        alert.showAndWait();
        updateUI();
    }

    private void updateUI() {
        String labelText = gameLogic.isPlayingSecondHand() ? "Druga ręka: " : "Pierwsza ręka: ";
        labelText += gameLogic.getPlayer().getHand().isEmpty() ? 0 : (gameLogic.isPlayingSecondHand() ? gameLogic.getPlayer().getSecondHandValue() : gameLogic.getPlayer().getHandValue());
        playerLabel.setText(labelText);
        dealerLabel.setText("Karty krupiera: " + (gameLogic.isDealerTurnCompleted() && !gameLogic.getDealer().getHand().isEmpty() ? gameLogic.getDealer().getHandValue() : "?"));
        moneyLabel.setText("Żetony: " + gameLogic.getPlayer().getMoney());
        if (!betField.isDisabled()){
            betField.setText(String.valueOf(gameLogic.getPlayer().getBet()));
        }

        playerCards.getChildren().clear();
        secondHandCards.getChildren().clear();
        dealerCards.getChildren().clear();

        DropShadow cardShadow = new DropShadow(10, Color.BLACK);

        List<Card> playerHandList = gameLogic.getPlayer().getHand();
        int currentPlayerHandSize = playerHandList.size();
        for (int i = 0; i < currentPlayerHandSize; i++) {
            Card card = playerHandList.get(i);
            ImageView cardView = loadCardImage(card);
            cardView.setEffect(cardShadow);
            playerCards.getChildren().add(cardView);
            if (i >= prevPlayerHandSize && (placeBetButton.isDisabled() || hitButton.isDisabled())) {
                animateNewCard(cardView, i - prevPlayerHandSize);
            }
        }

        List<Card> playerSecondHandList = gameLogic.getPlayer().getSecondHand();
        int currentPlayerSecondHandSize = playerSecondHandList.size();
        for (int i = 0; i < currentPlayerSecondHandSize; i++) {
            Card card = playerSecondHandList.get(i);
            ImageView cardView = loadCardImage(card);
            cardView.setEffect(cardShadow);
            secondHandCards.getChildren().add(cardView);
            if (i >= prevPlayerSecondHandSize && (placeBetButton.isDisabled() || hitButton.isDisabled())) {
                animateNewCard(cardView, i - prevPlayerSecondHandSize);
            }
        }

        List<Card> dealerHandList = gameLogic.getDealer().getHand();
        int currentDealerHandSize = dealerHandList.size();
        for (int i = 0; i < currentDealerHandSize; i++) {
            ImageView cardView;
            boolean isNewVisibleCard = false;
            if (i == 0 && !gameLogic.isDealerTurnCompleted()) {
                cardView = loadBackCardImage();
            } else {
                cardView = loadCardImage(dealerHandList.get(i));
                if (gameLogic.isDealerTurnCompleted() && i >= prevDealerHandSize) {
                    isNewVisibleCard = true;
                } else if (!gameLogic.isDealerTurnCompleted() && i == 1 && prevDealerHandSize < 2) {
                    isNewVisibleCard = true;
                }
            }
            cardView.setEffect(cardShadow);
            dealerCards.getChildren().add(cardView);

            if (isNewVisibleCard) {
                int delayFactor = 0;
                if(gameLogic.isDealerTurnCompleted()) delayFactor = i - prevDealerHandSize;
                else if (!gameLogic.isDealerTurnCompleted() && i==1) delayFactor = 0;

                animateNewCard(cardView, delayFactor);
            }
        }

        prevPlayerHandSize = currentPlayerHandSize;
        prevPlayerSecondHandSize = currentPlayerSecondHandSize;
        prevDealerHandSize = currentDealerHandSize;


        if (gameLogic.isPlayingSecondHand()) {
            playerCards.setStyle("-fx-border-color: transparent; -fx-border-width: 2;");
            secondHandCards.setStyle("-fx-border-color: linear-gradient(to bottom, #FFD700, #FFA500); -fx-border-width: 2; -fx-border-radius: 10;");
        } else {
            playerCards.setStyle("-fx-border-color: linear-gradient(to bottom, #FFD700, #FFA500); -fx-border-width: 2; -fx-border-radius: 10;");
            secondHandCards.setStyle("-fx-border-color: transparent; -fx-border-width: 2;");
        }
    }

    private void animateNewCard(ImageView cardView, int delayIndex) {
        cardView.setOpacity(0);
        FadeTransition ft = new FadeTransition(Duration.millis(400), cardView);
        ft.setToValue(1.0);
        ft.setDelay(Duration.millis(delayIndex * 150));
        ft.play();
    }

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

    private ImageView loadBackCardImage() {
        String imagePath = "/back.png";
        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(90);
            imageView.setFitHeight(130);
            return imageView;
        } catch (Exception e) {
            System.err.println("Nie znaleziono pliku: " + imagePath);
            return new ImageView();
        }
    }

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

    private void decreaseBetField(int amount) {
        try {
            int currentBet = Integer.parseInt(betField.getText());
            currentBet -= amount;
            if (currentBet < 0) {
                currentBet = 0;
            }
            betField.setText(String.valueOf(currentBet));
        } catch (NumberFormatException e) {
            betField.setText("0");
        }
    }

    private void toggleBetting(boolean inGame) {
        boolean shuffleInProgress = shuffleOverlayPane != null && shuffleOverlayPane.isVisible();

        betField.setDisable(inGame || shuffleInProgress);
        placeBetButton.setDisable(inGame || shuffleInProgress);

        hitButton.setDisable(!inGame || shuffleInProgress);
        standButton.setDisable(!inGame || shuffleInProgress);
        doubleDownButton.setDisable(!inGame || !gameLogic.getPlayer().canDoubleDown() || gameLogic.isDealerTurnCompleted() || shuffleInProgress);
        splitButton.setDisable(!inGame || !gameLogic.getPlayer().canSplit() || gameLogic.isDealerTurnCompleted() || shuffleInProgress);
        insuranceButton.setDisable(!inGame || !gameLogic.canOfferInsurance() || gameLogic.isDealerTurnCompleted() || shuffleInProgress);

        statsButton.setDisable(shuffleInProgress);
        resetButton.setDisable(shuffleInProgress);
    }

    public static void main(String[] args) {
        launch(args);
    }
}