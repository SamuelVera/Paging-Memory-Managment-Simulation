package logica;

import UI.UIEjecucion;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class OS {
    
    protected static Marco[] marcos; //Marcos en memoria principal (true = libre; false = ocupado)
    protected static double tamMP; //Tamaño de la memoria principal
    protected static double tamMS; //Tamaño de la memoria secundaria
    protected static double tamMarco;

        //Sincronizar inicio de procesos definidos anteriormente
    protected static Semaphore sincroStart = new Semaphore(0);
    protected static boolean noInicia;
    public static LinkedList<Proceso> procesos = new LinkedList<Proceso>(); //Tabla de procesos del OS
    
        //Inicialización del OS
    public OS(double tamMarco, double tamMP, double tamMS){
            //Inicialización de la memoria
        int numMarcos;
        numMarcos = (int) (tamMP/tamMarco);
            //Inicializar los datos para el planificador de mediano plazo
        OS.marcos = new Marco[numMarcos];
        for(int i=0;i<OS.marcos.length;i++){
            OS.marcos[i] = new Marco(i); //Inicializar los marcos
        }
        OS.tamMP = tamMP;
        OS.tamMS = tamMS;
        OS.tamMarco = tamMarco;
        
        PlanificadorMid.apuntador = 0; //Inicializar planificador de mediano plazo
        PlanificadorShort.state = 1; //Iniciar planificador de corto plazo
        OS.noInicia = true;
    }
    
    public void crearProceso(String id, double tam) throws InterruptedException{
        Proceso p = new Proceso(id, tam, OS.getTamMarco());
        
            //Determinar si es creable el proceso
        if((p.getCantidadPag()<(2*OS.getNumMarcos())) && p.getTam()<=OS.getRestanteMs()){ 
            PlanificadorMid.cargarProceso(p); //Crear proceso y asignarle memoria
            UIEjecucion.ocupado += p.getTam();
            UIEjecucion.labelIdProcesos.add(p.getIdP());
            p.start(); //Iniciar el proceso
            OS.procesos.add(p); //Añadir a la tabla de procesos
            UIEjecucion.updateProcessIDs();
        }else{
            System.out.println("Add JOptinonPane de ms insuficiente o proceso muy grande");
        }
    }
    
    public void eliminarProceso(String id){
        Proceso p = OS.getProceso(id);
        p.eliminar();
        PlanificadorMid.sacarProcesoMem(p);
        int aux = OS.getProcesoTableIndex(id);
        OS.procesos.remove(aux);
        UIEjecucion.ocupado -= p.getTam();
        UIEjecucion.labelIdProcesos.remove(aux);
        UIEjecucion.updateProcessIDs();
    }
    
    protected static void sacarFinalizado(Proceso p){
        PlanificadorMid.sacarProcesoMem(p);
        int aux = OS.getProcesoTableIndex(p.getIdP());
        OS.procesos.remove(aux);
        UIEjecucion.ocupado -= p.getTam();
        UIEjecucion.ocuLabel.setText("Espacio ocupado: "+(UIEjecucion.ocupado/(1024*1024))+" Mb");
        UIEjecucion.labelIdProcesos.remove(aux);
        UIEjecucion.updateProcessIDs();
    }
    
    public static int getNumMarcos(){
        return OS.marcos.length;
    }
    
    public static double getTamMarco(){
        return OS.tamMarco;
    }
    
    public static Proceso getProceso(String i){
        if(OS.procesos.size()>0){
            Object[] aux = OS.procesos.toArray();
            for(int j=0;j<aux.length;j++){
                if(i.equals(((Proceso)aux[j]).getIdP())){
                    return (Proceso)aux[j];
                }
            }
        }
        return null;
    }
    
    protected Proceso getProcesoByIndex(int i){
        return (Proceso)OS.procesos.remove(i);
    }
    
    protected static int getProcesoTableIndex(String i){
        if(OS.procesos.size()>0){
            Object[] aux = OS.procesos.toArray();
            for(int j=0;j<aux.length;j++){
                if(i.equals(((Proceso)aux[j]).getIdP())){
                    return j;
                }
            }
        }
        return -1;
    }
    
    protected static void actualizarProceso(Proceso p){
        int aux = OS.getProcesoTableIndex(p.getIdP());
        OS.procesos.remove(aux);
        OS.procesos.add(aux, p);
    }
    
    public double getTamMP(){
        return OS.tamMP;
    }
    
    public double getTamMS(){
        return OS.tamMS;
    }
    
    public int getNumProcesos(){
        return OS.procesos.size();
    }
    
    public String getEstadoProceso(String i){
        return this.getProceso(i).getEstado();
    }
    
    public static double getRestanteMs(){
        double ocu = 0;
        Object[] aux = OS.procesos.toArray();
        for(int i=0;i<aux.length;i++){
            ocu = ocu + ((Proceso)aux[i]).getTam();
        }
        ocu = OS.tamMS-ocu;
        return ocu;
    }
    
    public void startSimul(){
        OS.noInicia = false;
        OS.sincroStart.release(OS.sincroStart.getQueueLength());
    }
    
}
