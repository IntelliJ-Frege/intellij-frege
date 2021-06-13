package com.plugin.frege.stubs

import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import com.intellij.util.io.StringRef

class FregeProgramStub : FregeClassStub {
    val importStrings: List<String>

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>,
        name: String?,
        importStrings: List<String>
    ) : super(parent, elementType, name) {
        this.importStrings = importStrings
    }

    constructor(
        parent: StubElement<*>?,
        elementType: IStubElementType<*, *>,
        name: StringRef?,
        importStrings: List<String>
    ) : super(parent, elementType, name) {
        this.importStrings = importStrings
    }
}
