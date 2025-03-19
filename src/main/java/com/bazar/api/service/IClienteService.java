package com.bazar.api.service;

import com.bazar.api.dto.ApiRespuesta;
import com.bazar.api.model.Cliente;

import java.util.List;

public interface IClienteService {
    public ApiRespuesta<Cliente> crearCliente(Cliente cliente);

    public ApiRespuesta<Cliente> obtenerCliente(Long id);

    public ApiRespuesta<List<Cliente>> obtenerClientesDisponibles();

    public ApiRespuesta<List<Cliente>> obtenerTodosClientes();

    public ApiRespuesta<Cliente> editarCliente(Long id, Cliente cliente);

    public ApiRespuesta<Cliente> eliminarCliente(Long id);

}

