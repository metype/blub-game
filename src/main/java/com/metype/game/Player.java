package com.metype.game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

import static com.metype.game.EditorScreen.map;

class Player {
    Vector pos;
    Vector vel;
    Rect upHit, leftHit, downHit, rightHit;
    int jumpTimer = 0;
    int leftJumpTimer = 0;
    int rightJumpTimer = 0;
    boolean canToggle = false;
    float respawnTimer = 0;
    float levelCooldown = 0;
    boolean checkpoint = false;
    boolean died = false;
    boolean onoffstate = false;

    public Player(float x, float y) {
        pos = new Vector(x, y);
        vel = new Vector(0, 0);
    }

    public void render(GraphicsContext gc, double playerSize, PlayScreen playScreen) {
        levelCooldown--;
        gc.setFill(Color.grayRgb(51));
        gc.setStroke(Color.BLACK);
        if (respawnTimer > 0) {
            playerSize = map(respawnTimer, 100, 0, playerSize, 0);
        }
        gc.fillRect(gc.getCanvas().getWidth() / 2 - (playerSize / 2), gc.getCanvas().getHeight() / 2 - (playerSize / 2), playerSize, playerSize);
        if (respawnTimer == 1) {
            playScreen.reload();
            System.out.println("Respawning...");
            this.vel = new Vector(0, 0);
            respawnTimer--;
        } else {
            respawnTimer -= 3;
        }
    }

    public void respawn(boolean godMode) {
        if (godMode) {
            respawnTimer = 0;
            return;
        }
        respawnTimer = 100;
    }

    public Vector updatePhysics(List<Tile> collide, int timeStep, Level currentLevel, double tileSize, GraphicsContext gc, boolean godMode, byte[] arrows, boolean playTest, double frameTime) {
        double multiplier = (frameTime / 0.01649779880834474) * .9;
        this.pos.mult(tileSize);
        if (this.pos.y > (currentLevel.t[0].length * tileSize) && respawnTimer <= 0) respawn(godMode);
        double scrollX;
        double scrollY;
        jumpTimer--;
        jumpTimer = Math.max(jumpTimer, 0);
        upHit = new Rect(gc.getCanvas().getWidth() / 2 - (tileSize / 4) + (tileSize / 9), gc.getCanvas().getHeight() / 2 - (tileSize / 4), tileSize / 2 - (tileSize / 9) * 2, tileSize / 9);
        downHit = new Rect(gc.getCanvas().getWidth() / 2 - (tileSize / 4) + (tileSize / 9), gc.getCanvas().getHeight() / 2 - (tileSize / 4) + (tileSize / 2) - (tileSize / 9), tileSize / 2 - (tileSize / 9) * 2, tileSize / 9);
        leftHit = new Rect(gc.getCanvas().getWidth() / 2 - (tileSize / 4), gc.getCanvas().getHeight() / 2 - (tileSize / 4) + (tileSize / 9), tileSize / 9, tileSize / 2 - (tileSize / 9) * 2);
        rightHit = new Rect(gc.getCanvas().getWidth() / 2 - (tileSize / 4) + (tileSize / 2) - (tileSize / 9), gc.getCanvas().getHeight() / 2 - (tileSize / 4) + (tileSize / 9), tileSize / 9, tileSize / 2 - (tileSize / 9) * 2);
        Tile rightTouch = new Tile(-1), leftTouch = new Tile(-1), downTouch = new Tile(-1), upTouch = new Tile(-1);
        for (Tile t : collide) {
            if (upHit.isTouching(t.hitBox) && t.isSolid(onoffstate)) upTouch = t;
            if (downHit.isTouching(t.hitBox) && t.isSolid(onoffstate)) downTouch = t;
            if (leftHit.isTouching(t.hitBox) && t.isSolid(onoffstate)) leftTouch = t;
            if (rightHit.isTouching(t.hitBox) && t.isSolid(onoffstate)) rightTouch = t;
        }
        for (Tile[] tiles : currentLevel.t) {
            for (Tile t : tiles) {
                if (t.id == 20) {
                    t.state = false;
                }
            }
        }
        try {
            if (currentLevel.t[(int) Math.floor(this.pos.x / tileSize)][(int) Math.floor(this.pos.y / tileSize)].id == 20) {
                currentLevel.t[(int) Math.floor(this.pos.x / tileSize)][(int) Math.floor(this.pos.y / tileSize)].state = true;
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        if (upTouch.id == 7 || downTouch.id == 7 || leftTouch.id == 7 || rightTouch.id == 7) {
            timeStep *= 1.5;
        }

        if ((upTouch.id == 2 || downTouch.id == 2 || leftTouch.id == 2 || rightTouch.id == 2) && levelCooldown <= 0 && !playTest) {
            levelCooldown = 50;
            currentLevel.completed = true;
            respawn(godMode);
        }

        if (jumpTimer == 0) {
            if (downTouch.id == 6 || downTouch.id == 3) {
                if (arrows[0] == 1) {
                    this.vel.set(new Vector(this.vel.x, ((-tileSize / (5f * 1.5)) * 100) / timeStep));
                    jumpTimer = 1000;
                }
            }
            if (downTouch.isSolid(onoffstate) && (downTouch.id != 27 || (this.vel.y >= 0 && (downHit.y + downHit.h < downTouch.hitBox.y + 10)))) {
                if (arrows[0] == 0) {
                    if (downTouch.id == 4 && this.vel.y > 0) {
                        this.vel.set(new Vector(this.vel.x, (this.vel.y * -0.66f * 60) / timeStep));
                    } else {
                        this.vel.set(new Vector(this.vel.x, 0));
                    }
                } else {
                    if (upTouch.id == 6 || downTouch.id == 6 || leftTouch.id == 6 || rightTouch.id == 6) {
                        this.vel.set(new Vector(this.vel.x, (-tileSize / 1.5) / timeStep));
                    } else {
                        if (downTouch.id == 4) {
                            this.vel.set(new Vector(this.vel.x, (-tileSize * 30) / timeStep));
                        } else {
                            this.vel.set(new Vector(this.vel.x, (-tileSize * 15) / timeStep));
                        }
                    }
                    jumpTimer = 2000;
                }
            } else {
                this.vel.add(new Vector(0, (tileSize / 80f / timeStep)));
            }
        } else {
            jumpTimer--;
        }

        upHit = new Rect(gc.getCanvas().getWidth() / 2 - (tileSize / 4) + (tileSize / 9), gc.getCanvas().getHeight() / 2 - (tileSize / 4), tileSize / 2 - (tileSize / 9) * 2, tileSize / 9);
        downHit = new Rect(gc.getCanvas().getWidth() / 2 - (tileSize / 4) + (tileSize / 9), gc.getCanvas().getHeight() / 2 - (tileSize / 4) + (tileSize / 2) - (tileSize / 9), tileSize / 2 - (tileSize / 9) * 2, tileSize / 9);
        leftHit = new Rect(gc.getCanvas().getWidth() / 2 - (tileSize / 4), gc.getCanvas().getHeight() / 2 - (tileSize / 4) + (tileSize / 9), tileSize / 9, tileSize / 2 - (tileSize / 9) * 2);
        rightHit = new Rect(gc.getCanvas().getWidth() / 2 - (tileSize / 4) + (tileSize / 2) - (tileSize / 9), gc.getCanvas().getHeight() / 2 - (tileSize / 4) + (tileSize / 9), tileSize / 9, tileSize / 2 - (tileSize / 9) * 2);
        for (Tile t : collide) {
            if (upHit.isTouching(t.hitBox) && t.isSolid(onoffstate)) upTouch = t;
            if (downHit.isTouching(t.hitBox) && t.isSolid(onoffstate)) downTouch = t;
            if (leftHit.isTouching(t.hitBox) && t.isSolid(onoffstate)) leftTouch = t;
            if (rightHit.isTouching(t.hitBox) && t.isSolid(onoffstate)) rightTouch = t;
        }

        if (arrows[3] == 1) {
            this.vel.add(new Vector(-tileSize / 512f, 0));
        }

        if (arrows[2] == 1) {
            this.vel.add(new Vector(tileSize / 512f, 0));
        }

        if (rightTouch.isCollidable(onoffstate) && rightTouch.id != 27) {
            if (arrows[3] == 0) {
                if (jumpTimer == 0 || this.vel.x >= -1e-5)
                    this.vel.set(new Vector(0, this.vel.y));
            } else {
                this.vel.add(new Vector(-tileSize / 512f, 0));
            }
            if (arrows[0] == 1 && jumpTimer == 0) {
                this.vel.set(new Vector(-tileSize / 512f, (-tileSize * 12) / timeStep));
                rightJumpTimer = 1000;
                jumpTimer = 2000;
            }
            double dist = (rightHit.x + rightHit.w) - rightTouch.hitBox.x;
            this.pos.x -= dist;
        }

        if (leftTouch.isCollidable(onoffstate) && leftTouch.id != 27) {
            if (arrows[2] == 0) {
                if (jumpTimer == 0 || this.vel.x <= 1e-5)
                    this.vel.set(new Vector(0, this.vel.y));
            } else {
                this.vel.add(new Vector(tileSize / 512f, 0));
            }
            if (arrows[0] == 1 && jumpTimer == 0) {
                this.vel.set(new Vector(tileSize / 512f, (-tileSize * 12) / timeStep));
                leftJumpTimer = 1000;
                jumpTimer = 2000;
            }
            double dist = (leftTouch.hitBox.x + leftTouch.hitBox.w) - (leftHit.x);
            this.pos.x += dist;
        }

        if (leftJumpTimer > 0) {
            this.vel.set(new Vector(tileSize / 16f, this.vel.y));
            leftJumpTimer--;
        }

        if (rightJumpTimer > 0) {
            this.vel.set(new Vector(-tileSize / 16f, this.vel.y));
            rightJumpTimer--;
        }
        upHit = new Rect(gc.getCanvas().getWidth() / 2 - (tileSize / 4) + (tileSize / 9), gc.getCanvas().getHeight() / 2 - (tileSize / 4), tileSize / 2 - (tileSize / 9) * 2, tileSize / 9);
        downHit = new Rect(gc.getCanvas().getWidth() / 2 - (tileSize / 4) + (tileSize / 9), gc.getCanvas().getHeight() / 2 - (tileSize / 4) + (tileSize / 2) - (tileSize / 9), tileSize / 2 - (tileSize / 9) * 2, tileSize / 9);
        leftHit = new Rect(gc.getCanvas().getWidth() / 2 - (tileSize / 4), gc.getCanvas().getHeight() / 2 - (tileSize / 4) + (tileSize / 9), tileSize / 9, tileSize / 2 - (tileSize / 9) * 2);
        rightHit = new Rect(gc.getCanvas().getWidth() / 2 - (tileSize / 4) + (tileSize / 2) - (tileSize / 9), gc.getCanvas().getHeight() / 2 - (tileSize / 4) + (tileSize / 9), tileSize / 9, tileSize / 2 - (tileSize / 9) * 2);
        for (Tile t : collide) {
            if (upHit.isTouching(t.hitBox) && t.isSolid(onoffstate)) upTouch = t;
            if (downHit.isTouching(t.hitBox) && t.isSolid(onoffstate)) downTouch = t;
            if (leftHit.isTouching(t.hitBox) && t.isSolid(onoffstate)) leftTouch = t;
            if (rightHit.isTouching(t.hitBox) && t.isSolid(onoffstate)) rightTouch = t;
        }

        if (upTouch.isCollidable(onoffstate) && upTouch.id != 27) {
            if (upTouch.id == 9 && canToggle) {
                onoffstate = !onoffstate;
            }
            canToggle = false;
            double dist = (upTouch.hitBox.y + upTouch.hitBox.h) - upHit.y;
            this.vel.set(new Vector(this.vel.x, dist));
            this.vel.add(new Vector(0, 0.1));
        } else {
            canToggle = true;
        }
        if (this.vel.y >= tileSize * 1.2) this.vel.y = tileSize * 1.2;
        this.pos.y += (this.vel.y / timeStep) * multiplier;
        this.pos.x += (this.vel.x / timeStep) * multiplier;
        scrollX = -pos.x + (gc.getCanvas().getWidth() / 2);
        scrollY = -pos.y + (gc.getCanvas().getHeight() / 2);
        if (respawnTimer <= -1)
            for (Tile t : collide) {
                if (upHit.isTouching(t.hitBox) && t.isCollidable(onoffstate) && upTouch.id == -1) upTouch = t;
                if (downHit.isTouching(t.hitBox) && t.isCollidable(onoffstate) && downTouch.id == -1) downTouch = t;
                if (leftHit.isTouching(t.hitBox) && t.isCollidable(onoffstate) && leftTouch.id == -1) leftTouch = t;
                if (rightHit.isTouching(t.hitBox) && t.isCollidable(onoffstate) && rightTouch.id == -1) rightTouch = t;
            }

        if (upTouch.id == 3 || downTouch.id == 3 || leftTouch.id == 3 || rightTouch.id == 3) {
            respawn(godMode);
        }

        if (upTouch.id == 6 || downTouch.id == 6 || leftTouch.id == 6 || rightTouch.id == 6) {
            this.vel.x *= 0.95;
            this.vel.y *= 0.99;
        } else {
            if ((downTouch.id == 5) && arrows[2] == 0 && arrows[3] == 0) this.vel.x *= 0.9997;
            else if ((downTouch.id == -1) && arrows[2] == 0 && arrows[3] == 0) this.vel.x *= 0.999;
            else if (arrows[2] == 0 && arrows[3] == 0) this.vel.x *= 0.997;
            else this.vel.x *= 0.99;
        }

        if (downTouch.id == 6 || upTouch.id == 6 || leftTouch.id == 6 || rightTouch.id == 6) {
            if (arrows[0] == 1) {
                this.vel.set(new Vector(this.vel.x, ((-tileSize / (5f * 1.5)) * 100) / timeStep));
                jumpTimer = 1000;
            }
        }
        this.pos.div(tileSize);
        return new Vector(scrollX, scrollY);
    }
}