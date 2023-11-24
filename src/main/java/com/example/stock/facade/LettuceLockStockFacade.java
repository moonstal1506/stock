package com.example.stock.facade;

import org.springframework.stereotype.Component;

import com.example.stock.repository.LockRepository;
import com.example.stock.repository.RedisLockRepository;
import com.example.stock.service.StockService;

@Component
public class LettuceLockStockFacade {

	/**
	 * [Lettuce]
	 * 구현이 간단하다
	 * spring data redis 를 이용하면 lettuce 가 기본이기때문에 별도의 라이브러리를 사용하지 않아도 된다.
	 * spin lock 방식이기때문에 동시에 많은 스레드가 lock 획득 대기 상태라면 redis 에 부하가 갈 수 있다.
	 * 쓰레드 슬립으로 락획득 재시도 텀을 둬야함
	 * -> 재시도가 필요하지 않은 lock 은 lettuce 활용
	 */

	private final RedisLockRepository redisLockRepository;

	private final StockService stockService;

	public LettuceLockStockFacade(RedisLockRepository redisLockRepository, StockService stockService) {
		this.redisLockRepository = redisLockRepository;
		this.stockService = stockService;
	}

	public void decrease(Long id, Long quantity) throws InterruptedException {
		//락 획득
		while (!redisLockRepository.lock(id)) {
			Thread.sleep(100);
		}

		try {
			stockService.decrease(id, quantity);
		} finally {
			redisLockRepository.unlock(id);
		}
	}
}
