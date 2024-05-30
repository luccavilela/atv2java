package com.autobots.automanager.controles;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.modelos.TelefoneAtualizador;
import com.autobots.automanager.modelos.AdicionadorLinkCliente;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.TelefoneRepositorio;

@RestController
@RequestMapping("/telefone")
public class TelefoneControle {
	@Autowired 
	private ClienteRepositorio repositorioCliente;
	@Autowired
	private TelefoneRepositorio repositorioTelefone;
	@Autowired
	private AdicionadorLinkCliente adicionadorLink;
	
	@PostMapping("/adicionar/{clienteId}")
    public ResponseEntity<?> adicionarDocumento(@PathVariable Long clienteId, @RequestBody Telefone telefone) {
		try {
        	Cliente cliente = repositorioCliente.getById(clienteId);
        	HttpStatus status = HttpStatus.BAD_REQUEST;
        	if (cliente.getId() != null) {
        		cliente.getTelefones().add(telefone);
        		repositorioCliente.save(cliente);
        		status = HttpStatus.CREATED;
        	}
        	return new ResponseEntity<>(status);
		} catch (EntityNotFoundException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
        	
    }
	
	@GetMapping("/telefones/{clienteId}")
	public ResponseEntity<List<Telefone>> obterTelefonesCliente(@PathVariable Long clienteId) {
		try {
	    	Cliente cliente = repositorioCliente.getById(clienteId);
	    	if (cliente == null) {
	    		ResponseEntity<List<Telefone>> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    		return resposta;
	    	} else {
	    		ResponseEntity<List<Telefone>> resposta = new ResponseEntity<List<Telefone>>(cliente.getTelefones(),HttpStatus.FOUND);
	    		adicionadorLink.adicionarLink(cliente);
	    		return resposta;
	    	}
		} catch (EntityNotFoundException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}
	
	@PutMapping("/atualizar/{telefoneId}")
	public ResponseEntity<?> atualizarTelefone(@PathVariable Long telefoneId, @RequestBody Telefone novoTelefone) {
		try {
			Telefone telefone = repositorioTelefone.getById(telefoneId);
			TelefoneAtualizador atualizador = new TelefoneAtualizador();
			HttpStatus status = HttpStatus.BAD_REQUEST;
			if (telefone != null) {
			
				atualizador.atualizar(telefone, novoTelefone);		    
				repositorioTelefone.save(telefone);
				status = HttpStatus.OK;
			} else {
				status = HttpStatus.NOT_FOUND;
			}
			return new ResponseEntity<>(status);
		} catch (EntityNotFoundException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}
	
	@DeleteMapping("/excluir/{clienteId}/{telefoneId}")
	public ResponseEntity<?> deletarTelefone(@PathVariable Long clienteId, @PathVariable Long telefoneId) {
		try {
	    	Cliente cliente = repositorioCliente.getById(clienteId);
	    	HttpStatus status = HttpStatus.BAD_REQUEST;
	    	if (cliente != null && telefoneId != null)  {
	    		List<Telefone> telefones = cliente.getTelefones();
	    		telefones.removeIf(telefone -> telefone.getId().equals(telefoneId));    
	    		repositorioCliente.save(cliente);
	    		status = HttpStatus.OK;
	    	}
	    	return new ResponseEntity<>(status);
		} catch (EntityNotFoundException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}
	
}
