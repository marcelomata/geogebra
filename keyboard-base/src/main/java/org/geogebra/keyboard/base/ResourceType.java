package org.geogebra.keyboard.base;

/**
 * Resource types that can be used.
 */
public enum ResourceType {

    /**
     * The resource is a label.
     */
    TEXT,

    /**
     * The resource is a translation key.
     */
    TRANSLATION_KEY,

    /**
     * The resource is specified in the {@link ButtonConstants} class,
     * with the constants starting with <i><b>RESORUCE_</b></i>.
     */
    DEFINED_CONSTANT
}