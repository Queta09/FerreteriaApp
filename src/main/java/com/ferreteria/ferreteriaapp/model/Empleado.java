package com.ferreteria.ferreteriaapp.model;

public class Empleado extends Persona{
    protected String puesto;
    protected String password;
    protected String usuario;
    protected String telefono;
    private String rol;

    public Empleado() {
        super();
    }

    public Empleado(int id, String nombres, String apellidoPaterno, String apellidoMaterno, String telefono, String puesto, String usuario, String password,  String rol) {
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

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }
    
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
