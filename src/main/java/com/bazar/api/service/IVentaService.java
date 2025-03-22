package com.bazar.api.service;

import com.bazar.api.dto.ApiRespuesta;
import com.bazar.api.dto.DetalleVentaDTO;
import com.bazar.api.dto.ResumenVentasDTO;
import com.bazar.api.model.Estado;
import com.bazar.api.model.Producto;
import com.bazar.api.model.Venta;

import java.time.LocalDate;
import java.util.List;

public interface IVentaService {
   public ApiRespuesta<Venta> crearVenta(Venta venta);
   public ApiRespuesta<Venta> obtenerVenta(Long id_venta);
   public ApiRespuesta<Venta> actualizarVenta(Long id_venta, Venta venta);
   public ApiRespuesta<Venta> cambiarEstadoVenta(Long id_venta, Estado estado);
   public ApiRespuesta<List<Venta>> obtenerTodasVentas();
   public ApiRespuesta<List<Producto>> obtenerProductosDeVenta (Long id_venta);
   public ApiRespuesta<ResumenVentasDTO> obtenerResumenVentasPorFecha(LocalDate fecha);
   public ApiRespuesta<DetalleVentaDTO> obtenerDetalleDeVentaDeMayorMonto();
   public ApiRespuesta<Venta> eliminarVenta(Long id_venta);
}
