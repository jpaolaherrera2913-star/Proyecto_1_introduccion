# Base de datos

Para una base nueva, ejecutar primero `gestion_ventas_ddl_inical.sql` y luego `ventas.sql`.
La copia de ventas.sql corrige el texto accidental situado despues de AUTO_INCREMENT.
No ejecutar estos CREATE TABLE sobre tablas existentes ni borrar datos para actualizar el codigo.

La conexion se configura en src/main/resources/META-INF/persistence.xml.
Se conserva la configuracion existente: localhost:3307 y base gestion_ventas.
Hibernate valida las tablas; no las crea ni modifica.

Los catalogos tipo_cliente y metodo_pago y los productos deben tener datos para registrar clientes y ventas.
Los archivos originales no incluyen INSERT; no se agregan datos de ejemplo automaticamente.
Cada producto del resumen genera una fila en venta, con el metodo de pago seleccionado para la venta.
Se guarda el precio del producto al agregarlo y se descuentan existencias al confirmar.
Todas las filas se guardan en una transaccion; un error revierte la venta completa.
No se aplican descuentos porque el esquema no guarda ese concepto.
Las opciones de consultas, estadisticas y cierre de caja siguen pendientes de implementacion.

Si la base existente aun tiene cliente.metodo_pago_id, ejecutar actualizar_cliente.sql una sola vez. No ejecutarlo si ya se elimino la columna.
