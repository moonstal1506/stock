package com.example.stock.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;

@Service
public class OptimisticLockStockService {

	private final StockRepository stockRepository;

	public OptimisticLockStockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	/**
	 * Optimistic Lock
	 * 실제로 Lock 을 이용하지 않고 버전을 이용함으로써 정합성을 맞추는 방법입니다.
	 * 먼저 데이터를 읽은 후에 update 를 수행할 때 현재 내가 읽은 버전이 맞는지 확인하며 업데이트 합니다.
	 * 내가 읽은 버전에서 수정사항이 생겼을 경우에는 application에서 다시 읽은후에 작업을 수행해야 합니다.
	 * [장점]
	 * 별도의 락을 잡지 않아 Pessimistic Lock보다 성능상 이점
	 * [단점]
	 * 업데이트 실패 시 재시도 로직 개발자가 작성해야함
	 * 충돌 빈번 -> Pessimistic Lock
	 * 아니라면 -> Optimistic Lock
	 */
	@Transactional
	public synchronized void decrease(Long id, Long quantity) {
		Stock stock = stockRepository.findByIdWithOptimisticLock(id);
		stock.decrease(quantity);
		stockRepository.saveAndFlush(stock);
	}
}
