package com.example.stock.facade;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import com.example.stock.repository.RedisLockRepository;
import com.example.stock.service.StockService;

@Component
public class RedissonLockStockFacade {

	/**
	 * [Redisson]
	 * 락 획득 재시도를 기본으로 제공한다.
	 * pub-sub 방식으로 구현이 되어있기 때문에 lettuce 와 비교했을 때 redis 에 부하가 덜 간다.
	 * 별도의 라이브러리를 사용해야한다.
	 * lock 을 라이브러리 차원에서 제공해주기 때문에 사용법을 공부해야 한다.
	 * -> 재시도가 필요한 경우에는 redisson를 활용
	 */

	private final RedissonClient redissonClient;

	private final StockService stockService;

	public RedissonLockStockFacade(RedissonClient redissonClient, StockService stockService) {
		this.redissonClient = redissonClient;
		this.stockService = stockService;
	}

	public void decrease(Long id, Long quantity) {
		RLock lock = redissonClient.getLock(id.toString());
		try {
			boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);

			if (!available) {
				System.out.println("lock 획득 실패");
				return;
			}

			stockService.decrease(id, quantity);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}
}
