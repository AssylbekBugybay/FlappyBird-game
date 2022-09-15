import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFrame;
import javax.swing.Timer;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;


public class FlappyBird implements ActionListener, MouseListener, KeyListener
{

    public static FlappyBird flappyBird;

    public final int WIDTH = 800, HEIGHT = 800;

    public Renderer renderer;

    public Rectangle bird;

    public boolean pausing;

    public ArrayList<Rectangle> columns;

    public int ticks, yMotion, score;

    public boolean gameOver, started;

    public Random rand;

    JFrame jframe;

    public String highscore;

    public Clip clip;

    public Clip clip2;

    public boolean soundPlaying;

    public boolean soundInit;

    public String musicPath = "C:\\Users\\asbu273407\\eclipse-workspace\\FlappyBird\\src\\DaniAll - Dance Floor (Radio Edit).wav";

    public String laughPath = "C:\\Users\\asbu273407\\eclipse-workspace\\FlappyBird\\src\\devil's laugh.wav";

    public String highscorePath = "C:\\Users\\asbu273407\\eclipse-workspace\\FlappyBird\\src\\Highscore.txt";

    int speed = 10;

    int space = 300;

    public boolean hardcoreMode;

    public int test = 0;

    public Rectangle obstacle;

    public FlappyBird() throws IOException {
        FileReader reader = new FileReader(highscorePath);
        BufferedReader bufferedReader = new BufferedReader(reader);

        highscore = bufferedReader.readLine();
        reader.close();

        soundPlaying = false;
        soundInit = true;

        hardcoreMode = false;

        jframe = new JFrame();
        Timer timer = new Timer(20, this);

        renderer = new Renderer();
        rand = new Random();
        pausing = false;

        jframe.add(renderer);
        jframe.setTitle("Flappy Square");
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setSize(WIDTH, HEIGHT);
        jframe.addMouseListener(this);
        jframe.addKeyListener(this);
        jframe.setResizable(false);
        jframe.setVisible(true);

        bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
        columns = new ArrayList<Rectangle>();

        addColumn(true);
        addColumn(true);
        addColumn(true);
        addColumn(true);


        timer.start();
    }

    public void addColumn(boolean start)
    {
        int width = 100;
        int height = 50 + rand.nextInt(300);

        if (start)
        {
            columns.add(new Rectangle(WIDTH + width + columns.size() * 300, HEIGHT - height - 120, width, height));
            columns.add(new Rectangle(WIDTH + width + (columns.size() - 1) * 300, 0, width, HEIGHT - height - space));
        }
        else
        {
            columns.add(new Rectangle(columns.get(columns.size() - 1).x + 600, HEIGHT - height - 120, width, height));
            columns.add(new Rectangle(columns.get(columns.size() - 1).x, 0, width, HEIGHT - height - space));
        }
    }

    public void paintColumn(Graphics g, Rectangle column)
    {
        g.setColor(Color.green.darker());
        g.fillRect(column.x, column.y, column.width, column.height);
    }

    public void paintObstacle(Graphics g, Rectangle obstacle) {
        g.setColor(Color.lightGray.darker());
        g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
    }

    public void jump()
    {
        if (gameOver)
        {
            bird = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
            columns.clear();
            yMotion = 0;
            score = 0;
            speed = 10;
            space = 300;
            test = 0;
            obstacle = null;

            addColumn(true);
            addColumn(true);
            addColumn(true);
            addColumn(true);

            gameOver = false;

        }

        if (!started)
        {
            started = true;
        }
        else if (!gameOver)
        {
            if (yMotion > 0)
            {
                yMotion = 0;
            }

            yMotion -= 10;
        }

        if (!soundPlaying && soundInit) {
            soundPlaying = true;
            soundInit = false;
            playSound(musicPath);
        } else if (!soundPlaying) {
            soundPlaying = true;
            clip.start();
        }
    }

    public void playSound(String path) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(path).getAbsoluteFile());

            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void playDevilSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(laughPath).getAbsoluteFile());

            clip2 = AudioSystem.getClip();
            clip2.open(audioInputStream);
            clip2.start();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        //int speed = 10;
        ticks++;


        if (started && !pausing) {

            for (int i = 0; i < columns.size(); i++) {
                Rectangle column = columns.get(i);

                column.x -= speed;
            }

            //Flying animation of obstacle
            if (obstacle != null) {
                obstacle.x -= 30;
                if (obstacle.x <= 0)
                    obstacle = null;
            }

            if (ticks % 2 == 0 && yMotion < 15) {
                yMotion += 2;
            }

            for (int i = 0; i < columns.size(); i++) {
                Rectangle column = columns.get(i);

                if (column.x + column.width < 0) {
                    columns.remove(column);

                    if (column.y == 0) {
                        addColumn(false);
                    }
                }
            }

            bird.y += yMotion;

            for (Rectangle column : columns) {
                /*if (column.y == 0 && bird.x + bird.width / 2 > column.x + column.width / 2 - 10 && bird.x + bird.width / 2 < column.x + column.width / 2 + 10)
                {
                    score++;
                    speed++;

                    if(space <= 500)
                        space += 10;

                    if(speed == 13)
                        score--;
                    if(speed == 14)
                        score--;
                } */

                //check if bird passed column
                if (column.x <= bird.x && column.width == 100 && !gameOver) {
                    test++;
                    // Needed to fix the increased twice bug
                    if (test % 2 == 0)
                        score++;
                    if (hardcoreMode) {
                        if (score % 4 == 0)
                            speed++;
                        // Increase space between columns
                        if (space <= 400)
                            space += 5;
                    }
                    column.width = 101;

                    // 50% chance of spawning obstacle
                    if((int)Math.round(Math.random()) == 1) {
                        if (obstacle == null && hardcoreMode) {
                            int randomY = ThreadLocalRandom.current().nextInt(200, 500 + 1);
                            obstacle = new Rectangle(900, randomY, 20, 20);
                        }
                    }
                }

                if (column.intersects(bird)) {
                    gameOver = true;

                    if (bird.x <= column.x) {
                        bird.x = column.x - bird.width;

                    } else {
                        if (column.y != 0) {
                            bird.y = column.y - bird.height;
                        } else if (bird.y < column.height) {
                            bird.y = column.height;
                        }
                    }
                }
            }

            if (obstacle != null) {
                if (obstacle.intersects(bird)) {
                    gameOver = true;
                }
            }

            if (bird.y > HEIGHT - 120 || bird.y < 0)
            {
                gameOver = true;
            }

            if (bird.y + yMotion >= HEIGHT - 120)
            {
                bird.y = HEIGHT - 120 - bird.height;
                gameOver = true;
            }
        }

        renderer.repaint();
    }

    public void repaint(Graphics g)
    {
        g.setColor(Color.cyan);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.orange);
        g.fillRect(0, HEIGHT - 120, WIDTH, 120);

        g.setColor(Color.green);
        g.fillRect(0, HEIGHT - 120, WIDTH, 20);

        g.setColor(Color.red);
        g.fillRect(bird.x, bird.y, bird.width, bird.height);

        for (Rectangle column : columns)
        {
            paintColumn(g, column);
        }

        if (obstacle != null) {
            paintObstacle(g, obstacle);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", 1, 100));

        if (!started)
        {
            g.drawString("Click to start!", 75, HEIGHT / 2 - 50);
        }

        if (gameOver)
        {
            clip.stop();
            soundPlaying = false;
            soundInit = true;

            if (score > Integer.parseInt(highscore)) {
                highscore = String.valueOf(score);
                try {
                    FileWriter writer = new FileWriter(highscorePath, false);
                    BufferedWriter bufferedWriter = new BufferedWriter(writer);
                    bufferedWriter.write(highscore);
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            g.drawString("Game Over!", 100, HEIGHT / 2 - 50);
            g.drawString("Highscore: " + highscore, 100, HEIGHT / 4 - 50);

        }

        if (!gameOver && started)
        {
            g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
        }

        if(pausing) {
            if (!soundInit) {
                clip.stop();
                soundPlaying = false;
            }

            g.setColor(new Color(0,0,0,127));
            g.fillRect(0, 0, WIDTH, HEIGHT);

            Graphics2D graphics2D = (Graphics2D) g;
            graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Font font = g.getFont().deriveFont( 50.0f );
            //int widthText = g.getFontMetrics().stringWidth("PAUSED");

            g.setColor(new Color(255,255,255,255));
            g.setFont( font );
            g.drawString("PAUSED",300,HEIGHT/2 - 310);

            g.setColor(new Color(0,0,0,255));
            g.fillRect(248, 248, 304, 104);
            g.setColor(new Color(51,51,51, 255));
            g.fillRect(250, 250, 300, 100);


            graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            font = g.getFont().deriveFont( 50.0f );
            //widthText = g.getFontMetrics().stringWidth("EXIT GAM");

            g.setColor(new Color(255,255,255,255));
            g.setFont( font );
            g.drawString("RESUME",290,320);


            g.setColor(new Color(0,0,0,255));
            g.fillRect(248, 373, 304, 104);
            g.setColor(new Color(51,51,51, 255));
            g.fillRect(250, 375, 300, 100);


            graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            font = g.getFont().deriveFont( 50.0f );
            //widthText = g.getFontMetrics().stringWidth("EXIT GAM");

            g.setColor(new Color(255,255,255,255));
            g.setFont( font );
            g.drawString("EXIT GAME",262,445);

            // Hardcore
            g.setColor(new Color(0,0,0,255));
            g.fillRect(248, 498, 304, 104);
            if(hardcoreMode)
                g.setColor(new Color(200,51,51, 255));
            else
                g.setColor(new Color(51,51,51, 255));
            g.fillRect(250, 500, 300, 100);


            graphics2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            font = g.getFont().deriveFont( 50.0f );
            //widthText = g.getFontMetrics().stringWidth("EXIT GAM");

            g.setColor(new Color(255,255,255,255));
            g.setFont( font );
            g.drawString("HARDCORE",257,565);
        }


    }

    public static void main(String[] args) throws IOException {
        flappyBird = new FlappyBird();
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
    	System.out.println(e.getX() + ","+ e.getY());
        if(!pausing) {
            jump();
        } else {
            //Button actions
            if(e.getX()>250 && e.getX()<550 && e.getY()>250 && e.getY()<350) {
                pausing = !pausing;
            } else if(e.getX()>250 && e.getX()<550 && e.getY()>375 && e.getY()<475) {
                jframe.dispose();
                System.exit(0);
            } else if(e.getX()>250 && e.getX()<550 && e.getY()>500 && e.getY()<600) {
                // Can only click if run is over
                if(gameOver || !started) {
                    hardcoreMode = !hardcoreMode;
                    if (hardcoreMode)
                        playDevilSound();
                    if (!hardcoreMode)
                        clip2.stop();
                }
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e)
    {

    }

    @Override
    public void mousePressed(MouseEvent e)
    {
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }

    @Override
    public void keyTyped(KeyEvent e)
    {

    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_SPACE && !pausing)
        {
            jump();
        } else if(keyCode == 27) {
            pausing = !pausing;
            if (!pausing && !soundPlaying && !soundInit) {
                clip.start();
                soundPlaying = true;
            }
        }
    }

}