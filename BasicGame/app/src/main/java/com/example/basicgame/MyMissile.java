package com.example.basicgame;

public class MyMissile {
    int x, y;
    int missileSpeed = 15;

    MyMissile(int x, int y){
        this.x = x; this.y = y;
    }

    public void move() {
        y-=missileSpeed;
    }
}
