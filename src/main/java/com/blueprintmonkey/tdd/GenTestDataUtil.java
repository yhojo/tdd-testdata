package com.blueprintmonkey.tdd;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import org.apache.commons.lang3.time.DateUtils;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterables;

public class GenTestDataUtil {

	public static Iterable<Integer> range(final int from, final int to) {
		return new Iterable<Integer>() {
			@Override
			public Iterator<Integer> iterator() {
				return new AbstractIterator<Integer>() {
					private int current = from;
					@Override
					protected Integer computeNext() {
						if (current < to) {
							return current++;
						} else {
							return endOfData();
						}
					}
				};
			}
		};
	}

	public static Predicate<Integer> odd() {
		return new Predicate<Integer>() {
			@Override
			public boolean apply(Integer input) {
				return input % 2 != 0;
			}
		};
	}

	public static Predicate<Integer> even() {
		return new Predicate<Integer>() {
			@Override
			public boolean apply(Integer input) {
				return input % 2 == 0;
			}
		};
	}

	public static Product createProduct(final int seedNumber) {
		return new Product() {
			@Override
			public BigDecimal getUnitPrice() {
				int priceRank = seedNumber / 1000;
				return new BigDecimal((priceRank) * 100);
			}
			
			@Override
			public String getSkuNumber() {
				return String.format("SKU%04d", seedNumber);
			}
			
			@Override
			public String getName() {
				return String.format("NAME%04d", seedNumber);
			}
		};
	}

	public static Product createProductWithPriceRank(int priceRank) {
		return createProduct((priceRank + 1) * 1000);
	}

	public static Iterable<Product> createProductsWithTotalPrice(
			final BigDecimal totalPrice) {
		return new Iterable<Product>() {
			@Override
			public Iterator<Product> iterator() {
				return new AbstractIterator<Product>() {
					BigDecimal price = totalPrice;
					@Override
					protected Product computeNext() {
						if (price.compareTo(BigDecimal.ZERO) <= 0) {
							return endOfData();
						}
						Product product;
						if (price.compareTo(new BigDecimal(500)) >= 0) {
							product = createProductWithPriceRank(new Random().nextInt(5));
						} else {
							product = createProductWithPriceRank((price.intValue() / 100) - 1);
						}
						price = price.subtract(product.getUnitPrice());
						return product;
					}
				};
			}
		};
	}

	public static Sales createSalesWithTotalPrice(final Date date, final BigDecimal totalPrice) {
		return new Sales() {
			private Iterable<SalesDetail> details = Iterables.transform(createProductsWithTotalPrice(totalPrice),
					new Function<Product, SalesDetail>() {
						@Override
						public SalesDetail apply(final Product input) {
							return new SalesDetail() {
								@Override
								public BigDecimal getUnitPrice() {
									return input.getUnitPrice();
								}

								@Override
								public Product getProduct() {
									return input;
								}
							};
						}
			});

			@Override
			public Date getDate() {
				return date;
			}
			@Override
			public BigDecimal getTotalPrice() {
				return totalPrice;
			}
			
			@Override
			public Iterable<SalesDetail> getDetails() {
				return details;
			}
		};
	}

	public static Iterable<Sales> createSalesWithPrices(final Date date, Integer... prices) {
		return Iterables.transform(Arrays.asList(prices), new Function<Integer, Sales>() {
			@Override
			public Sales apply(Integer input) {
				return createSalesWithTotalPrice(date, new BigDecimal(input));
			}
		});
	}

	public static Iterable<Date> dateRange(final Date from, final Date to) {
		return new Iterable<Date>() {
			@Override
			public Iterator<Date> iterator() {
				return new AbstractIterator<Date>() {
					private Date current = from;
					@Override
					protected Date computeNext() {
						if (current.compareTo(to) < 0) {
							Date ret = current;
							current = DateUtils.addDays(current, 1);
							return ret;
						} else {
							return endOfData();
						}
					}
				};
			}
		};
	}
}
