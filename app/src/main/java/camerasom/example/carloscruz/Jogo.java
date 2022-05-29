package camerasom.example.carloscruz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import java.util.Random;



public class Jogo extends SurfaceView implements SurfaceHolder.Callback {


    //Receber valor da classe JogoThread
    private JogoThread thread;
    //receber valor da classe ObstacleManager
    private ObstacleManager ObstacleManager;
    //Criar um novo Rectangulo r para a função centrar Texto
    private Rect r = new Rect();

    // Novo jogador
    private Jogador player;
    //Nova posição do jogador
    private Point playerPoint;

    //Saber se o jogador está em movimento
    private boolean movingPlayer = false;

    //Tempo pós gameOver
    private long gameOverTime;

    //Saber se o gameOver está ativo
    private boolean gameOver = false;

    //Variavel para background
    private Drawable mCustomImage;

    //Variavel para a orientação
    private Orientacao orientacao;
    //Variavel para o tempo da orientação
    private long tempo;


    Context context;


    public Jogo(Context context) {
        super(context);

        
        SharedPreferences pref = context.getSharedPreferences("Key", Context.MODE_PRIVATE);
        Constantes.recorde = pref.getInt("Recorde", 0);

        //Guardar imagem na variavel
        mCustomImage = context.getResources().getDrawable(R.drawable.background);

        //acesso à superfície subjacente
        getHolder().addCallback(this);

        //chamar a classe thread e criar um thread novo
        thread = new JogoThread(getHolder(), this);

        Constantes.current = context;

        //Instanciar a classe orientação
        orientacao = new Orientacao();
        //função para registar os listenners
        orientacao.registar();
        //Tempo para o movimento
        tempo = System.currentTimeMillis();
        //Instaciar criandom um jogador novo
        player = new Jogador(new Rect(100, 100,250, 500), Color.argb(0,255, 0, 0));
        //Posição Inicial do Player
        playerPoint = new Point(Constantes.Screen_Width /2, 3*Constantes.Screen_Height/4);
        //atualizar a posição do jogador
        player.update(playerPoint);
        //Iniciar e criar os obstaculos no canvas
        Random rnd = new Random();
        ObstacleManager = new ObstacleManager(400, 1500, 90, Color.argb(200, rnd.nextInt( 200)+ 20, rnd.nextInt(200)+ 20, rnd.nextInt(200)+ 20));

        //Definir o focus da aplicação para este metodo
        setFocusable(true);
    }

    //Fazer o reset do jogo para recomeçar pós game Over
    public void reset(){
        //Reset do jogador
        player = new Jogador(new Rect(100, 100,250, 500), Color.argb(0,255, 0, 0));
        //Posição Inicial do Player
        playerPoint = new Point(Constantes.Screen_Width /2, 3*Constantes.Screen_Height/4);
        //dar update á posição do jogador
        player.update(playerPoint);
        //Remoçar obstaculos
        ObstacleManager = new ObstacleManager(400, 1500, 90, Color.WHITE);
        Constantes.pontos=0;

        //iniciar som
        SplashEnding.sound.start();
        SplashEnding.soundFire.start();
        //Colocar jogador sem movimento
        movingPlayer = false;


    }

    //Não funciona sem esta opção, mesmo estando vazia
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    //abrir o thread quando o jogo inicia
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        //Instaciar novo thread para jogar
        //ver classe thread caso esteja com crashs ou baixos fps
        thread = new JogoThread(getHolder(), this);

        Constantes.INIT_TIME = System.currentTimeMillis();
        //Iniciar Jogo no thread
        thread.setRunning(true);
        thread.start();
    }

    //Desligar o thread do jogo quando o jogo desliga
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        //Usar o retry para retirar o erro de crash quando usado o onPause
        while(retry) {
            try {
                thread.setRunning(false);
                thread.join();
            }catch(Exception e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    //Verificar o toque do player para defenir o que fazer com o jogador
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //Pedir a ação do jogador
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
               // Log.e("Erro1", "Case 1");
                //Se não houver game over e o jogador tiver um x e um y o jogaodor mexe-se
                if(!gameOver && player.getRectangle().contains((int)event.getX(), (int)event.getY()))
                    movingPlayer = true;
                   // Log.e("Erro2", "Case 2");
                //Se gameover ativo e o gameOverTime é menor
                // que 5 segundos então com touch podemos fazer reset
                if(gameOver && System.currentTimeMillis() - gameOverTime >= 2000){
                    reset();
                    gameOver = false;
                    orientacao.NovoJogo();

                }
                break;
            case MotionEvent.ACTION_MOVE:
                if(!gameOver && movingPlayer)
                   // Log.e("Erro3", "Case 3");
                    playerPoint.set((int)event.getX(), (int)event.getY());
                break;
            case MotionEvent.ACTION_UP:
                movingPlayer = false;
                break;
        }
        //return super.onTouchEvent(event);
        return true;
    }

    //Dar update ao canvas
    public void update() {



        //Se não houver colisão
        if(!gameOver) {
            //Iniciar o som do jogo
            SplashEnding.sound.start();
            SplashEnding.soundFire.start();
            //Se o tempo for menor que o tempo incial
            //Resolve o erro de sair da app e voltar a entrar
            if(tempo < Constantes.INIT_TIME)
                tempo = Constantes.INIT_TIME;
            int intervaloTempo = (int)(System.currentTimeMillis()  - tempo);
            //Guarda o tempo atual em tempo
            tempo = System.currentTimeMillis();
            //Se recebe valores da orientação então
            if(orientacao.getOrientation() != null && orientacao.getStartOrientation() != null) {
                //Pegar na orientação gerada na classe orientação
                float cimaBaixo = orientacao.getOrientation()[1] - orientacao.getStartOrientation()[1];
                float esquerdaDireita = orientacao.getOrientation()[2] - orientacao.getStartOrientation()[2];
                //Velocidade do player
                float velocidadeX = 2 * esquerdaDireita * Constantes.Screen_Width/1500f;
                float velocidadeY =-(2 * cimaBaixo * Constantes.Screen_Height/3000f);

                //Estabilizar a velocidade em caso de haver pequenas alterações
                playerPoint.x += Math.abs(velocidadeX*intervaloTempo) > 5 ? velocidadeX*intervaloTempo: 0;
                playerPoint.y += Math.abs(velocidadeY*intervaloTempo) > 10 ? velocidadeY*intervaloTempo: 0;
            }

            //Não deixar que a nave saia do ecrâ
            //limites da tela para x
            //Maior que 0
            if(playerPoint.x < 0)
                //Coloca a posição do player a 0
                playerPoint.x = 0;
                //Menor que o tamanho maximo do ecrâ
            else if(playerPoint.x > Constantes.Screen_Width)
                //Coloca a posição do player com o tamanho do ecrâ
                playerPoint.x = Constantes.Screen_Width;

            //limites da tela para Y
            //Maior que 0
            if(playerPoint.y < 0)
                //Coloca a posição do player a 0
                playerPoint.y= 0;
            //Menor que o tamanho maximo do ecrâ
            else if(playerPoint.y > Constantes.Screen_Height)
                //Coloca a posição do player com o tamanho do ecrâ
                playerPoint.y = Constantes.Screen_Height;
           // Log.e("teste", "Playing");
            //atualiza a posição do player
            player.update(playerPoint);
            //atualiza a posição do obstaculo
            ObstacleManager.update();
            //Se ouver colisão,
            if(ObstacleManager.playerCollide(player)){
                //Pausar som background
                SplashEnding.sound.pause();
                SplashEnding.soundFire.pause();
                //Iniciar Som de explosão
                SplashEnding.soundExplosion.start();

                //GameOver ativo
                gameOver = true;
                //Guarda o tempo atual
                gameOverTime = System.currentTimeMillis();
            }
        }
    }

    //Função para desenhar o jogo no canvas
    @Override
    public void draw(Canvas canvas) {

        super.draw(canvas);
        //Cor do background
       // canvas.drawColor(Color.WHITE);
        Rect imageBounds = canvas.getClipBounds();
        mCustomImage.setBounds(imageBounds);
        mCustomImage.draw(canvas);

        player.draw(canvas);
        ObstacleManager.draw(canvas);



        //Mostrar canvas mesmo quando perde
        if(gameOver){


            //Criar nova Pintura
            Paint paintStroke = new Paint();

            //Texto duplo com stroke
            paintStroke.setTextSize(100);
            paintStroke.setStyle(Paint.Style.STROKE);
            paintStroke.setStrokeWidth(30);
            paintStroke.setColor(Color.BLACK);
            textoCentradoStroke(canvas, paintStroke, "GameOver");
            textoCentradoPlusStroke(canvas, paintStroke, "Tap to Play Again");
            textoCentradoPontosStroke(canvas, paintStroke, "Pontos: " + Constantes.pontos);
            textoCentradoRecordeStroke(canvas, paintStroke, "Recorde: " + Constantes.recorde);
            Paint paint = new Paint();

            //Defenir o tamanho do texto
            paint.setTextSize(100);
            //Adicionar sombra ao texto
           // paint.setShader(new LinearGradient(0, 0, 0, getHeight(), Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

            //Definir a cor do texto
            paint.setColor(Color.WHITE);

            //Chamar a função para centrar o texto no ecrâ
            textoCentrado(canvas, paint, "GameOver");
            textoCentradoPlus(canvas, paint, "Tap to Play Again");
            textoCentradoPontos(canvas, paint, "Pontos: " + Constantes.pontos);
            textoCentradoRecorde(canvas, paint, "Recorde: " + Constantes.recorde);

        }
    }

    //Função para desenhar o texto no centro do ecrã
    private void textoCentrado(Canvas canvas, Paint paint, String text) {
        //Defenir o texto a esquerda
        paint.setTextAlign(Paint.Align.LEFT);

       // paint.setShader(100);
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom - 300;
        canvas.drawText(text, x, y, paint);

    }

    private void textoCentradoStroke(Canvas canvas, Paint paintStroke, String text) {
        //Definir o texto a esquerda
        paintStroke.setTextAlign(Paint.Align.LEFT);

        // paint.setShader(100);
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paintStroke.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom-300;
        canvas.drawText(text, x, y, paintStroke);

    }

    private void textoCentradoPlus(Canvas canvas, Paint paint, String text) {
        //Definir o texto a esquerda
        paint.setTextAlign(Paint.Align.LEFT);

        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom -100;
        canvas.drawText(text, x, y, paint);

    }

    private void textoCentradoPlusStroke(Canvas canvas, Paint paintStroke, String text) {
        //Definir o texto a esquerda
        paintStroke.setTextAlign(Paint.Align.LEFT);

        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paintStroke.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom -100;
        canvas.drawText(text, x, y, paintStroke);

    }
    private void textoCentradoPontos(Canvas canvas, Paint paint, String text) {
        //Definir o texto a esquerda
        paint.setTextAlign(Paint.Align.LEFT);

        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom + 100;
        canvas.drawText(text, x, y, paint);

    }

    private void textoCentradoPontosStroke(Canvas canvas, Paint paintStroke, String text) {
        //Definir o texto a esquerda
        paintStroke.setTextAlign(Paint.Align.LEFT);

        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paintStroke.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom + 100;
        canvas.drawText(text, x, y, paintStroke);

    }

    private void textoCentradoRecorde(Canvas canvas, Paint paint, String text) {
        //Definir o texto a esquerda
        paint.setTextAlign(Paint.Align.LEFT);

        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paint.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom + 300;
        canvas.drawText(text, x, y, paint);

    }

    private void textoCentradoRecordeStroke(Canvas canvas, Paint paintStroke, String text) {
        //Definir o texto a esquerda
        paintStroke.setTextAlign(Paint.Align.LEFT);

        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        paintStroke.getTextBounds(text, 0, text.length(), r);
        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 2f + r.height() / 2f - r.bottom + 300;
        canvas.drawText(text, x, y, paintStroke);

    }




}
