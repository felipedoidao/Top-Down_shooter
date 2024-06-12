package com.gcstudios.graficos;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.gcstudios.entities.Player;
import com.gcstudios.main.Game;

public class Ui {
    
    public void render(Graphics g){
        g.setColor(Color.red);
        g.fillRect(10, 4, 50, 8);
        g.setColor(Color.green);
        g.fillRect(10, 4, (int)((Game.player.life/Player.maxLife)*50), 8);
        g.setColor(Color.white);
        g.setFont(new Font("arial", Font.BOLD, 8));
        g.drawString((int)Game.player.life+"/"+(int)Player.maxLife, 22, 11);
    }

}
