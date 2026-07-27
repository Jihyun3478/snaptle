package com.snaptle.global.exchangerate;

import com.snaptle.global.common.Currency;
import java.math.BigDecimal;
import java.util.Optional;

public interface ExchangeRateClient {

    /**
     * @return 1 {@code from}가 몇 {@code to}인지의 환율. 조회 실패/미설정 시 empty.
     */
    Optional<BigDecimal> getRate(Currency from, Currency to);
}
