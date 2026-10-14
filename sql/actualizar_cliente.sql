USE gestion_ventas;
ALTER TABLE cliente DROP FOREIGN KEY fk_cliente_metodo_pago;
ALTER TABLE cliente DROP COLUMN metodo_pago_id;
