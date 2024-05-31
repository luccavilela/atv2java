package com.autobots.automanager.controles;


import java.util.List;
import java.util.NoSuchElementException;

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

import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.modelos.AdicionadorLinkEndereco;
import com.autobots.automanager.modelos.EnderecoAtualizador;
import com.autobots.automanager.repositorios.EnderecoRepositorio;

@RestController
@RequestMapping("/endereco")
public class EnderecoControle {
	
	@Autowired 
	private EnderecoRepositorio repositorioEndereco;
	
	@Autowired
	private AdicionadorLinkEndereco adicionadorLink;
	
	@GetMapping("/endereco/{enderecoId}")
	public ResponseEntity<Endereco> obterEndereco(@PathVariable Long enderecoId) {
	    try {
	        Endereco endereco = repositorioEndereco.findById(enderecoId).orElseThrow(() -> new EntityNotFoundException("Endereco não encontrado"));  
	        adicionadorLink.adicionarLink(endereco);
	        
	        return new ResponseEntity<>(endereco, HttpStatus.OK);
	    } catch (EntityNotFoundException | NoSuchElementException e) {
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    } catch (Exception e) {
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	@GetMapping("enderecos")
	public ResponseEntity<List<Endereco>> obterEnderecos() {
		List<Endereco> enderecos = repositorioEndereco.findAll();
		if (enderecos.isEmpty()) {
			ResponseEntity<List<Endereco>> resposta = new ResponseEntity<>(HttpStatus.NOT_FOUND);
			return resposta;
		} else {
			adicionadorLink.adicionarLink(enderecos);
			ResponseEntity<List<Endereco>> resposta = new ResponseEntity<>(enderecos, HttpStatus.FOUND);
			return resposta;
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
