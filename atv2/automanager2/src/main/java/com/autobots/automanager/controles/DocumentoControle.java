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
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.modelos.DocumentoAtualizador;
import com.autobots.automanager.modelos.AdicionadorLinkCliente;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.DocumentoRepositorio;

@RestController
@RequestMapping("/documento")
public class DocumentoControle {
	@Autowired 
	private ClienteRepositorio repositorioCliente;
	
	@Autowired 
	private DocumentoRepositorio repositorioDocumento;
	
	@Autowired
	private AdicionadorLinkCliente adicionadorLink;
	
	@PostMapping("/adicionar/{clienteId}")
	public ResponseEntity<?> adicionarDocumento(@PathVariable Long clienteId, @RequestBody Documento documento) {
	    try {
	        Cliente cliente = repositorioCliente.getById(clienteId);
	        cliente.getDocumentos().add(documento);
	        repositorioCliente.save(cliente);
	        return new ResponseEntity<>(HttpStatus.CREATED);
	    } catch (EntityNotFoundException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}
	
	
	@GetMapping("/documentos/{clienteId}")
	public ResponseEntity<List<Documento>> obterDocumentosCliente(@PathVariable Long clienteId) {
		try {
	        Cliente cliente = repositorioCliente.getById(clienteId);
	        ResponseEntity<List<Documento>> resposta = new ResponseEntity<>(cliente.getDocumentos(), HttpStatus.OK);
	        adicionadorLink.adicionarLink(cliente);
	        return resposta;
	    } catch (EntityNotFoundException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}
	

	
	@PutMapping("/atualizar/{documentoId}")
	public ResponseEntity<?> atualizarDocumento(@PathVariable Long documentoId, @RequestBody Documento novoDocumento) {
	    try {
	        Documento documento = repositorioDocumento.getById(documentoId);
	        DocumentoAtualizador atualizador = new DocumentoAtualizador();
	        HttpStatus status;
	        if (documento != null) {
	            atualizador.atualizar(documento, novoDocumento);
	            repositorioDocumento.save(documento);
	            status = HttpStatus.OK;
	        } else {
	            status = HttpStatus.NOT_FOUND;
	        }
	        return new ResponseEntity<>(status);
	    } catch (EntityNotFoundException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}
	
	@DeleteMapping("/excluir/{clienteId}/{documentoId}")
	public ResponseEntity<?> deletarDocumento(@PathVariable Long clienteId, @PathVariable Long documentoId) {
		try {
	    	Cliente cliente = repositorioCliente.getById(clienteId);
	    	HttpStatus status = HttpStatus.BAD_REQUEST;
	    	if (cliente != null && documentoId != null)  {
	    		List<Documento> documentos = cliente.getDocumentos();
	    		documentos.removeIf(documento -> documento.getId().equals(documentoId));    
	    		repositorioCliente.save(cliente);
	    		status = HttpStatus.OK;
	    	}
	    	return new ResponseEntity<>(status);
		} catch (EntityNotFoundException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    }
	}
}
