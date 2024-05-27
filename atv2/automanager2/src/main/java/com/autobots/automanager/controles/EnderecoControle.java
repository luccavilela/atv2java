package com.autobots.automanager.controles;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@RestController
@RequestMapping("/endereco")
public class EnderecoControle {
	@Autowired 
	private ClienteRepositorio repositorioCliente;
	
	@GetMapping("/endereco/{clienteId}")
	public ResponseEntity<Endereco> obterEnderecoCliente(@PathVariable Long clienteId) {
		
	    Cliente cliente = repositorioCliente.getById(clienteId);
	    if (cliente == null) {
	    	ResponseEntity<Endereco> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
	    } else {
	    	ResponseEntity<Endereco> resposta = new ResponseEntity<Endereco>(cliente.getEndereco(),HttpStatus.FOUND);
	    	return resposta;
	    }
	    
	}
	
	
	@PutMapping("/atualizar/{clienteId}")
    public ResponseEntity<?> atualizarEndereco(@PathVariable Long clienteId, @RequestBody Endereco novoEndereco) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
        Cliente cliente = repositorioCliente.getById(clienteId);
        if (cliente != null) {
        	cliente.setEndereco(novoEndereco);
            repositorioCliente.save(cliente);
            status = HttpStatus.OK;
        } 
        return new ResponseEntity<>(status);
    }
	
	
}
