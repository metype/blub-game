package com.metype.game;

public class Action {
    Tile[][] old;
    Tile[][] change;
    Vector position;

    public Action(Vector position){
        this.position=position;
    }

    public void setOld(Tile[][] oldTiles){
        this.old = oldTiles;
    }

    public void setNew(Tile[][] changedTiles){
        this.change = changedTiles;
    }

    public Level undo(Level victim) {
        Level recreated = victim.copy();
        for(int i=(int)position.x;i<position.x+old.length;i++){
            for(int j=(int)position.y;j<position.y+old[i-(int)position.x].length;j++){
                recreated.t[i][j] = old[i-(int)position.x][j-(int)position.y].copy();
            }
        }
        return recreated;
    }

    public Level redo(Level victim) {
        Level recreated = victim.copy();
        for(int i=(int)position.x;i<position.x+change.length;i++){
            for(int j=(int)position.y;j<position.y+change[i-(int)position.x].length;j++){
                recreated.t[i][j] = change[i-(int)position.x][j-(int)position.y].copy();
            }
        }
        return recreated;
    }
}
