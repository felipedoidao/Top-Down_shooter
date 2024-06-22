package com.gcstudios.main;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;

import com.gcstudios.entities.Enemy;
import com.gcstudios.entities.Entity;
import com.gcstudios.entities.Player;
import com.gcstudios.entities.Shoot;
import com.gcstudios.graficos.Spritesheet;
import com.gcstudios.graficos.Ui;
import com.gcstudios.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener{

    private static final long serialVersionUID = 1L;
    public static JFrame frame;
    private Thread thread;
    private boolean isRunning = true;
    public static final int WIDTH = 240;
    public static final int HEIGHT = 160;
    public static final int SCALE = 3;
    
    private int CUR_LEVEL = 1; //MAX_LEVEL = 1;
    private BufferedImage image;

    public static List<Entity> entities;
    public static List<Enemy> enemies;
    public static List<Shoot> bullets;

    public static Spritesheet spritesheet;

    public static World world;

    public static Player player;

    public static Random rand;

    public Ui ui;

    public static String gameState = "MENU";
    private boolean restartGame = false;

    Menu menu;


    public  Game(){
        rand = new Random();
        addKeyListener(this);
        addMouseListener(this);
        setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
        initFrame();
        //Inicializando jogo
        ui = new Ui();
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        entities = new ArrayList<Entity>();
        enemies = new ArrayList<Enemy>();
        bullets = new ArrayList<Shoot>();

        spritesheet = new Spritesheet("/sprites.png");
        player = new Player(16, 16, 16, 16, spritesheet.getSprite(32, 0, 16, 16));
        entities.add(player);
        world = new World("/level1.png");

        menu = new Menu();
    }

    public void initFrame() {
        frame = new JFrame("Game");
        frame.add(this);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public synchronized void start(){
        thread = new Thread(this);
        isRunning = true;
        thread.start();
    }

    public synchronized void stop(){
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }

    public void tick(){
        //Clips.music.loop();
        player.updateCamera();
        if(gameState == "NORMAL"){
            for(int i = 0; i < entities.size(); i++){
                Entity e = entities.get(i);
                e.tick();
            }
            for(int i = 0; i < bullets.size(); i++){
                bullets.get(i).tick();
            }

            if(enemies.size() == 0){
                gameState = "GANHOU";
                if(restartGame){
                    gameState = "NORMAL";
                    CUR_LEVEL = 1;
                    String newWorld = "level"+CUR_LEVEL+".png";
                    World.restartGame(newWorld);
                }
                /*CUR_LEVEL++;
                if(CUR_LEVEL > MAX_LEVEL){
                    CUR_LEVEL = 1;
                }
                String newWorld = "level"+CUR_LEVEL+".png";
                World.restartGame(newWorld);*/
            }
        }
        else if(gameState == "GAME_OVER" || gameState == "GANHOU"){
            if(restartGame){
                gameState = "NORMAL";
                CUR_LEVEL = 1;
                String newWorld = "level"+CUR_LEVEL+".png";
                World.restartGame(newWorld);
            }
        }
        else if(gameState == "MENU"){
            menu.tick();
        }
    }

    public void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = image.getGraphics();
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, WIDTH, HEIGHT);
        /***/
        world.render(g);

        for(int i = 0; i < entities.size(); i++){
            Entity e = entities.get(i);
            e.render(g);
        }
        for(int i = 0; i < bullets.size(); i++){
            bullets.get(i).render(g);
        }
        ui.render(g);
        /***/
        g.dispose();
        g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE, null);
        if(gameState == "GAME_OVER"){
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(0,0,0,100));
            g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
            g.setFont(new Font("arial", Font.BOLD, 50));
            g.setColor(Color.white);
            g.drawString("Game Over", (WIDTH*SCALE/2) - 130, HEIGHT*SCALE/2);
            g.drawString(">Pressione Enter<", (WIDTH*SCALE/2) - 215, (HEIGHT*SCALE/2) + 60);
        }
        else if(gameState == "MENU"){
            menu.render(g);
        }
        else if(gameState == "GANHOU"){
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(0,0,0,100));
            g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
            g.setFont(new Font("arial", Font.BOLD, 50));
            g.setColor(Color.white);
            g.drawString("Ganhou", (WIDTH*SCALE/2) - 100, HEIGHT*SCALE/2);
            g.drawString(">Pressione Enter<", (WIDTH*SCALE/2) - 215, (HEIGHT*SCALE/2) + 60);
        }
        bs.show();
        
    }

    public void run() {
        long lastTime = System.nanoTime();
        double  amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        int frames = 0;
        double timer = System.currentTimeMillis();
        requestFocus();
        while(isRunning){
            long now = System.nanoTime();
            delta+= (now - lastTime) / ns;
            lastTime = now;
            if (delta>=1){
                tick();
                render();
                frames++;
                delta--;
            }

            if(System.currentTimeMillis() - timer >= 1000){
                System.out.println("FPS "+ frames);
                frames = 0;
                timer+=1000;
            }

        }
        
        stop();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_D){
            player.right = true;
        }
        else if(e.getKeyCode() == KeyEvent.VK_A){
            player.left = true;
        }

        if(e.getKeyCode() == KeyEvent.VK_W){
            player.up = true;

            if(gameState == "MENU"){
                Menu.up = true;
            }
        }
        else if(e.getKeyCode() == KeyEvent.VK_S){
            player.down = true;

            if(gameState == "MENU"){
                Menu.down = true;
            }
        }

        if(e.getKeyCode() == KeyEvent.VK_X){
            player.shoot = true;
        }

        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            restartGame = true;

            if(gameState == "MENU"){
                Menu.enter = true;
            }
        }

        if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
            gameState = "MENU";
            menu.pause = true;       
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_D){
            player.right = false;
        }
        else if(e.getKeyCode() == KeyEvent.VK_A){
            player.left = false;
        }

        if(e.getKeyCode() == KeyEvent.VK_W){
            player.up = false;
        }
        else if(e.getKeyCode() == KeyEvent.VK_S){
            player.down = false;
        }

        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            restartGame = false;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        player.mouseShoot = true;
        player.mx = (e.getX()/3);
        player.my = (e.getY()/3);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
