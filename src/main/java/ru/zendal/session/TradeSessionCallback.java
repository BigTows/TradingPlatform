/*
 * Copyright (c) Alexander <gasfull98@gmail.com> Chapchuk
 * Project name: TradingPlatform
 *
 * Licensed under the MIT License. See LICENSE file in the project root for license information.
 */

package ru.zendal.session;

public interface TradeSessionCallback {

    public void onReady(Session tradeSession);

    public void processTrade(Session tradeSession);
}
