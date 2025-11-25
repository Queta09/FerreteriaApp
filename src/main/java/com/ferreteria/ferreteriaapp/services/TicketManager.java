package com.ferreteria.ferreteriaapp.services;

import com.ferreteria.ferreteriaapp.model.DetalleVenta;
import com.ferreteria.ferreteriaapp.model.Venta;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Servicio de generación de documentos digitales.
 * Utiliza la librería iText para crear archivos PDF formateados.
 */

public class TicketManager {
    /**
     * Genera un archivo PDF con el comprobante de la venta.
     * El archivo se guarda localmente con el formato "Ticket_Venta_[ID].pdf".
     *
     * @param venta El objeto venta con todos los detalles necesarios para el recibo.
     */

    public void generarTicketPDF(Venta venta, double pagoCliente) {
        // El nombre del archivo incluirá el ID de la venta para que sea único
        String nombreCarpeta = "tickets";
        String nombreArchivo = "Ticket_Venta_" + venta.getId() + ".pdf";

        // 2. Crear objeto File para la carpeta
        File carpeta = new File(nombreCarpeta);
        if (!carpeta.exists()) {
            carpeta.mkdir();
        }

        File archivoDestino = new File(carpeta, nombreArchivo);

        Document documento = new Document();

        try {
            PdfWriter.getInstance(documento, new FileOutputStream(archivoDestino));

            documento.open();

            // 1. Encabezado
            Font fontTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph titulo = new Paragraph("Ferretería El Martillo", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);

            Paragraph subtitulo = new Paragraph("Ticket de Venta #" + venta.getId());
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(20);
            documento.add(subtitulo);

            // 2. Datos Generales
            documento.add(new Paragraph("Fecha: " + venta.getFecha()));
            documento.add(new Paragraph("Cliente: " + venta.getCliente().getNombreCompleto()));
            documento.add(new Paragraph("Atendido por: " + (venta.getEmpleado() != null ? venta.getEmpleado().getNombre() : "Sistema")));
            documento.add(new Paragraph("----------------------------------------------------------"));
            documento.add(Chunk.NEWLINE);

            // 3. Tabla de Productos
            PdfPTable tabla = new PdfPTable(4); // 4 columnas
            tabla.setWidthPercentage(100);

            // Encabezados de tabla
            tabla.addCell("Cant.");
            tabla.addCell("Producto");
            tabla.addCell("P. Unit.");
            tabla.addCell("Total");

            for (DetalleVenta detalle : venta.getDetalles()) {
                tabla.addCell(String.valueOf(detalle.getCantidad()));
                tabla.addCell(detalle.getProducto().getNombre());
                tabla.addCell("$" + detalle.getPrecioUnitario());
                tabla.addCell("$" + detalle.getSubtotal());
            }
            documento.add(tabla);

            // 4. Totales
            documento.add(Chunk.NEWLINE);

            // 1. Calcular Subtotal Real (Suma de los productos)
            double subtotalReal = 0;
            for (DetalleVenta dv : venta.getDetalles()) {
                subtotalReal += dv.getSubtotal();
            }

            // 2. Imprimir Subtotal
            Paragraph pSubtotal = new Paragraph("Subtotal: $" + String.format("%.2f", subtotalReal));
            pSubtotal.setAlignment(Element.ALIGN_RIGHT);
            documento.add(pSubtotal);

            // 3. Imprimir Descuento
            if (venta.getDescuento() > 0) {
                Paragraph pDescuento = new Paragraph("Descuento: -$" + String.format("%.2f", venta.getDescuento()));
                pDescuento.setAlignment(Element.ALIGN_RIGHT);
                pDescuento.getFont().setColor(BaseColor.RED);
                documento.add(pDescuento);
            }

            // 4. CALCULAR TOTAL NETO
            // Restamos explícitamente el descuento al subtotal
            double totalNeto = subtotalReal - venta.getDescuento();
            if (totalNeto < 0) totalNeto = 0;

            // 5. Imprimir Total Final Correcto
            Paragraph pTotal = new Paragraph("TOTAL A PAGAR: $" + String.format("%.2f", totalNeto));
            pTotal.setAlignment(Element.ALIGN_RIGHT);
            Font fontTotal = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
            pTotal.setFont(fontTotal);
            documento.add(pTotal);

            // 6. Datos de Pago y Cambio (Usando el Total Neto)
            documento.add(Chunk.NEWLINE);

            // Calcular cambio usando el totalNeto, no venta.getTotal()
            double cambio = pagoCliente - totalNeto;
            if (cambio < 0) cambio = 0; // Por si acaso

            String metodoPago = (pagoCliente >= totalNeto) ? "Efectivo" : "Tarjeta/Exacto";

            documento.add(new Paragraph("Forma de Pago: " + metodoPago));
            documento.add(new Paragraph("Pagó con: $" + String.format("%.2f", pagoCliente)));
            documento.add(new Paragraph("Cambio: $" + String.format("%.2f", cambio))); // Ahora sí saldrá $20.00

            // 5. Pie de página
            documento.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph("¡Gracias por su compra!\nConserve este ticket para cualquier aclaración.");
            footer.setAlignment(Element.ALIGN_CENTER);
            documento.add(footer);

            System.out.println("Ticket PDF generado: " + nombreArchivo);

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        } finally {
            documento.close();
        }
    }
}