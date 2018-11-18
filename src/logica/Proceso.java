package logica;

import UI.UIEjecucion;
import java.util.LinkedList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Proceso extends Thread{
    
    private final ETP[] paginas; //Tabla de páginas
    private int punteroCarga; //Puntero de página a ejecutar
    private final String id; //IDsec del proceso
        //0: Listo; 1: Bloqueado; 2: Suspendido; 3: Ejecutandose
    private final boolean[] estado = {false, false, false, false};
    private final double tam; //Tamaño del proceso
    private final double frag; //Fragmentación de la última página en bytes
    private int tiempoRes;
    private boolean firstTime;
    
        //Inicialización del proceso con su cantidad de páginas, id, 
    protected Proceso(String id, double tam, double tamPag){
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
        this.firstTime = true;
        this.punteroCarga = 0;
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
                    this.setEstado(true, false, false, false); //Dar estado de listo
                    UIEjecucion.updateProcessIDs(); //Actualización visual
                    if(this.firstTime){//La primera vez que entra adquiere el primer puesto
                        PlanificadorShort.aquireCPUPrio(); //Entra al esperar por el CPU con prioridad
                        this.firstTime = false;
                    }else{
                        PlanificadorShort.aquireCPU(); //Entra a esperar por el CPU
                    }
                        //Si al menos la mitad de sus páginas no está en memoria principal y va a ejecutarse
                    if(this.getPaginasEnMp() < ((this.getCantidadPag()/2)+1)){ 
                        this.cargarPag(2);
                    }
                    this.setEstado(false, false, false, true); //En estado de ejecución
                    UIEjecucion.updateProcessIDs(); //Actualización visual
                    
                        //Rutina de ejecución
                    for(int i=0;i<4;i++){ //0.5 segundos en ejecución
                        if(this.tiempoRes > 0 && (this.getPaginasEnMp()>0)){
                            this.referenciaPag();
                            Thread.sleep(2000); //Espera 0.1 segundos
                            if(this.tiempoRes > 0){
                                this.tiempoRes -= 250;
                            }
                        }else{
                            i = 4;
                        }
                    }
                    
                    PlanificadorShort.releaseCPU(); //Libera al CPU antes de bloquearse o salir
                    if(this.tiempoRes > 0 && (this.getPaginasEnMp()>0)){
                        this.setEstado(false, true, false, false); //Se bloquea por E/S
                        UIEjecucion.updateProcessIDs(); //Actualización visual
                        aux = ((new Random()).nextInt(3)+2)*1000; //Tiempo de bloqueo de 2 a 4 segundos
                        if(aux > this.tiempoRes){
                            aux = this.tiempoRes; //Esperar por el tiempo que le queda
                            this.tiempoRes = 0; 
                        }else{
                            this.tiempoRes -= aux;
                        }
                        Thread.sleep(aux); //Tiempo de bloqueo
                            //Cambio de estado al desbloquearse
                        if(this.getPaginasEnMp() == 0 ){ //Si no tiene páginas en mp está suspendido
                            this.setEstado(false, false, true, false); //Estado supendido
                            UIEjecucion.updateProcessIDs();
                        }else{
                            this.setEstado(true, false, false, false); //Estado Listo
                            UIEjecucion.updateProcessIDs();
                        }
                    }else{
                        this.setEstado(false, false, true, false); //Estado supendido
                        UIEjecucion.updateProcessIDs();
                        Thread.sleep(2000); //2 segundos suspendido
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Proceso.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.setEstado(false, false, false, false); //Sale 
        UIEjecucion.updateProcessIDs();
        if(this.tiempoRes != -1){ try {
            //Si su tiempo restante es -1 salió por expulsión del usuario
            OS.sacarFinalizado(this);
            } catch (InterruptedException ex) {
                Logger.getLogger(Proceso.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

        //Se referencian las próximas 4 páginas
        //Se actualiza su referencia
        //O se produce un fallo de página
    private void referenciaPag() throws InterruptedException{
        for(int i=0;i<4;i++){
            if(this.getETP(this.punteroCarga).getP()){
                this.setU(this.punteroCarga, true);
            }else{  //Se pagina por adelantado un cuarto o menos de las páginas que no esten en mp
                this.cargarPag(4);
            }
            this.punteroCarga = ((this.punteroCarga+1)%this.paginas.length);
        }
    }
    
        //Cargar páginas de un proceso que estaba fuera de mp
    private void cargarPag(int factor) throws InterruptedException{
            //Deben cargarse la mitad de las páginas a partir del puntero
        LinkedList aux = new LinkedList();
        Object[] aux2;
        int[] aux1;
        int apuntador = this.punteroCarga;
        int cantCargar = (this.getCantidadPag()/factor);
        if(cantCargar >(this.getCantidadPag()-this.getPaginasEnMp())){
            cantCargar = (this.getCantidadPag()-this.getPaginasEnMp());
        }
        for(int i=0;i<this.getCantidadPag();i++){
            if(!this.paginas[apuntador].getP()){
                aux.add(apuntador);
                if(aux.size()==cantCargar){
                    break;
                }
            }
            apuntador = ((apuntador+1)%this.paginas.length);
        }
        aux2 = aux.toArray();
        aux1 = new int[aux2.length];
        for(int i=0;i<aux2.length;i++){
            aux1[i] = (int)aux2[i];
        }
        PlanificadorMid.escribirPag(this, aux1); //Escribir página
        this.setEstado(false, false, false, true); //En estado de ejecución
        UIEjecucion.updateProcessIDs(); //Actualización visual
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
            return "Listo";
        }else if(this.estado[1]){
            return "Bloqueado";
        }else if(this.estado[2]){
            return "Suspendido";
        }else if(this.estado[3]){
            return "En ejecución";
        }else{
            return "Eliminado";
        }
    }
    
    protected void setEstado(boolean i0, boolean i1, boolean i2, boolean i3){
        this.estado[0] = i0;
        this.estado[1] = i1;
        this.estado[2] = i2;
        this.estado[3] = i3;
    }
    
    public double getFrag(){
        return this.frag;
    }
    
    protected void eliminar(){
        this.tiempoRes = -1;
    }
    
    
    
}
