package in.thefleet.thefuelentry;

/**
 * Created by DILEEP on 19-01-2017.
 */

public class Globals{
    private static Globals instance;

    // Global variable
    private String fleetSelected;
    private double avgSelected;
    private double lqtySelected;

    private int fidSelected;
    private int sidSelected;
    private int fpSelected;

    private Object  latSelected;
    private Object  lonSelected;
    private String imei;
    private double rgfpselected;
    private double pmfpselected;
    private String fuelSelected;

    // Restrict the constructor from being instantiated
    private Globals(){}

    public String getFuelSelected() {return fuelSelected;}

    public void setFuelSelected(String fuelSelected) {this.fuelSelected = fuelSelected;}

    public Object getLatSelected() {return latSelected;}

    public void setLatSelected(Object latSelected) {this.latSelected = latSelected;}

    public Object getLonSelected() {return lonSelected;}

    public void setLonSelected(Object lonSelected) {this.lonSelected = lonSelected;}


    public void setfleetSelected(String f){
        this.fleetSelected=f;
    }
    public String getfleetSelected(){
        return this.fleetSelected;
    }

    public void setavgSelected(double o){
        this.avgSelected=o;
    }
    public double getavgSelected(){
        return this.avgSelected;
    }

    public void setlqtySelected(double l){
        this.lqtySelected=l;
    }
    public double getlqtySelected(){
        return this.lqtySelected;
    }

    public void setFidSelected(int fid) {this.fidSelected = fid;}
    public int getFidSelected() {return this.fidSelected;}

    public int getSidSelected() {return sidSelected;}
    public void setSidSelected(int sid) {this.sidSelected = sid;}

    public int getFpSelected() {return fpSelected;}
    public void setFpSelected(int fp) {this.fpSelected = fp;}

    public String getImei() {return imei;}

    public void setImei(String imei) {this.imei = imei;}


    public double getPmfpselected() {return pmfpselected;}

    public void setPmfpselected(double pmfpselected) {this.pmfpselected = pmfpselected;}

    public double getRgfpselected() {return rgfpselected;}

    public void setRgfpselected(double rgfpselected) {this.rgfpselected = rgfpselected;}

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }
}

