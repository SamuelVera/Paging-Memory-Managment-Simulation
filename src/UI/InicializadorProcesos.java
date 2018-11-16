package UI;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTextField;
import logica.OS;

public class InicializadorProcesos extends javax.swing.JFrame {
    
    public InicializadorProcesos(OS os) {
        UIEjecucion.OS = os;
        initComponents();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        double aux = UIEjecucion.OS.getTamMP();
        
        //Desplegar arreglo de memoria
        UIEjecucion.celdas = new JTextField[UIEjecucion.OS.getNumMarcos()];
        UIEjecucion.labelCeldas = new JLabel[UIEjecucion.OS.getNumMarcos()];
        
            //Inicializar arreglo visual
        for(int i=0;i<UIEjecucion.celdas.length;i++){
            UIEjecucion.celdas[i] = new JTextField();
            UIEjecucion.celdas[i].setFocusable(false);
            UIEjecucion.celdas[i].setBackground(Color.green);
            UIEjecucion.labelCeldas[i] = new JLabel();
            UIEjecucion.labelCeldas[i].setText(""+i);
        }
        
        if(aux/(1024*1024)>=1){
            aux = (aux/(1024*1024));
                //Está en Mbytes
            this.mempLabel.setText("Memoria Principal: "+aux+" Mb");
        }else{
            aux = (aux/1024);
                //Está en Kbytes
            this.mempLabel.setText("Memoria Principal: "+aux+" Kb");
        }
        
        aux = UIEjecucion.OS.getTamMarco();
        if(aux/(1024*1024)>=1){
            aux = (aux/(1024*1024));
                //Está en Mbytes
            this.marcoLabel.setText("Tamaño del Marco: "+aux+" Mb");
        }else{
            aux = (aux/1024);
                //Está en Kbytes
            this.marcoLabel.setText("Tamaño del Marco: "+aux+" Kb");
        }
        
        aux = UIEjecucion.OS.getTamMS();
        if(aux/(1024*1024*1024)>=1){
            aux = (aux/(1024*1024*1024));
                //Está en Gbytes
            this.memsLabel.setText("Memoria Secundaria: "+aux+" Gb");
        }else{
            aux = aux/(1024*1024);
                //Está en Mbytes
            this.memsLabel.setText("Memoria Secundaria: "+aux+" Mb");
        }
        
        this.ocuLabel.setText("Espacio ocupado: "+(UIEjecucion.ocupado)+" Mb");
        
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        iniciar = new javax.swing.JButton();
        idField = new javax.swing.JTextField();
        idLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        tamField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        mempLabel = new javax.swing.JLabel();
        marcoLabel = new javax.swing.JLabel();
        memsLabel = new javax.swing.JLabel();
        ocuLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        agregar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        iniciar.setText("Ir a UI de ejecución");
        iniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iniciarActionPerformed(evt);
            }
        });
        getContentPane().add(iniciar, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 260, -1, -1));
        getContentPane().add(idField, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 40, 210, -1));

        idLabel.setText("ID:");
        getContentPane().add(idLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 70, 20));

        jLabel1.setText("Tamaño:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, 70, 20));
        getContentPane().add(tamField, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 70, 210, -1));

        jLabel2.setText("Kb");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 70, 60, 20));

        mempLabel.setText("Memoria Principal:");
        getContentPane().add(mempLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 330, 20));

        marcoLabel.setText("Tamaño de página:");
        getContentPane().add(marcoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 330, 20));

        memsLabel.setText("Memoria Secundaria:");
        getContentPane().add(memsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 330, 20));

        ocuLabel.setText("Mem. Secundaria ocupada: ");
        getContentPane().add(ocuLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, 330, 20));

        jLabel7.setText("Agregar Procesos para comenzar la ejecución");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 260, 20));

        agregar.setText("Agregar");
        agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarActionPerformed(evt);
            }
        });
        getContentPane().add(agregar, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void agregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarActionPerformed
            //Validar campos llenos
        if((this.idField.getText().length()>0)&&(this.tamField.getText().length()>0)){
            if(UIEjecucion.OS.getProceso(this.idField.getText()) == null){
                    //Parte entera de la división
                double aux = Integer.parseInt(this.tamField.getText());
                String ingresar = this.idField.getText();
                try {
                    UIEjecucion.OS.crearProceso(ingresar, aux);
                    this.idField.setText("");
                    this.tamField.setText("");
                    this.ocuLabel.setText("Espacio ocupado: "+(UIEjecucion.ocupado/(1024*1024))+" Mb");
                } catch (InterruptedException ex) {
                    Logger.getLogger(UIEjecucion.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                System.out.println("Add JOptionPane para id repetido");
            }
        }else{
            System.out.println("Add JOptionPane para Campos inválidos");
        }
    }//GEN-LAST:event_agregarActionPerformed

    private void iniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iniciarActionPerformed
        UIEjecucion exe = new UIEjecucion();
        this.setVisible(false);
    }//GEN-LAST:event_iniciarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton agregar;
    private javax.swing.JTextField idField;
    private javax.swing.JLabel idLabel;
    private javax.swing.JButton iniciar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel marcoLabel;
    private javax.swing.JLabel mempLabel;
    private javax.swing.JLabel memsLabel;
    private javax.swing.JLabel ocuLabel;
    private javax.swing.JTextField tamField;
    // End of variables declaration//GEN-END:variables
}
