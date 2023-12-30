package com.marcos.pagamentos.service;

import com.marcos.pagamentos.http.PedidoClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.marcos.pagamentos.dto.PagamentoDto;
import com.marcos.pagamentos.model.Pagamento;
import com.marcos.pagamentos.model.Status;
import com.marcos.pagamentos.repository.PagamentoRepository;

import jakarta.persistence.EntityNotFoundException;

import java.util.Optional;

@Service
public class PagamentoService {

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private PedidoClient pedidoClient;

  @Autowired
  private PagamentoRepository repository;

  public Page<PagamentoDto> getAll(Pageable paginacao) {
    return repository
        .findAll(paginacao)
        .map(p -> modelMapper.map(p, PagamentoDto.class));
  }

  public PagamentoDto getById(Long id) {
    Pagamento pagamento = repository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException());

    return modelMapper.map(pagamento, PagamentoDto.class);
  }

  public PagamentoDto create(PagamentoDto dto) {
    Pagamento pagamento = modelMapper.map(dto, Pagamento.class);
    pagamento.setStatus(Status.CRIADO);
    repository.save(pagamento);

    return modelMapper.map(pagamento, PagamentoDto.class);
  }

  public PagamentoDto update(Long id, PagamentoDto dto) {
    Pagamento pagamento = modelMapper.map(dto, Pagamento.class);
    pagamento.setId(id);
    pagamento = repository.save(pagamento);
    return modelMapper.map(pagamento, PagamentoDto.class);
  }

  public void remove(Long id) {
    repository.deleteById(id);
  }

  public void confirmarPagamento(Long id){
    Optional<Pagamento> pagamento = repository.findById(id);

    if (!pagamento.isPresent()) {
      throw new EntityNotFoundException();
    }

    pagamento.get().setStatus(Status.CONFIRMADO);
    repository.save(pagamento.get());
    pedidoClient.atualizaPagamento(pagamento.get().getPedidoId());
  }

  public void alteraStatus(Long id) {
    Optional<Pagamento> pagamento = repository.findById(id);

    if (!pagamento.isPresent()) {
      throw new EntityNotFoundException();
    }

    pagamento.get().setStatus(Status.CONFIRMADO_SEM_INTEGRACAO);
    repository.save(pagamento.get());

  }
}
