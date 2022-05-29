package camerasom.example.carloscruz;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

//Definições do jogo, do genero Phazer.
public class JogoThread extends Thread {
    //Definir fps Maximo, para não haver erros.
    public static final int MAX_FPS = 30;
    private double averageFPS;
    private SurfaceHolder surfaceHolder;
    private Jogo Jogo;
    private boolean running;
    public static Canvas canvas;

    public void setRunning(boolean running) {
        this.running = running;
    }

    public JogoThread(SurfaceHolder surfaceHolder, Jogo Jogo) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.Jogo = Jogo;
    }

    @Override
    public void run() {
       // Log.e("Teste", "Teste Thread");
        long startTime;
        long timeMillis = 1000/MAX_FPS;
        long waitTime;
        int frameCount = 0;
        long totalTime = 0;
        long targetTime = 1000/MAX_FPS;

        while(running) {
            startTime = System.nanoTime();
            canvas = null;

            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    this.Jogo.update();
                    this.Jogo.draw(canvas);
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                if(canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            timeMillis = (System.nanoTime() - startTime)/1000000;
            waitTime = targetTime - timeMillis;
            try {
                if(waitTime > 0)
                    this.sleep(waitTime);
            } catch(Exception e) {
                e.printStackTrace();
            }
            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if(frameCount == MAX_FPS) {
                averageFPS = 1000/((totalTime/frameCount)/1000000);
                frameCount = 0;
                totalTime = 0;
                System.out.println(averageFPS);
            }
        }
    }
}
