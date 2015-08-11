package com.mgiorda.resttemplate.client.mvc;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

class AnnotatedParameter<T> {
		
		private final T value;
		private final List<Annotation> annotations;
		
		public AnnotatedParameter(T value, List<Annotation> annotations) {
		
			Objects.requireNonNull(annotations, "Annotations cannot be null");			
			
			this.value = value;
			this.annotations = annotations;
		}

		public T getValue() {
			return value;
		}
		
		@SuppressWarnings("unchecked")
		public <E extends Annotation> Optional<E> getAnnotation(Class<E> annotationClass) {
			
			Objects.requireNonNull(annotationClass, "Annotation class cannot be null");
			return annotations
					.parallelStream()
					.filter(ann -> ann.annotationType().equals(annotationClass))
					.map(ann -> (E) ann)
					.findFirst();
		}
	}
