package com.ani.db.listener;

import java.util.EventListener;

import com.ani.db.entity.BlackListItem;

public interface BlackListListener extends EventListener {
	void onAddBlackListItem(BlackListItem newBlackListItem);

	void onRemoveBlackListItem(BlackListItem newBlackListItem);
}
