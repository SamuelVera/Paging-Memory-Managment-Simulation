package logica;

public class Proceso{
    
    Pagina[] paginas; //Tabla de páginas
    String id; //ID del proceso
        //0: Activo/Parcialmente en memoria principal
        //1: Suspendido/Completamente fuera de memoria principal
    boolean[] estado = {false,false};
    int tam; //Tamaño del proceso
    
        //Inicialización del proceso con su cantidad de páginas, id, 
    Proceso(String id, int tam, int tamPag){
        int canPag = tam/tamPag; //Cantidad de páginas
        if(tam % tamPag != 0){ //Fragmentación interna en la última página
            canPag++;
        }
        this.paginas = new Pagina[canPag];
        this.id = id;
        this.tam = tam;
    }
    
    
    
}
