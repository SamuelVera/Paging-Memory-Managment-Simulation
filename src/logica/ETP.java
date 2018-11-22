package logica;

public class ETP { //Entrada de la tabla de página
    private boolean p; //Presente en memoria principal
    private boolean u; //Referenciado recientemente
    private int numMar; //Número de marco asociada
    private final int numPag; //Número de pagina del proceso
    private final String idProceso;
    
    public ETP(int i, String id){
        this.p = false;
        this.u = false;
        this.numPag = i;
        this.idProceso = id;
    }
    
    protected void setP(boolean set){
        this.p = set;
    }
    
    protected void setU(boolean set){
        this.u = set;
    }
    
    protected boolean getU(){
        return this.u;
    }
    
    public boolean getP(){
        return this.p;
    }
    
    public int getMarco(){
        if(this.p){
            return this.numMar;
        }else{
            return -1;
        }
    }
    
    protected String getIdProceso(){
        return this.idProceso;
    }
    
    protected void setMarco(int marco){
        this.numMar = marco;
    }
    
    protected int getNum(){
        return this.numPag;
    }
    
}
