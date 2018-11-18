package logica;

import UI.UIEjecucion;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class OS{
    
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
    
    public static void crearProceso(String id, double tam) throws InterruptedException{
        Proceso p = new Proceso(id, tam, OS.getTamMarco());
        
            //Determinar si es creable el proceso
        if((p.getCantidadPag()<(2*OS.getNumMarcos())) && p.getTam() <= OS.getDisponibleMs()){ 
            PlanificadorMid.cargarProceso(p); //Crear proceso y asignarle memoria
            UIEjecucion.disMs = OS.getDisponibleMs();
            p.start(); //Iniciar el proceso
            if(!OS.noInicia){
                UIEjecucion.updateProcessIDs();
            }
            OS.procesos.add(p); //Añadir a la tabla de procesos
        }else{
            System.out.println("Add JOptinonPane de ms insuficiente o proceso muy grande");
        }
    }
    
    public static void eliminarProceso(Proceso p) throws InterruptedException{
        p.eliminar();
        PlanificadorMid.sacarProcesoMem(p);
        int aux = OS.getProcesoTableIndex(p.getIdP());
        OS.procesos.remove(aux);
        UIEjecucion.disMs = OS.getDisponibleMs();
        UIEjecucion.updateProcessIDs();
    }
    
    protected static void sacarFinalizado(Proceso p) throws InterruptedException{
        PlanificadorMid.sacarProcesoMem(p);
        int aux = OS.getProcesoTableIndex(p.getIdP());
        UIEjecucion.updateProcessIDs();
        OS.procesos.remove(aux);
        UIEjecucion.disMs = OS.getDisponibleMs();
        UIEjecucion.disMsLabel.setText("Espacio disponible: "+(UIEjecucion.disMs/(1024*1024))+" Mb");
    }
    
    public static void suspenderProceso(Proceso p) throws InterruptedException{
        
        PlanificadorMid.sacarProcesoMem(p);
        p.setEstado(false, false, true, false); //Asignar estado de suspendido
        UIEjecucion.updateProcessIDs();
        
    }
    
    public static int getNumMarcos(){
        return OS.marcos.length;
    }
    
    public static double getTamMarco(){
        return OS.tamMarco;
    }
    
        //Hay marcos vacíos y cuantos son
    protected static int getMarcosVacios(){
        int cantidad = 0;
        for(int i=0;i<OS.marcos.length;i++){
            if(OS.marcos[i].getLibre() == true){
                cantidad++;
            }
        }
        return cantidad;
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
    
    public static double getTamMP(){
        return OS.tamMP;
    }
    
    public static double getTamMS(){
        return OS.tamMS;
    }
    
    public static int getNumProcesos(){
        return OS.procesos.size();
    }
    
    public static String getEstadoProceso(String i){
        return OS.getProceso(i).getEstado();
    }
    
    protected static double getDisponibleMs(){
        double ocu = 0;
        Object[] aux = OS.procesos.toArray();
        for(int i=0;i<aux.length;i++){
            ocu = ocu + ((Proceso)aux[i]).getTam();
        }
        return OS.getTamMS() - ocu;
    }
    
    protected static double getDisponibleMp(){
        double ocu = 0;
        for(int i=0;i<OS.marcos.length;i++){
            if(!OS.marcos[i].getLibre()){
                ocu += OS.getTamMarco();
            }
        }
        return OS.getTamMP() - ocu;
    }
    
    public static void startSimul(){
        OS.noInicia = false;
        OS.sincroStart.release(OS.sincroStart.getQueueLength());
    }
}
