package logica;

import java.util.LinkedList;

public class PlanificadorShort{

    private static final LinkedList colaListos = new LinkedList(); //Lista de procesos listos
    protected static int state ;
    
    protected static void aquireCPU() {
        PlanificadorShort.acquire();
    }

    protected static void releaseCPU() {
        PlanificadorShort.release();
    }
  
    private static void acquire(){
        Object object = new Object();

        synchronized (object) {
            try {
                PlanificadorShort.state--;
                if (PlanificadorShort.state < 0) {
                    PlanificadorShort.colaListos.addFirst(object);
                    object.wait();                
                }
            } catch (InterruptedException ie) {
            }
        }
    }
    
    private static void release() {
        PlanificadorShort.state++;
        if(PlanificadorShort.state <= 0){
            Object object = PlanificadorShort.colaListos.removeFirst();
            synchronized (object) {
                object.notify();
            }
        }
        
    }
    
}
