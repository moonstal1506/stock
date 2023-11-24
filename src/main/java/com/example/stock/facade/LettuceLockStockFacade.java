package com.example.stock.facade;

import org.springframework.stereotype.Component;

import com.example.stock.repository.LockRepository;
import com.example.stock.repository.RedisLockRepository;
import com.example.stock.service.StockService;

@Component
public class LettuceLockStockFacade {

	/**
	 * 구현 쉬움
	 * 스핀락 방식 -> 레디스에 부하
	 * 쓰레드 슬립으로 락획득 재시도 텀을 둬야함
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
