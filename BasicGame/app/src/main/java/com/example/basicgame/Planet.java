package com.example.basicgame;

public class Planet {

    int x,y;
    Planet(int x, int y){

        this.x = x; this.y = y;

    }

    public void move(int speed){
        y+=speed;
    }

}