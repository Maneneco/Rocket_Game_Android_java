package camerasom.example.carloscruz;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Orientacao  implements SensorEventListener {

    private SensorManager manager;

    private Sensor acelarometro;

    private Sensor Magnetometro;

    private float[] acelarometroOut;
    private float[] magnometroOut;

    private float [] orientacao = new float[3];

    public float[] getOrientation(){
        return orientacao;
    }

    private float[] startOrientation = null;

    public float[] getStartOrientation(){
            return startOrientation;
    }



    //Reset da orientação do telemovel, para não iniciar na
    public void NovoJogo(){
        startOrientation = null;
    }

    public Orientacao(){
        manager = (SensorManager)Constantes.current.getSystemService(Context.SENSOR_SERVICE);
        acelarometro = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Magnetometro = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void registar() {
        manager.registerListener(this, acelarometro, SensorManager.SENSOR_DELAY_GAME);
        manager.registerListener(this, Magnetometro, SensorManager.SENSOR_DELAY_GAME);
    }

    public void pause(){
        manager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){

    }

    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            acelarometroOut = event.values;
        else if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD)
            magnometroOut = event.values;
        if(acelarometroOut != null && magnometroOut != null){
            float [] R = new float[9];
            float [] I = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, acelarometroOut, magnometroOut);
            if(success){
                SensorManager.getOrientation(R, orientacao);
                if(startOrientation == null){
                    startOrientation = new float[orientacao.length];
                    System.arraycopy(orientacao, 0, startOrientation, 0, orientacao.length);
                }
            }
        }
    }
}
