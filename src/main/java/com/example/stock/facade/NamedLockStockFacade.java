package com.example.stock.facade;

import org.springframework.stereotype.Component;

import com.example.stock.repository.LockRepository;
import com.example.stock.service.StockService;

@Component
public class NamedLockStockFacade {

	/**
	 * NamedLock
	 * 이름을 가진 metadata locking 입니다.
	 * 이름을 가진 lock 을 획득한 후 해제할때까지 다른 세션은 이 lock 을 획득할 수 없도록 합니다.
	 * 주의할점으로는 transaction 이 종료될 때 lock 이 자동으로 해제되지 않습니다.
	 * 별도의 명령어로 해제를 수행해주거나 선점시간이 끝나야 해제됩니다.
	 * 분산락 구현할 때 사용
	 * [장점]
	 * Pessimistic Lock은 타임아웃 구현하기 힘들지만
	 * NamedLock은 타임아웃 구현하기 쉬움
	 * 데이터 정합성
	 * [단점]
	 * 락 해제, 세션 관리 잘해야함
	 * 구현 방법 복잡
	 */

	private final LockRepository lockRepository;

	private final StockService stockService;

	public NamedLockStockFacade(LockRepository lockRepository, StockService stockService) {
		this.lockRepository = lockRepository;
		this.stockService = stockService;
	}

	public void decrease(Long id, Long quantity) {
		try {
			lockRepository.getLock(id.toString());
			stockService.decrease(id, quantity);
		} finally {
			lockRepository.releaseLock(id.toString());
		}
	}
}
