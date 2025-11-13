package com.foodtracker.api.analytics;

import java.util.Map;

public interface ConversionFunnel {
    String getCategory();

    long getViewedCount();

    long getAddedCount();

    long getOrderedCount();

    double getConversionRate();

    Map<String, Object> getAdditionalMetrics();
}
