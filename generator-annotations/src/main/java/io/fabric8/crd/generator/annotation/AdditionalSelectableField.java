package io.fabric8.crd.generator.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.Repeatable;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.ElementType;

/**
 * Defines a selectable field. Must be placed at the root of the
 * custom resource.
 *
 * @see <a href=
 *      "https://kubernetes.io/docs/tasks/extend-kubernetes/custom-resources/custom-resource-definitions/#field-selectors">Kubernetes
 *      Docs - Field Selectors</a>
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(AdditionalSelectableField.List.class)
public @interface AdditionalSelectableField {

  /**
   * The JSON Path to the field.
   *
   * @return the JSON path
   */
  String jsonPath();

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.TYPE)
  @interface List {
    AdditionalSelectableField[] value();
  }
}
