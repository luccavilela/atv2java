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
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.modelos.AdicionadorLinkCliente;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@RestController
@RequestMapping("/documento")
public class DocumentoControle {
	@Autowired 
	private ClienteRepositorio repositorioCliente;
	@Autowired
	private AdicionadorLinkCliente adicionadorLink;
	
	@PostMapping("/adicionar/{clienteId}")
    public ResponseEntity<?> adicionarDocumento(@PathVariable Long clienteId, @RequestBody Documento documento) {
        Cliente cliente = repositorioCliente.getById(clienteId);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (cliente.getId() != null) {
        	cliente.getDocumentos().add(documento);
            repositorioCliente.save(cliente);
			status = HttpStatus.CREATED;
		}
        return new ResponseEntity<>(status);
    }
	
	
	@GetMapping("/documentos/{clienteId}")
	public ResponseEntity<List<Documento>> obterDocumentosCliente(@PathVariable Long clienteId) {
	    Cliente cliente = repositorioCliente.getById(clienteId);
	    if (cliente == null) {
	    	ResponseEntity<List<Documento>> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
	    } else {
	    	ResponseEntity<List<Documento>> resposta = new ResponseEntity<List<Documento>>(cliente.getDocumentos(),HttpStatus.FOUND);
	    	adicionadorLink.adicionarLink(cliente);
			return resposta;
	    }
	}
	

	
	@PutMapping("/atualizar/{clienteId}/{documentoId}")
	public ResponseEntity<?> atualizarDocumento(@PathVariable Long clienteId, @PathVariable Long documentoId, @RequestBody Documento novoDocumento) {
		Cliente cliente = repositorioCliente.getById(clienteId);
		HttpStatus status = HttpStatus.BAD_REQUEST;
		if (cliente != null) {
			
			List<Documento> documentos = cliente.getDocumentos();
			for (Documento documento : documentos) {
	    	
				if (documento.getId().equals(documentoId)) {
					documento.setTipo(novoDocumento.getTipo());
					documento.setNumero(novoDocumento.getNumero());
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
	
	@DeleteMapping("/excluir/{clienteId}/{documentoId}")
	public ResponseEntity<?> deletarDocumento(@PathVariable Long clienteId, @PathVariable Long documentoId) {
	    Cliente cliente = repositorioCliente.getById(clienteId);
	    HttpStatus status = HttpStatus.BAD_REQUEST;
	    if (cliente != null && documentoId != null)  {
	    	List<Documento> documentos = cliente.getDocumentos();
		    documentos.removeIf(documento -> documento.getId().equals(documentoId));    
		    repositorioCliente.save(cliente);
		    status = HttpStatus.OK;
	    }
	    return new ResponseEntity<>(status);
	}
}
