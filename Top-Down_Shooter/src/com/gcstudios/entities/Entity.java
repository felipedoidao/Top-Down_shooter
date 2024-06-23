package com.gcstudios.entities;

import java.awt.Graphics;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import com.gcstudios.world.Node;
import com.gcstudios.world.Vector2i;
import com.gcstudios.world.World;
import com.gcstudios.main.Game;
import com.gcstudios.world.Camera;

public class Entity {

    public static BufferedImage LIFEPACK_EN = Game.spritesheet.getSprite(6*16, 0, 16, 16);
    public static BufferedImage WEAPON_EN = Game.spritesheet.getSprite(7*16, 0, 16, 16);
    public static BufferedImage BULLET_EN = Game.spritesheet.getSprite(6*16, 16, 16, 16);
    public static BufferedImage ENEMY_EN = Game.spritesheet.getSprite(7*16, 16, 16, 16);
    public static BufferedImage ENEMY_DAMAGED = Game.spritesheet.getSprite(11*16, 16, 16, 16);
    public static BufferedImage GUN_RIGHT = Game.spritesheet.getSprite(7*16, 0, 16, 16);
    public static BufferedImage GUN_LEFT = Game.spritesheet.getSprite(8*16, 0, 16, 16);

    protected double x;
    protected double y;
    protected int width;
    protected int height;

    protected List<Node> path;

    private int maskx, masky, maskw, maskh;

    private BufferedImage sprite;

    public Entity(int x, int y, int width, int height, BufferedImage sprite){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.sprite = sprite;

        this.maskx = 0;
        this.masky = 0;
        this.maskw = width;
        this.maskh = height;
    }

    public void setMask(int maskx, int masky, int maskw, int maskh){
        this.maskx = maskx;
        this.masky = masky;
        this.maskw = maskw;
        this.maskh = maskh;
    }

    public void setX(int newX){
        this.x = newX;
    }

    public void setY(int newY){
        this.y = newY;
    }

    public int getX(){
        return (int)this.x;
    }

    public int getY(){
        return (int)this.y;
    }

    public int getWidth(){
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }

    public void tick(){
        
    }

    public void followPath(List<Node> path){

        if(path != null){
            if(path.size() > 0){
                Vector2i target = path.get(path.size() - 1).tile;

                if(x < target.x *16 && !isColiding(this.getX() + 1, this.getY())){
                    x += 1;

                }else if(x > target.x *16 && !isColiding(this.getX() - 1, this.getY())) {
                    x -= 1;

                }

                if(y < target.y *16 && !isColiding(this.getX(), this.getY() + 1)){
                    y += 1;

                }else if(y > target.y * 16 && !isColiding(this.getX(), this.getY() - 1)){ 
                    y -= 1;

                }

                if(x == target.x *16 && y == target.y * 16){
                    path.remove(path.size() - 1);
                }
            }
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
                if(Game.rand.nextInt(100) < 50)
                return true;
            }
        }


        return false;
    }

    public static boolean isColiding(Entity e1, Entity e2){
        Rectangle e1Mask = new Rectangle(e1.getX() + e1.maskx, e1.getY() + e1.masky, e1.maskw, e1.maskh);
        Rectangle e2Mask = new Rectangle(e2.getX() + e2.maskx, e2.getY() + e2.masky, e2.maskw, e2.maskh);

        return e1Mask.intersects(e2Mask);
    }

    public void render(Graphics g){
        g.drawImage(sprite, this.getX() - Camera.x, this.getY()- Camera.y, null);
    }

}
