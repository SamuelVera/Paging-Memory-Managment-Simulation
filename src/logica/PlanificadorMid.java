package logica;

import UI.UIEjecucion;
import java.awt.Color;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class PlanificadorMid { //Planificador de memoria
    
    protected static int apuntador;
    protected static Semaphore accesoMem = new Semaphore(1);
    
        //Crear proceso e ingresarlo todo si hay espacio
        //De no haberlo estará suspendido.
    public static void cargarProceso(Proceso p) throws InterruptedException{
        PlanificadorMid.accesoMem.acquire();
        int aux = OS.getMarcosVacios(); //Cantidad de marcos vacíos
            //Verificar si hay marcos suficientes para acomodar el proceso
        if(aux > p.getCantidadPag()/2){ //Hay más marcos que la mitad de páginas del proceso
                //Entra completamente en mp
            int i=0; //Número de página
            int j=0; //Número de marco
            while(i<(p.getCantidadPag())/2){
                if(OS.marcos[j].getLibre()){
                    UIEjecucion.celdas[j].setText(p.getIdP()+"/"+p.getETP(i).numPag); //Visual
                    UIEjecucion.celdas[j].setBackground(Color.red); //Visual
                    p.setMarcoToETP(j, i); //Darle el número de marco a la ETP
                    p.getETP(i).setP(true); //Bit de presencia
                    p.getETP(i).u = true; //Bit de referencia para reloj
                    OS.marcos[j].setLibre(false); //Este marco está ocupado
                    OS.marcos[j].setPagina(p.getETP(i)); //Nueva página en el marco
                    i++;
                }
                j++;
            }
        }
        UIEjecucion.disMp = OS.getDisponibleMp();
        if(UIEjecucion.disMp/(1024*1024) > 1){
            UIEjecucion.disMpLabel.setText("Espacio disponible: "+UIEjecucion.disMp/(1024*1024)+" Mb");
        }else{
            UIEjecucion.disMpLabel.setText("Espacio disponible: "+UIEjecucion.disMp/(1024)+" Kb");
        }
        PlanificadorMid.accesoMem.release();
    }
    
        //Sacar paginas de un proceso de memoria principal
    protected static void sacarProcesoMem(Proceso p) throws InterruptedException{
        PlanificadorMid.accesoMem.acquire();
        for(int i=0;i<OS.marcos.length;i++){
            if(OS.marcos[i].getPagina() != null){
                if(OS.marcos[i].getPagina().idProceso.equals(p.getIdP())){
                    UIEjecucion.celdas[i].setBackground(Color.green); //Visual
                    UIEjecucion.celdas[i].setText(""); //Visual
                    p.getETP(OS.marcos[i].getPagina().numPag).setP(false); //Bit de presencia = 0
                    p.getETP(OS.marcos[i].getPagina().numPag).u = false; //Bit de referencia 
                    p.setMarcoToETP(-1, OS.marcos[i].getPagina().numPag); //Quitarle marco a la página
                    OS.marcos[i].setLibre(true); //Libera el marco
                    OS.marcos[i].setPagina(null); //Sacar la página de mp
                }
            }
        }
        UIEjecucion.disMp = OS.getDisponibleMp();
        if(UIEjecucion.disMp/(1024*1024) > 1){
            UIEjecucion.disMpLabel.setText("Espacio disponible: "+UIEjecucion.disMp/(1024*1024)+" Mb");
        }else{
            UIEjecucion.disMpLabel.setText("Espacio disponible: "+UIEjecucion.disMp/(1024)+" Kb");
        }
        PlanificadorMid.accesoMem.release();
    }
    
        //Liberar marcos
    private static Object[] liberarMarcos(int n){
        if(n > OS.getNumMarcos()){ //Se están solicitando más páginas que marcos en memoria principal
            n = (n/2)+1; //Se reduce la solicitud a la mitad
        }
        Object[] aux;
        LinkedList marco = new LinkedList();
            //Primero se extraen los número de aquellos marcos libres
        for(int i=0;i<OS.marcos.length;i++){
            if(OS.marcos[i].getLibre()){
                marco.add(i);
                if(marco.size() >= n){
                    //Si hay suficientes marcos libres como para satisfacer
                    //la solicitud se detiene la búsqueda
                    aux = marco.toArray();
                    return aux;
                }
            }
        }
        return PlanificadorMid.algoritmoReemplazo(n, marco);
    }
    
        //Algoritmo de reloj, retorna los números de los "n" marcos cuyas páginas
        //son víctimas de reemplazo
    private static Object[] algoritmoReemplazo(int n, LinkedList marco){
        Object[] aux;
        while(true){
                //No considerar los marcos libres pues no tienen páginas
            if(!OS.marcos[PlanificadorMid.apuntador].getLibre()){
                if(OS.marcos[PlanificadorMid.apuntador].getPagina().u){
                        //Si la página tiene su bit de referencia en 1 lo setea a 0
                    OS.marcos[PlanificadorMid.apuntador].getPagina().u = false;
                }else{ //La página tiene su bit de referencia en 0
                        //Añadir el número del marco de dicha página
                    marco.add(PlanificadorMid.apuntador);
                    Proceso p = OS.getProceso(OS.marcos[PlanificadorMid.apuntador].getPagina().idProceso);
                        //Modificar el bit de presencia de la página a sacar
                    p.getETP(OS.marcos[PlanificadorMid.apuntador].getPagina().numPag).setP(false);
                        //Modificar bit de referencia de la página a sacar (Respaldo)
                    p.getETP(OS.marcos[PlanificadorMid.apuntador].getPagina().numPag).u = false;
                        //Desasignarle el marco a la página que va a ser sacada y volverlo -1
                    p.setMarcoToETP(-1, OS.marcos[PlanificadorMid.apuntador].getPagina().numPag);
                        //Liberar el marco de la mp
                    OS.marcos[PlanificadorMid.apuntador].setLibre(true);
                        //Eliminar referencia en mp de la página
                    OS.marcos[PlanificadorMid.apuntador].setPagina(null);
                        //Si el proceso cuya página es víctima se quedó sin páginas en mp se suspende
                    if(p.getPaginasEnMp() == 0){
                        p.setEstado(false, false, true, false);
                    }
                    UIEjecucion.celdas[PlanificadorMid.apuntador].setBackground(Color.green); //Visual
                    UIEjecucion.celdas[PlanificadorMid.apuntador].setText(""); //Visual
                }
            }
                //Mover el apuntador
            PlanificadorMid.apuntador = (PlanificadorMid.apuntador+1)%OS.marcos.length;
            if(marco.size() >= n){
                aux = marco.toArray();
                return aux;
            }
        }
    }
    
        //Escribir páginas de un proceso en memoria
    protected static void escribirPag(Proceso p, int[] pags) throws InterruptedException{
        PlanificadorMid.accesoMem.acquire();
        Object[] aux;
        aux = PlanificadorMid.liberarMarcos(pags.length);
        for(int i=0;i<aux.length;i++){
            p.getETP(pags[i]).setP(true); //Asignar bit de presencia
            p.getETP(pags[i]).u = true; //Asignar bit de referencia
            System.out.println((int)aux[i]);
            p.setMarcoToETP((int)aux[i], pags[i]); //Asignar número de marco
            OS.marcos[(int)aux[i]].setLibre(false); //Este marco ahora está ocupado
            OS.marcos[(int)aux[i]].setPagina(p.getETP(pags[i])); //Nueva página en mp
            UIEjecucion.celdas[(int)aux[i]].setText(p.getIdP()+"/"+p.getETP(pags[i]).numPag); //Visual
            UIEjecucion.celdas[(int)aux[i]].setBackground(Color.red); //Visual
        }
        UIEjecucion.disMp = OS.getDisponibleMp();
        if(UIEjecucion.disMp/(1024*1024) > 1){
            UIEjecucion.disMpLabel.setText("Espacio disponible: "+UIEjecucion.disMp/(1024*1024)+" Mb");
        }else{
            UIEjecucion.disMpLabel.setText("Espacio disponible: "+UIEjecucion.disMp/(1024)+" Kb");
        }
        PlanificadorMid.accesoMem.release();
    }
    
}
