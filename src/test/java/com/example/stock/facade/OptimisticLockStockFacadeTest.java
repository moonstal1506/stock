package com.example.stock.facade;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;

@SpringBootTest
class OptimisticLockStockFacadeTest {
	@Autowired
	private OptimisticLockStockFacade optimisticLockStockFacade;

	@Autowired
	private StockRepository stockRepository;

	@BeforeEach
	public void before() {
		stockRepository.saveAndFlush(new Stock(1L, 100L));
	}

	@AfterEach
	public void after() {
		stockRepository.deleteAll();
	}


	@Test
	public void 동시에_100개의_요청() throws InterruptedException {
		int threadCount = 100;
		//멀티쓰레드 이용, 비동기로 실행 작업 단순화 하여 사용하게 도와줌
		ExecutorService executorService = Executors.newFixedThreadPool(32);

		//100개의 요청이 끝날때까지 기다려야 함
		//다른 쓰레드에서 진행중인 작업이 완료될 때까지 대기할 수 있도록 도와주는 클래스
		CountDownLatch latch = new CountDownLatch(threadCount);

		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					try {
						optimisticLockStockFacade.decrease(1L, 1L);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();
		Stock stock = stockRepository.findById(1L).orElseThrow();

		//레이스 컨디션: 둘이상의 쓰레드가 공유데이터에 access, 동시에 변경하려고 할 때 발생
		//쓰레드1: 100에서 1감소 99
		//쓰레드2: 쓰레드1이 감소 되기전 재고를 가져가-> 100에서 1감소 99
		//해결방법: 하나의 쓰레드가 작업이 완료된 이후 다른 쓰레드가 데이터에 접근 할 수 있도록
		assertEquals(0, stock.getQuantity());
	}
}