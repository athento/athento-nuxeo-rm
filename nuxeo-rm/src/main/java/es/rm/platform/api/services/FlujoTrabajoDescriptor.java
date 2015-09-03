package es.rm.platform.api.services;

import java.io.Serializable;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * Descriptor de flujos de trabajo.
 * 
 * @author <a href="mailto:vs@athento.com">Victor Sanchez</a>
 */
@XObject("workflow")
public class FlujoTrabajoDescriptor implements Serializable {

    /**
     * Numero de serializacion.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Identificador del flujo de trabajo.
     */
    @XNode("@id")
    private String identificador = "";

    /**
     * Devuelve el identificador del flujo de trabajo.
     * 
     * @return El identificador del flujo de trabajo.
     */
    public final String getIdentificador() {
        return identificador;
    }

    /**
     * Configura el identificador del flujo de trabajo.
     * 
     * @param identificador El identificador del flujo de trabajo
     */
    public final void setIdentificador(final String identificador) {
        this.identificador = identificador;
    }

}
