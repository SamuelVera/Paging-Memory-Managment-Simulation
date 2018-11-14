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
    
    public double getTamMP(){
        return this.pm.getTamMP();
    }
    
    public double getTamMS(){
        return this.pm.getTamMS();
    }
    
}
