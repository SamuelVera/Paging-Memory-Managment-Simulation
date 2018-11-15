package logica;

public class Proceso{
    
    private ETP[] paginas; //Tabla de páginas
    private String id; //IDsec del proceso
        //0: Activo, 1: Bloqueado, 2: Ejecutandose
    boolean[] estado = {false, false, false};
    private double tam; //Tamaño del proceso
    private double frag; //Fragmentación de la última página en bytes
    
        //Inicialización del proceso con su cantidad de páginas, id, 
    public Proceso(String id, double tam, double tamPag){
        tam = tam*1024;
        int canPag = (int) (tam/tamPag); //Cantidad de páginas
        if(tam % tamPag != 0){ //Fragmentación interna en la última página
            canPag++;
        }
        this.paginas = new ETP[canPag];
        this.id = id;
        this.tam = (tam);
        for(int i=0;i<this.paginas.length;i++){
            this.paginas[i] = new ETP(i,this.id);
        }
        this.frag = (canPag*tamPag) - tam;
    }
    
    public int getCantidadPag(){
        return this.paginas.length;
    }
    
        //Setear marco a la ETP
    protected void setMarcoToETP(int numMarco, int numPag){
        this.paginas[numPag].setP(true);
        this.paginas[numPag].numMar = numMarco;
    }
    
    protected double getTam(){
        return this.tam;
    }
    
    public String getId(){
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
        if(this.estado[1] == true){
            return "Suspendido";
        }else{
            if(this.estado[2] == true){
                return "Ejecutandose";
            }else{
                return "Listo";
            }
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
    
}
