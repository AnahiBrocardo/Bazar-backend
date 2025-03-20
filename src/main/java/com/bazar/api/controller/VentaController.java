package com.bazar.api.controller;

import com.bazar.api.dto.ApiRespuesta;
import com.bazar.api.model.Venta;
import com.bazar.api.service.IVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class VentaController {
    @Autowired
    private IVentaService ventaService;

    @GetMapping("/ventas/crear")
    public ResponseEntity<ApiRespuesta<Venta>> crearVenta(@RequestBody Venta venta) {
        ApiRespuesta<Venta> respuesta = ventaService.crearVenta(venta);
        HttpStatus status;
        if (respuesta.isExito()) {
            status = HttpStatus.CREATED;
        } else {
            status = HttpStatus.OK;
        }
        return new ResponseEntity<>(respuesta, status);
    }

    @GetMapping("/ventas/traer")
    public ResponseEntity<ApiRespuesta<List<Venta>>> obtenerVentas() {
        ApiRespuesta<List<Venta>> respuesta = ventaService.obtenerTodasVentas();
        HttpStatus status;

        if(respuesta.isExito()) {
            status = HttpStatus.OK;
        }else{
            status = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(respuesta, status);
    }

    @GetMapping("/ventas/traer/{id_venta}")
    public ResponseEntity<ApiRespuesta<Venta>> obtenerUnaVenta(@PathVariable Long id_venta) {
        ApiRespuesta<Venta> respuesta = ventaService.obtenerVenta(id_venta);
        HttpStatus status;

        if(respuesta.isExito()) {
            status = HttpStatus.OK;
        }else{
            status = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(respuesta, status);
    }

    @PutMapping("/ventas/editar/{id_venta}")
    public ResponseEntity<ApiRespuesta<Venta>> editarVenta(@RequestBody Venta venta, @PathVariable Long id_venta) {
        ApiRespuesta<Venta> respuesta= ventaService.actualizarVenta(id_venta, venta);
        HttpStatus status;
        if(respuesta.isExito()) {
            status = HttpStatus.OK;
        }else{
            status = HttpStatus.NOT_FOUND;
        }
        return new ResponseEntity<>(respuesta, status);
    }

    /*@PutMapping("/ventas/cambiarEstado/{id}/{nuevoEstado}")
public ResponseEntity<ApiRespuesta<Venta>> cambiarEstadoVenta(
        @PathVariable("id") Long id_venta,
        @PathVariable("nuevoEstado") Estado nuevoEstado) {

    ApiRespuesta<Venta> respuesta = ventaService.cambiarEstadoVenta(id_venta, nuevoEstado);

    HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

    return ResponseEntity.status(status).body(respuesta);
}
*/
}