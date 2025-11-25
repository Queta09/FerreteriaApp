package com.ferreteria.ferreteriaapp.model;

public abstract class Persona {
    protected int id;
    protected String nombre;
    protected String apellidoPaterno;
    protected String apellidoMaterno;

    public Persona() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getNombreCompleto(){
        return nombre + " " + apellidoPaterno + " " + (apellidoMaterno != null ? apellidoMaterno : "");
    }
}
