// CarDodgerGame.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer;
import java.sql.*;

public class CarDodgerGame extends JPanel implements ActionListener, KeyListener {
    private int obstacleSpawnCooldown = 0;
    private final int spawnDelay = 30;
    private Timer timer;
    private int carX;
    private final int carY = 500;
    private final int carWidth = 50, carHeight = 80;
    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<Image> obstacleImages = new ArrayList<>();
    private int backgroundY1 = 0, backgroundY2 = -600;
    private int score = 0;
    private int lives = 2;
    private int distance = 0;
    private boolean gameOver = false;
    private boolean gameStarted = false;
    private boolean invincible = false;
    private long invincibleStartTime = 0;
    private Image carImage, backgroundImg;
    private final int roadLeft = 90;
    private final int roadRight = 310;
    private final ScoreDAO scoreDAO = new ScoreDAO();

    public CarDodgerGame() {
        setPreferredSize(new Dimension(400, 600));
        setFocusable(true);
        addKeyListener(this);
        setBackground(Color.BLACK);

        carImage = new ImageIcon(getClass().getResource("/assets/car.png")).getImage();
        backgroundImg = new ImageIcon(getClass().getResource("/assets/background.png")).getImage();
        obstacleImages.add(new ImageIcon(getClass().getResource("/assets/obstacle1.png")).getImage());
        obstacleImages.add(new ImageIcon(getClass().getResource("/assets/obstacle2.png")).getImage());

        carX = (roadLeft + roadRight - carWidth) / 2;
        timer = new Timer(20, this);
    }

    private class Obstacle {
        Rectangle rect;
        Image image;
        Obstacle(Rectangle rect, Image image) {
            this.rect = rect;
            this.image = image;
        }
    }

    private void spawnObstacle() {
        Random rand = new Random();
        int laneWidth = roadRight - roadLeft - carWidth;
        int x = roadLeft + rand.nextInt(laneWidth);
        Rectangle rect = new Rectangle(x, 0, carWidth, carHeight);
        Image img = obstacleImages.get(rand.nextInt(obstacleImages.size()));
        obstacles.add(new Obstacle(rect, img));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImg, 0, backgroundY1, getWidth(), getHeight(), null);
        g.drawImage(backgroundImg, 0, backgroundY2, getWidth(), getHeight(), null);

        if (!gameStarted) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("Press ENTER to Start", 60, 300);
            return;
        }

        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("Game Over", 100, 300);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press ENTER to Restart", 100, 350);
            return;
        }

        if (!invincible || (System.currentTimeMillis() / 200) % 2 == 0) {
            g.drawImage(carImage, carX, carY, carWidth, carHeight, null);
        }

        for (Obstacle obs : obstacles) {
            g.drawImage(obs.image, obs.rect.x, obs.rect.y, carWidth, carHeight, null);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Score: " + score, 10, 20);
        g.drawString("Lives: " + lives, 10, 40);
        g.drawString("Distance: " + distance + "m", 10, 60);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver || !gameStarted) return;

        backgroundY1 += 5;
        backgroundY2 += 5;
        if (backgroundY1 >= 600) backgroundY1 = -600;
        if (backgroundY2 >= 600) backgroundY2 = -600;

        List<Obstacle> toRemove = new ArrayList<>();
        for (Obstacle obs : obstacles) {
            obs.rect.y += 10;
            if (obs.rect.y > getHeight()) {
                toRemove.add(obs);
                score++;
            }
            if (!invincible && obs.rect.intersects(new Rectangle(carX, carY, carWidth, carHeight))) {
                lives--;
                if (lives < 0) {
                    gameOver = true;
                    timer.stop();
                    scoreDAO.insertScore(score, distance);
                } else {
                    invincible = true;
                    invincibleStartTime = System.currentTimeMillis();
                }
            }
        }
        obstacles.removeAll(toRemove);

        obstacleSpawnCooldown++;
        if (obstacleSpawnCooldown >= spawnDelay) {
            int minGap = 200 + Math.min(score * 2, 300);
            if (obstacles.isEmpty() || obstacles.get(obstacles.size() - 1).rect.y > minGap) {
                spawnObstacle();
                obstacleSpawnCooldown = 0;
            }
        }

        if (invincible && System.currentTimeMillis() - invincibleStartTime > 2000) {
            invincible = false;
        }

        distance++;
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameStarted || gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                gameStarted = true;
                gameOver = false;
                score = 0;
                lives = 2;
                distance = 0;
                invincible = false;
                carX = (roadLeft + roadRight - carWidth) / 2;
                obstacles.clear();
                spawnObstacle();
                timer.start();
            }
            return;
        }

        if (e.getKeyCode() == KeyEvent.VK_LEFT && carX > roadLeft) {
            carX -= 25;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && carX < roadRight - carWidth) {
            carX += 25;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("🚗 Car Racing Dodger");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new CarDodgerGame());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

// ScoreDAO.java
class ScoreDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/cardodger";
    private static final String USER = "root";
    private static final String PASS = "";

    public void insertScore(int score, int distance) {
        String query = "INSERT INTO scores (score, distance) VALUES (?, ?)";
        try (Connection con = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, score);
            ps.setInt(2, distance);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
