package com.bazar.api.service;

import com.bazar.api.dto.ApiRespuesta;
import com.bazar.api.dto.DetalleVentaDTO;
import com.bazar.api.dto.ResumenVentasDTO;
import com.bazar.api.exceptions.StockInsuficienteException;
import com.bazar.api.model.Estado;
import com.bazar.api.model.Producto;
import com.bazar.api.model.Venta;
import com.bazar.api.repository.IProductoRepository;
import com.bazar.api.repository.IVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VentaService implements IVentaService {
    @Autowired
    IVentaRepository ventaRepository;

    @Autowired
    IProductoRepository productoRespository;

    @Override
    public ApiRespuesta<Venta> crearVenta(Venta venta) {
        ApiRespuesta<Venta> respuesta = new ApiRespuesta<>();

        try{
            verificarYdescontarStock(venta.getLista_productos());
            venta.setTotal_venta(calcularTotalVenta(venta.getLista_productos()));
            ventaRepository.save(venta);
            respuesta= new ApiRespuesta<>(true, "Venta creada exitosamente", venta);
        }catch (StockInsuficienteException e){
            System.out.println("Error: " + e.getMessage());
            respuesta= new ApiRespuesta<>(false, "Stock insuficiente", venta);
        }
    return respuesta;
    }

    @Override
    public ApiRespuesta<Venta> obtenerVenta(Long id_venta) {
        ApiRespuesta<Venta> respuesta = new ApiRespuesta<>();
        Optional<Venta> ventaOp= ventaRepository.findById(id_venta);

        if(ventaOp.isPresent()){
            respuesta= new ApiRespuesta<>(true, "Venta obtenida correctamente", ventaOp.get());
        }else {
            respuesta= new ApiRespuesta<>(false, "No se encontro la venta", null);
        }
        return respuesta;
    }

    @Override
    public ApiRespuesta<Venta> actualizarVenta(Long id_venta, Venta venta) {
        ApiRespuesta<Venta> respuesta;
        Optional<Venta> ventaExistenteOpt = ventaRepository.findById(id_venta); // puede almacenar un producto o estar vacío si el producto no existe

        if (ventaExistenteOpt.isEmpty()) {
            respuesta= new ApiRespuesta<>(false, "No se encontro la venta", null);
        }else{
            Venta ventaExistente = ventaExistenteOpt.get();

            try{
                verificarYdescontarStock(ventaExistente.getLista_productos());
                ventaExistente.setTotal_venta(calcularTotalVenta(ventaExistente.getLista_productos()));
                ventaExistente.setCodigo_venta(venta.getCodigo_venta());
                ventaExistente.setFecha_realizacion_venta(venta.getFecha_realizacion_venta());
                ventaExistente.setEstado_venta(venta.getEstado_venta());
                ventaExistente.setCliente(venta.getCliente());
                ventaRepository.save(venta);
                respuesta= new ApiRespuesta<>(true, "Venta actualizada correctamente", venta);
            }catch (StockInsuficienteException e){
                System.out.println("Error: " + e.getMessage());
                respuesta= new ApiRespuesta<>(false, "Stock insuficiente");
            }
        }

        return respuesta;
    }

    @Override
    public ApiRespuesta<Venta> cambiarEstadoVenta(Long id_venta, Estado nuevoEstado) {
        Optional<Venta> ventaOptional= ventaRepository.findById(id_venta);
        ApiRespuesta<Venta> respuesta;

        if(ventaOptional.isPresent()){
            ventaOptional.get().setEstado_venta(nuevoEstado);
            respuesta= new ApiRespuesta<>(true, "Estado de venta modificado correctamente", ventaOptional.get());
        }
        else{
            respuesta= new ApiRespuesta<>(false, "Error, no se encontro la venta", null);
        }
        return respuesta;
    }

    @Override
    public ApiRespuesta<List<Venta>> obtenerTodasVentas() {
        List<Venta> ventas= ventaRepository.findAll();
        ApiRespuesta<List<Venta>> respuesta;

        if (ventas.isEmpty()) {
            respuesta= new ApiRespuesta<>(false, "No se encontraron ventas", ventas);
        }else{
            respuesta=new ApiRespuesta<>(true, "Ventas obtenidos correctamente", ventas);
        }

        return respuesta;
    }


    @Override
    public ApiRespuesta<List<Producto>> obtenerProductosDeVenta(Long id_venta) {
        ApiRespuesta<List<Producto>> respuesta = new ApiRespuesta<>();
        Optional<Venta> ventaOp = ventaRepository.findById(id_venta);

        if (ventaOp.isPresent()) {
            List<Producto> productos = ventaOp.get().getLista_productos();
            if(!productos.isEmpty()){
                respuesta = new ApiRespuesta<>(true, "Productos obtenidos correctamente", productos);
            } else {
                respuesta = new ApiRespuesta<>(false, "No hay productos asociados a la venta", productos);
            }
        }else{
            respuesta = new ApiRespuesta<>(false, "No se encontró la venta", null);
        }

        return respuesta;
    }


    //Obtener recaudacion total y cant de ventas en determinado dia
    @Override
    public ApiRespuesta<ResumenVentasDTO> obtenerResumenVentasPorFecha(LocalDate fecha) {
        ApiRespuesta<ResumenVentasDTO> respuesta;
        List<Venta> ventasPorFecha= ventaRepository.findAll().stream()
                .filter(venta-> venta.getFecha_realizacion_venta().equals(fecha))
                .toList();

        if(!ventasPorFecha.isEmpty()){
          double recaudacion=this.calcularRecaudacionDeVentas(ventasPorFecha);
            ResumenVentasDTO resumenVenta= new ResumenVentasDTO(ventasPorFecha.size(), recaudacion, fecha);
          respuesta= new ApiRespuesta<>(true, "Resumen de venta obtenido correctamente", resumenVenta);
        }else{
        respuesta= new ApiRespuesta<>(false, "No existen ventas realizadas en la fecha ingresada", null);
        }

    return respuesta;
    }

    //obtener el detalle de venta (codigo_venta,total,cantidad de productos,nombre y apellido de cliente) de la venta con el monto más alto de todas.
    @Override
    public ApiRespuesta<DetalleVentaDTO> obtenerDetalleDeVentaDeMayorMonto() {
        ApiRespuesta<DetalleVentaDTO> respuesta;
        Optional<Venta> ventaMax = ventaRepository.findAll().stream()
                .max(Comparator.comparingDouble(Venta::getTotal_venta));

        if(ventaMax.isPresent()){
            Venta venta= ventaMax.get();
            DetalleVentaDTO detalleVenta= new DetalleVentaDTO(venta.getCodigo_venta(), venta.getTotal_venta(), venta.getLista_productos().size(), venta.getCliente().getNombre(), venta.getCliente().getApellido());
            respuesta= new ApiRespuesta<>(true, "Detalle de venta de mayor monto obtenido correctamente", detalleVenta);
        }else{
            respuesta= new ApiRespuesta<>(false, "No se encontraron ventas");
        }
        return respuesta;
    }


    public void verificarYdescontarStock (List<Producto> listaProductos) throws StockInsuficienteException{
        for (Producto producto : listaProductos) {
            if(producto.getCantidad_disponible()>0){
                producto.setCantidad_disponible(producto.getCantidad_disponible()-1);
                productoRespository.save(producto);
            }else{
                throw new StockInsuficienteException("No hay suficiente stock para el producto: " + producto.getNombreProducto());
            }
        }
    }

    public Double calcularTotalVenta (List<Producto> listaProductos){
        Double totalVenta=0.0;

        for(Producto producto:listaProductos){
        totalVenta+= producto.getCosto();
        }

        return totalVenta;
    }

    public double calcularRecaudacionDeVentas(List<Venta> ventas){
        double recaudacion=0;
        for(Venta venta:ventas){
            recaudacion+= venta.getTotal_venta();
        }

        return recaudacion;
    }

}

