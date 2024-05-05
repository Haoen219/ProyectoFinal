package com.mycompany.clases;

import java.sql.Date;

public class Venta {
    private final int ID;
    private Date fecha;

    public Venta (int id, Date fecha){
        this.ID = id;
        this.fecha = fecha;
    }
    
    //GETTER
    public int getID() {
        return this.ID;
    }
    public Date getFecha() {
        return this.fecha;
    }
    //SETTER
    public void setPrecio(Date fecha) {
        this.fecha = fecha;
    }

}
