
package telecomunicaciones;


public class Formulas {
  private double fm, fc;
  private double m, vc, vm, sensibilidad, desviacion, desviacionFase; 
  private double k;
  
    public Formulas(){ //constructor sin parametros
    }
    
    public Formulas(double fm, double vm, double fc, double vc, double m, double k){ //Constructor para FM
        this.fm = fm;
        this.vm = vm;
        this.fc = fc;
        this.vc = vc;
        this.m = m;
        this.k = k;
    }
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
    public double SensibilidadKl() {
        sensibilidad = ((fm * m) / vm);
        return sensibilidad;
    }
          //////////////////////FM-FM-FM-FM///////////////////////////
    public double DesviacionFrecuencia() {
        desviacion = (SensibilidadKl() * vm);
        return desviacion;
    }
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
    public double SensibilidadK(){
        sensibilidad = (DesviacionFase() / vm);
        return sensibilidad;   
    }
          //////////////////////PM-PM-PM-PM///////////////////////////    
    public double DesviacionFase(){
        desviacionFase = (k * vm); //Desviacion de fase o (m) indice de modulacion
        return desviacionFase;
    }
}
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////