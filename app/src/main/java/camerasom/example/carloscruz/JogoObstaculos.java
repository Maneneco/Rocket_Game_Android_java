package camerasom.example.carloscruz;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.Observable;
import java.util.Random;

public class JogoObstaculos implements JogoObjeto {
    //Criar rectangulos com a função Rect
    private Rect rectangle;
    private Rect rectangle2;
    private int color;
    Random rnd = new Random();

    public Rect getRectangle() {
        //Log.e("teste", "Obstaculo");
        return rectangle;
    }

    public void incrementY(float y) {
        //Descer as plataformas
        rectangle.top += y;
        rectangle.bottom +=y;
        rectangle2.top += y;
        rectangle2.bottom +=y;
    }

    public JogoObstaculos(int rectHeight, int startX, int startY, int playerGap) {

         color = Color.argb(255, rnd.nextInt( 200)+ 20, rnd.nextInt(200)+ 20, rnd.nextInt(200)+ 20);
        //Primeiro Retangulo
        rectangle = new Rect(0, startY, startX, startY + rectHeight);
        //Segundo retangulo
        rectangle2 = new Rect(startX + playerGap, startY, Constantes.Screen_Width, startY + rectHeight);
    }

    //Colisão do player
    public boolean playerCollide(Jogador player) {
        return Rect.intersects(rectangle, player.getRectangle()) || Rect.intersects(rectangle2, player.getRectangle());
    }

    @Override
    public void draw(Canvas canvas) {
        //Criar Nova tinta
        Paint paint = new Paint();

        paint.setColor(color);
        //Desenhar os rectangulos
        canvas.drawRect(rectangle, paint);
        canvas.drawRect(rectangle2, paint);
    }

    public void update() {

    }
}
