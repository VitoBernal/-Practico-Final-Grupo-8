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
