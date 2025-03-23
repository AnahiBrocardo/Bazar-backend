package com.bazar.api.service;

import com.bazar.api.dto.ApiRespuesta;
import com.bazar.api.dto.DetalleVentaDTO;
import com.bazar.api.dto.ResumenVentasDTO;
import com.bazar.api.exceptions.StockInsuficienteException;
import com.bazar.api.model.Cliente;
import com.bazar.api.model.Estado;
import com.bazar.api.model.Producto;
import com.bazar.api.model.Venta;
import com.bazar.api.repository.IClienteRepository;
import com.bazar.api.repository.IProductoRepository;
import com.bazar.api.repository.IVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VentaService implements IVentaService {
    @Autowired
    IVentaRepository ventaRepository;

    @Autowired
    IProductoRepository productoRepository;

    @Autowired
    IProductoService productoService;

    @Autowired
    IClienteRepository clienteRepository;

    @Override
    public ApiRespuesta<Venta> crearVenta(Venta venta) {
        ApiRespuesta<Venta> respuesta;

        Cliente cliente = obtenerCliente(venta.getCliente().getId_cliente());
        if (cliente == null) {
            respuesta= new ApiRespuesta<>(false, "Cliente no encontrado");
        }else if (!cliente.isDisponible()){
            respuesta= new ApiRespuesta<>(false, "Cliente no disponible");
        }else{
            Producto productoNoDisponible = productoNoDisponible(venta.getLista_productos());
            if (productoNoDisponible !=null){
                respuesta= new ApiRespuesta<>(false, "El producto " + productoNoDisponible +" no esta disponible");
            }else{
                try {
                    procesarDescuento(venta.getLista_productos());
                    procesarVenta(venta, cliente);
                    respuesta= new ApiRespuesta<>(true, "Venta creada exitosamente", venta);
                } catch( StockInsuficienteException e){
                    System.out.println(e.getMessage());
                    respuesta= new ApiRespuesta<>(false, "Error en el stock: " + e.getMessage());
                }catch (Exception e){
                    System.out.println(e.getMessage());
                    respuesta= new ApiRespuesta<>(false, e.getMessage());
                }
            }

        }


        return respuesta;
    }


    private Cliente obtenerCliente(Long idCliente) {
        return clienteRepository.findById(idCliente).orElse(null);
    }

    public Map<Long, Integer> contarProductos(List<Producto> listaProductos) {
        Map<Long, Integer> conteoProductos = new HashMap<>();

        for (Producto producto : listaProductos) {
            Long idProducto = producto.getId_producto();
            conteoProductos.put(idProducto, conteoProductos.getOrDefault(idProducto, 0) + 1);
        }
        return conteoProductos;
    }

    public void validarStock(Map<Long, Integer> conteoProductos) throws StockInsuficienteException {
        for (Map.Entry<Long, Integer> entry : conteoProductos.entrySet()) {
            Long idProducto = entry.getKey();
            int cantidadRequerida = entry.getValue();

            Producto productoDB = productoRepository.findById(idProducto)
                    .orElseThrow(() -> new StockInsuficienteException("El producto con ID " + idProducto + " no existe."));

            if (productoDB.getCantidad_disponible() < cantidadRequerida) {
                throw new StockInsuficienteException("No hay suficiente stock para el producto: " + productoDB.getNombreProducto());
            }
        }
    }

    public void descontarYGuardarStock(Map<Long, Integer> conteoProductos) {
        for (Map.Entry<Long, Integer> entry : conteoProductos.entrySet()) {
            Long idProducto = entry.getKey();
            int cantidadADescontar = entry.getValue();

            Producto productoDB = productoRepository.findById(idProducto).orElse(null);

            if (productoDB != null) {


                productoDB.setCantidad_disponible(productoDB.getCantidad_disponible() - cantidadADescontar);

                productoService.editarProducto(productoDB.getId_producto(), productoDB);
            }
        }
    }

    public void procesarDescuento(List<Producto> listaProductos) throws StockInsuficienteException {
        Map<Long, Integer> conteoProductos = contarProductos(listaProductos);

        validarStock(conteoProductos);

        descontarYGuardarStock(conteoProductos);
    }

    private Producto productoNoDisponible(List<Producto> productos) {
        Producto productoNoDisponible = null;
        int i = 0;
        boolean encontrado = false;
        while (i < productos.size() && !encontrado) {
            Producto producto = productos.get(i);
            Producto productoDB = productoRepository.findById(producto.getId_producto()).orElse(null);
            if (productoDB != null) {
                if(!productoDB.isDisponible()) {
                    productoNoDisponible = productoDB;
                    encontrado = true;
                }
            }
            i++;
        }
        return productoNoDisponible;
    }

    private void procesarVenta(Venta venta, Cliente cliente) {
        venta.setLista_productos(venta.getLista_productos());
        venta.setTotal_venta(calcularTotalVenta(venta.getLista_productos()));
        venta.setFecha_realizacion_venta(LocalDate.now());
        venta.setCliente(cliente);

        venta = ventaRepository.save(venta);

        for (Producto producto : venta.getLista_productos()) {
            producto.setVenta(venta);
            productoRepository.save(producto);
        }

    }



    public Double calcularTotalVenta (List<Producto> listaProductos){
        Double totalVenta=0.0;

        for(Producto producto:listaProductos){
            totalVenta+= producto.getCosto();
        }

        return totalVenta;
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



    public double calcularRecaudacionDeVentas(List<Venta> ventas){
        double recaudacion=0;
        for(Venta venta:ventas){
            recaudacion+= venta.getTotal_venta();
        }

        return recaudacion;
    }

    @Override
    public ApiRespuesta<Venta> actualizarVenta(Long id_venta, Venta venta) { //VER!
        ApiRespuesta<Venta> respuesta;
        Optional<Venta> ventaExistenteOpt = ventaRepository.findById(id_venta); // puede almacenar un producto o estar vacío si el producto no existe

        if (ventaExistenteOpt.isEmpty()) {
            respuesta= new ApiRespuesta<>(false, "No se encontro la venta", null);
        }else{
            Venta ventaExistente = ventaExistenteOpt.get();

            try{
                procesarDescuento(venta.getLista_productos());
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

    @Override
    public ApiRespuesta<Venta> eliminarVenta(Long id_venta) { //eliminado fisico
        ApiRespuesta<Venta> respuesta;
        Venta venta = ventaRepository.findById(id_venta).orElse(null);

        if (venta != null) {
            for (Producto producto : venta.getLista_productos()) {
                producto.setVenta(null);
                productoRepository.save(producto);
            }

            ventaRepository.delete(venta);
            respuesta = new ApiRespuesta<>(true, "Venta eliminada correctamente", venta);
        } else {
            respuesta = new ApiRespuesta<>(false, "No existe la venta indicada", null);
        }
        return respuesta;
    }




}

