package com.gcstudios.entities;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.gcstudios.main.Game;
import com.gcstudios.world.Camera;
import com.gcstudios.world.World;

public class Enemy extends Entity{
    private double speed = 0.5;

    private int life = 10;

    private boolean isDamaged = false;
    private int damageFrames = 0;

    private int frames = 0, maxFrames = 15,index = 0, maxIndex = 3;
    private BufferedImage[] sprites;

    public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
        super(x, y, width, height, null);
        sprites = new BufferedImage[4];
        for(int i=0; i<4; i++){
            sprites[i] = Game.spritesheet.getSprite(112 + (i*16), 16, 16, 16);
        }
    }

    public void tick(){
        if(isColidingWithPlayer() == false){
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
        }else {
            if(Game.rand.nextInt(100) < 10){
                Game.player.life -= Game.rand.nextInt(3);
                Game.player.isDamaged = true;
            }
        }
        frames++;
            if(frames >= maxFrames){
                frames = 0;
                index++;
                if(index > maxIndex){
                    index = 0;
                }
            }

        if(this.isDamaged){
            this.damageFrames++;
            if(this.damageFrames >= 5){
                this.damageFrames = 0;
                isDamaged = false;
            }
        }

            collidingBullet();
            if(life <= 0){
                Game.enemies.remove(this);
                Game.entities.remove(this);
            }
    }

    public void collidingBullet(){
        for(int i = 0; i < Game.bullets.size(); i++){
            Entity e = Game.bullets.get(i);
            if(Entity.isColiding(this, e)){
                isDamaged = true;
                life--;
                Game.bullets.remove(i);
                return;
            }
        }
    }

    public boolean isColidingWithPlayer(){
        Rectangle currentEnemy = new Rectangle(this.getX(), this.getY(), World.TILE_SIZE, World.TILE_SIZE);
        Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);

        return currentEnemy.intersects(player);
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

    public void render(Graphics g){
        if(!isDamaged){
            g.drawImage(sprites[index], this.getX()-Camera.x, this.getY()-Camera.y, null);
        }else{
            g.drawImage(ENEMY_DAMAGED, this.getX() - Camera.x, this.getY() - Camera.y, null);
        }
    }
    
}
