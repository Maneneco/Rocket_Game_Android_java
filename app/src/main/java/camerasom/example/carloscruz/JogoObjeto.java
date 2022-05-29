package camerasom.example.carloscruz;

import android.graphics.Canvas;
import android.util.Log;

//Só funciona com a interface
public interface JogoObjeto {
    public void draw(Canvas canvas);
    public void update();
}
