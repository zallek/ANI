package com.ani.blacklist;

import android.content.Context;

import com.ani.db.entity.BlackListItem;
import com.ani.db.listener.BlackListListener;

public abstract class BlackListerAbstract {
	protected Context context;

	public BlackListerAbstract(Context context) {
		this.context = context;
		// TODO : This method is not static...
		// DbModel.addBlackListListener(blackListListener);

		// TODO : There is NullPointerException at this point.
		// ANIUtils.getDatabase().addBlackListListener(blackListListener);
	}

	private BlackListListener blackListListener = new BlackListListener() {
		@Override
		public void onAddBlackListItem(BlackListItem newBlackListItem) {
			enable(newBlackListItem);
		}

		@Override
		public void onRemoveBlackListItem(BlackListItem newBlackListItem) {
			disable(newBlackListItem);
		}
	};

	protected abstract void enable(BlackListItem blItem);

	protected abstract void disable(BlackListItem blItem);
}
