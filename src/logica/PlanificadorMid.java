package logica;

import UI.UIEjecucion;
import java.awt.Color;
import java.util.LinkedList;

public class PlanificadorMid { //Planificador de memoria
    
    protected static int apuntador;
    
    public PlanificadorMid(){}
    
        //Crear proceso e ingresarlo todo en memoria principal
        //De no poderse expulsar otro proceso.
    public static void cargarProceso(Proceso p) throws InterruptedException{
            //Al menos la mitad de las páginas caben en mp
            //Queda espacio en memoria secundaria
        int aux = PlanificadorMid.getMarcosVacios(); //Cantidad de marcos vacíos
        if(aux != 0){
            int i=0; //Número de página
            int j=0; //Número de marco
            //Verificar si hay marcos suficientes para acomodar el proceso
            if(aux > p.getCantidadPag()){ //Hay más marcos que páginas del proceso
                    //Entra completamente en mp
                while(i<p.getCantidadPag()){
                    if(OS.marcos[j].getLibre()){
                        UIEjecucion.celdas[j].setText(p.getIdP()+"/"+p.getETP(i).numPag); //Visual
                        UIEjecucion.celdas[j].setBackground(Color.red); //Llenar el espacio
                        p.setMarcoToETP(j, i); //Darle el número de marco a la ETP
                        p.getETP(i).setP(true); //Bit de presencia
                        p.getETP(i).u = true; //Bit de referencia para reloj
                        OS.marcos[j].setLibre(false); //Este marco está ocupado
                        OS.marcos[j].setPagina(p.getETP(i)); //Nueva página en el marco
                        i++;
                    }
                    j++;
                }
            }else{
                    //Ingreso apropiativo
                PlanificadorMid.ingresoApropiativo(p);
            }
        }else{
            //Ingreso apropiativo
            PlanificadorMid.ingresoApropiativo(p);
        }
        p.setEstado(true, false, false); //Estado listo
    }
    
        //Supender un proceso
    protected void suspProceso(){
        
    }
    
        //Sacar paginas de un proceso de memoria principal y memoria secundaria
    protected static void sacarProcesoMem(Proceso p){
        for(int i=0;i<OS.marcos.length;i++){
            if(OS.marcos[i].getPagina() != null){
                if(OS.marcos[i].getPagina().idProceso.equals(p.getIdP())){
                    UIEjecucion.celdas[p.getETP(OS.marcos[i].getPagina().numPag).numMar].setBackground(Color.green);
                    UIEjecucion.celdas[p.getETP(OS.marcos[i].getPagina().numPag).numMar].setText("");
                    p.getETP(OS.marcos[i].getPagina().numPag).setP(false); //Bit de presencia = 0
                    p.getETP(OS.marcos[i].getPagina().numPag).u = false; //Bit de referencia 
                    p.setMarcoToETP(-1, OS.marcos[i].getPagina().numPag); //Quitarle marco a la página
                    OS.marcos[i].setLibre(true); //Libera el marco
                    OS.marcos[i].setPagina(null); //Sacar de mp la página
                }
            }
        }
    }
    
        //Ingresar apropiandose de la memoria de otros procesos
        //2 casos
        //1: Si hay más memoria que la mitad de sus paginas se cargan cuantas se puedan
        //2: Si no se cumple el (1) se apropia de marcos de otros páginas de otros procesos hasta cargar su mitad
    private static void ingresoApropiativo(Proceso p){
        Object[] aux;
        aux = PlanificadorMid.liberarMarcos(p.getCantidadPag());
            //Se van a cargar las páginas del proceso para ejecutarlo
        for(int i=0;i<aux.length;i++){
            p.getETP(i).setP(true); //Asignar bit de presencia
            p.getETP(i).u = true; //Asignar bit de referencia
            p.setMarcoToETP((int)aux[i], i); //Asignar número de marco
            OS.marcos[(int)aux[i]].setLibre(false); //Este marco ahora está ocupado
            UIEjecucion.celdas[(int)aux[i]].setText(p.getIdP()+"/"+p.getETP(i).numPag); //Visual
            UIEjecucion.celdas[(int)aux[i]].setBackground(Color.red); //Llenar el espacio
            OS.marcos[(int)aux[i]].setPagina(p.getETP(i)); //Nueva página en mp
        }
    }
    
        //Algoritmo del reloj que retorna el número de los marcos liberados
    private static Object[] liberarMarcos(int n){
            //Recorrer el buffer//Se liberan n marcos
        if(n > OS.getNumMarcos()){ //El proceso tiene más páginas que marcos en memoria
            n = n/2;
        }
        Object[] aux;
        LinkedList marco = new LinkedList();
            //Antes de recorrer tomar los marcos vacios
        for(int i=0;i<OS.marcos.length;i++){
            if(OS.marcos[i].getLibre()){
                marco.add(i);
            }
        }
        while(true){
                //Ya se tienen los n marcos
            if(marco.size() >= n){
                aux = marco.toArray();
                return aux;
            }
            if(!OS.marcos[PlanificadorMid.apuntador].getLibre()){
                if(OS.marcos[PlanificadorMid.apuntador].getPagina().u){
                    OS.marcos[PlanificadorMid.apuntador].getPagina().u = false;
                }else{
                    marco.add(PlanificadorMid.apuntador); //Añadir número del marco
                    Proceso p = OS.getProceso(OS.marcos[PlanificadorMid.apuntador].getPagina().idProceso); //Proceso cuya página está en el marco
                    UIEjecucion.celdas[PlanificadorMid.apuntador].setBackground(Color.green); //Visual
                    p.getETP(OS.marcos[PlanificadorMid.apuntador].getPagina().numPag).setP(false); //Ya la página no se encuentra en mp
                    p.setMarcoToETP(-1, OS.marcos[PlanificadorMid.apuntador].getPagina().numPag); //Desasignar marco
                    OS.marcos[PlanificadorMid.apuntador].setLibre(true); //Liberar marco en mp
                    /*if(p.getPaginasEnMp() == 0){
                        p.setEstado(false, true, false); //Estado suspendido
                    }*/
                    OS.actualizarProceso(p);
                    OS.marcos[PlanificadorMid.apuntador].setPagina(null);
                }
            }
            PlanificadorMid.apuntador = (PlanificadorMid.apuntador+1)%OS.marcos.length;
        }
    }
    
        //Ingresar n páginas de un proceso
    public static void escribirPag(){
        
    }
    
        //Hay marcos vacíos y cuantos son
    private static int getMarcosVacios(){
        int cantidad = 0;
        for(int i=0;i<OS.marcos.length;i++){
            if(OS.marcos[i].getLibre() == true){
                cantidad++;
            }
        }
        if(cantidad == 0){ //No hay marcos vacíos
            return 0;
        }else{
            return cantidad; //Hay al menos un marco vacío
        }
    }

}
