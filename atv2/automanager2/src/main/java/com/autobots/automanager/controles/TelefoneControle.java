package com.autobots.automanager.controles;

import java.util.List;

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
import com.autobots.automanager.modelos.AdicionadorLinkCliente;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@RestController
@RequestMapping("/telefone")
public class TelefoneControle {
	@Autowired 
	private ClienteRepositorio repositorioCliente;
	@Autowired
	private AdicionadorLinkCliente adicionadorLink;
	
	@PostMapping("/adicionar/{clienteId}")
    public ResponseEntity<?> adicionarDocumento(@PathVariable Long clienteId, @RequestBody Telefone telefone) {
        Cliente cliente = repositorioCliente.getById(clienteId);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (cliente.getId() != null) {
        	cliente.getTelefones().add(telefone);
            repositorioCliente.save(cliente);
			status = HttpStatus.CREATED;
		}
        return new ResponseEntity<>(status);
    }
	
	@GetMapping("/telefones/{clienteId}")
	public ResponseEntity<List<Telefone>> obterTelefonesCliente(@PathVariable Long clienteId) {
	    Cliente cliente = repositorioCliente.getById(clienteId);
	    if (cliente == null) {
	    	ResponseEntity<List<Telefone>> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
	    } else {
	    	ResponseEntity<List<Telefone>> resposta = new ResponseEntity<List<Telefone>>(cliente.getTelefones(),HttpStatus.FOUND);
	    	adicionadorLink.adicionarLink(cliente);
			return resposta;
	    }
	}
	
	@PutMapping("/atualizar/{clienteId}/{telefoneId}")
	public ResponseEntity<?> atualizarTelefone(@PathVariable Long clienteId, @PathVariable Long telefoneId, @RequestBody Telefone novoTelefone) {
		Cliente cliente = repositorioCliente.getById(clienteId);
		HttpStatus status = HttpStatus.BAD_REQUEST;
		if (cliente != null) {
			
			List<Telefone> telefones = cliente.getTelefones();
			for (Telefone telefone : telefones) {
	    	
				if (telefone.getId().equals(telefoneId)) {
					telefone.setDdd(novoTelefone.getDdd());
					telefone.setNumero(novoTelefone.getNumero());
					repositorioCliente.save(cliente);
					status = HttpStatus.CREATED;
					break;
					
				} else {
					status = HttpStatus.NOT_FOUND;
				}
			}
		}
		return new ResponseEntity<>(status);
	}
	
	@DeleteMapping("/excluir/{clienteId}/{telefoneId}")
	public ResponseEntity<?> deletarTelefone(@PathVariable Long clienteId, @PathVariable Long telefoneId) {
	    Cliente cliente = repositorioCliente.getById(clienteId);
	    HttpStatus status = HttpStatus.BAD_REQUEST;
	    if (cliente != null && telefoneId != null)  {
	    	List<Telefone> telefones = cliente.getTelefones();
		    telefones.removeIf(telefone -> telefone.getId().equals(telefoneId));    
		    repositorioCliente.save(cliente);
		    status = HttpStatus.OK;
	    }
	    return new ResponseEntity<>(status);
	}
	
}
