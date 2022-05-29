package camerasom.example.carloscruz;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.animation.Animation;

public class AnimacaoNave {

    //Array de imagens
    private Bitmap[] frames;
    private int frameIndex = 0;
    private float frameTime;
    private long lastFrame;

    private boolean isPlaying = false;

    public boolean isPlaying(){
        return isPlaying;
    }

    public void play(){
        isPlaying = true;
       // frameIndex = 0;
        lastFrame = System.currentTimeMillis();
    }

    private void stop(){

    }

    public AnimacaoNave(Bitmap[] frames, float animTime){
        this.frames = frames;
     //   frameIndex = 0;

         frameTime = animTime/frames.length;

         lastFrame = System.currentTimeMillis();
    }

    public void draw(Canvas canvas, Rect destination){
        if(!isPlaying)
            return;
    //
        //incrememtar o index
        frameIndex++;
        //Se for maior que 3 recomeça
        if (frameIndex == 3)
        {
            frameIndex = 0;
        }
        //Verificar o novo valor do index, para atualizar com a nova imagem
        canvas.drawBitmap(frames[frameIndex], null, destination, new Paint());

    }
    public void update(){
        if(System.currentTimeMillis() - lastFrame > frameTime*1000){
            frameIndex++;
            frameIndex = frameIndex >= frames.length ? 0 : frameIndex;
            lastFrame = System.currentTimeMillis();
        }
    }

}
