package org.raml.model;

import java.util.Map;

public interface Traitable
{

    void applyTrait(Map<?, ?> template, Map<String, ?> params);
}
