package org.raml.v2.api;

import org.junit.Test;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jean-Philippe Belanger on 9/24/16.
 * Just potential zeroes and ones
 */
public class TypesTest {

    @Test
    public void inheritance() {
        File input = new File("src/test/resources/org/raml/v2/api/v10/types/types.raml");
        assertTrue(input.isFile());
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(input);

        assertFalse(ramlModelResult.hasErrors());

        List<TypeDeclaration> types = ramlModelResult.getLibrary().types();

        assertEquals(3, types.size());
        TypeDeclaration typeDeclaration = types.get(2);
        assertEquals("C", typeDeclaration.name());

        List<TypeDeclaration> parentTypes = typeDeclaration.parentTypes();
        assertEquals(2, parentTypes.size());
    }

    @Test
    public void no_types() {
        File input = new File("src/test/resources/org/raml/v2/api/v10/types/no-supertypes.raml");
        assertTrue(input.isFile());
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(input);

        assertFalse(ramlModelResult.hasErrors());

        List<TypeDeclaration> types = ramlModelResult.getLibrary().types();

        assertEquals(1, types.size());
        TypeDeclaration typeDeclaration = types.get(0);
        assertEquals("A", typeDeclaration.name());

        List<TypeDeclaration> parentTypes = typeDeclaration.parentTypes();
        assertEquals(0, parentTypes.size());
    }

}
