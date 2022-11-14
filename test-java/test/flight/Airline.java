package test.flight;

import java.util.List;

public class Airline
{
    String name;
    List planes;
    
    public Airline(Body body){}
    public List getPlanes(){return planes;}
    public void setPlanes(List planes){this.planes = planes;}
    public String getName(){return name;}
    public void setName (String name){this.name = name;}
}