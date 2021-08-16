package com.plugin.frege.psi

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMember

/**
 * Stores information about the qualified name of a usage or PSI member.
 * In Frege there are only qualified name with at most two qualifiers.
 *
 * Contract: `[firstQualifier] != null -> [secondQualifier] != null`.
 */
data class FregeName(
    /**
     * First qualifier if the qualified name has two qualifiers, otherwise `null`.
     * Examples:
     * * `B.c -> null`
     * * `A.B.c -> A`
     */
    val firstQualifier: String?,
    /**
     * Second qualifier if qualified name has at least one qualifier, otherwise `null`. Examples:
     * * `A.B.c -> B`
     * * `B.c -> B`
     * * 'c -> null'
     */
    val secondQualifier: String?,
    /**
     * Short name.
     * Examples:
     * * `A.B.c -> c`
     * * `A.B -> B`
     * * `c -> c`
     */
    val shortName: String
) {
    private constructor(qualifiersList: List<String>, name: String) : this(
        qualifiersList.getOrNull(1),
        qualifiersList.getOrNull(0),
        name,
    )

    /**
     * Pulls info from a usage of some element: it collects [FregeQualifier]s and constructs [FregeName].
     */
    constructor(usage: PsiElement) : this(collectQualifiers(usage), usage.text)

    companion object {
        private fun collectQualifiers(element: PsiElement): List<String> {
            return generateSequence(element.prevSibling as? FregeQualifier) { it.prevSibling as? FregeQualifier }
                .map { it.text.substringBeforeLast('.') }
                .toList()
                .also { require(it.size <= 2) }
        }

        /**
         * Pulls info from the containing classes of [member].
         * * [firstQualifier] is the name of containing class of containing class of [member].
         * * [secondQualifier] is the name of containing class of [member].
         * * [shortName] is the name of [member].
         */
        fun ofPsiMember(member: PsiMember): FregeName? {
            return member.name?.let { memberName ->
                val clazz = member.containingClass
                val clazzOfClazz = clazz?.containingClass
                FregeName(clazzOfClazz?.name, clazz?.name, memberName)
            }
        }

        /**
         * Merged [firstQualifier] and [secondQualifier] with '.' or `null` if they're both `null`.
         */
        val FregeName.qualifier
            get(): String? = when {
                firstQualifier != null -> "$firstQualifier.$secondQualifier"
                else -> secondQualifier
            }

        /**
         * Merged [firstQualifier], [secondQualifier] and [shortName] with '.', `null`-parts are omitted.
         */
        val FregeName.fullName get(): String = qualifier?.let { "$it.$shortName" } ?: shortName

        /**
         * Checks if qualified name has at least one qualifier.
         */
        val FregeName.isQualified get(): Boolean = secondQualifier != null

        /**
         * Checks if qualified name has no qualifiers.
         */
        val FregeName.isNotQualified get(): Boolean = !isQualified

        /**
         * Non-nullable version of [qualifier].
         */
        val FregeName.qualifierOrEmpty get(): String = qualifier ?: ""

        /**
         * Non-nullable version of [firstQualifier].
         */
        val FregeName.firstQualifierOrEmpty get(): String = firstQualifier ?: ""

        /**
         * Non-nullable version of [secondQualifier].
         */
        val FregeName.secondQualifierOrEmpty get(): String = secondQualifier ?: ""

        /**
         * Merges two names into one.
         * Examples:
         * * `First.Second + Second.method -> First.Second.method`
         * * `First + Second.method -> First.Second.method`
         * * `A.B.c + A.B.c -> A.B.c`
         * * `A.B + A.B.c -> A.B.c`
         * * `A.B.c + A.B -> null`
         * * `A.B + D.c -> null`
         */
        fun FregeName.merge(other: FregeName): FregeName? {
            // !!!
            // It could be implemented in more concise way, but it was implemented like this for optimization
            // !!!
            if (this == other) {
                return this
            }
            return if (firstQualifier != null) {
                if (other.firstQualifier == null) {
                    if (other.secondQualifier == null) {
                        if (shortName == other.shortName) this else null
                    } else {
                        if (secondQualifier == other.secondQualifier && shortName == other.shortName) this else null
                    }
                } else {
                    null
                }
            } else {
                if (other.firstQualifier != null) {
                    if (secondQualifier != null) {
                        if (secondQualifier == other.firstQualifier && shortName == other.secondQualifier) other else null
                    } else {
                        if (shortName == other.firstQualifier) other else null
                    }
                } else {
                    if (secondQualifier == null) {
                        if (other.secondQualifier != null) {
                            if (shortName == other.secondQualifier) {
                                FregeName(null, other.secondQualifier, other.shortName)
                            } else {
                                FregeName(shortName, other.secondQualifier, other.shortName)
                            }
                        } else {
                            if (shortName == other.shortName) this else FregeName(null, shortName, other.shortName)
                        }
                    } else {
                        if (shortName == other.secondQualifier || other.secondQualifier == null) {
                            FregeName(secondQualifier, shortName, other.shortName)
                        } else {
                            null
                        }
                    }
                }
            }
        }
    }
}
