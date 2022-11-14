package test.flight;

public class Plane
{
    Body body;
    Radar radar;
    String name;
    
    public Plane(Body body){this.body = body;}
    public Body getBody(){return body;}
    public void setBody(Body body){this.body = body;}
    public Radar getRadar(){return radar;}
    public void  setRadar(Radar radar){this.radar = radar;}
    public String getName(){return name;}
    public void setName (String name){this.name = name;}
}