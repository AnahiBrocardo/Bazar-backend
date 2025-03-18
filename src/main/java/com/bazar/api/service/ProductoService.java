package com.bazar.api.service;

import com.bazar.api.dto.ApiRespuesta;
import com.bazar.api.model.Producto;
import com.bazar.api.repository.IProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            productoRepository.save(producto);
            respuesta= new ApiRespuesta<>(true, "Producto creado correctamente", producto);
        }catch(Exception e){
            respuesta= new ApiRespuesta<>(true, "Error al crear el producto", producto);
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
            respuesta= new ApiRespuesta<List<Producto>>(false, "Productos no encontrados", null);
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
            respuesta= new ApiRespuesta<List<Producto>>(false, "Productos no encontrados", null);
        }else{
            respuesta=new ApiRespuesta<List<Producto>>(true, "Productos obtenidos correctamente", productos);
        }

        return respuesta;
    }

    @Override
    public ApiRespuesta<Producto> editarProducto(Long id, Producto producto) {
        return null;
    }

    @Override
    public ApiRespuesta<Producto> eliminarProducto(Long id) {
        ApiRespuesta<Producto> respuesta;

        try{
            productoRepository.deleteById(id);
            respuesta= new ApiRespuesta<Producto>(true, "Producto eliminado correctamente", productoRepository.findById(id).get());
        }catch(Exception e){
            respuesta= new ApiRespuesta<Producto>(false,"Error al eliminar el producto", productoRepository.findById(id).get());
        }
        return respuesta;
    }

    @Override
    public ApiRespuesta<List<Producto>> obtenerProductosDeterminadaCantEnStock(int cantidadStockAverificar) {
        ApiRespuesta<List<Producto>> respuesta;
        List<Producto> productos = new ArrayList<>();
        List<Producto> todosProductos = this.obtenerTodosProductos().getDatos();
        for (Producto producto : todosProductos) {
            if (producto.getCantidad_disponible() < cantidadStockAverificar) {
                productos.add(producto);
            }
        }

        if (productos.isEmpty()) {
            respuesta= new ApiRespuesta<List<Producto>>(false, "No se encontraron productos con esa cantidad en stock", null);
        }else{
            respuesta=new ApiRespuesta<List<Producto>>(true, "Productos con menos stock obtenidos correctamente", productos);
        }

        return  respuesta;
    }


}
