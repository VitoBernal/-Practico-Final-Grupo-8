-- cupones para probar
INSERT INTO cupones (codigo, porcentaje_descuento, fecha_vencimiento, usado) VALUES ('DESC10', 10, '2099-12-31', false);
INSERT INTO cupones (codigo, porcentaje_descuento, fecha_vencimiento, usado) VALUES ('VENC20', 20, '2020-01-01', false);
 
-- datos de ejemplo para probar la api desde swagger apenas arranca la aplicacion
INSERT INTO categorias (id, nombre) VALUES (1, 'Herramientas');
INSERT INTO categorias (id, nombre) VALUES (2, 'Pintura');
INSERT INTO categorias (id, nombre) VALUES (3, 'Electricidad');
 
INSERT INTO productos (id, nombre, descripcion, marca, categoria_id, stock, stock_critico, precio_base) VALUES (1, 'Taladro percutor', 'Taladro percutor 750W', 'Bosch', 1, 15, 5, 50000);
INSERT INTO productos (id, nombre, descripcion, marca, categoria_id, stock, stock_critico, precio_base) VALUES (2, 'Amoladora angular', 'Amoladora 115mm 850W', 'Makita', 1, 8, 5, 80000);
INSERT INTO productos (id, nombre, descripcion, marca, categoria_id, stock, stock_critico, precio_base) VALUES (3, 'Pintura latex 20L', 'Latex interior blanco', 'Alba', 2, 4, 5, 30000);
INSERT INTO productos (id, nombre, descripcion, marca, categoria_id, stock, stock_critico, precio_base) VALUES (4, 'Rodillo', 'Rodillo de lana 23cm', 'Alba', 2, 30, 5, 5000);
INSERT INTO productos (id, nombre, descripcion, marca, categoria_id, stock, stock_critico, precio_base) VALUES (5, 'Cable 2.5mm', 'Rollo de cable de 100 metros', 'Prysmian', 3, 12, 5, 45000);
INSERT INTO productos (id, nombre, descripcion, marca, categoria_id, stock, stock_critico, precio_base) VALUES (6, 'Lampara LED', 'Lampara LED 12W luz fria', 'Philips', 3, 50, 5, 2500);

-- el cliente 4 esta inhabilitado para probar que no puede comprar
INSERT INTO clientes (id, tipo, numero_identificacion, nombre, apellido, razon_social, telefono, email, direccion, habilitado) VALUES (1, 'PARTICULAR', '31222333', 'Carla', 'Mendez', NULL, '3884111111', 'carla.mendez@mail.com', 'Belgrano 100', true);
INSERT INTO clientes (id, tipo, numero_identificacion, nombre, apellido, razon_social, telefono, email, direccion, habilitado) VALUES (2, 'EMPRESA', '30712345678', NULL, NULL, 'Constructora Norte SA', '3884222222', 'contacto@norte.com', 'Ruta 34 km 5', true);
INSERT INTO clientes (id, tipo, numero_identificacion, nombre, apellido, razon_social, telefono, email, direccion, habilitado) VALUES (3, 'PARTICULAR', '27333444', 'Luis', 'Diaz', NULL, '3884333333', 'luis.diaz@mail.com', 'Lavalle 250', true);
INSERT INTO clientes (id, tipo, numero_identificacion, nombre, apellido, razon_social, telefono, email, direccion, habilitado) VALUES (4, 'PARTICULAR', '33444555', 'Marta', 'Ruiz', NULL, '3884444444', 'marta.ruiz@mail.com', 'Senador Perez 50', false);
