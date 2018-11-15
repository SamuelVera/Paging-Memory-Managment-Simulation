package logica;

public class Interfaz {
    
    private PlanificadorMid pm; //Planificador de mediano plazo
    
    public Interfaz(double tamMarco, double tamMP, double tamMS){
        int numMarcos;
        numMarcos = (int) (tamMP/tamMarco);
            //Inicializar los datos para el planificador de mediano plazo
        this.pm = new PlanificadorMid(numMarcos, tamMP, tamMS, tamMarco);
    }
    
    public int getNumMarcos(){
        return this.pm.getNumMarcos();
    }
    
    public int getTamMarco(){
        return (int) this.pm.getTamMarco();
    }
    
    public Proceso getProceso(String i){
        return this.pm.getProceso(i);
    }
    
    public boolean getMarcoValue(int i){
        return this.pm.getMarcoValue(i);
    }
    
    public double getTamMP(){
        return this.pm.getTamMP();
    }
    
    public double getTamMS(){
        return this.pm.getTamMS();
    }
    
    public void crearProceso(String id, double tam) throws InterruptedException{
        this.pm.crearProceso(id, tam);
    }
    
    public int getNumProcesos(){
        return this.pm.getNumProcesos();
    }
    
    public String getEstadoProceso(String i){
        return this.getProceso(i).getEstado();
    }
}
