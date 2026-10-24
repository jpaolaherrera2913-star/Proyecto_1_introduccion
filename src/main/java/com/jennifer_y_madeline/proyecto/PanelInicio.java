package com.jennifer_y_madeline.proyecto;

import com.jennifer_y_madeline.proyecto.databases.Cliente;
import com.jennifer_y_madeline.proyecto.databases.ClienteDAO;
import com.jennifer_y_madeline.proyecto.databases.VentaDAO;
import com.jennifer_y_madeline.proyecto.databases.CierreCajaDAO;
import com.jennifer_y_madeline.proyecto.databases.MetodoPago;
import com.jennifer_y_madeline.proyecto.databases.Producto;
import com.jennifer_y_madeline.proyecto.databases.ProductoDAO;
import com.jennifer_y_madeline.proyecto.databases.Venta;
import com.jennifer_y_madeline.proyecto.databases.VentaResumen;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.SwingWorker;

/**
 *
 * @author jpaol
 */
public class PanelInicio extends javax.swing.JPanel {
    private final ClienteDAO dao = new ClienteDAO(); 
    private final VentaDAO ventaDAO = new VentaDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    /** Productos con esta cantidad (o menos) se consideran "stock bajo". */
    private static final int UMBRAL_STOCK_BAJO = 5;

    private static final java.awt.Color VERDE = new java.awt.Color(46, 125, 50);
    private static final java.awt.Color ROJO = new java.awt.Color(176, 42, 42);


    public PanelInicio() {
        initComponents();
        Imagenes.ponerPerroYGato(jLabel3, 110);
        cargarTotalClientes();
        recargarVentasDelDia();
        cargarEstadoCaja();
        cargarIndicadores();
    }

    private void cargarEstadoCaja() {
        lblNumVentas2.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        new javax.swing.SwingWorker<CierreCajaDAO.Cierre, Void>() {
            @Override
            protected CierreCajaDAO.Cierre doInBackground() {
                return new CierreCajaDAO().buscar(LocalDate.now());
            }

            @Override
            protected void done() {
                CierreCajaDAO.Cierre cierre = null;
                try {
                    cierre = get();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                boolean cerrada = cierre != null;
                lblNumVentas1.setText(cerrada ? "Cerrada" : "Abierta");
                lblNumVentas1.setForeground(cerrada ? new java.awt.Color(176, 42, 42) : new java.awt.Color(46, 125, 50));
                jLabel7.setText(cerrada ? "🔒" : "🔓");
                lblNumVentas3.setText(cerrada && cierre.horaCierre() != null
                        ? cierre.horaCierre().format(DateTimeFormatter.ofPattern("HH:mm")) : "Pendiente");
            }
        }.execute();
    }

    public void actualizarVentas(long cantidad) {
        lblNumVentas.setText(String.valueOf(cantidad));
    }

    public void recargarVentasDelDia() {
        new SwingWorker<Long, Void>() {
            @Override
            protected Long doInBackground() {
                return ventaDAO.contarVentasDelDia();
            }
            @Override
            protected void done() {
                try {
                    actualizarVentas(get());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    actualizarVentas(0); 
                }
            }
        }.execute();
    }
        public void actualizarClientes(int cantidad) {
        lblNumClientes.setText(String.valueOf(cantidad));
    }
        private void cargarTotalClientes() {
        new SwingWorker<List<Cliente>, Void>() {
            @Override
            protected List<Cliente> doInBackground() {
                return dao.listar();
            }
            @Override
            protected void done() {
                try {
                    List<Cliente> lista = get();
                    actualizarClientes(lista.size()); 
                } catch (Exception ex) {
                    actualizarClientes(0); 
                }
            }
        }.execute();
    }

    private static final class Indicadores {
        int ventasHoy = 0;                              
        int unidadesHoy = 0;                            
        BigDecimal totalHoy = BigDecimal.ZERO;          
        BigDecimal promedioHoy = BigDecimal.ZERO;       
        BigDecimal ventaMayor = null;                   
        BigDecimal ventaMenor = null;                  
        String productoTop = "";
        int unidadesTop = 0;
        String metodoTop = "";
        int ventasMetodoTop = 0;
        int totalProductos = 0;
        int unidadesInventario = 0;
        int stockBajo = 0;
        int agotados = 0;
        String masBajo = "";
        int cantidadMasBajo = 0;
    }

    private void cargarIndicadores() {
        new SwingWorker<Indicadores, Void>() {
            @Override
            protected Indicadores doInBackground() {
                return calcularIndicadores(ventaDAO.listarResumenesDelDia(LocalDate.now()),
                        productoDAO.listar());
            }

            @Override
            protected void done() {
                try {
                    mostrarIndicadores(get());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    mostrarIndicadores(new Indicadores());
                }
            }
        }.execute();
    }

    private Indicadores calcularIndicadores(List<VentaResumen> ventasHoy, List<Producto> productos) {
        Indicadores d = new Indicadores();
        Map<String, Integer> unidadesPorProducto = new LinkedHashMap<>();
        Map<String, Integer> ventasPorMetodo = new LinkedHashMap<>();

        for (VentaResumen v : ventasHoy) {
            BigDecimal totalVenta = totalFinal(v);
            d.ventasHoy++;
            d.totalHoy = d.totalHoy.add(totalVenta);
            if (d.ventaMayor == null || totalVenta.compareTo(d.ventaMayor) > 0) {
                d.ventaMayor = totalVenta;
            }
            if (d.ventaMenor == null || totalVenta.compareTo(d.ventaMenor) < 0) {
                d.ventaMenor = totalVenta;
            }
            for (Venta linea : v.getLineas()) {
                d.unidadesHoy += linea.getCantidad();
                unidadesPorProducto.merge(linea.getProducto().getNombre(), linea.getCantidad(), Integer::sum);
            }
            MetodoPago pago = v.getMetodoPago();
            ventasPorMetodo.merge(pago == null ? "Sin método" : pago.getNombre(), 1, Integer::sum);
        }

        if (d.ventasHoy > 0) {
            d.promedioHoy = d.totalHoy.divide(BigDecimal.valueOf(d.ventasHoy), 2, RoundingMode.HALF_UP);
        }

        for (Map.Entry<String, Integer> e : unidadesPorProducto.entrySet()) {
            if (e.getValue() > d.unidadesTop) {
                d.unidadesTop = e.getValue();
                d.productoTop = e.getKey();
            }
        }
        for (Map.Entry<String, Integer> e : ventasPorMetodo.entrySet()) {
            if (e.getValue() > d.ventasMetodoTop) {
                d.ventasMetodoTop = e.getValue();
                d.metodoTop = e.getKey();
            }
        }

        boolean primero = true;
        for (Producto p : productos) {
            d.totalProductos++;
            d.unidadesInventario += p.getCantidad();
            if (p.getCantidad() == 0) {
                d.agotados++;
            }
            if (p.getCantidad() <= UMBRAL_STOCK_BAJO) {
                d.stockBajo++;
            }
            if (primero || p.getCantidad() < d.cantidadMasBajo) {
                d.cantidadMasBajo = p.getCantidad();
                d.masBajo = p.getNombre();
                primero = false;
            }
        }
        return d;
    }

    private void mostrarIndicadores(Indicadores d) {
        boolean hayVentas = d.ventasHoy > 0;
        lblTotalHoy.setText(q(d.totalHoy));
        lblPromedioHoy.setText(hayVentas ? q(d.promedioHoy) : "—");
        lblMayorHoy.setText(hayVentas ? q(d.ventaMayor) : "—");
        lblMenorHoy.setText(hayVentas ? q(d.ventaMenor) : "—");

        lblInventario.setText(d.unidadesInventario + (d.unidadesInventario == 1 ? " unidad" : " unidades"));
        lblInventario.setToolTipText(d.totalProductos + (d.totalProductos == 1 ? " producto distinto" : " productos distintos")
                + " en existencia");

        if (d.stockBajo == 0) {
            lblStockBajo.setText("Todo en orden");
            lblStockBajo.setForeground(VERDE);
            lblStockBajo.setToolTipText("Ningún producto tiene " + UMBRAL_STOCK_BAJO + " unidades o menos.");
        } else {
            lblStockBajo.setText(d.stockBajo + (d.stockBajo == 1 ? " producto" : " productos"));
            lblStockBajo.setForeground(ROJO);
            lblStockBajo.setToolTipText("Agotados: " + d.agotados + ". Más bajo: " + d.masBajo
                    + " (" + d.cantidadMasBajo + (d.cantidadMasBajo == 1 ? " unidad)" : " unidades)"));
        }

        if (hayVentas && d.unidadesTop > 0) {
            lblProductoTop.setText(recortar(d.productoTop, 14));
            lblProductoTop.setToolTipText(d.productoTop + " (" + d.unidadesTop
                    + (d.unidadesTop == 1 ? " unidad)" : " unidades)"));
            lblMetodoTop.setText(recortar(d.metodoTop, 14));
            lblMetodoTop.setToolTipText(d.metodoTop + " (" + d.ventasMetodoTop
                    + (d.ventasMetodoTop == 1 ? " venta)" : " ventas)"));
        } else {
            lblProductoTop.setText("Sin ventas hoy");
            lblMetodoTop.setText("Sin ventas hoy");
        }
    }

   
    private BigDecimal totalFinal(VentaResumen v) {
        BigDecimal descuento = BigDecimal.ZERO;
        BigDecimal recargo = BigDecimal.ZERO;
        for (Venta l : v.getLineas()) {
            if (l.getDescuento() != null) {
                descuento = descuento.add(l.getDescuento());
            }
            if (l.getRecargo() != null) {
                recargo = recargo.add(l.getRecargo());
            }
        }
        return v.getTotal().subtract(descuento).add(recargo);
    }

    private String recortar(String texto, int max) {
        return texto.length() <= max ? texto : texto.substring(0, max - 1) + "…";
    }

    private String q(BigDecimal monto) {
        return String.format(Locale.US, "Q%,.2f", monto);
    }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        lblNumClientes = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblNumVentas = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblNumVentas1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel10 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        Fecha = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lblNumVentas2 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        Fecha1 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        lblNumVentas3 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        tarjetaTotal = new javax.swing.JPanel();
        icoTotal = new javax.swing.JLabel();
        lblTituloTotal = new javax.swing.JLabel();
        lblTotalHoy = new javax.swing.JLabel();
        tarjetaPromedio = new javax.swing.JPanel();
        icoPromedio = new javax.swing.JLabel();
        lblTituloPromedio = new javax.swing.JLabel();
        lblPromedioHoy = new javax.swing.JLabel();
        tarjetaMayor = new javax.swing.JPanel();
        icoMayor = new javax.swing.JLabel();
        lblTituloMayor = new javax.swing.JLabel();
        lblMayorHoy = new javax.swing.JLabel();
        tarjetaMenor = new javax.swing.JPanel();
        icoMenor = new javax.swing.JLabel();
        lblTituloMenor = new javax.swing.JLabel();
        lblMenorHoy = new javax.swing.JLabel();
        tarjetaInventario = new javax.swing.JPanel();
        icoInventario = new javax.swing.JLabel();
        lblTituloInventario = new javax.swing.JLabel();
        lblInventario = new javax.swing.JLabel();
        tarjetaStockBajo = new javax.swing.JPanel();
        icoStockBajo = new javax.swing.JLabel();
        lblTituloStockBajo = new javax.swing.JLabel();
        lblStockBajo = new javax.swing.JLabel();
        tarjetaTop = new javax.swing.JPanel();
        icoTop = new javax.swing.JLabel();
        lblTituloTop = new javax.swing.JLabel();
        lblProductoTop = new javax.swing.JLabel();
        tarjetaPago = new javax.swing.JPanel();
        icoPago = new javax.swing.JLabel();
        lblTituloPago = new javax.swing.JLabel();
        lblMetodoTop = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(940, 800));

        jPanel1.setBackground(new java.awt.Color(255, 251, 250));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(116, 96, 153));
        jLabel1.setText("Panel Principal");

        jPanel2.setBackground(new java.awt.Color(248, 246, 252));
        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Total de Clientes");

        jLabel34.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 26));
        jLabel34.setForeground(new java.awt.Color(116, 96, 153));
        jLabel34.setText("👥");

        lblNumClientes.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblNumClientes.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, 0, 136, Short.MAX_VALUE)
                    .addComponent(lblNumClientes, 0, 100, Short.MAX_VALUE))
                .addGap(8, 8, 8))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel34)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(4, 4, 4)
                        .addComponent(lblNumClientes)))
                .addGap(10, 10, 10))
        );

        jPanel3.setBackground(new java.awt.Color(248, 246, 252));
        jPanel3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, java.awt.Color.lightGray, null, null));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setText("Ventas del Día");

        jLabel5.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 26));
        jLabel5.setForeground(new java.awt.Color(116, 96, 153));
        jLabel5.setText("  💵");

        lblNumVentas.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, 0, 136, Short.MAX_VALUE)
                    .addComponent(lblNumVentas, 0, 136, Short.MAX_VALUE))
                .addGap(8, 8, 8))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(4, 4, 4)
                        .addComponent(lblNumVentas)))
                .addGap(10, 10, 10))
        );

        jPanel4.setBackground(new java.awt.Color(248, 246, 252));
        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        jLabel6.setText("Estado de caja");

        jLabel7.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 26
        ));
        jLabel7.setForeground(new java.awt.Color(116, 96, 153));
        jLabel7.setText("🔒");

        lblNumVentas1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, 0, 136, Short.MAX_VALUE)
                    .addComponent(lblNumVentas1, 0, 136, Short.MAX_VALUE))
                .addGap(8, 8, 8))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(4, 4, 4)
                        .addComponent(lblNumVentas1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel3.setText("imagen perroygato");

        jLabel8.setForeground(new java.awt.Color(116, 96, 153));
        jLabel8.setText("Registro de ventas");

        jLabel9.setForeground(new java.awt.Color(116, 96, 153));
        jLabel9.setText("Registro de clientes");

        jLabel10.setForeground(new java.awt.Color(116, 96, 153));
        jLabel10.setText("Cierre de caja");

        jPanel6.setBackground(new java.awt.Color(248, 246, 252));
        jPanel6.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        Fecha.setText("Fecha");

        jLabel14.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 26));
        jLabel14.setForeground(new java.awt.Color(116, 96, 153));
        jLabel14.setText("📅");

        lblNumVentas2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Fecha, 0, 136, Short.MAX_VALUE)
                    .addComponent(lblNumVentas2, 0, 100, Short.MAX_VALUE))
                .addGap(8, 8, 8))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(Fecha)
                        .addGap(4, 4, 4)
                        .addComponent(lblNumVentas2)))
                .addGap(10, 10, 10))
        );

        jPanel7.setBackground(new java.awt.Color(248, 246, 252));
        jPanel7.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        Fecha1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Fecha1.setText("Hora de cierre");

        jLabel15.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.BOLD, 26));
        jLabel15.setForeground(new java.awt.Color(116, 96, 153));
        jLabel15.setText("🕐");

        lblNumVentas3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Fecha1, 0, 136, Short.MAX_VALUE)
                    .addComponent(lblNumVentas3, 0, 100, Short.MAX_VALUE))
                .addGap(8, 8, 8))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(Fecha1)
                        .addGap(4, 4, 4)
                        .addComponent(lblNumVentas3)
                        .addGap(10, 10, 10))))
        );

        jLabel11.setForeground(new java.awt.Color(116, 96, 153));
        jLabel11.setText("Resumen financiero de hoy");

        jLabel12.setForeground(new java.awt.Color(116, 96, 153));
        jLabel12.setText("Inventario y tendencias del día");

        tarjetaTotal.setBackground(new java.awt.Color(248, 246, 252));
        tarjetaTotal.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        icoTotal.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 26));
        icoTotal.setForeground(new java.awt.Color(116, 96, 153));
        icoTotal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        icoTotal.setText("💰");

        lblTituloTotal.setText("Total vendido");

        lblTotalHoy.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTotalHoy.setText("...");

        javax.swing.GroupLayout tarjetaTotalLayout = new javax.swing.GroupLayout(tarjetaTotal);
        tarjetaTotal.setLayout(tarjetaTotalLayout);
        tarjetaTotalLayout.setHorizontalGroup(
            tarjetaTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tarjetaTotalLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(icoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(tarjetaTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloTotal, 0, 106, Short.MAX_VALUE)
                    .addComponent(lblTotalHoy, 0, 100, Short.MAX_VALUE))
                .addGap(6, 6, 6))
        );
        tarjetaTotalLayout.setVerticalGroup(
            tarjetaTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tarjetaTotalLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(tarjetaTotalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(icoTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tarjetaTotalLayout.createSequentialGroup()
                        .addComponent(lblTituloTotal)
                        .addGap(2, 2, 2)
                        .addComponent(lblTotalHoy)))
                .addGap(8, 8, 8))
        );

        tarjetaPromedio.setBackground(new java.awt.Color(248, 246, 252));
        tarjetaPromedio.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        icoPromedio.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 26));
        icoPromedio.setForeground(new java.awt.Color(116, 96, 153));
        icoPromedio.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        icoPromedio.setText("📈");

        lblTituloPromedio.setText("Promedio");

        lblPromedioHoy.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblPromedioHoy.setText("...");

        javax.swing.GroupLayout tarjetaPromedioLayout = new javax.swing.GroupLayout(tarjetaPromedio);
        tarjetaPromedio.setLayout(tarjetaPromedioLayout);
        tarjetaPromedioLayout.setHorizontalGroup(
            tarjetaPromedioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tarjetaPromedioLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(icoPromedio, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(tarjetaPromedioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloPromedio, 0, 106, Short.MAX_VALUE)
                    .addComponent(lblPromedioHoy, 0, 100, Short.MAX_VALUE))
                .addGap(6, 6, 6))
        );
        tarjetaPromedioLayout.setVerticalGroup(
            tarjetaPromedioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tarjetaPromedioLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(tarjetaPromedioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(icoPromedio, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tarjetaPromedioLayout.createSequentialGroup()
                        .addComponent(lblTituloPromedio)
                        .addGap(2, 2, 2)
                        .addComponent(lblPromedioHoy)))
                .addGap(8, 8, 8))
        );

        tarjetaMayor.setBackground(new java.awt.Color(248, 246, 252));
        tarjetaMayor.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        icoMayor.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 26));
        icoMayor.setForeground(new java.awt.Color(116, 96, 153));
        icoMayor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        icoMayor.setText("⬆️");

        lblTituloMayor.setText("Venta mayor");

        lblMayorHoy.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMayorHoy.setText("...");

        javax.swing.GroupLayout tarjetaMayorLayout = new javax.swing.GroupLayout(tarjetaMayor);
        tarjetaMayor.setLayout(tarjetaMayorLayout);
        tarjetaMayorLayout.setHorizontalGroup(
            tarjetaMayorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tarjetaMayorLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(icoMayor, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(tarjetaMayorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloMayor, 0, 106, Short.MAX_VALUE)
                    .addComponent(lblMayorHoy, 0, 100, Short.MAX_VALUE))
                .addGap(6, 6, 6))
        );
        tarjetaMayorLayout.setVerticalGroup(
            tarjetaMayorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tarjetaMayorLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(tarjetaMayorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(icoMayor, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tarjetaMayorLayout.createSequentialGroup()
                        .addComponent(lblTituloMayor)
                        .addGap(2, 2, 2)
                        .addComponent(lblMayorHoy)))
                .addGap(8, 8, 8))
        );

        tarjetaMenor.setBackground(new java.awt.Color(248, 246, 252));
        tarjetaMenor.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        icoMenor.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 26));
        icoMenor.setForeground(new java.awt.Color(116, 96, 153));
        icoMenor.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        icoMenor.setText("⬇️");

        lblTituloMenor.setText("Venta menor");

        lblMenorHoy.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMenorHoy.setText("...");

        javax.swing.GroupLayout tarjetaMenorLayout = new javax.swing.GroupLayout(tarjetaMenor);
        tarjetaMenor.setLayout(tarjetaMenorLayout);
        tarjetaMenorLayout.setHorizontalGroup(
            tarjetaMenorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tarjetaMenorLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(icoMenor, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(tarjetaMenorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloMenor, 0, 106, Short.MAX_VALUE)
                    .addComponent(lblMenorHoy, 0, 100, Short.MAX_VALUE))
                .addGap(6, 6, 6))
        );
        tarjetaMenorLayout.setVerticalGroup(
            tarjetaMenorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tarjetaMenorLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(tarjetaMenorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(icoMenor, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tarjetaMenorLayout.createSequentialGroup()
                        .addComponent(lblTituloMenor)
                        .addGap(2, 2, 2)
                        .addComponent(lblMenorHoy)))
                .addGap(8, 8, 8))
        );

        tarjetaInventario.setBackground(new java.awt.Color(248, 246, 252));
        tarjetaInventario.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        icoInventario.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 26));
        icoInventario.setForeground(new java.awt.Color(116, 96, 153));
        icoInventario.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        icoInventario.setText("📦");

        lblTituloInventario.setText("Inventario");

        lblInventario.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblInventario.setText("...");

        javax.swing.GroupLayout tarjetaInventarioLayout = new javax.swing.GroupLayout(tarjetaInventario);
        tarjetaInventario.setLayout(tarjetaInventarioLayout);
        tarjetaInventarioLayout.setHorizontalGroup(
            tarjetaInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tarjetaInventarioLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(icoInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(tarjetaInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloInventario, 0, 106, Short.MAX_VALUE)
                    .addComponent(lblInventario, 0, 100, Short.MAX_VALUE))
                .addGap(6, 6, 6))
        );
        tarjetaInventarioLayout.setVerticalGroup(
            tarjetaInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tarjetaInventarioLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(tarjetaInventarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(icoInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tarjetaInventarioLayout.createSequentialGroup()
                        .addComponent(lblTituloInventario)
                        .addGap(2, 2, 2)
                        .addComponent(lblInventario)))
                .addGap(8, 8, 8))
        );

        tarjetaStockBajo.setBackground(new java.awt.Color(248, 246, 252));
        tarjetaStockBajo.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        icoStockBajo.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 26));
        icoStockBajo.setForeground(new java.awt.Color(116, 96, 153));
        icoStockBajo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        icoStockBajo.setText("⚠️");

        lblTituloStockBajo.setText("Stock bajo");

        lblStockBajo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblStockBajo.setText("...");

        javax.swing.GroupLayout tarjetaStockBajoLayout = new javax.swing.GroupLayout(tarjetaStockBajo);
        tarjetaStockBajo.setLayout(tarjetaStockBajoLayout);
        tarjetaStockBajoLayout.setHorizontalGroup(
            tarjetaStockBajoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tarjetaStockBajoLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(icoStockBajo, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(tarjetaStockBajoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloStockBajo, 0, 106, Short.MAX_VALUE)
                    .addComponent(lblStockBajo, 0, 100, Short.MAX_VALUE))
                .addGap(6, 6, 6))
        );
        tarjetaStockBajoLayout.setVerticalGroup(
            tarjetaStockBajoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tarjetaStockBajoLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(tarjetaStockBajoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(icoStockBajo, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tarjetaStockBajoLayout.createSequentialGroup()
                        .addComponent(lblTituloStockBajo)
                        .addGap(2, 2, 2)
                        .addComponent(lblStockBajo)))
                .addGap(8, 8, 8))
        );

        tarjetaTop.setBackground(new java.awt.Color(248, 246, 252));
        tarjetaTop.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        icoTop.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 26));
        icoTop.setForeground(new java.awt.Color(116, 96, 153));
        icoTop.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        icoTop.setText("🏆");

        lblTituloTop.setText("Más vendido");

        lblProductoTop.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblProductoTop.setText("...");

        javax.swing.GroupLayout tarjetaTopLayout = new javax.swing.GroupLayout(tarjetaTop);
        tarjetaTop.setLayout(tarjetaTopLayout);
        tarjetaTopLayout.setHorizontalGroup(
            tarjetaTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tarjetaTopLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(icoTop, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(tarjetaTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloTop, 0, 106, Short.MAX_VALUE)
                    .addComponent(lblProductoTop, 0, 100, Short.MAX_VALUE))
                .addGap(6, 6, 6))
        );
        tarjetaTopLayout.setVerticalGroup(
            tarjetaTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tarjetaTopLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(tarjetaTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(icoTop, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tarjetaTopLayout.createSequentialGroup()
                        .addComponent(lblTituloTop)
                        .addGap(2, 2, 2)
                        .addComponent(lblProductoTop)))
                .addGap(8, 8, 8))
        );

        tarjetaPago.setBackground(new java.awt.Color(248, 246, 252));
        tarjetaPago.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, null, new java.awt.Color(204, 204, 204), null, null));

        icoPago.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 26));
        icoPago.setForeground(new java.awt.Color(116, 96, 153));
        icoPago.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        icoPago.setText("💳");

        lblTituloPago.setText("Pago más usado");

        lblMetodoTop.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMetodoTop.setText("...");

        javax.swing.GroupLayout tarjetaPagoLayout = new javax.swing.GroupLayout(tarjetaPago);
        tarjetaPago.setLayout(tarjetaPagoLayout);
        tarjetaPagoLayout.setHorizontalGroup(
            tarjetaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tarjetaPagoLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(icoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addGroup(tarjetaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloPago, 0, 106, Short.MAX_VALUE)
                    .addComponent(lblMetodoTop, 0, 100, Short.MAX_VALUE))
                .addGap(6, 6, 6))
        );
        tarjetaPagoLayout.setVerticalGroup(
            tarjetaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tarjetaPagoLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(tarjetaPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(icoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(tarjetaPagoLayout.createSequentialGroup()
                        .addComponent(lblTituloPago)
                        .addGap(2, 2, 2)
                        .addComponent(lblMetodoTop)))
                .addGap(8, 8, 8))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 728, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 728, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 728, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tarjetaTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(tarjetaPromedio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(tarjetaMayor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(tarjetaMenor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 728, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tarjetaInventario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(tarjetaStockBajo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(tarjetaTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(tarjetaPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(26, 26, 26))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jLabel1)
                        .addGap(17, 17, 17)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addGap(12, 12, 12)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(jLabel11)
                        .addGap(8, 8, 8)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tarjetaTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tarjetaPromedio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tarjetaMayor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tarjetaMenor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(jLabel12)
                        .addGap(8, 8, 8)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tarjetaInventario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tarjetaStockBajo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tarjetaTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tarjetaPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel3)))
                .addGap(31, 31, 31))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Fecha;
    private javax.swing.JLabel Fecha1;
    private javax.swing.JLabel icoInventario;
    private javax.swing.JLabel icoMayor;
    private javax.swing.JLabel icoMenor;
    private javax.swing.JLabel icoPago;
    private javax.swing.JLabel icoPromedio;
    private javax.swing.JLabel icoStockBajo;
    private javax.swing.JLabel icoTop;
    private javax.swing.JLabel icoTotal;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JLabel lblInventario;
    private javax.swing.JLabel lblMayorHoy;
    private javax.swing.JLabel lblMenorHoy;
    private javax.swing.JLabel lblMetodoTop;
    private javax.swing.JLabel lblNumClientes;
    private javax.swing.JLabel lblNumVentas;
    private javax.swing.JLabel lblNumVentas1;
    private javax.swing.JLabel lblNumVentas2;
    private javax.swing.JLabel lblNumVentas3;
    private javax.swing.JLabel lblProductoTop;
    private javax.swing.JLabel lblPromedioHoy;
    private javax.swing.JLabel lblStockBajo;
    private javax.swing.JLabel lblTituloInventario;
    private javax.swing.JLabel lblTituloMayor;
    private javax.swing.JLabel lblTituloMenor;
    private javax.swing.JLabel lblTituloPago;
    private javax.swing.JLabel lblTituloPromedio;
    private javax.swing.JLabel lblTituloStockBajo;
    private javax.swing.JLabel lblTituloTop;
    private javax.swing.JLabel lblTituloTotal;
    private javax.swing.JLabel lblTotalHoy;
    private javax.swing.JPanel tarjetaInventario;
    private javax.swing.JPanel tarjetaMayor;
    private javax.swing.JPanel tarjetaMenor;
    private javax.swing.JPanel tarjetaPago;
    private javax.swing.JPanel tarjetaPromedio;
    private javax.swing.JPanel tarjetaStockBajo;
    private javax.swing.JPanel tarjetaTop;
    private javax.swing.JPanel tarjetaTotal;
    // End of variables declaration//GEN-END:variables
}
