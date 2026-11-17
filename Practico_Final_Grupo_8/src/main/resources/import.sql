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

-- venta 1: financiada hace 3 meses, la cuota 1 esta pagada y la cuota 2 ya vencio sin pagar
INSERT INTO ventas (id, modalidad, codigo_comprobante, fecha, cliente_id, subtotal, total, estado, porcentaje_recargo, monto_recargo) VALUES (1, 'FINANCIADA', 'FIN-1', DATE_SUB(CURDATE(), INTERVAL 3 MONTH), 3, 80000, 96000, 'PENDIENTE', 20, 16000);
INSERT INTO detalles_venta (id, venta_id, producto_id, cantidad, precio_base_aplicado, subtotal) VALUES (1, 1, 2, 1, 80000, 80000);
INSERT INTO cuotas (venta_id, numero, importe, fecha_vencimiento, estado) VALUES (1, 1, 16000, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 3 MONTH), INTERVAL 1 MONTH), 'PAGADA');
INSERT INTO cuotas (venta_id, numero, importe, fecha_vencimiento, estado) VALUES (1, 2, 16000, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 3 MONTH), INTERVAL 2 MONTH), 'ADEUDADA');
INSERT INTO cuotas (venta_id, numero, importe, fecha_vencimiento, estado) VALUES (1, 3, 16000, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 3 MONTH), INTERVAL 3 MONTH), 'ADEUDADA');
INSERT INTO cuotas (venta_id, numero, importe, fecha_vencimiento, estado) VALUES (1, 4, 16000, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 3 MONTH), INTERVAL 4 MONTH), 'ADEUDADA');
INSERT INTO cuotas (venta_id, numero, importe, fecha_vencimiento, estado) VALUES (1, 5, 16000, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 3 MONTH), INTERVAL 5 MONTH), 'ADEUDADA');
INSERT INTO cuotas (venta_id, numero, importe, fecha_vencimiento, estado) VALUES (1, 6, 16000, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 3 MONTH), INTERVAL 6 MONTH), 'ADEUDADA');
 
-- venta 2: de contado, ya esta saldada
INSERT INTO ventas (id, modalidad, codigo_comprobante, fecha, cliente_id, subtotal, total, estado, monto_descuento) VALUES (2, 'CONTADO', 'CON-2', DATE_SUB(CURDATE(), INTERVAL 10 DAY), 1, 60000, 60000, 'SALDADA', 0);
INSERT INTO detalles_venta (id, venta_id, producto_id, cantidad, precio_base_aplicado, subtotal) VALUES (2, 2, 1, 1, 50000, 50000);
INSERT INTO detalles_venta (id, venta_id, producto_id, cantidad, precio_base_aplicado, subtotal) VALUES (3, 2, 4, 2, 5000, 10000);
 
-- venta 3: financiada hace 5 dias, todas las cuotas se pueden cobrar
INSERT INTO ventas (id, modalidad, codigo_comprobante, fecha, cliente_id, subtotal, total, estado, porcentaje_recargo, monto_recargo) VALUES (3, 'FINANCIADA', 'FIN-3', DATE_SUB(CURDATE(), INTERVAL 5 DAY), 2, 60000, 66000, 'PENDIENTE', 10, 6000);
INSERT INTO detalles_venta (id, venta_id, producto_id, cantidad, precio_base_aplicado, subtotal) VALUES (4, 3, 3, 2, 30000, 60000);
INSERT INTO cuotas (venta_id, numero, importe, fecha_vencimiento, estado) VALUES (3, 1, 11000, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 5 DAY), INTERVAL 1 MONTH), 'ADEUDADA');
INSERT INTO cuotas (venta_id, numero, importe, fecha_vencimiento, estado) VALUES (3, 2, 11000, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 5 DAY), INTERVAL 2 MONTH), 'ADEUDADA');
INSERT INTO cuotas (venta_id, numero, importe, fecha_vencimiento, estado) VALUES (3, 3, 11000, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 5 DAY), INTERVAL 3 MONTH), 'ADEUDADA');
INSERT INTO cuotas (venta_id, numero, importe, fecha_vencimiento, estado) VALUES (3, 4, 11000, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 5 DAY), INTERVAL 4 MONTH), 'ADEUDADA');
INSERT INTO cuotas (venta_id, numero, importe, fecha_vencimiento, estado) VALUES (3, 5, 11000, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 5 DAY), INTERVAL 5 MONTH), 'ADEUDADA');
INSERT INTO cuotas (venta_id, numero, importe, fecha_vencimiento, estado) VALUES (3, 6, 11000, DATE_ADD(DATE_SUB(CURDATE(), INTERVAL 5 DAY), INTERVAL 6 MONTH), 'ADEUDADA');
