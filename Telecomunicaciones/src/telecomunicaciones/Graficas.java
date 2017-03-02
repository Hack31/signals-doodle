package telecomunicaciones;

import java.awt.BorderLayout;
import java.text.DecimalFormat;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.category.DefaultCategoryDataset;

public final class Graficas extends javax.swing.JFrame {
    private String TipoDeModulacion, TpSeñalPortadora, TpSeñalModuladora;
    private String FrecuenciaModuladora, FrecuenciaPortadora, MedidaEspectros;
    private double vc, vm, m, t;
    private int tiempo;
    private double fm, fc;

    public Graficas() {
        initComponents();
        this.setLocationRelativeTo(null);//Inicializa la ventana para que este centrada
        InicializarVariables();
        InitSpinnerDecimal(TextIndiceDeModulacion, 3.00, 0.00, 100, 0.1);
    }

    public void GraficarSeñales() {
        final XYSeries Moduladora, Portadora, Modulada;
        Moduladora = new XYSeries("Moduladora");
        Portadora = new XYSeries("Portadora");
        Modulada = new XYSeries("Modulada"); //XY chart
        
        ModulacionPM modularPM = new ModulacionPM(fm, fc, vc, vm, m);
        ModulacionFM modularFM = new ModulacionFM(fm, fc, vc, vm, m);

        modularFM.setTipoSeñal(TpSeñalModuladora);
        modularPM.setTipoSeñalModuladora(TpSeñalModuladora);
        modularPM.setTipoSeñalPortadora(TpSeñalPortadora);

        Formulas formula = new Formulas(fm, vm, m, m);
        modularFM.setSensibilidad(formula.kFM());

        for (int j = 1; j <= (tiempo * 100); j++) { //al aumentar el pow de abajo agregarle un 0 mas al 100 (condicionar parada)
            t = j / Math.pow(10, 5); //Aumenta los puntos a Evaluar (Renderizado) :v

            if (((double) tiempo / Math.pow(10, 3)) != t) { //tiempo en Millisegundos

                if (TipoDeModulacion.equals("FM")) {
                    Moduladora.add(t, modularFM.SeñalModuladora(t, TpSeñalModuladora));
                    Portadora.add(t, modularFM.SeñalPortadora(t, TpSeñalPortadora));
                    Modulada.add(t, modularFM.ModularFm(t));
                    //j+=1869;
                } else {
                    Moduladora.add(t, modularPM.SeñalModuladora(t, TpSeñalModuladora));
                    Portadora.add(t, modularPM.SeñalPortadora(t, TpSeñalPortadora));
                    Modulada.add(t, modularPM.ModularPM(t));
                    //j+=1869;
                }
            } else {

                if (TipoDeModulacion.equals("FM")) {
                    Moduladora.add(t, modularFM.SeñalModuladora(t, TpSeñalModuladora));
                    Portadora.add(t, modularFM.SeñalPortadora(t, TpSeñalPortadora));
                    Modulada.add(t, modularFM.ModularFm(t));
                     //j+=1869;
                } else {
                    Moduladora.add(t, modularPM.SeñalModuladora(t, TpSeñalModuladora));
                    Portadora.add(t, modularPM.SeñalPortadora(t, TpSeñalPortadora));
                    Modulada.add(t, modularPM.ModularPM(t));
                    //j+=1869;
                }
                break;
            }
        }
        CargarChart(Moduladora, PanelGraficaModuladora, "Señal Moduladora");
        CargarChart(Portadora, PanelGraficaPortadora, "Señal Portadora");

        if (CheckModuladoraModulante.isSelected()) { //integra el¨Panel de la señal moduladora
            CargarChartDoble(Moduladora, Modulada, PanelGraficaModulada, "Señal Modulada + Señal Moduladora");
        } else {
            CargarChart(Modulada, PanelGraficaModulada, "Señal Modulada");
        }
        CargarChart(Modulada, PanelDemodularModulada, "Señal Modulada");
        CargarChart(Moduladora, PanelDemodularModuladora, "Señal Demodulada");
        ArrojarResultados();
    }
    
    public void CargarChart(XYSeries series, JPanel panel, String Mensaje) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        CargarPanel(dataset, panel, Mensaje);
    }
    
    public void CargarChartDoble(XYSeries serieA, XYSeries serieB, JPanel panel, String Mensaje) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(serieA);
        dataset.addSeries(serieB);
        CargarPanel(dataset, panel, Mensaje);
    }
    
    public void CargarPanel(XYSeriesCollection dataset, JPanel panel, String Mensaje) {
        final JFreeChart chartSeries = ChartFactory.createXYLineChart(Mensaje, "Tiempo (X)", "Amplitud (Y)",
                dataset, PlotOrientation.VERTICAL, true, false, false);
        ChartPanel grafica = new ChartPanel(chartSeries, false, false, false, false, false);
        panel.removeAll();
        panel.setLayout(new java.awt.BorderLayout());
        panel.add(grafica);
        panel.validate();
    }
 
    public void InicializarVariables() {
        TextFrecuenciaPortadora.setValue(600); //Inicializamos valores 
        TextFrecuenciaModulante.setValue(100);
        TextAmplitudPortadora.setValue(10);
        TextIndiceDeModulacion.setValue(3.0);
        TextAmplitudModulante.setValue(10);
        TxtTiempo.setValue(30);
        ComboMedidaModuladora.setSelectedItem("Hz");
        ComboMedidaPortadora.setSelectedItem("Hz");
    }
    public void InitSpinnerDecimal(JSpinner spinner, double value, double min, double max, double aumento){
        spinner.setModel(new SpinnerNumberModel(value,min, max, aumento));
        JSpinner.NumberEditor editor  = (JSpinner.NumberEditor)TextIndiceDeModulacion.getEditor();
        DecimalFormat format = editor.getFormat();
        format.setMinimumFractionDigits(2);
    }
    
    public void actualizar() {
        fc = Integer.parseInt(TextFrecuenciaPortadora.getValue().toString());  //Capturamos los datos Ingresados por el Usuario
        fm = Integer.parseInt(TextFrecuenciaModulante.getValue().toString());
        vc = Double.parseDouble(TextAmplitudPortadora.getValue().toString());
        m = Double.parseDouble(TextIndiceDeModulacion.getValue().toString());
        vm = Double.parseDouble(TextAmplitudModulante.getValue().toString());//variable auxiliar para la amplitud modulante
        TpSeñalModuladora = (String) ComboTpseñalModuladora.getSelectedItem(); //Extraemos el tipo de señal que queremos modular
        TpSeñalPortadora = (String) ComboTpseñalPortadora.getSelectedItem();
        TipoDeModulacion = ModulacionDeTipo.getSelectedItem().toString();
        tiempo = Integer.parseInt(TxtTiempo.getValue().toString()); //Tiempo de simulacion en Milli segundos
        FrecuenciaModuladora = ComboMedidaModuladora.getSelectedItem().toString();
        FrecuenciaPortadora = ComboMedidaPortadora.getSelectedItem().toString();
        fm = CalcularFrecuencia(FrecuenciaModuladora, fm);
        fc = CalcularFrecuencia(FrecuenciaPortadora, fc);
        
        if (tiempo < 1) {
            TxtTiempo.setValue(1);
        }
        if (TipoDeModulacion.equals("PM")) { //Verificamos que tipo de señal quiere modular el usuario  
            Labelm.setText("Desviacion Max. de Fase (m):");
        } else {
            Labelm.setText("Indice de Modulacion (m):");
        }
        PanelDemodularModuladora.setVisible(false);
        BtnDemodular.setVisible(true);//cada vez que se actualiza el boton se coloca visible
    }

    public void ArrojarResultados() {
        final double[] espectros;
        final double[] frecuencias;
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset(); //Barchart
        Formulas formula = new Formulas(fm, vm, m, m);
        FuncionBessel bessel = new FuncionBessel(m, vc);
        int nbandas = bessel.ConjuntoBandasLateralesSignificativas();
        espectros = new double[(nbandas * 2) + 1];   //Arreglo que contendran los espectros
        frecuencias = new double[(nbandas * 2) + 1]; //Arreglo que contendran las frecuencias
        double[] amplitudes = bessel.amplitudes();

        TextPaneResultados.setText("Conjunto de Frecuencias laterales : " + String.valueOf(nbandas) + '\n');//Mostrando panel Resultados
        TextPanelConjuntos.setText("Por tabla de Bessel para un indice de modulacion (m) = " + m + ", se producen n = " + nbandas
                + " conjuntos de frecuencias laterales significativas");
        TextPaneResultados.setText(TextPaneResultados.getText() + "Amplitudes de los espectros\n");

        for (int i = 0; i <= nbandas; i++) {
            TextPaneResultados.setText(TextPaneResultados.getText() + "\tj" + i + " = " + String.valueOf(Math.rint(amplitudes[i] * 100) / 100) + "(v)\n");//mostrando en el panel de resultados

            espectros[i] = (Math.rint(amplitudes[nbandas - i] * 100) / 100);
            espectros[(nbandas * 2) - i] = espectros[i];
            if (i == nbandas) {
                frecuencias[i] = AjustarEspectros(fc);              
            } else {
                frecuencias[i] = AjustarEspectros(fc - ((nbandas - i) * fm));
                frecuencias[(nbandas * 2) - i] = AjustarEspectros(fc + ((nbandas - i) * fm));
            }
        }

        for (int i = 0; i < espectros.length; i++) {
            dataset.addValue(espectros[i], "espectros", String.valueOf(frecuencias[i]));
        }

        TextPaneResultados.setText(TextPaneResultados.getText() + "Ancho de banda Bessel = " + bessel.anchoBandaBessel(fm) + "\n");
        TextPaneResultados.setText(TextPaneResultados.getText() + "Ancho de banda Carson = " + bessel.anchoBandaCarson(formula.DesviacionFrecuencia(), fm) + "\n");

        if (TipoDeModulacion.equals("FM")) {
            TextPaneResultados.setText(TextPaneResultados.getText() + "Desviacion delta F = " + formula.DesviacionFrecuencia() + "\n");
            TextPaneResultados.setText(TextPaneResultados.getText() + "Sensibilidad Kl = " + formula.kFM() + "\n");
        } else {
            TextPaneResultados.setText(TextPaneResultados.getText() + "Desviacion de Fase = " + formula.mPM() + "\n");
            TextPaneResultados.setText(TextPaneResultados.getText() + "Sensibilidad K = " + formula.kFM() + "\n");  //acomodar esta malo 
        }
        TextPaneDatosSeñal.setText("DATOS DE LAS SEÑALES\n");
        TextPaneDatosSeñal.setText(TextPaneDatosSeñal.getText() + " vm = " + vm + " (v)\n");
        TextPaneDatosSeñal.setText(TextPaneDatosSeñal.getText() + " fm = " + fm + " Hz\n");
        TextPaneDatosSeñal.setText(TextPaneDatosSeñal.getText() + " vc = " + vc + " (v)\n");
        TextPaneDatosSeñal.setText(TextPaneDatosSeñal.getText() + " fc = " + fc + " Hz\n");
        TextPaneDatosSeñal.setText(TextPaneDatosSeñal.getText() + " m = " + m + " (adimensional)\n");

        JFreeChart barChart = ChartFactory.createBarChart("", "Frecuencia "+MedidaEspectros, "Voltaje (v)",
                dataset, PlotOrientation.VERTICAL, true, false, false);
        ChartPanel panel = new ChartPanel(barChart); //barchart
        PanelEspectro.removeAll(); //barchart
        PanelEspectro.setLayout(new java.awt.BorderLayout()); //barchart
        PanelEspectro.add(panel);
        PanelEspectro.validate();
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelLateral = new javax.swing.JPanel();
        TabbedModular = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        Panel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        ComboTpseñalModuladora = new javax.swing.JComboBox<>();
        TextFrecuenciaModulante = new javax.swing.JSpinner();
        TextAmplitudModulante = new javax.swing.JSpinner();
        jComboBox4 = new javax.swing.JComboBox<>();
        ComboMedidaModuladora = new javax.swing.JComboBox<>();
        Panel2 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        ComboTpseñalPortadora = new javax.swing.JComboBox<>();
        TextAmplitudPortadora = new javax.swing.JSpinner();
        TextFrecuenciaPortadora = new javax.swing.JSpinner();
        ComboMedidaPortadora = new javax.swing.JComboBox<>();
        jComboBox3 = new javax.swing.JComboBox<>();
        Panel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        Labelm = new javax.swing.JLabel();
        TextIndiceDeModulacion = new javax.swing.JSpinner();
        CheckModuladoraModulante = new javax.swing.JCheckBox();
        Panel4 = new javax.swing.JPanel();
        BtnModular = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        TxtTiempo = new javax.swing.JSpinner();
        ModulacionDeTipo = new javax.swing.JComboBox<>();
        PanelGraficaModuladora = new javax.swing.JPanel();
        LabelModuladora = new javax.swing.JLabel();
        PanelGraficaPortadora = new javax.swing.JPanel();
        LabelPortadora = new javax.swing.JLabel();
        PanelGraficaModulada = new javax.swing.JPanel();
        LabelModulada = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        PanelEspectro = new javax.swing.JPanel();
        LabelEspectroDeFrecuencia = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        TabbedResultados = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TextPaneResultados = new javax.swing.JTextPane();
        jScrollPane3 = new javax.swing.JScrollPane();
        TextPaneDatosSeñal = new javax.swing.JTextPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        TextPanelConjuntos = new javax.swing.JTextPane();
        PanelLeyenda = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        PanelDemodularModulada = new javax.swing.JPanel();
        LabelDemodularModulada = new javax.swing.JLabel();
        PanelDemodularModuladora = new javax.swing.JPanel();
        LabelDemodularModuladora = new javax.swing.JLabel();
        BtnDemodular = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        Comenzar = new javax.swing.JMenuItem();
        Salir = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        Desarrolladores = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        PanelLateral.setBackground(new java.awt.Color(0, 51, 102));
        PanelLateral.setForeground(new java.awt.Color(255, 255, 255));

        Panel1.setBackground(new java.awt.Color(153, 153, 153));
        Panel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED, null, java.awt.Color.white, null, null));

        jLabel4.setText("Amplitud:");

        jLabel5.setText("Frecuencia (Fm):");

        jLabel10.setText("Señal Modulante");

        jLabel2.setText("Tipo de Señal:");

        ComboTpseñalModuladora.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Senoidal", "Cosenoidal", "Triangular", "Diente de Sierra" }));
        ComboTpseñalModuladora.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ComboTpseñalModuladoraItemStateChanged(evt);
            }
        });

        TextFrecuenciaModulante.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TextFrecuenciaModulanteStateChanged(evt);
            }
        });

        TextAmplitudModulante.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TextAmplitudModulanteStateChanged(evt);
            }
        });

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "μV", "mV", "V", "kV", "MV", "GV" }));

        ComboMedidaModuladora.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "μHz", "mHz", "Hz", "kHz" }));
        ComboMedidaModuladora.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ComboMedidaModuladoraItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout Panel1Layout = new javax.swing.GroupLayout(Panel1);
        Panel1.setLayout(Panel1Layout);
        Panel1Layout.setHorizontalGroup(
            Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel1Layout.createSequentialGroup()
                .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Panel1Layout.createSequentialGroup()
                        .addGap(91, 91, 91)
                        .addComponent(jLabel10))
                    .addGroup(Panel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ComboTpseñalModuladora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(Panel1Layout.createSequentialGroup()
                                .addComponent(TextAmplitudModulante, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(Panel1Layout.createSequentialGroup()
                                .addComponent(TextFrecuenciaModulante, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ComboMedidaModuladora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        Panel1Layout.setVerticalGroup(
            Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel1Layout.createSequentialGroup()
                .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Panel1Layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jLabel4))
                    .addGroup(Panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel10)
                        .addGap(18, 18, 18)
                        .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TextAmplitudModulante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(9, 9, 9)
                .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TextFrecuenciaModulante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboMedidaModuladora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(ComboTpseñalModuladora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        Panel2.setBackground(new java.awt.Color(153, 153, 153));
        Panel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel8.setText("Frecuencia (Fp):");

        jLabel9.setText("Amplitud:");

        jLabel6.setText("Señal Portadora");

        jLabel18.setText("Tipo de Señal:");

        ComboTpseñalPortadora.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Senoidal", "Cosenoidal", "Triangular", "Diente de Sierra" }));
        ComboTpseñalPortadora.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ComboTpseñalPortadoraItemStateChanged(evt);
            }
        });

        TextAmplitudPortadora.setRequestFocusEnabled(false);
        TextAmplitudPortadora.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TextAmplitudPortadoraStateChanged(evt);
            }
        });

        TextFrecuenciaPortadora.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TextFrecuenciaPortadoraStateChanged(evt);
            }
        });

        ComboMedidaPortadora.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "μHz", "mHz", "Hz", "kHz", "MHz", "GHz", "THz" }));
        ComboMedidaPortadora.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ComboMedidaPortadoraItemStateChanged(evt);
            }
        });

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "μV", "mV", "V", "kV", "MV", "GV" }));

        javax.swing.GroupLayout Panel2Layout = new javax.swing.GroupLayout(Panel2);
        Panel2.setLayout(Panel2Layout);
        Panel2Layout.setHorizontalGroup(
            Panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel2Layout.createSequentialGroup()
                .addGroup(Panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Panel2Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(Panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel8)
                            .addComponent(jLabel18))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(Panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ComboTpseñalPortadora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(Panel2Layout.createSequentialGroup()
                                .addGroup(Panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(TextFrecuenciaPortadora, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                    .addComponent(TextAmplitudPortadora, javax.swing.GroupLayout.Alignment.LEADING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(Panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(ComboMedidaPortadora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(Panel2Layout.createSequentialGroup()
                        .addGap(91, 91, 91)
                        .addComponent(jLabel6)))
                .addContainerGap())
        );
        Panel2Layout.setVerticalGroup(
            Panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel2Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(TextAmplitudPortadora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(Panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(TextFrecuenciaPortadora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ComboMedidaPortadora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(Panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(ComboTpseñalPortadora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );

        Panel3.setBackground(new java.awt.Color(153, 153, 153));
        Panel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel11.setText("Señal Modulada");

        Labelm.setText("Indice de Modulacion (m):");

        TextIndiceDeModulacion.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TextIndiceDeModulacionStateChanged(evt);
            }
        });

        CheckModuladoraModulante.setToolTipText("Muestra la Grafica de la señal Modulada con la señal Modulante");
        CheckModuladoraModulante.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CheckModuladoraModulanteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Panel3Layout = new javax.swing.GroupLayout(Panel3);
        Panel3.setLayout(Panel3Layout);
        Panel3Layout.setHorizontalGroup(
            Panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel3Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(Labelm, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TextIndiceDeModulacion, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CheckModuladoraModulante)
                .addGap(41, 41, 41))
            .addGroup(Panel3Layout.createSequentialGroup()
                .addGap(93, 93, 93)
                .addComponent(jLabel11)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        Panel3Layout.setVerticalGroup(
            Panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addGroup(Panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(Labelm)
                        .addComponent(TextIndiceDeModulacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(CheckModuladoraModulante))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Panel4.setBackground(new java.awt.Color(153, 153, 153));
        Panel4.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        BtnModular.setText("Modular");
        BtnModular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnModularActionPerformed(evt);
            }
        });

        jLabel13.setText("Tiempo de Simulacion");

        jLabel1.setText("Tipo de Modulacion");

        TxtTiempo.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                TxtTiempoStateChanged(evt);
            }
        });

        ModulacionDeTipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FM", "PM" }));
        ModulacionDeTipo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ModulacionDeTipoItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout Panel4Layout = new javax.swing.GroupLayout(Panel4);
        Panel4.setLayout(Panel4Layout);
        Panel4Layout.setHorizontalGroup(
            Panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel4Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(Panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Panel4Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(Panel4Layout.createSequentialGroup()
                        .addComponent(TxtTiempo, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ModulacionDeTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Panel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BtnModular)
                .addGap(90, 90, 90))
        );
        Panel4Layout.setVerticalGroup(
            Panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Panel4Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(Panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel1))
                .addGap(21, 21, 21)
                .addGroup(Panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TxtTiempo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ModulacionDeTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addComponent(BtnModular)
                .addGap(21, 21, 21))
        );

        PanelGraficaModuladora.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        LabelModuladora.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        LabelModuladora.setText("Grafica de La Señal Modulante");
        PanelGraficaModuladora.add(LabelModuladora);

        PanelGraficaPortadora.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        LabelPortadora.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        LabelPortadora.setText("Grafica de La Señal Portadora");
        PanelGraficaPortadora.add(LabelPortadora);

        PanelGraficaModulada.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        LabelModulada.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        LabelModulada.setText("Grafica de La Señal Modulada");
        PanelGraficaModulada.add(LabelModulada);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(Panel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Panel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 267, Short.MAX_VALUE)
                    .addComponent(Panel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Panel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelGraficaModuladora, javax.swing.GroupLayout.DEFAULT_SIZE, 827, Short.MAX_VALUE)
                    .addComponent(PanelGraficaPortadora, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelGraficaModulada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(PanelGraficaModuladora, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PanelGraficaPortadora, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PanelGraficaModulada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(Panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(Panel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(9, 9, 9)
                        .addComponent(Panel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        TabbedModular.addTab("Modular", jPanel1);

        PanelEspectro.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        LabelEspectroDeFrecuencia.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        LabelEspectroDeFrecuencia.setText("Espectros de Frecuencia");
        PanelEspectro.add(LabelEspectroDeFrecuencia);

        TextPaneResultados.setEditable(false);
        jScrollPane2.setViewportView(TextPaneResultados);

        TextPaneDatosSeñal.setEditable(false);
        jScrollPane3.setViewportView(TextPaneDatosSeñal);

        javax.swing.GroupLayout TabbedResultadosLayout = new javax.swing.GroupLayout(TabbedResultados);
        TabbedResultados.setLayout(TabbedResultadosLayout);
        TabbedResultadosLayout.setHorizontalGroup(
            TabbedResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 216, Short.MAX_VALUE)
        );
        TabbedResultadosLayout.setVerticalGroup(
            TabbedResultadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TabbedResultadosLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
        );

        jTabbedPane1.addTab("Resultados", TabbedResultados);

        TextPanelConjuntos.setEditable(false);
        TextPanelConjuntos.setRequestFocusEnabled(false);
        TextPanelConjuntos.setVerifyInputWhenFocusTarget(false);
        jScrollPane1.setViewportView(TextPanelConjuntos);

        PanelLeyenda.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel3.setBackground(new java.awt.Color(0, 0, 0));
        jPanel3.setForeground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("LEYENDA");
        jPanel3.add(jLabel3);

        jLabel7.setText("vc : Voltaje de la Onda Portadora");

        jLabel14.setText("fc : Frecuencia de la Onda Portadora");

        jLabel15.setText("vm : Voltaje de la Onda Moduladora");

        jLabel16.setText("fm : Frecuencia de la Onda Moduladora");

        jLabel17.setText("m : Indice de Modulación");

        javax.swing.GroupLayout PanelLeyendaLayout = new javax.swing.GroupLayout(PanelLeyenda);
        PanelLeyenda.setLayout(PanelLeyendaLayout);
        PanelLeyendaLayout.setHorizontalGroup(
            PanelLeyendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(PanelLeyendaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelLeyendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jLabel14)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17)
                    .addComponent(jLabel15))
                .addContainerGap(173, Short.MAX_VALUE))
        );
        PanelLeyendaLayout.setVerticalGroup(
            PanelLeyendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelLeyendaLayout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(PanelLeyenda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(PanelEspectro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(21, 21, 21))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(PanelEspectro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1)
                            .addComponent(PanelLeyenda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(38, 38, 38))
        );

        TabbedModular.addTab("Espectro", jPanel2);

        PanelDemodularModulada.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        LabelDemodularModulada.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        LabelDemodularModulada.setText("Grafica de La Señal Modulante");
        PanelDemodularModulada.add(LabelDemodularModulada);

        PanelDemodularModuladora.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        LabelDemodularModuladora.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        LabelDemodularModuladora.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LabelDemodularModuladora.setText("Grafica de La Señal de Información Obtenida de Demodular (Modulante)");
        PanelDemodularModuladora.add(LabelDemodularModuladora);

        BtnDemodular.setText("Demodular");
        BtnDemodular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDemodularActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PanelDemodularModulada, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelDemodularModuladora, javax.swing.GroupLayout.DEFAULT_SIZE, 1010, Short.MAX_VALUE))
                .addGap(51, 51, 51))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BtnDemodular)
                .addGap(515, 515, 515))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(PanelDemodularModulada, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(BtnDemodular)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelDemodularModuladora, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35))
        );

        BtnDemodular.getAccessibleContext().setAccessibleParent(PanelDemodularModulada);

        TabbedModular.addTab("Demodulacion", jPanel4);

        javax.swing.GroupLayout PanelLateralLayout = new javax.swing.GroupLayout(PanelLateral);
        PanelLateral.setLayout(PanelLateralLayout);
        PanelLateralLayout.setHorizontalGroup(
            PanelLateralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelLateralLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(TabbedModular)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelLateralLayout.setVerticalGroup(
            PanelLateralLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelLateralLayout.createSequentialGroup()
                .addComponent(TabbedModular)
                .addGap(9, 9, 9))
        );

        jMenu1.setText("Archivo");

        Comenzar.setText("Comenzar");
        Comenzar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ComenzarActionPerformed(evt);
            }
        });
        jMenu1.add(Comenzar);

        Salir.setText("Salir");
        Salir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SalirActionPerformed(evt);
            }
        });
        jMenu1.add(Salir);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Ver");

        Desarrolladores.setText("Desarrolladores");
        Desarrolladores.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DesarrolladoresActionPerformed(evt);
            }
        });
        jMenu2.add(Desarrolladores);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(PanelLateral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(PanelLateral, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
   public double CalcularFrecuencia(String medida, double frecuencia){
    double resultado = 0.0;   
       switch (medida) {
           case "μHz": {
               System.out.println("μHz");
               resultado = frecuencia * Math.pow(10, -6);
               break;
           }
           case "mHz": {
               System.out.println("mHz");
               resultado = frecuencia * Math.pow(10, -3);
               break;
           }
           case "Hz": {
              System.out.println("Hz");
               resultado = frecuencia;
               break;
           }
           case "kHz": {
             System.out.println("kHz");
               resultado = frecuencia * Math.pow(10, 3);
               break;
           }
           case "MHz": {
                System.out.println("MHz");
               resultado = frecuencia * Math.pow(10, 6);
               break;
           }
           case "GHz": {
                System.out.println("GHz");
               resultado = frecuencia * Math.pow(10, 9);
               break;
           }
           case "THz": {
               System.out.println("THz");
               resultado = frecuencia * Math.pow(10, 12);
               break;
           }
       }
     return resultado;
   }
   
   public double AjustarEspectros(double frecuencia){  
    String[] unidades = {"μHz", "mHz", "Hz", "kHz", "MHz", "GHz", "THz"} ;
    int cont=0;
    for(int i = 0; i < unidades.length; i++){
        if(unidades[i].equals(FrecuenciaPortadora)){
          while(frecuencia > 1000){
             frecuencia = frecuencia / Math.pow(10,3);
             MedidaEspectros = unidades[i++]; cont++;
          }
          if(cont==0){
          MedidaEspectros = unidades[i];
          } 
       }
    }
    return frecuencia;
   }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
       int confirmar = JOptionPane.showConfirmDialog(null,"¿Estas seguro?", "Salir de la Aplicacion", JOptionPane.YES_NO_OPTION);
       if(JOptionPane.OK_OPTION == confirmar){
           System.exit(0);
       }else{
       }
    }//GEN-LAST:event_formWindowClosing

    private void DesarrolladoresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DesarrolladoresActionPerformed
        String developers; //Nombre de los Desarrolladores
        developers = " Kender Correia\n Juliannys Margoy\n Rosibel Vicent";
        JOptionPane.showMessageDialog(null, "Aplicacion Desarrollada Por:\n"+developers, "Desarrolladores", JOptionPane.NO_OPTION);
    }//GEN-LAST:event_DesarrolladoresActionPerformed

    private void BtnModularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnModularActionPerformed

        if(TxtTiempo.getValue().toString().equals("0")){
            JOptionPane.showMessageDialog(this,"Introduzca el Tiempo de Simulacion");
        }else{
            LabelModuladora.setVisible(false);
            LabelPortadora.setVisible(false);
            LabelModulada.setVisible(false);
            LabelEspectroDeFrecuencia.setVisible(false);
            LabelDemodularModulada.setVisible(false);
            LabelDemodularModuladora.setVisible(false);
            GraficarSeñales();
        }
    }//GEN-LAST:event_BtnModularActionPerformed

    private void TextAmplitudModulanteStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_TextAmplitudModulanteStateChanged
        actualizar();
        GraficarSeñales();
    }//GEN-LAST:event_TextAmplitudModulanteStateChanged

    private void TextFrecuenciaModulanteStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_TextFrecuenciaModulanteStateChanged
        actualizar();
        GraficarSeñales();
    }//GEN-LAST:event_TextFrecuenciaModulanteStateChanged

    private void TextAmplitudPortadoraStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_TextAmplitudPortadoraStateChanged
        actualizar();
        GraficarSeñales();
    }//GEN-LAST:event_TextAmplitudPortadoraStateChanged

    private void TextFrecuenciaPortadoraStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_TextFrecuenciaPortadoraStateChanged
        actualizar();
        GraficarSeñales();
    }//GEN-LAST:event_TextFrecuenciaPortadoraStateChanged

    private void TextIndiceDeModulacionStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_TextIndiceDeModulacionStateChanged
        actualizar();
        GraficarSeñales();
    }//GEN-LAST:event_TextIndiceDeModulacionStateChanged

    private void TxtTiempoStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_TxtTiempoStateChanged
        actualizar();
        GraficarSeñales();
    }//GEN-LAST:event_TxtTiempoStateChanged

    private void ModulacionDeTipoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ModulacionDeTipoItemStateChanged
        actualizar();
        GraficarSeñales();
    }//GEN-LAST:event_ModulacionDeTipoItemStateChanged

    private void CheckModuladoraModulanteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CheckModuladoraModulanteActionPerformed
        actualizar();
        GraficarSeñales();
    }//GEN-LAST:event_CheckModuladoraModulanteActionPerformed

    private void ComboTpseñalModuladoraItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ComboTpseñalModuladoraItemStateChanged
        actualizar();
        GraficarSeñales();
    }//GEN-LAST:event_ComboTpseñalModuladoraItemStateChanged

    private void ComboTpseñalPortadoraItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ComboTpseñalPortadoraItemStateChanged
        actualizar();
        GraficarSeñales();
    }//GEN-LAST:event_ComboTpseñalPortadoraItemStateChanged

    private void ComboMedidaModuladoraItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ComboMedidaModuladoraItemStateChanged
        actualizar();
        GraficarSeñales();
    }//GEN-LAST:event_ComboMedidaModuladoraItemStateChanged

    private void ComboMedidaPortadoraItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ComboMedidaPortadoraItemStateChanged
        actualizar();
        GraficarSeñales();
    }//GEN-LAST:event_ComboMedidaPortadoraItemStateChanged

    private void BtnDemodularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDemodularActionPerformed
        final JDialog dlgProgress = new JDialog(this, "Por Favor Espere", true);//true means that the dialog created is modal
        JLabel lblStatus = new JLabel("Demodulando..."); // this is just a label in which you can indicate the state of the processing

        JProgressBar pbProgress = new JProgressBar(0, 100);
        pbProgress.setIndeterminate(true); //we'll use an indeterminate progress bar

        dlgProgress.add(BorderLayout.NORTH, lblStatus);
        dlgProgress.add(BorderLayout.CENTER, pbProgress);
        dlgProgress.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // prevent the user from closing the dialog
        dlgProgress.setSize(300, 90);
        dlgProgress.setLocationRelativeTo(null);
        
        SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int index = 0; index < 500; index++) {
                int progress = Math.round(((float) index / 500f) * 100f);
                setProgress(progress);
                Thread.sleep(5);
            }
                return null;
            }

            @Override
            protected void done() {
                dlgProgress.dispose();//close the modal dialog
            }
        };

        sw.execute(); // this will start the processing on a separate thread
        dlgProgress.setVisible(true); //this will block user input as long as the processing task is working
        BtnDemodular.setVisible(false);
        PanelDemodularModuladora.setVisible(true);
    }//GEN-LAST:event_BtnDemodularActionPerformed

    private void SalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SalirActionPerformed
        int confirmar = JOptionPane.showConfirmDialog(null, "¿Estas seguro?", "Salir de la Aplicacion", JOptionPane.YES_NO_OPTION);
        if (JOptionPane.OK_OPTION == confirmar) {
            System.exit(0);
        }     // TODO add your handling code here:
    }//GEN-LAST:event_SalirActionPerformed

    private void ComenzarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ComenzarActionPerformed
        int confirmar = JOptionPane.showConfirmDialog(null, "¿Desea Comenzar nuevamente?", "¡Alerta Lautcom!", JOptionPane.YES_NO_OPTION);
        if (JOptionPane.OK_OPTION == confirmar) {
            InicializarVariables();
        }     // TODO add your handling code here:
    }//GEN-LAST:event_ComenzarActionPerformed

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Graficas().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnDemodular;
    private javax.swing.JButton BtnModular;
    private javax.swing.JCheckBox CheckModuladoraModulante;
    private javax.swing.JComboBox<String> ComboMedidaModuladora;
    private javax.swing.JComboBox<String> ComboMedidaPortadora;
    private javax.swing.JComboBox<String> ComboTpseñalModuladora;
    private javax.swing.JComboBox<String> ComboTpseñalPortadora;
    private javax.swing.JMenuItem Comenzar;
    private javax.swing.JMenuItem Desarrolladores;
    private javax.swing.JLabel LabelDemodularModulada;
    private javax.swing.JLabel LabelDemodularModuladora;
    private javax.swing.JLabel LabelEspectroDeFrecuencia;
    private javax.swing.JLabel LabelModulada;
    private javax.swing.JLabel LabelModuladora;
    private javax.swing.JLabel LabelPortadora;
    private javax.swing.JLabel Labelm;
    private javax.swing.JComboBox<String> ModulacionDeTipo;
    private javax.swing.JPanel Panel1;
    private javax.swing.JPanel Panel2;
    private javax.swing.JPanel Panel3;
    private javax.swing.JPanel Panel4;
    private javax.swing.JPanel PanelDemodularModulada;
    private javax.swing.JPanel PanelDemodularModuladora;
    private javax.swing.JPanel PanelEspectro;
    private javax.swing.JPanel PanelGraficaModulada;
    private javax.swing.JPanel PanelGraficaModuladora;
    private javax.swing.JPanel PanelGraficaPortadora;
    private javax.swing.JPanel PanelLateral;
    private javax.swing.JPanel PanelLeyenda;
    private javax.swing.JMenuItem Salir;
    private javax.swing.JTabbedPane TabbedModular;
    private javax.swing.JPanel TabbedResultados;
    private javax.swing.JSpinner TextAmplitudModulante;
    private javax.swing.JSpinner TextAmplitudPortadora;
    private javax.swing.JSpinner TextFrecuenciaModulante;
    private javax.swing.JSpinner TextFrecuenciaPortadora;
    private javax.swing.JSpinner TextIndiceDeModulacion;
    private javax.swing.JTextPane TextPaneDatosSeñal;
    private javax.swing.JTextPane TextPaneResultados;
    private javax.swing.JTextPane TextPanelConjuntos;
    private javax.swing.JSpinner TxtTiempo;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}

 /*private static Color COLOR_SERIE_1 = new Color(28, 84, 140);
 private static Color COLOR_RECUADROS_GRAFICA = new Color(31, 87, 4);
 private static Color COLOR_FONDO_GRAFICA = Color.GRAY;*/
////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////
/*  chartPortadora.setBackgroundPaint(COLOR_FONDO_GRAFICA); //ponerle color al fondo de la grafica
        final XYPlot plot = (XYPlot) chartPortadora.getPlot(); //cambiarle el color a los puntos de la grafica
        final XYPlot plot1 = (XYPlot) chartSeries.getPlot();
        final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(); //cambiarle el color a la grafica
        final XYLineAndShapeRenderer SeriesColor = (XYLineAndShapeRenderer) plot1.getRenderer(); 
        plot.setDomainGridlinePaint(COLOR_RECUADROS_GRAFICA); //pinta los puntos de la grafica 
        plot.setRangeGridlinePaint(COLOR_RECUADROS_GRAFICA);
        renderer.setSeriesPaint(0, COLOR_SERIE_1); //Le cambia el color a la grafica
        SeriesColor.setSeriesPaint(0, Color.blue); //Le cambia el color a la grafica
        SeriesColor.setSeriesPaint(1, Color.red); //Le cambia el color a la grafica*/
////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////
    /* public void FondoPantalla() {
       Panel2.setBackground(new Color(0,0,0,64));
        this.setLocationRelativeTo(null);//Inicializa la ventana para que este centrada
        //this.setResizable(false);//Inicializa la ventana para que no cambie de tamaño
        ((JPanel) getContentPane()).setOpaque(false);
        ImageIcon uno = new ImageIcon(this.getClass().getResource("/Imagenes/ModulacionFondo.png"));
        JLabel fondo = new JLabel();
        fondo.setIcon(uno);
        getLayeredPane().add(fondo, JLayeredPane.FRAME_CONTENT_LAYER);
        fondo.setBounds(0, 0, uno.getIconWidth(), uno.getIconHeight());
    }*/