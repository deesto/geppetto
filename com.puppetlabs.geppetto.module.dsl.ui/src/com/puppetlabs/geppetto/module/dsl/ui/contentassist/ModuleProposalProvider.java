/**
 * generated by Xtext
 */
package com.puppetlabs.geppetto.module.dsl.ui.contentassist;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.CrossReference;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;

import com.google.common.base.Predicate;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.puppetlabs.geppetto.module.dsl.ModuleUtil;
import com.puppetlabs.geppetto.module.dsl.metadata.JsonDependency;
import com.puppetlabs.geppetto.module.dsl.metadata.JsonObject;
import com.puppetlabs.geppetto.semver.Version;

/**
 * see http://www.eclipse.org/Xtext/documentation.html#contentAssist on how to customize content assistant
 */
@Singleton
public class ModuleProposalProvider extends AbstractModuleProposalProvider {
	@Inject
	private IQualifiedNameProvider qnProvider;

	@Inject
	private ModuleUtil moduleUtil;

	@Override
	public void completeJsonVersionRange_Value(EObject model, Assignment assignment, ContentAssistContext ctx,
			ICompletionProposalAcceptor acceptor) {
		EObject rq = model.eContainer();
		Version minVer = null;
		if(rq instanceof JsonDependency)
			if(moduleUtil.isResolved(rq))
				minVer = moduleUtil.getVersion(moduleUtil.getReferencedModule((JsonDependency) rq));
			else
				minVer = Version.create("0.1.0");
		else if(rq instanceof JsonObject) {
			minVer = "puppet".equals(moduleUtil.getString((JsonObject) rq, "name"))
				? Version.create("3.6.0")
				: Version.create("3.0.0");
		}
		else
			minVer = Version.create("0.1.0");

		StringBuilder bld = new StringBuilder();
		bld.append("\">=");
		minVer.toString(bld);
		bld.append('"');
		acceptor.accept(createCompletionProposal(bld.toString(), ctx));
		bld.setLength(1);
		bld.append(minVer.getMajor());
		bld.append(".x\"");
		acceptor.accept(createCompletionProposal(bld.toString(), ctx));
		bld.setLength(1);
		bld.append(minVer.getMajor());
		bld.append('.');
		bld.append(minVer.getMinor());
		bld.append(".x\"");
		acceptor.accept(createCompletionProposal(bld.toString(), ctx));
		bld.setLength(1);
		minVer.toString(bld);
		bld.append('"');
		acceptor.accept(createCompletionProposal(bld.toString(), ctx));
	}

	/**
	 * Avoid proposing ':' when current is whitespace that comes after it
	 */
	@Override
	public void completeKeyword(Keyword keyword, ContentAssistContext contentAssistContext, ICompletionProposalAcceptor acceptor) {
		INode currentNode = contentAssistContext.getCurrentNode();
		if(currentNode instanceof ILeafNode)
			if(((ILeafNode) currentNode).isHidden() && keyword.getValue().equals(":"))
				return;

		super.completeKeyword(keyword, contentAssistContext, acceptor);
	}

	/**
	 * Avoid including the module itself when proposing dependency alternatives
	 */
	@Override
	public void completeMetadataRefPair_Ref(EObject m, Assignment a, ContentAssistContext ctx, ICompletionProposalAcceptor acceptor) {
		final QualifiedName qn = qnProvider.getFullyQualifiedName(ctx.getRootModel());
		lookupCrossReference(((CrossReference) a.getTerminal()), ctx, acceptor, new Predicate<IEObjectDescription>() {
			@Override
			public boolean apply(final IEObjectDescription d) {
				return !qn.equals(d.getQualifiedName());
			}
		});
	}

	@Override
	public void completeRequirementNameValue_Value(EObject m, Assignment a, ContentAssistContext ctx, ICompletionProposalAcceptor acceptor) {
		acceptor.accept(createCompletionProposal("\"pe\"", ctx));
		acceptor.accept(createCompletionProposal("\"puppet\"", ctx));
	}
}
