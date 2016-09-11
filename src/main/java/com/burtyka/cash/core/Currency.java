package com.burtyka.cash.core;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Currency  implements Serializable {

	private static final long serialVersionUID = 4037227943016334848L;

    java.lang.String abbreviation;
    java.lang.String id;
    java.lang.String name;

}
