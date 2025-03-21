package com.bazar.api.controller;

import com.bazar.api.dto.ApiRespuesta;
import com.bazar.api.dto.DetalleVentaDTO;
import com.bazar.api.dto.ResumenVentasDTO;
import com.bazar.api.model.Estado;
import com.bazar.api.model.Producto;
import com.bazar.api.model.Venta;
import com.bazar.api.service.IVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class VentaController {
    @Autowired
    private IVentaService ventaService;

    @PostMapping("/ventas/crear")
    public ResponseEntity<ApiRespuesta<Venta>> crearVenta(@RequestBody Venta venta) {
        ApiRespuesta<Venta> respuesta = ventaService.crearVenta(venta);

        HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(respuesta, status);
    }

    @GetMapping("/ventas/traer")
    public ResponseEntity<ApiRespuesta<List<Venta>>> obtenerVentas() {
        ApiRespuesta<List<Venta>> respuesta = ventaService.obtenerTodasVentas();

        HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(respuesta, status);
    }

    @GetMapping("/ventas/traer/{id_venta}")
    public ResponseEntity<ApiRespuesta<Venta>> obtenerUnaVenta(@PathVariable Long id_venta) {
        ApiRespuesta<Venta> respuesta = ventaService.obtenerVenta(id_venta);

        HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(respuesta, status);
    }

    @PutMapping("/ventas/editar/{id_venta}")
    public ResponseEntity<ApiRespuesta<Venta>> editarVenta(@RequestBody Venta venta, @PathVariable Long id_venta) {
        ApiRespuesta<Venta> respuesta= ventaService.actualizarVenta(id_venta, venta);

        HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(respuesta, status);
    }

    @PutMapping("/ventas/cambiarEstado/{id_venta}")
   public ResponseEntity<ApiRespuesta<Venta>> cambiarEstadoVenta(
        @PathVariable Long id_venta,
        @RequestBody Estado nuevoEstado) {

        ApiRespuesta<Venta> respuesta = ventaService.cambiarEstadoVenta(id_venta, nuevoEstado);
        HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        return ResponseEntity.status(status).body(respuesta);
    }

    @GetMapping ("/ventas/productos/{id_venta}")
    public ResponseEntity<ApiRespuesta<List<Producto>>> obtenerPoductosVenta(@PathVariable Long id_venta) {
        ApiRespuesta<List<Producto>> respuesta= ventaService.obtenerProductosDeVenta(id_venta);
        HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(respuesta, status);
    }

    @GetMapping("/ventas/{fecha_venta}")
    public ResponseEntity<ApiRespuesta<ResumenVentasDTO>> obtenerResumenVentaFecha(@PathVariable LocalDate fecha_venta){
        ApiRespuesta<ResumenVentasDTO> respuesta= ventaService.obtenerResumenVentasPorFecha(fecha_venta);

        HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(respuesta, status);
    }

    @GetMapping("/ventas/mayor_venta")
    public ResponseEntity<ApiRespuesta<DetalleVentaDTO>> obtenerDetalleMayorVenta(){
        ApiRespuesta<DetalleVentaDTO> respuesta= ventaService.obtenerDetalleDeVentaDeMayorMonto();

        HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.NOT_FOUND;

        return new ResponseEntity<>(respuesta, status);
    }



}