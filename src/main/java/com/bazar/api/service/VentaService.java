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
import jakarta.transaction.Transactional;
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
                    procesarVentaCompleta(venta, cliente);
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

    @Transactional
    public Venta validarYDescontar(List<Producto> listaProductos, Venta venta) throws StockInsuficienteException {
        List<Producto> nuevaLista = new ArrayList<>();
        for (Producto producto : listaProductos) {
            Producto productoDB = productoRepository.findById(producto.getId_producto()).orElse(null);
            if (productoDB != null) {
                if (productoDB.getCantidad_disponible() <= 0) {

                    throw new StockInsuficienteException("El producto " + productoDB.getNombreProducto() + " tiene stock insuficiente: "+ productoDB.getCantidad_disponible());
                }else{
                    int nuevoStock = productoDB.getCantidad_disponible() - 1;
                    productoDB.setCantidad_disponible(nuevoStock);
                    productoRepository.save(productoDB);

                    nuevaLista.add(productoDB);
                }
            }
        }
        venta.setLista_productos(nuevaLista);
        return venta;
    }

    public boolean verificarStockProducto (Long id_producto, int cantDescontar) {
        boolean exito=false;
        Producto productoBDD= productoRepository.findById(id_producto).orElse(null);
        if(productoBDD!=null){
            int nuevoStock= productoBDD.getCantidad_disponible()-cantDescontar;
            if(nuevoStock>=0){
                exito= true;
            }
        }
        return exito;
    }

    private void procesarVentaCompleta(Venta venta, Cliente cliente) throws StockInsuficienteException {

        venta= validarYDescontar(venta.getLista_productos(), venta);
        venta.setTotal_venta(calcularTotalVenta(venta.getLista_productos()));
        venta.setFecha_realizacion_venta(LocalDate.now());
        venta.setCliente(cliente);
        venta.setLista_productos(venta.getLista_productos());

        Venta ventaGuardada = ventaRepository.save(venta);

        for (Producto producto : venta.getLista_productos()) {

            producto.setVenta(ventaGuardada);

            productoService.editarProducto(producto.getId_producto(), producto);

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
    public ApiRespuesta<Venta> actualizarVenta(Long id_venta, List<Producto> productos) { //VER!
        ApiRespuesta<Venta> respuesta;
        Venta ventaBDD= ventaRepository.findById(id_venta).orElse(null);
        if(ventaBDD!=null){
           if(ventaBDD.getEstado_venta() == Estado.CANCELADA || ventaBDD.getEstado_venta()== Estado.FINALIZADA){
               respuesta= new ApiRespuesta<>(false, "No se puede editar una venta " + ventaBDD.getEstado_venta()); // no se puede editar una venta finalizada o cancelada
           }else{
               List<Producto> productosOriginales= ventaBDD.getLista_productos();

               // Se convierte las listas de productos en mapas (id_producto -> cantidad)
               Map<Long, Integer> productosOriginalesMap = convertirListaAMapa(ventaBDD.getLista_productos());
               Map<Long, Integer> productosNuevosMap = convertirListaAMapa(productos);

               try{
                   actualizarStock(productosOriginalesMap, productosNuevosMap);
                   double nuevoTotal= calcularTotalVenta(productos);
                   ventaBDD.setTotal_venta(nuevoTotal);
                   ventaBDD.setLista_productos(productos);
                   ventaRepository.save(ventaBDD);

                   respuesta= new ApiRespuesta<>(true, "Venta actualizada correctamente", ventaBDD);
               }catch(StockInsuficienteException e){
                   respuesta= new ApiRespuesta<>(false, e.getMessage());
               }

           }
        }else{
            respuesta=new ApiRespuesta<>(false, "Venta no encontrada");
        }
        return respuesta;
    }

    private Map<Long, Integer> convertirListaAMapa(List<Producto> productos) {
        Map<Long, Integer> mapa = new HashMap<>();
        for (Producto producto : productos) {
            if (mapa.containsKey(producto.getId_producto())) {
                mapa.put(producto.getId_producto(), mapa.get(producto.getId_producto()) + 1);
            } else {
                mapa.put(producto.getId_producto(), 1);
            }
        }
        return mapa;
    }

    public void eliminarVentaDeProducto(Long idProducto) {
        Producto producto = productoRepository.findById(idProducto).orElse(null);
        if (producto != null) {
            producto.setVenta(null);
            productoRepository.save(producto);
        }
    }

    private void actualizarStock(Map<Long, Integer> productosOriginales, Map<Long, Integer> productosNuevos) throws StockInsuficienteException {
        // Verificar si hay productos eliminados
        for (Long idProducto : productosOriginales.keySet()) {
            if (!productosNuevos.containsKey(idProducto)) {
                int cantidadEliminada = productosOriginales.get(idProducto);
                if(cantidadEliminada>0){
                    restaurarStock(idProducto, cantidadEliminada);
                    eliminarVentaDeProducto(idProducto);
                }
            }
        }

        // Verificar productos agregados o modificados
        for (Map.Entry<Long, Integer> entry : productosNuevos.entrySet()) {
            Long idProducto = entry.getKey();
            int nuevaCantidad = entry.getValue();
            int cantidadAntigua = productosOriginales.getOrDefault(idProducto, 0);
            int diferencia = nuevaCantidad - cantidadAntigua;

            if (nuevaCantidad>cantidadAntigua) { // Si aumentó la cantidad de un producto
                if (verificarStockProducto(idProducto, diferencia)) { // Verificar stock antes de descontar
                    descontarStock(idProducto, diferencia);
                } else {
                    throw new StockInsuficienteException("Stock insuficiente para el producto con ID: " + idProducto);
                }
            } else if(cantidadAntigua>nuevaCantidad){
                int cantidadRestaurada = cantidadAntigua - nuevaCantidad;
                if (cantidadRestaurada > 0) { // Restaurar solo si la cantidad restaurada es positiva
                    restaurarStock(idProducto, cantidadRestaurada); // Restaurar el stock
                }
            }
        }
    }

    private void descontarStock(Long idProducto, int cantADescontar) {
        Producto producto = productoRepository.findById(idProducto).orElse(null);
        if (producto != null) {
            producto.setCantidad_disponible(producto.getCantidad_disponible() - cantADescontar);
            productoRepository.save(producto);
        }
    }

    private void restaurarStock(Long idProducto, int diferencia) {
        if (diferencia > 0) { // Solo restaurar si la diferencia es positiva
            Producto producto = productoRepository.findById(idProducto).orElse(null);
            if (producto != null) {
                producto.setCantidad_disponible(producto.getCantidad_disponible() + diferencia);
                productoRepository.save(producto);
            }
        }
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

