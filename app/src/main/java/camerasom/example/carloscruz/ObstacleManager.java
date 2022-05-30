package camerasom.example.carloscruz;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
public class ObstacleManager {

    private ArrayList<JogoObstaculos> obstacles;
    private int playerGap;
    private int obstacleGap;
    private int obstacleHeight;
    private int color;

    private long startTime;

    private long initTime;

    Context context;


    public ObstacleManager(int playerGap, int obstacleGap, int obstacleHeight, int color) {

        this.playerGap = playerGap;
        this.obstacleGap = obstacleGap;
        this.obstacleHeight = obstacleHeight;
        this.color = color;

        startTime = System.currentTimeMillis();
        initTime = System.currentTimeMillis();

        obstacles = new ArrayList<>();

        populateObstacles();
    }

    public boolean playerCollide(Jogador player){
        for(JogoObstaculos ob : obstacles){
            if(ob.playerCollide(player))
                return true;
        }
        return false;
    }

    private void populateObstacles() {
        //Log.e("teste", "Populated");
        int currY = -5*Constantes.Screen_Height/4;
        while(currY < 0) {
            // Log.e("teste", "Populated");
            int xStart = (int)(Math.random()*(Constantes.Screen_Width - playerGap));
            obstacles.add(new JogoObstaculos(obstacleHeight, xStart, currY, playerGap));
            currY += obstacleHeight + obstacleGap;
        }
    }

    public void update() {


        if(startTime < Constantes.INIT_TIME)
            startTime = Constantes.INIT_TIME;
        //Log.e("teste", "ObsUpdate");
        int elapsedTime = (int)(System.currentTimeMillis() - startTime);
        startTime = System.currentTimeMillis();
        //Velocidade da criação de novas barreira
        //A cada 5 segundos a velocidade aumenta
        float speed = (float)(Math.sqrt((startTime - initTime)/2000))*Constantes.Screen_Height/(10000.0f);
        for(JogoObstaculos ob : obstacles) {
            ob.incrementY(speed * elapsedTime);
        }

        if(obstacles.get(obstacles.size() - 1).getRectangle().top >= Constantes.Screen_Height) {
            Constantes.pontos ++;
            int xStart = (int)(Math.random()*(Constantes.Screen_Width - playerGap));
            obstacles.add(0, new JogoObstaculos(obstacleHeight, xStart, obstacles.get(0).getRectangle().top - obstacleHeight - obstacleGap, playerGap));
            obstacles.remove(obstacles.size() - 1);
        }
    }

    public void draw(Canvas canvas) {


        for(JogoObstaculos ob : obstacles)
            ob.draw(canvas);

        if(Constantes.pontos > Constantes.recorde){

            Constantes.recorde = Constantes.pontos;



        }

        Paint paint = new Paint();
        paint.setTextSize(120);
        paint.setColor(Color.WHITE);
        canvas.drawText(""+ Constantes.pontos, 100, 100 + paint.descent() - paint.ascent(), paint);

    }
}

