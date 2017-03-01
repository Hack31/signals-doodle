
package telecomunicaciones;


public class Formulas {
  private double fm, m, vm, k;
  
    public Formulas(double fm, double vm,double m, double k){ //Constructor para FM
        this.fm = fm;
        this.vm = vm;
        this.m = m;
        this.k = k;
    }
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
    public double kFM() { //sensibilidad de la desviacion (kl)
        return ((fm * m) / vm);
    }
    //////////////////////FM-FM-FM-FM///////////////////////////
    public double DesviacionFrecuencia() { //delta f
        return (kFM() * vm);
    }
    public double mFM() { //Indice de modulacion (m)
        return ((k * vm) / fm);
    }
    public double vmFM(){ //amplitud moduladora (vm)
        return ((fm * m) / kFM());
    }
    public double vmFM2(){ //amplitud cuando no se la frecuencia ni indice de modulacion
        return (DesviacionFrecuencia() / kFM());
    }
    public double fFM(){ //frecuencia moduladora (fm)
        return (DesviacionFrecuencia() / m);
    }
    
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
    public double kPM() {//sensibilidad a la desviacion (k) PM
        return ((k * vm) / vm);
    }
    //////////////////////PM-PM-PM-PM///////////////////////////    
    public double mPM() {//Desviacion de fase (m)
        return (k * vm);
    }
    public  double vmPM(){//amplitud moduladora (vm)
        return (mPM() / kPM());    
    }
    
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

   
    
}
