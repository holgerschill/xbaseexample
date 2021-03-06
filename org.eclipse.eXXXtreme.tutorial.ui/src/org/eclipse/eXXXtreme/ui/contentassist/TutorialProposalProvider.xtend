/*
 * generated by Xtext 2.9.0-SNAPSHOT
 */
package org.eclipse.eXXXtreme.ui.contentassist

import com.google.inject.Inject
import org.eclipse.eXXXtreme.tutorial.Query
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EReference
import org.eclipse.xtext.Assignment
import org.eclipse.xtext.EcoreUtil2
import org.eclipse.xtext.common.types.TypesPackage
import org.eclipse.xtext.common.types.util.TypeReferences
import org.eclipse.xtext.common.types.xtext.ui.TypeMatchFilters
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor

/**
 * See https://www.eclipse.org/Xtext/documentation/304_ide_concepts.html#content-assist
 * on how to customize the content assistant.
 */
class TutorialProposalProvider extends AbstractTutorialProposalProvider {
	
	@Inject
	TypeReferences typeReferences

	override completeJavaTypes(ContentAssistContext context, EReference reference,
		ICompletionProposalAcceptor acceptor) {
		completeJavaTypes(context, reference, qualifiedNameValueConverter, createVisibilityFilter(context), acceptor);
	}

	override completeQuery_Table(EObject model, Assignment assignment, ContentAssistContext context, 
		ICompletionProposalAcceptor acceptor) {
		typesProposalProvider.createSubTypeProposals(typeReferences.findDeclaredType("org.eclipse.eXXXtreme.tutorial.ITable",model), this, context,
			TypesPackage.Literals.JVM_PARAMETERIZED_TYPE_REFERENCE__TYPE, TypeMatchFilters.canInstantiate, acceptor)
	}
	
	override completeJvmParameterizedTypeReference_Type(EObject model, Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		val query = EcoreUtil2.getContainerOfType(model,Query)
		if(query != null) {
			typesProposalProvider.createSubTypeProposals(typeReferences.findDeclaredType("org.eclipse.eXXXtreme.tutorial.ITable",model), this, context,
			TypesPackage.Literals.JVM_PARAMETERIZED_TYPE_REFERENCE__TYPE, TypeMatchFilters.canInstantiate, acceptor)
		} else
			super.completeJvmParameterizedTypeReference_Type(model, assignment, context, acceptor)
		
	}
	
}
