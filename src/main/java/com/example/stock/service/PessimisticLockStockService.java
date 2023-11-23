package com.example.stock.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;

@Service
public class PessimisticLockStockService {

	private final StockRepository stockRepository;

	public PessimisticLockStockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	/**
	 * Pessimistic Lock
	 * 실제로 데이터에 Lock 을 걸어서 정합성을 맞추는 방법입니다.
	 * exclusive lock 을 걸게되며 다른 트랜잭션에서는 lock 이 해제되기전에 데이터를 가져갈 수 없게됩니다.
	 * 데드락이 걸릴 수 있기때문에 주의하여 사용하여야 합니다.
	 * [장점]
	 * 충돌이 빈번하게 일어난다면 Optimistic Lock보다 성능이 좋음
	 * 락을 통해 데이터 정합성 보장
	 * [단점]
	 * 별도의 락 성능 감소
	 */
	@Transactional
	public synchronized void decrease(Long id, Long quantity) {
		Stock stock = stockRepository.findByWithPessimisticLock(id);
		stock.decrease(quantity);
		stockRepository.saveAndFlush(stock);
	}
}
