package com.blueprintmonkey.tdd;

import java.math.BigDecimal;
import java.util.Date;

public interface Sales {
	Iterable<SalesDetail> getDetails();
	BigDecimal getTotalPrice();
	Date getDate();
}
