USE gestion_ventas;
CREATE TABLE producto (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    cantidad INT UNSIGNED NOT NULL DEFAULT 0,
    precio_unitario DECIMAL(10,2) NOT NULL,

    CONSTRAINT pk_producto PRIMARY KEY (id)
);




CREATE TABLE venta (
    id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    cliente_id INT UNSIGNED NOT NULL,
    producto_id INT UNSIGNED NOT NULL,
    cantidad INT UNSIGNED NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    metodo_pago_id INT UNSIGNED NOT NULL,

    CONSTRAINT pk_venta PRIMARY KEY (id),

    CONSTRAINT fk_venta_cliente
        FOREIGN KEY (cliente_id)
        REFERENCES cliente (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_venta_producto
        FOREIGN KEY (producto_id)
        REFERENCES producto (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    CONSTRAINT fk_venta_metodo_pago
        FOREIGN KEY (metodo_pago_id)
        REFERENCES metodo_pago (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);
