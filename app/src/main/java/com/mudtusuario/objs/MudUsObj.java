package com.mudtusuario.objs;

import java.io.Serializable;

public class MudUsObj implements Serializable {

    public double carga_lat, carga_lon, des_lat, des_lon;
    public String carga_dir, des_dir, fecha, desc;
    public UnitObj unitObj;
    public int piso_carga, piso_des;
    public boolean elev_carga, elev_des;

}
