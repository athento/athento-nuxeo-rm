package org.athento.nuxeo.rm.schema.test;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.schema.SchemaManager;
import org.nuxeo.ecm.core.storage.sql.SQLRepositoryTestCase;
import org.nuxeo.runtime.api.Framework;

/**
 * Test para la comprobacion de los esquemas correctos del rm.
 * 
 * @author Victor Sanchez
 * 
 */
public class TestRMSchema extends SQLRepositoryTestCase {
	
	/**
	 * Método setUp del test.
	 * 
	 * Conflicto con checkstyle. El método debe lanzar la excepción Exception ya
	 * que hereda de una clase externa que la declara.
	 * 
	 * @exception Exception excepcion lanzada en caso de error
	 */
    @Override
	public final void setUp() throws Exception {
//        super.setUp();
//        deployContrib("es.rm.platform.dm.test",
//                "OSGI-INF/test-dm-schema-contrib.xml");
//        deployContrib("es.rm.platform.dm.test",
//                "OSGI-INF/test-dm-doctype-contrib.xml");
//        openSession();
    }
    
    /**
     * Testea el registro de esquemas base.
     * @throws Exception excepción lanzada en caso de error
     */
    public final void testBaseSchemas() throws Exception {
//        SchemaManager schemas = Framework.getService(SchemaManager.class);
//        assertNotNull(schemas);
//
//        DocumentModel miDoc = session.createDocumentModel("MiDocumento");
//        assertNotNull(miDoc);
//        
//        miDoc.setPropertyValue("grfondo:Descripcion", "des1");
//        assertEquals(miDoc.getPropertyValue("grfondo:Descripcion"), "des1");
//        
//        miDoc.setPropertyValue("grfondo:Fechas/FechaInicio", "valor1");
//        assertEquals(miDoc.getPropertyValue("grfondo:Fechas/FechaInicio"), "valor1");
    }

}
