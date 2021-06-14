package com.plugin.frege.stubs

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import com.intellij.util.io.StringRef
import com.plugin.frege.psi.FregePsiClass

open class FregeClassStub : FregeNamedStub<FregePsiClass> {
    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>,
        name: String?
    ) : super(parent, elementType, name)

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>,
        name: StringRef?
    ) : super(parent, elementType, name)
}
