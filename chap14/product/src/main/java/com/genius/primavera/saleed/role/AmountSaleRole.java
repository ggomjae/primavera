package com.genius.primavera.saleed.role;

import com.genius.primavera.Product;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AmountSaleRole implements Saleable {

	@Override
	public boolean isSaleable(Product product) {
		return true;
	}
}
