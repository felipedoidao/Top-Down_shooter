package com.gcstudios.entities;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.gcstudios.main.Game;
import com.gcstudios.world.World;

public class Enemy extends Entity{
    private double speed = 1;

    public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
        super(x, y, width, height, sprite);
    }

    public void tick(){
        if((int)x < Game.player.getX() && World.isFree((int)(x+speed), this.getY()) && !isColiding((int)(x+speed), this.getY())){
            x+=speed;
        }
         else if((int)x > Game.player.getX() && World.isFree((int)(x-speed), this.getY()) && !isColiding((int)(x-speed), this.getY())){
            x-=speed;
         }
        if((int)y < Game.player.getY() && World.isFree(this.getX(), (int)(y+speed)) && !isColiding(this.getX(), (int)(y+speed))){
            y+=speed;
        }
         else if((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y-speed)) && !isColiding(this.getX(), (int)(y-speed))){
            y-=speed;
         }
    }

    public boolean isColiding(int xnext, int ynext){
        Rectangle enemyCurrent = new Rectangle(xnext, ynext, World.TILE_SIZE, World.TILE_SIZE);
        for(int i = 0; i < Game.enemies.size(); i++){
            Enemy e = Game.enemies.get(i);
            if(e == this)
                continue;

            Rectangle targetEnemy = new Rectangle(e.getX(), e.getY(), World.TILE_SIZE, World.TILE_SIZE);
            if(enemyCurrent.intersects(targetEnemy)){
                return true;
            }
        }


        return false;
    }
    
}
