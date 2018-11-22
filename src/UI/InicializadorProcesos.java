package UI;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import logica.OS;
import logica.Proceso;

public class InicializadorProcesos extends javax.swing.JFrame {
    
    public InicializadorProcesos() {
        
        initComponents();
        this.setVisible(true);
        this.setLocationRelativeTo(null);
        
        double aux = OS.getTamMP();
        
            //Desplegar arreglo de memoria
        UIEjecucion.celdas = new JTextField[OS.getNumMarcos()];
        UIEjecucion.labelCeldas = new JLabel[OS.getNumMarcos()];
        UIEjecucion.disMpLabel = new JLabel();
        
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
        
        aux = OS.getTamMarco();
        if(aux/(1024*1024)>=1){
            aux = (aux/(1024*1024));
                //Está en Mbytes
            this.marcoLabel.setText("Tamaño del Marco: "+aux+" Mb");
        }else{
            aux = (aux/1024);
                //Está en Kbytes
            this.marcoLabel.setText("Tamaño del Marco: "+aux+" Kb");
        }
        
        aux = OS.getDisponibleMs();
        if(aux/(1024*1024*1024)>=1){
            aux = (aux/(1024*1024*1024));
                //Está en Gbytes
            this.memsLabel.setText("Memoria Secundaria: "+aux+" Gb");
        }else{
            aux = aux/(1024*1024);
                //Está en Mbytes
            this.memsLabel.setText("Memoria Secundaria: "+aux+" Mb");
        }
        
        if(OS.getDisponibleMs()/(1024*1024*1024)>1){
            this.disMsLabel.setText("Espacio disponible: "+OS.getDisponibleMs()/(1024*1024*1024)+" Gb");
        }else if(OS.getDisponibleMs()/(1024*1024) > 1){
            this.disMsLabel.setText("Espacio disponible: "+OS.getDisponibleMs()/(1024*1024)+" Mb");
        }else{
            this.disMsLabel.setText("Espacio disponible: "+OS.getDisponibleMs()/(1024)+" Kb");
        }
        
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        iniciar = new javax.swing.JButton();
        idField = new javax.swing.JTextField();
        idLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        tamField = new javax.swing.JTextField();
        mempLabel = new javax.swing.JLabel();
        marcoLabel = new javax.swing.JLabel();
        memsLabel = new javax.swing.JLabel();
        disMsLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        agregar = new javax.swing.JButton();
        unitProTam = new javax.swing.JComboBox<>();

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

        tamField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tamFieldKeyTyped(evt);
            }
        });
        getContentPane().add(tamField, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 70, 210, -1));

        mempLabel.setText("Memoria Principal:");
        getContentPane().add(mempLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 160, 330, 20));

        marcoLabel.setText("Tamaño de página:");
        getContentPane().add(marcoLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 330, 20));

        memsLabel.setText("Memoria Secundaria:");
        getContentPane().add(memsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 330, 20));

        disMsLabel.setText("Mem. Secundaria ocupada: ");
        getContentPane().add(disMsLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, 330, 20));

        jLabel7.setText("Agregar Procesos para comenzar la ejecución");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 10, 260, 20));

        agregar.setText("Agregar");
        agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarActionPerformed(evt);
            }
        });
        getContentPane().add(agregar, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, -1, -1));

        unitProTam.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Bytes", "Kb", "Mb" }));
        getContentPane().add(unitProTam, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 70, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void agregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarActionPerformed
            //Validar campos llenos
        if((this.idField.getText().length() > 0) && (this.tamField.getText().length() > 0)){
            if(OS.getProceso(this.idField.getText()) == null){
                double aux = Integer.parseInt(this.tamField.getText());
                if(aux <= 0){
                    JOptionPane.showMessageDialog(this, "Tamaño negativo", "ERROR AL AÑADIR PROCESO", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String ingresar = this.idField.getText();
                try {
                    
                    String aux2 = this.unitProTam.getItemAt(this.unitProTam.getSelectedIndex());
                        
                    if(aux2.equals("Mb")){
                        aux = (aux*1024*1024); //Pasar de Mb a bytes
                    }else if(aux2.equals("Kb")){
                        aux = (aux*1024); //Pasar de Kb a bytes
                    }
                    
                    Proceso p = new Proceso(ingresar, aux, OS.getTamMarco());
                    
                    if((p.getCantidadPag()<(2*OS.getNumMarcos())) && p.getTam() <= OS.getDisponibleMs()){ 
                        OS.crearProceso(p);
                        if(OS.getDisponibleMs()/(1024*1024*1024)>1){
                            this.disMsLabel.setText("Espacio disponible: "+OS.getDisponibleMs()/(1024*1024*1024)+" Gb");
                        }else if(OS.getDisponibleMs()/(1024*1024) > 1){
                            this.disMsLabel.setText("Espacio disponible: "+OS.getDisponibleMs()/(1024*1024)+" Mb");
                        }else{
                            this.disMsLabel.setText("Espacio disponible: "+OS.getDisponibleMs()/(1024)+" Kb");
                        }
                        this.idField.setText("");
                        this.tamField.setText("");
                    }else{
                        if(p.getTam() >= OS.getDisponibleMs()){
                            JOptionPane.showMessageDialog(this, "Memoria secundaria no disponible para la petición", "ERROR AL AÑADIR PROCESO", JOptionPane.ERROR_MESSAGE);
                        }else{
                            JOptionPane.showMessageDialog(this, "Memoria principal insuficiente para la petición", "ERROR AL AÑADIR PROCESO", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(UIEjecucion.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                JOptionPane.showMessageDialog(this, "Ya hay un proceso por esta ID", "ERROR AL AÑADIR PROCESO", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(this, "Campor inválidos", "ERROR AL AÑADIR PROCESO", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_agregarActionPerformed

    private void iniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iniciarActionPerformed
        try {
            UIEjecucion exe = new UIEjecucion();
            this.setVisible(false);
        } catch (InterruptedException ex) {
            Logger.getLogger(InicializadorProcesos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_iniciarActionPerformed

    private void tamFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tamFieldKeyTyped
        char aux = evt.getKeyChar();
        if(Character.isLetter(aux)){
            evt.consume();
            JOptionPane.showMessageDialog(this, "El campo solo acepta números", "ERROR DE INPUT", JOptionPane.WARNING_MESSAGE);
            return;
        }
    }//GEN-LAST:event_tamFieldKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton agregar;
    private javax.swing.JLabel disMsLabel;
    private javax.swing.JTextField idField;
    private javax.swing.JLabel idLabel;
    private javax.swing.JButton iniciar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel marcoLabel;
    private javax.swing.JLabel mempLabel;
    private javax.swing.JLabel memsLabel;
    private javax.swing.JTextField tamField;
    private javax.swing.JComboBox<String> unitProTam;
    // End of variables declaration//GEN-END:variables
}
