package org.ice1000.braceSucks

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.*
import com.intellij.openapi.editor.Document
import com.intellij.psi.*
import com.intellij.psi.JavaTokenType.*
import com.intellij.psi.util.PsiTreeUtil

val PsiElement.hasNoError get() = (this as? StubBasedPsiElement<*>)?.stub != null || !PsiTreeUtil.hasErrorElements(this)

class EndFoldingBuilder : FoldingBuilderEx() {
	override fun buildFoldRegions(root: PsiElement, document: Document, quick: Boolean): Array<FoldingDescriptor> =
		if (root.hasNoError) SyntaxTraverser.astTraverser(root.node)
			.filterTypes { it in acceptable }
			.map { NamedFoldingDescriptor(it, it.textRange, null, if (it.elementType == RBRACE) "end" else "") }
			.toTypedArray() else emptyArray()

	override fun getPlaceholderText(node: ASTNode) = "..."
	override fun isCollapsedByDefault(node: ASTNode) = true
	private val acceptable = listOf(LBRACE, RBRACE)
}
