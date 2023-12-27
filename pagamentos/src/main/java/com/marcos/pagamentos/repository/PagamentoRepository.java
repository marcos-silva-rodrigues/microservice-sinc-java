package com.marcos.pagamentos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.marcos.pagamentos.model.Pagamento;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long>{
  
}
