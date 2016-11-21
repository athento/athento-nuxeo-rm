package org.athento.nuxeo.rm.bean;

import static org.jboss.seam.ScopeType.PAGE;

import java.io.Serializable;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Clase auxiliar de utilizades para los "bean".
 */
@Name("rmUtilsActions")
@Scope(PAGE)
public class RMUtilsActionsBean implements Serializable {

    /**
     * Variable de serializacion.
     */
    private static final long serialVersionUID = 9178619156569361531L;

    /**
     * Declaraci&oacute;n del booleano showCSV.
     */
    private boolean showCSV;
    /**
     * Declaraci&oacute;n del booleano showIdDocOrigen.
     */
    private boolean showIdDocOrigen;
    
    /**
     * Devuelve el valor de showCSV (booleano).
     * 
     * @return showCSV
     */
    public final boolean isShowCSV() {
        return showCSV;
    }
    
    /**
     * Asigna el valor de showValorCSV que se le pasa como entrada a showCSV.
     * 
     * @param showValorCSV valor a asignar al atributo
     */
    public final void setShowCSV(boolean showValorCSV) {
        this.showCSV = showValorCSV;
    }
    
    /**
     * Devuelve el valor de ShowIdDocOrigen(booleano).
     * 
     * @return showIdDocOrigen
     */
    public final boolean isShowIdDocOrigen() {
        return showIdDocOrigen;
    }

    /**
     * Asigna el valor de showIdDocOrigen que se le pasa como entrada a showIdDocOrigen.
     * 
     * @param showIdDocOrigen valor a asignar al atributo
     */
    public final void setShowIdDocOrigen(boolean showIdDocOrigen) {
        this.showIdDocOrigen = showIdDocOrigen;
    }

    /**
     * Si value es igual a "TF01" asigna true a showCSV, si no, le asigna false.
     * Si value es igual a "EE02" o "EE03" o "EE04" asigna true a showIdDocOrigen, falso en caso de que no sea igual a ninguno
     * 
     * @param value valor a aasignar al atributo
     */
    public final void toogleValue(String value) {
        showCSV = "TF01".equals(value);
        showIdDocOrigen = "EE02".equals(value) || "EE03".equals(value)
                || "EE04".equals(value);
    }

    /**
     * Si layout es tr_expediente y widget distinto de expedient_valorCSV y expedient_definicionGeneracionCSV devuelve true,
     * si es tr_expediente pero widget no cumple la condici&oacute;n anterior, devuelve el valor de showCSV.
     * 
     * Si layout es igual a e_document y widget distinto de edocument_valorCSV, edocument_definicionGeneracionCSV y 
     * edocument_odDocumentoOrigen, devuelve true, si no, si widget es igual a edocument_idDocumentoOrigen, devuelve
     * el valor de showIdDocOrigen, y si no cumple esto &uacute;ltimo,  pero widget es igual a edocument_valorCSV o 
     * edocument_definicionGeneracionCSV, devuelve el valor de showCSV.
     * 
     * Si no cumple nada de esto, devuelve true.
     * 
     * @param layout nombre del layout
     * @param widget nombre del widget
     * @return true si muestra la etiqueta; false en caso contrario
     */
    public final boolean showWidgetLabel(String layout, String widget) {
        boolean result = true;
    	if ("tr_expediente".equals(layout)) {
            if (!"expedient_valorCSV".equals(widget)
                    && !"expedient_definicionGeneracionCSV".equals(widget)) {
                result = true;
            } else {
                result = showCSV;
            }
        } else if ("e_document".equals(layout)) {
            if (!"edocument_valorCSV".equals(widget)
                    && !"edocument_definicionGeneracionCSV".equals(widget)
                    && !"edocument_idDocumentoOrigen".equals(widget)) {
                result = true;
            } else if ("edocument_idDocumentoOrigen".equals(widget)) {
                result = showIdDocOrigen;
            } else if ("edocument_valorCSV".equals(widget)
                    || "edocument_definicionGeneracionCSV".equals(widget)) {
                result = showCSV;
            }
        }
        return result;
    }

    /**
     * Si widget es edocument_tipoFirma, devuelve edocument_valorCSV.
     * Si widget es expedient_tipoFirma, devuelve expedient_valorCSV.
     * En otro caso, no devuelve nada.
     *
     * @param widget the widget
     * @return the string
     */
    public final String reRenderValorCSV(String widget) {
    	String result = "";
        if ("edocument_tipoFirma".equals(widget)) {
            result = "edocument_valorCSV";
        } else if ("expedient_tipoFirma".equals(widget)) {
            result = "expedient_valorCSV";
        }
        return result;
    }

    /**
     * Si widget es edocument_tipoFirma, devuelve edocument_definicionGeneracionCSV.
     * Si widget es expedient_tipoFirma, devuelve expedient_definicionGeneracionCSV.
     * En otro caso, no devuelve nada.
     * 
     * @param widget nombre del widget
     * @return String especificado
     */
    public final String reRenderdefCSV(String widget) {
    	String result = "";
    	if ("edocument_tipoFirma".equals(widget)) {
            result = "edocument_definicionGeneracionCSV";
        } else if ("expedient_tipoFirma".equals(widget)) {
            result = "expedient_definicionGeneracionCSV";
        }
        return result;
    }

    /**
     * Si widget es edocument_estadoElaboracion, devuelve edocument_idDocumentoOrigen.
     * En otro caso, no devuelve nada.
     * 
     * @param widget nombre del widget
     * @return String especificado
     */
    public final String reRenderIdDocOrigen(String widget) {
    	String result = "";
        if ("edocument_estadoElaboracion".equals(widget)) {
            result = "edocument_idDocumentoOrigen";
        } else {
            result = "";
        }
        return result;
    }
}
