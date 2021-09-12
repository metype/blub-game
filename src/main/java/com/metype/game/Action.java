package com.metype.game;

public class Action {
    Tile[][] old;
    Tile[][] change;
    Vector position;

    public Action(Vector position) {
        this.position = position;
    }

    public void setOld(Tile[][] oldTiles) {
        this.old = oldTiles;
    }

    public void setNew(Tile[][] changedTiles) {
        this.change = changedTiles;
    }

    public Level undo(Level victim) {
        for (int i = (int) position.x; i < position.x + old.length; i++) {
            for (int j = (int) position.y; j < position.y + old[i - (int) position.x].length; j++) {
                victim.t[i][j] = old[i - (int) position.x][j - (int) position.y].copy();
            }
        }
        return victim;
    }

    public Level redo(Level victim) {
        for (int i = (int) position.x; i < position.x + change.length; i++) {
            for (int j = (int) position.y; j < position.y + change[i - (int) position.x].length; j++) {
                victim.t[i][j] = change[i - (int) position.x][j - (int) position.y].copy();
            }
        }
        return victim;
    }
}
