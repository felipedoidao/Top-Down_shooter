package com.gcstudios.entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.gcstudios.main.Game;
import com.gcstudios.main.Sound.Clips;
import com.gcstudios.world.Camera;
import com.gcstudios.world.World;

public class Player extends Entity{

    public boolean right,up,left,down;
    public int right_dir = 0, left_dir = 1;
    public int dir = right_dir;
    public int speed = 1;

    public double life = 100;
    public static double maxLife = 100;
    public int mx, my;
    
    private int frames = 0, maxFrames = 5,index = 0, maxIndex = 3;
    private boolean moved = false;
    private BufferedImage[] rightPlayer;
    private BufferedImage[] leftPlayer;

    private BufferedImage playerDamage;

    public boolean isDamaged = false;
    private int damageFrames = 0;

    public boolean shoot = false;
    public boolean mouseShoot = false;

    private boolean arma = false;

    public int ammo = 0;

    public Player(int x, int y, int width, int height, BufferedImage sprite) {
        super(x, y, width, height, sprite);

        rightPlayer = new BufferedImage[4];
        leftPlayer = new BufferedImage[4];
        playerDamage = Game.spritesheet.getSprite(0, 16, 16, 16);

        for(int i=0; i<4; i++){
            rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 0, 16, 16);
        }
        for(int i=0; i<4; i++){
            leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 16, 16, 16);
        }
    }

    public void tick(){
        moved = false;
        if(right){
            dir = right_dir;
            if(World.isFree((this.getX()+(int)this.speed), this.getY())){
                moved = true;
                x+=speed;
            }
        }
        else if(left){
            dir = left_dir;
            if(World.isFree((this.getX()-(int)this.speed), this.getY())){
                moved = true;
                x-=speed;
            } 
        }
        if(up && World.isFree(this.getX(), (this.getY()-(int)this.speed))){
            moved = true;
            y-=speed;
        }
        else if(down && World.isFree(this.getX(), (this.getY()+(int)this.speed))){
            moved = true;
            y+=speed;
        }

        if(moved){
            frames++;
            if(frames >= maxFrames){
                frames = 0;
                index++;
                if(index > maxIndex){
                    index = 0;
                }
            }
        }

        if(this.isDamaged){
            this.damageFrames++;
            if(this.damageFrames >= 5){
                this.damageFrames = 0;
                isDamaged = false;
            }
        }

        if(mouseShoot){
            mouseShoot = false;
            if(ammo > 0 && arma){
                ammo --;
                
                int px = 0;
                int py = 0;
                if(dir == right_dir){
                    px = 18;
                }else{
                    px = -8;
                }

                double angle = Math.atan2(my - (this.getY() - Camera.y), mx - (this.getX()+px - Camera.x));

                double dx = Math.cos(angle);
                double dy = Math.sin(angle);
            
                Shoot shoot = new Shoot(this.getX() + px, this.getY() + py, 6, 6, null, dx, dy);
                Game.bullets.add(shoot);
            }
        }

        if(life <=0){
            life = 0;
            Game.gameState = "GAME_OVER";
        }

        checkCollisionLifePack();
        checkCollisionAmmo();
        checkCollisionGun();

        
    }

    public void updateCamera(){
        Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2), 0, World.WIDTH*16 - Game.WIDTH);
        Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2), 0, World.HEIGHT*16 - Game.HEIGHT);
    }

    public void checkCollisionGun(){
        for(int i = 0; i < Game.entities.size(); i++){
            Entity atual = Game.entities.get(i);
            if(atual instanceof Weapon){
                if(Entity.isColiding(this, atual)){
                    arma = true;
                    Game.entities.remove(atual);
                }
            }
        }
    }

    public void checkCollisionAmmo(){
        for(int i = 0; i < Game.entities.size(); i++){
            Entity atual = Game.entities.get(i);
            if(atual instanceof Bullet){
                if(Entity.isColiding(this, atual)){
                    ammo += 20;
                    Game.entities.remove(atual);
                }
            }
        }
    }

    public void checkCollisionLifePack(){
        for(int i = 0; i < Game.entities.size(); i++){
            Entity atual = Game.entities.get(i);
            if(atual instanceof Lifepack){
                if(Entity.isColiding(this, atual) && life < 100){
                    life += 10;
                    Game.entities.remove(atual);
                    if(life > 100)
                        life = 100;
                }
            }
        }
    }

    public void render(Graphics g){
        if(!isDamaged){
            if(dir == right_dir){
                g.drawImage(rightPlayer[index], this.getX() - Camera.x , this.getY() - Camera.y , null);
                if(arma){
                    g.drawImage(GUN_RIGHT, this.getX() + 8 - Camera.x, this.getY() - Camera.y, null);
                }
            }else if(dir == left_dir){
                g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
                if(arma){
                    g.drawImage(GUN_LEFT, this.getX() - 8 - Camera.x, this.getY() - Camera.y, null);
                }
            }
        }else{
           Clips.hurt.play();
            g.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y, null);
            if(arma){
                if(dir == right_dir){
                    g.drawImage(GUN_RIGHT, this.getX() + 8 - Camera.x, this.getY() - Camera.y, null);
                }else{
                    g.drawImage(GUN_LEFT, this.getX() - 8 - Camera.x, this.getY() - Camera.y, null);
                }
            }
        }
    }
    
}
