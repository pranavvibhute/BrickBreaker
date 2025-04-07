public class Anim implements Runnable {
   
    GamePanel gp;

    public Anim(GamePanel p){
        gp = p;
    }

    @Override
    public void run(){
        

        while(!Thread.currentThread().isInterrupted()){
            
            if(!gp.isPaused && !gp.isGameOver){
                gp.update();
            }

            try{
                Thread.sleep(10);
            }
            catch(InterruptedException e){
                Thread.currentThread().interrupt();
            }

        }
    }
}
