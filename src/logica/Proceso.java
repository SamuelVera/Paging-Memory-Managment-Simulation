package logica;

import UI.UIEjecucion;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Proceso extends Thread{
    
    private final ETP[] paginas; //Tabla de páginas
    private final String id; //IDsec del proceso
        //0: Listo, 1: Bloqueado, 2: Ejecutandose
    private final boolean[] estado = {false, false, false};
    private final double tam; //Tamaño del proceso
    private final double frag; //Fragmentación de la última página en bytes
    private int tiempoRes;
    
        //Inicialización del proceso con su cantidad de páginas, id, 
    protected Proceso(String id, double tam, double tamPag){
        tam = tam*1024;
        int canPag = (int) (tam/tamPag); //Cantidad de páginas
        if(tam % tamPag != 0){ //Fragmentación interna en la última página
            canPag++;
        }
            //Tiempo de ejecución desde 3 a 5 segundos
        this.tiempoRes = ((new Random()).nextInt(3)+3)*1000;
        this.paginas = new ETP[canPag];
        this.id = id;
        this.tam = (tam);
        for(int i=0;i<this.paginas.length;i++){
            this.paginas[i] = new ETP(i,this.id);
        }
        this.frag = (canPag*tamPag) - tam;
    }
    
    @Override
    public void run(){
        int aux;
        if(OS.noInicia){
            try {
                OS.sincroStart.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(Proceso.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        while(this.tiempoRes > 0){
            try {
                if(this.tiempoRes > 0){
                    if(!this.estado[0]){ //Si no está en memoria principal y va a ejecutarse
                        
                    }
                    PlanificadorShort.aquireCPU(); //Entra al esperar por el CPU
                    this.setEstado(true, false, true); //En estado de ejecución
                    for(int i=0;i<6;i++){ //0.5 segundos en ejecución
                        if(tiempoRes > 0){
                            if(this.getCantidadPag() > this.getPaginasEnMp()){ 
                                aux = (new Random()).nextInt(2);
                                if(aux == 0){
                                    System.out.println("Fallo de página");
                                }
                            }
                            Thread.sleep(100); //Espera 0.1 segundos
                            if(this.tiempoRes > 0){
                                this.tiempoRes -= 100;
                            }
                        }else{
                            i = 6;
                        }
                    }
                    PlanificadorShort.releaseCPU(); //Libera al CPU antes de bloquearse o salir
                    if(this.tiempoRes > 0){
                        this.setEstado(true, true, false); //Se bloquea el proceso pero sigue en mp
                        
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Proceso.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.setEstado(false, false, false);
        if(this.tiempoRes != -1){
            OS.sacarFinalizado(this);
        }
    }
    
    public int getCantidadPag(){
        return this.paginas.length;
    }
    
        //Setear marco a la ETP
    protected void setMarcoToETP(int numMarco, int numPag){
        this.paginas[numPag].numMar = numMarco;
    }
    
    public double getTam(){
        return this.tam;
    }
    
    public String getIdP(){
        return this.id;
    }
    
    protected void setU(int i, boolean is){
        this.paginas[i].u = is;
    }
    
    public ETP getETP(int i){
        return this.paginas[i];
    }
    
    protected int getPaginasEnMp(){
        int cantidad = 0;
        for(int i=0;i<this.paginas.length;i++){
            if(this.paginas[i].getP()){
                cantidad++;
            }
        }
        return cantidad;
    }
    
    protected String getEstado(){
        if(this.estado[0]){
            if(this.estado[1]){
                return "Bloqueado";
            }else if(this.estado[2]){
                return "En ejecución";
            }else{
                return "Listo";
            }
        }else if(!this.estado[0]){
           return "Suspendido";
        }else{
            return "Eliminado";
        }
    }
    
    protected void setEstado(boolean i0, boolean i1, boolean i2){
        this.estado[0] = i0;
        this.estado[1] = i1;
        this.estado[2] = i2;
    }
    
    public double getFrag(){
        return this.frag;
    }
    
    protected void eliminar(){
        this.tiempoRes = -1;
    }
}
