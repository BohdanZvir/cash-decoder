package com.burtyka.cash.core;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
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

}
