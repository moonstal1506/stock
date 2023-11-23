package com.example.stock.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;

@Service
public class StockService {

	private final StockRepository stockRepository;

	public StockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	//synchronized: 해당 메소드에 하나의 쓰레드만 접근 가능
	//synchronized 문제점: 서버가 2대 이상일 경우 데이터 접근 여러대, 각 프로세스 내에서만 보장
	//서버1가 재고 100 가져가, 서버2도 재고 100 가져가 문제
	//트랜잭션 decrease 완료 후 실제 데이터베이스가 업데이트 되기 전 다른 쓰레드가 decrease 호출
	// @Transactional
	public synchronized void decrease(Long id, Long quantity) {
		//stock 조회
		//재고를 감소한 뒤
		//갱신된 값을 저장
		Stock stock = stockRepository.findById(id).orElseThrow();
		stock.decrease(quantity);
		stockRepository.saveAndFlush(stock);
	}
}
