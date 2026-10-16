package com.jennifer_y_madeline.proyecto.databases;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Una venta completa: junta las líneas (productos) que se registraron en la misma venta
 * (mismo cliente, misma fecha y mismo método de pago).
 */
public class VentaResumen {

    private final List<Venta> lineas = new ArrayList<>();

    public void agregar(Venta linea) {
        lineas.add(linea);
    }

    public List<Venta> getLineas() {
        return lineas;
    }

    /** Número de venta: el id de su primera línea. */
    public int getNumero() {
        int menor = Integer.MAX_VALUE;
        for (Venta v : lineas) {
            if (v.getId() != null && v.getId() < menor) {
                menor = v.getId();
            }
        }
        return menor == Integer.MAX_VALUE ? 0 : menor;
    }

    public LocalDateTime getFecha() {
        return lineas.get(0).getFecha();
    }

    public Cliente getCliente() {
        return lineas.get(0).getCliente();
    }

    public MetodoPago getMetodoPago() {
        return lineas.get(0).getMetodoPago();
    }

    /** Suma de precio x cantidad de todas las líneas. */
    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Venta v : lineas) {
            total = total.add(v.getPrecioUnitario().multiply(BigDecimal.valueOf(v.getCantidad())));
        }
        return total.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public List<String> getNombresProductos() {
        List<String> nombres = new ArrayList<>();
        for (Venta v : lineas) {
            nombres.add(v.getProducto().getNombre());
        }
        return nombres;
    }

    public List<Integer> getIdsLineas() {
        List<Integer> ids = new ArrayList<>();
        for (Venta v : lineas) {
            ids.add(v.getId());
        }
        return ids;
    }
}
