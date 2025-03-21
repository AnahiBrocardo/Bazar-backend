package com.bazar.api.controller;

import com.bazar.api.dto.ApiRespuesta;
import com.bazar.api.model.Producto;
import com.bazar.api.service.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductoController {
    @Autowired
    private IProductoService productoService;

    @PostMapping("/productos/crear")
    public ResponseEntity<ApiRespuesta<Producto>> crearProducto(@RequestBody Producto producto) {
        ApiRespuesta<Producto> respuesta = productoService.crearProducto(producto);

        HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(respuesta, status);
    }


    @GetMapping("/productos/traer")
    public ResponseEntity<ApiRespuesta<List<Producto>>> traerProductos() {
        ApiRespuesta<List<Producto>> respuesta = productoService.obtenerTodosProductos();

        HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(respuesta, status);
    }

    @GetMapping("/productos/traer/disponibles")
    public ResponseEntity<ApiRespuesta<List<Producto>>> traerProductosDisponibles() {
        ApiRespuesta<List<Producto>> respuesta = productoService.obtenerProductosDisponibles();

        HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(respuesta, status);
    }

    @GetMapping("/productos/traer/{id_producto}")
    public ResponseEntity<ApiRespuesta<Producto>> traerProducto(@PathVariable Long id_producto) {
        ApiRespuesta<Producto> respuesta= productoService.obtenerProducto(id_producto);

        HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(respuesta, status);
    }

    @DeleteMapping("/productos/eliminar/{id_producto}")
    public ResponseEntity<ApiRespuesta<Producto>> eliminarProducto(@PathVariable Long id_producto) {
        ApiRespuesta<Producto> respuesta= productoService.eliminarProducto(id_producto);

        HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(respuesta, status);
    }

    @PutMapping("/productos/editar/{id_producto")
    public ResponseEntity<ApiRespuesta<Producto>> editarProducto(@PathVariable Long id_producto, @RequestBody Producto producto) {
        ApiRespuesta<Producto>respuesta = productoService.editarProducto(id_producto, producto);

        HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(respuesta, status);
    }

    @GetMapping("productos/obtener/falta_stock}")
    public ResponseEntity<ApiRespuesta<List<Producto>>> obtenerProductosFaltaStock() {
        ApiRespuesta<List<Producto>> respuesta= productoService.obtenerProductosConFaltaStock();
        HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return new ResponseEntity<>(respuesta, status);
    }
}
