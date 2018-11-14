package logica;

import java.util.LinkedList;

public class PlanificadorMid {
    
    private boolean[] marcos; //Marcos en memoria principal
    private Pagina[] paginasEnMp; //Páginas en memoria principal
    private Proceso[] procesos; //Global de los procesos
    private double tamMP; //Tamaño de la memoria principal
    private double tamMS; //Tamaño de la memoria secundaria
    private double tamMarco;
    
    PlanificadorMid(int numMarcos, double tamMP, double tamMS, double tamMarco){
        this.marcos = new boolean[numMarcos];
        for(int i=0;i<this.marcos.length;i++){
            this.marcos[i] = true; //Inicializar como marcos vacios
        }
        this.tamMP = tamMP;
        this.tamMS = tamMS;
        this.tamMarco = tamMarco;
    }
    
    public void crearProceso(){
    }
    
    public void suspProceso(){
        
    }
    
    public void elimProceso(){
        
    }
    
    private void sacarProcesoMem(){
        
    }
    
    public int getNumMarcos(){
        return this.marcos.length;
    }
    
    public double getTamMarco(){
        return this.tamMarco;
    }
    
    public double getTamMP(){
        return this.tamMP;
    }
    
    public double getTamMS(){
        return this.tamMS;
    }
    
}
