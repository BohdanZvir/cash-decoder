package com.burtyka.cash.core;

import java.io.Serializable;

public class Settings implements Serializable {

	private static final long serialVersionUID = 3340633318770345189L;
	
	java.util.ArrayList barChartUnchecked;
    com.dropbox.client2.session.AccessTokenPair dropBoxTokenPair;
    java.lang.Boolean fullScreenMode;
    java.lang.String language;
    java.lang.String mMoneyFormat;
    java.lang.String mTheme;
    java.lang.Boolean passwordProtection;
    java.util.ArrayList pieChartUnchecked;
    java.lang.String screenOrientation;
    
	public java.util.ArrayList getBarChartUnchecked() {
		return barChartUnchecked;
	}
	public void setBarChartUnchecked(java.util.ArrayList barChartUnchecked) {
		this.barChartUnchecked = barChartUnchecked;
	}
	public com.dropbox.client2.session.AccessTokenPair getDropBoxTokenPair() {
		return dropBoxTokenPair;
	}
	public void setDropBoxTokenPair(com.dropbox.client2.session.AccessTokenPair dropBoxTokenPair) {
		this.dropBoxTokenPair = dropBoxTokenPair;
	}
	public java.lang.Boolean getFullScreenMode() {
		return fullScreenMode;
	}
	public void setFullScreenMode(java.lang.Boolean fullScreenMode) {
		this.fullScreenMode = fullScreenMode;
	}
	public java.lang.String getLanguage() {
		return language;
	}
	public void setLanguage(java.lang.String language) {
		this.language = language;
	}
	public java.lang.String getmMoneyFormat() {
		return mMoneyFormat;
	}
	public void setmMoneyFormat(java.lang.String mMoneyFormat) {
		this.mMoneyFormat = mMoneyFormat;
	}
	public java.lang.String getmTheme() {
		return mTheme;
	}
	public void setmTheme(java.lang.String mTheme) {
		this.mTheme = mTheme;
	}
	public java.lang.Boolean getPasswordProtection() {
		return passwordProtection;
	}
	public void setPasswordProtection(java.lang.Boolean passwordProtection) {
		this.passwordProtection = passwordProtection;
	}
	public java.util.ArrayList getPieChartUnchecked() {
		return pieChartUnchecked;
	}
	public void setPieChartUnchecked(java.util.ArrayList pieChartUnchecked) {
		this.pieChartUnchecked = pieChartUnchecked;
	}
	public java.lang.String getScreenOrientation() {
		return screenOrientation;
	}
	public void setScreenOrientation(java.lang.String screenOrientation) {
		this.screenOrientation = screenOrientation;
	}
	
	@Override
	public String toString() {
		return "Settings [barChartUnchecked=" + barChartUnchecked + ", dropBoxTokenPair=" + dropBoxTokenPair
				+ ", fullScreenMode=" + fullScreenMode + ", language=" + language + ", mMoneyFormat=" + mMoneyFormat
				+ ", mTheme=" + mTheme + ", passwordProtection=" + passwordProtection + ", pieChartUnchecked="
				+ pieChartUnchecked + ", screenOrientation=" + screenOrientation + "]";
	}

}
