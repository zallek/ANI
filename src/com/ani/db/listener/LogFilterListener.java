package com.ani.db.listener;

import java.util.EventListener;
import java.util.List;

import com.ani.db.entity.LogFilter;

public interface LogFilterListener extends EventListener {
	void onLogFilterChange(List<LogFilter> newLogFilterList);
}
