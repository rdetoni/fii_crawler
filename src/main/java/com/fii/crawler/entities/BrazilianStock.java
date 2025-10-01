package com.fii.crawler.entities;

import lombok.Builder;

@Builder
public record BrazilianStock(String name, String ticker) {}
