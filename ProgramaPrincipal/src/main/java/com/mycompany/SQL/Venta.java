package com.mycompany.SQL;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class Venta {
    private final BigInteger ID;
    private LocalDateTime fecha;

    public Venta (BigInteger id, LocalDateTime fecha){
        this.ID = id;
        this.fecha = fecha;
    }
    
    //GETTER
    public BigInteger getID() {
        return this.ID;
    }
    public LocalDateTime getFecha() {
        return this.fecha;
    }
    //SETTER
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

}
