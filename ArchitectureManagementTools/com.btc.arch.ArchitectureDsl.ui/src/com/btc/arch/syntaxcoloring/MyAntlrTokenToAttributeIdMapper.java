package com.btc.arch.syntaxcoloring;

import org.eclipse.xtext.ui.editor.syntaxcoloring.antlr.DefaultAntlrTokenToAttributeIdMapper;

public class MyAntlrTokenToAttributeIdMapper extends DefaultAntlrTokenToAttributeIdMapper {
/*
	@Override
	protected String calculateId(String tokenName, int tokenType) {
		if( "'BTCUnitTester'".equals(tokenName)) {
			return MyHighLightingConfiguration.LAYOUT_ID;
		}
		return super.calculateId(tokenName, tokenType);
	}
	*/
}
