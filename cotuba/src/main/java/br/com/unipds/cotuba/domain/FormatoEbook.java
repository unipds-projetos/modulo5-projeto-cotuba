package br.com.unipds.cotuba.domain;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject  // valor default
public enum FormatoEbook {
    PDF, EPUB,HTML;

}
