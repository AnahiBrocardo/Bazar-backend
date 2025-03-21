package com.bazar.api.controller;

import com.bazar.api.dto.ApiRespuesta;
import com.bazar.api.model.Cliente;
import com.bazar.api.service.IClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ClienteController {
     @Autowired
    IClienteService clienteService;

    @PostMapping("/clientes/crear")
    public ResponseEntity<ApiRespuesta<Cliente>> crearCliente(@RequestBody Cliente cliente) {
       ApiRespuesta<Cliente> respuesta = clienteService.crearCliente(cliente);
        HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(respuesta);
    }

    @GetMapping("/clientes/traer")
    public ResponseEntity<ApiRespuesta<List<Cliente>>> obtenerTodosClientes(){
        ApiRespuesta<List<Cliente>> respuesta = clienteService.obtenerTodosClientes();
        HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(respuesta);
    }


    @GetMapping("/clientes/traer/{id_cliente}")
    public ResponseEntity<ApiRespuesta<Cliente>> obtenerCliente(@PathVariable Long id_cliente){
        ApiRespuesta<Cliente> respuesta = clienteService.obtenerCliente(id_cliente);
        HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(respuesta);
    }

    @GetMapping("/clientes/traer/disponibles")
    public ResponseEntity<ApiRespuesta<List<Cliente>>> obtenerClientesDisponibles(){
        ApiRespuesta<List<Cliente>> respuesta = clienteService.obtenerClientesDisponibles();
        HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(respuesta);
    }

    @DeleteMapping("/clientes/eliminar/{id_cliente}")
    public ResponseEntity<ApiRespuesta<Cliente>> eliminarCliente(@PathVariable Long id_cliente){
        ApiRespuesta<Cliente> respuesta= clienteService.eliminarCliente(id_cliente);
        HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(respuesta);
    }

    @PutMapping("/clientes/editar/{id_cliente}")
    public ResponseEntity<ApiRespuesta<Cliente>> actualizarCliente(@PathVariable Long id_cliente, @RequestBody Cliente cliente){
        ApiRespuesta<Cliente>respuesta= clienteService.editarCliente(id_cliente, cliente);
        HttpStatus status = respuesta.isExito() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;

        return ResponseEntity.status(status).body(respuesta);
    }
}
