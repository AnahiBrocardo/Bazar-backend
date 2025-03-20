package com.bazar.api.service;

import com.bazar.api.dto.ApiRespuesta;
import com.bazar.api.model.Producto;
import com.bazar.api.repository.IProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoService implements IProductoService{
    @Autowired
    private IProductoRepository productoRepository;


    @Override
    public ApiRespuesta<Producto> crearProducto(Producto producto) {
       ApiRespuesta<Producto> respuesta;
        try{
            producto.setDisponible(true);
            producto.setFechaEliminacion(null);
            producto.setVenta(null);
            productoRepository.save(producto);
            respuesta= new ApiRespuesta<>(true, "Producto creado correctamente", producto);
        }catch(Exception e){
            System.out.println("Error al crear el producto: " + e.getMessage());
            respuesta= new ApiRespuesta<>(false, "Error al crear el producto", producto);
        }
        return respuesta;
    }

    @Override
    public ApiRespuesta<Producto> obtenerProducto(Long id) {
        Optional<Producto> productoOpt = productoRepository.findById(id);
        ApiRespuesta<Producto> respuesta;

        if (productoOpt.isPresent()) {
            respuesta=  new ApiRespuesta<>(true, "Producto obtenido correctamente", productoOpt.get());
        } else {
            respuesta= new ApiRespuesta<>(false, "Producto no encontrado", null);
        }
        return respuesta;
    }


    @Override
    public ApiRespuesta<List<Producto>> obtenerProductosDisponibles() {
        List<Producto> productosDisponibles = productoRepository.findAll().stream()
                .filter(Producto::isDisponible) // se filtran productos disponibles
                .collect(Collectors.toList()); // convierte a una lista

        ApiRespuesta<List<Producto>> respuesta;

        if (productosDisponibles.isEmpty()) {
            respuesta= new ApiRespuesta<>(false, "Productos no encontrados", productosDisponibles);
        }else{
            respuesta=new ApiRespuesta<List<Producto>>(true, "Productos obtenidos correctamente", productosDisponibles);
        }

        return respuesta;
    }

    @Override
    public ApiRespuesta<List<Producto>> obtenerTodosProductos() {
        List<Producto> productos= productoRepository.findAll();
        ApiRespuesta<List<Producto>> respuesta;

        if (productos.isEmpty()) {
            respuesta= new ApiRespuesta<List<Producto>>(false, "No se encontraron productos", null);
        }else{
            respuesta=new ApiRespuesta<List<Producto>>(true, "Productos obtenidos correctamente", productos);
        }

        return respuesta;
    }

    @Override
    public ApiRespuesta<Producto> editarProducto(Long id, Producto producto) {
        ApiRespuesta<Producto> respuesta;
        Optional<Producto> productoExistenteOpt = productoRepository.findById(id); // puede almacenar un producto o estar vacío si el producto no existe

        if (productoExistenteOpt.isEmpty()) {
            respuesta= new ApiRespuesta<>(false, "El producto no existe", null);
        }else{
            Producto productoExistente = productoExistenteOpt.get();

            // Se actualizan todos los campos, menos el id
            productoExistente.setNombreProducto(producto.getNombreProducto());
            productoExistente.setMarca(producto.getMarca());
            productoExistente.setCosto(producto.getCosto());
            productoExistente.setCantidad_disponible(producto.getCantidad_disponible());
            productoExistente.setDescripcion(producto.getDescripcion());
            productoExistente.setUrl_imagen(producto.getUrl_imagen());

            productoRepository.save(productoExistente);
            respuesta= new ApiRespuesta<>(true, "Producto actualizado correctamente", productoExistente);
        }

     return respuesta;
    }


    @Override
    public ApiRespuesta<Producto> eliminarProducto(Long id) { //eliminado logico
        ApiRespuesta<Producto> respuesta;
        Producto productoAeliminar= productoRepository.findById(id).orElse(null);

        if(productoAeliminar!=null){
            productoAeliminar.setDisponible(false);
            productoAeliminar.setFechaEliminacion(LocalDate.now());
            productoRepository.save(productoAeliminar);
            respuesta= new ApiRespuesta<>(true, "Producto eliminado correctamente", productoAeliminar);
        }else{
            respuesta= new ApiRespuesta<>(false,"Error al eliminar el producto", null);
        }
        return respuesta;
    }

    @Override
    public ApiRespuesta<List<Producto>> obtenerProductosConFaltaStock() {
        int cantidadStockAverificar=5;
        ApiRespuesta<List<Producto>> respuesta;
        List<Producto> productos = new ArrayList<>();
        List<Producto> todosProductos = this.obtenerTodosProductos().getDatos();
        for (Producto producto : todosProductos) {
            if (producto.getCantidad_disponible() < cantidadStockAverificar) {
                productos.add(producto);
            }
        }

        if (productos.isEmpty()) {
            respuesta= new ApiRespuesta<>(false, "No se encontraron productos con esa cantidad en stock", null);
        }else{
            respuesta=new ApiRespuesta<>(true, "Productos con menos stock obtenidos correctamente", productos);
        }

        return  respuesta;
    }


}
