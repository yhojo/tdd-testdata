package com.blueprintmonkey.tdd;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class GenTestDataUtilTest {

	@Test
	public void testRange() {
		Iterable<Integer> range = GenTestDataUtil.range(0, 10);
		assertEquals(10, Iterables.size(range));
		Integer[] array = Iterables.toArray(range, Integer.class);
		for (int i = 0; i < 10; i++) {
			assertEquals((Integer) i, array[i]);
		}
	}

	@Test
	public void testOddEven() {
		assertTrue(GenTestDataUtil.odd().apply(1));
		assertTrue(GenTestDataUtil.even().apply(0));

		Iterable<Integer> odd = Iterables.filter(GenTestDataUtil.range(0, 10), GenTestDataUtil.odd());
		assertEquals(5, Iterables.size(odd));
		assertEquals((Integer) 1, Iterables.getFirst(odd, null));
	}

	@Test
	public void testCreateProduct() {
		Product product = GenTestDataUtil.createProduct(5000);
		assertEquals(new BigDecimal(500), product.getUnitPrice());
		assertFalse(StringUtils.isEmpty(product.getSkuNumber()));
		assertFalse(StringUtils.isEmpty(product.getName()));
	}

	@Test
	public void testCreateProductWithPriceRank() {
		assertEquals(new BigDecimal(100), 
				GenTestDataUtil.createProductWithPriceRank(0).getUnitPrice());
		assertEquals(new BigDecimal(200), 
				GenTestDataUtil.createProductWithPriceRank(1).getUnitPrice());
		assertEquals(new BigDecimal(300), 
				GenTestDataUtil.createProductWithPriceRank(2).getUnitPrice());
		assertEquals(new BigDecimal(400), 
				GenTestDataUtil.createProductWithPriceRank(3).getUnitPrice());
		assertEquals(new BigDecimal(500), 
				GenTestDataUtil.createProductWithPriceRank(4).getUnitPrice());
		
	}

	@Test
	public void testCreateProductsWithPrice() {
		Iterable<Product> products = GenTestDataUtil.createProductsWithTotalPrice(new BigDecimal(1500));
		assertTrue(1 < Iterables.size(products));
		BigDecimal total = BigDecimal.ZERO;
		for (Product product: products) {
			total = total.add(product.getUnitPrice());
		}
		assertEquals(new BigDecimal(1500), total);
	}

	@Test
	public void testCreateSalesWithPrice() {
		Sales sales = GenTestDataUtil.createSalesWithTotalPrice(date("2012/01/01"), new BigDecimal(1500));
		assertEquals(new BigDecimal(1500), sales.getTotalPrice());
		assertTrue(1 < Iterables.size(sales.getDetails()));
		assertEquals(date("2012/01/01"), sales.getDate());
	}

	@Test
	public void testCreateSalesSlipsWithPrices() {
		Iterable<Sales> sales = GenTestDataUtil.createSalesWithPrices(date("2012/01/01"), 1000, 1200, 1600);
		assertEquals(3, Iterables.size(sales));
		Sales[] array = Iterables.toArray(sales, Sales.class);
		assertEquals(new BigDecimal(1000), array[0].getTotalPrice());
		assertEquals(new BigDecimal(1200), array[1].getTotalPrice());
		assertEquals(new BigDecimal(1600), array[2].getTotalPrice());
	}

	private Date date(String str) {
		try {
			return DateUtils.parseDate(str, "yyyy/MM/dd");
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testCreateDateRangeSales() {
		Iterable<Date> dateRange = GenTestDataUtil.dateRange(date("2012/01/01"), date("2012/02/01"));
		assertEquals(date("2012/01/01"), Iterables.getFirst(dateRange, null));
		assertEquals(date("2012/01/31"), Iterables.getLast(dateRange));
		assertEquals(31, Iterables.size(dateRange));
		Iterable<Sales> sales = Iterables.concat(Iterables.transform(
				dateRange,
				new Function<Date, Iterable<Sales>>() {
					@Override
					public Iterable<Sales> apply(Date input) {
						return GenTestDataUtil.createSalesWithPrices(input, 1000, 1200, 1600);
					}
				}));
		assertEquals(31 * 3, Iterables.size(sales));
		BigDecimal totalAmount = BigDecimal.ZERO;
		for (Sales s: sales) {
			totalAmount = totalAmount.add(s.getTotalPrice());
		}
		assertEquals(new BigDecimal(31 * (1000 + 1200 + 1600)), totalAmount);
		assertEquals(date("2012/01/01"), Iterables.getFirst(sales, null).getDate());
		assertEquals(date("2012/01/31"), Iterables.getLast(sales).getDate());
	}
}
