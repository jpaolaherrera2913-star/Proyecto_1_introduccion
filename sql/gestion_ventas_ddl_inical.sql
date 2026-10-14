CREATE DATABASE IF NOT EXISTS gestion_ventas;

USE gestion_ventas;

-- Catálogo de tipos de cliente
CREATE TABLE tipo_cliente (
    id      INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre  VARCHAR(50)  NOT NULL,
    CONSTRAINT pk_tipo_cliente PRIMARY KEY (id)
);

-- Catálogo de métodos de pago
CREATE TABLE metodo_pago (
    id      INT UNSIGNED NOT NULL AUTO_INCREMENT,
    nombre  VARCHAR(50)  NOT NULL,
    CONSTRAINT pk_metodo_pago PRIMARY KEY (id)
);

-- Clientes
CREATE TABLE cliente (
    id               INT UNSIGNED     NOT NULL AUTO_INCREMENT,
    nombre           VARCHAR(100)     NOT NULL,
    edad             TINYINT UNSIGNED NULL,
    tipo_cliente_id  INT UNSIGNED     NOT NULL,
    CONSTRAINT pk_cliente PRIMARY KEY (id),
    CONSTRAINT fk_cliente_tipo_cliente FOREIGN KEY (tipo_cliente_id) REFERENCES tipo_cliente (id) ON UPDATE CASCADE ON DELETE RESTRICT
);