package com.bazar.api.service;

import com.bazar.api.dto.ApiRespuesta;
import com.bazar.api.model.Producto;

import java.util.List;

public interface IProductoService {
    public ApiRespuesta<Producto> crearProducto(Producto producto);

    public ApiRespuesta<Producto> obtenerProducto(Long id);

    public ApiRespuesta<List<Producto>> obtenerProductosDisponibles();

    public ApiRespuesta<List<Producto>> obtenerTodosProductos();

    public ApiRespuesta<Producto> editarProducto(Long id, Producto producto);

    public ApiRespuesta<Producto> eliminarProducto(Long id);

    public ApiRespuesta<List<Producto>> obtenerProductosConFaltaStock();

}
