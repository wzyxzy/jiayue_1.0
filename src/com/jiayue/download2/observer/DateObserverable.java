package com.jiayue.download2.observer;

import android.database.Observable;

import com.jiayue.download2.entity.DocInfo;

public class DateObserverable extends Observable<DataObserver> {

	public void notifyChanged() {
		synchronized (mObservers) {
			for (DataObserver observer : mObservers) {
				observer.onChanged(mDocInfo);
			}
		}
	}

	public void notifyInvalidated() {
		synchronized (mObservers) {
			for (DataObserver observer : mObservers) {
				observer.onInvalidated(mDocInfo);
			}
		}
	}
	
	private DocInfo mDocInfo;

	public void setCurrentDoc(DocInfo info) {
		this.mDocInfo = info;
	}
}
