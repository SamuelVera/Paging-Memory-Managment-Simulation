package logica;

import UI.UIEjecucion;
import java.awt.Color;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class PlanificadorMid {
    
    private boolean[] marcos; //Marcos en memoria principal
    private int apuntador = 0;
    private ETP[] paginasEnMp; //Páginas en memoria principal
    private LinkedList procesos = new LinkedList(); //Global de los proceso
    private double tamMP; //Tamaño de la memoria principal
    private double tamMS; //Tamaño de la memoria secundaria
    private double tamMarco;
    static Semaphore accessMem = new Semaphore(1);
    
    public PlanificadorMid(int numMarcos, double tamMP, double tamMS, double tamMarco){
        this.marcos = new boolean[numMarcos];
        this.paginasEnMp = new ETP[numMarcos];
        for(int i=0;i<this.marcos.length;i++){
            this.paginasEnMp[i] = null; //Comienza vacio
            this.marcos[i] = true; //Inicializar como marcos vacios
        }
        this.tamMP = tamMP;
        this.tamMS = tamMS;
        this.tamMarco = tamMarco;
    }
    
        //Crear proceso e ingresarlo todo en memoria principal
        //De no poderse al menos ingresar su mitad
    public void crearProceso(String id, double tam) throws InterruptedException{
            //Crear el proceso
        Proceso p = new Proceso(id, tam, this.tamMarco);
            //Validar que es creable el proceso
            //Al menos la mitad de las páginas caben en mp
            //Queda espacio en memoria secundaria
        if((p.getCantidadPag()<(2*this.getNumMarcos())) && p.getTam()<=this.getRestanteMs()){ 
            int aux = this.getMarcosVacios(); //Cantidad de marcos vacíos
            if(aux != 0){
                int i=0; //Número de página
                int j=0; //Número de marco
                //Verificar si hay marcos suficientes para acomodar el proceso
                if(aux > p.getCantidadPag()){ //Hay más marcos que páginas del proceso
                        //Entra completamente en mp
                    while(i<p.getCantidadPag()){
                        if(this.marcos[j]){
                            this.marcos[j] = false; //Este marco está ocupado
                            UIEjecucion.celdas[j].setText(p.getId()+"/"+i); //Visual
                            UIEjecucion.celdas[j].setBackground(Color.red); //Llenar el espacio
                            p.setMarcoToETP(j, i); //Darle el número de marco a la ETP
                            p.getETP(i).setP(true); //Bit de presencia
                            p.getETP(i).u = false; //Bit de referencia para reloj
                            this.paginasEnMp[i] = p.getETP(i); //Nueva página en mp
                            i++;
                        }
                        j++;
                    }
                    p.setEstado(true, false, false); //Estado activo
                }else{
                    if((p.getCantidadPag()/aux) <= 2){ //Se puede acomodar al menos la mitad de páginas en memoria principal
                            //Entra parcialmente
                        while(i<aux){
                            if(this.marcos[j]){
                                this.marcos[j] = false; //Este marco está ocupado
                                UIEjecucion.celdas[j].setText(p.getId()+"/"+i); //Visual
                                UIEjecucion.celdas[j].setBackground(Color.red); //Llenar el espacio
                                p.setMarcoToETP(j, i); //Darle el número de marco a la ETP
                                p.getETP(i).setP(true); //Bit de presencia
                                p.getETP(i).u = false; //Bit de referencia para reloj
                                this.paginasEnMp[i] = p.getETP(i); //Nueva página en mp
                                i++;
                            }
                            j++;
                        }
                        p.setEstado(true, false, false);
                    }else{
                        //Ingreso apropiativo
                        this.ingresoApropiativo(p);
                        p.setEstado(true, false, false);
                    }
                }
            }else{
                //Ingreso apropiativo
                this.ingresoApropiativo(p);
                p.setEstado(true, false, false);
            }
            this.procesos.add(p);
            UIEjecucion.ocupado += p.getTam();
            UIEjecucion.labelIdProcesos.add(p.getId());
        }else{
            System.out.println("Add JOptinonPane de ms insuficiente o proceso muy grande");
        }
    }
    
        //Supender un proceso
    public void suspProceso(){
        
    }
    
        //Eliminar proceso del sistema
    public void elimProceso(){
        
    }
    
        //Sacar paginas de un proceso de memoria principal y memoria secundaria
    private void sacarProcesoMem(){
        
    }
    
        //Ingresar apropiandose de la memoria de otros procesos
    private void ingresoApropiativo(Proceso p){
        Object[] aux;
        aux = this.liberarMarcos(p.getCantidadPag()/2);
            //Se van a cargar la mitad de las páginas del proceso
        for(int i=0;i<aux.length;i++){
            p.getETP(i).setP(true); //Asignar bit de presencia
            p.getETP(i).u = true; //Asignar bit de referencia
            p.getETP(i).numMar = (int)aux[i]; //Asignar número de marco
            this.marcos[(int)aux[i]] = false; //Este marco está ocupado
            UIEjecucion.celdas[(int)aux[i]].setText(p.getId()+"/"+i); //Visual
            UIEjecucion.celdas[(int)aux[i]].setBackground(Color.red); //Llenar el espacio
        }
    }
    
        //Algoritmo del reloj que retorna el número de los marcos liberados
    private Object[] liberarMarcos(int n){
            //Recorrer el buffer//Se liberan n marcos
        Object[] aux;
        LinkedList marco = new LinkedList();
            //Antes de recorrer tomar los marcos vacios
        for(int i=0;i<this.marcos.length;i++){
            if(this.marcos[i]){
                marco.add(i);
            }
        }
        while(true){
                //Ya se tienen los n marcos
            if(n == marco.size()){
                aux = marco.toArray();
                return aux;
            }
            if(this.paginasEnMp[this.apuntador] != null){
                if(this.paginasEnMp[this.apuntador].u){
                    this.paginasEnMp[this.apuntador].u = false;
                }else{
                    System.out.println(n);
                    marco.add(this.paginasEnMp[this.apuntador].numMar); //Añadir número de marco
                    this.marcos[this.paginasEnMp[this.apuntador].numMar] = true; //Liberar marco
                    UIEjecucion.celdas[this.paginasEnMp[this.apuntador].numMar].setBackground(Color.green);
                    Proceso p = this.getProceso(this.paginasEnMp[this.apuntador].idProceso);
                    p.getETP(this.paginasEnMp[this.apuntador].numPag).setP(false); //Ya la página no se encuentra en mp
                    p.getETP(this.paginasEnMp[this.apuntador].numPag).u = false; //Por ende u = false
                    if(p.getPaginasEnMp() == 0){
                        p.setEstado(false, true, false); //Estado suspendido
                    }
                    this.paginasEnMp[this.apuntador] = null;
                }
            }
            this.apuntador = (this.apuntador+1)%this.marcos.length;
        }
    }
    
    public void leerPag(){
        
    }
    
        //Hay marcos vacíos y cuantos son
    private int getMarcosVacios(){
        boolean vacio = true;
        int cantidad = 0;
        for(int i=0;i<this.marcos.length;i++){
            if(this.marcos[i] == true){
                cantidad++;
            }
        }
        if(cantidad == 0){ //No hay marcos vacíos
            return 0;
        }else{
            return cantidad; //Hay al menos un marco vacío
        }
    }
    
    public double getRestanteMs(){
        double ocu = 0;
        Object[] aux = this.procesos.toArray();
        for(int i=0;i<aux.length;i++){
            ocu = ocu + ((Proceso)aux[i]).getTam();
        }
        ocu = this.tamMS-ocu;
        return ocu;
    }
    
    public int getNumProcesos(){
        return this.procesos.size();
    }
    
    public Proceso getProceso(String i){
        if(this.procesos.size()>0){
            Object[] aux = this.procesos.toArray();
            for(int j=0;j<aux.length;j++){
                if(i.equals(((Proceso)aux[j]).getId())){
                    return (Proceso)aux[j];
                }
            }
        }
        return null;
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
    
    public boolean getMarcoValue(int i){
        return this.marcos[i];
    }
    
}
