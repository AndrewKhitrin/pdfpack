package com.rstyle.rsdoc.pdfpack;
import java.io.IOException;

import org.apache.jempbox.xmp.XMPMetadata;
import org.w3c.dom.Document;

public class RSPDFMetaMetadata extends XMPMetadata {


public RSPDFMetaMetadata() throws IOException {
    super();
}

public RSPDFMetaMetadata(Document xmpDoc) {
    super(xmpDoc);
    addXMLNSMapping(RSPDFMetaSchema.NAMESPACE, RSPDFMetaSchema.class);
}

public RSPDFMetaSchema addRSPDFMetaSchema() {
	RSPDFMetaSchema schema = new RSPDFMetaSchema(this);
    return (RSPDFMetaSchema) basicAddSchema(schema);
}

public RSPDFMetaSchema getRSPDFSchema() throws IOException {
    return (RSPDFMetaSchema) getSchemaByClass(RSPDFMetaSchema.class);
}
	
}
