package camerasom.example.carloscruz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

public class Jogador implements JogoObjeto {

    private Rect rectangle;
    private int color;

    private AnimacaoNave subidaAnimacao;

    public Jogador(Rect rectangle, int color){
        this.rectangle = rectangle;
        this.color = color;

        //Criar um bitmap de imagens
        BitmapFactory bf = new BitmapFactory();
        //Adicionar as imagens as variaveis
        Bitmap Subida = bf.decodeResource(Constantes.current.getResources(), R.drawable.nave_01);
        Bitmap Subida2 = bf.decodeResource(Constantes.current.getResources(), R.drawable.nave_02);
        Bitmap Subida3 = bf.decodeResource(Constantes.current.getResources(), R.drawable.nave_03);
        Bitmap Subida4 = bf.decodeResource(Constantes.current.getResources(), R.drawable.nave_04);

        //Nova animação com as imagens no array
        subidaAnimacao = new AnimacaoNave(new Bitmap[]{Subida, Subida2, Subida3,Subida4}, 0.5f);

    }

//Getter
    public Rect getRectangle(){
        return rectangle;
    }

    @Override
    public void draw(Canvas canvas){
        //Criar nova tinta
        Paint paint = new Paint();
        //atribuir a tinta ao rectangulo
        paint.setColor(color);
        //desenhar o rectangulo
        canvas.drawRect(rectangle, paint);

//        subidaAnimacao.update();
        subidaAnimacao.draw(canvas, rectangle);

    }

    //Necessário para atualizar os dados
    @Override
    public void update(){

    }

    //Atualizar os pontos de colisão.
    public void update(Point point){
        subidaAnimacao.play();
        rectangle.set(point.x - rectangle.width()/2, point.y - rectangle.height()/2, point.x + rectangle.width()/2, point.y + rectangle.height()/2);
    }
}
