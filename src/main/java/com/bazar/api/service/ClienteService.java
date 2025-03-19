package com.bazar.api.service;

import com.bazar.api.dto.ApiRespuesta;
import com.bazar.api.model.Cliente;
import com.bazar.api.repository.IClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteService implements IClienteService {
    @Autowired
    IClienteRepository clienteRepository;

    @Override
    public ApiRespuesta<Cliente> crearCliente(Cliente cliente) {
        ApiRespuesta<Cliente> respuesta;

        try{
        clienteRepository.save(cliente);
        respuesta= new ApiRespuesta<>(true, "Cliente creado correctamente", cliente);
        }catch (Exception e){
            System.out.println("Error al crear el cliente"+e.getMessage());
            respuesta= new ApiRespuesta<>(false, "Error al crear el cliente", cliente);
        }
        return respuesta;
    }

    @Override
    public ApiRespuesta<Cliente> obtenerCliente(Long id) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(id);
        ApiRespuesta<Cliente> respuesta;

        if (clienteOpt.isPresent()) {
            respuesta= new ApiRespuesta<>(true, "Cliente obtenido correctamente", clienteOpt.get());
        }else{
            respuesta= new ApiRespuesta<>(false, "Cliente no encontrado", null);
        }
        return respuesta;
    }

    @Override
    public ApiRespuesta<List<Cliente>> obtenerClientesDisponibles() {
        List<Cliente> clientes = clienteRepository.findAll().stream()
                .filter(Cliente::isDisponible)
                .collect(Collectors.toList());

        ApiRespuesta<List<Cliente>> respuesta;

        if(clientes.isEmpty()){
            respuesta= new ApiRespuesta<>(false, "Clientes no encontrado", clientes);
        }else{
            respuesta= new ApiRespuesta<>(true, "Clientes obtenidos correctamente", clientes);
        }

        return respuesta;
    }

    @Override
    public ApiRespuesta<List<Cliente>> obtenerTodosClientes() {
        List<Cliente> clientes = clienteRepository.findAll();
        ApiRespuesta<List<Cliente>> respuesta;

        if(clientes.isEmpty()){
            respuesta= new ApiRespuesta<>(false, "Clientes no encontrados", null);
        }else{
            respuesta= new ApiRespuesta<>(true, "Clientes obtenidos correctamente", clientes);
        }
        return respuesta;
    }

    @Override
    public ApiRespuesta<Cliente> editarCliente(Long id, Cliente cliente) {
        ApiRespuesta<Cliente> respuesta;
        Optional<Cliente> clienteOpt = clienteRepository.findById(id);

        if(clienteOpt.isEmpty()){
            respuesta= new ApiRespuesta<>(false, "Cliente no encontrado", null);
        }else{
            Cliente clienteExistente= clienteOpt.get();

            clienteExistente.setNombre(cliente.getNombre());
            clienteExistente.setApellido(cliente.getApellido());
            clienteExistente.setDni(cliente.getDni());

            clienteRepository.save(clienteExistente);
            respuesta= new ApiRespuesta<>(true, "Cliente actualizado correctamente", clienteExistente);
        }
        return respuesta;
    }

    @Override
    public ApiRespuesta<Cliente> eliminarCliente(Long id) {
        ApiRespuesta<Cliente> respuesta;
        Cliente clienteAeliminar= clienteRepository.findById(id).orElse(null);

        if (clienteAeliminar!=null){
            clienteAeliminar.setDisponible(false);
            clienteAeliminar.setFechaEliminacion(LocalDate.now());
            clienteRepository.save(clienteAeliminar);
            respuesta= new ApiRespuesta<>(true, "Cliente eliminado correctamente", clienteAeliminar);
        }else{
            respuesta= new ApiRespuesta<>(false, "Error al eliminar el cliente", null);
        }

        return respuesta;
    }
}
