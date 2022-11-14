package test.flight;

import java.util.List;

public class Body
{
    BodyType bodyType;
    List wings;
    Tail tail;
    
    public Body(BodyType bodyType, List wings, Tail tail){
      this.tail = tail;
      this.wings = wings;
      this.bodyType = bodyType; 
    }
    
    public List getWings(){return wings;}
    public BodyType getType(){return bodyType;}
    public Tail getTail(){return tail;}
    public void setWings(List wings){this.wings = wings;}
    public void setType(BodyType byteType){this.bodyType = bodyType;}
    public void setTail(Tail tail){this.tail = tail;}
    
    public enum BodyType{
      SMALL, MEDIUM, LARGE;
    }
}