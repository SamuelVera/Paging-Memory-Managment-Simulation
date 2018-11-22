package logica;

public class Marco {
    
    private ETP pagina;
    private final int numMarco;
    private boolean libre;
    
    protected Marco(int numMarco){
        this.numMarco = numMarco;
        this.libre = true;
    }
    
    protected int getNumMarco(){
        return this.numMarco;
    }
    
    protected ETP getPagina(){
        if(!this.libre){
            return this.pagina;
        }else{
            return null;
        }
    }
    
    protected boolean getLibre(){
        return this.libre;
    }
    
    protected void setPagina(ETP p){
        this.pagina = p;
    }
    
    protected void setLibre(boolean p){
        this.libre = p;
    }
    
}
