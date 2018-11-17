package logica;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class PlanificadorShort{

    private static final LinkedList colaListos = new LinkedList(); //Lista de procesos listos
    protected static int state ;
    private static Semaphore accessSema = new Semaphore(1);
    
    protected static void aquireCPUPrio() {
        PlanificadorShort.acquirePrio();
    }
    
    protected static void aquireCPU(){
        PlanificadorShort.acquire();
    }

    protected static void releaseCPU() throws InterruptedException {
        PlanificadorShort.release();
    }
    
    private static void acquire(){
        Object object = new Object();

        synchronized (object) {
            try {
                PlanificadorShort.accessSema.acquire();
                PlanificadorShort.state--;
                if (PlanificadorShort.state < 0) {
                    PlanificadorShort.colaListos.addLast(object);
                    PlanificadorShort.accessSema.release();
                    object.wait();                
                }else{
                    PlanificadorShort.accessSema.release();
                }
            } catch (InterruptedException ie) {
            }
        }
    }
  
    private static void acquirePrio(){
        Object object = new Object();
        synchronized (object) {
            try {
                PlanificadorShort.accessSema.acquire();
                PlanificadorShort.state--;
                if (PlanificadorShort.state < 0) {
                    PlanificadorShort.colaListos.addFirst(object);
                    PlanificadorShort.accessSema.release();
                    object.wait();                
                }else{
                    PlanificadorShort.accessSema.release();
                }
            } catch (InterruptedException ie) {
            }
        }
    }
    
    private static void release() throws InterruptedException {
        PlanificadorShort.accessSema.acquire();
        PlanificadorShort.state++;
        if(PlanificadorShort.state <= 0){
            Object object = PlanificadorShort.colaListos.removeFirst();
            PlanificadorShort.accessSema.release();
            synchronized (object) {
                object.notify();
            }
        }else{
            PlanificadorShort.accessSema.release();
        }
    }
}
