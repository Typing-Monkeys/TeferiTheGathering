package com.magicengine;

import java.util.List;

/**
 * Gestisce la priorità delle regole all'interno di drools, nel caso
 * caso in cui quest'ultime abbiano un ordine di esecuzione.
 */
public class RulePriorityCounter<T> extends ListPointer<T> {
	
	public final static Integer STATE_BASED_ACTIONS = 1;
	
	private Integer scope;
	
	public RulePriorityCounter(Integer scope, List<T> rules) {
		super(rules);
		
		this.setScope(scope);
	}

	public Integer getScope() {
		return scope;
	}

	public void setScope(Integer scope) {
		this.scope = scope;
	}
	
}
