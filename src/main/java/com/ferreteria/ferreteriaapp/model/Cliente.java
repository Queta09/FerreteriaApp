package com.ferreteria.ferreteriaapp.model;

public class Cliente extends Persona{

    protected String correo;
    protected String direccion;
    protected String telefono;

    public Cliente(int id, String nombre, String apellidoPaterno, String apellidoMaterno, String telefono, String correo, String direccion) {
        super();
    }

    public Cliente() {

    }

    @Override
    public int getId() {
        return super.getId();
    }

    @Override
    public void setId(int id) {
        super.setId(id);
    }

    @Override
    public String getNombre() {
        return super.getNombre();
    }

    @Override
    public void setNombre(String nombre) {
        super.setNombre(nombre);
    }

    @Override
    public String getApellidoPaterno() {
        return super.getApellidoPaterno();
    }

    @Override
    public void setApellidoPaterno(String apellidoPaterno) {
        super.setApellidoPaterno(apellidoPaterno);
    }

    @Override
    public String getApellidoMaterno() {
        return super.getApellidoMaterno();
    }

    @Override
    public void setApellidoMaterno(String apellidoMaterno) {
        super.setApellidoMaterno(apellidoMaterno);
    }

    @Override
    public String getNombreCompleto() {
        return super.getNombreCompleto();
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
