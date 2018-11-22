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
        //0: Listo; 1: Bloqueado; 2: Ejecutandose
    private final boolean[] estado = {false, false, false};
    private final double tam; //Tamaño del proceso
    private final double frag; //Fragmentación de la última página en bytes
    private int tiempoRes;
    private boolean eliminado;
    private boolean firstTime;
    
        //Inicialización del proceso con su cantidad de páginas, id, 
    public Proceso(String id, double tam, double tamPag){
        int canPag = (int) (tam/tamPag); //Cantidad de páginas
        if(tam % tamPag != 0){ //Fragmentación interna en la última página
            canPag++;
        }
            //Tiempo de ejecución desde 3 a 5 segundos
        this.tiempoRes = ((new Random()).nextInt(3)+3)*1000;
        this.paginas = new ETP[canPag];
        this.id = id;
        this.eliminado = false;
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
        while(this.tiempoRes > 0 && !this.eliminado){
            try {
                if(this.tiempoRes > 0 && !this.eliminado){
                    this.setEstado(true, false, false); //Dar estado de listo
                    UIEjecucion.updateProcessIDs(); //Actualización visual
                    if(this.firstTime){//La primera vez que entra adquiere el primer puesto
                        PlanificadorShort.aquireCPUPrio(); //Entra al esperar por el CPU con prioridad
                        this.firstTime = false;
                    }else{
                        PlanificadorShort.aquireCPU(); //Entra a esperar por el CPU
                    }
                        //Si al menos la mitad de sus páginas no está en memoria principal y va a ejecutarse
                        //Y no se bloqueo previamente
                    if(!this.eliminado){
                        if(this.getPaginasEnMp() < ((this.getCantidadPag()/2)+1) && !this.estado[1]){ 
                            this.enviarSolic(2);
                        }
                        if(!this.estado[1]){//Si no se bloqueo previamente
                            this.setEstado(false, false, true); //En estado de ejecución
                            UIEjecucion.updateProcessIDs(); //Actualización visual
                        }
                    }
                        //Rutina de ejecución
                    for(int i=0;i<4;i++){ //En ejecución
                        if(this.tiempoRes > 0 && (this.getPaginasEnMp()>0) && !this.estado[1] && !this.eliminado){
                            Thread.sleep(1000); //Espera 1 segundos
                            this.referenciaPag();
                            this.tiempoRes -= 250;
                        }else{
                            if(this.eliminado && i != 0){
                                this.eliminar();
                            }
                            i = 10;
                        }
                    }
                    PlanificadorShort.releaseCPU(); //Libera al CPU antes de bloquearse o salir
                    if(!this.eliminado){
                        if(this.tiempoRes>0 && (this.getPaginasEnMp()>0) && !this.estado[1]){
                            this.setEstado(false, true, false); //Se bloquea por E/S
                            UIEjecucion.updateProcessIDs(); //Actualización visual
                            aux = ((new Random()).nextInt(3)+1)*1000; //Tiempo de bloqueo de 1 a 3 segundos
                            if((this.tiempoRes - 250) == 0){
                                aux = this.tiempoRes; //Esperar por el tiempo que le queda
                                this.tiempoRes = 0; 
                            }else{
                                this.tiempoRes -= 250;
                            }
                            Thread.sleep(aux); //Tiempo de bloqueo
                        }else if(this.estado[1]){ //Se bloqueo durante la ejecución o antes de empezar a ejecutar
                            Thread.sleep(1000); //1 segundos bloqueado
                        }else{
                            UIEjecucion.updateProcessIDs();
                            Thread.sleep(1000); //Ver que se suspende
                        }
                        this.setEstado(true, false, false); //Estado Listo
                        UIEjecucion.updateProcessIDs();
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Proceso.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.setEstado(false, false, false); //Sale 
        if(!this.eliminado){
            try {
                    //Sale de memoria
                OS.sacarFinalizado(this);
                PlanificadorMid.sacarProcesoMem(this);
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
                PlanificadorMid.accesoMem.acquire();
                if(!this.eliminado){
                    this.enviarSolic(4); //Cargar páginas si no ha sido eliminado
                }
                PlanificadorMid.accesoMem.release();
            }
            this.punteroCarga = ((this.punteroCarga+1)%this.paginas.length);
        }
    }
    
        //Enviar solicitud de cargar páginas de un proceso que produjeron un fallo
    private void enviarSolic(int factor) throws InterruptedException{
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
    }
    
    public int getCantidadPag(){
        return this.paginas.length;
    }
    
        //Setear marco a la ETP
    protected void setMarcoToETP(int numMarco, int numPag){
        this.paginas[numPag].setMarco(numMarco);
    }
    
    public double getTam(){
        return this.tam;
    }
    
    public String getIdP(){
        return this.id;
    }
    
    protected void setU(int i, boolean is){
        this.paginas[i].setU(is);
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
            if(this.getPaginasEnMp() > 0){
            return "Listo";
            }else{
                return "S/Listo";
            }
        }else if(this.estado[1]){
            if(this.getPaginasEnMp() > 0){
            return "Bloqueado";
            }else{
                return "S/Bloqueado";
            }
        }else if(this.estado[2]){
            return "En ejecución";
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
    
    protected void eliminar() throws InterruptedException{
        this.eliminado = true;
        this.setEstado(false, false, false);
        PlanificadorMid.sacarProcesoMem(this);
    }
    
        //Para efectos de la coherencia
    protected void setPagina(ETP p){
        this.paginas[p.getNum()] = p;
    }
    
}
