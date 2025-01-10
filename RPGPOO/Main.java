import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.text.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.*;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.RenderingHints;
import javax.sound.sampled.*;

public class Main {
    private Clip backgroundMusic;
    private Clip gameOverMusic;
    private int playerHitCount = 0;
    private int enemyHitCount = 0;
    private Timer combatTimer;
    private boolean canPlayerAct = false;
    private boolean isCombatActive = false;
    private boolean playerIsDodging = false;
    private JFrame frame;
    private JTextPane narrativeBox;
    private JProgressBar healthBar;
    private JProgressBar manaBar;
    private Timer textTimer;
    private JPanel statsPanel;
    private Image backgroundImage;
    private Enemy currentEnemy;
    private List<Item> inventory = new ArrayList<>();
    private int dodgeTurnsRemaining = 0;
    private boolean isPlayerTurn = true;

    public Main() {
        playBackgroundMusic();
        initializeStatusBars();
        showSplashScreen();
        setBackgroundImage("backgrounds/dungeon.jpg");
    }
    private void initializeStatusBars() {
        healthBar = new JProgressBar(0, 100);
        manaBar = new JProgressBar(0, 100);

        configureStatusBar(healthBar, "HP", new Color(255, 0, 0, 180));
        configureStatusBar(manaBar, "MP", new Color(0, 0, 255, 180));
    }

    private void initializeInventory() {
        inventory.add(new Item("Health Potion", "Restores 50 HP", ItemType.HEALTH_POTION, 50));
        inventory.add(new Item("Mana Potion", "Restores 30 MP", ItemType.MANA_POTION, 30));
        inventory.add(new Item("Strength Elixir", "Increases damage by 20%", ItemType.DAMAGE_BOOST, 20));
        inventory.add(new Item("Shield Potion", "Increases defense by 30%", ItemType.DEFENSE_BOOST, 30));
    }

    private void configureStatusBar(JProgressBar bar, String prefix, Color foreground) {
        bar.setStringPainted(true);
        bar.setString(prefix + ": 0/0");
        bar.setForeground(foreground);
        bar.setBackground(new Color(60, 60, 60, 180));
        bar.setBorderPainted(false);
        bar.setPreferredSize(new Dimension(200, 20));
    }


    private void showSplashScreen() {
        JFrame splashFrame = new JFrame("Shadow Adventurers");
        splashFrame.setUndecorated(true);


        JPanel splashPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(0, 0, 0),
                        0, getHeight(), new Color(60, 20, 80)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        splashPanel.setLayout(new BorderLayout());
        splashFrame.setSize(800, 600);

        JLabel titleLabel = new JLabel("Shadow Adventurers");
        titleLabel.setFont(new Font("Angel wish", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        splashPanel.add(titleLabel, BorderLayout.CENTER);
        splashFrame.add(splashPanel);
        splashFrame.setLocationRelativeTo(null);
        splashFrame.setVisible(true);


        javax.swing.Timer timer = new javax.swing.Timer(3000, e -> {
            splashFrame.dispose();
            initializeMainGame();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void initializeMainGame() {
        frame = new JFrame("Shadow Adventurers");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1024, 768);


        frame.setLayout(new BorderLayout(0, 0));


        frame.setUndecorated(true);


        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainPanel = createMainPanel();
        mainPanel.setBorder(null);


        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        statusPanel.setOpaque(false);
        statusPanel.add(healthBar);
        statusPanel.add(manaBar);
        mainPanel.add(statusPanel, BorderLayout.NORTH);


        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);


        statsPanel = createStatsPanel();
        contentPanel.add(statsPanel, BorderLayout.CENTER);


        createNarrativeBox();


        JPanel narrativePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0, 0, 0, 230));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        narrativePanel.setOpaque(false);
        narrativePanel.setPreferredSize(new Dimension(frame.getWidth(), 200));
        narrativePanel.add(narrativeBox);


        JScrollPane scrollPane = new JScrollPane(narrativePanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        contentPanel.add(scrollPane, BorderLayout.SOUTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        showIntroduction();
    }


    private JPanel createMainPanel() {
        return new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BICUBIC);

                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);


                    g2d.setColor(new Color(0, 0, 0, 100));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                } else {

                    Graphics2D g2d = (Graphics2D) g;
                    GradientPaint gradient = new GradientPaint(
                            0, 0, new Color(20, 20, 40),
                            0, getHeight(), new Color(60, 20, 80)
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
    }

    public void changeBackground(String sceneName) {
        String imagePath = "backgrounds/" + sceneName + ".jpg";
        setBackgroundImage(imagePath);
    }

    private void createNarrativeBox() {
        narrativeBox = new JTextPane() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(new Color(0, 0, 0, 0));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        narrativeBox.setEditable(false);
        narrativeBox.setOpaque(false);
        narrativeBox.setForeground(Color.WHITE);
        narrativeBox.setFont(new Font("Monospaced", Font.PLAIN, 16));
        narrativeBox.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));


        StyledDocument doc = narrativeBox.getStyledDocument();
        Style defaultStyle = doc.addStyle("defaultStyle", null);
        StyleConstants.setFontFamily(defaultStyle, "Monospaced");
        StyleConstants.setFontSize(defaultStyle, 16);
        StyleConstants.setForeground(defaultStyle, Color.WHITE);


        Style gameText = doc.addStyle("gameText", null);
        StyleConstants.setForeground(gameText, Color.WHITE);
        StyleConstants.setFontFamily(gameText, "Monospaced");
        StyleConstants.setFontSize(gameText, 16);
    }


    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        JTextArea statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.BOLD, 16));
        statsArea.setForeground(Color.WHITE);
        statsArea.setBackground(new Color(0, 0, 0, 0));

        panel.add(statsArea, BorderLayout.CENTER);
        return panel;
    }

    private void displayText(String text) {
        if (textTimer != null) {
            textTimer.cancel();
            textTimer.purge();
        }

        textTimer = new Timer();
        final int[] charIndex = {0};

        SwingUtilities.invokeLater(() -> {
            try {
                StyledDocument doc = narrativeBox.getStyledDocument();
                doc.remove(0, doc.getLength());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (charIndex[0] < text.length()) {
                    final int currentIndex = charIndex[0];
                    SwingUtilities.invokeLater(() -> {
                        try {
                            StyledDocument doc = narrativeBox.getStyledDocument();
                            doc.insertString(doc.getLength(),
                                    String.valueOf(text.charAt(currentIndex)),
                                    doc.getStyle("gameText"));
                            narrativeBox.setCaretPosition(doc.getLength());
                        } catch (BadLocationException e) {
                            e.printStackTrace();
                        }
                    });
                    charIndex[0]++;
                } else {
                    textTimer.cancel();
                    textTimer.purge();
                }
            }
        };


        textTimer.scheduleAtFixedRate(task, 0, 50);
    }


    private void showIntroduction() {
        String[] introText = {
                "In the depths of the cosmos, a tale unfolds...",
                "You find yourself in a realm between worlds, where shadow and light dance in eternal conflict.",
                "The Astral Gate, your only path home, lies sealed behind the trials of the Fallen.",
                "Choose your path wisely, for your journey through the Shadow Realm begins now..."
        };

        displayTextSequence(introText, this::showCharacterSelection);
    }

    private void displayTextSequence(String[] texts, Runnable onComplete) {
        final int[] currentIndex = {0};
        Timer sequenceTimer = new Timer();
        AtomicBoolean isRunning = new AtomicBoolean(true);


        sequenceTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isRunning.get()) {
                    sequenceTimer.cancel();
                    sequenceTimer.purge();
                    return;
                }

                if (currentIndex[0] < texts.length) {
                    final int index = currentIndex[0];
                    SwingUtilities.invokeLater(() -> {
                        displayText(texts[index]);
                    });
                    currentIndex[0]++;
                } else {
                    isRunning.set(false);
                    sequenceTimer.cancel();
                    sequenceTimer.purge();
                    if (onComplete != null) {
                        SwingUtilities.invokeLater(onComplete);
                    }
                }
            }
        }, 0, 6000);
    }

    private void setBackgroundImage(String imagePath) {
        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));

            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            int width = gd.getDisplayMode().getWidth();
            int height = gd.getDisplayMode().getHeight();

            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = resizedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.drawImage(originalImage, 0, 0, width, height, null);
            g2d.dispose();

            backgroundImage = resizedImage;

            if (frame != null) {
                frame.repaint();
            }
        } catch (IOException e) {
            System.err.println("Error loading background image: " + e.getMessage());
            backgroundImage = null;
        }
    }


    private void showCharacterSelection() {
        updateStatsPanel("""
    === Available Classes ===
    
    WARRIOR               MAGE                 THIEF
    ╔══════════╗         ╔══════════╗         ╔══════════╗
    ║ HP: 120  ║         ║ HP: 80   ║         ║ HP: 90   ║
    ║ MP: 50   ║         ║ MP: 150  ║         ║ MP: 70   ║
    ║ DMG: 12  ║         ║ DMG: 15  ║         ║ DMG: 14  ║
    ╚══════════╝         ╚══════════╝         ╚══════════╝
    
    PALADIN              RANGER
    ╔══════════╗         ╔══════════╗
    ║ HP: 110  ║         ║ HP: 95   ║
    ║ MP: 80   ║         ║ MP: 60   ║
    ║ DMG: 11  ║         ║ DMG: 13  ║
    ╚══════════╝         ╚══════════╝""");

        displayText("Choose your class...");
        createCharacterSelectionButtons();
    }

    private void createCharacterSelectionButtons() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        buttonPanel.setOpaque(false);

        String[] classes = {"Warrior", "Mage", "Thief", "Paladin", "Ranger"};
        for (String className : classes) {
            JButton button = new JButton(className);
            styleButton(button);
            button.addActionListener(e -> selectCharacter(className));
            buttonPanel.add(button);
        }


        for (Component comp : frame.getContentPane().getComponents()) {
            if (comp instanceof JPanel) {
                ((JPanel) comp).add(buttonPanel, BorderLayout.SOUTH);
                break;
            }
        }
        frame.revalidate();
        frame.repaint();
    }

    private void updateStatsPanel(String text) {

        statsPanel.removeAll();


        JTextArea statsArea = new JTextArea(text);
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.BOLD, 16));
        statsArea.setForeground(Color.WHITE);
        statsArea.setBackground(new Color(0, 0, 0, 0));


        statsPanel.add(statsArea);


        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(60, 30, 70));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));


        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(80, 40, 90));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(60, 30, 70));
            }
        });
    }

    private void playBackgroundMusic() {
        try {
            if (backgroundMusic != null) {
                backgroundMusic.stop();
                backgroundMusic.close();
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("music/background.wav.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);


            FloatControl gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-10.0f);
        } catch (Exception e) {
            System.err.println("Error playing background music: " + e.getMessage());
        }
    }

    private void playGameOverMusic() {
        try {

            if (backgroundMusic != null) {
                backgroundMusic.stop();
                backgroundMusic.close();
            }


            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("music/gameover.wav.wav"));
            gameOverMusic = AudioSystem.getClip();
            gameOverMusic.open(audioStream);
            gameOverMusic.start();


            FloatControl gainControl = (FloatControl) gameOverMusic.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-5.0f);
        } catch (Exception e) {
            System.err.println("Error playing game over music: " + e.getMessage());
        }
    }

    private void selectCharacter(String className) {

        if (textTimer != null) {
            textTimer.cancel();
            textTimer.purge();
        }


        SwingUtilities.invokeLater(() -> {

            for (Component comp : frame.getContentPane().getComponents()) {
                if (comp instanceof JPanel mainPanel) {

                    statsPanel.removeAll();
                    statsPanel.revalidate();
                    statsPanel.repaint();


                    for (Component innerComp : mainPanel.getComponents()) {
                        if (innerComp instanceof JPanel && ((JPanel) innerComp).getLayout() instanceof GridLayout) {
                            mainPanel.remove(innerComp);
                            break;
                        }
                    }
                }
            }

            frame.revalidate();
            frame.repaint();


            switch (className.toLowerCase()) {
                case "warrior" -> player = new Warrior(className);
                case "mage" -> player = new Mage(className);
                case "thief" -> player = new Thief(className);
                case "paladin" -> player = new Paladin(className);
                case "ranger" -> player = new Ranger(className);
            }


            try {
                narrativeBox.getStyledDocument().remove(0, narrativeBox.getStyledDocument().getLength());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }


            displayText("You have chosen the " + className + ".\n");


            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(() -> {
                        displayText("\nBackstory: " + player.getBackstory() + "\n");


                        Timer startGameTimer = new Timer();
                        startGameTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                SwingUtilities.invokeLater(() -> startGame());
                            }
                        }, 8000);
                    });
                }
            }, 3000);
        });
    }

    private void updateStatusBars() {
        healthBar.setMaximum(player.maxHealthPoints);
        healthBar.setValue(player.healthPoints);
        healthBar.setString("HP: " + player.healthPoints + "/" + player.maxHealthPoints);

        manaBar.setMaximum(player.maxManaPoints);
        manaBar.setValue(player.manaPoints);
        manaBar.setString("MP: " + player.manaPoints + "/" + player.maxManaPoints);
    }


    private void initializeStatsPanel() {
        statsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE),
                "Character Stats",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                Color.WHITE
        ));
    }

    private void startGame() {

        changeBackground("dungeon");


        SwingUtilities.invokeLater(() -> {
            updateStatusBars();
            createCombatButtons();
            disableCombatButtons();
            frame.revalidate();
            frame.repaint();
        });


        Timer narrativeTimer = new Timer();
        narrativeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    displayText("\nYour journey begins...\n");

                    Timer nextTimer = new Timer();
                    nextTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            SwingUtilities.invokeLater(() -> {
                                displayText("\nThe shadows of the dungeon loom before you...\n");

                                Timer encounterTimer = new Timer();
                                encounterTimer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        SwingUtilities.invokeLater(() -> startFirstEncounter());
                                    }
                                }, 2000);
                            });
                        }
                    }, 2000);
                });
            }
        }, 1000);
    }

    private void createCombatButtons() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        buttonPanel.setOpaque(false);

        String[] actions = {"Attack", "Dodge", "Heal", "Item"};
        for (String action : actions) {
            JButton button = new JButton(action);
            styleButton(button);
            button.addActionListener(e -> handleCombatAction(action));
            buttonPanel.add(button);
        }

        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.revalidate();
    }

    private void handleCombatAction(String action) {
        if (!isCombatActive || !canPlayerAct) {
            return;
        }

        switch (action) {
            case "Attack":
                performAttack();
                break;
            case "Dodge":
                if (performDodge()) {
                    displayText("\nYou prepare to dodge the next attack!\n");
                }
                break;
            case "Heal":
                if (performHeal()) {
                    displayText("\nYou heal yourself!\n");
                }
                break;
            case "Item":
                showInventory();
                break;
        }

        if (!isCombatActive) return;


        if (combatTimer != null) {
            combatTimer.cancel();
            combatTimer.purge();
        }
        startCombatTimer();
    }


    private void performAttack() {
        playerHitCount++;
        displayText("\nYou attack the enemy! (" + playerHitCount + "/3)\n");

        if (playerHitCount >= 3) {
            handleEnemyDeath();
            return;
        }
    }


    private void startEnemyTurn() {
        if (combatTimer != null) {
            combatTimer.cancel();
        }

        combatTimer = new Timer();
        combatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (currentEnemy != null && currentEnemy.healthPoints > 0) {
                        performEnemyAttack();
                    }
                });
            }
        }, 1500);
    }

    private void checkCombatStatus() {
        if (playerHitCount >= 3) {
            handleEnemyDeath();
        }
    }

    private void performEnemyAttack() {
        if (!isCombatActive) return;

        canPlayerAct = false;
        disableCombatButtons();

        if (!playerIsDodging) {
            player.healthPoints -= currentEnemy.damage;
            displayText("\nThe enemy attacks you for " + currentEnemy.damage + " damage!\n");
        } else {
            displayText("\nYou successfully dodged the attack!\n");
            playerIsDodging = false;
        }

        updateStatusBars();
        enemyHitCount++;

        if (!player.isAlive() || enemyHitCount >= 3) {
            handlePlayerDeath();
            return;
        }


        Timer actionTimer = new Timer();
        actionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    displayText("\nIt's your turn to act!\n");
                    enableCombatButtons();
                    canPlayerAct = true;
                    startCombatTimer();
                });
            }
        }, 3000);
    }


    private boolean performDodge() {
        if (player.manaPoints >= 10) {
            player.manaPoints -= 10;
            playerIsDodging = true;
            updateStatusBars();
            return true;
        }
        displayText("\nNot enough mana to dodge!\n");
        return false;
    }

    private boolean performHeal() {
        if (player.manaPoints >= 15) {
            player.manaPoints -= 15;
            int healAmount = 30;
            player.healthPoints = Math.min(player.maxHealthPoints,
                    player.healthPoints + healAmount);
            updateStatusBars();
            return true;
        }
        displayText("\nNot enough mana to heal!\n");
        return false;
    }


    private void showInventory() {
        JDialog inventoryDialog = new JDialog(frame, "Inventory", true);
        inventoryDialog.setLayout(new BorderLayout());
        inventoryDialog.setSize(400, 300);

        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));

        for (Item item : inventory) {
            JButton itemButton = new JButton(item.name);
            styleButton(itemButton);
            itemButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            itemButton.addActionListener(e -> {
                useItem(item);
                inventoryDialog.dispose();
            });

            itemButton.setToolTipText(item.description);

            itemsPanel.add(itemButton);
            itemsPanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        inventoryDialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        styleButton(closeButton);
        closeButton.addActionListener(e -> inventoryDialog.dispose());
        inventoryDialog.add(closeButton, BorderLayout.SOUTH);

        inventoryDialog.setLocationRelativeTo(frame);
        inventoryDialog.setVisible(true);
    }

    private void startFirstEncounter() {
        currentEnemy = new Enemy("Shadow Wraith", 100, 15);
        isCombatActive = true;
        playerHitCount = 0;
        enemyHitCount = 0;

        displayText("\nA Shadow Wraith appears before you!\n");
        displayText("\nHit it 3 times within 5 seconds to win!\n");
        displayText("\nBut if you take too long, it will attack. 3 enemy hits and you lose!\n");

        // Start with enemy attack
        Timer startTimer = new Timer();
        startTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    displayText("\nThe enemy prepares to attack...\n");
                    Timer attackTimer = new Timer();
                    attackTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            SwingUtilities.invokeLater(() -> performEnemyAttack());
                        }
                    }, 8000);
                });
            }
        }, 3000);
    }


    private void startCombatTimer() {
        if (combatTimer != null) {
            combatTimer.cancel();
            combatTimer.purge();
        }

        combatTimer = new Timer();
        combatTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isCombatActive && canPlayerAct) {
                    SwingUtilities.invokeLater(() -> {
                        displayText("\nYou took too long to act!\n");
                        performEnemyAttack();
                    });
                }
            }
        }, 5000);
    }

    private void enableCombatButtons() {
        for (Component comp : frame.getContentPane().getComponents()) {
            if (comp instanceof JPanel panel) {
                for (Component button : panel.getComponents()) {
                    if (button instanceof JButton) {
                        button.setEnabled(true);
                    }
                }
            }
        }
    }

    private void disableCombatButtons() {
        for (Component comp : frame.getContentPane().getComponents()) {
            if (comp instanceof JPanel panel) {
                for (Component button : panel.getComponents()) {
                    if (button instanceof JButton) {
                        button.setEnabled(false);
                    }
                }
            }
        }
    }


    private void useItem(Item item) {
        switch (item.type) {
            case HEALTH_POTION:
                int healAmount = Math.min(item.value, player.maxHealthPoints - player.healthPoints);
                player.healthPoints += healAmount;
                displayText("\nYou used a Health Potion and recovered " + healAmount + " HP!\n");
                break;

            case MANA_POTION:
                int manaAmount = Math.min(item.value, player.maxManaPoints - player.manaPoints);
                player.manaPoints += manaAmount;
                displayText("\nYou used a Mana Potion and recovered " + manaAmount + " MP!\n");
                break;

            case DAMAGE_BOOST:
                player.damage = (int)(player.damage * (1 + item.value/100.0));
                displayText("\nYour damage has been increased by " + item.value + "%!\n");
                break;

            case DEFENSE_BOOST:
                playerIsDodging = true;
                dodgeTurnsRemaining = 3;
                displayText("\nYour defense has been increased!\n");
                break;
        }

        inventory.remove(item);
        updateStatusBars();
        startEnemyTurn();
    }


    private void handleEnemyDeath() {
        isCombatActive = false;
        if (combatTimer != null) {
            combatTimer.cancel();
            combatTimer.purge();
        }

        inventory.add(new Item("Health Potion", "Restores 50 HP", ItemType.HEALTH_POTION, 50));
        displayText("\nVictory! You have defeated the enemy!\n");
        displayText("You received a Health Potion!\n");
        showGameOverScreen();
    }



    private void handlePlayerDeath() {
        isCombatActive = false;
        if (combatTimer != null) {
            combatTimer.cancel();
            combatTimer.purge();
        }
        displayText("\nYou have been defeated...\n");
        showGameOverScreen();
    }


    private void cleanup() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.close();
        }
        if (gameOverMusic != null) {
            gameOverMusic.stop();
            gameOverMusic.close();
        }
    }

    private void showGameOverScreen() {
        playGameOverMusic();

        frame.getContentPane().removeAll();

        JPanel gameOverPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        JLabel gameOverLabel = new JLabel("GAME OVER");
        gameOverLabel.setFont(new Font("Alagard", Font.BOLD, 72));
        gameOverLabel.setForeground(Color.RED);
        gameOverLabel.setHorizontalAlignment(JLabel.CENTER);

        JButton restartButton = new JButton("Restart Game");
        styleButton(restartButton);
        restartButton.addActionListener(e -> {

            if (gameOverMusic != null) {
                gameOverMusic.stop();
                gameOverMusic.close();
            }
            frame.dispose();
            SwingUtilities.invokeLater(Main::new);
        });

        gameOverPanel.add(gameOverLabel, BorderLayout.CENTER);
        gameOverPanel.add(restartButton, BorderLayout.SOUTH);

        frame.add(gameOverPanel);
        frame.revalidate();
        frame.repaint();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }



        SwingUtilities.invokeLater(Main::new);
    }


    private Protagonist player;
}
