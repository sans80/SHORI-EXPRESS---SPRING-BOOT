USE SHORI_EXPRESS;
DELIMITER $$

-- 1) ROL
CREATE PROCEDURE InsertRol(
    IN p_nombre VARCHAR(50),
    OUT p_id INT
)
BEGIN
    INSERT INTO ROL(nombre_rol) VALUES (p_nombre);
    SET p_id = LAST_INSERT_ID();
END$$

-- 2) USUARIO (usa SHA2 para ejemplo)
CREATE PROCEDURE InsertUsuario(
    IN p_tipo_doc ENUM('CC','TI','PET','PPT','Pasaporte'),
    IN p_documento VARCHAR(10),
    IN p_nombre_usuario VARCHAR(50),
    IN p_contraseña VARCHAR(255),
    IN p_primer_nombre VARCHAR(40),
    IN p_apellido VARCHAR(40),
    IN p_correo VARCHAR(100),
    IN p_telefono VARCHAR(20),
    IN p_direccion VARCHAR(100),
    IN p_estado ENUM('activo','inactivo'),
    IN p_id_rol INT,
    OUT p_id INT
)
BEGIN
    INSERT INTO USUARIO(
        tipo_documento_usuario,
        documento_usuario,
        nombre_usuario,
        contraseña_usuario,
        primer_nombre_usuario,
        apellido_usuario,
        correo_usuario,
        telefono_usuario,
        direccion_usuario,
        estado_usuario,
        id_rol
    )
    VALUES (
        p_tipo_doc,
        p_documento,
        p_nombre_usuario,
        SHA2(p_contraseña,256),
        p_primer_nombre,
        p_apellido,
        p_correo,
        p_telefono,
        p_direccion,
        p_estado,
        p_id_rol
    );
    SET p_id = LAST_INSERT_ID();
END$$

-- 3) MATERIA_PRIMA
CREATE PROCEDURE InsertMateriaPrima(
    IN p_nombre VARCHAR(100),
    IN p_categoria VARCHAR(50),
    IN p_unidad ENUM('kg','g','unidad','ml','l'),
    IN p_descripcion VARCHAR(200),
    IN p_precio DECIMAL(10,2),
    IN p_stock INT,
    IN p_estado ENUM('disponible','reservada','en preparación','agotada','caducada','en mal estado','devuelta','pendiente de ingreso','bloqueada','preparada','en proceso de descongelación'),
    IN p_fecha_vencimiento DATE,
    OUT p_id INT
)
BEGIN
    INSERT INTO MATERIA_PRIMA(
        nombre_materia_prima,
        categoria_materia_prima,
        unidad_medida_materia_prima,
        descripcion_materia_prima,
        precio_materia_prima,
        stock_materia_prima,
        estado_materia_prima,
        fecha_vencimiento_materia_prima
    ) VALUES (
        p_nombre, p_categoria, p_unidad, p_descripcion, p_precio, p_stock, p_estado, p_fecha_vencimiento
    );
    SET p_id = LAST_INSERT_ID();
END$$

-- 4) PRODUCTO_TERMINADO
CREATE PROCEDURE InsertProductoTerminado(
    IN p_cantidad INT,
    IN p_stock INT,
    IN p_nombre VARCHAR(100),
    IN p_descripcion VARCHAR(200),
    IN p_precio DECIMAL(10,2),
    OUT p_id INT
)
BEGIN
    INSERT INTO PRODUCTO_TERMINADO(
        cantidad_producto_terminado,
        stock_producto_terminado,
        nombre_producto_terminado,
        descripcion_producto_terminado,
        precio_venta_producto_terminado
    ) VALUES (
        p_cantidad, p_stock, p_nombre, p_descripcion, p_precio
    );
    SET p_id = LAST_INSERT_ID();
END$$

-- 5) DETALLE_PRODUCTO_FINAL
CREATE PROCEDURE InsertDetalleProductoFinal(
    IN p_id_materia INT,
    IN p_id_producto INT,
    IN p_cantidad_utilizada INT,
    IN p_unidad ENUM('kg','g','unidad','ml','l'),
    IN p_observaciones TEXT,
    OUT p_id INT
)
BEGIN
    INSERT INTO DETALLE_PRODUCTO_FINAL(
        id_materia_prima, id_producto_terminado, cantidad_utilizada, unidad_medida_deta, observaciones_detalle
    ) VALUES (
        p_id_materia, p_id_producto, p_cantidad_utilizada, p_unidad, p_observaciones
    );
    SET p_id = LAST_INSERT_ID();
END$$

-- 6) ENTRADA_INVENTARIO
CREATE PROCEDURE InsertEntradaInventario(
    IN p_cantidad INT,
    IN p_precio_unitario DECIMAL(10,2),
    IN p_tipo ENUM('Entrada','Devolucion'),
    IN p_descripcion VARCHAR(200),
    IN p_id_materia INT,
    IN p_id_usuario INT,
    OUT p_id INT
)
BEGIN
    INSERT INTO ENTRADA_INVENTARIO(
        cantidad_entrada, precio_unitario, tipo_entrada, descripcion_entrada, id_materia_prima, id_usuario
    ) VALUES (
        p_cantidad, p_precio_unitario, p_tipo, p_descripcion, p_id_materia, p_id_usuario
    );
    SET p_id = LAST_INSERT_ID();
    -- actualizamos stock de materia prima
    UPDATE MATERIA_PRIMA
    SET stock_materia_prima = stock_materia_prima + p_cantidad
    WHERE id_materia_prima = p_id_materia;
END$$

-- 7) PEDIDO
CREATE PROCEDURE InsertPedido(
    IN p_descripcion VARCHAR(200),
    IN p_estado ENUM('carrito','pendiente','preparación','en camino','entregado','cancelado'),
    IN p_total DECIMAL(10,2),
    IN p_id_usuario INT,
    OUT p_id INT
)
BEGIN
    INSERT INTO PEDIDO(
        descripcion_pedido, estado_pedido, total_pedido, id_usuario
    ) VALUES (
        p_descripcion, p_estado, p_total, p_id_usuario
    );
    SET p_id = LAST_INSERT_ID();
END$$

-- 8) DETALLE_PEDIDO
CREATE PROCEDURE InsertDetallePedido(
    IN p_id_pedido INT,
    IN p_id_producto INT,
    IN p_cantidad INT,
    OUT p_id INT
)
BEGIN
    INSERT INTO DETALLE_PEDIDO(
        id_pedido, id_producto_terminado, cantidad_detalle_pedido
    ) VALUES (
        p_id_pedido, p_id_producto, p_cantidad
    );
    SET p_id = LAST_INSERT_ID();
    -- actualizar total del pedido automáticamente (suma precio_producto * cantidad)
    UPDATE PEDIDO p
    JOIN PRODUCTO_TERMINADO pt ON pt.id_producto_terminado = p_id_producto
    SET p.total_pedido = p.total_pedido + (pt.precio_venta_producto_terminado * p_cantidad)
    WHERE p.id_pedido = p_id_pedido;
END$$

-- 9) METODO_PAGO
CREATE PROCEDURE InsertMetodoPago(
    IN p_nombre VARCHAR(50),
    OUT p_id INT
)
BEGIN
    INSERT INTO METODO_PAGO(nombre_metodo_pago) VALUES (p_nombre);
    SET p_id = LAST_INSERT_ID();
END$$

-- 10) FACTURA
CREATE PROCEDURE InsertFactura(
    IN p_numero VARCHAR(20),
    IN p_subtotal DECIMAL(10,2),
    IN p_iva DECIMAL(10,2),
    IN p_total DECIMAL(10,2),
    IN p_id_pedido INT,
    IN p_id_metodo_pago INT,
    OUT p_id INT
)
BEGIN
    INSERT INTO FACTURA(
        numero_factura, subtotal, iva, total, id_pedido, id_metodo_pago
    ) VALUES (
        p_numero, p_subtotal, p_iva, p_total, p_id_pedido, p_id_metodo_pago
    );
    SET p_id = LAST_INSERT_ID();
END$$

-- 11) BONO
CREATE PROCEDURE InsertBono(
    IN p_puntos_acum INT,
    IN p_puntos_neces INT,
    IN p_estado ENUM('disponible','redimido','no disponible'),
    IN p_id_usuario INT,
    OUT p_id INT
)
BEGIN
    INSERT INTO BONO(
        puntos_acumulados_bono, puntos_necesarios_bono, estado_bono, id_usuario
    ) VALUES (
        p_puntos_acum, p_puntos_neces, p_estado, p_id_usuario
    );
    SET p_id = LAST_INSERT_ID();
END$$

-- 12) REDENCION_BONO
CREATE PROCEDURE InsertRedencionBono(
    IN p_id_bono INT,
    IN p_id_usuario INT,
    OUT p_id INT
)
BEGIN
    INSERT INTO REDENCION_BONO(id_bono, id_usuario) VALUES (p_id_bono, p_id_usuario);
    SET p_id = LAST_INSERT_ID();
    -- marcar bono como redimido
    UPDATE BONO SET estado_bono = 'redimido' WHERE id_bono = p_id_bono;
END$$

DELIMITER ;


-- Roles de prueba
CALL InsertRol('admin', @rol_admin); SELECT @rol_admin;
CALL InsertRol('cliente', @rol_cliente); SELECT @rol_cliente;

-- Métodos de pago
CALL InsertMetodoPago('Efectivo', @mp_efectivo); SELECT @mp_efectivo;
CALL InsertMetodoPago('Tarjeta', @mp_tarjeta); SELECT @mp_tarjeta;
CALL InsertMetodoPago('Nequi', @mp_nequi); SELECT @mp_nequi;

-- Usuarios (admin y cliente)
CALL InsertUsuario('CC','12345678','admin_shori','admin123','Admin','Root','admin@shori.com','3001112222','Calle 1 #1-1','activo', @rol_admin, @user_admin); SELECT @user_admin;
CALL InsertUsuario('CC','98765432','cliente1','cliente123','Juan','Perez','juan@example.com','3003334444','Calle 2 #2-2','activo', @rol_cliente, @user_cliente); SELECT @user_cliente;

-- Materias primas
CALL InsertMateriaPrima('Carne Res','Carnes','kg','Carne para hamburguesa',12000.00, 50, 'disponible','2026-12-31', @mp_carne); SELECT @mp_carne;
CALL InsertMateriaPrima('Pan Hamburguesa','Panaderia','unidad','Pan grande para hamburguesa',200.00, 200, 'disponible','2025-12-31', @mp_pan); SELECT @mp_pan;
CALL InsertMateriaPrima('Queso','Lacteos','kg','Queso en bloque',15000.00, 20, 'disponible','2025-11-30', @mp_queso); SELECT @mp_queso;

-- Productos terminados
CALL InsertProductoTerminado(1, 30, 'Hamburguesa Clasica','Carne, pan, queso, lechuga', 15000.00, @prod_hamb); SELECT @prod_hamb;
CALL InsertProductoTerminado(1, 50, 'Gaseosa 500ml','Bebida gaseosa 500ml', 4000.00, @prod_gase); SELECT @prod_gase;

-- Relación producto-final (receta ejemplo)
CALL InsertDetalleProductoFinal(@mp_carne, @prod_hamb, 0.2, 'kg', '200g de carne por hamburguesa', @det_hamb_carne); SELECT @det_hamb_carne;
CALL InsertDetalleProductoFinal(@mp_pan, @prod_hamb, 1, 'unidad', '1 pan por hamburguesa', @det_hamb_pan); SELECT @det_hamb_pan;
CALL InsertDetalleProductoFinal(@mp_queso, @prod_hamb, 0.05, 'kg', '50g de queso', @det_hamb_queso); SELECT @det_hamb_queso;

-- Entrada de inventario (registrar compra de materia prima)
CALL InsertEntradaInventario(20, 12000.00, 'Entrada', 'Compra carne', @mp_carne, @user_admin, @entrada1); SELECT @entrada1;
CALL InsertEntradaInventario(100, 200.00, 'Entrada', 'Compra pan', @mp_pan, @user_admin, @entrada2); SELECT @entrada2;

-- Crear un carrito (PEDIDO en estado 'carrito') para cliente
CALL InsertPedido('Carrito de Juan', 'carrito', 0.00, @user_cliente, @pedido_carrito); SELECT @pedido_carrito;

-- Agregar items al carrito (DETALLE_PEDIDO). Esto también actualiza total en PEDIDO.
CALL InsertDetallePedido(@pedido_carrito, @prod_hamb, 2, @det1); SELECT @det1;
CALL InsertDetallePedido(@pedido_carrito, @prod_gase, 1, @det2); SELECT @det2;

-- Verifica total del pedido (opcional)
SELECT * FROM PEDIDO WHERE id_pedido = @pedido_carrito;

-- Cuando confirme compra: cambiar estado a 'pendiente' y generar factura.
UPDATE PEDIDO SET estado_pedido = 'pendiente' WHERE id_pedido = @pedido_carrito;

-- Crear factura (calcula subtotal/iva/total). Para ejemplo:
-- tomamos subtotal como el total del pedido actual
SET @subtotal = (SELECT total_pedido FROM PEDIDO WHERE id_pedido = @pedido_carrito);
SET @iva = ROUND(@subtotal * 0.19,2);
SET @total = ROUND(@subtotal + @iva,2);

CALL InsertFactura(CONCAT('FAC-', LPAD(@pedido_carrito,6,'0')), @subtotal, @iva, @total, @pedido_carrito, @mp_efectivo, @fact1); SELECT @fact1;

-- Bono: dar puntos al usuario (ejemplo 2 puntos)
CALL InsertBono(2, 5, 'no disponible', @user_cliente, @bono1); SELECT @bono1;

-- Si redime bono:
CALL InsertRedencionBono(@bono1, @user_cliente, @red1); SELECT @red1;

-- Mostrar datos insertados de ejemplo:
SELECT * FROM ROL;
SELECT id_usuario, nombre_usuario, correo_usuario, id_rol FROM USUARIO;
SELECT * FROM MATERIA_PRIMA LIMIT 10;
SELECT * FROM PRODUCTO_TERMINADO;
SELECT * FROM PEDIDO WHERE id_pedido = @pedido_carrito;
SELECT * FROM DETALLE_PEDIDO WHERE id_pedido = @pedido_carrito;
SELECT * FROM FACTURA WHERE id_factura = @fact1;
SELECT * FROM BONO WHERE id_bono = @bono1;
SELECT * FROM REDENCION_BONO WHERE id_redencion = @red1;
