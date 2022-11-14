package test.flight;

public class Tail
{
    String color;
    TailFlap flap;
    
    public Tail(TailFlap flap, String color){this.color = color; this.flap = flap;}
    public String getColor(){return color;}
    public void setColor(String color){this.color = color;}
    public TailFlap getFlap(){return flap;}
    public void setFlap(TailFlap flap){this.flap = flap;}
 
    public class TailFlap
    {
      String label;
      public TailFlap(String label){
        this.label = label;
      }
      public String getLabel(){return label;}
      public void setLabel(String label){this.label = label;}
    }
    
}