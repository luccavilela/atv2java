package com.autobots.automanager.controles;


import javax.persistence.EntityNotFoundException;

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
import com.autobots.automanager.modelos.EnderecoAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.EnderecoRepositorio;

@RestController
@RequestMapping("/endereco")
public class EnderecoControle {
	@Autowired 
	private ClienteRepositorio repositorioCliente;
	
	@Autowired 
	private EnderecoRepositorio repositorioEndereco;
	
	@GetMapping("/endereco/{clienteId}")
	public ResponseEntity<Endereco> obterEnderecoCliente(@PathVariable Long clienteId) {
		try {
	    	Cliente cliente = repositorioCliente.getById(clienteId);
	    	if (cliente == null) {
	    		ResponseEntity<Endereco> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
				return resposta;
	    	} else {
	    		ResponseEntity<Endereco> resposta = new ResponseEntity<Endereco>(cliente.getEndereco(),HttpStatus.FOUND);
	    		return resposta;
	    	}
		} catch (EntityNotFoundException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	    
	}
	
	
	@PutMapping("/atualizar/{enderecoId}")
    public ResponseEntity<?> atualizarEndereco(@PathVariable Long enderecoId, @RequestBody Endereco novoEndereco) {
		try {
			HttpStatus status = HttpStatus.BAD_REQUEST;
			Endereco endereco = repositorioEndereco.getById(enderecoId);
			if (endereco != null) {
				EnderecoAtualizador atualizador = new EnderecoAtualizador();
				atualizador.atualizar(endereco, novoEndereco);
            
				repositorioEndereco.save(endereco);
				status = HttpStatus.OK;
			} 
			return new ResponseEntity<>(status);
		} catch (EntityNotFoundException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
    }
	
	
}
