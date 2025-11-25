package com.ferreteria.ferreteriaapp.services;

import java.util.List;

/**
 * Interfaz genérica que define el contrato de operaciones CRUD (Crear, Leer, Actualizar, Eliminar).
 * Permite estandarizar la gestión de datos para todas las entidades del sistema.
 *
 * @param <T> El tipo de modelo que se va a gestionar (Cliente, Producto, Proveedor, etc.)
 */
public interface IGestionable<T> {

    // Crear
    void agregar(T entidad);

    // Leer
    List<T> obtenerTodos();
    T obtenerPorId(int id);

    // Actualizar
    void actualizar(T entidad);

    // Borrar
    void eliminar(int id);
}