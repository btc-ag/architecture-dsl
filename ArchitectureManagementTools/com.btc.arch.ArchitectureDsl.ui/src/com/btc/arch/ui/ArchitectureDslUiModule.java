/*
 * generated by Xtext
 */
package com.btc.arch.ui;

import org.eclipse.b3.beelang.ui.xtext.linked.ExtLinkedXtextEditor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.antlr.AbstractAntlrTokenToAttributeIdMapper;

import com.btc.arch.syntaxcoloring.MyAntlrTokenToAttributeIdMapper;
import com.btc.arch.syntaxcoloring.MyHighLightingConfiguration;
import com.google.inject.Binder;

/**
 * Use this class to register components to be used within the IDE.
 */
public class ArchitectureDslUiModule extends com.btc.arch.ui.AbstractArchitectureDslUiModule {
	public ArchitectureDslUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}
	
	public Class<? extends IHighlightingConfiguration> bindILexicalHighlightingConfiguration() {
		return MyHighLightingConfiguration.class;
	}

	public Class<? extends AbstractAntlrTokenToAttributeIdMapper> bindAbstractAntlrTokenToAttributeIdMapper() {
		return MyAntlrTokenToAttributeIdMapper.class ;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.xtext.ui.DefaultUiModule#configure(com.google.inject.Binder)
	 */
//	@Override
//	public void configure(Binder binder) {
//		// binder.bind(XtextDocumentProvider.class).to(EFSExtendedDocumentProvider.class);
//		binder.bind(XtextEditor.class).to(ExtLinkedXtextEditor.class);
//		super.configure(binder);
//	}
}
