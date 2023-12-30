package com.marcos.pagamentos.controller;

import com.marcos.pagamentos.dto.PagamentoDto;
import com.marcos.pagamentos.service.PagamentoService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

  @Autowired
  private PagamentoService pagamentoService;

  @GetMapping
  public Page<PagamentoDto> listar(@PageableDefault(size = 10) Pageable paginacao) {
    return pagamentoService.getAll(paginacao);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PagamentoDto> detalhar(@PathVariable @NotNull Long id) {
    PagamentoDto dto = pagamentoService.getById(id);
    return ResponseEntity.ok(dto);
  }

  @PostMapping
  public ResponseEntity<PagamentoDto> cadastrar(@RequestBody @Valid PagamentoDto dto, UriComponentsBuilder uriBuilder) {
    PagamentoDto pagamento = pagamentoService.create(dto);
    URI endereco = uriBuilder.path("/pagamentos/{id}").buildAndExpand(pagamento.getId()).toUri();

    return ResponseEntity.created(endereco).body(pagamento);
  }

  @PutMapping("/{id}")
  public ResponseEntity<PagamentoDto> atualizar(@PathVariable @NotNull Long id, @RequestBody @Valid PagamentoDto dto) {
    PagamentoDto atualizado = pagamentoService.update(id, dto);
    return ResponseEntity.ok(atualizado);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<PagamentoDto> remover(@PathVariable @NotNull Long id) {
    pagamentoService.remove(id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/confirmar")
  @CircuitBreaker(name = "atualizaPedido", fallbackMethod = "pagamentoAutorizadoComIntegracaoPendente")
  public void confirmarPagamento(@PathVariable @NotNull Long id){
    pagamentoService.confirmarPagamento(id);
  }

  public void pagamentoAutorizadoComIntegracaoPendente(Long id, Exception e){
    pagamentoService.alteraStatus(id);
  }

}
