package com.example.mathgame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private GestureDetectorCompat mDetector;
    Bitmap basket;
    int basket_x, basket_y;
    int basketWidth, basketHeight;

    Bitmap leftKey, rightKey;
    int leftKey_x, leftKey_y;
    int rightKey_x, rightKey_y;
    int Width, Height;
    int score;
    int button_width;

    Bitmap balloonimg;
    int balloonWidth, balloonHeight;
    AnswerBalloon answerBalloon;
    ArrayList<Balloon> balloons;

    Bitmap screen;

    int number1, number2;
    int answer;
    int[] wrongNumber = new int[5];

    int count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MyView(this));
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Width = display.getWidth();
        Height = display.getHeight();
        button_width = Width / 6;

        balloons = new ArrayList<Balloon>();
        basket = BitmapFactory.decodeResource(getResources(), R.drawable.basket);
        int x = Width / 4;
        int y = Height / 14;
        basket = Bitmap.createScaledBitmap(basket, x, y, true);
        basketWidth = basket.getWidth();
        basketHeight = basket.getHeight();
        basket_x = Width / 9;
        basket_y = Height * 6 / 9;

        leftKey = BitmapFactory.decodeResource(getResources(), R.drawable.leftkey);
        leftKey = Bitmap.createScaledBitmap(leftKey, button_width, button_width, true);
        leftKey_x = Width * 5 / 9;
        leftKey_y = Height * 7 / 9;

        rightKey = BitmapFactory.decodeResource(getResources(), R.drawable.rightkey);
        rightKey = Bitmap.createScaledBitmap(rightKey, button_width, button_width, true);
        rightKey_x = Width * 7 / 9;
        rightKey_y = Height * 7 / 9;

        balloonimg = BitmapFactory.decodeResource(getResources(), R.drawable.balloon);
        balloonimg = Bitmap.createScaledBitmap(balloonimg, button_width, button_width + button_width, true);
        balloonWidth = balloonimg.getWidth();
        balloonHeight = balloonimg.getHeight();

        screen = BitmapFactory.decodeResource(getResources(), R.drawable.screenmath);
        screen = Bitmap.createScaledBitmap(screen, Width, Height, true);
        Random r1 = new Random();
        int xx = r1.nextInt(Width-button_width);
        answerBalloon = new AnswerBalloon(xx, 0, 5);
    }

    class MyView extends View {
        MyView(Context context) {
            super(context);
            setBackgroundColor(Color.BLUE);
            gHandler.sendEmptyMessage(0);
            makeQuestion();
        }

        @Override
        synchronized public void onDraw(Canvas canvas) {
            if(balloons.size() < 5 ) {
                Random r1 = new Random();
                int x = r1.nextInt(Width - button_width);
                int y = r1.nextInt(Height / 4);
                balloons.add(new Balloon(x, -y, 5));
            }
            Paint p1 = new Paint();
            p1.setColor(Color.WHITE);
            p1.setTextSize(Width / 14);
            canvas.drawBitmap(screen, 0, 0, p1);
            
            canvas.drawText("점수 : " + Integer.toString(score), 0, Height / 12, p1);
            canvas.drawText("문제 : " + Integer.toString(number1) + "+" + Integer.toString(number2), 0, Height*2/12, p1);
            canvas.drawBitmap(basket, basket_x, basket_y, p1);
            canvas.drawBitmap(leftKey, leftKey_x, leftKey_y, p1);
            canvas.drawBitmap(rightKey,rightKey_x,rightKey_y, p1);
            
            for (Balloon tmp : balloons)
                canvas.drawBitmap(balloonimg, tmp.x, tmp.y, p1);
            
            for (int i = balloons.size() - 1; i >= 0; i--)
                canvas.drawText(Integer.toString(wrongNumber[i]), balloons.get(i).x + balloonWidth / 6, balloons.get(i).y + balloonWidth * 2 /3 , p1);
            
            canvas.drawBitmap(balloonimg, answerBalloon.x, answerBalloon.y, p1);
            canvas.drawText(Integer.toString(answer), answerBalloon.x + balloonWidth /6, answerBalloon.y + balloonWidth * 2 /3 , p1);
            
            if( answerBalloon.y > Height)
                answerBalloon.y = -50;
            
            moveBalloon();
            checkCollision();
            count++;
        }

        private void makeQuestion() {
            Random r1 = new Random();

            int x = r1.nextInt(100) + 1;
            number1 = x;
            x = r1.nextInt(100) + 1;
            number2 = x;
            answer = number1 + number2;

            int counter = 0;
            for (int i = 0; i < 5; i++) {
                x = r1.nextInt(200) + 1;
                while ( x == answer) {
                    x = r1.nextInt(200) + 1;
                }
                wrongNumber[i] = x;
            }
        }

        private void moveBalloon() {
            for (int i = balloons.size() - 1; i >= 0; i--) {
                balloons.get(i).move();
            }

            for (int i = balloons.size() - 1; i >= 0; i-- ) {
                if (balloons.get(i).y > Height)
                    balloons.get(i).y = -100;
            }

            answerBalloon.move();
        }
        private void checkCollision() {
            for (int i = balloons.size() - 1; i >= 0; i--) {
                if (balloons.get(i).x + balloonWidth / 2 > basket_x
                        && balloons.get(i).x + balloonWidth / 2 < basket_x + basketWidth
                        && balloons.get(i).y + balloonHeight > basket_y
                        && balloons.get(i).y + balloonHeight > basket_x + basketWidth) {
                    balloons.remove(i);
                    score -= 10;
                }
            }

            if ( answerBalloon.x + balloonWidth / 2 > basket_x
            && answerBalloon.x + balloonWidth / 2 < basket_x + basketWidth
            && answerBalloon.y + balloonHeight > basket_y
            && answerBalloon.y + balloonHeight > basket_x + basketWidth) {
                score += 30;
                makeQuestion();
                Random r1 = new Random();
                int xx = r1.nextInt(Width - button_width);
                answerBalloon.x = xx;
                xx = r1.nextInt(300);
                answerBalloon.y = -xx;
            }
        }

        private final Handler gHandler = new Handler() {
            public void handleMessage(Message msg) {
                invalidate();
                gHandler.sendEmptyMessage(0);
            }
        };

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            int x = 0, y = 0;
            if (event.getAction() == MotionEvent.ACTION_DOWN ||
                    event.getAction() == MotionEvent.ACTION_MOVE) {
                    x = (int) event.getX();
                    y = (int) event.getY();
            }
            if ((x>leftKey_x) && (x < leftKey_x + button_width)&& ( y>leftKey_y) && (x<leftKey_y + button_width))
                basket_x -= 20;
            if ((x>rightKey_x) && (x < rightKey_x + button_width)&& ( y>rightKey_y) && (x<rightKey_y + button_width))
                basket_x += 20;
            return true;
        }
    }
}
