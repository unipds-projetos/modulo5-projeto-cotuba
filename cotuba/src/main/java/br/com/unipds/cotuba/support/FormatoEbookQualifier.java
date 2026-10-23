package br.com.unipds.cotuba.support;

import br.com.unipds.cotuba.domain.FormatoEbook;
import jakarta.inject.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FormatoEbookQualifier {
    FormatoEbook value();
}
