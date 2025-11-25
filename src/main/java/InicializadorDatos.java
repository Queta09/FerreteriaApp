package com.ferreteria.ferreteriaapp;

import com.ferreteria.ferreteriaapp.model.Cliente;
import com.ferreteria.ferreteriaapp.model.Empleado;
import com.ferreteria.ferreteriaapp.model.Producto; // <--- No olvides importar esto
import com.ferreteria.ferreteriaapp.model.Proveedor;
import com.ferreteria.ferreteriaapp.services.*;

public class InicializadorDatos {

    public static void main(String[] args) {
        System.out.println("--- 🔄 INICIANDO CARGA MASIVA DE DATOS ---");

        // 1. Inicializar Managers
        EmpleadoManager empleadoManager = new EmpleadoManager();
        ClienteManager clienteManager = new ClienteManager();
        ProveedorManager proveedorManager = new ProveedorManager();
        InventarioManager inventarioManager = new InventarioManager();
        new VentaManager();

        System.out.println("✅ Tablas verificadas.");

        // ---------------------------------------------------------
        // 2. EMPLEADOS (Login)
        // ---------------------------------------------------------
        if (empleadoManager.obtenerTodos().isEmpty()) {
            // Vendedor
            Empleado emp = new Empleado();
            emp.setNombre("Pedro");
            emp.setApellidoPaterno("Uuh");
            emp.setTelefono("986-123-4567");
            emp.setPuesto("Vendedor");
            emp.setUsuario("Pedro");
            emp.setPassword("123");
            emp.setRol("VENDEDOR");
            empleadoManager.agregar(emp);

            // Admin
            Empleado emp2 = new Empleado();
            emp2.setNombre("Juan");
            emp2.setApellidoPaterno("Fernandez");
            emp2.setTelefono("999-999-8888");
            emp2.setPuesto("Gerente");
            emp2.setUsuario("admin");
            emp2.setPassword("admin");
            emp2.setRol("ADMIN");
            empleadoManager.agregar(emp2);

            System.out.println("👤 Empleados creados.");
        }

        // ---------------------------------------------------------
        // 3. PROVEEDORES (Necesarios para los productos)
        // ---------------------------------------------------------
        if (proveedorManager.obtenerTodos().isEmpty()) {
            // ID 1: Truper (Herramientas)
            Proveedor p1 = new Proveedor();
            p1.setNombreEmpresa("Truper S.A. de C.V.");
            p1.setRfc("TRU-987654-MX");
            p1.setDireccion("Parque Industrial CDMX");
            p1.setCorreo("pedidos@truper.com");
            p1.setDiasEntrega("Lunes y Jueves");
            p1.setNombre("Roberto"); p1.setApellidoPaterno("Ventas"); p1.setTelefono("55-1111-2222");
            proveedorManager.agregar(p1);

            // ID 2: Materiales del Sur (Construcción)
            Proveedor p2 = new Proveedor();
            p2.setNombreEmpresa("Materiales del Sur");
            p2.setRfc("MAT-123123-YUC");
            p2.setDireccion("Periférico Mérida");
            p2.setCorreo("ventas@matsur.com");
            p2.setDiasEntrega("Martes");
            p2.setNombre("Carlos"); p2.setApellidoPaterno("Canto"); p2.setTelefono("999-333-4444");
            proveedorManager.agregar(p2);

            // ID 3: Luminex (Eléctrico)
            Proveedor p3 = new Proveedor();
            p3.setNombreEmpresa("Luminex Eléctrica");
            p3.setRfc("LUM-555666-MX");
            p3.setDireccion("Av. Itzaes");
            p3.setCorreo("contacto@luminex.com");
            p3.setDiasEntrega("Viernes");
            p3.setNombre("Ana"); p3.setApellidoPaterno("Solis"); p3.setTelefono("999-555-6666");
            proveedorManager.agregar(p3);

            System.out.println("🚚 3 Proveedores creados.");
        }

        // ---------------------------------------------------------
        // 4. CLIENTES
        // ---------------------------------------------------------
        if (clienteManager.obtenerTodos().isEmpty()) {
            Cliente c1 = new Cliente();
            c1.setNombre("Juan");
            c1.setApellidoPaterno("Pérez");
            c1.setTelefono("999-123-0000");
            c1.setCorreo("juan@mail.com");
            c1.setDireccion("Centro, Tizimín");
            clienteManager.agregar(c1);

            Cliente c2 = new Cliente();
            c2.setNombre("María");
            c2.setApellidoPaterno("López");
            c2.setTelefono("986-987-6543");
            c2.setCorreo("maria@mail.com");
            c2.setDireccion("Col. San José");
            clienteManager.agregar(c2);

            System.out.println("🛒 2 Clientes creados.");
        }

        // ---------------------------------------------------------
        // 5. PRODUCTOS (Inventario Inicial)
        // ---------------------------------------------------------
        if (inventarioManager.obtenerTodos().isEmpty()) {
            // --- HERRAMIENTAS (Proveedor ID 1 - Truper) ---
            crearProducto(inventarioManager, "HER-001", "Martillo de Uña", "Herramientas", 85.50, 145.00, 15, 5, 1);
            crearProducto(inventarioManager, "HER-002", "Destornillador Plano", "Herramientas", 25.00, 45.00, 30, 10, 1);
            crearProducto(inventarioManager, "HER-003", "Taladro Rotomartillo", "Herramientas", 800.00, 1250.00, 4, 2, 1);
            crearProducto(inventarioManager, "HER-004", "Llave Inglesa 10p", "Herramientas", 120.00, 199.90, 8, 3, 1);

            // --- CONSTRUCCIÓN (Proveedor ID 2 - Materiales del Sur) ---
            crearProducto(inventarioManager, "CON-001", "Cemento Gris 50kg", "Construcción", 210.00, 260.00, 50, 20, 2);
            crearProducto(inventarioManager, "CON-002", "Calidra 25kg", "Construcción", 65.00, 90.00, 40, 10, 2);
            crearProducto(inventarioManager, "CON-003", "Varilla 3/8", "Construcción", 145.00, 185.00, 100, 50, 2);

            // --- ELÉCTRICO (Proveedor ID 3 - Luminex) ---
            crearProducto(inventarioManager, "ELE-001", "Foco LED 10W", "Eléctrico", 18.00, 35.00, 60, 15, 3);
            crearProducto(inventarioManager, "ELE-002", "Cable Calibre 12 (Metro)", "Eléctrico", 12.00, 22.00, 200, 50, 3);
            crearProducto(inventarioManager, "ELE-003", "Contacto Duplex", "Eléctrico", 25.00, 45.00, 25, 5, 3);

            System.out.println("📦 10 Productos agregados al inventario.");
        }

        System.out.println("--- ✅ CARGA COMPLETA: LISTO PARA USAR ---");
    }

    // Método auxiliar para no repetir tanto código al crear productos
    private static void crearProducto(InventarioManager manager, String codigo, String nombre, String categoria,
                                      double costo, double venta, int stock, int minimo, int idProveedor) {
        Producto p = new Producto();
        p.setCodigo(codigo);
        p.setNombre(nombre);
        p.setCategoria(categoria);
        p.setPrecioCosto(costo);
        p.setPrecioVenta(venta);
        p.setStockActual(stock);
        p.setStockMinimo(minimo);
        p.setIdProveedor(idProveedor);

        manager.agregar(p);
    }
}