package camerasom.example.carloscruz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class SplashEnding extends Activity {

    public static MediaPlayer sound;
    public static  MediaPlayer soundExplosion;
    public static  MediaPlayer soundFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Pegar nas dimensões do ecrâ
        //Criar metricas do ecrâ
        DisplayMetrics dm = new DisplayMetrics();
        //buscar medidas do ecrâ utilizado
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        //Gravar os valores nas constantes da classe constantes
        Constantes.Screen_Width = dm.widthPixels;
        Constantes.Screen_Height = dm.heightPixels;

        //Colocar a fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Retirar a borda no topo do ecrã
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        sound = MediaPlayer.create(SplashEnding.this, R.raw.sound);
        sound.setLooping(true); // Set looping
        sound.setVolume(1.0f, 1.0f);

        soundExplosion = MediaPlayer.create(SplashEnding.this, R.raw.explosion);
        soundExplosion.setVolume(1.0f, 1.0f);

        soundFire = MediaPlayer.create(SplashEnding.this, R.raw.fire_sound);
        soundFire.setVolume(0.6f, 0.6f);

        //Mudar o conteudo do ecrâ para a classe Jogo
        setContentView(new Jogo(this));
    }

    @Override
    protected void onPause() {
        super.onPause();
        sound.stop();
        soundExplosion.stop();
        soundFire.stop();
    }
}