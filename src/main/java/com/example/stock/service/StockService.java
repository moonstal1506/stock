package com.example.stock.service;

import org.springframework.stereotype.Service;

import com.example.stock.repository.StockRepository;

@Service
public class StockService {

	private final StockRepository stockRepository;

	public StockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	public void decrease(Long id, Long quantity) {
		//stock 조회
		//재고를 감소한 뒤
		//갱신된 값을 저장
	}
}
