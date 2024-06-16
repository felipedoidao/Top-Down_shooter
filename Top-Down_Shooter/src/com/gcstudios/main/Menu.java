package com.gcstudios.main;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Menu {

    public String[] options = {"Novo Jogo", "Sair"};

    public int currentOption = 0;
    public int maxOption = options.length-1;

    public static boolean up = false, down = false, enter = false;

    public boolean pause = false;

    public void tick(){

        if(up){
            up = false;
            currentOption --;
            if(currentOption < 0){
                currentOption = maxOption;
            }
        }
        else if (down){
            down = false;
            currentOption++;
            if(currentOption > maxOption){
                currentOption = 0;
            }
        }

        if(enter){
            enter = false;
            if(options[currentOption] == "Novo Jogo" || options[currentOption] == "Continuar"){
                Game.gameState = "NORMAL";
                pause = false;
            }else if(options[currentOption] == "Sair"){
                System.exit(1);
            }
        }

    }

    public void render(Graphics g){
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0,0,0,100));
        g.fillRect(0, 0, Game.WIDTH*Game.SCALE, Game.HEIGHT*Game.SCALE);
        g.setColor(Color.white);
        g.setFont(new Font("arial", Font.BOLD, 36));
        g.drawString("Jogo Brabo", (Game.WIDTH*Game.SCALE / 2) - 90, (Game.HEIGHT*Game.SCALE / 2) - 150);

        g.setFont(new Font("arial", Font.BOLD, 24));
        if(pause == false){
            g.drawString("Novo Jogo", (Game.WIDTH*Game.SCALE / 2) - 54, 180);
        }else{
            g.drawString("Continuar", (Game.WIDTH*Game.SCALE / 2) - 54, 180);
        }
        g.drawString("Sair", (Game.WIDTH*Game.SCALE / 2) - 12, 250);

        if(options[currentOption] == "Novo Jogo"){
            g.drawString(">", (Game.WIDTH*Game.SCALE / 2) - 90, 180);
        }
        else if(options[currentOption] == "Sair"){
            g.drawString(">", (Game.WIDTH*Game.SCALE / 2) - 50, 250);
        }
    }

}
