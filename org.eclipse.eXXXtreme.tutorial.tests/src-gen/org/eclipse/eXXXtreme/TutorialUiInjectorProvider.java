/*
 * generated by Xtext
 */
package org.eclipse.eXXXtreme;

import org.eclipse.xtext.junit4.IInjectorProvider;

import com.google.inject.Injector;

public class TutorialUiInjectorProvider implements IInjectorProvider {
	
	@Override
	public Injector getInjector() {
		return org.eclipse.eXXXtreme.ui.internal.TutorialActivator.getInstance().getInjector("org.eclipse.eXXXtreme.Tutorial");
	}
	
}