package com.ferreteria.ferreteriaapp.model;

public class Proveedor extends Persona{
    private String nombreEmpresa;
    private String telefono;
    private String direccion;
    private String correo;
    private String diasEntrega;
    private String rfc;

    public Proveedor(){
        super();
    }

    public Proveedor(int id, String nombre, String apellidoPaterno, String apellidoMaterno, String telefono,
                     String nombreEmpresa, String rfc, String direccion, String correo, String diasEntrega) {
        super();
        // Datos heredados (del agente de ventas)
        this.id = id;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.telefono = telefono;

        // Datos propios (de la empresa)
        this.nombreEmpresa = nombreEmpresa;
        this.rfc = rfc;
        this.direccion = direccion;
        this.correo = correo;
        this.diasEntrega = diasEntrega;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDiasEntrega() {
        return diasEntrega;
    }

    public void setDiasEntrega(String diasEntrega) {
        this.diasEntrega = diasEntrega;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
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

    @Override
    public String toString() {
        return nombreEmpresa + " (" + getNombre() + ")";
    }
}
